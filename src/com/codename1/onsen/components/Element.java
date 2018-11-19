/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.onsen.OnsApplication;
import com.codename1.onsen.TagBuilder;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class Element extends AbstractElement  {
    private String componentName;
    private String innerHTML;
    private String innerText;
    private Builder builder;

    
    //public Element() {
    //    this("div");
    //}
    
    public Element(OnsApplication app) {
        this(app, "div");
    }

    @Override
    public String getTagName() {
        return componentName;
    }
    
    
    
    //public Element(String componentName) {
    //    this(OnsApplication.getInstance(), componentName);
    //}
    
    public Element(OnsApplication app, String componentName) {
        super(app);
        this.componentName = componentName;
    }
    
    private String getCSSClassString() {
        StringBuilder sb = new StringBuilder();
        if (hasAttribute("class")) {
            sb.append(getAttribute("class")).append(" ");
        }
        for (String cssClass : getCSSClasses()) {
            sb.append(cssClass).append(" ");
        }
        return sb.toString().trim();
    }
    
    @Override
    public String createTag() {
        TagBuilder tb = new TagBuilder().tagName(componentName).innerHTML(createInnerHTML());
        
        for (Map.Entry<String,String> e : getAttributesInternal().entrySet()) {
            if ("class".equals(e.getKey())) {
                //tb.attr("class", getCSSClassString());
            } else {
                tb.attr(e.getKey(), e.getValue());
            }
        }
        String cssClass = getCSSClassString();
        if (!cssClass.isEmpty()) {
            tb.attr("class", cssClass);
        }
        tb.attr("id", getElement().getId());
        return tb.toString();
        
    }

    @Override
    public String createInnerHTML() {
        if (innerHTML != null) {
            return innerHTML;
        }
        if (innerText != null) {
            return TagBuilder.encode(innerText);
        }
        return super.createInnerHTML(); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Element setInnerHTML(String innerHTML) {
        this.innerHTML = innerHTML;
        innerText = null;
        if (isInstalled()) {
            getElement().setProperty("innerHTML", innerHTML);
        }
        return this;
    }
    
    @Override
    public Element setInnerText(String innerText) {
        this.innerText = innerText;
        innerHTML = null;
        if (isInstalled()) {
            getElement().setProperty("innerText", innerText);
        }
        return this;
    }
    
   
    
    /**
     * Alias of {@link #setInnerHTML(java.lang.String) }
     * @param innerHTML
     * @return 
     */
    public Element html(String innerHTML) {
        return setInnerHTML(innerHTML);
    }
    
    public Element text(String innerText) {
        return setInnerText(innerText);
    }
    
    
    public Element setComponentName(String componentName) {
        if (isInstalled()) {
            throw new IllegalStateException("Cannot change component name after it has been installed in the dom.");
        }
        this.componentName = componentName;
        return this;
    }
    
    @Override
    public Element addCSSClass(String... cssClasses) {
        super.addCSSClass(cssClasses);
        return this;
    }
    
    @Override
    public Element removeCSSClass(String... cssClasses) {
        return (Element)super.removeCSSClass(cssClasses);
    }

    @Override
    public Element add(AbstractElement child) {
        return (Element)super.add(child); 
    }

    @Override
    public Element prop(String key, String value) {
        return (Element)super.prop(key, value); 
    }

    @Override
    public Element attr(String key, String value) {
        return (Element)super.attr(key, value); 
    }
    
    
    
    
    
    public String getComponentName() {
        return componentName;
    }
    
    public Builder builder() {
        if (builder == null) {
            builder = new Builder();
        }
        return builder;
    }
    
    public class Builder {
        public Builder innerHTML(String innerHTML) {
            setInnerHTML(innerHTML);
            return this;
        }
        
        public Builder attr(String key, String value) {
            setAttribute(key, value);
            return this;
        }
        
        public Element get() {
            return Element.this;
        }
    }
}
