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
import ca.weblite.swete.util.SnapshotUtil;
import com.codename1.components.InteractionDialog;
import com.codename1.components.SpanLabel;
import com.codename1.components.ToastBar;
import com.codename1.components.ToastBar.Status;
import com.codename1.io.Log;
import com.codename1.ui.BrowserComponent;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import static com.codename1.ui.CN.callSerially;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Dialog;
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
public class UpdatePageSnapshotForm extends Form {
    private WebSite site;
    private String url;
    private Snapshot snapshot;
    private BrowserComponent browser;
    private boolean allowChangeSnapshot;
    private PopupMenu snapshotsMenu;
    private TextField urlField;
    private Form backForm;
    
    public UpdatePageSnapshotForm(Snapshot snap, String url) {
        super(new BorderLayout());
        snapshot = snap;
        allowChangeSnapshot = false;
        init(snap.getWebSite(), url);
        
        
    }
    
    public UpdatePageSnapshotForm(WebSite site, String url) {
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
        setTitle("Update Page Snapshot");
        
        backForm = CN.getCurrentForm();
        if (backForm != null) {
            
            toolbar.setBackCommand(new Command("Back") {
                public void actionPerformed(ActionEvent e) {
                    backForm.showBack();
                }
            });
        }
        
        urlField = new TextField();
        urlField.setEditable(false);
        
        
        PopupMenu menu = new PopupMenu();
        menu.addCommand(Command.createMaterial("Open Source Page in Browser", FontImage.MATERIAL_OPEN_IN_BROWSER, e->{
            CN.execute(site.getSourceUrlForPage(browser.getURL()));
        }));
        
        menu.addCommand(Command.createMaterial("Open Translated Page in Browser", FontImage.MATERIAL_OPEN_IN_BROWSER, e->{
            CN.execute(site.getProxyUrlForPage(browser.getURL()));
        }));
        
        getToolbar().addCommandToRightBar(menu.getCommand());
        
        
        
        
        this.site = website;
        
        this.url = url;
        browser = new BrowserComponent();
        Status status = ToastBar.getInstance().createStatus();
        status.setMessage("Loading "+url);
        status.setShowProgressIndicator(true);
        status.show();
        loadingStatus = status;
        browser.addWebEventListener("onLoad", e->{
            if (loadingStatus != null) {
                loadingStatus.clear();
            }
            urlField.setText(browser.getURL());
            boolean requireRefresh = false;
            String message = "Refreshing page";
            requireRefresh  = BrowserUtil.disableSnapshots(browser) || requireRefresh;
            //requireRefresh = BrowserUtil.stopCapturing(site, browser) || requireRefresh;
            message = "Loading live version.  Please wait...";

            
            if (requireRefresh) {
                loadingStatus = ToastBar.getInstance().createStatus();
                loadingStatus.setMessage(message);
                loadingStatus.show();
                CN.callSerially(()->browser.reload());
            }
        });
        urlField.addActionListener(e->{
            browser.setURL(site.getProxyUrlForPage(urlField.getText()));
        });
        browser.setURL(url);
        
        add(BorderLayout.CENTER, browser);
        
        Button updateButton = new Button("Update Now", "UpdateSnapshotUpdateNowButton");
        updateButton.setMaterialIcon(FontImage.MATERIAL_CHECK);
        updateButton.addActionListener(e->{
            callSerially(()->{
                updateNow();
            });
        });
        
        
        SpanLabel instructions = new SpanLabel("Please review the page content below.  If would like to replace the current snapshotted version of this page with the version below, click the 'Update Now' button.");
        
        add(BorderLayout.NORTH, BorderLayout.center(BoxLayout.encloseY(instructions, urlField)).add(BorderLayout.EAST, updateButton));
        
        
    }

    ToastBar.Status loadingStatus;
    
    @Override
    protected void onShowCompleted() {
        super.onShowCompleted(); 
        
    }
    
    
    private void updateNow() {
        
        boolean success = SnapshotUtil.recrawlPageSnapshotInfiniteBlocking(snapshot, url);
        if (success) {
            backForm.showBack();
        }
        
    
    }
    
    
    
    
    
}
