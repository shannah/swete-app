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
import com.codename1.components.InteractionDialog;
import com.codename1.components.ToastBar;
import com.codename1.components.ToastBar.Status;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Slider;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.table.TableLayout;
import java.io.IOException;

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
        //setDisposeWhenPointerOutOfBounds(true);
        
        final Form backForm = CN.getCurrentForm();
        if (backForm != null) {
            
            getToolbar().setBackCommand(new Command("Back") {
                public void actionPerformed(ActionEvent e) {
                    backForm.showBack();
                }
            });
        }
        
        add(BorderLayout.CENTER, snapshotsContainer);
        
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
    protected void onShow() {
        super.onShow(); //To change body of generated methods, choose Tools | Templates.
        CN.callSerially(()->refresh());
    }
    
    
    
    private void refresh() {
        SweteClient client = new SweteClient(website);
        snapshotsContainer.removeAll();
        snapshotsContainer.revalidate();
        snapshotsContainer.setLayout(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE));
        Slider slider = new Slider();
        slider.setInfinite(true);
        snapshotsContainer.add(BorderLayout.CENTER, slider);
        snapshotsContainer.revalidate();
        
        try {
            client.load(website);
            client.loadSnapshots(website);
        } catch (IOException ex) {
            Log.e(ex);
            ToastBar.showErrorMessage("Failed to refresh snapshots due to a network error");
        }
        
        snapshotsContainer.removeAll();
        snapshotsContainer.setLayout(tl);
        snapshotsContainer.revalidate();
        int row=0;
        
        snapshotsContainer.add(c(row, 0), "Active");
        snapshotsContainer.add(c(row, 1), "ID");
        snapshotsContainer.add(c(row, 2), "Date Created");
        snapshotsContainer.add(c(row, 3), "Date Completed");
        
        row++;
        for (Snapshot snapshot : website.getSnapshots()) {
            CheckBox active = new CheckBox();
            $(active).addTags("active-cb");
            active.addActionListener(e->{
                CN.callSerially(()->{
                    activeCheckBoxClicked(active, snapshot);
                });
                
            });
            active.setSelected(website.getCurrentSnapshotId() != null && website.getCurrentSnapshotId() == snapshot.getSnapshotId());
            Button detailsButton = new Button();
            detailsButton.setMaterialIcon(FontImage.MATERIAL_INFO);
            
            snapshotsContainer.add(c(row, 0), active);
            snapshotsContainer.add(c(row, 1), String.valueOf(snapshot.getSnapshotId()));
            snapshotsContainer.add(c(row, 2), String.valueOf(snapshot.getDateCreated()));
            snapshotsContainer.add(c(row, 3), String.valueOf(snapshot.getDateCompleted()));
            snapshotsContainer.add(c(row, 4), detailsButton);
            row++;
        }
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
    }

    private JobQueue getJobQueue() {
        return SweteApp.getInstance().getJobQueue();
    }
}
