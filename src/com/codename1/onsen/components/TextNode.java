/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.htmlform.HTMLElement;
import com.codename1.onsen.OnsApplication;
import com.codename1.onsen.TagBuilder;

/**
 *
 * @author shannah
 */
public class TextNode extends Element {
    private String text;
    public TextNode(OnsApplication app) {
        super(app, "#TEXT");
    }
    
    public TextNode(OnsApplication app, String text) {
        this(app);
        this.text = text;
    }
    
    public TextNode setText(String text) {
        this.text = text;
        
        return this;
    }
    
    public String getText() {
        return text;
    }

    @Override
    public String createTag() {
        return text;
    }
    
    public AbstractElement appendTo(HTMLElement parent) {
        getContext().execute("var el = document.createTextNode(${0}); "+parent.getSelectionJSString()+".appendChild(el);", new Object[]{text});
        return this;

    }
    
    
    
    public AbstractElement insertBefore(HTMLElement parent, HTMLElement el) {
        getContext().execute("var el = document.createTextNode(${0}); var parent = "+parent.getSelectionJSString()+"; var after = "+el.getSelectionJSString()+"; parent.insertBefore(el, after);", 
                new Object[]{text});
        return this;
        
    }
    
    public AbstractElement insertAfter(HTMLElement parent, HTMLElement el) {
        getContext().execute("var el = document.createTextNode(${0}); var parent = "+parent.getSelectionJSString()+"; var after = "+el.getSelectionJSString()+"; parent.insertAfter(el, after);", 
                new Object[]{text});
        return this;
        
    }
    
    
}
