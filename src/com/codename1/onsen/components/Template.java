/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.htmlform.HTMLElement;
import com.codename1.onsen.OnsApplication;


/**
 *
 * @author shannah
 */
public class Template<T extends AbstractElement> extends Element {
    private T content;
    
    public Template(OnsApplication app) {
        super(app, "template");
    }
    
    public Template(OnsApplication app, T content) {
        super(app, "template");
        this.content = content;
        add(content);
        
    }
    
    public T getContent() {
        return content;
    }

   
    @Override
    protected void onStateTransition(State old, State newState) {
        if (newState == State.Installing) {
            //System.out.println("Template changing to Installing State");
            if (content != null) {
                content.attr("cn1-template-id", getElement().getId());
            }
        }
    }
    
    
    
    
    @Override
    protected void installImpl() {
        HTMLElement body = getContext().getElement("cn1-body");
        
        appendTo(body);
    }

    @Override
    public Element add(AbstractElement child) {
        super.add(child); 
        if (content == null) {
            content = (T)child;
        }
        return this;
    }
    
    
    
    
}
