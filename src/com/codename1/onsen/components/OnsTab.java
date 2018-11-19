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
public class OnsTab extends Element {
    private Template<OnsPage> page;
    private OnsIcon icon, activeIcon;
    
    public OnsTab(OnsApplication app) {
        super(app, "ons-tab");
    }
    
    
    public OnsTab setPage(OnsPage page) {
        this.page = new Template<OnsPage>(getApplication(), page);
        addDependency(this.page);
        setAttribute("page", this.page.getElement().getId());
        return this;
    }

   
    @Override
    protected void onStateTransition(State old, State newState) {
        if (newState == State.Initialized) {
            if (page != null) {
                getApplication().registerPage(page.getContent());
                page.install();
            }
        }
    }
    
    
    public OnsPage getPage() {
        if (page != null) {
            return page.getContent();
        }
        return null;
    }
    
    public OnsTab setIcon(OnsIcon icon) {
        this.icon = icon;
        setAttribute("icon", icon.getIcon());
        return this;
    }
    
    public OnsIcon getIcon() {
        return icon;
    }
    
    public OnsTab setActiveIcon(OnsIcon icon) {
        this.activeIcon = icon;
        attr("active-icon", icon.getIcon());
        return this;
    }
    
    public OnsIcon getActiveIcon() {
        return this.activeIcon != null ? this.activeIcon : this.icon;
    }
    
    public OnsTab setLabel(String label) {
        return (OnsTab)attr("label", label);
    }
    
    public String getLabel() {
        return getAttribute("label");
    }
    
    public OnsTab setBadge(String badge) {
        return (OnsTab)attr("badge", badge);
    }
    
    public OnsTab setActive(boolean active) {
        return (OnsTab)attr("active", active);
    }
    
}
