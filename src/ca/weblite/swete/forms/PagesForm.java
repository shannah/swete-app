/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.forms;

import ca.weblite.shared.ui.tools.TableBuilder;
import ca.weblite.swete.SweteClient;
import ca.weblite.swete.models.WebSite;
import ca.weblite.swete.models.WebpageStatus;
import com.codename1.ui.Container;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A work in progress.  We need to build some server-side infrastructure
 * to track more information about crawls.
 * @author shannah
 */
public class PagesForm {
    private WebSite site;
    private Container pagesCnt;
    private String[] whitelist;
    private WebpageStatus[] webpages;
    private Map<String,WebpageStatus> statusIndex = new HashMap<>();
    private static final WebpageStatus UNKNOWN_STATUS=new WebpageStatus();
    public PagesForm(WebSite site) {
        this.site = site;
        initUI();
    }
    
    private void initUI() {
        pagesCnt = new Container();
    }
    
    
    private void refresh() throws IOException {
        SweteClient client = new SweteClient(site);
        String[] whitelist = client.loadWhitelist();
        site.setWhitelist(whitelist);
        webpages = client.loadWebpageStatuses();
        statusIndex.clear();
        
        
        
        for (String page : whitelist) {
            statusIndex.put(site.getProxyUrlForPage(page), UNKNOWN_STATUS);
        }
        for (WebpageStatus status : webpages) {
            if (statusIndex.containsKey(status.getUrl())) {
                statusIndex.put(status.getUrl(), status);
            }
        }
        
    }
    
    private void updatePagesCnt() {
        if (whitelist == null) {
            return;
        }
        pagesCnt.removeAll();
        TableBuilder tb = new TableBuilder();
        //tb.append("Page").append("")
        for (String str : whitelist) {
            
        }
                
        
    }
    
}
