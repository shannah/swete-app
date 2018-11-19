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
public class OnsRadio extends Element  {
    
    public static class RadioGroup {
        private String name;
        public RadioGroup(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    private RadioGroup group;
    private Set<Modifier> modifiers;
    
    
    public static enum Modifier {
        Material("material");
        
        String name;
        
        Modifier(String name) {
            this.name =name;
        }
    }
    
    public OnsRadio(OnsApplication app) {
        super(app, "ons-radio");
    }
    
    public void setChecked(boolean checked) {
        setProperty("checked", checked);
    }
    
    public boolean isChecked() {
        return getBooleanProperty("checked");
    }
    
    public void setValue(String value) {
        setProperty("value", value);
    }
    
    public String getValue() {
        return getStringProperty("value");
    }
    
    
    public void setDisabled(boolean disabled) {
        setProperty("disabled", disabled);
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
     
     public void setGroup(RadioGroup group) {
         this.group = group;
         setAttribute("name", group.name);
     }
     
     public RadioGroup getGroup() {
         return group;
     }
     
     
    
}
