/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.htmlform;

import java.io.IOException;

/**
 *
 * @author shannah
 */
public class HTMLWindow {
    private final HTMLContext context;
    private final BrowserWindow browserWindow;
    
    public HTMLWindow(String path) throws IOException {
        this(null, path);
    }
    
    public HTMLWindow(Object internal, String path) throws IOException {
        browserWindow = new BrowserWindow(internal);
        context = new HTMLContext(browserWindow, path);
        
    }
    
    public HTMLContext getContext() {
        return context;
    }
    
    public static void runQueuedEvent() {
        HTMLContext.runQueuedEvent();
    }
    
    public BrowserWindow getBrowserWindow() {
        return browserWindow;
    }
    
    public void setVisible(boolean visible) {
        browserWindow.setVisible(visible);
    }
}
