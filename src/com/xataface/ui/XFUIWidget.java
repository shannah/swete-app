/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xataface.ui;

import com.codename1.capture.Capture;
import com.codename1.components.SpanLabel;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.l10n.ParseException;
import com.codename1.ui.Button;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.URLImage;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.spinner.Picker;
import com.codename1.xml.Element;
import com.xataface.query.XFRecord;
import com.xataface.ui.XFUIForm.ValidationError;
import com.xataface.ui.XFUIForm.Validator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author shannah
 */
public class XFUIWidget extends Container {
    private Label label;
    private final Component input;
    private SpanLabel descriptionLabel;
    
    public XFUIWidget(String label, String description, Component input) {
        super(BoxLayout.y());
        this.input = input;
        this.label = new Label(label);
        this.descriptionLabel = new SpanLabel(description);

        
        add(this.label)
                .add(this.input)

                .add(this.descriptionLabel);

        
    }
    
    
    private static String getText(Element el) {
        StringBuilder sb = new StringBuilder();
        List<Element> children = (List<Element>)el.getTextDescendants(null, true);
        if (children != null) {
            for(Element child : children) {
                sb.append(child.getText());
            }
        }
        return sb.toString();
    }
    
    public static class Builder {
        public XFUIWidget build(XFUIForm form, Element el, XFRecord record) {
            String description="";
            String label="";
            String defaultValue=null;
            String fieldType = el.getAttribute("type");
            Element widget = null;
            Element vocab = null;
            List<Element> validators = null;
            for (Element child : el) {
                switch (child.getTagName()) {
                    case "widget" :
                        widget = child;
                        break;
                    case "description" :
                        description = getText(child);
                        break;
                    case "label" :
                        label = getText(child);
                        break;
                    case "value" :
                        defaultValue = getText(child);
                        break;
                    case "validator" :
                        if (validators == null) {
                            validators = new ArrayList<Element>();
                        }
                        validators.add(child);
                        break;
                        
                        
                }
            }
            if (widget == null) {
                widget = new Element("widget");
                widget.setAttribute("type", "text");
            }
            
            vocab = widget.getFirstChildByTagName("vocabulary");
            
            String type = widget.getAttribute("type");
            String column = el.getAttribute("name");
            if (defaultValue != null && !record.isset(column)) {
                switch (fieldType) {
                    case "date":
                    case "datetime":
                    case "timestamp":
                        try {
                            record.set(column, defaultValue, record.getServerDateFormat());
                        } catch (ParseException ex) {
                            Log.e(ex);
                        }
                        break;
                    default:
                        record.set(column, defaultValue);
                        break;
                }
            }
            
            Component c = null;
            switch (type) {
                case "static":
                {
                    c = new Label(record.getString(column));
                    break;
                }
                case "checkbox": {
                    if (vocab != null) {
                        Container cnt = new Container(new GridLayout(2));
                        c = cnt;
                        
                        for (Element value : vocab) {
                            if ("".equals(value.getAttribute("key"))) {
                                continue;
                            }
                            CheckBox cb = new CheckBox(getText(value));
                            
                            cb.addActionListener(e->{
                                if (cb.isSelected()) {
                                    record.add(column, value.getAttribute("key"));
                                } else {
                                    record.remove(column, value.getAttribute("key"));
                                }
                            });
                            cnt.add(cb);
                        }
                        
                    }
                    
                    break;
                }
                    
                case "text" :
                case "password": {
                    
                    TextField tf = new TextField(record.getString(column));
                    if ("password".equals(type)) {
                        tf.setConstraint(TextField.PASSWORD);
                    }
                    c = tf;
                    tf.addActionListener(e->{
                        record.set(column, tf.getText());
                        if (form.hasValidationErrors()) {
                            form.validate(false);
                        }
                    });
                    break;
                }
                case "textarea": {
                    TextArea ta = new TextArea(record.getString(column));
                    c = ta;
                    ta.addActionListener(e->{
                        record.set(column, ta.getText());
                        if (form.hasValidationErrors()) {
                            form.validate(false);
                        }
                    });
                    break;
                }
                case "calendar": {
                    Picker picker = new Picker();
                    c = picker;
                    int pickerType;
                    if ("true".equals(widget.getAttribute("date")) && "true".equals(widget.getAttribute("time"))) {
                        pickerType = Display.PICKER_TYPE_DATE_AND_TIME;
                    } else if ("true".equals(widget.getAttribute("date"))) {
                        pickerType = Display.PICKER_TYPE_DATE;
                    } else {
                        pickerType = Display.PICKER_TYPE_TIME;
                    }
                    picker.setType(pickerType);
                    Date value = record.getDate(column);
                    if (value == null) {
                        value = new Date();
                    }
                    picker.setDate(value);
                    break;
                }
                
                case "file" : {
                    Button btn = new Button();
                    int prefW = Display.getInstance().convertToPixels(30);
                    btn.addActionListener(e-> {
                        String photo = Capture.capturePhoto();
                        if (photo != null) {
                            record.setFile(column, photo, "image/png");
                            FileSystemStorage fs = FileSystemStorage.getInstance();
                            try {
                                Image img = Image.createImage(fs.openInputStream(photo));
                                img = img.scaledHeight(prefW);
                                btn.setIcon(img);
                            } catch (IOException ex) {
                                Log.e(ex);
                            }
                        }
                    });
                    Image placeholder = FontImage.createMaterial(FontImage.MATERIAL_CAMERA, btn.getStyle(), 30)
                                .fill(prefW, prefW);
                    if (record.isset(column)) {
                        String url = record.getString(column);
                        String imName = url + "?"+prefW+"x"+prefW;
                        
                        btn.setIcon(URLImage.createCachedImage(imName, url, placeholder, URLImage.FLAG_RESIZE_SCALE));
                    } else {
                        btn.setIcon(placeholder);
                    }
                    c = btn;
                    break;
                }
                
                
                
                case "select": {
                    Picker picker = new Picker();
                    c = picker;
                    picker.setType(Display.PICKER_TYPE_STRINGS);
                    
                    final List<Element> values = (List<Element>)vocab.getChildrenByTagName("value");
                    String[] strings = new String[values.size()];
                    int i = 0;
                    for (Element e : values) {
                        strings[i++] = getText(e);
                    }
                    picker.setStrings(strings);
                    picker.addActionListener(e->{
                        String selected = picker.getSelectedString();
                        if (selected != null) {
                            for (Element v : values) {
                                if (selected.equals(getText(v))) {
                                    String key = v.getAttribute("key");
                                    record.set(column, key);
                                    
                                }
                            }
                        } else {
                            record.set(column, (String)null);
                        }
                    });
                    
                    String selVal = record.getString(column);
                    String selString = null;
                    if (selVal != null) {
                        for (Element v : values) {
                            if (selVal.equals(v.getAttribute("key"))) {
                                selString = getText(v);
                                break;
                            }
                        }
                        picker.setSelectedString(selString);
                    }
                    
                    
                        
                    break;
                            
                }
                default :
                    throw new IllegalArgumentException("Widget type "+type+" not supported");
                    
                    
            }
            
            if (validators != null) {
                final Component cf = c;
                for (final Element validator : validators) {
                    String vtype = validator.getAttribute("type");
                    switch (vtype) {
                        case "required" : {
                            form.addInternalValidator(new Validator() {
                                @Override
                                public boolean validate(XFUIForm.ValidationEvent evt) {
                                    String val = record.getString(column);
                                    if (val == null || val.trim().length() == 0) {
                                        evt.addError(new ValidationError(cf, validator.getAttribute("message")));
                                       
                                        return false;
                                    }
                                    return true;
                                }

                            });
                            break;
                        }

                        default: {
                            Log.p("Validator type "+vtype+" not supported");
                        }
                    }
                }
            }
            
            return new XFUIWidget(label, description, c);
        }
    }
}
