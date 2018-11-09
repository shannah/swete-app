/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.components;

import com.codename1.components.InteractionDialog;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;

/**
 *
 * @author shannah
 */
public class AddWebSiteDialog extends InteractionDialog {
    private AddWebSitePanel panel;
    
    
    public AddWebSiteDialog() {
        super("Add WebSite", new BorderLayout());
        setDisposeWhenPointerOutOfBounds(true);
        panel = new AddWebSitePanel();
        panel.addActionListener(e->{
            dispose();
        });
        getContentPane().add(BorderLayout.CENTER, panel);
    }
    
    public void addActionListener(ActionListener l) {
        panel.addActionListener(l);
    }
    
    public void removeActionListener(ActionListener l) {
        panel.removeActionListener(l);
    }
    
    public String getSelectedUrl() {
        return panel.getSelectedUrl();
    }
    
}
