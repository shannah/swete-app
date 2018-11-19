/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.htmlform.HTMLElement;
import com.codename1.onsen.OnsApplication;
import com.codename1.onsen.Scalar;
import com.codename1.onsen.TagBuilder;

/**
 *
 * @author shannah
 */
public class OnsNavigator extends AbstractElement {
    
    private OnsNavigatorAnimation animation;
    private Double swipeThreshold;
    private String swipeTargetWidth;
    private Boolean swipeable;
    
    public static enum OnsNavigatorAnimation {
        Fade("fade"),
        Lift("lift"),
        Slide("slide"),
        None("none");
        
        private String name;

        private OnsNavigatorAnimation(String name) {
            this.name = name;
        }
        
        
    }
  
    public OnsNavigator(OnsApplication app) {
        super(app);
    }
    
    public OnsNavigator(OnsApplication app, String id, boolean requiresInstall) {
        super(app, id, requiresInstall);
    }

    @Override
    protected void installImpl() {
        //System.out.println("Installing navigator");
        HTMLElement body = getContext().getElement("cn1-body");
        appendTo(body);
    }
    
    
    
    @Override
    public String createTag() {
        TagBuilder tb = new TagBuilder().tagName("ons-navigator").attr("id", getElement().getId());
        
        if (animation != null) {
            tb.attr("animation", animation.name);
        }
        if (swipeThreshold != null) {
            tb.attr("swipe-threshold", swipeThreshold.toString());
        }
        
        if (swipeTargetWidth != null) {
            tb.attr("swipe-target-width", swipeTargetWidth);
        }
        
        if (swipeable != null) {
            tb.attr("swipeable", swipeable.toString());
        }
        tb.innerHTML(createInnerHTML());
        return tb.toString();
    }
    
   
    public void pushPage(OnsPage page) {
        getApplication().registerPage(page);
        page.installDependencies();
        getElement().call("pushPage(null, {pageHTML: ${0}})", new Object[]{page.createTag()});
    }
    
    public void replacePage(OnsPage page) {
        getApplication().registerPage(page);
        page.installDependencies();
        getElement().call("replacePage(null, {pageHTML: ${0}})", new Object[]{page.createTag()});
    }
    
    public void insertPage(int index, Template<OnsPage> page) {
        if (!page.isInstalled()) {
            getApplication().install(page);
        }
        getElement().call("insertPage(${0}, {pageHTML: ${1}})", new Object[]{index, page.createTag()});
    }
    
    public void popPage() {
        getElement().call("popPage()");
    }
    
    public void removePage(int index) {
        getElement().call("removePage(${0})", new Object[]{index});
        
    }
    
    public void resetToPage(Template<OnsPage> page) {
        if (!page.isInstalled()) {
            getApplication().install(page);
        }
        getElement().call("resetToPage(null, {pageHTML: ${0}})", new Object[]{page.createTag()});
    }
    
    public void bringPageToTop(Template<OnsPage> page) {
        if (!page.isInstalled()) {
            getApplication().install(page);
        }
        getElement().call("bringPageToTop(${0})", new Object[]{page.getElement().getId()});
    }
    

    public OnsNavigator setSwipeTargetWidth(Scalar value) {
        return (OnsNavigator)attr("swipe-target-width", value.toString());
    }

    @Override
    public String getTagName() {
        return "ons-navigator";
    }
    
    
    
}
