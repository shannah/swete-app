/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.htmlform.HTMLElement;
import com.codename1.onsen.OnsApplication;
import com.codename1.onsen.components.OnsSplitterSide.Side;

/**
 *
 * @author shannah
 */
public class OnsSplitter extends Element {
    private OnsSplitterContent content;

    public OnsSplitter(OnsApplication app) {
        super(app, "ons-splitter");
        content = new OnsSplitterContent(getApplication());
        add(content);
    }
    
    public OnsSplitterContent getContent() {
        return content;
    }
    
    @Override
    public OnsSplitter add(AbstractElement el) {
        if (el instanceof OnsSplitterContent) {
            if (content != null && content != el) {
                remove(content);
            }
        }
        
        super.add(el);
        return this;
    }
    
    
    public OnsSplitter setContent(OnsPage page) {
        if (content == null) {
            content = new OnsSplitterContent(getApplication());
            content.setPage(page);
        } else {
            content.setPage(page);
        }
        return this;
    }
    
    public OnsSplitterSide getSide() {
        return getLeft() != null ? getLeft() : getRight();
    }
    
    public OnsSplitter setLeft(OnsPage page) {
        OnsSplitterSide left = getLeft();
        if (left == null) {
            left = new OnsSplitterSide(getApplication()).setSide(Side.Left).setPage(page);
            add(left);
        } else {
            left.setPage(page);
        }
        return this;
    }
    
    public OnsSplitterSide getLeft() {
        for (AbstractElement el : this) {
            if (el instanceof OnsSplitterSide) {
                if (!"right".equals(el.getAttribute("side"))) {
                    return (OnsSplitterSide)el;
                }
            }
        }
        return null;
    }
    
    public OnsSplitterSide getRight() {
        for (AbstractElement el : this) {
            if (el instanceof OnsSplitterSide) {
                if ("right".equals(el.getAttribute("side"))) {
                    return (OnsSplitterSide)el;
                }
            }
        }
        return null;
    }
    
    public OnsSplitter setRight(OnsPage page) {
        OnsSplitterSide right = getRight();
        if (right == null) {
            right = new OnsSplitterSide(getApplication()).setSide(Side.Right).setPage(page);
            add(right);
        } else {
            right.setPage(page);
        }
        return this;
    }
    
    public OnsSplitter setRight(OnsSplitterSide s) {
        s.setSide(Side.Right);
        OnsSplitterSide curr = getRight();
        if (curr != null) {
            remove(curr);
        }
        add(s);
        return this;
    }
    
    public OnsSplitter setLeft(OnsSplitterSide s) {
        s.setSide(Side.Left);
        OnsSplitterSide curr = getLeft();
        if (curr != null) {
            remove(curr);
        }
        add(s);
        return this;
    }

    @Override
    protected void installImpl() {
        HTMLElement body = getContext().getElement("cn1-body");
        appendTo(body);
    }
    
    
    
    
}
