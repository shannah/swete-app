/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.forms;

import ca.weblite.swete.SweteClient;
import ca.weblite.swete.components.PopupMenu;
import ca.weblite.swete.components.WhitelistSelectionDialog;
import ca.weblite.swete.models.Snapshot;
import ca.weblite.swete.models.WebSite;
import ca.weblite.swete.util.BrowserUtil;
import com.codename1.components.InteractionDialog;
import com.codename1.components.ToastBar;
import com.codename1.components.ToastBar.Status;
import com.codename1.io.Log;
import com.codename1.ui.BrowserComponent;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import java.io.IOException;

/**
 *
 * @author shannah
 */
public class PreviewForm extends Form {
    private WebSite site;
    private String url;
    private Snapshot snapshot;
    private BrowserComponent browser;
    private boolean allowChangeSnapshot;
    private PopupMenu snapshotsMenu;
    private TextField urlField;
    private Button showWhitelistButton;
    
    public PreviewForm(Snapshot snap, String url) {
        super(new BorderLayout());
        snapshot = snap;
        allowChangeSnapshot = false;
        init(snap.getWebSite(), url);
        
        
    }
    
    public PreviewForm(WebSite site, String url) {
        super(new BorderLayout());
        allowChangeSnapshot = true;
        if (site.getCurrentSnapshotId() != null) {
            snapshot = new Snapshot(site, site.getCurrentSnapshotId());
        }
        init(site, url);
    }
    
    private void init(WebSite website, String url) {
        Toolbar toolbar = new Toolbar();
        setToolbar(toolbar);
        setEnableCursors(true);
        if (snapshot != null) {
            setTitle("Preview: "+snapshot.getSnapshotId());
        } else {
            setTitle("Preview");
        }
        
         final Form backForm = CN.getCurrentForm();
        if (backForm != null) {
            
            toolbar.setBackCommand(new Command("Back") {
                public void actionPerformed(ActionEvent e) {
                    backForm.showBack();
                }
            });
        }
        
        urlField = new TextField();
        showWhitelistButton = new Button();
        showWhitelistButton.setMaterialIcon(FontImage.MATERIAL_ARROW_DROP_DOWN);
        showWhitelistButton.addActionListener(e->{
            WhitelistSelectionDialog dialog = new WhitelistSelectionDialog(website);
            dialog.addActionListener(e2->{
                browser.setURL(site.getProxyUrlForPage(dialog.getSelectedUrl()));
                dialog.dispose();
            });
            dialog.showPopupDialog(showWhitelistButton);
        });
        
        
        getToolbar().addMaterialCommandToRightBar("", FontImage.MATERIAL_MORE_VERT, e->{
            InteractionDialog dlg = new InteractionDialog(BoxLayout.y());
            dlg.setDisposeWhenPointerOutOfBounds(true);
            Button openSourcePage = new Button("Open Source Page in Browser");
            openSourcePage.setMaterialIcon(FontImage.MATERIAL_OPEN_IN_BROWSER);
            openSourcePage.addActionListener(e2->{
                CN.execute(site.getSourceUrlForPage(browser.getURL()));
            });
            
            Button openProxyPage = new Button("Open Translated Page in Browser");
            openProxyPage.setMaterialIcon(FontImage.MATERIAL_OPEN_IN_BROWSER);
            openProxyPage.addActionListener(e2->{
                CN.execute(site.getProxyUrlForPage(browser.getURL()));
            });
            
            $(openSourcePage, openProxyPage)
                    .setTextPosition(Component.RIGHT)
                    .selectAllStyles().setAlignment(Component.LEFT);
            
            dlg.add(openSourcePage);
            dlg.add(openProxyPage);
            
            
            dlg.showPopupDialog(getToolbar().findCommandComponent(e.getCommand()));
        });
        
        snapshotsMenu = new PopupMenu();
        snapshotsMenu.setMaterialIcon(FontImage.MATERIAL_ARROW_DROP_DOWN);
        if (snapshot == null) {
            snapshotsMenu.setCommandLabel("Snapshot: None");
        } else {
            snapshotsMenu.setCommandLabel("Snapshot: "+snapshot.getSnapshotId());
        }
        if (allowChangeSnapshot) {
            getToolbar().addCommandToLeftBar(snapshotsMenu.getCommand());
        }
        
        this.site = website;
        
        this.url = url;
        browser = new BrowserComponent();
        Status status = ToastBar.getInstance().createStatus();
        status.setMessage("Loading "+url);
        status.setShowProgressIndicator(true);
        status.show();
        browser.addWebEventListener("onLoad", e->{
            status.clear();
            urlField.setText(browser.getURL());
            if (snapshot != null) {
                if (!BrowserUtil.checkIfSnapshotEnabled(browser, snapshot)) {
                    if (snapshot != null) {
                        ToastBar.showMessage("Activating snapshot "+snapshot.getSnapshotId(), FontImage.MATERIAL_INFO, 3000);
                    } else {
                        ToastBar.showMessage("Activating snapshot", FontImage.MATERIAL_INFO, 3000);
                    }
                    BrowserUtil.enableSnapshots(browser, snapshot);
                }
                if (BrowserUtil.checkIfCaptureEnabled(browser)) {
                    ToastBar.showMessage("Disabling string capture", FontImage.MATERIAL_INFO, 3000);
                    BrowserUtil.stopCapturing(site, browser);
                }
            } else {
                if (BrowserUtil.checkIfSnapshotsEnabled(browser)) {
                    ToastBar.showMessage("Deactivating snapshots", FontImage.MATERIAL_INFO, 3000);
                    BrowserUtil.disableSnapshots(browser);
                }
            }
        });
        urlField.addActionListener(e->{
            browser.setURL(site.getProxyUrlForPage(urlField.getText()));
        });
        browser.setURL(url);
        
        add(BorderLayout.CENTER, browser);
        add(BorderLayout.NORTH, BorderLayout.center(urlField).add(BorderLayout.EAST, showWhitelistButton));
        
        
    }

