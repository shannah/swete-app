/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.forms;

import ca.weblite.swete.SweteClient;
import ca.weblite.swete.models.Settings;
import ca.weblite.swete.models.WebSite;
import com.codename1.components.InteractionDialog;
import com.codename1.components.MultiButton;
import com.codename1.components.ToastBar;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import java.io.IOException;

/**
 *
 * @author shannah
 */
public class SiteMenuForm extends Form {
    private WebSite website;
    
    public SiteMenuForm(WebSite website) {
        super(new BorderLayout());
        this.website = website;
        final Form backForm = CN.getCurrentForm();
        CN.callSeriallyOnIdle(()->{
            SweteClient client = new SweteClient(website);
            try {
                client.load(website);
            } catch (IOException ex) {
                Log.e(ex);
                ToastBar.showErrorMessage("Failed to load website info:"+ex.getMessage());
                backForm.showBack();
            }
        });
        Toolbar toolbar = new Toolbar();
        
        
        
        setToolbar(toolbar);
        setEnableCursors(true);
        setTitle(website.getName());
        
        
        if (backForm != null) {
            
            toolbar.setBackCommand(new Command("Back") {
                public void actionPerformed(ActionEvent e) {
                    backForm.showBack();
                }
            });
        }
        
        getToolbar().addMaterialCommandToRightBar("", FontImage.MATERIAL_MORE_VERT, e->{
            InteractionDialog dlg = new InteractionDialog(BoxLayout.y());
            dlg.setDisposeWhenPointerOutOfBounds(true);
            Button openAdminDashboard = new Button("Open Dashboard");
            openAdminDashboard.setMaterialIcon(FontImage.MATERIAL_SECURITY);
            openAdminDashboard.addActionListener(e2->{
                CN.execute(website.getAdminUrl());
            });
            
            Button logout = new Button("Logout");
            logout.setMaterialIcon(FontImage.MATERIAL_EXIT_TO_APP);
            logout.addActionListener(e2->{
                website.setPassword(null);
                try {
                    Settings.getInstance().save();
                    SweteClient client = new SweteClient(website);
                    client.logout();
                } catch (IOException ex) {
                    Log.e(ex);
                    ToastBar.showErrorMessage("Failed to log out: "+ex.getMessage());
                    return;
                }
                backForm.showBack();
            });
           
            
            $(openAdminDashboard, logout )
                    .setTextPosition(Component.RIGHT)
                    .selectAllStyles().setAlignment(Component.LEFT);
            
            dlg.add(openAdminDashboard).add(logout);
            
            
            
            dlg.showPopupDialog(getToolbar().findCommandComponent(e.getCommand()));
        });
        
        
        MultiButton strings = new MultiButton();
        strings.setTextLine1("String Management");
        strings.setTextLine2("Manage site strings and translations");
        strings.setIcon(FontImage.createMaterial(FontImage.MATERIAL_TRANSLATE, strings.getStyle()));
        strings.setEmblem(FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_RIGHT, strings.getStyle()));
        strings.addActionListener(e->{
            new StringManagementForm(website).show();
        });
        
        MultiButton snapshots = new MultiButton();
        snapshots.setTextLine1("Snapshot Management");
        snapshots.setTextLine2("Manage site snapshots");
        snapshots.setIcon(FontImage.createMaterial(FontImage.MATERIAL_PHOTO_CAMERA, snapshots.getStyle()));
        snapshots.setEmblem(FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_RIGHT, snapshots.getStyle()));
        snapshots.addActionListener(e->{
            new SnapshotsForm(website).show();
        });
        
        MultiButton preview = new MultiButton();
        preview.setTextLine1("Preview");
        preview.setTextLine2("Preview Web Pages");
        preview.setIcon(FontImage.createMaterial(FontImage.MATERIAL_WEB, preview.getStyle()));
        preview.setEmblem(FontImage.createMaterial(FontImage.MATERIAL_KEYBOARD_ARROW_RIGHT, snapshots.getStyle()));
        preview.addActionListener(e->{
            new PreviewForm(website, website.getProxyUrl()).show();
        });
        Container center = new Container(BoxLayout.y());
        center.setScrollableY(true);
        center.add(strings).add(snapshots).add(preview);
        add(BorderLayout.CENTER, center);
    }
}
