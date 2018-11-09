/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.components;

import ca.weblite.swete.SweteClient;
import ca.weblite.swete.models.Snapshot;
import ca.weblite.swete.models.WebSite;
import com.codename1.components.InteractionDialog;
import com.codename1.components.ToastBar;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Slider;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.table.TableLayout;
import java.io.IOException;

/**
 *
 * @author shannah
 */
public class SnapshotsDialog extends InteractionDialog {
    private WebSite website;
    private Container snapshotsContainer;
    private TableLayout tl;
    
    
    
    public SnapshotsDialog(WebSite website) {
        super(new BorderLayout());
        setTitle("Snapshots");
        this.website = website;
        tl = new TableLayout(website.getSnapshots().size()+1, 5);
        snapshotsContainer = new Container(tl);
        snapshotsContainer.setScrollableY(true);
        setDisposeWhenPointerOutOfBounds(true);
        
        add(BorderLayout.CENTER, snapshotsContainer);
        
        Button addSnapshot = new Button("Create New Snapshot");
        addSnapshot.setMaterialIcon(FontImage.MATERIAL_ADD);
        
        Button refreshSnapshots = new Button("Refresh");
        refreshSnapshots.addActionListener(e->{
            CN.callSerially(()->refresh());
            
        });
        
        add(BorderLayout.NORTH, FlowLayout.encloseRight(refreshSnapshots, addSnapshot));
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
            Button detailsButton = new Button();
            detailsButton.setMaterialIcon(FontImage.MATERIAL_INFO);
            
            snapshotsContainer.add(c(row, 0), active);
            snapshotsContainer.add(c(row, 1), snapshot.getSnapshotId());
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
    
}
