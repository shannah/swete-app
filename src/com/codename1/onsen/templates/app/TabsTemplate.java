/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.templates.app;

import com.codename1.onsen.components.OnsTabBar;
import com.codename1.onsen.templates.page.PageWithTabs;

/**
 *
 * @author shannah
 */
public class TabsTemplate extends SinglePageTemplate {
    
    public TabsTemplate() {
        pageTemplate = new PageWithTabs();
    }
    
    private PageWithTabs pageTemplate() {
        return (PageWithTabs)pageTemplate;
    }

    public OnsTabBar getTabBar() {
        return pageTemplate().getTabBar();
    }
    
}
