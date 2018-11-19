/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.forms;

import ca.weblite.swete.SweteClient;
import ca.weblite.swete.models.WebSite;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.Dialog;
import com.codename1.ui.Form;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import java.io.IOException;

/**
 *
 * @author shannah
 */
public class LoggedOutForm extends Form {
    
    private Form backTo;
    private WebSite site;
    
    public LoggedOutForm(Form backTo, WebSite site) {
        super("Logged Out of "+backTo.getTitle(), new BorderLayout());
        this.site = site;
        this.backTo = backTo;
        Button btn = new Button("Log in");
        btn.addActionListener(e->{
            CN.callSerially(()->login());
        });
        
        add(BorderLayout.CENTER, BoxLayout.encloseY(btn));
    }
    
    
    private void login() {
        SweteClient client = new SweteClient(site);
        try {
            client.load();
            backTo.showBack();
        } catch (IOException ex) {
            Dialog.show("Login failed", ex.getMessage(), "OK", null);
        }
        
        
        
    }
}
