/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.htmlform.HTMLContext;
import com.codename1.htmlform.HTMLElement;
import com.codename1.htmlform.HTMLForm;
import com.codename1.htmlform.HTMLContext.DOMEvent;
import com.codename1.io.Util;
import com.codename1.onsen.OnsApplication;
import com.codename1.ui.events.ActionListener;
import com.codename1.util.StringUtil;
import com.codename1.util.SuccessCallback;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author shannah
 */
public abstract class AbstractElement implements Iterable<AbstractElement> {
    private AbstractElement parent;
    private OnsApplication app;
    private HTMLElement el;
    private boolean fixed;
    
    //private boolean installed, initialized;
    private boolean requiresInstall;
    private List<AbstractElement> children;
    private Map<String,List<ActionListener>> listeners;
    private List<Runnable> readyListeners, installOnceListeners, initAlwaysListeners;
    private Map<String,Object> properties;
    private Set<String> cssClasses;
    private final Map<String,String> attributes = new HashMap<>();
    private final Map<String,String> styleProperties = new HashMap<>();
    
    private List<AbstractElement> dependencies;
    
    public static enum State {
        Uninstalled,
        Installing,
        Installed,
        Initialized
    }
    
    private State state=State.Uninstalled;
    
    
    public AbstractElement(OnsApplication app) {
        this(app, null, true);
    }
    
    public AbstractElement(OnsApplication app, String componentId, boolean requiresInstall) {
        this.app = app;
        if (componentId == null) {
            requiresInstall = true;
            componentId = "cn1-cmp-"+app.incrementComponentIndex();
        }
        this.el = app.getContext().getElement(componentId);
        this.requiresInstall = requiresInstall;
        
    }
    
    public AbstractElement addDependency(AbstractElement e) {
        if (dependencies == null) {
            dependencies = new ArrayList<AbstractElement>();
        }
        dependencies.add(e);
        switch (getState()) {
            case Installing:
            case Installed:
            case Initialized:
                e.install();
        }
        return this;
    }
    
    public AbstractElement removeDependency(AbstractElement e) {
        if (dependencies != null) {
            dependencies.remove(e);
            if (dependencies.isEmpty()) {
                dependencies = null;
            }
        }
        return this;
    }
    private boolean dependenciesInstalled;
    public AbstractElement installDependencies() {
        if (!dependenciesInstalled) {
            dependenciesInstalled = true;
            if (dependencies != null) {
                for (AbstractElement dep : dependencies) {
                    dep.install();
                }
            }
            if (children != null) {
                for (AbstractElement child : children) {
                    child.installDependencies();
                }
            }
        }
        return this;
    }
    
    /**
     * Indicates whether this a fixed element (i.e. position:fixed) or not.  Fixed elements may be added 
     * to pages differently.
     * @return 
     */
    public boolean isFixed() {
        return fixed;
    }
    
    public AbstractElement setFixed(boolean fixed) {
        this.fixed = fixed;
        return this;
    }
    
    //public AbstractElement() {
    //    this(OnsApplication.getInstance());
    //}
    
    //public AbstractElement(String componentId, boolean requiresInstall) {
    //    this(OnsApplication.getInstance(), componentId, requiresInstall);
    //}
    
    private AbstractElement onInstallOnce(Runnable r) {
        if (installOnceListeners == null) {
            installOnceListeners = new ArrayList<Runnable>();
        }
        installOnceListeners.add(r);
        return this;
    }
    
    private void onInstallOnce() {
        if (installOnceListeners != null) {
            List<Runnable> toRun = new ArrayList<Runnable>(installOnceListeners);
            installOnceListeners = null;
            for (Runnable r : toRun) {
                r.run();
            }
        }
    }
    
    private AbstractElement onInitAlways(Runnable r) {
        if (initAlwaysListeners == null) {
            initAlwaysListeners = new ArrayList<Runnable>();
        }
        initAlwaysListeners.add(r);
        return this;
    }
    
