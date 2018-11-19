/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.htmlform.HTMLElement;
import com.codename1.htmlform.Selector;
import com.codename1.onsen.OnsApplication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author shannah
 */
public class OnsSelect extends Element {
    private HTMLElement selectElement;
    private List<Option> options = new ArrayList<>();
    
    public static class Option extends Element {
        public Option(OnsApplication app) {
            super(app, "option");
        }
        
        public Option(OnsApplication app, String value) {
            super(app, "option");
            setValue(value);
        }
        
        public Option(OnsApplication app, String value, String label) {
            super(app, "option");
            setValue(value, label);
        }
        
        public void setValue(String value) {
            setProperty("value", value);
            setLabel(value);
        }
        
        public String getValue() {
            return getStringProperty("value");
        }
        
        public void setLabel(String label) {
            setInnerHTML(label);
            
        }
        
        public String getLabel() {
            return getStringProperty("innerHTML");
        }
        
        public void setValue(String value, String label) {
            setValue(value);
            setLabel(label);
        }
        
        
    }
    
    
    public OnsSelect(OnsApplication app) {
        super(app, "ons-select");
        selectElement = getContext().getElement(new Selector("select", getElement()));
    }

    @Override
    public HTMLElement getContentElement() {
        return selectElement;
    }
    
    
    
    public boolean isDisabled() {
        return hasAttribute("disabled");
    }
    
    public void setDisabled(boolean dis) {
        setAttribute("disabled", String.valueOf(dis));
    }
    
    public void setMultiple(boolean checked) {
        if (checked) {
            setAttribute("multiple", String.valueOf(checked));
        } else {
            removeAttribute("multiple");
        }
    }
    
    public boolean isMultiple() {
        return hasAttribute("multiple");
    }
    
    public void setName(String name) {
        setAttribute("name", name);
    }
    
    public void setAutofocus(boolean autofocus) {
        if (autofocus) {
            setAttribute("autofocus", "true");
        } else {
            removeAttribute("autofocus");
        }
    }
    
    public boolean isAutofocus() {
        return hasAttribute("autofocus");
    }
    
    public void setRequired(boolean req) {
        if (req) {
            setAttribute("required", "true");
        } else {
            removeAttribute("required");
        }
    }
    
    public boolean isRequired() {
        return hasAttribute("required");
    }
    
    public void setSelectId(String selectId) {
        setAttribute("select-id", selectId);
    }
    
    public String getSelectId() {
        return getAttribute("select-id");
    }
    
    
    public void setSize(int size) {
        setAttribute("size", String.valueOf(size));
    }
    
    public int getSize() {
        try {
            return Integer.parseInt(getAttribute("size"));
        } catch (Throwable t) {
            return 1;
        }
    }
    
    
    public int getLength() {
        return getIntProperty("length");
    }
    
    
    public void setLength(int len) {
        setProperty("length", len);
        while (len < options.size()) {
            options.remove(options.size()-1);
        }
    }
    
    public String getValue() {
        return getStringProperty("value");
    }
    
    public void setValue(String val) {
        setProperty("value", val);
    }
    
    public void addOption(Option opt) {
        options.add(opt);
        add(opt);
    }
    
    public void removeOption(Option opt) {
        options.remove(opt);
        remove(opt);
    }
    
    
    public void addOptions(Option... opts) {
        for (Option opt : opts) {
            addOption(opt);
        }
    }
    
    public Option getOption(int index) {
        return options.get(index);
    }
    
    public void addOptions(String... values) {
        for (String value : values) {
            addOption(new Option(getApplication(), value));
        }
    }
    
    public void addOptionsWithLabels(String... valuesAndLabels) {
        int len = valuesAndLabels.length;
        for (int i=0; i<= len; i+=2) {
            addOption(new Option(getApplication(), valuesAndLabels[i], valuesAndLabels[i+1]));
        }
    }
    
    
    private Set<Modifier> modifiers;
    
    public static enum Modifier {
        Material("material"),
        Underbar("underbar");
        
        String name;
        
        Modifier(String name) {
            this.name =name;
        }
    }
    
     public Set<Modifier> getModifiers() {
        Set<Modifier> out = new HashSet<Modifier>();
        if (modifiers != null) {
            out.addAll(modifiers);
        }
        return out;
    }
    
    public void setModifiers(Modifier... mods) {
        if (modifiers == null) {
            modifiers = new HashSet<>();
        }
        modifiers.clear();
        addModifiers(mods);
    }
    
    public void addModifiers(Modifier... mods) {
        if (modifiers == null) {
            modifiers = new HashSet<Modifier>();
        }
        modifiers.addAll(Arrays.asList(mods));
        setAttribute("modifier", createModifiersString());
        
    }
    
    public void removeModifiers(Modifier... mods) {
        if (modifiers != null) {
            modifiers.removeAll(Arrays.asList(mods));
        }
        setAttribute("modifier", createModifiersString());
    }
    
     private String createModifiersString() {
        StringBuilder sb = new StringBuilder();
        boolean first=true;
        for (Modifier p : modifiers) {
            if (first) {
                first = false;
            } else {
                sb.append(" ");
            }
            sb.append(p.name);
                
        }
        return sb.toString();
    }
    
}
