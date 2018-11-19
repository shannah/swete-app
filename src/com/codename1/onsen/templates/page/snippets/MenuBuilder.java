/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.templates.page.snippets;

import com.codename1.htmlform.HTMLContext.DOMEvent;
import com.codename1.onsen.OnsUIBuilder;
import com.codename1.onsen.components.OnsIcon;
import com.codename1.onsen.components.OnsList;
import com.codename1.ui.events.ActionListener;

/**
 *
 * @author shannah
 */
public class MenuBuilder extends OnsUIBuilder {
    private OnsList menu;
    @Override
    public void run() {
        
    }

    @Override
    public void init() {
        super.init();
        menu = onsList();
    }

    
    public OnsList.ListTitle addMenuTitle(String title) {
        OnsList.ListTitle out = onsListTitle(title);
        menu.add(out);
        return out;
    }
    
    public OnsList.ListItem addMenuItem(String label) {
        return addMenuItem(null, label, null);
    }
    
    public OnsList.ListItem addMenuItem(String label, ActionListener<DOMEvent> l) {
        return addMenuItem(null, label, l);
    }
    
    public OnsList.ListItem addMenuItem(OnsIcon icon, String label, ActionListener<DOMEvent> l) {
        OnsList.ListItem out = onsListItem();
        if (icon != null) {
            out.setIcon(icon, OnsList.Position.Left);
        }
        if (label != null) {
            out.setLabel(label);
        }
        if (l != null) {
            out.on("click", l);
        }
        menu.add(out);
        return out;
    }
    
    
    public OnsList get() {
        return menu;
    }
    
}
