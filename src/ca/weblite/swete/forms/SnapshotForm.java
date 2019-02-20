/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.forms;

import ca.weblite.shared.ui.components.HelpButton;
import ca.weblite.shared.ui.components.URLButton;
import ca.weblite.shared.ui.tools.TableBuilder;
import ca.weblite.swete.SweteApp;
import ca.weblite.swete.SweteClient;
import ca.weblite.swete.components.JobProgressBar;
import ca.weblite.swete.components.JobQueueProgressBar;
import ca.weblite.swete.components.PopupMenu;
import ca.weblite.swete.components.ScanSnapshotPagesDialog;
import ca.weblite.swete.components.SnapshotDetailsInteractionDialog;
import ca.weblite.swete.models.Snapshot;
import ca.weblite.swete.models.Snapshot.PageStatus;
import ca.weblite.swete.models.Snapshot.SnapshotPage;
import ca.weblite.swete.models.WebSite;
import ca.weblite.swete.models.WebpageStatus;
import ca.weblite.swete.services.AbstractPageCrawler;
import ca.weblite.swete.services.BackgroundJob;
import ca.weblite.swete.services.BackgroundJob.RequestStatus;
import ca.weblite.swete.services.CapturePageCrawler;
import ca.weblite.swete.services.JobQueue;
import ca.weblite.swete.services.JobQueue.JobQueueListener;
import ca.weblite.swete.util.Dispatcher;
import ca.weblite.swete.util.PageUtil;
import ca.weblite.swete.util.SnapshotUtil;
import com.codename1.components.InfiniteProgress;
import com.codename1.components.MultiButton;
import com.codename1.components.SpanLabel;
import com.codename1.components.ToastBar;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import static com.codename1.ui.CN.callSerially;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import static com.codename1.ui.layouts.BoxLayout.encloseX;
import static com.codename1.ui.layouts.BoxLayout.encloseY;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.table.TableLayout;
import com.codename1.util.DateUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author shannah
 */
public class SnapshotForm extends Form implements JobQueueListener {

   
    public interface SnapshotFormDelegate {
        public void previewPage(Snapshot snapshot, String url);
        public void refreshPageSnapshot(Snapshot snapshot, String url, Component pageRow);
        public void refreshSiteSnapshot(Snapshot snapshot);
        public void scanPage(WebSite site, String url, Component pageRow);
        public void scanPagesInSnapshot(Snapshot snapshot, Collection<WebpageStatus> pages);
        
    }
    
    private final Snapshot snapshot;
    private JobProgressBar progressBar;
    private Container center;
    private TextField search;
    private CheckBox changedOnly, unchangedOnly, incompleteOnly, completeOnly;
    private boolean updated;
    private WebpageStatus[] webpages;
    private Map<String,WebpageStatus> statusIndex = new HashMap<>();
    private static final WebpageStatus UNKNOWN_STATUS=new WebpageStatus();
    private SnapshotFormDelegate delegate = new SnapshotFormDelegateImpl();
    private JobQueueProgressBar jobQueueProgressBar;
    private Button detailsCmdButton;
    private SnapshotDetailsInteractionDialog detailsDialog;
    
