/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.htmlform;

import com.codename1.desktop.CNWindow;
import com.codename1.desktop.CNWindowListener;
import com.codename1.desktop.CNWindowResizeListener;
import com.codename1.system.NativeLookup;
import com.codename1.ui.BrowserComponent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Point;
import com.codename1.ui.geom.Rectangle;
import com.codename1.util.Callback;
import com.codename1.util.SuccessCallback;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class BrowserWindow implements BrowserComponentProxy, CNWindow {
    private final BrowserWindowNative peer;
    private static Map<Integer,Object> propertyMap = new HashMap<Integer,Object>();
    private static int nextPropertyId;
    public static int pushProperty(Object prop) {
        synchronized(BrowserWindow.class) {
            int id = nextPropertyId++;
            propertyMap.put(id, prop);
            return id;
        }
    }
    
    
    
    public static Object popProperty(int propertyId) {
        return propertyMap.remove(propertyId);
    }
    
    public BrowserWindow() {
        peer = NativeLookup.create(BrowserWindowNative.class);
        peer.initBrowserWindowNative(pushProperty(null));
    }
    
    public BrowserWindow(Object internal) {
        peer = NativeLookup.create(BrowserWindowNative.class);
        peer.initBrowserWindowNative(pushProperty(internal));
    }
    
    public static boolean isSupported() {
        try {
            BrowserWindow win = new BrowserWindow();
            return win.peer != null && win.peer.isSupported();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void setDebugMode(boolean debug) {
        peer.setDebugMode(debug);
    }

    @Override
    public void putClientProperty(String key, Object value) {
        peer.putClientProperty(key, pushProperty(value));
    }

    @Override
    public void setFireCallbacksOnEdt(boolean b) {
        peer.setFireCallbacksOnEdt(b);
    }

    @Override
    public Object getInternal() {
        return popProperty(peer.getInternalId());
    }

    @Override
    public void setURLHierarchy(String path) throws IOException {
        peer.setURLHierarchy(path);
    }

    @Override
    public void execute(String js, Object[] params) {
        execute(BrowserComponent.injectParameters(js, params));
    }

    @Override
    public void execute(String js) {
        peer.execute(js);
    }

    @Override
    public String executeAndReturnString(String js, Object[] params) {
        return peer.executeAndReturnString(BrowserComponent.injectParameters(js, params));
    }

    @Override
    public void addJSCallback(String js, SuccessCallback<BrowserComponent.JSRef> callback) {
        peer.addJSCallback(js, pushProperty(callback));
    }

    @Override
    public void removeJSCallback(Callback<BrowserComponent.JSRef> callback) {
        peer.removeJSCallback(pushProperty(callback));
    }

    @Override
    public String executeAndReturnString(String js) {
        return peer.executeAndReturnString(js);
    }

    @Override
    public void addWebEventListener(String eventName, ActionListener l) {
        peer.addWebEventListener(eventName, pushProperty(l));
    }

    @Override
    public void addJSCallback(String js, Object[] params, SuccessCallback<BrowserComponent.JSRef> callback) {
        peer.addJSCallback(BrowserComponent.injectParameters(js, params), pushProperty(callback));
    }
    
    public void setVisible(boolean visible) {
        peer.setVisible(visible);
    }

    @Override
    public void addWindowListener(CNWindowListener l) {
        peer.addWindowListener(pushProperty(l));
    }

    @Override
    public void removeWindowListener(CNWindowListener l) {
        peer.removeWindowListener(pushProperty(l));
    }

    @Override
    public void addWindowResizeListener(CNWindowResizeListener l) {
        peer.addWindowResizeListener(pushProperty(l));
    }

    @Override
    public void removeWindowResizeListener(CNWindowResizeListener l) {
        peer.removeWindowResizeListener(pushProperty(l));
    }

    @Override
    public void setTitle(String title) {
        peer.setTitle(title);
    }

    @Override
    public void setSize(int width, int height) {
        peer.setSize(width, height);
    }

    @Override
    public void setPosition(int x, int y) {
        peer.setPosition(x, y);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        setPosition(x, y);
        setSize(w, h);
    }

    @Override
    public String getTitle() {
        return peer.getTitle();
    }

    @Override
    public Dimension getSize() {
        
        return new com.codename1.ui.geom.Dimension(peer.getWidth(), peer.getHeight());
    }

    @Override
    public Point getPosition() {
        return new com.codename1.ui.geom.Point(peer.getX(), peer.getY());
    }

    @Override
    public Rectangle getBounds() {
        return new com.codename1.ui.geom.Rectangle(peer.getX(), peer.getY(), peer.getWidth(), peer.getHeight());
    }
    
}
