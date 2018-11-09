/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.services;

import com.codename1.ui.events.ActionListener;

/**
 *
 * @author shannah
 */
public interface BackgroundJob extends Runnable {
    public static class RequestStatus {
        
        String proxyUrl;
        boolean complete;
        boolean inProgress;
        int responseCode;
        String errorMessage;
    }
    
    public boolean isInProgress();
    public boolean isComplete();
    
    
    public int getTotal();
    
    public int getComplete();
    
    public int getSucceeded();
    
    public int getProgressPercent();
    
    public RequestStatus getCurrentRequest();
    
    public void addProgressListener(ActionListener l);
    public void removeProgressListener(ActionListener l);
    
    public void cancel();
    public boolean isCancelled();
    
    public String getJobDescription();
    public void setJobDescription(String desc);
}
