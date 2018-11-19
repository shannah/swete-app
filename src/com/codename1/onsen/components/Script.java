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
public class Script extends Element {
    public Script(OnsApplication app) {
        super(app, "script");
    }

    @Override
    public Element setInnerText(String innerText) {
        return setInnerHTML(innerText);
    }
    
    
    
}
