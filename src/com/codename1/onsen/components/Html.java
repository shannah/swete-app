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
public class Html extends Element {
    public Html(OnsApplication app) {
        super(app, "html");
    }

    @Override
    protected void installImpl() {
        for (AbstractElement head : getElementsByTagName("head")) {
            head.install();
        }
        for (AbstractElement body : getElementsByTagName("body")) {
            body.install();
        }
        
    }
    
    
}