    public SnapshotForm(Snapshot snapshot) {
        super(new BorderLayout());
        setToolbar(new Toolbar());
        setTitle("Snapshot "+snapshot.getSnapshotId());
        this.setEnableCursors(true);
        this.snapshot = snapshot;
        
        final Form backForm = CN.getCurrentForm();
        if (backForm != null) {
            
            getToolbar().setBackCommand(new Command("Back") {
                public void actionPerformed(ActionEvent e) {
                    backForm.showBack();
                }
            });
        }
        
        Command detailsCmd = new Command("") {
            public void actionPerformed(ActionEvent evt) {
                if (detailsDialog != null && detailsDialog.isShowing()) {
                    detailsDialog.dispose();
                    detailsDialog = null;
                    return;
                }
                SnapshotDetailsInteractionDialog dlg = new SnapshotDetailsInteractionDialog(snapshot, Arrays.asList(webpages));
                detailsDialog = dlg;
                dlg.setDelegate(new SnapshotDetailsInteractionDialog.SnapshotDetailsInteractionDialogDelegate() {
                    @Override
                    public void updateSnapshot(SnapshotDetailsInteractionDialog dlg) {
                        if (delegate != null) {
                            delegate.refreshSiteSnapshot(snapshot);
                        }
                    }

                    @Override
                    public void scanPagesForChanges(SnapshotDetailsInteractionDialog dlg) {
                        if (delegate != null) {
                            delegate.scanPagesInSnapshot(snapshot, Arrays.asList(webpages));
                        }
                    }
                });
                dlg.showPopupDialog(detailsCmdButton);
            }
        };
        getToolbar().addCommandToRightBar(detailsCmd);
        detailsCmdButton = getToolbar().findCommandComponent(detailsCmd);
        detailsCmdButton.setMaterialIcon(FontImage.MATERIAL_SETTINGS);
        
        center = new Container(BoxLayout.y());
        center.setScrollableY(true);
        
        Button refreshButton = new Button("Refresh");
        refreshButton.addActionListener(e->{
            CN.callSerially(()->refresh());
        });
        
        search = new TextField("", "Search");
        search.setUIID("SnapshotFormSearch");
        search.addDataChangedListener(new DataChangedListener() {
            @Override
            public void dataChanged(int type, int index) {
                if (!updated) {
                    // Make sure that this text field isn't responsible
                    // for the first update.
                    return;
                }
                update();
                revalidateWithAnimationSafety();
            }
            
        });
        
        changedOnly = new CheckBox("Changed Pages Only");
        changedOnly.setUIID("ChangedOnlyCheckbox");
        changedOnly.addActionListener(e->{
            update();
            revalidateWithAnimationSafety();
        });
        
        unchangedOnly = new CheckBox("Unchanged Pages Only");
        unchangedOnly.setUIID("UnchangedOnlyCheckbox");
        unchangedOnly.addActionListener(e->{
            update();
            revalidateWithAnimationSafety();
        });
        
        completeOnly = new CheckBox("Complete Pages Only");
        completeOnly.setUIID("CompleteOnlyCheckbox");
        completeOnly.addActionListener(e->{
            update();
            revalidateWithAnimationSafety();
        });
        
        incompleteOnly = new CheckBox("Incomplete Pages Only");
        incompleteOnly.setUIID("IncompleteOnlyCheckbox");
        incompleteOnly.addActionListener(e->{
            update();
            revalidateWithAnimationSafety();
        });
        
        Container searchBar = encloseY(search, FlowLayout.encloseIn(new Label("Filters:"), changedOnly, unchangedOnly, completeOnly, incompleteOnly));
        searchBar.setUIID("SearchBar");
        add(BorderLayout.NORTH, searchBar);
        add(BorderLayout.CENTER, center);
        jobQueueProgressBar = new JobQueueProgressBar(SweteApp.getInstance().getJobQueue());
        
        getJobQueue().addListener(this);
        add(BorderLayout.SOUTH, jobQueueProgressBar);
        CN.callSerially(()->refresh());
        
    }
    
    private void update() {
        updated = true;
        center.removeAll();
        /*
        TableLayout tl = new TableLayout(2, 2);
        Container table = new Container(tl);
        table.add(tl.createConstraint(0, 0), new Label("Snapshot ID"));
        table.add(tl.createConstraint(0, 1), new Label(""+snapshot.getSnapshotId()));
        table.add(tl.createConstraint(1, 0), new Label("Created"));
        table.add(tl.createConstraint(1, 1), new Label(new DateUtil().getTimeAgo(snapshot.getDateCreated())));
        table.add(tl.createConstraint(2, 0), new Label("Completed"));
        table.add(tl.createConstraint(2, 1), new Label(new DateUtil().getTimeAgo(snapshot.getDateCompleted())));
        
        center.add(table);
        
        if (snapshot.getDateCompleted() != null) {
            center.add(new Label(new DateUtil().getTimeAgo(snapshot.getDateCompleted()), "SnapshotFormDate"));
        } else if (snapshot.getDateCreated() != null) {
            center.add(new Label(new DateUtil().getTimeAgo(snapshot.getDateCreated()), "SnapshotFormDate"));
        }
        */
        center.add(new SpanLabel("This form allows you to manage the pages in this snapshot."));
        
        //center.add(new Label("Pages", "SnapshotFormHeading"));
        List<SnapshotPage> pages = snapshot.getPages();
        if (pages != null && !pages.isEmpty()) {
            for (SnapshotPage page : pages) {
                if (page.getPage().toLowerCase().indexOf(search.getText().toLowerCase()) < 0) {
                    continue;
                    
                }
                WebpageStatus lastCrawl = statusIndex.get(proxify(page.getPage()));
                if (lastCrawl == null) lastCrawl = UNKNOWN_STATUS;
                if (changedOnly.isSelected() && (lastCrawl.getTranslationsChecksum() == null ? page.getTranslationsChecksum() == null : lastCrawl.getTranslationsChecksum().equals(page.getTranslationsChecksum()))) {
                    continue;
                }
                if (unchangedOnly.isSelected() && (lastCrawl.getTranslationsChecksum() == null ? page.getTranslationsChecksum() != null : !lastCrawl.getTranslationsChecksum().equals(page.getTranslationsChecksum()))) {
                    continue;
                }
                boolean isMoved = (lastCrawl.getResponseCode() >= 300 && lastCrawl.getResponseCode() < 400);
                if (completeOnly.isSelected() && (isMoved || lastCrawl == UNKNOWN_STATUS || lastCrawl.getNumUntranslatedStrings() > 0) ) {
                    
                    continue;
                }
                
                if (incompleteOnly.isSelected() && (lastCrawl != UNKNOWN_STATUS && lastCrawl.getNumUntranslatedStrings() == 0) ) {
                    continue;
                }
                
                
                
                center.add(createPageRow(page));
            }
        } else {
            center.add(new SpanLabel("This snapshot does not contain any pages"));
        }
        
        $("*", this).filter(c->{
            return c instanceof Button;
        }).setCursor(Component.HAND_CURSOR);
        
        //revalidateWithAnimationSafety();
        
    }
    
