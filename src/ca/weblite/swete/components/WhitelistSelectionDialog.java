/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.components;

import ca.weblite.swete.SweteClient;
import ca.weblite.swete.models.WebSite;
import com.codename1.components.InteractionDialog;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkManager;
import com.codename1.io.Util;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.FontImage;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.util.regex.RE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shannah
 */
public class WhitelistSelectionDialog extends InteractionDialog {

    /**
     * @return the selectedUrl
     */
    public String getSelectedUrl() {
        return selectedUrl;
    }

    /**
     * @param selectedUrl the selectedUrl to set
     */
    public void setSelectedUrl(String selectedUrl) {
        this.selectedUrl = selectedUrl;
    }
    private TextField filterField;
    private WebSite site;
    private String selectedUrl;
    private Container resultsContainer;

    private final EventDispatcher listeners = new EventDispatcher();
    
    public WhitelistSelectionDialog(WebSite site) {
        super("Select Page");
        this.site = site;
        this.setDisposeWhenPointerOutOfBounds(true);
        filterField = new TextField();
        filterField.setHint("Filter");
        filterField.setHintIcon(FontImage.createMaterial(FontImage.MATERIAL_SEARCH, filterField.getStyle()));
        resultsContainer = new Container(BoxLayout.y()) {
            @Override
            protected Dimension calcPreferredSize() {
                
                Dimension dim = super.calcPreferredSize();
                dim.setWidth(Math.max(Font.getDefaultFont().stringWidth("Mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"), dim.getWidth()));
                dim.setHeight(Math.max(dim.getHeight(), (int)Math.round(Display.getInstance().getDisplayHeight() * 0.7f)));
                return dim;
            }
            
        };
        resultsContainer.setScrollableY(true);
        
        
        filterField.addDataChangedListener(new DataChangedListener() {
            @Override
            public void dataChanged(int type, int index) {
                updateResults();
            }
            
        });
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(BorderLayout.NORTH, filterField);
        getContentPane().add(BorderLayout.CENTER, resultsContainer);
        CN.callSerially(()->updateResults());
    }
    
    
    
    private boolean updating;
    private void updateResults() {
        if (updating) return;
        updating = true;
        try {
            if (site.getWhitelist() == null) {
                try {
                    site.setWhitelist(new SweteClient(site).loadWhitelist());

                } catch (IOException ex) {
                   site.setWhitelist(new String[0]);
                }
            }

            resultsContainer.removeAll();
            for (String url : site.getWhitelist()) {
                if (filterField.getText().trim().length() == 0 || 
                        url.toLowerCase().contains(filterField.getText().toLowerCase())) {
                    Button b = new Button(url.substring(site.getSrcUrl().length()));
                    b.setUIID("Label");
                    b.addActionListener(e->{
                        setSelectedUrl(url);
                        listeners.fireActionEvent(e);
                    });
                    resultsContainer.add(b);
                }
            }
        } finally {
            updating = false;
        }
        
        revalidateWithAnimationSafety();
    }
    
    public void addActionListener(ActionListener l) {
        listeners.addListener(l);
    }
    
    public void removeActionListener(ActionListener l) {
        listeners.removeListener(l);
    }
    
    
}
