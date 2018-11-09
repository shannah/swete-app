/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xataface.ui;

import com.codename1.components.InfiniteScrollAdapter;
import com.codename1.components.ToastBar;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.xataface.query.XFClient;
import com.xataface.query.XFQuery;
import com.xataface.query.XFRecord;
import com.xataface.query.XFRowSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shannah
 */
public class XFUIRecordList extends Container {
    private final XFRowSet rowset;
    private final XFClient client;
    private final ViewFactory viewFactory;
    
    
    public XFClient getClient() {
        return client;
    }
    
    public static interface ViewFactory {
        
        /**
         * Creates a row of the list.
         * @param list The list within which the row is created.
         * @param record The record that is the model for this row.
         * @return The Component that encapsulates the row.
         */
        public Component createRowView(XFUIRecordList list, XFRecord record);
        
        /**
         * Generates a component that represents the given rowset.
         * @param list
         * @param rowset
         * @return 
         */
        public Container createList(XFUIRecordList list, XFRowSet rowset) ;
        
        /**
         * Creates the details view for a particular record.
         * @param list
         * @param record
         * @return 
         */
        public Component createDetailsView(XFUIRecordList list, XFRecord record);
    }
    
    public XFUIRecordList(XFRowSet rs, XFClient client) {
        this(rs, client, new XFUIViewFactory());
    }
    
    public XFUIRecordList(XFRowSet rs, XFClient client, ViewFactory viewFactory) {
        this.rowset = rs;
        this.client = client;
        this.viewFactory = viewFactory;
        setLayout(new BorderLayout());
        addComponent(BorderLayout.CENTER, viewFactory.createList(this, rowset));
    }
    
    
    
    
    protected void showDetailsForm(XFRecord record) {
        Form curr = Display.getInstance().getCurrent();
        Form f = new Form(record.getTitle());
        Toolbar tb = new Toolbar();
        f.setToolbar(tb);
        
        Component details = viewFactory.createDetailsView(this, record);
        f.setLayout(new BorderLayout());
        f.addComponent(BorderLayout.CENTER, details);
        
        f.setBackCommand(new Command("Back") {

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (curr != null) {
                    curr.showBack();
                }
            }
            
        });
        tb.setBackCommand(f.getBackCommand());
        
        
        f.show();
        
        XFQuery q = new XFQuery(record.getTable())
                .byId(record.getId());
        ToastBar.Status status = ToastBar.getInstance().createStatus();
        status.setMessage("Loading Details");
        status.setShowProgressIndicator(true);
        
        client.find(q, rs -> {
            status.clear();
            f.addComponent(BorderLayout.CENTER, viewFactory.createDetailsView(this, rs.iterator().next()));
            f.revalidate();
        });
    }
}
