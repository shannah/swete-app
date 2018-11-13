/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.forms;

import ca.weblite.swete.SweteApp;
import ca.weblite.swete.SweteClient;
import ca.weblite.swete.components.JobQueueProgressBar;
import ca.weblite.swete.models.Snapshot;
import ca.weblite.swete.models.WebSite;
import ca.weblite.swete.services.BackgroundJob;
import ca.weblite.swete.services.JobQueue;
import ca.weblite.swete.services.JobQueue.JobQueueListener;
import ca.weblite.swete.services.RefreshSnapshotPageCrawler;
import com.codename1.components.InfiniteProgress;
import com.codename1.components.InteractionDialog;
import com.codename1.components.ToastBar;
import com.codename1.components.ToastBar.Status;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Slider;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.table.TableLayout;
import com.codename1.util.DateUtil;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author shannah
 */
public class SnapshotsForm extends com.codename1.ui.Form implements JobQueueListener  {
    private WebSite website;
    private Container snapshotsContainer;
    private TableLayout tl;
    private JobQueueProgressBar jobQueueProgressBar;
    
    
    public SnapshotsForm(WebSite website) {
        super(new BorderLayout());
        setToolbar(new Toolbar());
        setTitle("Snapshots");
        this.website = website;
        tl = new TableLayout(website.getSnapshots().size()+1, 5);
        snapshotsContainer = new Container(tl);
        snapshotsContainer.setScrollableY(true);
        snapshotsContainer.setName("SnapshotsTable");
        //setDisposeWhenPointerOutOfBounds(true);
        
        final Form backForm = CN.getCurrentForm();
        if (backForm != null) {
            
            getToolbar().setBackCommand(new Command("Back") {
                public void actionPerformed(ActionEvent e) {
                    backForm.showBack();
                }
            });
        }
        Container center = BoxLayout.encloseY(snapshotsContainer);
        center.setScrollableY(true);
        add(BorderLayout.CENTER, center);
        
        Button addSnapshot = new Button("Create New Snapshot");
        addSnapshot.setMaterialIcon(FontImage.MATERIAL_ADD);
        //addSnapshot
        addSnapshot.addActionListener(e->{
            CN.callSerially(()->createNewSnapshot());
        });
        
        Button refreshSnapshots = new Button("Refresh");
        refreshSnapshots.setMaterialIcon(FontImage.MATERIAL_REFRESH);
        refreshSnapshots.addActionListener(e->{
            CN.callSerially(()->refresh());
            
        });
        jobQueueProgressBar = new JobQueueProgressBar(SweteApp.getInstance().getJobQueue());
        add(BorderLayout.NORTH, FlowLayout.encloseRight(refreshSnapshots, addSnapshot));
        add(BorderLayout.SOUTH, jobQueueProgressBar);
        
    }

    @Override
    protected void onShowCompleted() {
        super.onShowCompleted(); //To change body of generated methods, choose Tools | Templates.
        CN.callSerially(()->refresh());
    }
    private void associateSnapshot(Component cmp, Snapshot snap) {
        cmp.putClientProperty("snapshot", snap);
    }
    
    private void disassociateSnapshot(Component cmp) {
        cmp.putClientProperty("snapshot", null);
    }
    
    private Snapshot getAssociatedSnapshot(Component cmp) {
        return (Snapshot)cmp.getClientProperty("snapshot");
    }
    
    private static final String TAG_SNAPSHOT_PROGRESS = "snapshot-progress";
    
    
    private void refresh() {
        SweteClient client = new SweteClient(website);
        snapshotsContainer.removeAll();
        snapshotsContainer.repaint();
        snapshotsContainer.setLayout(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE));
        InfiniteProgress slider = new InfiniteProgress();
        
        snapshotsContainer.add(BorderLayout.CENTER, slider);
        snapshotsContainer.revalidateWithAnimationSafety();
        
        try {
            client.load(website);
            client.loadSnapshots(website);
        } catch (IOException ex) {
            Log.e(ex);
            ToastBar.showErrorMessage("Failed to refresh snapshots due to a network error");
        }
        
        snapshotsContainer.removeAll();
        snapshotsContainer.setLayout(tl);
        snapshotsContainer.repaint();
        int row=0;
        
        snapshotsContainer.add(c(row, 0), "Active");
        snapshotsContainer.add(c(row, 1), "ID");
        snapshotsContainer.add(c(row, 2), "Date Created");
        snapshotsContainer.add(c(row, 3), "Date Completed");
        
