/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.htmlform;

import com.codename1.ui.BrowserComponent;
import com.codename1.ui.events.ActionListener;
import com.codename1.util.Callback;
import com.codename1.util.SuccessCallback;
import java.io.IOException;

/**
 *
 * @author shannah
 */
public class DefaultBrowserComponentProxy implements BrowserComponentProxy {
    private final BrowserComponent internal;
    public DefaultBrowserComponentProxy() {
        internal = new BrowserComponent();
    }
    
    @Override
    public void setDebugMode(boolean debug) {
        internal.setDebugMode(debug);
    }

    @Override
    public void putClientProperty(String key, Object value) {
        internal.putClientProperty(key, value);
    }

    @Override
    public void setFireCallbacksOnEdt(boolean b) {
        internal.setFireCallbacksOnEdt(b);
    }

    @Override
    public Object getInternal() {
        return internal;
    }

    @Override
    public void setURLHierarchy(String path) throws IOException {
        internal.setURLHierarchy(path);
    }

    @Override
    public void execute(String js, Object[] params) {
        internal.execute(js, params);
    }

    @Override
    public void execute(String js) {
        internal.execute(js);
    }

    @Override
    public String executeAndReturnString(String js, Object[] params) {
        return internal.executeAndReturnString(js, params);
    }

    @Override
    public void addJSCallback(String js, SuccessCallback<BrowserComponent.JSRef> callback) {
        internal.addJSCallback(js, callback);
    }

    @Override
    public void removeJSCallback(Callback<BrowserComponent.JSRef> callback) {
        internal.removeJSCallback(callback);
    }

    @Override
    public String executeAndReturnString(String js) {
        return internal.executeAndReturnString(js);
    }

    @Override
    public void addWebEventListener(String eventName, ActionListener l) {
        internal.addWebEventListener(eventName, l);
    }

    @Override
    public void addJSCallback(String js, Object[] params, SuccessCallback<BrowserComponent.JSRef> callback) {
        internal.addJSCallback(js, params, callback);
    }
    
}