    private Component findPageRow(String url) {
        return $("*", this).filter(c->{
            return url.equals(c.getClientProperty("proxyUrl"));
        }).asComponent();
    }
    
    private Component createPageRow(SnapshotPage page) {
        String proxyUrl = snapshot.getWebSite().getProxyUrlForPage(page.getPage());
        Container out = new Container(new BorderLayout(), "SnapshotFormPageRow");
        out.putClientProperty("proxyUrl", proxyUrl);
        Button name = new Button(page.getPage(), "SnapshotFormPageRowName");
        Container north = new Container(new BorderLayout());
        Container statusLabelsContainer = new Container(BoxLayout.x());
        Button unscannedStatus = new Button("Unscanned", "UnscannedStatusButton");
        unscannedStatus.setMaterialIcon(FontImage.MATERIAL_WARNING);
        Button changedButton = new Button("Changed", "ChangedStatusButton");
        changedButton.setMaterialIcon(FontImage.MATERIAL_COMPARE);
        Button currentButton = new Button("Unchanged", "UnchangedStatusButton");
        currentButton.setMaterialIcon(FontImage.MATERIAL_CHECK);
        Button completeButton = new Button("Complete", "CompleteStatusButton");
        completeButton.setMaterialIcon(FontImage.MATERIAL_CHECK);
        Button incompleteButton = new Button("Incomplete", "IncompleteStatusButton");
        incompleteButton.setMaterialIcon(FontImage.MATERIAL_TRANSLATE);
        Button movedButton = new Button("Moved", "MovedStatusButton");
        movedButton.setMaterialIcon(FontImage.MATERIAL_LINK);
        
        
        Label expandRetract = new Label();
        expandRetract.setMaterialIcon(FontImage.MATERIAL_KEYBOARD_ARROW_DOWN);
        north.add(BorderLayout.EAST, expandRetract);
        north.add(BorderLayout.CENTER, BorderLayout.centerEastWest(name, statusLabelsContainer, null));
        north.setLeadComponent(name);
        
        Label responseCode = new Label("", "DarkValue");
        Label lastUpdate = new Label("", "DarkValue");
        if (page.getStatus() != null) {
            PageStatus status = page.getStatus();
            if (status.getStatusCode() <= 0) {
                responseCode.setText("Queued");
            } else {
                responseCode.setText("Response code: "+status.getStatusCode());
                if (status.getTimestamp() != null) {
                    lastUpdate.setText(new DateUtil().getTimeAgo(status.getTimestamp()));
                }
            }
            
        }
        
        Container snapshotCnt = new Container(new BorderLayout(), "SnapshotDetailsCnt");
        
        snapshotCnt.add(BorderLayout.NORTH, new Label("Snapshot info:", "DarkH3"));
        Container snapshotDetails = new TableBuilder()
                .append(new Label("Created", "DarkLabel"))
                .append(lastUpdate).newRow()
                .append(new Label("Response Code", "DarkLabel"))
                .append(responseCode).newRow()
                .append(new Label("Checksum", "DarkLabel")).append(new Label(String.valueOf(page.getTranslationsChecksum()), "DarkValue"))
                .build();
        
        Button previewBtn = new Button("Preview", "SnapshotFormRowButton");
        previewBtn.addActionListener(e->{
            if (delegate != null) {
                delegate.previewPage(snapshot, proxyUrl);
            }
        });
        
        Button refreshPageSnap = new Button("Update", "SnapshotFormRowButton");
        refreshPageSnap.addActionListener(e->{
            if (delegate != null) {
                refreshPageSnap.getParent().replace(refreshPageSnap, new InfiniteProgress(), null);
                out.revalidateWithAnimationSafety();
                delegate.refreshPageSnapshot(snapshot, proxyUrl, out);
            }
        });
        
        Container buttonsCnt = FlowLayout.encloseCenter(
                encloseX(previewBtn, new HelpButton("Show the snapshotted version of this page.")), 
                encloseX(refreshPageSnap, new HelpButton("Update the snapshot of this page with the latest content and translations.")));
        
        snapshotCnt.add(BorderLayout.CENTER, BoxLayout.encloseY(snapshotDetails, buttonsCnt));
        
        WebpageStatus lastCrawl = statusIndex.get(proxyUrl);
        Container lastCrawlCnt = new Container(new BorderLayout(), "LastCrawlCnt");
        if (lastCrawl != null) {
            
            lastCrawlCnt.add(BorderLayout.NORTH, new Label("Last crawl info:", "DarkH3"));
            Date lastChange = DateUtil.max(lastCrawl.getLastOutputContentChange(), lastCrawl.getLastResponseBodyChange());
            Container lastCrawlDetails = new TableBuilder()
                    .append(new Label("Date", "DarkLabel"))
                    .append(new Label(new DateUtil().getTimeAgo(lastCrawl.getLastChecked()), "DarkValue"))
                    .newRow()
                    .append(new Label("Response Code", "DarkLabel"))
                    .append(new Label(String.valueOf(lastCrawl.getResponseCode()), "DarkValue"))
                    .newRow()
                    .append(new Label("Last Change", "DarkLabel"))
                    .append(new Label(new DateUtil().getTimeAgo(lastCrawl.getLastTranslationsChange()), "DarkValue"))
                    .newRow()
                    .append(new Label("Checksum", "DarkLabel"))
                    .append(new Label(lastCrawl.getTranslationsChecksum(), "DarkValue"))
                    .newRow()
                    .append(new Label("Untranslated strings", "DarkLabel"))
                    .append(encloseX(
                            new Label(String.valueOf(lastCrawl.getNumUntranslatedStrings()), "DarkValue"),
                            new URLButton(lastCrawl.getUntranslatedStringsURL(), "Edit Translations", "SnapshotFormRowButton")
                    ))
                    .build();
            Button crawlPreviewBtn = new Button("Preview", "SnapshotFormRowButton");
            crawlPreviewBtn.addActionListener(e->{
                if (delegate != null) {
                    delegate.previewPage((Snapshot)null, proxyUrl);
                }
            });
            
            Button recrawl = new Button("Scan for Changes", "SnapshotFormRowButton");
            recrawl.addActionListener(e->{
                if (delegate != null) {
                    recrawl.getParent().replace(recrawl, new InfiniteProgress(), null);
                    out.revalidateWithAnimationSafety();
                    delegate.scanPage(snapshot.getWebSite(), proxyUrl, out);
                }
            });
            
            Container btns = FlowLayout.encloseCenter(
                    encloseX(crawlPreviewBtn, new HelpButton("Preview this page with the latest changes and translations applied.")) ,
                    encloseX(recrawl, new HelpButton("Crawl this page and load changes.")));
            
            lastCrawlCnt.add(BorderLayout.CENTER, BoxLayout.encloseY(lastCrawlDetails, btns));
            
            if (lastCrawl.getResponseCode() >= 300 && lastCrawl.getResponseCode() < 400) {
                statusLabelsContainer.add(movedButton);
            } else {
                if (Objects.equals(lastCrawl.getTranslationsChecksum(), page.getTranslationsChecksum())) {
                    statusLabelsContainer.add(currentButton);
                } else {
                    statusLabelsContainer.add(changedButton);
                }
                if (lastCrawl.getNumUntranslatedStrings() == 0) {
                    statusLabelsContainer.add(completeButton);
                } else {
                    statusLabelsContainer.add(incompleteButton);
                }
            }
        
        } else {
            statusLabelsContainer.add(unscannedStatus);
            
        }
        out.add(BorderLayout.NORTH, north);
        Container grid = GridLayout.encloseIn(2, snapshotCnt, lastCrawlCnt);
        $(grid).addTags("grid");
        grid.setUIID("SnapshotRowContentPane");
        name.addActionListener(e->{
            if (!grid.isHidden()) {
                expandRetract.setMaterialIcon(FontImage.MATERIAL_KEYBOARD_ARROW_DOWN);
            } else {
                expandRetract.setMaterialIcon(FontImage.MATERIAL_KEYBOARD_ARROW_UP);
            }
            if (!grid.isHidden()) {
                //int h = grid.getHeight();
                //grid.setHeight(0);
                //Component p = grid.getParent();
                //while (p != null && p != SnapshotForm.this) {
                //    p.setHeight(Math.max(0, p.getHeight() - h));
                //    p = p.getParent();
                //}
                //out.animateUnlayoutAndWait(500, 255);
                grid.setHidden(true);
                out.revalidateWithAnimationSafety();
            } else {
                //Dimension prefSize = grid.getPreferredSize();
                //int h = prefSize.getHeight();
                //grid.setHeight(prefSize.getHeight());
                //Component p = grid.getParent();
                //while (p != null && p != SnapshotForm.this) {
                //    p.setHeight(p.getHeight() + h);
                //    p = p.getParent();
                //}
                //out.animateUnlayoutAndWait(500, 255);
                grid.setHidden(false);
                out.revalidateWithAnimationSafety();
            }
            //grid.setHidden(!grid.isHidden());
            //if (grid.isHidden()) {
            //    $(grid).slideDown();
            //} else {
            //    $(grid).slideUp();
            //}
            //grid.getParent().animateLayout(500);
        });
        grid.setHidden(true);
        
        out.add(BorderLayout.CENTER,
               grid
        );
        
        return out;
    }
    
    
   
