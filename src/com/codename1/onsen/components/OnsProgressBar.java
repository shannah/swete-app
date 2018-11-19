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
public class OnsProgressBar extends Element {
    
    public OnsProgressBar(OnsApplication app) {
        super(app, "ons-progress-bar");
    }
    
    
    public OnsProgressBar setValue(int value) {
        setProperty("value", value);
        return this;
    }
    
    public int getValue() {
        return this.getIntProperty("value");
    }
    
    public OnsProgressBar setIndeterminate(boolean indeterminate) {
        setProperty("indeterminate", indeterminate);
        return this;
    }
    
    public boolean isIndeterminate() {
        return getBooleanProperty("indeterminate");
    }
}
