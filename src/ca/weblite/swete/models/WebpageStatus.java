/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.models;

import java.util.Date;
import java.util.List;

/**
 *
 * @author shannah
 */
public class WebpageStatus {

    /**
     * @return the translationsChecksum
     */
    public String getTranslationsChecksum() {
        return translationsChecksum;
    }

    /**
     * @param translationsChecksum the translationsChecksum to set
     */
    public void setTranslationsChecksum(String translationsChecksum) {
        this.translationsChecksum = translationsChecksum;
    }

    /**
     * @return the lastTranslationsChange
     */
    public Date getLastTranslationsChange() {
        return lastTranslationsChange;
    }

    /**
     * @param lastTranslationsChange the lastTranslationsChange to set
     */
    public void setLastTranslationsChange(Date lastTranslationsChange) {
        this.lastTranslationsChange = lastTranslationsChange;
    }

    /**
     * @return the lastOutputContentChange
     */
    public Date getLastOutputContentChange() {
        return lastOutputContentChange;
    }

    /**
     * @param lastOutputContentChange the lastOutputContentChange to set
     */
    public void setLastOutputContentChange(Date lastOutputContentChange) {
        this.lastOutputContentChange = lastOutputContentChange;
    }

    /**
     * @return the lastResponseBodyChange
     */
    public Date getLastResponseBodyChange() {
        return lastResponseBodyChange;
    }

    /**
     * @param lastResponseBodyChange the lastResponseBodyChange to set
     */
    public void setLastResponseBodyChange(Date lastResponseBodyChange) {
        this.lastResponseBodyChange = lastResponseBodyChange;
    }

    /**
     * @return the responseCode
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * @param responseCode the responseCode to set
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    

    /**
     * @return the site
     */
    public WebSite getSite() {
        return site;
    }

    /**
     * @param site the site to set
     */
    public void setSite(WebSite site) {
        this.site = site;
    }

    /**
     * @return the webpageStatusId
     */
    public long getWebpageStatusId() {
        return webpageStatusId;
    }

    /**
     * @param webpageStatusId the webpageStatusId to set
     */
    public void setWebpageStatusId(long webpageStatusId) {
        this.webpageStatusId = webpageStatusId;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the onWhitelist
     */
    public boolean isOnWhitelist() {
        return onWhitelist;
    }

    /**
     * @param onWhitelist the onWhitelist to set
     */
    public void setOnWhitelist(boolean onWhitelist) {
        this.onWhitelist = onWhitelist;
    }

    /**
     * @return the lastChecked
     */
    public Date getLastChecked() {
        return lastChecked;
    }

    /**
     * @param lastChecked the lastChecked to set
     */
    public void setLastChecked(Date lastChecked) {
        this.lastChecked = lastChecked;
    }

    /**
     * @return the numUntranslatedStrings
     */
    public int getNumUntranslatedStrings() {
        return numUntranslatedStrings;
    }

    /**
     * @param numUntranslatedStrings the numUntranslatedStrings to set
     */
    public void setNumUntranslatedStrings(int numUntranslatedStrings) {
        this.numUntranslatedStrings = numUntranslatedStrings;
    }
    
    public String getAllStringsURL() {
        if (site == null) {
            return null;
        }
        return site.getAdminUrl()+"?-table=webpage_status&-action=swete_translate_page&webpage_status_id=="+webpageStatusId;
    }
    
    public String getUntranslatedStringsURL() {
        if (site == null) {
            return null;
        }
        return site.getAdminUrl()+"?-table=webpage_status&-action=swete_translate_page&--untranslated-only=1&webpage_status_id=="+webpageStatusId;
    }
    
    public static String[] getURLs(List<WebpageStatus> pages) {
        return getURLs(pages.toArray(new WebpageStatus[pages.size()]));
    }
    
    public static String[] getURLs(WebpageStatus... pages) {
        int len = pages.length;
        String[] out = new String[len];
        for (int i=0; i<len; i++) {
            out[i] = pages[i].getUrl();
        }
        return out;
    }
    
    private WebSite site;
    private long webpageStatusId;
    private String url;
    private boolean onWhitelist;
    private Date lastChecked;
    private int numUntranslatedStrings;
    private int responseCode;
    private String contentType;
    // The date that the output content was last
    // changed.  This is the *translated version*
    // of the page.
    private Date lastOutputContentChange;
    
    // The date that the *source* content was last
    // changed.  This is the *non-translated* version
    // of the page
    private Date lastResponseBodyChange;
    
    private Date lastTranslationsChange;
    
    private String translationsChecksum;
}
