/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.htmlform.HTMLElement;
import com.codename1.htmlform.Selector;
import com.codename1.onsen.OnsApplication;

/**
 *
 * @author shannah
 */
public class Body extends Element {
    public Body(OnsApplication app) {
        super(app, "body");
    }

    @Override
    protected void installImpl() {
        for (AbstractElement page : getElementsByTagName("ons-page")) {
            if (page instanceof OnsPage) {
                getApplication().registerPage((OnsPage)page);
            }
        }
        HTMLElement body = getContext().getElement(new Selector("body"));
        //System.out.println("Installing body...");
        
        for (AbstractElement child : this) {
            child.appendTo(body);
        }
    }
    
    
}
