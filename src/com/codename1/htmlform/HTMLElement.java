/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.htmlform;

import com.codename1.htmlform.HTMLContext.DOMEvent;
import com.codename1.ui.events.ActionListener;

/**
 *
 * @author shannah
 */
public class HTMLElement {
    private String id;
    private Selector selector;
    private final HTMLContext context;
    private String document="document";
    
    HTMLElement(HTMLContext context, String id) {
        this.context = context;
        this.id = id;
        this.selector = null;
        
    }
    
    HTMLElement(HTMLContext context, Selector selector) {
        this.context = context;
        this.id = null;
        this.selector = selector;
    }
    
    public void setDocument(String doc) {
        this.document = doc;
        if (selector != null) {
            selector.setDocument(document);
        }
    }
    
    public String getId(){
        return id;
    }
    
    public void setId(String id) {
        if (id != this.id) {
            this.id = id;
            this.selector = null;
        }
    }
    
    public Selector getSelector() {
        return selector;
    }
    
    public String getSelectionJSString() {
        return selector != null ? selector.getSelectionJSString() : document+".getElementById('"+id+"')";
    }
    
    public void addCSSClass(String... classes) {
        if (id != null) {
            context.addCSSClass(id, classes);
        } else {
            context.addCSSClass(selector, classes);
        }
    }
    
    public void removeCSSClass(String... classes) {
        if (id != null) {
            context.removeCSSClass(id, classes);
        } else {
            context.removeCSSClass(selector, classes);
        }
    }
    
    public boolean hasAttribute(String attName) {
        if (id != null) {
            return context.hasAttribute(id, attName);
        } else {
            return context.hasAttribute(selector, attName);
        }
    }
    
    public void setAttribute(String attName, String attValue) {
        if (id != null) {
            context.setAttribute(id, attName, attValue);
        } else {
            context.setAttribute(selector, attName, attValue);
        }
    }
    
    public void removeAttribute(String attName) {
        if (id != null) {
            context.removeAttribute(id, attName);
        } else {
            context.removeAttribute(selector, attName);
        }
    }
    
    public void addEventListener(String eventName, ActionListener<DOMEvent> l) {
        if (id != null) {
            context.addEventListener(id, eventName, l);
        } else {
            context.addEventListener(selector, eventName, l);
        }
    }
    
    public void removeEventListener(String eventName, ActionListener<DOMEvent> l){
        if (id != null) {
            context.removeEventListener(id, eventName, l);
        } else {
            context.removeEventListener(selector, eventName, l);
        }
    }
    
    public void setProperty(String propertyName, String propertyValue) {
        if (id != null) {
            context.setProperty(id, propertyName, propertyValue);
        } else {
            context.setProperty(selector, propertyName, propertyValue);
        }
    }
    
    public void setProperty(String propertyName, boolean propertyValue) {
        if (id != null) {
            context.setProperty(id, propertyName, propertyValue);
        } else {
            context.setProperty(selector, propertyName, propertyValue);
        }
    }
    
    public void setProperty(String propertyName, int propertyValue) {
        if (id != null) {
            context.setProperty(id, propertyName, propertyValue);
        } else {
            context.setProperty(selector, propertyName, propertyValue);
        }
    }
    
    public void setStyleProperty(String propertyName, String propertyValue) {
        if (id != null) {
            context.setStyleProperty(id, propertyName, propertyValue);
        } else {
            context.setStyleProperty(selector, propertyName, propertyValue);
        }
              
                
    }
    
    public String getProperty(String propertyName) {
        if (id != null) {
            return context.getProperty(id, propertyName);
        } else {
            return context.getProperty(selector, propertyName);
        }
    }
    
    public void call(String js) {
        if (id != null) {
            context.execute(document+".getElementById('"+id+"')."+js);
        } else {
            context.execute(selector.getSelectionJSString()+"."+js);
        }
        
    }
    
    public void call(String js, Object[] params) {
        if (id != null) {
            context.execute(document+".getElementById('"+id+"')."+js, params);
        } else {
            context.execute(selector.getSelectionJSString()+"."+js, params);
        }
    }
    
    public void remove() {
        if (id != null) {
            context.remove(id);
        } else {
            context.remove(selector);
        }
    }
}
