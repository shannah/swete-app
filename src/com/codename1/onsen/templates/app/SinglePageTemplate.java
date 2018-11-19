/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.templates.app;

import com.codename1.onsen.OnsUITemplate;
import com.codename1.onsen.components.OnsNavigator;
import com.codename1.onsen.components.OnsPage;
import com.codename1.onsen.components.OnsToolbar;
import com.codename1.onsen.templates.page.PageTemplate;
import com.codename1.onsen.templates.page.PageWithToolbar;

/**
 *
 * @author shannah
 */
public class SinglePageTemplate extends OnsUITemplate {
    private OnsNavigator nav;
    protected PageWithToolbar pageTemplate = new PageWithToolbar();
    
    public OnsNavigator getNavigator() {
        return nav;
    }
    
    public OnsPage getPage() {
        return pageTemplate.getPage();
    }
    
    public OnsToolbar getToolbar() {
        return pageTemplate.getToolbar();
    }
    
    public void setTitle(String title) {
        pageTemplate.setTitle(title);
    }
    
    public String getTitle() {
        return pageTemplate.getTitle();
    }
    
    public boolean isIncludeToolbar() {
        return pageTemplate.isIncludeToolbar();
    }
    
    public void setIncludeToolbar(boolean inc) {
        pageTemplate.setIncludeToolbar(inc);
    }
    
    @Override
    public void init() {
        nav = onsNavigator();
        pageTemplate.setApplication(getApplication());
        pageTemplate.init();
        initUI();
    }

    protected void initUI() {
        
    }
    
    @Override
    public void run() {
        init();
        nav.install();
        nav.pushPage(getPage());
    }
    
}
