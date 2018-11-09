/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.components;

import ca.weblite.swete.SweteClient;
import ca.weblite.swete.models.WebSite;
import com.codename1.io.URL;
import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Label;
import com.codename1.ui.TextComponent;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.ui.validation.Constraint;
import com.codename1.ui.validation.Validator;
import java.net.URISyntaxException;

/**
 *
 * @author shannah
 */
public class AddWebSitePanel extends Container {
    private TextComponent url;
    private Button ok, cancel;
    private String selectedUrl;
    private final EventDispatcher listeners = new EventDispatcher();
    
    public AddWebSitePanel() {
        super(new BorderLayout());
        url = new TextComponent();
        
        url.getField().setHint("https://www.example.com.mx/");
        url.getField().addActionListener(e->{
            String val = url.getText();
            if (!val.startsWith("http://") && !val.startsWith("https://")) {
                url.getField().setText("http://"+val);
            }
        });
        url.onTopMode(true);
        Validator validator = new Validator();
        validator.setShowErrorMessageForFocusedComponent(true);
        validator.addConstraint(url, new Constraint() {
            private String message;
            @Override
            public boolean isValid(Object value) {
                String v = (String)value;
                message = null;
                url.errorMessage(message);
                if (v.length() == 0) {
                    return false;
                }
               
                if (!v.startsWith("http://") && !v.startsWith("https://")) {
                    message = "URL must start with http:// or https://";
                    url.errorMessage(message);
                    return false;
                }
                /*
                WebSite temp = new WebSite();
                temp.setProxyUrl(v);
                
                SweteClient client = new SweteClient(temp);
                if (!client.validateAdminUrl()) {
                    message = "URL is not a SWeTE website";
                    url.errorMessage(message);
                    return false;
                }
*/  
                try {
                    URL u = new URL(v);
                } catch (URISyntaxException ex) {
                    message = ex.getMessage();
                    url.errorMessage(ex.getMessage());
                    return false;
                }
                url.errorMessage(null);
                return true;
            }

            @Override
            public String getDefaultFailMessage() {
                if (message != null) {
                    return message;
                }
                return "Invalid URL";
            }
            
        });
        
        ok = new Button("Add");
        ok.setMaterialIcon(FontImage.MATERIAL_ADD);
        ok.addActionListener(e->{
            selectedUrl = url.getText().trim();
            listeners.fireActionEvent(e);
        });
        cancel = new Button("Cancel");
        cancel.setMaterialIcon(FontImage.MATERIAL_CANCEL);
        cancel.addActionListener(e->{
            selectedUrl = null;
            listeners.fireActionEvent(e);
        });
        
        validator.addSubmitButtons(ok);
        
        add(BorderLayout.CENTER, url);
        add(BorderLayout.SOUTH, GridLayout.encloseIn(2, cancel, ok));
        
    }
    
     public void addActionListener(ActionListener e) {
        listeners.addListener(e);
    }
    
    public void removeActionListener(ActionListener e) {
        listeners.removeListener(e);
    }
    
    public String getSelectedUrl() {
        return selectedUrl;
    }
    
}