    @Override
    protected void onShowCompleted() {
        super.onShowCompleted(); 
        CN.callSerially(()->refreshSnapshots());
    }
    
    
    
    private void refreshSnapshots() {
        snapshotsMenu.removeAllCommands();
        snapshotsMenu.revalidateWithAnimationSafety();
        SweteClient client = new SweteClient(site);
        try {
            client.loadSnapshots(site);
        } catch (IOException ex) {
            Log.e(ex);
            ToastBar.showErrorMessage("Failed to refresh snapshots: "+ex.getMessage());
            return;
        }
        updateSnapshots();
        
        
    }
    
    private void changeSnapshot(Snapshot snap) {
        this.snapshot = snap;
        if (snapshot == null) {
            BrowserUtil.disableSnapshots(browser);
            getToolbar().findCommandComponent(snapshotsMenu.getCommand()).setText("Snapshot: None");
        } else {
            BrowserUtil.enableSnapshots(browser, snap);
            getToolbar().findCommandComponent(snapshotsMenu.getCommand()).setText("Snapshot: "+snap.getSnapshotId());
        }
        
   }
    
    private void updateSnapshots() {
        snapshotsMenu.removeAllCommands();
        snapshotsMenu.addCommand(Command.create("None", null, e->{
            CN.callSerially(()->changeSnapshot(null));
        }));
        for (Snapshot snapshot : site.getSnapshots()) {
            if (snapshot.getDateCompleted() == null) {
                continue;
            }
            Command cmd = Command.create(snapshot.getSnapshotId()+": "+snapshot.getDateCompleted(), null, ev->{
                CN.callSerially(()->changeSnapshot(snapshot));
            });
            snapshotsMenu.addCommand(cmd);
        }
        snapshotsMenu.revalidateWithAnimationSafety();
    }
}
