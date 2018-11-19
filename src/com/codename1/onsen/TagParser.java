/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen;

import com.codename1.htmlform.HTMLUtil;
import com.codename1.io.Util;
import com.codename1.onsen.components.AbstractElement;
import com.codename1.onsen.components.ElementFactory;
import com.codename1.onsen.components.TextNode;
import com.codename1.processing.Result;
import com.codename1.util.StringUtil;
import com.codename1.xml.XMLParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parses HTML content into components that can be added to an Onsen app.
 * @author shannah
 */
public class TagParser {
    private boolean xml=false;
    private XMLParser parser = new XMLParser();
    private ElementFactory factory;
    private OnsApplication app;
    
    public TagParser(OnsApplication app) {
        this.app = app;
        this.factory = app.getFactory();
    }
    
    /**
     * Parses XHTML content.  Returns root element.
     * @param input
     * @return
     * @throws IOException 
     */
    public AbstractElement parse(String input) throws IOException {
        
        return parse(new ByteArrayInputStream(input.getBytes("UTF-8")));
    }
    
    /**
     * Parses XHTML content.  Returns root element.
     * @param is
     * @return
     * @throws IOException 
     */
    public AbstractElement parse(InputStream is) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(is, "UTF-8")) {
            return parse(reader);
        }
    }
    
    /**
     * Parses XHTML content.  Returns root AbstractElement.
     * @param input
     * @return 
     */
    public AbstractElement parse(Reader input) {
        com.codename1.xml.Element root = parser.parse(input);
        return parse(root);
    }
    
    private AbstractElement parse(com.codename1.xml.Element xmlNode) {
        AbstractElement out = factory.create(xmlNode.getTagName());
        if (!xmlNode.isTextElement() && xmlNode.getAttributes() != null) {
            for (String att : (Set<String>)xmlNode.getAttributes().keySet()) {
                out.attr(att, xmlNode.getAttribute(att));
            }
        }
        int len = xmlNode.getNumChildren();
        StringBuilder innerText = null;
        List<AbstractElement> children = null;
        for (int i=0; i<len; i++) {
            com.codename1.xml.Element child = xmlNode.getChildAt(i);
            if (child.isTextElement()) {
                if (innerText == null) {
                    innerText = new StringBuilder();
                }
                innerText.append(child.getText());
            } else {
                if (children == null) {
                    children = new ArrayList<AbstractElement>();
                    if (innerText != null) {
                        children.add(new TextNode(app, innerText.toString()));
                    }
                }
                children.add(parse(child));
            }
            
        }
        if (children != null) {
            for (AbstractElement child : children) {
                out.add(child);
            }
        } else if (innerText != null) {
            String txt = innerText.toString().trim();
            if (txt.length() > 0) {
                out.setInnerText(txt);
            }
        }
        return out;
    }
    
    /**
     * Parses HTML content.  Returns root element.
     * @param html
     * @return 
     */
    public AbstractElement parseHTML(String html) {
        Result result = HTMLUtil.htmlToJSON(app.getContext(), html);
        Map htmlNode = (Map)result.getAsArray("child").get(0);
        return parseHTML(htmlNode);
        
    }
    
    private String join(List<String> vals) {
        StringBuilder sb = new StringBuilder();
        for (String v : vals) {
            sb.append(v).append(" ");
        }
        return sb.toString().trim();
    }
    
    private AbstractElement parseHTML(Map node) {
        String nodeType = (String)node.get("node");
        if ("element".equals(nodeType)) {
            
        }
        String tagName = (String)node.get("tag");
        Map atts = (Map)node.get("attr");
        
        AbstractElement out = factory.create(tagName);
        if (atts != null) {
            for (String att : (Set<String>)atts.keySet()) {
                Object attVal = atts.get(att);
                String attStr;
                if (attVal instanceof String) {
                    attStr = (String)attVal;
                } else if (attVal instanceof List) {
                    attStr = join((List)attVal);
                } else {
                    attStr = String.valueOf(attVal);
                }
                out.attr(att, attStr);
            }
        }
        
        if ("ons-page".equals(tagName)) {
            node.put("currPageId", out.getElement().getId());
        }
        List childrenSrc = (List)node.get("child");
        StringBuilder innerText = null;
        List<AbstractElement> children = null;
        if (childrenSrc != null) {
            int len = childrenSrc.size();

            for (int i=0; i<len; i++) {
                Map child = (Map)childrenSrc.get(i);
                String childType = (String)child.get("node");
                if (!"element".equals(childType)) {
                    if (innerText == null) {
                        innerText = new StringBuilder();
                    }
                    innerText.append((String)child.get("text"));
                } else {
                    if (children == null) {
                        children = new ArrayList<AbstractElement>();
                        if (innerText != null) {
                            children.add(new TextNode(app, innerText.toString()));
                        }
                    }
                    if (node.containsKey("currPageId")) {
                        child.put("currPageId", node.get("currPageId"));
                    }
                    children.add(parseHTML(child));
                }

            }
        }
        if (children != null) {
            for (AbstractElement child : children) {
                out.add(child);
            }
        } else if (innerText != null) {
            String txt = innerText.toString().trim();
            //if ("script".equals(tagName) && node.containsKey("currPageId") && txt.indexOf("ons.getScriptPage().onInit") >= 0) {
            //    txt = StringUtil.replaceAll(txt, "ons.getScriptPage().onInit", "cn1.onScriptPageProxy('"+node.get("currPageId")+"').onInit");
            //}
            //if ("script".equals(tagName)) {
            //    System.out.println("--------Outputing script: "+txt);
            //}
            if (txt.length() > 0) {
                out.setInnerHTML(txt);
            }
        }
        return out;
    }
    
    /**
     * Parses HTML content.  Returns root element.
     * @param is
     * @return
     * @throws IOException 
     */
    public AbstractElement parseHTML(InputStream is) throws IOException {
        return parseHTML(Util.readToString(is));
    }
}
