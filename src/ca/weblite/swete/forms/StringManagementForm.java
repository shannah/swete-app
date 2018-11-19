/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.forms;

import ca.weblite.swete.SweteApp;
import ca.weblite.swete.SweteClient;
import ca.weblite.swete.components.JobQueueProgressBar;
import ca.weblite.swete.components.PopupMenu;
import ca.weblite.swete.models.TranslationStats;
import ca.weblite.swete.models.WebSite;
import ca.weblite.swete.services.BackgroundJob;
import ca.weblite.swete.services.CapturePageCrawler;
import ca.weblite.swete.services.JobQueue;
import ca.weblite.swete.services.JobQueue.JobQueueListener;
import com.codename1.components.InfiniteProgress;
import com.codename1.components.MultiButton;
import com.codename1.components.ToastBar;
import com.codename1.io.Log;
import com.codename1.ui.CN;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.table.TableLayout;
import java.io.IOException;

/**
 *
 * @author shannah
 */
public class StringManagementForm extends Form implements JobQueueListener {
    private WebSite website;
    Container center;
    private JobQueueProgressBar jobQueueProgressBar;
    
    public StringManagementForm(WebSite website) {
        super(new BorderLayout());
        this.website = website;
        Toolbar toolbar = new Toolbar();
        setToolbar(toolbar);
        setEnableCursors(true);
        setTitle(website.getName()+" > Strings");
        
        final Form backForm = CN.getCurrentForm();
        if (backForm != null) {
            
            toolbar.setBackCommand(new Command("Back") {
                public void actionPerformed(ActionEvent e) {
                    backForm.showBack();
                }
            });
        }
        /*
        PopupMenu menu = new PopupMenu();
        menu.addCommand(Command.createMaterial("Crawl Site", FontImage.MATERIAL_WEB, e->{
            CN.callSerially(()->crawlSite());
        }));
        getToolbar().addCommandToRightBar(menu.getCommand());
        */
        center = new Container(BoxLayout.y());
        add(BorderLayout.CENTER, center);
        jobQueueProgressBar = new JobQueueProgressBar(SweteApp.getInstance().getJobQueue());
        add(BorderLayout.SOUTH, jobQueueProgressBar);
       
        
        
        CN.callSerially(()->refresh());
        
    }
    
    private void crawlSite() {
        CapturePageCrawler crawler = new CapturePageCrawler(website);
        crawler.setJobDescription("Looking for changes in "+website.getName());
        getJobQueue().add(crawler);
        
    }
    
    private void refresh() {
        
        center.removeAll();
        center.add(FlowLayout.encloseCenter(new InfiniteProgress()));
        revalidateWithAnimationSafety();
        
        SweteClient client = new SweteClient(website);
        try {
            client.loadTranslationStats();
        } catch (IOException ex) {
            Log.e(ex);
            ToastBar.showErrorMessage("Failed to refresh stats: "+ex.getMessage());
            return;
        }
        
        
        update();
        
        
        
    }
    
    private void update() {
        center.removeAll();
        TranslationStats stats = website.getTranslationStats();
        if (stats == null) {
            Log.p("No stats found");
            return;
        }
        TableLayout tl = new TableLayout(4, 2);
        Container table = new Container(tl);
        table.add(tl.createConstraint(0, 0), new Label("Total Words:"));
        table.add(tl.createConstraint(0, 1), new Label(""+stats.getTotalWords()));
        table.add(tl.createConstraint(1, 0), new Label("Total Phrases:"));
        table.add(tl.createConstraint(1, 1), new Label(""+stats.getTotalPhrases()));
        table.add(tl.createConstraint(2, 0), new Label("Untranslated Words:"));
        table.add(tl.createConstraint(2, 1), new Label(""+stats.getUntranslatedWords()));
        table.add(tl.createConstraint(3, 0), new Label("Untranslated Phrases:"));
        table.add(tl.createConstraint(3, 1), new Label(""+stats.getUntranslatedPhrases()));
        center.add(table);
        
        MultiButton openTranslationForm = new MultiButton();
        openTranslationForm.setTextLine1("Open Translation Form");
        openTranslationForm.setTextLine2("No Filters");
        openTranslationForm.setIcon(FontImage.createMaterial(FontImage.MATERIAL_EDIT, openTranslationForm.getStyle()));
        openTranslationForm.setEmblem(FontImage.createMaterial(FontImage.MATERIAL_OPEN_IN_NEW, openTranslationForm.getStyle()));
        openTranslationForm.addActionListener(e->{
            CN.execute(website.getAdminUrl()+"?-table=swete_strings&website_id="+website.getSiteId());
        });
        
        MultiButton openTranslationForm2 = new MultiButton();
        openTranslationForm2.setTextLine1("Open Translation Form");
        openTranslationForm2.setTextLine2("Filter: Untranslated Strings");
        openTranslationForm2.setIcon(FontImage.createMaterial(FontImage.MATERIAL_EDIT, openTranslationForm2.getStyle()));
        openTranslationForm2.setEmblem(FontImage.createMaterial(FontImage.MATERIAL_OPEN_IN_NEW, openTranslationForm2.getStyle()));
        openTranslationForm2.addActionListener(e->{
            CN.execute(website.getAdminUrl()+"?-table=swete_strings&website_id="+website.getSiteId()+"&normalized_translation_value=%3D");
        });
        
        MultiButton crawlSite = new MultiButton();
        crawlSite.setTextLine1("Refresh Strings");
        crawlSite.setTextLine2("Crawl the site to detect changes that require translation");
        crawlSite.setIcon(FontImage.createMaterial(FontImage.MATERIAL_AUTORENEW, crawlSite.getStyle()));
        crawlSite.addActionListener(e->{
            CN.callSerially(()->crawlSite());
        });
        
        center.add(openTranslationForm);
        center.add(openTranslationForm2);
        center.add(crawlSite);
        
        revalidateWithAnimationSafety();
        
        
    }
    
    private JobQueue getJobQueue() {
        return SweteApp.getInstance().getJobQueue();
    }

   @Override
    public void jobAdded(BackgroundJob job) {
        jobQueueProgressBar.setVisible(getJobQueue().getCurrentlyRunningJob() != null);
    }

    @Override
    public void jobRemoved(BackgroundJob job) {
        jobQueueProgressBar.setVisible(getJobQueue().getCurrentlyRunningJob() != null);
    }

    @Override
    public void jobChanged(BackgroundJob job) {
        jobQueueProgressBar.setVisible(getJobQueue().getCurrentlyRunningJob() != null);
        ;
    }
}
