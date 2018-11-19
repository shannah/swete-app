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
public class OnsToolbar extends Element {
    private Element left, center, right;

   
    public OnsToolbar(OnsApplication app) {
        super(app, "ons-toolbar");
        setFixed(true);
        left = new Element(app);
        left.setAttribute("class", "left");
        center = new Element(app);
        center.setAttribute("class", "center");
        right = new Element(app);
        right.setAttribute("class", "right");
        add(left);
        add(center);
        add(right);
        
    }
    
    public OnsToolbar(OnsApplication app, String title, boolean withBackButton) {
        this(app);
        if (withBackButton) {
            left.add(getApplication().createElement("ons-back-button"));
        }
        center.setInnerText(title);
    }
    
    public void setVisibility(boolean visible) {
        getElement().call("setVisibility(${0})", new Object[]{visible});
    }
    
    public OnsToolbar show() {
        setVisibility(true);
        return this;
    }
    
    public OnsToolbar hide() {
        setVisibility(false);
        return this;
    }

    /**
     * @return the left
     */
    public Element getLeft() {
        return left;
    }

    

    /**
     * @return the center
     */
    public Element getCenter() {
        return center;
    }


    /**
     * @return the right
     */
    public Element getRight() {
        return right;
    }

    public void setTitle(String title) {
        getCenter().setInnerText(title);
    }

    @Override
    public Element add(AbstractElement child) {
        if (left !=null && child.getCSSClasses().contains("left") && !child.equals(left)) {
            remove(left);
            left = (Element)child;
            
        }
        if (right !=null && child.getCSSClasses().contains("right") && !child.equals(right)) {
            remove(right);
            right = (Element)child;
        }
        if (center !=null && child.getCSSClasses().contains("center") && !child.equals(center)) {
            remove(center);
            center = (Element)child;
            
        }
        if (child instanceof OnsBackButton) {
            left.add(child);
            return this;
        }
        return super.add(child); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
}
