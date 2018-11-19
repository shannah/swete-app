/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.htmlform;

import com.codename1.io.Log;
import com.codename1.processing.Result;
import com.codename1.system.NativeLookup;
import com.codename1.ui.BrowserComponent;
import com.codename1.ui.BrowserComponent.JSRef;
import com.codename1.ui.Component;
import com.codename1.ui.ComponentSelector;
import com.codename1.ui.Form;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;

import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.util.Callback;
import com.codename1.util.SuccessCallback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class HTMLForm extends Form {
    private final HTMLContext context;
    
    public HTMLForm(String path) throws IOException {
        super("", new BorderLayout());
        Toolbar tb = getToolbar();
        if (tb != null) {
            tb.setUIID("Container");
            tb.hideToolbar();
        }
        context = new HTMLContext(new DefaultBrowserComponentProxy(), path);
        
        ComponentSelector.select(this).selectAllStyles().setPadding(0).setMargin(0);
        Object internal = context.getInternal();
        if (internal instanceof Component) {
            add(BorderLayout.CENTER, (Component)internal);
        
        }
    }
    
    public HTMLContext getContext() {
        return context;
    }
    
    public static void runQueuedEvent() {
        HTMLContext.runQueuedEvent();
    }
}