        row++;
        for (Snapshot snapshot : website.getSnapshots()) {
            addRow(snapshotsContainer, row, snapshot);
            row++;
        }
        revalidateWithAnimationSafety();
        
    }
    
    private void addRow(Container cnt, int row, Snapshot snapshot) {
        CheckBox active = new CheckBox();
        $(active).addTags("active-cb");
        active.addActionListener(e->{
            CN.callSerially(()->{
                activeCheckBoxClicked(active, snapshot);
            });

        });

        InfiniteProgress progress = new InfiniteProgress();
        progress.setVisible(false);
        $(progress).addTags(TAG_SNAPSHOT_PROGRESS);
        associateSnapshot(progress, snapshot);

        active.setSelected(website.getCurrentSnapshotId() != null && website.getCurrentSnapshotId() == snapshot.getSnapshotId());
        Button detailsButton = new Button();
        detailsButton.setMaterialIcon(FontImage.MATERIAL_INFO);
        
        detailsButton.addActionListener(e->{
            CN.callSerially(()->{
                new SnapshotForm(snapshot).show();
            });
        }); 

        snapshotsContainer.add(c(row, 0), active);
        snapshotsContainer.add(c(row, 1), String.valueOf(snapshot.getSnapshotId()));
        snapshotsContainer.add(c(row, 2), new DateUtil().getTimeAgo(snapshot.getDateCreated()));
        snapshotsContainer.add(c(row, 3), new DateUtil().getTimeAgo(snapshot.getDateCompleted()));
        snapshotsContainer.add(c(row, 4), FlowLayout.encloseIn(detailsButton, progress));

    }
    
    private TableLayout.Constraint c(int row, int col) {
        TableLayout tl = (TableLayout)snapshotsContainer.getLayout();
        return tl.createConstraint(row, col);
    }
    
    private void createNewSnapshot() {
        SweteClient client = new SweteClient(website);
        ToastBar.Status status = ToastBar.getInstance().createStatus();
        status.setMessage("Creating snapshot");
        status.setShowProgressIndicator(true);
        status.show();
        try {
            Snapshot snapshot = client.createNewSnapshot();
            refresh();
            RefreshSnapshotPageCrawler crawler = new RefreshSnapshotPageCrawler(snapshot);
            crawler.setJobDescription("Refreshing Snapshot "+snapshot.getSnapshotId());
            SweteApp.getInstance().getJobQueue().add(crawler);
            
        } catch (IOException ex) {
            ToastBar.showErrorMessage("Failed to create snapshot: "+ex.getMessage());
            Log.e(ex);
        } finally {
            status.clear();
        }
    }
    
    private void activeCheckBoxClicked(CheckBox active, Snapshot snapshot) {
        SweteClient client = new SweteClient(website);
        if (active.isSelected()) {
            if (website.getCurrentSnapshotId() == null || website.getCurrentSnapshotId() != snapshot.getSnapshotId()) {
                Integer old = website.getCurrentSnapshotId();
                website.setCurrentSnapshotId(snapshot.getSnapshotId());
                Status status = ToastBar.getInstance().createStatus();
                status.setShowProgressIndicator(true);
                status.show();

                try {
                    client.save(website);
                    
                } catch (IOException ex) {
                    website.setCurrentSnapshotId(old);
                    active.setSelected(false);
                    Log.e(ex);
                    return;
                } finally {
                    status.clear();
                }
                refresh();
            }
        } else {
            website.setCurrentSnapshotId(-1);
            Status status = ToastBar.getInstance().createStatus();
            status.setShowProgressIndicator(true);
            status.show();

            try {
                client.save(website);

            } catch (IOException ex) {
                active.setSelected(true);
                Log.e(ex);
                return;
            } finally {
                status.clear();
            }
            refresh();
        }
    }

    private void updateSnapshotJobProgress() {
        Set<Integer> runningSnaps = new HashSet<Integer>();
        for (BackgroundJob job : getJobQueue().getRunningJobs()) {
            if (job instanceof RefreshSnapshotPageCrawler) {
                RefreshSnapshotPageCrawler crawler = (RefreshSnapshotPageCrawler)job;
                if (crawler.isInProgress()) {
                    runningSnaps.add(crawler.getSnapshot().getSnapshotId());
                }
            }
        }
        $("."+TAG_SNAPSHOT_PROGRESS, this).each(c->{
            Snapshot snapshot = getAssociatedSnapshot(c);
            if (snapshot != null) {
                c.setVisible(runningSnaps.contains(snapshot.getSnapshotId()));
            }
        });
        snapshotsContainer.repaint();
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
        updateSnapshotJobProgress();
    }

    private JobQueue getJobQueue() {
        return SweteApp.getInstance().getJobQueue();
    }
}
