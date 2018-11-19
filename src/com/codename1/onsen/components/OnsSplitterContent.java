/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.htmlform.HTMLContext.DOMEvent;
import com.codename1.htmlform.HTMLForm;
import com.codename1.onsen.OnsApplication;
import com.codename1.ui.events.ActionListener;
import com.codename1.util.StringUtil;
import com.codename1.util.SuccessCallback;

/**
 *
 * @author shannah
 */
public class OnsSplitterContent extends Element {
    private OnsPage page;
    public OnsSplitterContent(OnsApplication app) {
        super(app, "ons-splitter-content");
    }
    
    public OnsSplitterContent setPage(OnsPage page) {
        if (page != this.page) {
            this.page = page;
            removeAll();
            if (page != null) {
                getApplication().registerPage(page);
                add(page);
            }
        }
        return this;
    }
    
    public OnsPage getPage() {
        return page;
    }
    
    public OnsSplitterContent load(OnsPage page, SuccessCallback callback) {
        Template<OnsPage> tpl = new Template<>(getApplication(), page);
        final OnsApplication app = getApplication();
        final String eventName = StringUtil.replaceAll("cn1PageLoad-"+page.getElement().getId(), "-", "");
        ActionListener<DOMEvent> l = new ActionListener<DOMEvent>() {

            @Override
            public void actionPerformed(DOMEvent evt) {
                tpl.getElement().remove();
                app.getContext().removeDocumentListener(eventName, this);
                callback.onSucess(null);
            }
            
        };
        app.getContext().addDocumentListener(eventName, l);
        app.registerPage(page);
        
        tpl.install();
        getElement().call("load(${0}, {callback:function(){document.dispatchEvent(new CustomEvent(${1}))}})", new Object[]{tpl.getElement().getId(), eventName});
        return this;
    }
    
    
}
