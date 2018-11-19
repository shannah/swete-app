/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.htmlform;

/**
 *
 * @author shannah
 */
public class Selector {
    private HTMLElement root;
    private String selector;
    private String document = "document";
    
    public Selector(String selector) {
        this.selector = selector;
    }
    
    public Selector(String selector, HTMLElement root) {
        this.selector = selector;
        this.root = root;
        
    }


    public Selector setDocument(String document) {
        this.document = document;
        return this;
    }
    
    public String getSelectionJSString() {
        String base;
        if (root == null) {
            base = document;
        } else if (root.getId() != null) {
            base = document+".getElementById('"+root.getId()+"')";
        } else {
            base = root.getSelector().getSelectionJSString();
        }
        
        return base + ".querySelector('"+selector+"')";
        
    }
    
    public String getCookedSelector() {
        String base;
        if (root == null) {
            base = "";
                    
        } else if (root.getId() != null) {
            base = "#" + root.getId();
        } else {
            base = root.getSelector().getCookedSelector();
        }
        
        return (base + " " +selector).trim();
    }
}
