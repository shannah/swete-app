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
public class Head extends Element {
    public Head(OnsApplication app) {
        super(app, "head");
    }

    @Override
    protected void installImpl() {
        HTMLElement head = getApplication().getContext().getElement(new Selector("head"));
        for (AbstractElement child : this) {
            //if ("script".equals(child.getTagName())) {
                //System.out.println("appending script "+child.createTag());
            //}
            child.appendTo(head);
        }
    }
    
    
}