    private void refresh() {
        try {
            center.removeAll();
            center.add(new InfiniteProgress());
            center.revalidateWithAnimationSafety();
            SweteClient client = new SweteClient(snapshot.getWebSite());
            client.load(snapshot);
            for (SnapshotPage page : snapshot.getPages()) {
                statusIndex.put(snapshot.getWebSite().getProxyUrlForPage(page.getPage()), UNKNOWN_STATUS);
            }
            webpages = client.loadWebpageStatuses();
            for (WebpageStatus status : webpages) {
                if (statusIndex.containsKey(status.getUrl())) {
                    statusIndex.put(status.getUrl(), status);
                }
            }
            
            update();
            revalidateWithAnimationSafety();
            
        } catch (Throwable t) {
            Log.e(t);
            ToastBar.showErrorMessage(t.getMessage());
        }
    }
    
    private void refreshWithNoUIStuff(String url) throws IOException {
        SweteClient client = new SweteClient(snapshot.getWebSite());
        client.load(snapshot);
        WebpageStatus status = client.loadWebpageStatus(url);
        if (status != null) {
            statusIndex.put(url, status);
        }
    }
    
    private String proxify(String url) {
        return snapshot.getWebSite().getProxyUrlForPage(url);
    }
    
    private SnapshotPage findSnapshotPage(String url) {
        url = proxify(url);
        for (SnapshotPage pg : snapshot.getPages()) {
            
            if (proxify(pg.getPage()).equals(url)) {
                return pg;
            }
        }
        return null;
        
    }
    
