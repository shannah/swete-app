/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.htmlform.HTMLContext.DOMEvent;
import com.codename1.onsen.OnsApplication;
import com.codename1.ui.events.ActionListener;
import com.codename1.util.SuccessCallback;

/**
 *
 * @author shannah
 */
public class OnsTabBar extends Element {
    public OnsTabBar(OnsApplication app) {
        super(app, "ons-tabbar");
    }
    
    public static enum Position {
        Top,
        Bottom,
        Auto
    }
    
    public void setActiveTab(int index, SuccessCallback callback) {
        if (!isInitialized()) {
            ready(()->setActiveTab(index, callback));
            return;
        }
        final OnsApplication app = getApplication();
        final String eventName = "cn1TabChanged";
        ActionListener<DOMEvent> l = new ActionListener<DOMEvent>() {

            @Override
            public void actionPerformed(DOMEvent evt) {
                app.getContext().removeDocumentListener(eventName, this);
                callback.onSucess(null);
            }
        };
        app.getContext().addDocumentListener(eventName, l);
        getElement().call("setActiveTab(${0}, {callback:function(){document.dispatchEvent(new CustomEvent(${1}));}})", new Object[]{index, eventName});
    }
    
    public void setTabbarVisibility(boolean visible) {
        if (!isInitialized()) {
            ready(()->setTabbarVisibility(visible));
            return;
        }
        getElement().call("setTabbarVisibility(${0})", new Object[]{visible});
    }
    
    public int getActiveTabIndex() {
        return Integer.parseInt(getApplication().getContext().executeAndReturnString(getElement().getSelectionJSString()+".getActiveTabIndex()"));
    }
    
    public OnsTabBar setSwipeable(boolean swipeable) {
        return (OnsTabBar)attr("swipeable", swipeable);
    }
    
    public OnsTabBar setPosition(Position position) {
        return (OnsTabBar)attr("position", position.name().toLowerCase());
    }
    
    public OnsTab getTab(int index) {
        return (OnsTab)get(index);
    }
    
    public int getIndex(DOMEvent evt) {
        return evt.getEventData().getAsInteger("cn1Details/index");
    }
    
    public OnsTab getTabItem(DOMEvent evt) {
        return getTab(getIndex(evt));
    }
}
