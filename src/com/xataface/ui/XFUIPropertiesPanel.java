/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xataface.ui;

import com.codename1.components.ToastBar;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.TextField;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.EventDispatcher;
import com.xataface.query.XFClient;
import com.xataface.query.XFRecord;

/**
 *
 * @author shannah
 */
public class XFUIPropertiesPanel extends Container {
    private final XFRecord record;
    private final XFClient client;
    private final ViewFactory viewFactory;
    private XFUIEditorKit editorKit = new XFUIEditorKit();
    
    
    
    
    public XFClient getClient() {
        return client;
    }
    
    
    
    
    public static interface ViewFactory {
        public Component createWidget(XFUIPropertiesPanel propertiesPanel, XFRecord record, String column);
        
    }
    
    public XFUIPropertiesPanel(XFRecord record, XFClient client) {
        this(record, client, new XFUIViewFactory());
    }
    
    public XFUIPropertiesPanel(XFRecord record, XFClient client, ViewFactory viewFactory) {
        this.record = record;
        this.client = client;
        this.viewFactory = viewFactory;
        this.setLayout(new BorderLayout());
        this.addComponent(BorderLayout.CENTER, createDetailsTable(record, client));
    }
    
    
    
    private Container createDetailsTable(XFRecord record, XFClient client) {
        
        Container wrapper = new Container(new BorderLayout());
        
        TableLayout l = new TableLayout(record.getColumnNames().size(), 2);
        Container details = new Container(l);
        
        int row=0;
        for (String col : record.getColumnNames()) {
            details.add(l.createConstraint(row, 0), col);
            Component widget = viewFactory.createWidget(this, record, col);
            details.add(l.createConstraint(row, 1), widget);
            
            row++;
        }
        Command saveCmd = editorKit.getSaveCommand(client, record, res->{
            if (res != null) {
                wrapper.getParent().replace(wrapper, createDetailsTable(res, client), CommonTransitions.createFade(300));
            }
        });
        Button btnSave = new Button(saveCmd);
        
        Command deleteCmd = editorKit.getDeleteCommand(client, record, res->{
            if (res) {
                Command back = Display.getInstance().getCurrent().getBackCommand();
                if (back != null) {
                    back.actionPerformed(new ActionEvent(this));
                }
                
            }
        });
        
        Button btnDelete = new Button(deleteCmd);
        
        
        Container north = new Container(new BorderLayout());
        north.add(BorderLayout.EAST, btnSave);
        north.add(BorderLayout.WEST, btnDelete);
        wrapper.add(BorderLayout.NORTH, north);
        
        details.setScrollableY(true);
        wrapper.addComponent(BorderLayout.CENTER, details);
        return wrapper;
    }
    
    
}
