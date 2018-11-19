package ca.weblite.swete;


import ca.weblite.swete.forms.SiteForm;
import ca.weblite.swete.forms.SiteMenuForm;
import ca.weblite.swete.forms.WelcomeForm;
import ca.weblite.swete.models.Settings;
import ca.weblite.swete.models.WebSite;
import ca.weblite.swete.onsen.controllers.StringsPageController;
import ca.weblite.swete.services.JobQueue;
import com.codename1.components.InfiniteProgress;
import com.codename1.components.SpanLabel;
import static com.codename1.ui.CN.*;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Dialog;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.io.Log;
import com.codename1.ui.Toolbar;
import java.io.IOException;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.io.NetworkEvent;
import com.codename1.io.Util;
import com.codename1.onsen.OnsApplication;
import com.codename1.onsen.TagParser;
import com.codename1.onsen.components.AbstractElement;
import com.codename1.onsen.components.OnsList;
import com.codename1.onsen.components.OnsPage;
import com.codename1.onsen.components.OnsSplitterSide;
import com.codename1.onsen.templates.app.SinglePageTemplate;
import com.codename1.onsen.templates.app.SplitterTemplate;
import com.codename1.onsen.templates.page.snippets.FormBuilder;
import com.codename1.onsen.templates.page.snippets.MenuBuilder;
import com.codename1.ui.CN;
import com.codename1.ui.layouts.BorderLayout;


/**
 * This file was generated by <a href="https://www.codenameone.com/">Codename One</a> for the purpose 
 * of building native mobile applications using Java.
 */
public class SweteApp {

    private boolean singleSiteMode = true;
    private String singleSiteUrl = "https://www.a2hosting.com.mx/";
    
    private JobQueue jobQueue;
    private static SweteApp instance;
    
    private Form current;
    private Resources theme;

    public void init(Object context) {
        if ("HTML5".equals(CN.getPlatformName())) {
            singleSiteUrl = null;
        }
        instance = this;
        // use two network threads instead of one
        updateNetworkThreadCount(2);

        theme = UIManager.initFirstTheme("/theme");

        // Enable Toolbar on all Forms by default
        Toolbar.setGlobalToolbar(true);
        if (CN.isTablet()) {
            Toolbar.setPermanentSideMenu(true);
        }

        // Pro only feature
        Log.bindCrashProtection(true);

        addNetworkErrorListener(err -> {
            // prevent the event from propagating
            err.consume();
            if(err.getError() != null) {
                Log.e(err.getError());
            }
            Log.sendLogAsync();
            Dialog.show("Connection Error", "There was a networking error in the connection to " + err.getConnectionRequest().getUrl(), "OK", null);
        });  
        
        Util.register(new WebSite());
        Util.register(new Settings());
        jobQueue = new JobQueue();
        
    }
    
    
    
    
    public void startHtml(Settings settings, WebSite website) {
        
        
        SplitterTemplate tpl = new SplitterTemplate.Left() {
            @Override
            protected void initUI() {
                super.initUI();
                getSplitterSide().setCollapse(false);
                getSplitterSide().setAnimation(OnsSplitterSide.Animation.Reveal);
                getSplitterSide().getPage().add(createComponent("<div>Menu</div>"));
                getSplitter().getContent().getPage().add(createComponent("<div>Hello Main</div>"));
                
                OnsPage menuPage = getSplitterSide().getPage();
                MenuBuilder mb = menuBuilder();
                mb.addMenuTitle("Navigation");
                mb.addMenuItem("Translations",e -> {
                    
                    StringsPageController controller = new StringsPageController(getApplication(), website);
                    OnsPage translationsPage = controller.getPage();
                    getSplitter().getContent().load(translationsPage, res->{});

                });
                mb.addMenuItem("Snapshots");
                mb.addMenuItem("Preview");
                menuPage.add(mb.get());
                
                
                OnsPage p = getSplitter().getContent().getPage();
                
                TagParser tp = new TagParser(getApplication());
                AbstractElement root;
                try {
                    root = tp.parseHTML(CN.getResourceAsStream("/Welcome.html"));
                    
                } catch (IOException ex) {
                    Log.e(ex);
                    throw new RuntimeException(ex);
                }
                p.add(root);
                
            }
            
        };
        
        
        OnsApplication.launch(tpl);
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
        
        /*
        WebSite site = new WebSite();
        site.setName("A2 Hosting");
        site.setSrcUrl("https://mx.www.a2hosting.com/");
        site.setProxyUrl("https://www.a2hosting.com.mx/");
        site.setSiteId("428");
        Form hi = new SiteForm(site);
        
        hi.show();
        */
        Dialog dlg = new InfiniteProgress().showInfiniteBlocking();
        if (singleSiteMode) {
            
            
            
            
            Form errorForm = new Form("A problem occurred");
            errorForm.setLayout(BoxLayout.y());
            SpanLabel errorLabel = new SpanLabel();
            errorForm.add(errorLabel);
            // The web version should only support the current website itself.
            try {
                //Log.p("Loading settings");
                Settings settings = Settings.getInstance();
                //Log.p("Settings loaded with websites "+settings.getWebSites());
                if (settings.getWebSites() == null || settings.getWebSites().isEmpty()) {
                    if (singleSiteUrl == null) {
                        String location = CN.getProperty("browser.window.location.href", null);
                        if (location == null) {
                            errorLabel.setText("Failed to load the admin panel because the window location could not be found.");
                            errorForm.show();
                            return;
                        }

                        if (location.indexOf("/swete-admin/") < 0) {
                            errorLabel.setText("This application can only be accessed from within a SWeTE instalation.");
                            errorForm.show();
                            return;
                        }
                        singleSiteUrl= location.substring(0, location.indexOf("/swete-admin/")+1);
                    }

                    //Log.p("Adding website with url "+singleSiteUrl);
                        
                    if (!settings.addWebSiteWithURL(singleSiteUrl)) {
                        errorLabel.setText("Failed to add website with URL "+singleSiteUrl);
                        errorForm.show();
                        return;
                    }
                    if (settings.getWebSites() == null || settings.getWebSites().isEmpty()) {
                        errorLabel.setText("There was a problem adding teh website "+singleSiteUrl);
                        errorForm.show();
                        return;
                    }
                    
                }
                WebSite ws = settings.getWebSites().get(0);
                //Log.p("Username: "+ws.getUserName()+", Password: "+ws.getPassword());
                SweteClient cl = new SweteClient(ws);
                cl.load();
                settings.save();
                new SiteMenuForm(ws).show();
                return;
            } catch (IOException ex) {
                Log.e(ex);
                errorLabel.setText("There was a problem loading the site information: "+ex.getMessage());
                errorForm.show();
                return;
            }
        }
        
        try {
            WelcomeForm form = new WelcomeForm(Settings.getInstance());
            form.show();
        } catch (IOException ex) {
            Log.e(ex);
        }
    }

    public void stop() {
        current = getCurrentForm();
        if(current instanceof Dialog) {
            ((Dialog)current).dispose();
            current = getCurrentForm();
        }
    }
    
    public void destroy() {
    }
    
    public static SweteApp getInstance() {
        return instance;
    }
    
    public JobQueue getJobQueue() {
        return jobQueue;
    }

    
    public boolean isSingleSiteMode() {
        return singleSiteMode;
    }
}
