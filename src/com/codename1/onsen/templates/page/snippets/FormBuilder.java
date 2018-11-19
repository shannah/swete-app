/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.templates.page.snippets;

import com.codename1.onsen.OnsUIBuilder;
import com.codename1.onsen.components.Element;
import com.codename1.onsen.components.OnsIcon;
import com.codename1.onsen.components.OnsList;


/**
 *
 * @author shannah
 */
public class FormBuilder extends OnsUIBuilder {

    
    
    
    @Override
    public void run() {
        
    }
    
    public static enum LabelPosition {
        Left,
        Center,
        Right
    }
    
        
    public OnsList.ListItem createField(String label, Element field) {
        return createField(LabelPosition.Left, label, field);
    } 
    
    public OnsList.ListItem createField(LabelPosition pos, String label, Element field) {
        if (getApplication() == null) {
            throw new IllegalStateException("Form builder not initialized");
        }
        OnsList.ListItem row = onsListItem();
        Element labelEl = (Element)createElement("label");
        labelEl.addCSSClass(pos.name().toLowerCase());
        labelEl.setInnerText(label);
        
        switch (pos) {
            case Right: {
                field.addCSSClass("center");
                row.setCenter(field);
                row.setRight(labelEl);
                break;
            }
            case Left: {
                field.addCSSClass("center");
                row.setCenter(field);
                row.setLeft(labelEl);
                
                break;
            } 
            case Center:
                field.addCSSClass("right");
                row.setRight(field);
                row.setCenter(labelEl);
                break;
        }
        
        return row;
        
        
    }
    
}
