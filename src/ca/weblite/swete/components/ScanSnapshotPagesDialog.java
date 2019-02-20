/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.components;

import ca.weblite.swete.models.Snapshot;
import ca.weblite.swete.models.Snapshot.SnapshotStats;
import ca.weblite.swete.models.WebpageStatus;
import ca.weblite.swete.services.CapturePageCrawler;
import ca.weblite.swete.util.PageUtil;
import com.codename1.components.SpanLabel;
import com.codename1.ui.CN;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.events.SelectionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.list.DefaultListModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author shannah
 */
public class ScanSnapshotPagesDialog extends Dialog {
    private final List<WebpageStatus> pages;
    private final Snapshot snapshot;
    private final SpanLabel description = new SpanLabel();
    private final ComboBox<String> scanOptions = new ComboBox<>();
    private final Command startScanCommand = new Command("Start Scan");
    private final Command cancelCommand = new Command("Cancel");
    
    
    
    public ScanSnapshotPagesDialog(Snapshot snapshot, Collection<WebpageStatus> pages) {
        this.snapshot = snapshot;
        this.pages = new ArrayList<>(pages);
        setCommandsAsButtons(true);
        placeButtonCommands(new Command[]{startScanCommand, cancelCommand});
        //addCommand(startScanCommand);
        //addCommand(cancelCommand);
        
        setBackCommand(cancelCommand);
        setDefaultCommand(startScanCommand);
        setTitle("Scan Site");
        initUI();
        update();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        Container wrapper = new Container(BoxLayout.y());
        wrapper.setScrollableY(false);
        wrapper.setPreferredW(CN.convertToPixels(100f));
        wrapper.setPreferredH(CN.convertToPixels(100f));
        description.setText("Scan the pages of this snapshot.  This will crawl the pages of the site one at a time, scanning them for changes.  This will take some time (approx 2-3 seconds per page).");
        wrapper.add(description);
        
        SnapshotStats stats = snapshot.calculateStats(pages);
        List<String> opts = new ArrayList<>();
        opts.add("Full Scan ("+stats.getNumPages()+" pages)");
        if (stats.getNumMissingCurrentVersion() > 0) {
            opts.add("Unscanned pages only ("+stats.getNumMissingCurrentVersion()+" pages)");
        }
        opts.add("Non-recently-scanned pages only ("+stats.getNumNotRecentlyScanned()+")");
        
        scanOptions.setModel(new DefaultListModel(opts));
        
        wrapper.add(FlowLayout.encloseCenter(scanOptions));
        
        
        
        add(BorderLayout.CENTER, wrapper);
        
        
    }
    
    private void update() {
        
        
        
        
    }
    
    public String[] getURLsToScan() {
        int msize = scanOptions.getModel().getSize();
        int sel = scanOptions.getSelectedIndex();
        if (sel == 0) {
            // Full Scan
            return WebpageStatus.getURLs(pages);
            
        }
        if (msize > 2 && sel == 1) {
            // Unscanned pages only
            
            SnapshotStats stats = snapshot.calculateStats(pages);
            return stats.getUrlsMissingCurrentVersion();
        }
        if (msize == 2 && sel == 1 || msize > 2 && sel == 2) {
            // Not-recently scanned pages ony
            SnapshotStats stats = snapshot.calculateStats(pages);
            return stats.getNonRecentlyScannedUrls();
            
        }
        return new String[0];
    }
    
    
    
    public boolean showScanDialog() {
        Command res = this.showDialog();
        return res == startScanCommand;
    }

    
    
    
}
