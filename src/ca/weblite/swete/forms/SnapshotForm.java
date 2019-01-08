/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.forms;

import ca.weblite.swete.SweteClient;
import ca.weblite.swete.components.JobProgressBar;
import ca.weblite.swete.components.PopupMenu;
import ca.weblite.swete.models.Snapshot;
import ca.weblite.swete.models.Snapshot.PageStatus;
import ca.weblite.swete.models.Snapshot.SnapshotPage;
import com.codename1.components.InfiniteProgress;
import com.codename1.components.MultiButton;
import com.codename1.components.SpanLabel;
import com.codename1.components.ToastBar;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.table.TableLayout;
import com.codename1.util.DateUtil;
import java.util.List;

/**
 *
 * @author shannah
 */
public class SnapshotForm extends Form {
    private final Snapshot snapshot;
    private JobProgressBar progressBar;
    private Container center;
    
    
    public SnapshotForm(Snapshot snapshot) {
        super(new BorderLayout());
        setToolbar(new Toolbar());
        setTitle("Snapshot "+snapshot.getSnapshotId());
        this.snapshot = snapshot;
        
        final Form backForm = CN.getCurrentForm();
        if (backForm != null) {
            
            getToolbar().setBackCommand(new Command("Back") {
                public void actionPerformed(ActionEvent e) {
                    backForm.showBack();
                }
            });
        }
        
        center = new Container(BoxLayout.y());
        center.setScrollableY(true);
        
        Button refreshButton = new Button("Refresh");
        refreshButton.addActionListener(e->{
            CN.callSerially(()->refresh());
        });
        
        add(BorderLayout.CENTER, center);
        CN.callSerially(()->refresh());
        
    }
    
    private void update() {
        center.removeAll();
        TableLayout tl = new TableLayout(2, 2);
        Container table = new Container(tl);
        table.add(tl.createConstraint(0, 0), new Label("Snapshot ID"));
        table.add(tl.createConstraint(0, 1), new Label(""+snapshot.getSnapshotId()));
        table.add(tl.createConstraint(1, 0), new Label("Created"));
        table.add(tl.createConstraint(1, 1), new Label(new DateUtil().getTimeAgo(snapshot.getDateCreated())));
        table.add(tl.createConstraint(2, 0), new Label("Completed"));
        table.add(tl.createConstraint(2, 1), new Label(new DateUtil().getTimeAgo(snapshot.getDateCompleted())));
        
        center.add(table);
        
        center.add(new Label("Pages"));
        List<SnapshotPage> pages = snapshot.getPages();
        if (pages != null && !pages.isEmpty()) {
            for (SnapshotPage page : pages) {
                center.add(createPageRow(page));
            }
        } else {
            center.add(new SpanLabel("This snapshot does not contain any pages"));
        }
        
        revalidateWithAnimationSafety();
        
    }
    
    
    private Component createPageRow(SnapshotPage page) {
        Container out = new Container(new BorderLayout(), "SnapshotFormPageRow");
        Label name = new Label(page.getPage(), "SnapshotFormPageRowName");
        Label responseCode = new Label("", "SnapshotFormPageRowResponseCode");
        Label lastUpdate = new Label("", "SnapshotFormPageRowLastUpdate");
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
        
        PopupMenu menu = new PopupMenu();
        menu.setMaterialIcon(FontImage.MATERIAL_MORE_VERT);
        
        Command preview = new Command("Preview") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new PreviewForm(snapshot, snapshot.getWebSite().getProxyUrlForPage(page.getPage())).show();
            }
            
        };
        menu.addCommand(preview);
        
        Command update = new Command("Update") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new UpdatePageSnapshotForm(snapshot, snapshot.getWebSite().getProxyUrlForPage(page.getPage())).show();
            }
            
        };
        menu.addCommand(update);
        out.add(BorderLayout.CENTER, BoxLayout.encloseY(name, responseCode, lastUpdate));
        out.add(BorderLayout.EAST, new Button(menu.getCommand()));
        return out;
    }
    
    private Component createPageRow_old(SnapshotPage page) {
        MultiButton out = new MultiButton();
        out.setEmblem(FontImage.createMaterial(FontImage.MATERIAL_ARROW_FORWARD, out.getStyle()));
        out.addActionListener(e->{
            new PreviewForm(snapshot, snapshot.getWebSite().getProxyUrlForPage(page.getPage())).show();
        });
        out.setTextLine1(page.getPage());
        if (page.getStatus() != null) {
            PageStatus status = page.getStatus();
            if (status.getStatusCode() <= 0) {
                out.setTextLine2("Queued");
            } else {
                out.setTextLine2("Response code: "+status.getStatusCode());
                if (status.getTimestamp() != null) {
                    out.setTextLine3(new DateUtil().getTimeAgo(status.getTimestamp()));
                }
            }
            
        }
        return out;
        
    }
    
    private void refresh() {
        try {
            center.removeAll();
            center.add(new InfiniteProgress());
            center.revalidateWithAnimationSafety();
            SweteClient client = new SweteClient(snapshot.getWebSite());
            client.load(snapshot);
            update();
            
        } catch (Throwable t) {
            Log.e(t);
            ToastBar.showErrorMessage(t.getMessage());
        }
    }
}
