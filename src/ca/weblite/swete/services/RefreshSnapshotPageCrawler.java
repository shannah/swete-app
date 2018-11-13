/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.services;

import ca.weblite.swete.models.Snapshot;
import com.codename1.io.Log;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author shannah
 */
public class RefreshSnapshotPageCrawler extends AbstractPageCrawler {
    private Snapshot snapshot;
    public RefreshSnapshotPageCrawler(Snapshot snapshot) {
        super(snapshot.getWebSite());
        this.snapshot = snapshot;
        
    }

    @Override
    protected void setup() {
        super.setup();
        try {
            client.load(snapshot);
        } catch (IOException ex) {
            Log.e(ex);
            fireException(ex);
        }
        List<String> urls = snapshot.getProxyUrls();
        
        setURLs(urls.toArray(new String[urls.size()]));

    }

    @Override
    protected void onComplete() {
        super.onComplete();
        snapshot.setDateCompleted(new Date());
        try {
            client.save(snapshot);
        } catch (IOException ex) {
            fireException(ex);
        }
        
    }
    
    
    public Snapshot getSnapshot() {
        return snapshot;
    }
    
    
    @Override
    protected RequestResult sendRequest(String url) {
        try {
            client.refreshPageSnapshotAndWait(url, snapshot);
            return new RequestResult(200);
        } catch (IOException ex) {
            Log.e(ex);
            return new RequestResult(500);
        }
        
    }
    
}
