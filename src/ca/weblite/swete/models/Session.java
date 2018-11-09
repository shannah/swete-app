/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.models;

import com.codename1.ui.BrowserComponent;

/**
 *
 * @author shannah
 */
public class Session {

    /**
     * @return the captureEnabled
     */
    public boolean isCaptureEnabled() {
        return captureEnabled;
    }

    /**
     * @param captureEnabled the captureEnabled to set
     */
    public void setCaptureEnabled(boolean captureEnabled) {
        this.captureEnabled = captureEnabled;
    }

    /**
     * @return the snapshotsEnabled
     */
    public boolean isSnapshotsEnabled() {
        return snapshotsEnabled;
    }

    /**
     * @param snapshotsEnabled the snapshotsEnabled to set
     */
    public void setSnapshotsEnabled(boolean snapshotsEnabled) {
        this.snapshotsEnabled = snapshotsEnabled;
    }
    private boolean captureEnabled, snapshotsEnabled;
    
    
    
}
