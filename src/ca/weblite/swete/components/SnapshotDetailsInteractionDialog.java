/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.components;

import ca.weblite.shared.ui.components.HelpButton;
import ca.weblite.swete.models.Models;
import ca.weblite.swete.models.Snapshot;
import ca.weblite.swete.models.Snapshot.SnapshotStats;
import ca.weblite.swete.models.WebpageStatus;
import com.codename1.components.InteractionDialog;
import com.codename1.components.SpanLabel;
import com.codename1.ui.Button;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.Slider;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import static com.codename1.ui.layouts.BoxLayout.encloseX;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.util.DateUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author shannah
 */
public class SnapshotDetailsInteractionDialog extends InteractionDialog {

    /**
     * @return the delegate
     */
    public SnapshotDetailsInteractionDialogDelegate getDelegate() {
        return delegate;
    }

    /**
     * @param delegate the delegate to set
     */
    public void setDelegate(SnapshotDetailsInteractionDialogDelegate delegate) {
        this.delegate = delegate;
    }
    
    public static interface SnapshotDetailsInteractionDialogDelegate {
        public void updateSnapshot(SnapshotDetailsInteractionDialog dlg);
        public void scanPagesForChanges(SnapshotDetailsInteractionDialog dlg);
    }
    
    public SnapshotDetailsInteractionDialog(
            Snapshot snapshot, 
            Collection<WebpageStatus> webpages) {
        this.snapshot = snapshot;
        this.pages = new ArrayList<>(webpages);
        setTitle("Snapshot "+snapshot.getSnapshotId()+" Details");
        initUI();
        update();
        setDisposeWhenPointerOutOfBounds(true);
        
    }
    
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        Container wrapper = new Container(BoxLayout.y());
        this.normalWrapper = wrapper;
        Container percentCurrentCnt = new Container(BoxLayout.y());
        Container cnt = percentCurrentCnt;
        Label percentCurrentLbl = new Label("percent current");
        cnt.add(encloseX(percentCurrent, percentCurrentLbl, percentCurrentHelpButton));
        cnt.add(percentCurrentSlider);
        wrapper.add(cnt);
        
        Container percentCompleteCnt = new Container(BoxLayout.y());
        cnt = percentCompleteCnt;
        Label percentCompleteLbl = new Label("percent complete");
        cnt.add(encloseX(percentComplete,percentCompleteLbl, percentCompleteHelpButton));
        cnt.add(percentCompleteSlider);
        cnt.add(FlowLayout.encloseCenter(snapshotBtn));
        
        wrapper.add(cnt);
        
        Container lastScanCnt = new Container(BoxLayout.y());
        cnt = lastScanCnt;
        cnt.add(BorderLayout.centerEastWest(lastScanLabel, lastScanHelpButton, null));
        cnt.add(FlowLayout.encloseCenter(scanBtn));
        
        wrapper.add(cnt);
        
        snapshotBtn.addActionListener(e->{
            if (getDelegate() != null) {
                getDelegate().updateSnapshot(this);
            }
        });
        
        scanBtn.addActionListener(e->{
            if (getDelegate() != null) {
                getDelegate().scanPagesForChanges(this);
            }
        });
        
        
        
        
        add(BorderLayout.CENTER, wrapper);
        
        // Now create the incomplete scan wrapper which is shown
        // if some of the pages haven't been scanned.
        wrapper = new Container(BoxLayout.y());
        incompleteScanWrapper = wrapper;
        
        scanForChanges2.addActionListener(e->{
            if (delegate != null) {
                delegate.scanPagesForChanges(this);
            }
        });
        wrapper.add(incompleteScanLabel).add(FlowLayout.encloseCenter(scanForChanges2));
        
    }
    
    private void update() {
        SnapshotStats stats = snapshot.calculateStats(pages);
        int pctCurrent = (int)Math.round(stats.getPercentCurrent());
        int pctComplete = (int)Math.round(stats.getPercentComplete());
        
        if (stats.getNumMissingCurrentVersion() > 0) {
            incompleteScanLabel.setText(stats.getNumMissingCurrentVersion()+" pages have not been scanned yet.  You can't properly manage the snapshot until you have scanned all of the pages in the site.");
            // Not all of the pages have been scanned.
            if (contains(normalWrapper)) {
                normalWrapper.remove();
                add(BorderLayout.CENTER, incompleteScanWrapper);
            }
        } else {
            if (contains(incompleteScanWrapper)) {
                incompleteScanWrapper.remove();
                add(BorderLayout.CENTER, normalWrapper);
            }
        }
        
        percentComplete.setText(String.valueOf(pctComplete));
        percentCurrent.setText(String.valueOf(pctCurrent));
        percentCompleteSlider.setProgress(pctComplete);
        percentCurrentSlider.setProgress(pctCurrent);
        WebpageStatus oldestScan = Models.getMinByLastChecked(pages);
        if (oldestScan == null || oldestScan.getLastChecked() == null) {
            lastScanLabel.setText("Site has not been scanned yet");
        } else {
            lastScanLabel.setText("Site was last scanned "+new DateUtil().getTimeAgo(oldestScan.getLastChecked()));
        }
        
        
        percentCompleteHelpButton.setHelpText(stats.getNumComplete()+" of "+stats.getNumNonCurrent()+" pages are fully translated and ready to be added to the snapshot");
        percentCurrentHelpButton.setHelpText(stats.getNumNonCurrent()+" of "+stats.getNumActivePages()+" have changed since this snapshot.");
        //lastScanHelpButton.setHelpText("Scanning the site involves loading the current source version");
       
        //revalidateWithAnimationSafety();
    }
    
    private Container normalWrapper;
    private Container incompleteScanWrapper;
    
    private SpanLabel incompleteScanLabel = new SpanLabel();
    private Button scanForChanges2 = new Button("Start Scan Now");
    
    private Snapshot snapshot;
    private SnapshotDetailsInteractionDialogDelegate delegate;
    private List<WebpageStatus> pages;
    private Label heading = new Label("Snapshot Details");

    private Label percentCurrent = new Label();
    private HelpButton percentCurrentHelpButton = new HelpButton("");
    private Slider percentCurrentSlider = new Slider();
    
    
    private Label percentComplete = new Label();
    private HelpButton percentCompleteHelpButton = new HelpButton("");
    private Slider percentCompleteSlider = new Slider();
    private Button snapshotBtn = new Button("Update Snapshot");
    
    
    private SpanLabel lastScanLabel = new SpanLabel();
    private HelpButton lastScanHelpButton = new HelpButton("");
    
    

    private Button scanBtn = new Button("Scan for Changes");
    
    
    private Button refreshBtn = new Button("Update Snapshot");
    
}
