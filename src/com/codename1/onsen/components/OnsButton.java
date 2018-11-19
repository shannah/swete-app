/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.htmlform.HTMLContext.DOMEvent;
import com.codename1.onsen.OnsApplication;
import com.codename1.ui.events.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author shannah
 */
public class OnsButton extends Element {
    
    public static enum Modifier {
        Outline("outline"),
        Light("light"),
        Quiet("quiet"),
        Cta("cta"),
        Large("large"),
        LargeQuiet("large--quiet"),
        LargeCta("large-cta"),
        Material("material"),
        MaterialFlat("material--flat");
        
        String name;
        
        Modifier(String name) {
            this.name = name;
        }
        
    }
    
    private HashSet<Modifier> modifiers;

    public OnsButton(OnsApplication app) {
        this(app, "");
    }
    
   
    public OnsButton(OnsApplication app, String label) {
        super(app, "ons-button");
        setInnerText(label);
        
    }
    
    public OnsButton addClickListener(ActionListener<DOMEvent> l) {
        addEventListener("click", l);
        return this;
    }
    
    public OnsButton removeClickListener(ActionListener<DOMEvent> l) {
        removeEventListener("click", l);
        return this;
    }
    
    public OnsButton setDisabled(boolean disabled) {
        getElement().setProperty("disabled", disabled);
        return this;
    }
    
    public OnsButton addModifiers(Modifier... presets) {
        if (this.modifiers == null) {
            this.modifiers = new HashSet<Modifier>();
        }
        this.modifiers.addAll(Arrays.asList(presets));
        setAttribute("modifier", createModifiersString());
        return this;
    }
    
    private String createModifiersString() {
        StringBuilder sb = new StringBuilder();
        boolean first=true;
        for (Modifier p : modifiers) {
            if (first) {
                first = false;
            } else {
                sb.append(" ");
            }
            sb.append(p.name);
                
        }
        return sb.toString();
    }
    
    public OnsButton removeModifiers(Modifier... presets) {
        if (this.modifiers == null) return this;
        this.modifiers.removeAll(Arrays.asList(presets));
        setAttribute("modifier", createModifiersString());
        return this;
    }
    
    
}