    private void onInitAlways() {
        if (initAlwaysListeners != null) {
            for (Runnable r : initAlwaysListeners) {
                r.run();
            }
        }
    }
    
    public AbstractElement ready(Runnable r) {
        if (isInitialized()) {
            r.run();
        } else {
            if (readyListeners == null) {
                readyListeners = new ArrayList<>();
            }
            readyListeners.add(r);
        }
        return this;
    }
    
    public abstract String createTag();
    public String createInnerHTML() {
        if (children == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        
        for (AbstractElement child : children) {
            sb.append(child.createTag()).append("\n");
        }
        return sb.toString();
    }
    
    public boolean isInstalled() {
        switch (state) {
            case Installed:
            case Initialized:
                return true;
        }
        return false;
    }
    
    public boolean isInitialized() {
        return state == State.Initialized;
    }
    
    public AbstractElement setState(State state) {
        if (state == this.state) return this;
        Template tpl = getContainingTemplate();
        if (state != State.Initialized && tpl != null) {
            el.setDocument(tpl.getElement().getSelectionJSString()+".content");
        } else {
            el.setDocument("document");
        }
        State old = this.state;
        this.state = state;
        if (state == State.Initialized) {
            
            
            installListeners();
            onInitAlways();
            if (readyListeners != null) {
                List<Runnable> toRun = new ArrayList<Runnable>(readyListeners);
                readyListeners = null;
                for (Runnable r : toRun) {
                    r.run();
                }
            }
        }
        if (state == State.Installed && old == State.Installing) {
            onInstallOnce();
        }
        if (children != null) {
            for (AbstractElement child : children) {
                child.setState(state);
            }
        }
        onStateTransition(old, state);
        return this;
        
    }
    
    protected void onStateTransition(State old, State newState) {
        
    }
    
    /**
     * Initialized means that the component is part of the "actual" dom, and not just part of a template in the dom.
     * @param initialized 
     */
    public final void setInitialized(boolean initialized) {
        setState(State.Initialized);
    }
    
    public final void setInstalled(boolean installed) {
        setState(State.Installed);
    }
    
    void beforeInstallInternal() {
        beforeInstall();
        if (children != null) {
            for (AbstractElement el : children) {
                el.beforeInstallInternal();
            }
        }
    }
    
    protected void beforeInstall() {
        
    }
    
    public OnsApplication getApplication() {
        return app;
    }
    
    public HTMLElement getElement() {
        return el;
    }
    
    public void install() {
        if (requiresInstall && !isInstalled()) {
            beforeInstallInternal();
            installDependencies();
            setState(State.Installing);
            final String eventName = "install-"+getElement().getId();
            ActionListener<DOMEvent> onInstall = new ActionListener<DOMEvent>() {

                @Override
                public void actionPerformed(DOMEvent evt) {
                    getContext().removeDocumentListener(eventName, this);
                    setState(State.Installed);
                }
                
            };
            getContext().addDocumentListener(eventName, onInstall);
            installImpl();
            getContext().execute("(function(){document.dispatchEvent(new CustomEvent(${0}));})()", new Object[]{eventName});
        }
    }
    
    public AbstractElement addCSSClass(String... classes) {
        if (cssClasses == null) cssClasses = new HashSet<>();
        cssClasses.addAll(Arrays.asList(classes));
        if (isInitialized()) {
            getElement().addCSSClass(classes);
        }
        return this;
    }
    
    public AbstractElement removeCSSClass(String... classes) {
        if (cssClasses != null) {
            cssClasses.removeAll(Arrays.asList(classes));
        }
        if (isInitialized()) {
            getElement().removeCSSClass(classes);
        }
        return this;
    }
    
    protected List<String> getCSSClasses() {
        if (cssClasses != null) {
            return new ArrayList<String>(cssClasses);
        } else {
            return new ArrayList<String>();
        }
    }
    
    public boolean hasCSSClass(String cls) {
        return cssClasses != null && cssClasses.contains(cls);
    }
    
    protected void installImpl() {
        
    }
    
    private void installListeners() {
        if (listeners != null) {
            HTMLElement el = getElement();
            for (String key : listeners.keySet()) {
                List<ActionListener> ll = listeners.get(key);
                
                for (ActionListener l : ll) {
                    el.addEventListener(key, l);
                }
            }
            listeners = null;
        }
    }
    
    public HTMLElement getContentElement() {
        return getElement();
    }
    
    public AbstractElement appendTo(HTMLElement parent) {
        getContext().execute("jQuery(${0}).appendTo("+parent.getSelectionJSString()+");", new Object[]{createTag()});
        return this;

    }
    
    
    
    public AbstractElement insertBefore(HTMLElement parent, HTMLElement el) {
        getContext().execute("var el = jQuery(${0}).get(0); var parent = "+parent.getSelectionJSString()+"; var after = "+el.getSelectionJSString()+"; parent.insertBefore(el, after);", 
                new Object[]{createTag()});
        return this;
        
    }
    
    public AbstractElement insertAfter(HTMLElement parent, HTMLElement el) {
        getContext().execute("var el = jQuery(${0}).get(0); var parent = "+parent.getSelectionJSString()+"; var after = "+el.getSelectionJSString()+"; parent.insertAfter(el, after);", 
                new Object[]{createTag()});
        return this;
        
    }
    
    public AbstractElement remove(HTMLElement el) {
        el.call("remove()");
        return this;
    }
    
    public HTMLContext getContext() {
        return getApplication().getContext();
    }
    
    public State getState() {
        return state;
    }
    
    protected void appendChild(AbstractElement child) {
        switch (state) {
            case Installing:
                onInstallOnce(()->child.appendTo(getTargetContainerForChild(child)));
                break;
            case Installed:
                child.appendTo(getTargetContainerForChild(child));
                break;
            case Initialized:
                child.appendTo(getTargetContainerForChild(child));
                break;
        }
    }
    
    protected HTMLElement getTargetContainerForChild(AbstractElement child) {
        return getElement();
    }
    
    public AbstractElement add(AbstractElement child) {
        if (children == null) children = new ArrayList<>();
        child.setParent(this);
        children.add(child);
        appendChild(child);
        child.setState(state);
        return this;
    }
    
    public AbstractElement addAll(List<AbstractElement> els) {
        for (AbstractElement el : els) {
            add(el);
        }
        return this;
    }
    
    public AbstractElement get(int index) {
        if (children == null) return null;
        return children.get(index);
    }
    
    public AbstractElement remove(AbstractElement child) {
        if (children == null || !children.contains(child)) {
            return this;
        }
        
        if (children.remove(child)) {
            child.setParent(null);
            switch (state) {
                case Installing:
                    onInstallOnce(()->remove(child.getElement()));
                    break;
                case Installed:
                case Initialized:
                    remove(child.getElement());
            }
            child.setState(State.Uninstalled);
        }
        return this;
    }
    
    public AbstractElement removeAll() {
        if (children == null) {
            return this;
        }
        List<AbstractElement> toRemove = new ArrayList<AbstractElement>(children);
        children.clear();
        for (AbstractElement el : toRemove) {
            el.setState(State.Uninstalled);
            el.setParent(null);
        }
        String jsString = "(function(){ var el = "+getElement().getSelectionJSString()+"; while (el.firstChild) el.removeChild(el.firstChild);})()";
        switch (state) {
            case Installing:
                onInstallOnce(()->getContext().execute(jsString));
                break;
            case Installed:
            case Initialized:
                getContext().execute(jsString);
        }
        return this;
    }
    
    
    public AbstractElement addEventListener(String eventName, ActionListener<DOMEvent> l) {
        if (isInitialized()) {
            getElement().addEventListener(eventName, l);
        } else {
            if (listeners == null) {
                listeners = new HashMap<>();
            }
            if (!listeners.containsKey(eventName)) {
                listeners.put(eventName, new ArrayList<>());
            }
            listeners.get(eventName).add(l);
        }
        return this;
    }
    
    /**
     * Alias of {@link #addEventListener(java.lang.String, com.codename1.ui.events.ActionListener) }
     * @param eventName
     * @param l
     * @return 
     */
    public AbstractElement on(String eventName, ActionListener<DOMEvent> l) {
        return addEventListener(eventName, l);
    }
    
    public AbstractElement removeEventListener(String eventName, ActionListener<DOMEvent> l) {
        if (isInitialized()) {
            getElement().removeEventListener(eventName, l);
        } else {
            if (listeners != null && listeners.containsKey(eventName)) {
                listeners.get(eventName).remove(l);
            }
        }
        return this;
    }
    
    /**
     * Alias of {@link #removeEventListener(java.lang.String, com.codename1.ui.events.ActionListener) }
     * @param eventName
     * @param l
     * @return 
     */
    public AbstractElement off(String eventName, ActionListener<DOMEvent> l) {
        return removeEventListener(eventName, l);
    }
    
    private Map<String,Object> props() {
        if (properties == null) {
            properties = new HashMap<String,Object>();
        }
        return properties;
    }
    
    
    
    public AbstractElement setProperty(String key, String value) {
        props().put(key, value);
        if (isInitialized()) {
            getElement().setProperty(key, value);
        } 
        onInitAlways(()->{
            getElement().setProperty(key, value);
        });
        
        
        return this;
    }
    
    /**
     * Alias of {@link #setProperty(java.lang.String, java.lang.String) }
     * @param key
     * @param value
     * @return 
     */
    public AbstractElement prop(String key, String value) {
        return setProperty(key, value);
    }
    
    public AbstractElement setProperty(String key, boolean value) {
        props().put(key, value);
        if (isInitialized()) {
            getElement().setProperty(key, value);
        } 
        onInitAlways(()->{
            getElement().setProperty(key, value);
        });
        
        
        return this;
    }
    
    /**
     * Alias of {@link #setProperty(java.lang.String, boolean) }
     * @param key
     * @param value
     * @return 
     */
    public AbstractElement prop(String key, boolean value) {
        return setProperty(key, value);
    }
    
    
    /**
     * 
     * @param key
     * @param value
     * @return 
     */
    public AbstractElement setProperty(String key, int value) {
        props().put(key, value);
        if (isInitialized()) {
            getElement().setProperty(key, value);
        } 
        onInitAlways(()->{
            getElement().setProperty(key, value);
        });
        
        
        return this;
    }
    
    /**
     * Alias of {@link #setProperty(java.lang.String, int) }
     * @param key
     * @param value
     * @return 
     */
    public AbstractElement prop(String key, int value) {
        return setProperty(key, value);
    }
    
    /**
     * 
     * @param key
     * @return 
     */
    public String getStringProperty(String key) {
        if (isInitialized()) {
            return getElement().getProperty(key);
        } else {
            Object prop = props().get(key);
            return prop == null ? null : String.valueOf(prop);
        }
    }
    
    /**
     * Alias of {@link #getStringProperty(java.lang.String) }
     * @param key
     * @return 
     */
    public String prop(String key) {
        return getStringProperty(key);
    }
    
    public Boolean getBooleanProperty(String key) {
        if (isInitialized()) {
            String p = getElement().getProperty(key);
            return "true".equals(p);
        } else {
            return Boolean.TRUE.equals(props().get(key));
        }
    }
    
    /**
     * Alias of {@link #getBooleanProperty(java.lang.String) }
     * @param key
     * @return 
     */
    public Boolean propBool(String key) {
        return getBooleanProperty(key);
    }
    public Integer getIntProperty(String key) {
        if (isInitialized()) {
            String p = getElement().getProperty(key);
            if (p == null) {
                return null;
            }
            return Integer.parseInt(p);
        } else {
            return (Integer)props().get(key);
            
        }
    }

    /**
     * Alias of {@link #getIntProperty(java.lang.String) }
     * @param key
     * @return 
     */
    public Integer propInt(String key) {
        return getIntProperty(key);
    }
     
    public boolean hasAttribute(String key) {
        if (isInstalled()) {
            return getElement().hasAttribute(key);
        } else {
            return attributes.containsKey(key);
        }
    }
    
    public AbstractElement removeAttribute(String key) {
       attributes.remove(key);
       switch (state) {
           case Installing:
               onInstallOnce(()->getElement().removeAttribute(key));
               break;
           case Installed:
           case Initialized:
               getElement().removeAttribute(key);
               break;
       }
       return this;
   }
    
    
    public AbstractElement setAttribute(String key, String value) {
        attributes.put(key, value);
        if ("class".equals(key)) {
            String[] classes = Util.split(value, " ");
            if (cssClasses != null) cssClasses.clear();
            addCSSClass(classes);
        }
        switch (state) {
            case Installing:
                onInstallOnce(()->getElement().setAttribute(key, value));
                break;
            case Installed:
            case Initialized:
                getElement().setAttribute(key, value);
        }
        if ("id".equals(key)) {
            el.setId(value);
        }
        
        return this;
    }
    
    public AbstractElement setAttribute(String key, boolean value) {
        if (value) {
            return setAttribute(key, key);
        } else {
            return removeAttribute(key);
        }
                
    }
    
    public String getAttribute(String key) {
        return attributes.get(key);
    }
    
    public Map<String,String> getAttributes() {
        return new HashMap<String,String>(attributes);
    }
    
    protected Map<String,String> getAttributesInternal() {
        return attributes;
    }
    
    
    
    public AbstractElement attr(String key, String value) {
        setAttribute(key, value);
        return this;
    }
    
    public AbstractElement attr(String key, boolean value) {
        return setAttribute(key, value);
    }
    
    public AbstractElement attrs(String... keysAndValues) {
        int len = keysAndValues.length;
        for (int i=0; i<len; i+=2) {
            setAttribute(keysAndValues[i], keysAndValues[i+1]);
        }
        return this;
    }
    
    public AbstractElement show() {
        return setStyleProperty("display", "");
    }
    
    public AbstractElement hide() {
        return setStyleProperty("display", "none");
    }
    
    public AbstractElement setStyleProperty(String key, String value) {
        styleProperties.put(key, value);
        
        switch (state) {
            case Installing:
                onInstallOnce(()->getElement().setStyleProperty(key, value));
                break;
            case Installed:
            case Initialized:
                getElement().setStyleProperty(key, value);
        }
        
        return this;
    }
    
    public AbstractElement setInnerText(String text) {return this;}
    public AbstractElement setInnerHTML(String html){
        return this;
    }

    @Override
    public Iterator<AbstractElement> iterator() {
        if (children == null) {
            return new ArrayList<AbstractElement>().iterator();
        }
        return children.iterator();
    }
    
    
    public AbstractElement getElementById(String id) {
        if (id.equals(el.getId())) {
            return this;
        }
        for (AbstractElement child : this) {
            AbstractElement found = child.getElementById(id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
    
    public List<AbstractElement> getElementsByTagName(String tagName) {
        return getElementsByTagName(tagName, new ArrayList<AbstractElement>());
    }
    
    private List<AbstractElement> getElementsByTagName(String tagName, List<AbstractElement> out) {
        if (tagName.equals(getTagName())) {
            out.add(this);
        }
        if (children != null) {
            for (AbstractElement child : children) {
                child.getElementsByTagName(tagName, out);
            }
        }
        return out;
    }
    
    public abstract String getTagName();
    
    public AbstractElement getParent() {
        return parent;
    }
    
    private void setParent(AbstractElement parent) {
        this.parent = parent;
    }
    
    public Template getContainingTemplate() {
        if (parent == null) {
            return null;
        }
        if (parent instanceof Template) {
            return (Template)parent;
        }
        return parent.getContainingTemplate();
    }
}
