/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.onsen.controllers;

import ca.weblite.swete.SweteApp;
import ca.weblite.swete.SweteClient;
import ca.weblite.swete.models.TranslationStats;
import ca.weblite.swete.models.WebSite;
import ca.weblite.swete.services.BackgroundJob;
import ca.weblite.swete.services.CapturePageCrawler;
import ca.weblite.swete.services.JobQueue;
import ca.weblite.swete.services.JobQueue.JobQueueListener;
import com.codename1.io.Log;
import com.codename1.onsen.OnsApplication;
import com.codename1.onsen.OnsNotification;
import com.codename1.onsen.components.AbstractElement;
import com.codename1.onsen.components.OnsButton;
import com.codename1.onsen.components.OnsPage;
import com.codename1.onsen.components.OnsProgressBar;
import com.codename1.ui.CN;
import java.io.IOException;

/**
 *
 * @author shannah
 */
public class StringsPageController implements JobQueueListener {
    private final OnsApplication app;
    private OnsPage page;
    private final WebSite website;
    private final AbstractElement totalWords, totalPhrases, untranslatedWords, untranslatedPhrases, translatedWords, translatedPhrases,
            captureProgressCurrentPage, captureProgressTotalPages
            ;
    private final AbstractElement translationStats, captureStringsActive, captureStringsInactive;
    private final OnsButton showTranslationFormAllButton, showTranslationFormUntranslatedButton, startCaptureButton, cancelButton;
    private final OnsProgressBar progressBar;
    
    public StringsPageController(OnsApplication app, WebSite website) {
        this.app = app;
        this.website = website;
        
        try {
            this.page = app.load(OnsPage.class, "/StringsPage.html");
        } catch (IOException ex) {
            Log.e(ex);
            throw new RuntimeException(ex);
        }
   
        
        translationStats = page.getElementById("translation-stats");
        captureStringsActive = page.getElementById("capture-strings-active");
        captureStringsInactive = page.getElementById("capture-strings-inactive");
        captureProgressCurrentPage = page.getElementById("capture-progress-current-page");
        captureProgressTotalPages = page.getElementById("capture-progress-total-pages");
        progressBar = (OnsProgressBar) page.getElementById("capture-progress-bar");
        cancelButton = (OnsButton) page.getElementById("capture-cancel");
        
        totalWords = page.getElementById("total-words");
        totalPhrases = page.getElementById("total-phrases");
        untranslatedPhrases = page.getElementById("untranslated-phrases");
        untranslatedWords = page.getElementById("untranslated-words");
        translatedPhrases = page.getElementById("translated-phrases");
        translatedWords = page.getElementById("translated-words");
        showTranslationFormAllButton = (OnsButton)page.getElementById("open-translation-form-all");
        showTranslationFormUntranslatedButton = (OnsButton)page.getElementById("open-translation-form-untranslated");
        startCaptureButton = (OnsButton)page.getElementById("start-capture");
        
        showTranslationFormAllButton.addClickListener(e->{
            CN.execute(website.getAdminUrl()+"?-table=swete_strings&website_id="+website.getSiteId());
        });
        
        showTranslationFormUntranslatedButton.addClickListener(e->{
            CN.execute(website.getAdminUrl()+"?-table=swete_strings&website_id="+website.getSiteId()+"&normalized_translation_value=%3D");
        });
        
        startCaptureButton.addClickListener(e->{
            CN.callSerially(()->crawlSite());
        });
        
        cancelButton.addClickListener(e->{
            CapturePageCrawler activeJob = getActivePageCrawlJob();
            if (activeJob != null) {
                activeJob.cancel();
            }
        });
        
        page.addShowListener(e-> {
            getJobQueue().addListener(this);
            refresh();
        });
        
        page.addHideListener(e->{
            getJobQueue().removeListener(this);
        });
    }
    
    private void refresh() {
        Log.p("Refreshing");
        CN.callSerially(()->{
            Log.p("About to load translation stats");
            SweteClient client = new SweteClient(website);
            try {
                client.loadTranslationStats();
            } catch (IOException ex) {
                Log.e(ex);
                OnsNotification.toast(app, "Failed to refresh: "+ex.getMessage(), res -> {

                });
                return;
            }
            app.getContext().runLater(()->update());
        });

    }
    
    private void update() {
        if (!app.getContext().isMainThread()) {
            app.getContext().runLater(()->update());
            return;
        }
        TranslationStats stats = website.getTranslationStats();
        if (stats == null) {
            translationStats.hide();
            return;
        } 
        translationStats.show();
        totalWords.setInnerText(stats.getTotalWords()+"");
        totalPhrases.setInnerText(stats.getTotalPhrases()+"");
        untranslatedPhrases.setInnerText(stats.getUntranslatedPhrases()+"");
        untranslatedWords.setInnerText(stats.getUntranslatedWords()+"");
        translatedPhrases.setInnerText((stats.getTotalPhrases() - stats.getUntranslatedPhrases())+"");
        translatedWords.setInnerText((stats.getTotalWords() - stats.getUntranslatedWords())+"");
        
    }
    
    private void updateJobProgress() {
        if (!app.getContext().isMainThread()) {
            app.getContext().runLater(()->updateJobProgress());
            return;
        }
        CapturePageCrawler activeJob = getActivePageCrawlJob();
        if (activeJob == null) {
            captureStringsActive.hide();
            captureStringsInactive.show();
            return;
        } else {
            captureStringsActive.show();
            captureStringsInactive.hide();
        }
        
        captureProgressCurrentPage.setInnerText(String.valueOf(activeJob.getComplete()+1));
        captureProgressTotalPages.setInnerText(String.valueOf(activeJob.getTotal()));
        progressBar.setValue(activeJob.getProgressPercent());
        
        
    }
    
    
    private void crawlSite() {
        CapturePageCrawler crawler = new CapturePageCrawler(website);
        crawler.setJobDescription("Looking for changes in "+website.getName());
        getJobQueue().add(crawler);
        
    }
    
    private JobQueue getJobQueue() {
        return SweteApp.getInstance().getJobQueue();
    }
    
    private CapturePageCrawler getActivePageCrawlJob() {
        for (BackgroundJob job : getJobQueue().getJobs()) {
            if (job instanceof CapturePageCrawler && !job.isCancelled() && !job.isComplete()) {
                return (CapturePageCrawler) job;
            }
        }
        return null;
    }

    @Override
    public void jobAdded(BackgroundJob job) {
        app.getContext().runLater(()->updateJobProgress());
    }

    @Override
    public void jobRemoved(BackgroundJob job) {
        app.getContext().runLater(()->updateJobProgress());
    }

    @Override
    public void jobChanged(BackgroundJob job) {
        app.getContext().runLater(()->updateJobProgress());

    }
    
    
    public OnsPage getPage() {
        return page;
    }
}
