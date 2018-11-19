/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.htmlform;

import com.codename1.system.NativeInterface;
import java.io.IOException;

/**
 *
 * @author shannah
 */
public interface BrowserWindowNative extends NativeInterface {
    
    public void initBrowserWindowNative(int internal);
    
    public void setDebugMode(boolean debug);

    public void putClientProperty(String key, int valueId);

    public void setFireCallbacksOnEdt(boolean b);

    //public Object getInternal();

    public void setURLHierarchy(String path);



    public void execute(String js);

    public String executeAndReturnString(String js);

    public void addJSCallback(String js, int callbackId);

    public void removeJSCallback(int callbackId);

    
    public void addWebEventListener(String eventName, int listenerId);
    
    public int getInternalId();
    
    
    public void setVisible(boolean visible);

    public void addWindowListener(int pushProperty);

    public void removeWindowListener(int pushProperty);

    public void addWindowResizeListener(int pushProperty);

    public void removeWindowResizeListener(int pushProperty);

    public void setTitle(String title);

    public void setSize(int width, int height);

    public void setPosition(int x, int y);

    public String getTitle();
    public int getWidth();
    public int getHeight();
    public int getX();
    public int getY();
    

}