    private Component getReplacement(Component cmp) {
        if (cmp.getClientProperty("replacedBy") != null) {
            return getReplacement((Component)cmp.getClientProperty("replacedBy"));
        }
        return cmp;
    }
    
    private void updatePageRow(Component pageRow, String url) {
        pageRow = getReplacement(pageRow);
        SnapshotPage pg = findSnapshotPage(url);
        if (pg != null) {
            Component newRow = createPageRow(pg);
            pageRow.putClientProperty("replacedBy", newRow);
            Container parent = pageRow.getParent();
            if (parent == null) {
                return;
            }
            $("grid", newRow).setHidden($("grid", pageRow).isHidden());
            parent.replace(pageRow, newRow, null);
            if (pageRow instanceof Container) {
                ((Container)pageRow).revalidateWithAnimationSafety();
            }
        }
        
    }
    
    
    private class SnapshotFormDelegateImpl implements SnapshotFormDelegate {

        @Override
        public void previewPage(Snapshot snapshot, String url) {
            Dispatcher.previewPage(
                    SnapshotForm.this.snapshot.getWebSite(), 
                    snapshot, 
                    url
            );
        }

        @Override
        public void refreshPageSnapshot(Snapshot snapshot, String url, Component pageRow) {
            if (snapshot == null) {
                snapshot = SnapshotForm.this.snapshot;
            }
            Command start = new Command("Start");
            Command cancel = new Command("Cancel");
            SpanLabel msg = new SpanLabel("Click 'Start' to refresh "+url+" in this snapshot.");
            String message = "Refresh Page Snapshot";
            if (start == Dialog.show(message, msg, start, cancel)) {
                Log.p("Adding "+url+" to be updated in snapshot");
                SnapshotUtil.recrawlPageSnapshotAddToQueue(snapshot, url, ()->{
                    try {
                        refreshWithNoUIStuff(url);
                    } catch (Throwable t) {
                        Log.e(t);
                    }
                    updatePageRow(pageRow, url);
                });
            }
        }

