/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.onsen.OnsApplication;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author shannah
 */
public class OnsCheckBox extends Element  {
    
    private Set<Modifier> modifiers;
    
    public static enum Modifier {
        Material("material"),
        NoBorder("noborder");
        
        String name;
        
        Modifier(String name) {
            this.name =name;
        }
    }
    
    public OnsCheckBox(OnsApplication app) {
        super(app, "ons-checkbox");
    }
    
    public OnsCheckBox setChecked(boolean checked) {
        setProperty("checked", checked);
        return this;
    }
    
    public boolean isChecked() {
        return getBooleanProperty("checked");
    }
    
    public OnsCheckBox setValue(String value) {
        setProperty("value", value);
        return this;
    }
    
    public String getValue() {
        return getStringProperty("value");
    }
    
    
    public OnsCheckBox setDisabled(boolean disabled) {
        setProperty("disabled", disabled);
        return this;
    }
    
    public boolean isDisabled() {
        return getBooleanProperty("disabled");
    }
    
    public Set<Modifier> getModifiers() {
        Set<Modifier> out = new HashSet<Modifier>();
        if (modifiers != null) {
            out.addAll(modifiers);
        }
        return out;
    }
    
    public OnsCheckBox setModifiers(Modifier... mods) {
        if (modifiers == null) {
            modifiers = new HashSet<>();
        }
        modifiers.clear();
        addModifiers(mods);
        return this;
    }
    
    public OnsCheckBox addModifiers(Modifier... mods) {
        if (modifiers == null) {
            modifiers = new HashSet<Modifier>();
        }
        modifiers.addAll(Arrays.asList(mods));
        setAttribute("modifier", createModifiersString());
        return this;
        
    }
    
    public OnsCheckBox removeModifiers(Modifier... mods) {
        if (modifiers != null) {
            modifiers.removeAll(Arrays.asList(mods));
        }
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
    
}
