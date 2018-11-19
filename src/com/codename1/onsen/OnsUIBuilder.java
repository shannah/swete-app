/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen;

import com.codename1.htmlform.HTMLContext.DOMEvent;
import com.codename1.onsen.components.AbstractElement;
import com.codename1.onsen.components.Element;
import com.codename1.onsen.components.Img;
import com.codename1.onsen.components.OnsBackButton;
import com.codename1.onsen.components.OnsButton;
import com.codename1.onsen.components.OnsCard;
import com.codename1.onsen.components.OnsCheckBox;
import com.codename1.onsen.components.OnsFab;
import com.codename1.onsen.components.OnsIcon;
import com.codename1.onsen.components.OnsInput;
import com.codename1.onsen.components.OnsList;
import com.codename1.onsen.components.OnsNavigator;
import com.codename1.onsen.components.OnsPage;
import com.codename1.onsen.components.OnsProgressBar;
import com.codename1.onsen.components.OnsRadio;
import com.codename1.onsen.components.OnsRange;
import com.codename1.onsen.components.OnsSearchInput;
import com.codename1.onsen.components.OnsSelect;
import com.codename1.onsen.components.OnsSplitter;
import com.codename1.onsen.components.OnsSplitterContent;
import com.codename1.onsen.components.OnsSplitterSide;
import com.codename1.onsen.components.OnsSwitch;
import com.codename1.onsen.components.OnsTab;
import com.codename1.onsen.components.OnsTabBar;
import com.codename1.onsen.components.OnsToolbar;
import com.codename1.onsen.components.OnsToolbarButton;
import com.codename1.onsen.components.Script;
import com.codename1.onsen.components.Template;
import com.codename1.onsen.components.TextNode;
import com.codename1.onsen.templates.page.snippets.FormBuilder;
import com.codename1.onsen.templates.page.snippets.MenuBuilder;
import com.codename1.ui.CN;
import com.codename1.ui.events.ActionListener;
import java.io.IOException;

/**
 *
 * @author shannah
 */
public abstract class OnsUIBuilder implements Runnable{
    private OnsApplication app;
    
    public OnsUIBuilder() {
        
    }
    
    public void init() {
        
    }
    
    public void setApplication(OnsApplication app) {
        this.app = app;
    }
    
    public OnsApplication getApplication() {
        return app;
    }
    
    public OnsUIBuilder(OnsApplication app) {
        this.app = app;
    }
    
    public Element createElement(String tagName) {
        return app.createElement(tagName);
    }
    
    public AbstractElement createComponent(String html) {
        return app.createComponent(html);
    }
    
    public <T extends AbstractElement> T createComponent(Class<T> cls, String tagName) {
        return app.createComponent(cls, tagName);
    }
    
    public Img img() {
        return (Img)createElement("img");
    }
    
    public OnsBackButton onsBackButton() {
        return (OnsBackButton)createElement("ons-back-button");
    }
    
    public OnsButton onsButton() {
        return (OnsButton)createElement("ons-button");
    }
    
    public OnsButton onsButton(String label) {
        return (OnsButton)onsButton().text(label);
    }
    
    public OnsButton onsButton(String label, ActionListener<DOMEvent> l) {
        return (OnsButton)onsButton().text(label).on("click", l);
    }
    
    public OnsCard onsCard() {
        return (OnsCard)createElement("ons-card");
    }
    
    public OnsProgressBar onsProgressBar() {
        return (OnsProgressBar)createElement("ons-progress-bar");
    }
    
    public OnsCheckBox onsCheckBox() {
        return (OnsCheckBox)createElement("ons-checkbox");
    }
    
    public OnsFab onsFab() {
        return (OnsFab)createElement("ons-fab");
    }
    
    public OnsIcon onsIcon() {
        return (OnsIcon)createElement("ons-icon");
    }
    
    public OnsIcon onsIcon(String icon) {
        OnsIcon out = onsIcon();
        out.setIcon(icon);
        return out;
    }
    
