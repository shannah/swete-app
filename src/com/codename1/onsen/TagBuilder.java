/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen;

import com.codename1.util.StringUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author shannah
 */
public class TagBuilder {
    private String tagName;
    private Map<String,String> attributes = new HashMap<>();
    private String innerHTML;
    
    private static final String[][] escapes = {
        {"&", "&amp;"},
        {"\"", "&quot;"},
        {"<", "&lt;"},
        {">", "&gt;"},
        {"'", "&apos;"}
    };
    
    public TagBuilder tagName(String name) {
        tagName = name;
        return this;
    }
    
    public TagBuilder attr(String key, String value) {
        attributes.put(key, value);
        return this;
    }
    
    public TagBuilder attr(String key, boolean value) {
        if (value) return attr(key, key);
        else attributes.remove(key);
        return this;
    }
    
    public TagBuilder attrs(String... keysAndValues) {
        int len = keysAndValues.length;
        for (int i=0; i<len; i+=2) {
            attr(keysAndValues[i], keysAndValues[i+1]);
        }
        return this;
    }
    
    public TagBuilder innerHTML(String innerHTML) {
        this.innerHTML = innerHTML;
        return this;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(tagName);
        
        for (Map.Entry<String,String> e : attributes.entrySet()) {
            sb.append(" ");
            sb.append(e.getKey()).append("=\"").append(encode(e.getValue())).append("\"");
        }
        if (innerHTML != null) {
            sb.append(">").append(innerHTML).append("</").append(tagName).append(">");
        } else {
            sb.append("/>");
        }
        return sb.toString();
    }
    public static String encode(String text) {

        int elen = escapes.length;
        for (int i = 0; i < elen; i++) {
            text = StringUtil.replaceAll(text, escapes[i][0], escapes[i][1]);
        }

        return text;
    }
}
