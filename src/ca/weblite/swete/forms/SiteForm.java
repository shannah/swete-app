/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.forms;

import ca.weblite.swete.SweteApp;
import ca.weblite.swete.components.BrowserToolbar;
import ca.weblite.swete.components.JobQueueProgressBar;
import ca.weblite.swete.components.WhitelistSelectionDialog;
import ca.weblite.swete.models.Session;
import ca.weblite.swete.models.WebSite;
import ca.weblite.swete.services.BackgroundJob;
import ca.weblite.swete.services.JobQueue;
import ca.weblite.swete.services.JobQueue.JobQueueListener;
import ca.weblite.swete.util.BrowserUtil;
import com.codename1.components.SplitPane;
import com.codename1.components.Switch;
import com.codename1.components.ToastBar;
import com.codename1.components.ToastBar.Status;
import com.codename1.io.Log;
import com.codename1.ui.BrowserComponent;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.FlowLayout;

/**
 *
 * @author shannah
 */
public class SiteForm extends Form implements JobQueueListener {
    private WebSite website;
    private Session session;
    private BrowserComponent srcBrowser, proxyBrowser;
    private boolean isCapturing;
    private SplitPane splitPane;
    private TextField urlField;
    private Button showWhitelistButton;
    private Container bottomBar;
    private JobQueueProgressBar jobQueueProgressBar;
    

    public SiteForm(WebSite site) {
        super(site.getName(), new BorderLayout());
        Toolbar toolbar = new Toolbar();
        
        
        
        setToolbar(toolbar);
        setEnableCursors(true);
        setTitle(site.getName());
        
        toolbar.addMaterialCommandToRightSideMenu("Manage Snapshots", FontImage.MATERIAL_PAGES, e->{
            SnapshotsForm snapshots = new SnapshotsForm(website);
            Log.p("Showing snapshots form");
            snapshots.show();
        });
        
        final Form backForm = CN.getCurrentForm();
        if (backForm != null) {
            
            toolbar.setBackCommand(new Command("Back") {
                public void actionPerformed(ActionEvent e) {
                    backForm.showBack();
                }
            });
        }
        
        
        this.website = site;
        urlField = new TextField();
        showWhitelistButton = new Button();
        showWhitelistButton.setMaterialIcon(FontImage.MATERIAL_ARROW_DROP_DOWN);
        showWhitelistButton.addActionListener(e->{
            WhitelistSelectionDialog dialog = new WhitelistSelectionDialog(website);
            dialog.addActionListener(e2->{
                srcBrowser.setURL(dialog.getSelectedUrl());
                dialog.dispose();
            });
            dialog.showPopupDialog(showWhitelistButton);
        });
        srcBrowser = new BrowserComponent();
        proxyBrowser = new BrowserComponent();
        splitPane = new SplitPane(new SplitPane.Settings().preferredInset("50%"), srcBrowser, proxyBrowser);
        
        srcBrowser.addWebEventListener("onLoad", e->{
            System.out.println("In onLoad");
            urlField.setText(srcBrowser.getURL());
            String newUrl = website.getProxyUrlForPage(srcBrowser.getURL());
            if (!newUrl.equals(proxyBrowser.getURL())) {
                proxyBrowser.setURL(newUrl);
            }
        });
        BrowserToolbar btoolbar = new BrowserToolbar();
        proxyBrowser.addWebEventListener("onLoad", e->{
            
            String snapshotsCookie = BrowserUtil.getCookieAndWait(proxyBrowser, "--swete-static");
            System.out.println("Snapshots cookie is "+snapshotsCookie);
            if (snapshotsCookie == null || !"true".equals(snapshotsCookie)) {
                btoolbar.getSnapshotsSwitch().setOn();
            } else {
                btoolbar.getSnapshotsSwitch().setOff();
            }
            
            String captureCookie = BrowserUtil.getCookieAndWait(proxyBrowser, "--swete-capture");
            if ("1".equals(captureCookie)) {
                btoolbar.getCaptureSwitch().setOn();
            } else {
                btoolbar.getCaptureSwitch().setOff();
            }
        });
        srcBrowser.setURL(site.getSrcUrl());
        
        
        
        btoolbar.getCaptureSwitch().addActionListener(e->{
            Switch src = btoolbar.getCaptureSwitch();
            if (src.isOn()) {
                BrowserUtil.startCapturing(website, proxyBrowser);
            } else {
                BrowserUtil.stopCapturing(website, proxyBrowser);
            }
        });
        btoolbar.getSnapshotsSwitch().addActionListener(e->{
            Switch src = btoolbar.getSnapshotsSwitch();
            if (src.isOn()) {
                BrowserUtil.enableSnapshots(proxyBrowser);
            } else {
                BrowserUtil.disableSnapshots(proxyBrowser);
            }
        });
        btoolbar.getOpenProxyInBrowser().addActionListener(e->{
            CN.execute(proxyBrowser.getURL());
        });
        btoolbar.getOpenSrcInBrowser().addActionListener(e->{
            CN.execute(srcBrowser.getURL());
        });
        btoolbar.getRefresh().addActionListener(e->{
            srcBrowser.reload();
            proxyBrowser.reload();
        });
        btoolbar.getSnapshotsSwitch().setOn();
        
        jobQueueProgressBar = new JobQueueProgressBar(SweteApp.getInstance().getJobQueue());
        bottomBar = new Container(new FlowLayout());
        bottomBar.add(jobQueueProgressBar);
        
        
        add(BorderLayout.NORTH, btoolbar);
        
        Container center = new Container(new BorderLayout());
        center.add(BorderLayout.CENTER, splitPane);
        center.add(BorderLayout.NORTH, BorderLayout.center(urlField).add(BorderLayout.EAST, showWhitelistButton));
        add(BorderLayout.CENTER, center);
    }
    
    
    @Override
    public void jobAdded(BackgroundJob job) {
        jobQueueProgressBar.setVisible(getJobQueue().getCurrentlyRunningJob() != null);
        revalidateWithAnimationSafety();
    }

    @Override
    public void jobRemoved(BackgroundJob job) {
        jobQueueProgressBar.setVisible(getJobQueue().getCurrentlyRunningJob() != null);
        revalidateWithAnimationSafety();
    }

    @Override
    public void jobChanged(BackgroundJob job) {
        jobQueueProgressBar.setVisible(getJobQueue().getCurrentlyRunningJob() != null);
        revalidateWithAnimationSafety();
    }

    @Override
    protected void initComponent() {
        super.initComponent(); 
        jobQueueProgressBar.setVisible(getJobQueue().getCurrentlyRunningJob() != null);
        getJobQueue().addListener(this);
        
    }

    @Override
    protected void deinitialize() {
        getJobQueue().removeListener(this);
        super.deinitialize(); 
    }
    
    
    
    
    private JobQueue getJobQueue() {
        return SweteApp.getInstance().getJobQueue();
    }
    
    
    
}