        @Override
        public void refreshSiteSnapshot(Snapshot snapshot) {
            if (snapshot == null) {
                snapshot = SnapshotForm.this.snapshot;
            }
            Command start = new Command("Start");
            Command cancel = new Command("Cancel");
            SpanLabel msg = new SpanLabel("Click 'Start' to refresh this snapshot.  This will re-snapshot all of the pages in this snapshot.");
            String message = "Refresh Snapshot "+snapshot.getSnapshotId();
            if (start == Dialog.show(message, msg, start, cancel)) {
                SnapshotUtil.recrawlSnapshot(snapshot);
            }
            
        }

        @Override
        public void scanPage(WebSite site, String url, Component pageRow) {
            PageUtil.crawlPageAddToQueue(site, url, ()->{
                try {
                    refreshWithNoUIStuff(url);
                } catch (Throwable t) {
                    Log.e(t);
                    ToastBar.showErrorMessage("Failed to refresh form after crawl: "+t.getMessage());
                }
                updatePageRow(pageRow, url);
                
            });
                
        }

        @Override
        public void scanPagesInSnapshot(Snapshot snapshot, Collection<WebpageStatus> pages) {
            ScanSnapshotPagesDialog dlg = new ScanSnapshotPagesDialog(snapshot, pages);
            if (dlg.showScanDialog()) {
                PageUtil.crawlPagesAddToQueue(snapshot.getWebSite(), dlg.getURLsToScan(), null);
            }
        }
        
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

    private WebpageStatus findStatusForUrl(String url) {
        for (WebpageStatus page : webpages) {
            if (url.equals(page.getUrl())) {
                return page;
            }
        }
        return null;
    }
    
    private int findStatusIndexForUrl(String url) {
        if (webpages == null) {
            return -1;
        }
        int len = webpages.length;
        for (int i=0; i < len; i++) {
            if (webpages[i] == null) {
                continue;
            }
            if (url.equals(webpages[i].getUrl())) {
                return i;
            }
        }
        return -1;
    }
    
    private void refreshPageStatus(String url) {
        if (webpages == null) {
            return;
        }
        System.out.println("Refreshing page status for "+url);
        
        Component pageRow = findPageRow(url);
        SweteClient client = new SweteClient(snapshot.getWebSite());
        try {
            refreshWithNoUIStuff(url); // To refresh the snapshot page status
            WebpageStatus ws = client.loadWebpageStatus(url);
            if (ws != null) {
                int oldStatusIndex = findStatusIndexForUrl(url);

                if (oldStatusIndex != -1) {
                    webpages[oldStatusIndex] = ws;
                } else {
                    WebpageStatus[] newPages = new WebpageStatus[webpages.length+1];
                    System.arraycopy(webpages, 0, newPages, 0, webpages.length);
                    newPages[newPages.length-1] = ws;
                    webpages = newPages;
                }
                statusIndex.put(url, ws);
                if (pageRow != null) {
                    updatePageRow(pageRow, url);
                }
                revalidateWithAnimationSafety();
            }
        } catch (Throwable t) {
            Log.e(t);
        }
        
        
    }
    
    @Override
    public void jobChanged(BackgroundJob job) {
        if (job instanceof AbstractPageCrawler) {
            AbstractPageCrawler crawler = (AbstractPageCrawler)job;
            RequestStatus currentRequest = crawler.getCurrentRequest();
            if (currentRequest != null && currentRequest.isComplete()) {
                String url = currentRequest.getProxyUrl();
                
                callSerially(()->{
                    refreshPageStatus(url);
                });
                
            }
            
            
        }
        jobQueueProgressBar.setVisible(getJobQueue().getCurrentlyRunningJob() != null);
        ;
    }
}
