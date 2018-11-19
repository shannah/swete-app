/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.onsen.OnsApplication;



/**
 *
 * @author shannah
 */
public class OnsList extends Element {
    
    public static enum Position {
        Left,
        Right,
        Center,
        ExpandableContent
    }
    
    public static class ListTitle extends Element {
        public ListTitle(OnsApplication app) {
            super(app, "ons-list-title");
        }
        
        public ListTitle(OnsApplication app, String title) {
            this(app);
            setInnerText(title);
        }
    }
    
    public static class ListHeader extends Element {
        public ListHeader(OnsApplication app) {
            super(app, "ons-list-header");
        }
        
        public ListHeader(OnsApplication app, String text) {
            this(app);
            setInnerText(text);
            
        }
    }
    
    
    public static class ListItem extends Element {
        private String label;
        private Element left, center, right, expandableContent;
        
        
        
        public ListItem(OnsApplication app) {
            super(app, "ons-list-item");
        }
        
        public ListItem(OnsApplication app, String label) {
            this(app);
            setLabel(label);
        }
        
        public ListItem(OnsApplication app, String title, OnsIcon icon) {
            this(app);
            center = createCenter();
            addTitleClass(center);
            center.setInnerText(title);
            left = createLeft().add(addIconClass(icon));
            
            add(left);
            add(center);
            
        }
        
        public ListItem setLeft(Element content) {
            if (content.hasCSSClass("left")) {
                if (left != null) {
                    remove(left);
                }
                left = content;
                add(left);
                return this;
            }
            if (left == null) {
                left = createLeft();
                add(left);
            }
            left.removeAll();
            left.add(content);
            return this;
        }
        
        public ListItem setCenter(Element content) {
            if (content.hasCSSClass("center")) {
                if (center != null) {
                    remove(center);
                }
                center = content;
                add(center);
                return this;
            }
            if (center == null) {
                center = createCenter();
                add(center);
            }
            center.removeAll();
            center.add(content);
            return this;
        }
        
        public ListItem setRight(Element content) {
            if (content.hasCSSClass("right")) {
                if (right != null) {
                    remove(right);
                }
                right = content;
                add(right);
                return this;
            }
            if (right == null) {
                right = createRight();
                add(right);
            }
            right.removeAll();
            right.add(content);
            return this;
        }
        
        public ListItem setExpandableContent(Element content) {
            if (content.hasCSSClass("expandable-content")) {
                if (expandableContent != null) {
                    remove(expandableContent);
                }
                expandableContent = content;
                add(expandableContent);
                return this;
            }
            if (expandableContent == null) {
                createExpandableContent();
                add(expandableContent);
            }
            expandableContent.removeAll();
            expandableContent.add(content);
            return this;
        }
        
        public ListItem setLeft(String text) {
            return setLeft(getApplication().createElement("span").setInnerText(text));
        }
        
        public ListItem setRight(String text) {
            return setRight(getApplication().createElement("span").setInnerText(text));
        }
        
        public ListItem setCenter(String text) {
            return setCenter(getApplication().createElement("span").setInnerText(text));
        }
        
        public ListItem setExpandableContent(String text) {
            return setExpandableContent(getApplication().createElement("span").setInnerText(text));
        }
        
        public ListItem setIcon(OnsIcon icon, Position position) {
            addIconClass(icon);
            switch (position) {
                case Left:
                    setLeft(icon);
                    break;
                case Right:
                    setRight(icon);
                    break;
                case Center:
                    setCenter(icon);
                    break;
                case ExpandableContent:
                    setExpandableContent(icon);
                    break;
                    
            }
            return this;
            
        }
        
        public ListItem setFixedWidth(boolean fixedWidth) {
            return (ListItem)attr("fixed-width", fixedWidth);
        }
        
        
        public ListItem(OnsApplication app, String title, Img thumbnail) {
            this(app);
            center = createCenter();
            addTitleClass(center);
            center.setInnerText(title);
            left = createLeft().add(addThumbnailClass(thumbnail));
            
            add(left);
            add(center);
        }
        
        private Element createCenter() {
            center = getApplication().createElement("div").addCSSClass("center");
            return center;
        }
        
        private Element createLeft() {
            left = getApplication().createElement("div").addCSSClass("left");
            return left;
        }
        
        private Element createRight() {
            right = getApplication().createElement("div").addCSSClass("right");
            return right;
        }
        
        private Element createExpandableContent() {
            expandableContent = getApplication().createElement("div").addCSSClass("expandable-content");
            return expandableContent;
        }
        
        public ListItem setLabel(String label) {
            setInnerText(label);
            return this;
        }
        
    }
    
    public OnsList(OnsApplication app) {
        super(app, "ons-list");
    }
    
    public void add(String itemText) {
        ListItem item = new ListItem(getApplication());
        item.setLabel(itemText);
        add(item);
    }
    
    public void addAll(String... itemText) {
        for (String i : itemText) {
            add(i);
        }
    }
    
    public void addAll(ListItem... items) {
        for (ListItem i : items) {
            add(i);
        }
    }
    
    
    public static AbstractElement addIconClass(AbstractElement cmp) {
        cmp.addCSSClass("list-item__icon");
        return cmp;
    }
    
    public static AbstractElement addSubtitleClass(AbstractElement cmp) {
        cmp.addCSSClass("list-item__subtitle");
        return cmp;
        
    }
    
    public static AbstractElement addTitleClass(AbstractElement cmp) {
        cmp.addCSSClass("list-item__title");
        return cmp;
    }
    
    public static AbstractElement addThumbnailClass(AbstractElement cmp) {
        cmp.addCSSClass("list-item__thumbnail");
        return cmp;
    }
    
    
    public Element createTitle(String text) {
       return (Element)addTitleClass(getApplication().ons(Element.class, "div").builder().innerHTML(text).get());
    }
    
    public Element createSubtitle(String text) {
        return (Element)addSubtitleClass(getApplication().ons(Element.class, "div").builder().innerHTML(text).get());
    }
    
    public Element createThumbnail(String src) {
        return (Element)addThumbnailClass(getApplication().ons(Element.class, "img").builder().attr("src", src).get());
    }
    
    public OnsIcon createIcon(String icon) {
        OnsIcon icn = new OnsIcon(getApplication());
        icn.setIcon(icon);
        return (OnsIcon)addIconClass(icn);
    }
    
    
    
    
}
