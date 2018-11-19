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
public interface BrowserComponentProxy {

    public void setDebugMode(boolean debug);

    public void putClientProperty(String key, Object value);

    public void setFireCallbacksOnEdt(boolean b);

    public Object getInternal();

    public void setURLHierarchy(String path) throws IOException;

    public void execute(String js, Object[] params);

    public void execute(String js);

    public String executeAndReturnString(String js, Object[] params);

    public void addJSCallback(String js, SuccessCallback<BrowserComponent.JSRef> callback);

    public void removeJSCallback(Callback<BrowserComponent.JSRef> callback);

    public String executeAndReturnString(String js);
    
    public void addWebEventListener(String eventName, ActionListener l);

    public void addJSCallback(String js, Object[] params, SuccessCallback<BrowserComponent.JSRef> callback);
    
}
