/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.htmlform.HTMLForm;
import com.codename1.htmlform.HTMLContext.DOMEvent;
import com.codename1.onsen.OnsApplication;
import com.codename1.onsen.Orientation;
import com.codename1.onsen.Scalar;
import com.codename1.ui.events.ActionListener;
import com.codename1.util.StringUtil;
import com.codename1.util.SuccessCallback;

/**
 *
 * @author shannah
 */
public class OnsSplitterSide extends Element {
    
    public static enum Animation {
        Overlay,
        Push,
        Reveal,
        Default
    }
    
    
    private OnsPage page;
    public OnsSplitterSide(OnsApplication app) {
        super(app, "ons-splitter-side");
        
    }
    
    
    public static enum Side {
        Left,
        Right;
        
    }
    
    public static enum Mode {
        Collapse,
        Split;
        
        
        
    }
    
    public OnsSplitterSide setPage(OnsPage page) {
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
    
    public OnsSplitterSide open() {
        getElement().call("open()");
        return this;
    }
    
    public OnsSplitterSide close() {
        getElement().call("close()");
        return this;
    }
    
    public OnsSplitterSide toggle() {
        getElement().call("toggle()");
        return this;
    }
    
    public OnsSplitterSide load(OnsPage page, SuccessCallback callback) {
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
    
    public OnsSplitterSide setCollapse(boolean collapse) {
        attr("collapse", collapse);
        return this;
    }
    
    public OnsSplitterSide setCollapse(Orientation orientation) {
        attr("collapse", orientation.name().toLowerCase());
        return this;
    }
    
    public OnsSplitterSide setCollapse(String mediaQuery) {
        attr("collapse", mediaQuery);
        return this;
    }
    
    public OnsSplitterSide setSwipeable(boolean swipeable) {
        attr("swipeable", swipeable);
        return this;
    }
    
    public OnsSplitterSide setSide(Side side) {
        attr("side", side.name().toLowerCase());
        return this;
    }
    
    public OnsSplitterSide setOpenThreshold(double threshold) {
        attr("open-threshold", String.valueOf(threshold));
        return this;
    }
    
    public OnsSplitterSide setWidth(Scalar width) {
        attr("width", width.toString());
        return this;
    }
    
   
    
    public boolean isOpen() {
        return propBool("isOpen");
    }
    
    public OnsSplitterSide addModeChangeListener(ActionListener<DOMEvent> l) {
        return (OnsSplitterSide)on("modechange", l);
    }
    public OnsSplitterSide removeModeChangeListener(ActionListener<DOMEvent> l) {
        return (OnsSplitterSide)off("modechange", l);
    }
    
    public OnsSplitterSide addPreOpenListener(ActionListener<DOMEvent> l) {
        return (OnsSplitterSide)on("preopen", l);
    }
    public OnsSplitterSide removePreOpenListener(ActionListener<DOMEvent> l) {
        return (OnsSplitterSide)off("preopen", l);
    }
    
    public OnsSplitterSide addPostOpenListener(ActionListener<DOMEvent> l) {
        return (OnsSplitterSide)on("postopen", l);
    }
    public OnsSplitterSide removePostOpenListener(ActionListener<DOMEvent> l) {
        return (OnsSplitterSide)off("postopen", l);
    }
    
    public OnsSplitterSide addPreCloseListener(ActionListener<DOMEvent> l) {
        return (OnsSplitterSide)on("preclose", l);
    }
    public OnsSplitterSide removePreCloseListener(ActionListener<DOMEvent> l) {
        return (OnsSplitterSide)off("preclose", l);
    }
    public OnsSplitterSide addPostCloseListener(ActionListener<DOMEvent> l) {
        return (OnsSplitterSide)on("postclose", l);
    }
    public OnsSplitterSide removePostCloseListener(ActionListener<DOMEvent> l) {
        return (OnsSplitterSide)off("postclose", l);
    }
    
    
    public OnsSplitterSide setAnimation(Animation anim) {
        attr("animation", anim.name().toLowerCase());
        return this;
    }
    
   
    
}
