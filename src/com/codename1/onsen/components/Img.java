/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.onsen.OnsApplication;

/**
 *
 * @author shannah
 */
public class Img extends Element {
    public Img(OnsApplication app) {
        super(app, "img");
    }
    
    public Img(OnsApplication app, String src) {
        this(app);
        setSrc(src);
    }
    
    public Img setSrc(String src) {
        if (isInitialized()) {
            setProperty("src", src);
        } else {
            setAttribute("src", src);
        }
        return this;
    }
    
    public String getSrc() {
        if (isInitialized()) {
            return getStringProperty("src");
        } else {
            return getAttribute("src");
        }
    }
            
}
