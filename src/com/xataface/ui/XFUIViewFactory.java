/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xataface.ui;

import com.codename1.components.InfiniteScrollAdapter;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.TextField;
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
public class XFUIViewFactory implements XFUIRecordList.ViewFactory, XFUIPropertiesPanel.ViewFactory {

    public Component createRowView(XFUIRecordList list, XFRecord record) {
        Button b = new Button(record.getTitle());
        b.addActionListener(e -> {
            list.showDetailsForm(record);
        });
        return b;
    }

    public Container createList(XFUIRecordList list, XFRowSet rowset) {
        XFClient client = list.getClient();
        Container wrapper = new Container(new BorderLayout());
        Container root = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        Container stats = new Container(new BorderLayout());
        stats.add(BorderLayout.CENTER, "Showing " + rowset.getFirst() + " to " + rowset.getLast() + " of " + rowset.getFound());
        wrapper.add(BorderLayout.NORTH, stats);
        root.setScrollableY(true);
        for (XFRecord row : rowset) {
            root.addComponent(createRowView(list, row));
        }
        wrapper.add(BorderLayout.CENTER, root);
        XFRowSet[] lastSet = new XFRowSet[]{rowset};

        InfiniteScrollAdapter infiniteScroll = InfiniteScrollAdapter.createInfiniteScroll(root, () -> {
            if (lastSet[0].getFound() > lastSet[0].getSkip() + lastSet[0].getLimit()) {
                XFQuery nextQuery = lastSet[0].getQuery().getNextQuery();
                System.out.println("About to query with limit " + nextQuery.getLimit() + " and skip " + nextQuery.getSkip());
                client.find(nextQuery, newResult -> {
                    if (newResult == null) {
                        return;
                    }
                    stats.add(BorderLayout.CENTER, "Showing " + rowset.getFirst() + " to " + newResult.getLast() + " of " + newResult.getFound());
                    List<Component> toAdd = new ArrayList<Component>();
                    for (XFRecord row : newResult) {
                        toAdd.add(createRowView(list, row));
                    }
                    InfiniteScrollAdapter.addMoreComponents(root, toAdd.toArray(new Component[toAdd.size()]), newResult.getLast() < newResult.getFound());
                    lastSet[0] = newResult;
                    wrapper.revalidate();
                });
            }
        });

        return wrapper;
    }

    public Component createDetailsView(XFUIRecordList list, XFRecord record) {
        return new XFUIPropertiesPanel(record, list.getClient());
    }

    @Override
    public Component createWidget(XFUIPropertiesPanel propertiesPanel, XFRecord record, String column) {
        TextField ta = new TextField();
        ta.setText(record.getString(column));
        ta.getAllStyles().setFgColor(0x0);
        //details.add(l.createConstraint(row, 1), ta);
        ta.addActionListener(e->{
            if (!ta.getText().equals(record.getString(column))) {
                record.set(column, ta.getText());
            }
        });
        return ta;
    }
    
    
}
