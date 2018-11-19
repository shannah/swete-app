/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.templates.page;

import com.codename1.onsen.components.OnsTab;
import com.codename1.onsen.components.OnsTabBar;

/**
 *
 * @author shannah
 */
public class PageWithTabs extends PageWithToolbar {
    private OnsTabBar tabbar;

    @Override
    protected void initUI() {
        super.initUI();
        tabbar = onsTabBar();
        tabbar.setSwipeable(true);
        tabbar.setPosition(OnsTabBar.Position.Auto);
        tabbar.on("prechange", e->{
            OnsTab selectedTab = tabbar.getTabItem(e);
            getToolbar().setTitle(selectedTab.getLabel());
            
        });
        getPage().add(tabbar);
    }
    
    
    public OnsTabBar getTabBar() {
        return tabbar;
    }
    
}
