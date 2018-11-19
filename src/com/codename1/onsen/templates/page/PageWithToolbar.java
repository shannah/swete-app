/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.templates.page;

import com.codename1.onsen.components.OnsToolbar;

/**
 *
 * @author shannah
 */
public class PageWithToolbar extends PageTemplate {
    private OnsToolbar toolbar;
    private String title;
    private boolean includeToolbar=true;
    
    public void setTitle(String title) {
        this.title = title;
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }
    
    public String getTitle() {
        return title;
    }
    
    public OnsToolbar getToolbar() {
        return toolbar;
    }
    
    public boolean isIncludeToolbar() {
        return includeToolbar;
    }
    
    public void setIncludeToolbar(boolean inc) {
        
        boolean old = includeToolbar;
        if (old != inc) {
            includeToolbar = inc;
            if (toolbar != null) {
                if (includeToolbar) {
                    getPage().add(toolbar);
                } else {
                    getPage().remove(toolbar);
                }
            }
        }
    }

    
    
    @Override
    protected void initUI() {
        super.initUI();
        toolbar = onsToolbar();
        if (title != null) toolbar.setTitle(title);
        if (includeToolbar) getPage().add(toolbar);
        
        
        
    }
    
}
