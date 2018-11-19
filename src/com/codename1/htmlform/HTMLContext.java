/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.htmlform;

import com.codename1.io.Log;
import com.codename1.processing.Result;
import com.codename1.system.NativeLookup;
import com.codename1.ui.BrowserComponent;
import com.codename1.ui.CN;
import com.codename1.ui.Component;
import com.codename1.ui.ComponentSelector;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.util.Callback;
import com.codename1.util.EasyThread;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class HTMLContext {
   
    private final BrowserComponentProxy browser;
    private Map<ActionListener,RegisteredCallback> callbackMap;
    private int nextCallbackId;
    private boolean loaded;
    private ArrayList<Runnable> onLoad=new ArrayList<>();
    private HTMLFormNative nativeInstance;
    
    private ArrayList lookup = new ArrayList();

    
    public <T> T lookup(Class<T> cls) {
        for (Object o : lookup) {
            if (cls.isAssignableFrom(o.getClass())) {
                return (T)o;
            }
        }
        return null;
    }
    

    
    public void addLookup(Object o) {
        lookup.add(o);
    }
    
    public void replaceLookup(Class cls, Object o) {
        removeLookup(cls);
        lookup.add(o);
    }
    
    public void removeLookup(Class cls) {
        List toRemove = new ArrayList();
        for (Object o : lookup) {
            if (cls.isAssignableFrom(o.getClass())) {
                toRemove.add(o);
            }
        }
        for (Object o : toRemove) {
            lookup.remove(o);
        }
    }

  

    
    public void setDebugMode(boolean debug) {
        browser.setDebugMode(debug);
    }
    

    

    
    
    public class DOMEvent extends ActionEvent {
        private Result data;
        public DOMEvent(Object source, Result eventData) {
            super(source);
            this.data = eventData;
        }
        
        public Result getEventData() {
            return data;
        }
        
    }
    
    private class RegisteredCallback implements Callback<BrowserComponent.JSRef> {
        private final int callbackId=nextCallbackId++;
        private final ActionListener listener;

        
        RegisteredCallback(ActionListener l) {
            this.listener = l;
        }
        

        @Override
        public void onSucess(BrowserComponent.JSRef value) {
            String val = value.getValue();
            final Result eventData;
            int pos;
            final String selector;
            if (val.indexOf("@sel(") == 0) {
                val = val.substring(val.indexOf("(")+1);
                pos = val.indexOf(")");
                selector = val.substring(0, pos);
                val = val.substring(pos+1);
            } else {
                selector = null;
            }
            if ((pos = val.indexOf(":")) >= 0) {
                eventData = Result.fromContent(val.substring(pos+1), "json");
                val = val.substring(0, pos);
            } else {
                eventData = null;
            }
            
            
            if (isMainThread()) {
                try {
                    listener.actionPerformed(new DOMEvent(selector == null ? getElement(val) : getElement(new Selector(selector)), eventData));
                } catch (Throwable t) {
                    Log.e(t);
                }
            } else {
                String fVal = val;
                runLater(()->{
                    try {
                        listener.actionPerformed(new DOMEvent(selector == null ? getElement(fVal) : getElement(new Selector(selector)), eventData));
                    } catch (Throwable t) {
                        Log.e(t);
                    }
                });
            }
        }
        
        public int getCallbackId() {
            return callbackId;
        }

        @Override
        public void onError(Object sender, Throwable err, int errorCode, String errorMessage) {
            Log.e(err);
            onSucess(null);
            
        }
        
    }
    
    private void onLoad(Runnable r) {
        if (loaded) {
            ArrayList<Runnable> toRun;
            if (onLoad != null && !onLoad.isEmpty()) {
                synchronized(this) {
                    if (onLoad != null && !onLoad.isEmpty()) {
                        toRun = new ArrayList<>(onLoad);
                        onLoad.clear();
                        onLoad = null;
                    } else {
                        toRun = null;
                    }

                }
            } else {
                toRun = null;
            }
            if (isMainThread()) {
                if (toRun != null) {
                    for (Runnable run : toRun) {
                        run.run();
                    }
                }
                if (r != null) {
                    r.run();
                }
            } else {
                runLater(()->{
                    if (toRun != null) {
                        for (Runnable run : toRun) {
                            run.run();
                        }
                    }
                    if (r != null) {
                        r.run();
                    }
                });
            }
        } else {
            if (r == null) {
                return;
            }
            synchronized(this) {
                if (onLoad == null) {
                    onLoad = new ArrayList<>();
                }
                onLoad.add(r);
            }
        }
    }
    
    public HTMLContext(BrowserComponentProxy browserProxy, String path) throws IOException {
       
        
        nativeInstance = NativeLookup.create(HTMLFormNative.class);
        
        browser = browserProxy;
        browser.putClientProperty("BrowserComponent.firebug", Boolean.TRUE);
        browser.setFireCallbacksOnEdt(false); // We want to work on native thread.
        Object internal = browser.getInternal();
        if (internal instanceof Component) {
            ComponentSelector.select((Component)internal).selectAllStyles().setMargin(0).setPadding(0);
        }
        //add(BorderLayout.CENTER, browser);
        browser.addWebEventListener("onLoad", e->{
            //System.out.println("in onLoad " + e.getSource());
            loaded = true;
            onLoad(null);
        });
        browser.setURLHierarchy(path);
        
    }
    
    
    public Object getInternal() {
        return browser.getInternal();
    }
    
    public void remove(String id) {
        browser.execute("(function(){var el = document.getElementById(${0}); if (!el) return; el.remove()})()", new Object[]{id});
    }
    
    public void remove(Selector selector) {
        browser.execute("(function(){var el = "+selector.getSelectionJSString()+"; if (!el) return; el.remove()})()");
    }
    
    public HTMLElement getElement(Selector selector) {
        return new HTMLElement(this, selector);
    }
    
    public HTMLElement getElement(String id){
        return new HTMLElement(this, id);
    }
    
    public String getProperty(String id, String propertyName){
        return browser.executeAndReturnString("(function(){var el = document.getElementById(${0}); if (!el) return null; return el[${1}]; })();", new Object[]{id, propertyName});
    }
    public String getProperty(Selector selector, String propertyName) {
        return browser.executeAndReturnString("(function(){var el = "+selector.getSelectionJSString()+"; if (!el) return null; return el[${0}]; })();", new Object[]{propertyName});
    }
    
    public int getPropertyInt(String id, String propertyName){
        return 0;
    }
    public double getPropertyDouble(String id, String propertyName){return 0;}
    public String getStyleProperty(String id, String propertyName){return null;}
    public int getStylePropertyInt(String id, String propertyName){return 0;}
    public double getStylePropertyDouble(String id, String propertyName){ return 0;}
    
    public void addCSSClass(String id, String... classes) {
        ArrayList params = new ArrayList();
        StringBuilder js = new StringBuilder();
        params.add(id);
        js.append("(function(){var el = document.getElementById(${0}); if (!el) return;");
        int paramIndex=1;
        for (String cls : classes) {
            params.add(cls);
            js.append("el.classList.add(${").append(paramIndex++).append("});");
        }
        js.append("})()");
        browser.execute(js.toString(), params.toArray(new Object[params.size()]));
    }
    public void addCSSClass(Selector selector, String... classes) {
        ArrayList params = new ArrayList();
        StringBuilder js = new StringBuilder();
        js.append("(function(){var el = ").append(selector.getSelectionJSString()).append("; if (!el) return;");
        int paramIndex=0;
        for (String cls : classes) {
            params.add(cls);
            js.append("el.classList.add(${").append(paramIndex++).append("});");
        }
        js.append("})()");
        browser.execute(js.toString(), params.toArray(new Object[params.size()]));
    }
    public void removeCSSClass(String id, String... classes){
        ArrayList params = new ArrayList();
        StringBuilder js = new StringBuilder();
        params.add(id);
        js.append("var el = document.getElementById(${0}); if (!el) return;");
        int paramIndex=1;
        for (String cls : classes) {
            params.add(cls);
            js.append("el.classList.remove(${").append(paramIndex++).append("});");
        }
        browser.execute(js.toString(), params.toArray(new Object[params.size()]));  
    }
    public void removeCSSClass(Selector selector, String... classes){
        ArrayList params = new ArrayList();
        StringBuilder js = new StringBuilder();
        
        js.append("var el = "+selector.getSelectionJSString()+"; if (!el) return;");
        int paramIndex=0;
        for (String cls : classes) {
            params.add(cls);
            js.append("el.classList.remove(${").append(paramIndex++).append("});");
        }
        browser.execute(js.toString(), params.toArray(new Object[params.size()]));  
    }
    public void addCSSClassBySelector(String selector, String... classes){}
    public void removeCSSClassBySelector(String selector, String... classes){}
    public void addCSSClassBySelector(String rootId, String selector, String... classes){}
    public void removeCSSClassBySelector(String rootId, String selector, String... classes){}
    
    public void setProperty(String id, String propertyName, int propertyValue) {
        browser.execute("(function(){var el = document.getElementById(${0}); if (!el) return; el[${1}]=${2}; })();", new Object[]{id, propertyName, propertyValue});
    }
    
    public void setProperty(Selector selector, String propertyName, int propertyValue) {
        browser.execute("(function(){var el = "+selector.getSelectionJSString()+"; if (!el) return; el[${0}]=${1}; })();", new Object[]{propertyName, propertyValue});
    }
    
    public void setProperty(String id, String propertyName, boolean propertyValue) {
        browser.execute("(function(){var el = document.getElementById(${0}); if (!el) return; el[${1}]=${2}; })();", new Object[]{id, propertyName, propertyValue});
    }
    
    public void setProperty(Selector selector, String propertyName, boolean propertyValue) {
        browser.execute("(function(){var el = "+selector.getSelectionJSString()+"; if (!el) return; el[${0}]=${1}; })();", new Object[]{propertyName, propertyValue});
    }
    
    public void setProperty(String id, String propertyName, String propertyValue){
        browser.execute("(function(){var el = document.getElementById(${0}); if (!el) return; el[${1}]=${2}; })();", new Object[]{id, propertyName, propertyValue});
    }
    
    public void setProperty(Selector selector, String propertyName, String propertyValue){
        browser.execute("(function(){var el = "+selector.getSelectionJSString()+"; if (!el) return; el[${0}]=${1}; })();", new Object[]{propertyName, propertyValue});
    }
    public void setPropertyBySelector(String selector, String propertyName, String propertyValue){}
    public void setPropertyBySelector(String rootId, String selector, String propertyName, String propertyValue){}
    public void setProperties(String id, JSProperty... properties){}
    public void setPropertiesBySelector(String selector, JSProperty... properties){}
    public void setPropertiesBySelector(String rootId, String selector, JSProperty... properties){}
    public void setStyleProperty(String id, String propertyName, String propertyValue){
        browser.execute("(function(){var el = document.getElementById(${0}); if (!el) return; el.style[${1}]=${2}})()", new Object[]{id, propertyName, propertyValue});
    }
    public void setStyleProperty(Selector selector, String propertyName, String propertyValue) {
        browser.execute("(function(){var el ="+selector.getSelectionJSString()+"; if (!el) return; el.style[${0}]=${1}})()", new Object[]{propertyName, propertyValue});
    }
    public void setStylePropertyBySelector(String selector, String propertyName, String propertyValue){
    
    
    }
    public void setStylePropertyBySelector(String rootId, String selector, String propertyName, String propertyValue){}
    public void setStyleProperties(String id, JSProperty... properties) {}
    public void setStylePropertiesBySelector(String selector, JSProperty... properties){}
    public void setStylePropertiesBySelector(String rootId, String selector, JSProperty... properties){}
    
    public void setVisible(String id, boolean visible){
        setProperty(id, "display", visible ? "" : "none");
    }
    public void setVisibleBySelector(String selector, boolean visible){}
    public void setVisibleBySelector(String rootId, String selector, boolean visible){}
    
    private synchronized RegisteredCallback getCallbackForListener(ActionListener l) {
        if (callbackMap == null) {
            callbackMap = new HashMap<ActionListener,RegisteredCallback>();
        }
        RegisteredCallback callback;
        if (callbackMap.containsKey(l)) {
            callback = callbackMap.get(l);
        } else {
            callback = new RegisteredCallback(l);
            callbackMap.put(l, callback);
        }
        return callback;
        
    }
    
    private synchronized void removeCallbackForListener(ActionListener l) {
        if (callbackMap != null) {
            callbackMap.remove(l);
        }
    }
    
    public void addDocumentListener(String eventName, ActionListener<DOMEvent> l) {
        RegisteredCallback callback = getCallbackForListener(l);
        browser.addJSCallback("(function(){var f = function(evt){var callbackId = ${0}; evt.cn1Detail = evt.detail; var stringData = callbackId+':'+JSON.stringify(evt); callback.onSuccess(stringData)}; window.htmlformCallbacks = window.htmlformCallbacks||{}; window.htmlformCallbacks[${2}] = f; document.addEventListener(${1}, f);})()", 
                new Object[]{"*document", eventName, callback.getCallbackId()},
                callback
        );
        
    }
    public void removeDocumentListener(String eventName, ActionListener<DOMEvent> l) {
        RegisteredCallback callback = getCallbackForListener(l);
        browser.removeJSCallback((Callback<BrowserComponent.JSRef>)callback);
        browser.execute("(function(){if (!window.htmlformCallbacks) return;  var f = window.htmlformCallbacks[${1}]; if (!f) return; var el = document.removeEventListener(${0}, f);})()",
                new Object[]{eventName, callback.getCallbackId()}
                
        );
        removeCallbackForListener(l);
    }
    
    public boolean isDocumentElement(String id) {
        return "*document".equals(id);
    }
    
    public boolean isDocumentElement(HTMLElement el) {
        return isDocumentElement(el.getId());
    }
    
    public boolean hasAttribute(String id, String attName) {
        return "true".equals(browser.executeAndReturnString("(function(){var el = document.getElementById(${0});if (!el) return false; return el.hasAttribute(${1});})()", new Object[]{id, attName}));
    }
    
    public boolean hasAttribute(Selector selector, String attName) {
        return "true".equals(browser.executeAndReturnString("(function(){var el = "+selector.getSelectionJSString()+";if (!el) return false; return el.hasAttribute(${0});})()", new Object[]{attName}));
    }
    
    public void setAttribute(String id, String attName, String attValue) {
        if (attValue == null) {
            removeAttribute(id, attName);
            return;
        }
        browser.execute("(function(){var el = document.getElementById(${0});if (!el) return; el.setAttribute(${1}, ${2});})()", new Object[]{id, attName, attValue});
    }
    
    public void setAttribute(Selector selector, String attName, String attValue) {
        if (attValue == null) {
            removeAttribute(selector, attName);
            return;
        }
        browser.execute("(function(){var el = "+selector.getSelectionJSString()+";if (!el) return; el.setAttribute(${0}, ${1});})()", new Object[]{attName, attValue});
    }

    public void removeAttribute(Selector selector, String attName) {
        browser.execute("(function(){var el = "+selector.getSelectionJSString()+";if (!el) return; el.removeAttribute(${0});})()", new Object[]{attName});
    }
    
    public void removeAttribute(String id, String attName) {
        browser.execute("(function(){var el = document.getElementById(${0});if (!el) return; el.removeAttribute(${1});})()", new Object[]{id, attName});
    }
    
    public void addEventListener(String id, String eventName, ActionListener<DOMEvent> l) {
        if (isDocumentElement(id)) {
            addDocumentListener(eventName, l);
            return;
        }
        RegisteredCallback callback = getCallbackForListener(l);
        browser.addJSCallback("(function(){var f = function(evt){var callbackId = ${1}; evt.cn1Details = evt.detail; var stringData = callbackId+':'+JSON.stringify(evt);callback.onSuccess(stringData)}; window.htmlformCallbacks = window.htmlformCallbacks||{}; window.htmlformCallbacks[${2}] = f; document.getElementById(${1}).addEventListener(${0}, f);})()", 
                new Object[]{eventName, id, callback.getCallbackId()},
                callback
        );
        
    }
    public void addEventListener(Selector selector, String eventName, ActionListener<DOMEvent> l) {
        
        RegisteredCallback callback = getCallbackForListener(l);
        browser.addJSCallback("(function(){var f = function(evt){var callbackId = '@sel("+selector.getCookedSelector()+")'; var stringData = callbackId+':'+JSON.stringify(evt);callback.onSuccess(stringData)}; window.htmlformCallbacks = window.htmlformCallbacks||{}; window.htmlformCallbacks[${1}] = f; "+selector.getSelectionJSString()+".addEventListener(${0}, f);})()", 
                new Object[]{eventName, callback.getCallbackId()},
                callback
        );
    }
    public void removeEventListener(String id, String eventName, ActionListener<DOMEvent> l) {
        if (isDocumentElement(id)) {
            removeDocumentListener(eventName, l);
            return;
        }
        RegisteredCallback callback = getCallbackForListener(l);
        browser.removeJSCallback((Callback<BrowserComponent.JSRef>)callback);
        browser.execute("(function(){if (!window.htmlformCallbacks) return;  var f = window.htmlformCallbacks[${2}]; if (!f) return; var el = document.getElementById(${1}); if (!el) return; el.removeEventListener(${0}, f);})()",
                new Object[]{eventName, id, callback.getCallbackId()}
        );
        removeCallbackForListener(l);
    }
    
    public void removeEventListener(Selector selector, String eventName, ActionListener<DOMEvent> l) {
        RegisteredCallback callback = getCallbackForListener(l);
        browser.removeJSCallback((Callback<BrowserComponent.JSRef>)callback);
        browser.execute("(function(){if (!window.htmlformCallbacks) return;  var f = window.htmlformCallbacks[${1}]; if (!f) return; var el = "+selector.getSelectionJSString()+"; if (!el) return; el.removeEventListener(${0}, f);})()",
                new Object[]{eventName, callback.getCallbackId()}
        );
        removeCallbackForListener(l);
    }
    
    private static ArrayList<Runnable> dispatchQueue = new ArrayList<Runnable>();
    
    public static void runQueuedEvent() {
        Runnable next;
        synchronized(dispatchQueue) {
            
            next = dispatchQueue.isEmpty() ? null : dispatchQueue.remove(0);
        }
        if (next != null) {
            next.run();
        }
    }
    
    public void runLater(Runnable r){
        if (!loaded) {
            onLoad(r);
        } else {
            if ("HTML5".equals(CN.getPlatformName())) {
                CN.callSerially(r);
                return;
            }
            synchronized(dispatchQueue) {
                dispatchQueue.add(r);
            }
            nativeInstance.notifyDispatchQueue();
        }
    }
    
    public void run(Runnable r) {
        if (!loaded) {
            onLoad(r);
        } else {
            if (isMainThread()) {
                r.run();
                return;
            }
            if ("HTML5".equals(CN.getPlatformName())) {
                CN.callSerially(r);
                return;
            }
            synchronized(dispatchQueue) {
                dispatchQueue.add(r);
            }
            nativeInstance.notifyDispatchQueue();
        }
    }
    
    public boolean isMainThread(){
        if ("HTML5".equals(CN.getPlatformName())) {
            return CN.isEdt();
        }
        return nativeInstance.isMainThread();
    }
    
    
    public void execute(String js) {
        browser.execute(js);
    }
    
    public void execute(String js, Object[] params) {
        browser.execute(js, params);
    }
    
    public String executeAndReturnString(String js) {
        return browser.executeAndReturnString(js);
    }
    
    public String executeAndReturnString(String js, Object[] params) {
        return browser.executeAndReturnString(js, params);
    }
    
    public Result executeAndReturnJSON(String js) {
        return Result.fromContent(executeAndReturnString(js), "json");
    }
    
    public Result executeAndReturnJSON(String js, Object[] params) {
        return Result.fromContent(executeAndReturnString(js, params), "json");
    }
    
}
