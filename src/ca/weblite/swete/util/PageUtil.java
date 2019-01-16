/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.util;

import ca.weblite.swete.SweteApp;
import ca.weblite.swete.SweteClient;
import ca.weblite.swete.models.WebSite;
import ca.weblite.swete.models.WebpageStatus;
import ca.weblite.swete.services.CapturePageCrawler;
import com.codename1.components.InfiniteProgress;
import com.codename1.components.ToastBar;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.Log;
import com.codename1.ui.CN;
import com.codename1.ui.Dialog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author shannah
 */
public class PageUtil {
    
    public static void crawlPageAddToQueue(WebSite site, String url, final Runnable onComplete) {
        CapturePageCrawler crawler = new CapturePageCrawler(site) {
            @Override
            protected void onComplete() {
                super.onComplete();
                CN.callSerially(onComplete);
            }
        };
        crawler.setJobDescription("Scanning "+url);
        crawler.setURLs(new String[]{url});
        SweteApp.getInstance().getJobQueue().add(crawler);
    }
    
    public static void crawlPagesAddToQueue(WebSite site, Collection<WebpageStatus> pages, final Runnable onComplete) {
        List<String> urls = new ArrayList<>();
        for (WebpageStatus s : pages) {
            urls.add(s.getUrl());
        }
        crawlPagesAddToQueue(site, urls.toArray(new String[urls.size()]), onComplete);
    }
    
    public static void crawlPagesAddToQueue(WebSite site, String[] urls, final Runnable onComplete) {
        CapturePageCrawler crawler = new CapturePageCrawler(site) {
            @Override
            protected void onComplete() {
                super.onComplete();
                if (onComplete != null) {
                    CN.callSerially(onComplete);
                }
            }
        };
        crawler.setJobDescription("Scanning "+urls.length+" pages");
        crawler.setURLs(urls);
        SweteApp.getInstance().getJobQueue().add(crawler);
    }
    
    public static void crawlSiteAddToQueue(WebSite site, final Runnable onComplete) {
        CapturePageCrawler crawler = new CapturePageCrawler(site) {
            @Override
            protected void onComplete() {
                super.onComplete();
                CN.callSerially(onComplete);
            }
        };
        crawler.setJobDescription("Scanning site "+site.getName());
        SweteApp.getInstance().getJobQueue().add(crawler);
    }
    
    
    public static boolean crawlPageInfiniteBlocking(WebSite site, String url) {
        SweteClient client = new SweteClient(site);
        InfiniteProgress progress = new InfiniteProgress();
        Dialog blocking = progress.showInfiniteBlocking();
        
        
        boolean success = false;
        try {
            Log.p("Crawling page");
            ConnectionRequest req = client.requestPageWithCapturing(url);
            Log.p("Connection finished");
            success = req.getResponseCode() >= 200 && req.getResponseCode() < 300;
            Log.p("Success "+success);
        } finally {

            blocking.dispose();
        }
        
        return success;
    }
}
