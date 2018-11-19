/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.onsen.OnsApplication;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author shannah
 */
public class OnsInput extends Element {
    public OnsInput(OnsApplication app) {
        super(app, "ons-input");
        setType("text");
    }
    
    public OnsInput(OnsApplication app, String value) {
        super(app, "ons-input");
        setType("text");
        setValue(value);
    }
    
    public void setValue(String value) {
        setProperty("value", value);
    }
    
    public String getValue() {
        return getStringProperty("value");
    }
    
    public boolean isDisabled() {
        return getBooleanProperty("disabled");
    }
    
    public void setDisabled(boolean dis) {
        setProperty("disabled", dis);
    }
    
    public void setPlaceholder(String text) {
        setAttribute("placeholder", text);
    }
    
    public String getPlaceholder() {
        return getAttribute("placeholder");
    }
    
    public void setFloat(boolean f) {
        if (f) {
            setAttribute("float", "float");
        } else {
            removeAttribute("float");
        }
    }
    
    public void setType(String type) {
        setAttribute("type", type);
    }
    
    public String getType() {
        return getAttribute("type");
    }
    
    public void setInputId(String inputId) {
        setAttribute("input-id", inputId);
    }
    
    public String getInputId() {
        return getAttribute("input-id");
    }
    
    private Set<Modifier> modifiers;
    
    public static enum Modifier {
        Material("material"),
        Underbar("Underbar"),
        Transparent("Transparent");
        
        String name;
        
        Modifier(String name) {
            this.name =name;
        }
    }
    
     public Set<Modifier> getModifiers() {
        Set<Modifier> out = new HashSet<Modifier>();
        if (modifiers != null) {
            out.addAll(modifiers);
        }
        return out;
    }
    
    public void setModifiers(Modifier... mods) {
        if (modifiers == null) {
            modifiers = new HashSet<>();
        }
        modifiers.clear();
        addModifiers(mods);
    }
    
    public void addModifiers(Modifier... mods) {
        if (modifiers == null) {
            modifiers = new HashSet<Modifier>();
        }
        modifiers.addAll(Arrays.asList(mods));
        setAttribute("modifier", createModifiersString());
        
    }
    
    public void removeModifiers(Modifier... mods) {
        if (modifiers != null) {
            modifiers.removeAll(Arrays.asList(mods));
        }
        setAttribute("modifier", createModifiersString());
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
