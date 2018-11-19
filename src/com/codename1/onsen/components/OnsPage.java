/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.htmlform.HTMLElement;
import com.codename1.htmlform.HTMLContext.DOMEvent;
import com.codename1.htmlform.Selector;
import com.codename1.onsen.OnsApplication;
import com.codename1.ui.events.ActionListener;


/**
 *
 * @author shannah
 */
public class OnsPage extends Element {
    private AbstractElement contentEl;
    private AbstractElement backgroundEl;
    
    public OnsPage(OnsApplication app) {
        super(app, "ons-page");
        contentEl = app.createElement("div").addCSSClass("content");
        backgroundEl = app.createElement("div").addCSSClass("background");
        super.add(backgroundEl);
        super.add(contentEl);
        //contentEl = getContext().getElement(new Selector(".page__content", getElement()));
    }
    
    public HTMLElement getContentElement() {
        return contentEl.getElement();
    }
    
    public HTMLElement getBackgroundElement() {
        return backgroundEl.getElement();
    }

    @Override
    public Element add(AbstractElement child) {
        if (!child.isFixed()) {
            if (child.hasCSSClass("content")) {
                super.remove(contentEl);
                contentEl = child;
                super.add(contentEl);
            } else if (child.hasCSSClass("background")) {
                super.remove(backgroundEl);
                backgroundEl = child;
                super.add(backgroundEl);
            } else {
                contentEl.add(child);
            }
            return this;
        } else {
            return super.add(child);
        }
    }

    @Override
    public AbstractElement remove(AbstractElement child) {
        if (!child.isFixed()) {
            if (child.hasCSSClass("content")) {
                super.remove(child);
                contentEl = getApplication().createElement("div").addCSSClass("content");
                super.add(contentEl);
            } else if (child.hasCSSClass("background")) {
                super.remove(child);
                backgroundEl = getApplication().createElement("div").addCSSClass("background");
                super.add(backgroundEl);
            } else {
                contentEl.remove(child);
            }
            return this;
        }
        return super.remove(child);
    }
    
    
    

    
    public void addShowListener(ActionListener<DOMEvent> l) {
        addEventListener("show", l);
    }
    
    public void removeShowListener(ActionListener<DOMEvent> l) {
        removeEventListener("show", l);
    }
    
    public void addHideListener(ActionListener<DOMEvent> l) {
        addEventListener("hide", l);
    }
    
    public void removeHideListener(ActionListener<DOMEvent> l) {
        removeEventListener("hide", l);
    }
    
    public void addInitListener(ActionListener<DOMEvent> l) {
        addEventListener("init", l);
    }
    
    public void removeInitListener(ActionListener<DOMEvent> l) {
        removeEventListener("init", l);
    }
    
    public void addDestroyListener(ActionListener<DOMEvent> l) {
        addEventListener("destroy", l);
    }
    
    public void removeDestroyListener(ActionListener<DOMEvent> l) {
        removeEventListener("destroy", l);
    }

    @Override
    protected void installImpl() {
        getApplication().registerPage(this);
        HTMLElement body = getContext().getElement("cn1-body");
        appendTo(body);
        
    }
    
    
    
}
