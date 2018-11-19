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
public class OnsToolbarButton extends Element {
    public OnsToolbarButton(OnsApplication app) {
        super(app, "ons-toolbar-button");
    }
    
    public OnsToolbarButton(OnsApplication app, OnsIcon icon) {
        this(app);
        add(icon);
    }
    
}
