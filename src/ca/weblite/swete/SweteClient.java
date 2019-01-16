/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete;

import ca.weblite.swete.models.Settings;
import ca.weblite.swete.models.Snapshot;
import ca.weblite.swete.models.Snapshot.PageStatus;
import ca.weblite.swete.models.Snapshot.SnapshotPage;
import ca.weblite.swete.models.TranslationStats;
import ca.weblite.swete.models.WebSite;
import ca.weblite.swete.models.WebpageStatus;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.Log;
import com.codename1.io.NetworkManager;
import com.codename1.io.URL;
import com.codename1.io.Util;
import com.codename1.processing.Result;
import com.codename1.ui.CN;
import com.codename1.util.regex.RE;
import com.xataface.query.XFClient;
import com.xataface.query.XFClient.XFClientListener;
import com.xataface.query.XFCustomAction;
import com.xataface.query.XFQuery;
import com.xataface.query.XFRecord;
import com.xataface.query.XFRowSet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class SweteClient {
    private XFClient xfClient;
    private WebSite site;
    private static final String[] WEBPAGE_STATUS_COLS = new String[]{
        "response_status_code", 
                        "response_content_type", 
                        "page_url", 
                        "webpage_status_id", 
                        "last_checked", 
                        "num_translation_misses", 
                        "response_status_code", 
                        "response_content_type", 
                        "response_status_code", 
                        "response_content_type", 
                        "last_response_body_change", 
                        "last_output_content_change",
                        "last_translations_change",
                        "translations_checksum"
    };
    
    private final XFClientListener clientListener = new XFClientListener() {
        @Override
        public void afterLoginSuccess(XFClient client) {
            site.setUserName(client.getUsername());
            site.setPassword(client.getPassword());
            try {
                if (Settings.getInstance().getWebSites().contains(site)) {
                    Settings.getInstance().save();
                }
            } catch (IOException ioe) {
                Log.p("Failed to save username and password after login");
                Log.e(ioe);
            }
        }

        @Override
        public void afterLoginFail(XFClient client) {
            
        }
        
    };
    
    public SweteClient(WebSite site) {
        this.site = site;
        xfClient = new XFClient(site.getAdminUrl());
        xfClient.setUsername(site.getUserName());
        xfClient.setPassword(site.getPassword());
        xfClient.addListener(clientListener);
        
    }
    
    public boolean validateAdminUrl() {
        ConnectionRequest conn = new ConnectionRequest();
        conn.setUrl(site.getAdminUrl());
        conn.setFailSilently(true);
        conn.setReadResponseForErrors(true);
        conn.setFollowRedirects(true);
        NetworkManager.getInstance().addToQueueAndWait(conn);
        return conn.getResponseCode() >= 200 && conn.getResponseCode() < 300;
        
    }
    
    private class InternalResult {
        Throwable error;
        boolean complete;
    }
    
    public void load() throws IOException {
        URL proxyUrl;
        try {
            proxyUrl = new URL(site.getProxyUrl());
        } catch (URISyntaxException ex) {
            throw new IOException("Invalid proxy URL.", ex);
        }
        
        if (!validateAdminUrl()) {
            throw new IOException("Cannot load site at url "+site.getProxyUrl()+" because the admin URL "+site.getAdminUrl()+" is not valid.");
        }
        xfClient.setUrl(site.getAdminUrl());
        
        if (site.getUserName() == null || site.getPassword() == null) {
            InternalResult res = new InternalResult();
            xfClient.login(true, r -> {
                if (r) {
                    site.setUserName(xfClient.getUsername());
                    site.setPassword(xfClient.getPassword());
                    
                } else {
                    res.error = new RuntimeException("Login Failed");
                }
                res.complete = true;
                synchronized(res) {
                    res.notify();
                }
            });
            
            CN.invokeAndBlock(()->{
                while (!res.complete) {
                    try {
                        synchronized(res) {
                            res.wait();
                        }
                    } catch (Throwable t){}
                
                }
            });
            if (res.error != null) {
                throw new IOException("Failed to load site with URL "+site.getProxyUrl(), res.error);
            }
            
        } 
        xfClient.setUsername(site.getUserName());
        xfClient.setPassword(site.getPassword());
        
        
        //System.out.println("Proxy URL host is "+proxyUrl.getHost());
        XFQuery query = new XFQuery("websites")
                .findOne()
                .matches("active", "1")
                .matches("host", proxyUrl.getHost()+(proxyUrl.getPort() >= 0 ? (":"+proxyUrl.getPort()):""))
                .matches("base_path", proxyUrl.getPath());
        
        XFRowSet rs = xfClient.findAndWait(query);
        if (rs.getFound() != 1) {
            throw new IOException("WebSite Not Found.  Found only "+rs.getFound()+" sites matching the description.");
        }
        XFRecord rec = null;
        for (XFRecord r : rs) {
            rec = r;
            break;
        }
        site.setName(rec.getString("website_name"));
        site.setSiteId(rec.getString("website_id"));
        site.setSrcUrl(rec.getString("website_url"));
        site.setSourceLanguage(rec.getString("source_language"));
        site.setProxyLanguage(rec.getString("target_language"));
        site.setCurrentSnapshotId(rec.getInteger("current_snapshot_id"));
                
        
        
        
    }
    
    
    public String[] loadWhitelist() throws IOException {
        ConnectionRequest req = new ConnectionRequest();
        req.setUrl(site.getWhitelistUrl());
        req.setReadResponseForErrors(true);
        req.setFailSilently(true);
        req.setFollowRedirects(true);
        NetworkManager.getInstance().addToQueueAndWait(req);
        if (req.getResponseCode() >= 200 && req.getResponseCode() < 300) {
            String data = new String(req.getResponseData(), "UTF-8");
            String[] lines = Util.split(data, "\n");
            RE re = new RE("\\s+");
            List<String> tmpList = new ArrayList<String>();
            for (String line : lines) {
                String[] parts = re.split(line);
                if (parts.length < 1) continue;
                tmpList.add(parts[0]);
            }
            String[] whitelist = tmpList.toArray(new String[tmpList.size()]);
            return whitelist;
        } else {
            throw new IOException("Failed to load whitelist");
        }
    }
    
    public WebpageStatus loadWebpageStatus(String url) throws IOException {
        
        XFQuery q = new XFQuery("webpage_status")
                .matches("website_id", site.getSiteId())
                .matches("page_url", url)
                .select(WEBPAGE_STATUS_COLS)
                .findOne();
        XFRowSet results = xfClient.findAndWait(q);
        List<WebpageStatus> out = new ArrayList<WebpageStatus>();
        for (XFRecord rec : results) {
            return newWebpageStatus(rec);
        }
        return null;
    }
    
    public WebpageStatus[] loadWebpageStatuses() throws IOException {
        
        XFQuery q = new XFQuery("webpage_status")
                .matches("website_id", site.getSiteId())
                .select(WEBPAGE_STATUS_COLS)
                .limit(999)
                .findAll();
        XFRowSet results = xfClient.findAndWait(q);
        List<WebpageStatus> out = new ArrayList<WebpageStatus>();
        for (XFRecord rec : results) {
            out.add(newWebpageStatus(rec));
        }
        return out.toArray(new WebpageStatus[out.size()]);
    }
    
    private WebpageStatus newWebpageStatus(XFRecord rec) {
        WebpageStatus s = new WebpageStatus();
        s.setLastChecked(rec.getDate("last_checked"));
        s.setUrl(rec.getString("page_url"));
        s.setWebpageStatusId(rec.getLong("webpage_status_id"));
        s.setNumUntranslatedStrings(rec.getInt("num_translation_misses"));
        s.setSite(site);
        s.setResponseCode(rec.getInt("response_status_code"));
        s.setContentType(rec.getString("response_content_type"));
        s.setLastOutputContentChange(rec.getDate("last_output_content_change"));
        s.setLastResponseBodyChange(rec.getDate("last_response_body_change"));
        s.setLastTranslationsChange(rec.getDate("last_translations_change"));
        s.setTranslationsChecksum(rec.getString("translations_checksum"));
        return s;
    }
    
    public ConnectionRequest requestPageWithCapturing(String url) {
        ConnectionRequest req = new ConnectionRequest();
        req.setPost(false);
        req.setHttpMethod("GET");
        req.setUrl(url);
        req.setCookiesEnabled(false);
        req.addRequestHeader("Cookie", "--swete-capture=1");
        req.setFailSilently(true);
        req.setReadResponseForErrors(true);
        req.setFollowRedirects(false);
        req.setCacheMode(ConnectionRequest.CachingMode.OFF);
        NetworkManager.getInstance().addToQueueAndWait(req);
        return req;
    }
    
    public ConnectionRequest requestPageNoStatic(String url) {
        ConnectionRequest req = new ConnectionRequest();
        req.setUrl(url);
        req.setCookiesEnabled(false);
        req.addRequestHeader("Cookie", "--swete-static=false");
        req.setFailSilently(true);
        req.setReadResponseForErrors(true);
        req.setFollowRedirects(true);
        req.setCacheMode(ConnectionRequest.CachingMode.OFF);
        NetworkManager.getInstance().addToQueueAndWait(req);
        return req;
    }
    
    public void refreshPageSnapshotAndWait(String url, Snapshot snapshot) throws IOException {
        XFCustomAction action = new XFCustomAction("refresh_page_snapshot");
        action.put("snapshot_id", snapshot.getSnapshotId())
                .put("page", url);
        ConnectionRequest req = xfClient.postSync(action);
        if (req.getResponseCode() >= 200 && req.getResponseCode() < 300) {
            Result res = Result.fromContent(new ByteArrayInputStream(req.getResponseData()), Result.JSON);
            if (res.getAsInteger("code") == 200) {
                return;
            } else {
                throw new IOException("Failed to refresh page snapshot for url "+url+" with snapshot ID "+snapshot.getSnapshotId()+": "+res.getAsString("message"));
            }
        } else {
            throw new IOException("Failed to refresh the page snapshot due to a network problem.");
        }
        
    }
    
    public void load(WebSite site) throws IOException {
        XFQuery query = new XFQuery("websites")
                .matches("website_id", site.getSiteId())
                .findOne();
        XFRowSet rs = xfClient.findAndWait(query);
        
        if (rs == null || rs.getFound() == 0) {
            throw new IOException("Snapshot not found");
        }
        XFRecord record = rs.iterator().next();
        loadWebSite(site, record);
    }
    
    private void loadWebSite(WebSite site, XFRecord record) {
        site.setCurrentSnapshotId(record.getInteger("current_snapshot_id"));
        site.setProxyLanguage(record.getString("target_language"));
        site.setSourceLanguage(record.getString("source_language"));
        
    }
    
    public void save(WebSite site) throws IOException {
        XFQuery query = new XFQuery("websites")
                .matches("website_id", site.getSiteId())
                .findOne();
        XFRowSet rs = xfClient.findAndWait(query);
        if (rs.getFound() == 0) {
            throw new IOException("Snapshot not found");
        }
        XFRecord record = rs.iterator().next();
        
        copyTo(site, record);
        record = xfClient.saveAndWait(record);
        loadWebSite(site, record);
        
        
    }
    
    public void save(Snapshot snapshot) throws IOException {
        XFQuery query = new XFQuery("snapshots")
                .matches("snapshot_id", snapshot.getSnapshotId())
                .findOne();
        XFRowSet rs = xfClient.findAndWait(query);
        if (rs.getFound() == 0) {
            throw new IOException("Snapshot not found");
        }
        XFRecord record = rs.iterator().next();
        
        copyTo(snapshot, record);
        record = xfClient.saveAndWait(record);
        loadSnapshot(snapshot, record, false);
        
        
    }
    
    private void copyTo(Snapshot snapshot, XFRecord record) {
        record.set("date_completed", snapshot.getDateCompleted());
        
    }
    
    private void copyTo(WebSite website, XFRecord rec) {
        rec.set("current_snapshot_id", website.getCurrentSnapshotId());
        rec.set("target_language", website.getProxyLanguage());
        rec.set("source_language", website.getSourceLanguage());
    }
    
    public void load(Snapshot snapshot) throws IOException {
        XFQuery query = new XFQuery("snapshots")
                .matches("snapshot_id", snapshot.getSnapshotId())
                .findOne();
        XFRowSet rs = xfClient.findAndWait(query);
        if (rs.getFound() == 0) {
            throw new IOException("Snapshot not found");
        }
        XFRecord record = rs.iterator().next();
        loadSnapshot(snapshot, record, true);
                
    }
    
    private void loadSnapshot(Snapshot snapshot, XFRecord record, boolean loadPages) {
        snapshot.setActive("Yes".equals(record.getString("active")));
        snapshot.setDateCompleted(record.getDate("date_completed"));
        snapshot.setDateCreated(record.getDate("date_created"));
        if (loadPages) {
            List<Snapshot.SnapshotPage> pages = snapshot.getPages();
            pages.clear();
            boolean hasPageStatus = false;
            
            Map statusMap = null;
            if (record.getString("pagestatus") != null && !record.getString("pagestatus").isEmpty()) {
                //pageStatusJson = Result.fromContent(record.getString("pagestatus"), Result.JSON);
                JSONParser parser = new JSONParser();
                try {
                    statusMap = parser.parseJSON(
                            new InputStreamReader(
                                    new ByteArrayInputStream(record.getString("pagestatus").getBytes("UTF-8"))));
                    
                    hasPageStatus = true;
                } catch (IOException ex){
                    Log.e(ex);
                }
            }
            
            String[] pagelist = Util.split(record.getString("pagelist"), "\n");
            for (String line : pagelist) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                //System.out.println("Checking line "+line);
                if (statusMap == null) {
                    statusMap = new HashMap();
                }
                Map status = (Map)statusMap.get(line);
                if (status == null && "/".equals(line)) {
                    status = (Map)statusMap.get(site.getProxyUrl());
                }
                if (status == null) {
                    status = (Map)statusMap.get(site.getProxyUrl()+line);
                }
                
                if (status == null) {
                    status = new HashMap();
                }
                int statusCode = status.containsKey("statusCode") && status.get("statusCode") != null ? ((Number)status.get("statusCode")).intValue() : -1;
                long timestamp = status.containsKey("timestamp") && status.get("statusCode") != null ? ((Number)status.get("timestamp")).longValue() * 1000l : 0;
                String statusString = status.containsKey("statusString") ? (String)status.get("statusString") : null;
                SnapshotPage page = new SnapshotPage(line, new PageStatus(statusCode, statusString, new Date(timestamp)));
                
                String translationsChecksum = status.containsKey("translations_checksum") ? (String)status.get("translations_checksum") : null;
                page.setTranslationsChecksum(translationsChecksum);
                pages.add(page);
            }
        }
    }
    
    public void loadTranslationStats() throws IOException {
        XFCustomAction action = new XFCustomAction("dashboard_site_stats");
        action.put("--format", "json");
        action.put("website_id", site.getSiteId());
        action.put("-table", "websites");
        Map res = xfClient.postSyncJSON(action);
        TranslationStats stats = new TranslationStats();
        res = (Map)((List)res.get("results")).get(0);
        Result r = Result.fromContent(res);
        stats.setTotalPhrases(r.getAsInteger("numphrases") );
        stats.setUntranslatedPhrases(r.getAsInteger("untranslated_phrases"));
        stats.setTotalWords(r.getAsInteger("numwords"));
        stats.setUntranslatedWords(r.getAsInteger("untranslated_words"));
        site.setTranslationStats(stats);
        
    }
    
    public void loadSnapshots(WebSite website) throws IOException {
        XFQuery query = new XFQuery("snapshots")
                .matches("website_id", site.getSiteId())
                .limit(999)
                .select("website_id", "snapshot_id", "active", "date_completed", "date_created")
                .sort(XFQuery.SortOrder.DESCENDING, "date_created")
                .findAll();
        XFRowSet rs = xfClient.findAndWait(query);
        website.getSnapshots().clear();
        for (XFRecord rec : rs) {
            Snapshot snapshot = new Snapshot(website, rec.getInteger("snapshot_id"));
            loadSnapshot(snapshot, rec, false);
            website.getSnapshots().add(snapshot);
        }
                
        
    }
    
    public Snapshot createNewSnapshot() throws IOException {
        site.setWhitelist(loadWhitelist());
        XFRecord rec = new XFRecord(xfClient, "snapshots", null);
        rec.set("website_id", site.getSiteId());
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String url : site.getWhitelist()) {
            if (url.startsWith(site.getProxyUrl())) {
                url = url.substring(site.getProxyUrl().length());
            } else if (url.startsWith(site.getSrcUrl())) {
                url = url.substring(site.getSrcUrl().length());
            }
            if (first) {
                first = false;
            } else {
                sb.append("\n");
            }
            sb.append(url);
        }
        
        rec.set("pagelist", sb.toString());
        rec = xfClient.saveAndWait(rec);
        Snapshot out = new Snapshot(site, rec.getInt("snapshot_id"));
        this.loadSnapshot(out, rec, true);
        return out;
        
    }
    
    public void logout() {
        xfClient.logout(res->{});
    }
}