    public OnsInput onsInput() {
        return (OnsInput)createElement("ons-input");
    }
    
    public OnsList onsList() {
        return (OnsList)createElement("ons-list");
    }
    
    public OnsList.ListItem onsListItem() {
        return (OnsList.ListItem)createElement("ons-list-item");
    }
    
    public OnsList.ListTitle onsListTitle() {
        return (OnsList.ListTitle)createElement("ons-list-title");
    }

    public OnsList.ListTitle onsListTitle(String title) {
        return (OnsList.ListTitle)onsListTitle().setInnerText(title);
    }
    
    
    public OnsNavigator onsNavigator() {
        return (OnsNavigator)createComponent("ons-navigator");
    }
    
    public OnsPage onsPage() {
        return (OnsPage)createElement("ons-page");
    }
    
    public OnsRadio onsRadio() {
        return (OnsRadio)createElement("ons-radio");
    }
    
    public OnsRange onsRange() {
        return (OnsRange)createElement("ons-range");
    }
    
    public OnsSearchInput onsSearchInput() {
        return (OnsSearchInput) createElement("ons-search-input");
    }
    
    public OnsSelect onsSelect() {
        return (OnsSelect) createElement("ons-select");
    }
    
    public OnsSplitter onsSplitter() {
        return (OnsSplitter) createElement("ons-splitter");
    }
    
    public OnsSplitterContent onsSplitterContent() {
        return (OnsSplitterContent) createElement("ons-splitter-content");
    }
    
    public OnsSplitterSide onsSplitterSide() {
        return (OnsSplitterSide)createElement("ons-splitter-side");
    }
    
    public OnsSwitch onsSwitch() {
        return (OnsSwitch)createElement("ons-switch");
    }
    
    public OnsTab onsTab() {
        return (OnsTab) createElement("ons-tab");
    }
    
    public OnsTab onsTab(String label) {
        OnsTab out = onsTab();
        out.setLabel(label);
        return out;
    }
    
    public OnsTab onsTab(String label, OnsIcon icon) {
        OnsTab out = onsTab(label);
        out.setIcon(icon);
        return out;
    }
    
    public OnsTab onsTab(String label, OnsIcon icon, OnsPage page) {
        OnsTab out = onsTab(label, icon);
        out.setPage(page);
        return out;
    }
    
    public OnsTabBar onsTabBar() {
        return (OnsTabBar) createElement("ons-tabbar");
    }
    
    public OnsToolbar onsToolbar() {
        return (OnsToolbar) createElement("ons-toolbar");
    }
    
    public OnsToolbarButton onsToolbarButton() {
        return (OnsToolbarButton) createElement("ons-toolbar-button");
    }
    
    public Script script() {
        return (Script)createElement("script");
    }
    
    public Script script(String content) {
        Script script = script();
        script.setInnerHTML(content);
        return script;
    }
    
    public Script firebug() {
        Script script = script();
        script.attr("src", "http://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js");
        return script;
    }
    
    public Template template() {
        return (Template)createElement("template");
    }
    
    public TextNode textNode(String text) {
        return new TextNode(app, text);
    }
    
    public OnsUIBuilder prepare(OnsUIBuilder... builders) {
        System.out.println("In init(...builders)");
        for (OnsUIBuilder b : builders) {
            System.out.println("About to call init on "+b);
            b.setApplication(getApplication());
            b.init();
        }
        return this;
    }
    
    
    public FormBuilder formBuilder() {
        FormBuilder blder = new FormBuilder();
        prepare(blder);
        return blder;
                
    }
    
    public MenuBuilder menuBuilder() {
        MenuBuilder blder = new MenuBuilder();
        prepare(blder);
        return blder;
    }
    
    public OnsUIBuilder toast(String message) {
        OnsNotification.toast(app, message, null);
        return this;
    }
    
    public <T extends AbstractElement> T load(Class<T> type, String path) throws IOException {
        TagParser parser = new TagParser(getApplication());
        return (T)parser.parseHTML(CN.getResourceAsStream(path));
        
    }
}
