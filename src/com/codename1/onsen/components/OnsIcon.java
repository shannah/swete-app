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
public class OnsIcon extends Element {
    public OnsIcon(OnsApplication app) {
        super(app, "ons-icon");
    }
    
    public OnsIcon(OnsApplication app, String iconName) {
        this(app);
        setIcon(iconName);
    }
    
    public void setIcon(String icon) {
        setAttribute("icon", icon);
    }
    
    public String getIcon() {
        return getAttribute("icon");
    }
    
    public OnsIcon setFixedWidth(boolean fixedWidth) {
        return (OnsIcon)attr("fixed-width", fixedWidth);
    }
}
