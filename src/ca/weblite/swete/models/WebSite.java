/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.models;

import ca.weblite.swete.SweteClient;
import com.codename1.io.Externalizable;
import com.codename1.io.Util;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shannah
 */
public class WebSite implements Externalizable {

    /**
     * @return the translationStats
     */
    public TranslationStats getTranslationStats() {
        return translationStats;
    }

    /**
     * @param translationStats the translationStats to set
     */
    public void setTranslationStats(TranslationStats translationStats) {
        this.translationStats = translationStats;
    }

    private Integer currentSnapshotId;
    private TranslationStats translationStats;
    
    /**
     * @return the snapshots
     */
    public List<Snapshot> getSnapshots() {
        return snapshots;
    }
    private List<Snapshot> snapshots = new ArrayList<Snapshot>();
    private String[] whitelist;
    
    /**
     * @return the sourceLanguage
     */
    public String getSourceLanguage() {
        return sourceLanguage;
    }

    /**
     * @param sourceLanguage the sourceLanguage to set
     */
    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    /**
     * @return the proxyLanguage
     */
    public String getProxyLanguage() {
        return proxyLanguage;
    }

    /**
     * @param proxyLanguage the proxyLanguage to set
     */
    public void setProxyLanguage(String proxyLanguage) {
        this.proxyLanguage = proxyLanguage;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    
    /**
     * @return the siteId
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     * @param siteId the siteId to set
     */
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the srcUrl
     */
    public String getSrcUrl() {
        return srcUrl;
    }

    /**
     * @param srcUrl the srcUrl to set
     */
    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    /**
     * @return the proxyUrl
     */
    public String getProxyUrl() {
        return proxyUrl;
    }

    /**
     * @param proxyUrl the proxyUrl to set
     */
    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }
    private String name;
    private String srcUrl;
    private String proxyUrl;
    private String siteId;
    private String userName;
    private String password;
    private String sourceLanguage, proxyLanguage;
    
    public String getProxyUrlForPage(String srcUrl) {
        if (srcUrl.startsWith(this.srcUrl)) {
            return proxyUrl + srcUrl.substring(this.srcUrl.length());
        } else if (srcUrl.startsWith(this.proxyUrl)) {
            return srcUrl;
        } else {
            return this.proxyUrl + srcUrl;
        }
    }
    
    public String getSourceUrlForPage(String srcUrl) {
        if (srcUrl.startsWith(this.proxyUrl)) {
            return this.srcUrl + srcUrl.substring(this.proxyUrl.length());
        } else if (srcUrl.startsWith(this.srcUrl)) {
            return srcUrl;
        } else {
            return this.srcUrl + srcUrl;
        }
    }
    
    public String getStartCaptureUrl() {
        return proxyUrl+"!swete:start-capture";
    }
    
    public String getStopCaptureUrl() {
        return proxyUrl+"!swete:stop-capture";
    }
    
    public String getWhitelistUrl() {
        if (getSiteId() == null) {
            throw new IllegalStateException("Whitelist is only available if the site ID is set");
        }
        return proxyUrl+"swete-admin/sites/"+getSiteId()+"/whitelist.txt";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void externalize(DataOutputStream out) throws IOException {
        Util.writeUTF(name, out);
        Util.writeUTF(siteId, out);
        Util.writeUTF(srcUrl, out);
        Util.writeUTF(proxyUrl, out);
        Util.writeUTF(getUserName(), out);
        Util.writeUTF(getPassword(), out);
        Util.writeUTF(sourceLanguage, out);
        Util.writeUTF(proxyLanguage, out);
    }

    @Override
    public void internalize(int version, DataInputStream in) throws IOException {
        name = Util.readUTF(in);
        siteId = Util.readUTF(in);
        srcUrl = Util.readUTF(in);
        proxyUrl = Util.readUTF(in);
        setUserName(Util.readUTF(in));
        setPassword(Util.readUTF(in));
        sourceLanguage = Util.readUTF(in);
        proxyLanguage = Util.readUTF(in);
    }

    @Override
    public String getObjectId() {
        return "WebSite";
    }
    
    
    public String getAdminUrl() {
        return proxyUrl + "swete-admin/index.php";
    }
    
    public String[] getWhitelist() {
        return whitelist;
    }
    
    public void setWhitelist(String[] whitelist) {
        this.whitelist = whitelist;
    }

    /**
     * @return the currentSnapshotId
     */
    public Integer getCurrentSnapshotId() {
        return currentSnapshotId;
    }

    /**
     * @param currentSnapshotId the currentSnapshotId to set
     */
    public void setCurrentSnapshotId(Integer currentSnapshotId) {
        this.currentSnapshotId = currentSnapshotId;
    }
}
