package com.codename1.htmlform;


import com.codename1.desktop.CNWindowEvent;
import com.codename1.desktop.CNWindowListener;
import com.codename1.desktop.CNWindowResizeListener;
import com.codename1.impl.javase.JavaSEPort;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.JSONParser;
import com.codename1.io.Log;
import com.codename1.processing.Result;
import com.codename1.ui.BrowserComponent.JSRef;
import com.codename1.ui.Display;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.BrowserNavigationCallback;
import com.codename1.util.Callback;
import com.codename1.util.CallbackAdapter;
import com.codename1.util.SuccessCallback;
import java.awt.EventQueue;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class BrowserWindowNativeImpl implements com.codename1.htmlform.BrowserWindowNative {
    private List<CNWindowListener> windowListeners;
    private List<CNWindowResizeListener> windowResizeListeners;
    private boolean exposeFileSystem = Boolean.getBoolean("cn1.exposeFileSystem");
    private boolean debugMode;
    private WebView webview;
    private java.awt.Window frame;
    private Stage stage;
    private String currentURL;
    private Map<String,List<ActionListener>> listeners;
    private boolean fireCallbacksOnEdt = false;
    private boolean initialized;
    private BrowserNavigationCallback browserNavigationCallback = new BrowserNavigationCallback(){
        public boolean shouldNavigate(String url) {
            return true;
        }
    };
    
    public BrowserWindowNativeImpl() {
        
    }
    private static boolean fxInitialized;
    private static void initFX() {
        if (fxInitialized) return;
        try {
            Platform.runLater(()->{});
        } catch (IllegalStateException ex) {
            if (EventQueue.isDispatchThread()) {
                new JFXPanel();
                
            } else {
                final CountDownLatch latch = new CountDownLatch(1);
                EventQueue.invokeLater(()->{
                    new JFXPanel();
                    latch.countDown();
                });
                try {
                    latch.await();
                } catch (InterruptedException ex1) {
                    Logger.getLogger(BrowserWindowNativeImpl.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        fxInitialized = true;
        return;
    }
    
    private void init() {
        initFX();
        if (internal == null || internal instanceof Stage) {
            if (Platform.isFxApplicationThread()) {
                if (!initialized) {
                    initialized = true;
                    try {
                        initStage();
                    } catch (IllegalStateException ex) {
                        EventQueue.invokeLater(()->initFrame());
                    }
                }
            } else {
                Platform.runLater(()->init());
            }
        } else if (internal instanceof JFrame) {
            if (EventQueue.isDispatchThread()) {
                initFrame();
            } else {
                EventQueue.invokeLater(()->initFrame());
            }
        }
    }

    private boolean visible;
    
    
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        if (Platform.isFxApplicationThread()) {
            init();
            if (stage != null) {
                if (visible) {
                    stage.show();
                } else {
                    stage.hide();
                }
            } else if (frame != null) {
                EventQueue.invokeLater(()->setVisible(visible));
            }
        } else if (EventQueue.isDispatchThread()) {
            if (frame != null) {
                frame.setVisible(visible);
            } else if (stage != null) {
                Platform.runLater(()->setVisible(visible));
            }
        } else {
            Platform.runLater(()->setVisible(visible));
        }
    }

    @Override
    public synchronized void addWindowListener(int propId) {
        CNWindowListener l = (CNWindowListener)BrowserWindow.popProperty(propId);
        if (windowListeners == null) {
            windowListeners = new ArrayList<CNWindowListener>();
        }
        windowListeners.add(l);
    }

    @Override
    public synchronized void removeWindowListener(int propId) {
        CNWindowListener l = (CNWindowListener)BrowserWindow.popProperty(propId);
        if (windowListeners != null) {
            windowListeners.remove(l);
            if (windowListeners.isEmpty()) {
                windowListeners = null;
            }
        }
    }

    @Override
    public void addWindowResizeListener(int propId) {
        CNWindowResizeListener l = (CNWindowResizeListener)BrowserWindow.popProperty(propId);
        if (windowResizeListeners == null) {
            windowResizeListeners = new ArrayList<CNWindowResizeListener>();
        }
        windowResizeListeners.add(l);
    }

    @Override
    public void removeWindowResizeListener(int propId) {
        CNWindowResizeListener l = (CNWindowResizeListener)BrowserWindow.popProperty(propId);
        if (windowResizeListeners != null) {
            windowResizeListeners.remove(l);
            if (windowResizeListeners.isEmpty()) {
                windowResizeListeners = null;
            }
        }
    }

    private String title;
    @Override
    public void setTitle(String title) {
        this.title = title;
        if (stage != null) {
            setStageTitle(title);
        } else if (frame != null) {
            setFrameTitle(title);
        }
    }
    
    private void setFrameTitle(String title) {
        if (EventQueue.isDispatchThread()) {
            if (frame instanceof JDialog) {
                ((JDialog)frame).setTitle(title);
            } else if (frame instanceof JFrame) {
                ((JFrame)frame).setTitle(title);
            }
        } else {
            EventQueue.invokeLater(()->setFrameTitle(title));
        }
    }
    
    private void setStageTitle(String title) {
        if (Platform.isFxApplicationThread()) {
            stage.setTitle(title);
        } else {
            Platform.runLater(()->setStageTitle(title));
        }
    }

    private int width, height;
    
    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        if (stage != null) {
            setStageSize(width, height);
        } else if (frame != null) {
            setFrameSize(width, height);
        }
    }
    
    private void setStageSize(int width, int height) {
        if (Platform.isFxApplicationThread()) {
            stage.setWidth(width);
            stage.setHeight(height);
        } else {
            Platform.runLater(()->setStageSize(width, height));
        }
    }
    
    private void setFrameSize(int width, int height) {
        if (EventQueue.isDispatchThread()) {
            frame.setSize(width, height);
        } else {
            EventQueue.invokeLater(()->setFrameSize(width, height));
        }
    }

    private int x, y;
    
    
    
    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if (stage != null) {
            setStagePosition(x, y);
        } else if (frame != null) {
            setFramePosition(x, y);
        }
    }
    
    private void setStagePosition(int x, int y) {
        if (Platform.isFxApplicationThread()) {
            stage.setX(x);
            stage.setY(y);
        } else {
            Platform.runLater(()->setStagePosition(x, y));
        }
    }
    
    private void setFramePosition(int x, int y) {
        if (EventQueue.isDispatchThread()) {
            frame.setBounds(x,y, frame.getWidth(), frame.getHeight());
        } else {
            EventQueue.invokeLater(()->setFramePosition(x, y));
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    private Object internal;
    
    
    @Override
    public void initBrowserWindowNative(int internal) {
        this.internal = BrowserWindow.popProperty(internal);
        initFX();
        init();
        
    }
    
    public class Bridge {
        
        Bridge() {
           
        }
        
        /**
         * A method injected into the webview to provide direct access to the getBrowserNavigationCallback
         * that is registered.  This is kind of a workaround since there doesn't seem to be any way
         * to prevent the webview from loading a URL that is set via window.location.href and we need
         * the browser navigation callback to be executed for the Javascript bridge to work.  So we inject this
         * here, and the JavascriptContext checks for this hook when trying to send messages.
         * @param url
         * @return 
         */
        public boolean shouldNavigate(String url) {
            return fireBrowserNavigationCallbacks(url);
        } 
        
        public void log(String val) {
            System.out.println("[JS Console] "+val);
        }
    }
    
    /**
     * List of registered browser navigation callbacks.
     */
    private Vector<BrowserNavigationCallback> browserNavigationCallbacks;
    
    private Vector<BrowserNavigationCallback> browserNavigationCallbacks() {
        if (browserNavigationCallbacks == null) {
            browserNavigationCallbacks = new Vector<BrowserNavigationCallback>();
        }
        return browserNavigationCallbacks;
    }
    
    /**
     * Adds a navigation callback.
     * @param callback The callback to call before navigating to a URL.
     */
    public void addBrowserNavigationCallback(BrowserNavigationCallback callback) {
        browserNavigationCallbacks().add(callback);
    }
    
    /**
     * Removes a navigation callback.
     * @param callback The callback to call before navigating to a URL.
     */
    public void removeBrowserNavigationCallback(BrowserNavigationCallback callback) {
        if (browserNavigationCallbacks != null) {
            browserNavigationCallbacks().remove(callback);
        }
    }
    
    /**
     * Decodes a URL
     * @param s The string to decode.
     * @param enc The encoding.  E.g. UTF-8
     * @return The decoded URL.
     */
    private static String decodeURL(String s, String enc){

        boolean needToChange = false;
        int numChars = s.length();
        StringBuilder sb = new StringBuilder(numChars > 500 ? numChars / 2 : numChars);
        int i = 0;

        char c;
        byte[] bytes = null;
        while (i < numChars) {
            c = s.charAt(i);
            switch (c) {
            case '+':
                sb.append(' ');
                i++;
                needToChange = true;
                break;
            case '%':
                /*
                 * Starting with this instance of %, process all
                 * consecutive substrings of the form %xy. Each
                 * substring %xy will yield a byte. Convert all
                 * consecutive  bytes obtained this way to whatever
                 * character(s) they represent in the provided
                 * encoding.
                 */

                try {

                    // (numChars-i)/3 is an upper bound for the number
                    // of remaining bytes
                    if (bytes == null)
                        bytes = new byte[(numChars-i)/3];
                    int pos = 0;

                    while ( ((i+2) < numChars) &&
                            (c=='%')) {
                        int v = Integer.parseInt(s.substring(i+1,i+3),16);
                        if (v < 0)
                            throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - negative value");
                        bytes[pos++] = (byte) v;
                        i+= 3;
                        if (i < numChars)
                            c = s.charAt(i);
                    }

                    // A trailing, incomplete byte encoding such as
                    // "%x" will cause an exception to be thrown

                    if ((i < numChars) && (c=='%'))
                        throw new IllegalArgumentException(
                         "URLDecoder: Incomplete trailing escape (%) pattern");
                    try {
                        sb.append(new String(bytes, 0, pos, enc));
                    } catch (Throwable t) {
                        throw new RuntimeException(t.getMessage());
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                    "URLDecoder: Illegal hex characters in escape (%) pattern - "
                    + e.getMessage());
                }
                needToChange = true;
                break;
            default:
                sb.append(c);
                i++;
                break;
            }
        }

        return (needToChange? sb.toString() : s);
    }
    
    private boolean fireBrowserNavigationCallbacks(String url) {
        //System.out.println("In fireBrowserNavigationCallback: "+url);
        boolean shouldNavigate = true;
        if (browserNavigationCallback != null && !browserNavigationCallback.shouldNavigate(url)) {
            shouldNavigate = false;
        }
        if (browserNavigationCallbacks != null) {
            for (BrowserNavigationCallback cb : browserNavigationCallbacks) {
                if (!cb.shouldNavigate(url)) {
                    shouldNavigate = false;
                }
            }
        }
        if ( !url.startsWith("javascript:") && url.indexOf(RETURN_URL_PREFIX) != -1 ){
            //System.out.println("Found callback");
            //System.out.println("Received browser navigation callback "+url);
            String result = decodeURL(url.substring(url.indexOf(RETURN_URL_PREFIX) + RETURN_URL_PREFIX.length()), "UTF-8");
            //System.out.println("After decode "+result);
            Result structResult = Result.fromContent(result, Result.JSON);
            int callbackId = structResult.getAsInteger("callbackId");
            final String value = structResult.getAsString("value");
            final String type = structResult.getAsString("type");
            final String errorMessage = structResult.getAsString("errorMessage");
            final SuccessCallback<JSRef> callback = popReturnValueCallback(callbackId);
            if (jsCallbacks != null && jsCallbacks.contains(callback)) {
                // If this is a registered callback, then we treat it more like
                // an event listener, and we retain it for future callbacks.
                returnValueCallbacks.put(callbackId, callback);
            }
            if (callback != null) {
                if (errorMessage != null) {
                    if (fireCallbacksOnEdt) {
                        Display.getInstance().callSerially(new Runnable() {

                            public void run() {
                                if (callback instanceof Callback) {
                                    ((Callback)callback).onError(this, new RuntimeException(errorMessage), 0, errorMessage);

                                }
                            }

                        });
                    } else {
                        if (callback instanceof Callback) {
                            ((Callback)callback).onError(this, new RuntimeException(errorMessage), 0, errorMessage);

                        }
                    }
                    
                } else {
                    if (fireCallbacksOnEdt) {
                        Display.getInstance().callSerially(new Runnable() {

                            public void run() {
                                callback.onSucess(new JSRef(value, type));
                            }

                        });
                    } else {
                         callback.onSucess(new JSRef(value, type));
                    }
                    
                }
            } else {
                Log.e(new RuntimeException("Received return value from javascript, but no callback could be found for that ID"));
            }
            shouldNavigate = false;
        }
        return shouldNavigate;
    }
    
    private static EventHandler<WebErrorEvent> createOnErrorHandler() {
        return new EventHandler<WebErrorEvent>() {
            @Override
            public void handle(WebErrorEvent event) {
                Log.p("WebError: " + event.toString());
            }
        };
    }

    @Override
    public boolean isSupported() {
        return true;
    }
    
    
    private void fireWebEvent(String eventName, ActionEvent evt) {
        List<ActionListener> receivers = null;
        synchronized(this) {
            if (listeners.containsKey(eventName)) {
                List<ActionListener> el = listeners.get(eventName);
                receivers = new ArrayList<ActionListener>(el);
            }
        }
        if (receivers != null) {
            for (ActionListener l : receivers) {
                l.actionPerformed(evt);
            }
        }
    }
    private Bridge bridge;
    private Bridge consoleBridge;
    private void initWebview() {
        if (webview != null) return;
        webview = new WebView();
        webview.getEngine().setOnError(createOnErrorHandler());
        webview.getEngine().getLoadWorker().messageProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                
                if (t1.startsWith("Loading http:") || t1.startsWith("Loading file:") || t1.startsWith("Loading https:")) {
                    String url = t1.substring("Loading ".length());
                    if (!url.equals(currentURL)) {
                        fireWebEvent("onStart", new ActionEvent(url));
                    }
                    currentURL = url;
                } else if ("Loading complete".equals(t1)) {
                    
                }
            }
        });
        
        webview.getEngine().setOnAlert(new EventHandler<WebEvent<String>>() {

            @Override
            public void handle(WebEvent<String> t) {
                
                String msg = t.getData();
                if (msg.startsWith("!cn1_message:")) {
                    System.out.println("Receiving message "+msg);
                    fireWebEvent("onMessage", new ActionEvent(msg.substring("!cn1_message:".length())));
                }
            }
            
        });
        
        webview.getEngine().getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {
            @Override
            public void changed(ObservableValue<? extends Throwable> ov, Throwable t, Throwable t1) {
                System.out.println("Received exception: "+t1.getMessage());
                if (ov.getValue() != null) {
                    ov.getValue().printStackTrace();
                }
                if (t != ov.getValue() && t != null) {
                    t.printStackTrace();
                }
                if (t1 != ov.getValue() && t1 != t && t1 != null) {
                    t.printStackTrace();
                }
                
            }
        });
        
        
        webview.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                
                try {
                    netscape.javascript.JSObject w = (netscape.javascript.JSObject)webview.getEngine().executeScript("window");
                    if (w == null) {
                        System.err.println("Could not get window");
                    } else {
                        consoleBridge = new Bridge();
                        //self.putClientProperty("SEBrowserComponent.Bridge.jconsole", b);
                        w.setMember("jconsole", consoleBridge);
                    }
                } catch (Throwable t) {
                    Log.e(t);
                }
               
                String url = webview.getEngine().getLocation();
                if (newState == Worker.State.SCHEDULED) {
                    fireWebEvent("onStart", new ActionEvent(url));
                } else if (newState == Worker.State.RUNNING) {
                    fireWebEvent("onLoadResource", new ActionEvent(url));
                    
                } else if (newState == Worker.State.SUCCEEDED) {
                    //if (!p.isNativeScrollingEnabled()) {
                    //    self.web.getEngine().executeScript("document.body.style.overflow='hidden'");
                    //}
                    
                    // Since I end of injecting firebug nearly every time I have to do some javascript code
                    // let's just add a client property to the BrowserComponent to enable firebug
                    if (debugMode) {
                        webview.getEngine().executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
                    }
                    netscape.javascript.JSObject window = (netscape.javascript.JSObject)webview.getEngine().executeScript("window");
                    bridge = new Bridge();
                    //self.putClientProperty("SEBrowserComponent.Bridge.cn1application", b);
                    window.setMember("cn1application", bridge);
                    
                    webview.getEngine().executeScript("while (window._cn1ready && window._cn1ready.length > 0) {var f = window._cn1ready.shift(); f();}");
                    //System.out.println("cn1application is "+self.web.getEngine().executeScript("window.cn1application && window.cn1application.shouldNavigate"));
                    webview.getEngine().executeScript("window.addEventListener('unload', function(e){console.log('unloading...');return 'foobar';});");
                    fireWebEvent("onLoad", new ActionEvent(url));
                    
                }
                currentURL = url;
            }
        });
        webview.getEngine().getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {
            @Override
            public void changed(ObservableValue<? extends Throwable> ov, Throwable t, Throwable t1) {
                
                t1.printStackTrace();
                if(t1 == null) {
                    if(t == null) {
                        fireWebEvent("onError", new ActionEvent("Unknown error", -1));
                    } else {
                        fireWebEvent("onError", new ActionEvent(t.getMessage(), -1));
                    }
                } else {
                    fireWebEvent("onError", new ActionEvent(t1.getMessage(), -1));
                }
            }
        });

        // Monitor the location property so that we can send the shouldLoadURL event.
        // This allows us to cancel the loading of a URL if we want to handle it ourself.
        webview.getEngine().locationProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> prop, String before, String after) {
                
                if ( !fireBrowserNavigationCallbacks(webview.getEngine().getLocation()) ){
                    webview.getEngine().getLoadWorker().cancel();
                }
            }
        });
        
        
    }

    @Override
    public void setDebugMode(boolean debug) {
        debugMode = debug;
    }

    @Override
    public void putClientProperty(String key, int valueId) {
        Object value = BrowserWindow.popProperty(valueId);
    }

    @Override
    public void setFireCallbacksOnEdt(boolean b) {
        
    }

    //@Override
    //public Object getInternal() {
    //    return stage;
    //}

    
    private BrowserWindow getLightweightPeer() {
        return null;
    }
    
    private void fireWindowOpened() {
        List<CNWindowListener> recipients = null;
        synchronized(this) {
            if (windowListeners != null) {
                recipients = new ArrayList<CNWindowListener>(windowListeners);
            }
        }
        if (recipients != null) {
            CNWindowEvent evt = new CNWindowEvent(getLightweightPeer());
            for (CNWindowListener l : recipients) {
                l.windowOpened(evt);
            }
        }
    }
    
    private void fireWindowClosing() {
        List<CNWindowListener> recipients = null;
        synchronized(this) {
            if (windowListeners != null) {
                recipients = new ArrayList<CNWindowListener>(windowListeners);
            }
        }
        if (recipients != null) {
            CNWindowEvent evt = new CNWindowEvent(getLightweightPeer());
            for (CNWindowListener l : recipients) {
                l.windowClosing(evt);
            }
        }
    }
    
    private void fireWindowClosed() {
        List<CNWindowListener> recipients = null;
        synchronized(this) {
            if (windowListeners != null) {
                recipients = new ArrayList<CNWindowListener>(windowListeners);
            }
        }
        if (recipients != null) {
            CNWindowEvent evt = new CNWindowEvent(getLightweightPeer());
            for (CNWindowListener l : recipients) {
                l.windowClosed(evt);
            }
        }
    }
    
    private void fireWindowIconified() {
        List<CNWindowListener> recipients = null;
        synchronized(this) {
            if (windowListeners != null) {
                recipients = new ArrayList<CNWindowListener>(windowListeners);
            }
        }
        if (recipients != null) {
            CNWindowEvent evt = new CNWindowEvent(getLightweightPeer());
            for (CNWindowListener l : recipients) {
                l.windowIconified(evt);
            }
        }
    }
    
    private void fireWindowDeiconified() {
        List<CNWindowListener> recipients = null;
        synchronized(this) {
            if (windowListeners != null) {
                recipients = new ArrayList<CNWindowListener>(windowListeners);
            }
        }
        if (recipients != null) {
            CNWindowEvent evt = new CNWindowEvent(getLightweightPeer());
            for (CNWindowListener l : recipients) {
                l.windowDeiconified(evt);
            }
        }
    }
    
    private void fireWindowActivated() {
        List<CNWindowListener> recipients = null;
        synchronized(this) {
            if (windowListeners != null) {
                recipients = new ArrayList<CNWindowListener>(windowListeners);
            }
        }
        if (recipients != null) {
            CNWindowEvent evt = new CNWindowEvent(getLightweightPeer());
            for (CNWindowListener l : recipients) {
                l.windowActivated(evt);
            }
        }
    }
    
    private void fireWindowDeactivated() {
        List<CNWindowListener> recipients = null;
        synchronized(this) {
            if (windowListeners != null) {
                recipients = new ArrayList<CNWindowListener>(windowListeners);
            }
        }
        if (recipients != null) {
            CNWindowEvent evt = new CNWindowEvent(getLightweightPeer());
            for (CNWindowListener l : recipients) {
                l.windowDeactivated(evt);
            }
        }
    }
    
    private void fireWindowResized() {
        List<CNWindowResizeListener> recipients = null;
        synchronized(this) {
            if (windowResizeListeners != null) {
                recipients = new ArrayList<CNWindowResizeListener>(windowResizeListeners);
            }
        }
        if (recipients != null) {
            
            for (CNWindowResizeListener l : recipients) {
                l.windowResized(width, height);
            }
        }
    }
    
    private void fireWindowMoved() {
        List<CNWindowResizeListener> recipients = null;
        synchronized(this) {
            if (windowResizeListeners != null) {
                recipients = new ArrayList<CNWindowResizeListener>(windowResizeListeners);
            }
        }
        if (recipients != null) {
            
            for (CNWindowResizeListener l : recipients) {
                l.windowMoved(x, y);
            }
        }
    }
    
    private void fireWindowShown() {
        List<CNWindowResizeListener> recipients = null;
        synchronized(this) {
            if (windowResizeListeners != null) {
                recipients = new ArrayList<CNWindowResizeListener>(windowResizeListeners);
            }
        }
        if (recipients != null) {
            
            for (CNWindowResizeListener l : recipients) {
                l.windowShown();
            }
        }
    }
    
    private void fireWindowHidden() {
        List<CNWindowResizeListener> recipients = null;
        synchronized(this) {
            if (windowResizeListeners != null) {
                recipients = new ArrayList<CNWindowResizeListener>(windowResizeListeners);
            }
        }
        if (recipients != null) {
            
            for (CNWindowResizeListener l : recipients) {
                l.windowHidden();
            }
        }
    }
    
    private void initFrame() {
        if (frame != null) {
            return;
        }
        
        frame = internal instanceof java.awt.Window ? (java.awt.Window)internal : new JFrame();
        if (title != null) {
            if (frame instanceof JFrame) {
                ((JFrame)frame).setTitle(title);
            } else if (frame instanceof JDialog) {
                ((JDialog)frame).setTitle(title);
            }
        }
        if (width != 0 && height != 0) {
            frame.setSize(width, height);
        }
        if (x != 0 || y != 0) {
            setFramePosition(x, y);
        }
        frame.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
                fireWindowOpened();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                fireWindowClosing();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                fireWindowClosed();
            }

            @Override
            public void windowIconified(WindowEvent e) {
                fireWindowIconified();
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                fireWindowDeiconified();
            }

            @Override
            public void windowActivated(WindowEvent e) {
                fireWindowActivated();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                fireWindowDeactivated();
            }
            
        });
        frame.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                width = e.getComponent().getWidth();
                height = e.getComponent().getHeight();
                fireWindowResized();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                x = e.getComponent().getX();
                y = e.getComponent().getY();
                fireWindowMoved();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                visible = true;
                fireWindowShown();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                visible = false;
                fireWindowHidden();
            }
            
        });
        JFXPanel fxWrapper = new JFXPanel();
        if (frame instanceof JFrame) {
            ((JFrame)frame).getContentPane().setLayout(new java.awt.BorderLayout());
            ((JFrame)frame).getContentPane().add(fxWrapper, java.awt.BorderLayout.CENTER);
        } else if (frame instanceof JDialog) {
            ((JDialog)frame).getContentPane().setLayout(new java.awt.BorderLayout());
            ((JDialog)frame).getContentPane().add(fxWrapper, java.awt.BorderLayout.CENTER);
        }
        Platform.runLater(()->{
            initWebview();
            StackPane root = new StackPane();
            root.getChildren().add(webview);
            fxWrapper.setScene(new Scene(root));
        });
        
        if (visible) {
            frame.setVisible(true);
        }
        
    }
    
    private void initStage() {
        if (stage != null) {
            return;
        }
        initWebview();
        
        stage = internal instanceof Stage ? (Stage) internal : new Stage();
        if (title != null) {
            stage.setTitle(title);
        }
        if (width != 0) {
            stage.setWidth(width);
        }
        if (height != 0) {
            stage.setHeight(height);
        }
        if (x != 0) {
            stage.setX(x);
        }
        if (y != 0) {
            stage.setY(y);
        }
        
        
        
        StackPane root = new StackPane();
        root.getChildren().add(webview);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        if (visible) {
            stage.show();
        }
    }
    
    private void setBrowserURL(String url) throws IOException {
        if(url.startsWith("file:") && (url.indexOf("/html/") < 0 || !exposeFileSystem)) {
            
            try {
                File f = new File(FileSystemStorage.getInstance().toNativePath(url));
                url = f.toURI().toString();
            } catch (Throwable t){
                url = "file://" + FileSystemStorage.getInstance().toNativePath(url);
            }
        }
        if (url.startsWith("jar:")) {
            url = url.substring(6);
            url = this.getClass().getResource(url).toExternalForm();
        }
        final String theUrl = url;
        if (Platform.isFxApplicationThread()) {
            webview.getEngine().load(theUrl);
        } else {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    webview.getEngine().load(theUrl);
                }
            });
        }
    }
    
    
    private void setURLHierarchyProdn(String url) throws IOException {
        JavaSEPort.instance.installTar();

        FileSystemStorage fs = FileSystemStorage.getInstance();
        String tardir = fs.getAppHomePath() + "cn1html";
        if(tardir.startsWith("/")) {
            tardir = "file://" + tardir;
        }
        if(url.startsWith("/")) {
            setBrowserURL(tardir + url);
        } else {
            setBrowserURL(tardir  + "/" + url);            
        }
    }
    
    
    @Override
    public void setURLHierarchy(String url) {
        try {
            File f = new File("codenameone_settings.properties");        
            if(!f.exists()) {
                setURLHierarchyProdn(url);
                return;
            }

            String sep = File.separator;
            File[] searchPaths = new File[]{
                new File(f.getParent(), "build" + sep + "classes"+ sep + "html"),
                new File(f.getParent(), "src" + sep + "html"),
                new File(f.getParent(), "lib" + sep + "impl" + sep + "cls" + sep + "html")
            };

            File u = null;
            boolean found = false;
            for (File htmldir : searchPaths) {
                u = new File(htmldir, url);
                if (u.exists()) {
                    u = htmldir;
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("Could not display browser page "+url+" because it doesn't exist in bundle html hierarchy.");
            }
            /*
            File u = new File(f.getParent(), "build" + File.separator + "classes"+ File.separator + "html");
            if (!u.exists()) {
                u = new File(f.getParent(), "src" + File.separator + "html");
            }
            if (!u.exists()) {
                u = new File(f.getParent(), "lib" + File.separator + "impl" + File.separator + "cls" + File.separator )
            }*/
            String base = u.toURI().toURL().toExternalForm(); 
            if(base.endsWith("/")) {
                base = base.substring(0, base.length() - 1);
            }
            if(url.startsWith("/")) {
                setBrowserURL(base + url);
            } else {
                setBrowserURL(base  + "/" + url);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

  
    @Override
    public void execute(String js) {
        if (Platform.isFxApplicationThread()) {
            System.out.println("execute("+js+")");
            webview.getEngine().executeScript(js);
        } else {
            Platform.runLater(()->{
                execute(js);
            });
        }
    }

    @Override
    public String executeAndReturnString(String js) {
        if (Platform.isFxApplicationThread()) {
            try {
                return ""+webview.getEngine().executeScript(js);
            } catch (Throwable jse) {
                System.out.println("Error trying to execute js "+js);
                throw new RuntimeException(jse);
            }
        }
        
        final String[] result = new String[1];
        final boolean[] complete = new boolean[]{false};
        final Throwable[] error = new Throwable[1];
        Platform.runLater(new Runnable() {
            public void run() {
                try {
                    result[0] = ""+webview.getEngine().executeScript(js);
                } catch (Throwable jse) {
                    System.out.println("Error trying to execute js "+js);
                    error[0] = jse;
                }
                synchronized(complete){
                    complete[0] = true;
                    complete.notify();
                }
            }
        });

        // We need to wait for the result of the javascript operation
        // but we don't want to block the entire EDT, so we use invokeAndBlock
        while (!complete[0]) {
            synchronized(complete){
                try {
                    complete.wait(20);
                } catch (InterruptedException ex) {
                }
            }
        }
        if (error[0] != null) {
            throw new RuntimeException(error[0]);
        }
        return result[0];
    }

    @Override
    public void addJSCallback(String js, int callbackId) {
        Callback<JSRef> callback = (Callback<JSRef>)BrowserWindow.popProperty(callbackId);
        jsCallbacks().add(callback);
        execute(js, callback);
    }

    @Override
    public void removeJSCallback(int callbackId) {
        Callback<JSRef> callback = (Callback<JSRef>)BrowserWindow.popProperty(callbackId);
        if (jsCallbacks != null) {
            jsCallbacks.remove(callback);
        }
    }

    @Override
    public synchronized void addWebEventListener(String eventName, int listenerId) {
        ActionListener l = (ActionListener)BrowserWindow.popProperty(listenerId);
        if (listeners == null) {
            listeners = new HashMap<String,List<ActionListener>>();
        }
        if (!listeners.containsKey(eventName)) {
            listeners.put(eventName, new ArrayList<ActionListener>());
        }
        listeners.get(eventName).add(l);
    }

    @Override
    public int getInternalId() {
        return BrowserWindow.pushProperty(stage);
    }
    
    private void execute(String js, SuccessCallback<JSRef> callback) {
        StringBuilder fullJs = new StringBuilder();
        String isSimulator = Display.getInstance().isSimulator() ? "true":"false";
        if (callback == null) {
            callback = new CallbackAdapter<JSRef>();
        }
        int callbackId = addReturnValueCallback(callback);
        fullJs
                .append("(function(){")
                //.append("cn1application.log('we are here');")
                .append("var BASE_URL='https://www.codenameone.com").append(RETURN_URL_PREFIX).append("';")
                .append("function doCallback(val) { ")
                //.append("console.log('in doCallback');")
                .append("  var url = BASE_URL + encodeURIComponent(JSON.stringify(val));")
                .append("  if (window.cn1application && window.cn1application.shouldNavigate) { window.cn1application.shouldNavigate(url) } else if ("+isSimulator+") {window._cn1ready = window._cn1ready || []; window._cn1ready.push(function(){window.cn1application.shouldNavigate(url)});} else {window.location.href=url}")
                .append("} ")
                .append("var result = {value:null, type:null, errorMessage:null, errorCode:0, callbackId:").append(callbackId).append("};")
                .append("var callback = {")
                .append("  onSucess: function(val) { this.onSuccess(val);}, ")
                .append("  onSuccess: function(val) { result.value = val; result.type = typeof(val); if (val !== null && typeof val === 'object') {result.value = val.toString();} doCallback(result);}, ")
                .append("  onError: function(message, code) { result.errorMessage = message; result.errorCode = code; doCallback(result);}")
                .append("};")
                
                .append("try { ").append(js).append("} catch (e) {try {callback.onError(e.message, 0);} catch (e2) {callback.onError('Unknown error', 0);}}")
                
                .append("})();");
        execute(fullJs.toString());
        
    }
    
    
    private static final String RETURN_URL_PREFIX = "/!cn1return/";
    private Hashtable<Integer, SuccessCallback<JSRef>> returnValueCallbacks;
    private Hashtable<Integer, SuccessCallback<JSRef>> returnValueCallbacks() {
        if (returnValueCallbacks == null) {
            returnValueCallbacks = new Hashtable<Integer,SuccessCallback<JSRef>>();
        }
        return returnValueCallbacks;
    }
    
    private int nextReturnValueCallbackId = 0;
    private int addReturnValueCallback(SuccessCallback<JSRef> callback) {
        int id = nextReturnValueCallbackId++;
        while (returnValueCallbacks().containsKey(id)) {
            id++;
        }
        returnValueCallbacks().put(id, callback);
        nextReturnValueCallbackId = id+1;
        if (nextReturnValueCallbackId > 10000) {
            nextReturnValueCallbackId=0;
        }
        return id;
    }
    private SuccessCallback<JSRef> popReturnValueCallback(int id) {
        if (returnValueCallbacks != null) {
            return returnValueCallbacks.remove(id);
        }
        return null;
    }
    private JSONParser returnValueParser;
    private JSONParser returnValueParser() {
        if (returnValueParser == null) {
            returnValueParser = new JSONParser();
        }
        return returnValueParser;
    }
    
    /**
     * Sets of callbacks that are registered to persist for multiple calls.
     */
    private Set jsCallbacks;
    private Set jsCallbacks() {
        if (jsCallbacks == null) {
            jsCallbacks = new HashSet();
        }
        return jsCallbacks;
    }
    

}
