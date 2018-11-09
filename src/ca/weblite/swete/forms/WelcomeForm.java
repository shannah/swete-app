/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.forms;

import ca.weblite.swete.components.WebSitesMenu;
import ca.weblite.swete.models.Settings;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;

/**
 *
 * @author shannah
 */
public class WelcomeForm extends Form {
    WebSitesMenu menu;
    Settings settings;
    public WelcomeForm(Settings settings) {
        super("Simple Website Translation Engine", new BorderLayout());
        this.settings = settings;
        menu = new WebSitesMenu(settings);
        menu.addActionListener(e->{
            SiteForm siteForm = new SiteForm(menu.getSelectedWebSite());
            siteForm.show();
        });
        add(BorderLayout.NORTH, new Label("Welcome to SWeTE"));
        
        add(BorderLayout.CENTER, menu);
        
    }
}
