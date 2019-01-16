/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.util;

import ca.weblite.swete.SweteApp;
import ca.weblite.swete.SweteClient;
import ca.weblite.swete.models.Snapshot;
import ca.weblite.swete.models.WebSite;
import ca.weblite.swete.services.RefreshSnapshotPageCrawler;
import com.codename1.components.InfiniteProgress;
import com.codename1.components.ToastBar;
import com.codename1.io.Log;
import com.codename1.ui.CN;
import com.codename1.ui.Dialog;
import java.io.IOException;

/**
 *
 * @author shannah
 */
public class SnapshotUtil {
    
    public static void recrawlPageSnapshotAddToQueue(Snapshot snapshot, String url, Runnable onComplete) {
        RefreshSnapshotPageCrawler crawler = new RefreshSnapshotPageCrawler(snapshot) {
            @Override
            protected void onComplete() {
                super.onComplete(); 
                CN.callSerially(onComplete);
            }

            @Override
            protected void setup() {
                String[] urls = getURLs();
                super.setup();
                setURLs(urls);
                
            }
            
            
            
        };
        crawler.setJobDescription("Updating page snapshot in snapshot "+snapshot.getSnapshotId()+" for "+url);
        crawler.setURLs(new String[]{url});
        SweteApp.getInstance().getJobQueue().add(crawler);
       
    }
    
    public static boolean recrawlPageSnapshotInfiniteBlocking(Snapshot snapshot, String url) {
        SweteClient client = new SweteClient(snapshot.getWebSite());
        InfiniteProgress progress = new InfiniteProgress();
        Dialog blocking = progress.showInfiniteBlocking();
        ToastBar.Status status = ToastBar.getInstance().createStatus();
        status.setMessage("Creating snapshot");
        status.setShowProgressIndicator(true);
        status.show();
        boolean success = false;
        try {
            client.refreshPageSnapshotAndWait(url, snapshot);
            success = true;
        } catch (IOException ex) {
            ToastBar.showErrorMessage("Failed to update snapshot: "+ex.getMessage());
            Log.e(ex);
        } finally {
            status.clear();
            blocking.dispose();
        }
        
        return success;
    }
    
    public static void recrawlSnapshot(Snapshot snapshot) {
        //WebSite website = snapshot.getWebSite();
        //SweteClient client = new SweteClient(website);
            
        RefreshSnapshotPageCrawler crawler = new RefreshSnapshotPageCrawler(snapshot);
        crawler.setJobDescription("Refreshing Snapshot "+snapshot.getSnapshotId());
        SweteApp.getInstance().getJobQueue().add(crawler);
    
    }
    
    public static void createNewSnapshot(WebSite website) {
        SweteClient client = new SweteClient(website);
        ToastBar.Status status = ToastBar.getInstance().createStatus();
        status.setMessage("Creating snapshot");
        status.setShowProgressIndicator(true);
        status.show();
        try {
            Snapshot snapshot = client.createNewSnapshot();
            //refresh();
            RefreshSnapshotPageCrawler crawler = new RefreshSnapshotPageCrawler(snapshot);
            crawler.setJobDescription("Refreshing Snapshot "+snapshot.getSnapshotId());
            SweteApp.getInstance().getJobQueue().add(crawler);
            
        } catch (IOException ex) {
            ToastBar.showErrorMessage("Failed to create snapshot: "+ex.getMessage());
            Log.e(ex);
        } finally {
            status.clear();
        }
    }
    
    /**
     * 
     * @param snapshot The snapshot to set active/inactive
     * @param active True to set snapshot as active, false otherwise.
     * @return True if the snapshot is active after this function is done.  false otherwise.
     */
    public static boolean setActiveSnapshot(Snapshot snapshot, boolean active) {
        WebSite website = snapshot.getWebSite();
        SweteClient client = new SweteClient(website);
        if (active) {
            if (website.getCurrentSnapshotId() == null || website.getCurrentSnapshotId() != snapshot.getSnapshotId()) {
                Integer old = website.getCurrentSnapshotId();
                website.setCurrentSnapshotId(snapshot.getSnapshotId());
                ToastBar.Status status = ToastBar.getInstance().createStatus();
                status.setShowProgressIndicator(true);
                status.show();

                try {
                    client.save(website);
                    
                } catch (IOException ex) {
                    website.setCurrentSnapshotId(old);
                    //active.setSelected(false);
                    Log.e(ex);
                    return false;
                } finally {
                    status.clear();
                }
                //refresh();
            }
        } else {
            website.setCurrentSnapshotId(-1);
            ToastBar.Status status = ToastBar.getInstance().createStatus();
            status.setShowProgressIndicator(true);
            status.show();

            try {
                client.save(website);

            } catch (IOException ex) {
                //active.setSelected(true);
                Log.e(ex);
                return true;
            } finally {
                status.clear();
            }
            //refresh();
        }
        return active;
    }
}
