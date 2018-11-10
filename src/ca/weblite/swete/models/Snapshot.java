/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author shannah
 */
public class Snapshot {

    /**
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * @return the dateCompleted
     */
    public Date getDateCompleted() {
        return dateCompleted;
    }

    /**
     * @param dateCompleted the dateCompleted to set
     */
    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    private WebSite webSite;
    private int snapshotId;
    private Date dateCreated, dateCompleted;
    private List<SnapshotPage> pages = new ArrayList<SnapshotPage>();
    private boolean active;
    
    public static class SnapshotPage {
        private String page;
        private PageStatus status;
        
        public SnapshotPage(String page, PageStatus status) {
            this.page = page;
            this.status = status;
        }
    }
    
    public static class PageStatus {
        private int statusCode;
        private String statusString;
        private Date timestamp;
        
        public PageStatus(int statusCode, String statusString, Date timestamp) {
            this.statusCode = statusCode;
            this.statusString = statusString;
            this.timestamp = timestamp;
        }
                
    }
    
    public Snapshot(WebSite website, int id) {
        this.webSite = website;
        this.snapshotId = id;
    }
    
    public WebSite getWebSite() {
        return webSite;
    }
    
    public int getSnapshotId() {
        return snapshotId;
    }
    
    public List<SnapshotPage> getPages() {
        return pages;
    }
    
    public List<String> getProxyUrls() {
        ArrayList<String> out = new ArrayList<String>();
        for (SnapshotPage page : getPages()) {
            if (page.page.trim().length() == 0) {
                continue;
            }
            if (page.page.startsWith(webSite.getProxyUrl())) {
                out.add(page.page.trim());
            } else if (page.page.startsWith(webSite.getSrcUrl())) {
                out.add(webSite.getProxyUrl() + page.page.substring(webSite.getSrcUrl().length()));
            } else if (!page.page.startsWith("http://") && !page.page.startsWith("https://")) {
                out.add(webSite.getProxyUrl() + page.page);
            }
        }
        return out;
    }
}
