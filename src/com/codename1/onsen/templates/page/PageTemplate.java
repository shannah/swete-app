/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.templates.page;

import com.codename1.onsen.OnsUITemplate;
import com.codename1.onsen.components.OnsPage;

/**
 *
 * @author shannah
 */
public class PageTemplate extends OnsUITemplate{
    private OnsPage page;

    public OnsPage getPage() {
        return page;
    }
    
    @Override
    public void init() {
        page = onsPage();
        initUI();
    }
    
    protected void initUI() {
        
    }

    @Override
    public void run() {
        
    }
    
    
}
