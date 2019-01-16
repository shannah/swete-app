/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


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
    
    
    public static class SnapshotStats {

        /**
         * @return the numMoved
         */
        public int getNumMoved() {
            return numMoved;
        }

        /**
         * @param numMoved the numMoved to set
         */
        public void setNumMoved(int numMoved) {
            this.numMoved = numMoved;
        }

        /**
         * @return the movedUrls
         */
        public String[] getMovedUrls() {
            return movedUrls;
        }

        /**
         * @param movedUrls the movedUrls to set
         */
        public void setMovedUrls(String[] movedUrls) {
            this.movedUrls = movedUrls;
        }

        /**
         * @return the nonRecentlyScannedUrls
         */
        public String[] getNonRecentlyScannedUrls() {
            return nonRecentlyScannedUrls;
        }

        /**
         * @param nonRecentlyScannedUrls the nonRecentlyScannedUrls to set
         */
        public void setNonRecentlyScannedUrls(String[] nonRecentlyScannedUrls) {
            this.nonRecentlyScannedUrls = nonRecentlyScannedUrls;
        }

        /**
         * @return the urlsMissingCurrentVersion
         */
        public String[] getUrlsMissingCurrentVersion() {
            return urlsMissingCurrentVersion;
        }

        /**
         * @param urlsMissingCurrentVersion the urlsMissingCurrentVersion to set
         */
        public void setUrlsMissingCurrentVersion(String[] urlsMissingCurrentVersion) {
            this.urlsMissingCurrentVersion = urlsMissingCurrentVersion;
        }

        /**
         * @return the numNotRecentlyScanned
         */
        public int getNumNotRecentlyScanned() {
            return numNotRecentlyScanned;
        }

        /**
         * @param numNotRecentlyScanned the numNotRecentlyScanned to set
         */
        public void setNumNotRecentlyScanned(int numNotRecentlyScanned) {
            this.numNotRecentlyScanned = numNotRecentlyScanned;
        }

        /**
         * @return the numComplete
         */
        public int getNumComplete() {
            return numComplete;
        }

        /**
         * @param numComplete the numComplete to set
         */
        public void setNumComplete(int numComplete) {
            this.numComplete = numComplete;
        }

        /**
         * @return the numPages
         */
        public int getNumPages() {
            return numPages;
        }

        /**
         * @param numPages the numPages to set
         */
        public void setNumPages(int numPages) {
            this.numPages = numPages;
        }

        /**
         * @return the numCurrent
         */
        public int getNumCurrent() {
            return numCurrent;
        }

        /**
         * @param numCurrent the numCurrent to set
         */
        public void setNumCurrent(int numCurrent) {
            this.numCurrent = numCurrent;
        }

        /**
         * @return the numMissingCurrentVersion
         */
        public int getNumMissingCurrentVersion() {
            return numMissingCurrentVersion;
        }

        /**
         * @param numMissingCurrentVersion the numMissingCurrentVersion to set
         */
        public void setNumMissingCurrentVersion(int numMissingCurrentVersion) {
            this.numMissingCurrentVersion = numMissingCurrentVersion;
        }

        /**
         * @return the numNonCurrent
         */
        public int getNumNonCurrent() {
            return numNonCurrent;
        }

        /**
         * @param numNonCurrent the numNonCurrent to set
         */
        public void setNumNonCurrent(int numNonCurrent) {
            this.numNonCurrent = numNonCurrent;
        }
        
        public double getPercentCurrent() {
            return getNumCurrent() / (double)getNumActivePages() * 100.0;
        }
        
        public int getNumActivePages() {
            return numPages-numMoved;
        }
        
        public double getPercentComplete() {
            return getNumComplete() / (double)getNumNonCurrent() * 100.0;
        }
        private int numPages;
        private int numCurrent;
        private int numMissingCurrentVersion;
        private String[] urlsMissingCurrentVersion;
        private int numNonCurrent;
        
        private int numComplete;
        private int numMoved;
        private String[] movedUrls;
        
        private int numNotRecentlyScanned;
        private String[] nonRecentlyScannedUrls;
        
    }
    
    public static final long ONE_DAY = 24l * 60l * 60l * 1000l;
    public static final long DEFAULT_NON_RECENTLY_SCANNED_THRESHOLD=ONE_DAY;
    
    public SnapshotStats calculateStats(Collection<WebpageStatus> currentVersions) {
        return calculateStats(currentVersions, DEFAULT_NON_RECENTLY_SCANNED_THRESHOLD);
    } 
    
    public SnapshotStats calculateStats(Collection<WebpageStatus> currentVersions, long notRecentlyScannedThresholdMS) {
        Map<String,WebpageStatus> m = new HashMap<>();
        for (WebpageStatus s : currentVersions) {
            m.put(s.getUrl(), s);
        }
        
        SnapshotStats out = new SnapshotStats();
        List<SnapshotPage> nonCurrentPages= new ArrayList<SnapshotPage>();
        List<String> missingUrls = new ArrayList<String>();
        List<String> movedUrls = new ArrayList<>();
        // Calculate percentCurrent stats
        for (SnapshotPage page : pages) {
            String url = webSite.getProxyUrlForPage(page.getPage());
            WebpageStatus currStatus = m.get(url);
            out.numPages++;
            if (currStatus == null) {
                out.numMissingCurrentVersion++;
                missingUrls.add(url);
                continue;
            }
            if (currStatus.getResponseCode() >= 300 && currStatus.getResponseCode() < 400) {
                out.setNumMoved(out.getNumMoved() + 1);
                movedUrls.add(url);
                continue;
            }
            if (currStatus.getTranslationsChecksum() == null) {
                out.numNonCurrent++;
                nonCurrentPages.add(page);
                continue;
            }
            if (Objects.equals(currStatus.getTranslationsChecksum(), page.getTranslationsChecksum())) {
                out.numCurrent++;
            } else {
                out.numNonCurrent++;
                nonCurrentPages.add(page);
            }
        }
        out.setMovedUrls(movedUrls.toArray(new String[movedUrls.size()]));
        out.setUrlsMissingCurrentVersion(missingUrls.toArray(new String[missingUrls.size()]));
        // Now find out how many of the non-current pages
        // are complete
        for (SnapshotPage page : nonCurrentPages) {
            WebpageStatus status = m.get(webSite.getProxyUrlForPage(page.getPage()));
            if (status.getNumUntranslatedStrings() == 0) {
                out.numComplete++;
            }
        }
        long now = new Date().getTime();
        List<String> nonRecentlyScanned = new ArrayList<>();
        for (SnapshotPage page : pages) {
            String url = webSite.getProxyUrlForPage(page.getPage());
            WebpageStatus status = m.get(url);
            if (status == null || status.getLastChecked() == null || status.getLastChecked().getTime() + notRecentlyScannedThresholdMS < now) {
                out.setNumNotRecentlyScanned(out.getNumNotRecentlyScanned() + 1);
                nonRecentlyScanned.add(url);
            }
        }
        out.setNonRecentlyScannedUrls(nonRecentlyScanned.toArray(new String[nonRecentlyScanned.size()]));
        
        return out;
    }
    
    public static class SnapshotPage {

        /**
         * Compares the checksum of this snapshot page to the provided webpagestatus
         * to see if they match.  If they match, then the snapshot is up to date
         * with the webpage status.
         * @param webpage
         * @return 
         */
        public boolean isUpToDate(WebpageStatus webpage) {
            return (this.getTranslationsChecksum() == null ? webpage.getTranslationsChecksum() == null : this.getTranslationsChecksum().equals(webpage.getTranslationsChecksum()));
        }
        
        /**
         * Gets the translation checksum
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
         * @return the page
         */
        public String getPage() {
            return page;
        }

        /**
         * @param page the page to set
         */
        public void setPage(String page) {
            this.page = page;
        }

        /**
         * @return the status
         */
        public PageStatus getStatus() {
            return status;
        }

        /**
         * @param status the status to set
         */
        public void setStatus(PageStatus status) {
            this.status = status;
        }
        private String page;
        private PageStatus status;
        private String translationsChecksum;
        
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

        /**
         * @return the statusCode
         */
        public int getStatusCode() {
            return statusCode;
        }

        /**
         * @param statusCode the statusCode to set
         */
        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        /**
         * @return the statusString
         */
        public String getStatusString() {
            return statusString;
        }

        /**
         * @param statusString the statusString to set
         */
        public void setStatusString(String statusString) {
            this.statusString = statusString;
        }

        /**
         * @return the timestamp
         */
        public Date getTimestamp() {
            return timestamp;
        }

        /**
         * @param timestamp the timestamp to set
         */
        public void setTimestamp(Date timestamp) {
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
            if (page.getPage().trim().length() == 0) {
                continue;
            }
            if (page.getPage().startsWith(webSite.getProxyUrl())) {
                out.add(page.getPage().trim());
            } else if (page.getPage().startsWith(webSite.getSrcUrl())) {
                out.add(webSite.getProxyUrl() + page.getPage().substring(webSite.getSrcUrl().length()));
            } else if (!page.page.startsWith("http://") && !page.page.startsWith("https://")) {
                out.add(webSite.getProxyUrl() + page.getPage());
            }
        }
        return out;
    }
}
