/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.services;

import ca.weblite.swete.SweteClient;
import ca.weblite.swete.models.WebSite;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.Log;
import com.codename1.io.Util;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shannah
 */
public abstract class AbstractPageCrawler implements BackgroundJob {
    private int throttleDelay = 2000;
    /**
     * @return the jobDescription
     */
    @Override
    public String getJobDescription() {
        return jobDescription;
    }

    /**
     * @param jobDescription the jobDescription to set
     */
    @Override
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
    protected WebSite site;
    protected SweteClient client;
    private final List<RequestStatus> statuses = new ArrayList<>();
    private RequestStatus currentRequest;
    private EventDispatcher listeners = new EventDispatcher();
    private String[] urls;
    private boolean cancelled;
    private String jobDescription;
    private boolean inProgress, complete;
    
    
    public AbstractPageCrawler(WebSite site) {
        this.site = site;
        this.client = new SweteClient(site);
        listeners.setBlocking(true);
    }

    @Override
    
    public void run() {
        try {
            runImpl();
        } catch (Exception ex) {
            fireException(ex);
        }
    }
    
    public class RequestResult {
        int responseCode;
        
        public RequestResult(int responseCode) {
            this.responseCode = responseCode;
        }
        
        public int getResponseCode() {
            return responseCode;
        }
        
    }
    
    public void runImpl() throws IOException {
        inProgress = true;
        try {
            listeners.fireActionEvent(new ActionEvent(this));
            setup();

            for (String srcUrl : getURLs()) {
                RequestStatus status = new RequestStatus();
                status.proxyUrl = srcUrl;
                statuses.add(status);
            }
            for (RequestStatus status : statuses) {
                if (cancelled) {
                    listeners.fireActionEvent(new ActionEvent(this));
                    break;
                }
                currentRequest = status;
                status.inProgress = true;
                listeners.fireActionEvent(new ActionEvent(this));
                RequestResult req = sendRequest(status.proxyUrl);
                status.responseCode = req.getResponseCode();
                status.complete = true;
                status.inProgress = false;

                listeners.fireActionEvent(new ActionEvent(this));
                Util.sleep(throttleDelay);

            }
            complete = true;
            onComplete();
            listeners.fireActionEvent(new ActionEvent(this));
        } finally {
            inProgress = false;
            listeners.fireActionEvent(new ActionEvent(this));
        }
        
    }
    
    protected abstract RequestResult sendRequest(String url);
    protected void setup() {
        
    }
    
    protected void onComplete() {
        
    }
    
    public String[] getURLs() {
        if (urls == null) {
            if (site.getWhitelist() == null) {
                try {
                    site.setWhitelist(client.loadWhitelist());
                    urls = site.getWhitelist();
                    int len = urls.length;
                    for (int i=0; i<len; i++) {
                        urls[i] = site.getProxyUrlForPage(urls[i]);
                    }
                } catch (IOException ex) {
                    Log.e(ex);
                    urls = new String[0];
                }
            } else {
                urls = site.getWhitelist();
            }
        }
        return urls;
    }
    
    protected void fireException(Exception ex) {
        Log.e(ex);
    }
    
    @Override
    public void addProgressListener(ActionListener l) {
        listeners.addListener(l);
    }
    @Override
    public void removeProgressListener(ActionListener l) {
        listeners.removeListener(l);
    }
    
    @Override
    public int getTotal() {
        return statuses.size();
    }
    
    @Override
    public int getComplete() {
        int count = 0;
        for (RequestStatus status : statuses) {
            if (status.complete) count++;
            else break;
        }
        return count;
    }
    
    @Override
    public int getSucceeded() {
        int count = 0;
        for (RequestStatus status : statuses) {
            if (status.complete) {
                if (status.responseCode >= 200 && status.responseCode < 300) {
                    count++;
                }
            } else {
                break;
            }
        }
        return count;
    }
    
    @Override
    public int getProgressPercent() {
        return (int)Math.round(getComplete() / (double)getTotal() * 100.0);
    }
    
    @Override
    public RequestStatus getCurrentRequest() {
        return currentRequest;
    }
    
    public void setURLs(String[] urls) {
        this.urls = urls;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isInProgress() {
        return inProgress;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }
    
    
    
    
    
    
}
