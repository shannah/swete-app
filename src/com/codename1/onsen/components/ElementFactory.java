/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.onsen.OnsApplication;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shannah
 */
public interface ElementFactory {
    public AbstractElement create(String tagName);
    static interface Builder {
        AbstractElement build(OnsApplication app);
    }
    
    static Builder builder(Builder b) {
        return b;
    }
    public static class OnDefaultComponentFactory implements ElementFactory {
        
        
        private OnsApplication app;
        private static Map<String,Builder> elementTypes = new HashMap<>();
        static {
            Object[] types = new Object[]{
                "body", builder(app->new Body(app)),
                "head", builder(app->new Head(app)),
                "html", builder(app->new Html(app)),
                "img", builder(app->new Img(app)),
                "ons-back-button", builder(app->new OnsBackButton(app)),
                "ons-button", builder(app->new OnsButton(app)),
                "ons-card", builder(app->new OnsCard(app)),
                "ons-checkbox", builder(app->new OnsCheckBox(app)),
                "ons-fab", builder(app->new OnsFab(app)),
                "ons-icon", builder(app->new OnsIcon(app)),
                "ons-input", builder(app->new OnsInput(app)),
                "ons-list", builder(app->new OnsList(app)),
                "ons-list-item", builder(app->new OnsList.ListItem(app)),
                "ons-list-header", builder(app->new OnsList.ListHeader(app)),
                "ons-list-title", builder(app->new OnsList.ListTitle(app)),
                "ons-navigator", builder(app->new OnsNavigator(app)),
                "ons-page", builder(app->new OnsPage(app)),
                "ons-progress-bar", builder(app->new OnsProgressBar(app)),
                "ons-radio", builder(app->new OnsRadio(app)),
                "ons-range", builder(app->new OnsRange(app)),
                "ons-search-input", builder(app->new OnsSearchInput(app)),
                "ons-select", builder(app->new OnsSelect(app)),
                
                "ons-splitter", builder(app->new OnsSplitter(app)),
                "ons-splitter-content", builder(app->new OnsSplitterContent(app)),
                "ons-splitter-side", builder(app->new OnsSplitterSide(app)),
                "ons-switch", builder(app->new OnsSwitch(app)),
                "ons-tab", builder(app->new OnsTab(app)),
                "ons-tabbar", builder(app->new OnsTabBar(app)),
                "ons-toolbar", builder(app->new OnsToolbar(app)),
                "ons-toolbar-button", builder(app->new OnsToolbarButton(app)),
                "option", builder(app->new OnsSelect.Option(app)),
                "script", builder(app-> new Script(app)),
                "template", builder(app->new Template(app))
                
                
            };
            
            int len = types.length;
            for (int i=0; i<len; i+=2) {
                elementTypes.put((String)types[i], (Builder)types[i+1]);
            }
        }
        
    
        @Override
        public AbstractElement create(String tagName) {
            if (elementTypes.containsKey(tagName)) {
                try {
                    AbstractElement out = ((Builder)(elementTypes.get(tagName))).build(app);
                    return out;
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            Element e = new Element(app, tagName);
            return e;
        }
        
        
        public OnDefaultComponentFactory(OnsApplication app) {
            this.app = app;
        }
        
    }
}
