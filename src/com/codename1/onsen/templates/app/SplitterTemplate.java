/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.templates.app;

import com.codename1.onsen.components.OnsSplitter;
import com.codename1.onsen.components.OnsSplitterSide;
import com.codename1.onsen.templates.page.PageTemplate;
import com.codename1.onsen.templates.page.PageWithSplitter;
import com.codename1.onsen.templates.page.PageWithSplitter.SplitterType;

/**
 *
 * @author shannah
 */
public class SplitterTemplate extends SinglePageTemplate {
    protected PageWithSplitter splitterTemplate;
    public SplitterTemplate(PageWithSplitter splitterTemplate) {
        this.splitterTemplate = splitterTemplate;
        super.setIncludeToolbar(false);
        
    }
    
    public SplitterTemplate() {
        this(new PageWithSplitter());
    }

    public SplitterTemplate setAnimation(OnsSplitterSide.Animation anim) {
        splitterTemplate.setAnimation(anim);
        return this;
    }
    
    public OnsSplitter getSplitter() {
        return splitterTemplate.getSplitter();
    }
    
    public OnsSplitterSide getSplitterSide() {
        return splitterTemplate.getSplitterSide();
    }
    
    @Override
    protected void initUI() {
        super.initUI();
        prepare(splitterTemplate);
        getPage().add(splitterTemplate.getSplitter());
    }
    
    
    public static class Basic extends SplitterTemplate {
        public Basic(PageWithSplitter.SplitterType type) {
            super();
            splitterTemplate = new PageWithSplitter.Basic(type);
        }

        
        
        
        private void initSideUIImpl(PageTemplate tpl) {
            initSideUI(tpl);
        }
        private void initContentUIImpl(PageTemplate tpl) {
            initContentUI(tpl);
        }
        
        protected void initSideUI(PageTemplate tpl) {
            
        }
        
        protected void initContentUI(PageTemplate tpl) {
            
        }
    }
    
    public static class Left extends Basic {
        public Left() {
            super(SplitterType.Left);
        }
    }
    
    public static class Right extends Basic {
        public Right() {
            super(SplitterType.Right);
        }
    }
}


