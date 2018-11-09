/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.components;

import ca.weblite.swete.models.Settings;
import ca.weblite.swete.models.WebSite;
import com.codename1.components.MultiButton;
import com.codename1.components.ToastBar;
import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.util.EventDispatcher;

/**
 *
 * @author shannah
 */
public class WebSitesMenu extends Container {
    private Settings settings;
    private TextField filter;
    private Container results;
    private WebSite selectedWebSite;
    private final EventDispatcher listeners = new EventDispatcher();
    private Button addWebSiteButton;
    
    public WebSitesMenu(Settings settings) {
        super(new BorderLayout());
        this.settings = settings;
        results = new Container(BoxLayout.y());
        results.setScrollableY(true);
        filter = new TextField();
        filter.setHint("Filter");
        filter.setHintIcon(FontImage.createMaterial(FontImage.MATERIAL_SEARCH, filter.getStyle()));
        addWebSiteButton = new Button("Add Web Site");
        addWebSiteButton.addActionListener(e->{
            addWebSite();
        });
        add(BorderLayout.NORTH, filter);
        add(BorderLayout.CENTER, results);
        add(BorderLayout.SOUTH, addWebSiteButton);
        updateResults();
        
    }
    
    private void updateResults() {
        results.removeAll();
        String filterString = filter.getText().trim().toLowerCase();
        for (WebSite site : settings.getWebSites()) {
            if (filterString.length() == 0 || 
                    site.getProxyUrl().toLowerCase().contains(filterString) || 
                    site.getName().toLowerCase().contains(filterString)) {
                MultiButton btn = new MultiButton();
                btn.setTextLine1(site.getName());
                btn.setTextLine2(site.getProxyUrl());
                btn.addActionListener(e->{
                    selectedWebSite = site;
                    listeners.fireActionEvent(e);
                });
                results.add(btn);
            }
        }
        revalidate();
    }
    
    public void addActionListener(ActionListener e) {
        listeners.addListener(e);
    }
    
    public void removeActionListener(ActionListener e) {
        listeners.removeListener(e);
    }
    
    public WebSite getSelectedWebSite() {
        return selectedWebSite;
    }
    
    private void addWebSite() {
        AddWebSiteDialog dialog = new AddWebSiteDialog();
        dialog.addActionListener(e->{
            String selectedUrl = dialog.getSelectedUrl();
            if (selectedUrl != null) {
                if (settings.addWebSiteWithURL(selectedUrl)) {
                    updateResults();
                } else {
                    ToastBar.showErrorMessage("Failed to add website: "+settings.getValidationFailure().getMessage());
                }
            }
        });
        dialog.showPopupDialog(this);
    }
    
    
}
