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
public class OnsFab extends Element {
    
    public static enum Position {
        TopLeft("top left"),
        TopRight("top right"),
        BottomLeft("bottom left"),
        BottomRight("bottom right"),
        TopCenter("top center"),
        BottomCenter("bottom center"),
        CenterLeft("center left"),
        CenterRight("center right");
        
        String name;
        
        Position(String name) {
            this.name = name;
        }
    }
    
    public OnsFab(OnsApplication app) {
        this(app, null);
    }
    
    public OnsFab(OnsApplication app, Position pos) {
        super(app, "ons-fab");
        setFixed(true);
        if (pos != null) {
            setAttribute("position", pos.name);
        }
    }
    
    public void setPosition(Position pos) {
        setAttribute("position", pos.name);
    }
    
    public Position getPosition() {
        String pos = getAttribute("position");
        if (pos == null || pos.isEmpty()) {
            return null;
        }
        for (Position p : Position.values()) {
            if (pos.equals(p.name)) {
                return p;
            }
        }
        return null;
    }
    
    public boolean isDisabled() {
        return getBooleanProperty("disabled");
    }
    
    public void setDisabled(boolean disabled) {
        setProperty("disabled", disabled);
    }
    
    public boolean isVisible() {
        return getBooleanProperty("visible");
    }
    
    public void setVisible(boolean visible) {
        setProperty("visible", visible);
    }
    
    public OnsFab show() {
        getElement().call("show()");
        return this;
    }
    
    public OnsFab hide() {
        getElement().call("hide()");
        return this;
    }
    
    public void toggle() {
        getElement().call("toggle()");
    }
    
     private Set<Modifier> modifiers;
    
    public static enum Modifier {
        Mini("min");
        
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
