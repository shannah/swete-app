/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.components;

import ca.weblite.swete.services.BackgroundJob;
import ca.weblite.swete.services.JobQueue;
import ca.weblite.swete.services.JobQueue.JobQueueListener;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Label;
import com.codename1.ui.Slider;
import com.codename1.ui.layouts.BorderLayout;

/**
 *
 * @author shannah
 */
public class JobQueueProgressBar extends Container implements JobQueueListener {
    private Slider progress;
    private Button moreButton;
    private Label description;
    
    private JobQueue queue;
    
    public JobQueueProgressBar(JobQueue queue) {
        super(new BorderLayout());
        progress = new Slider();
        progress.setInfinite(true);
        description = new Label();
        moreButton = new Button();
        moreButton.setMaterialIcon(FontImage.MATERIAL_INFO);
        moreButton.setCursor(Component.HAND_CURSOR);
        moreButton.addActionListener(e->{
            JobQueueDialog dialog = new JobQueueDialog(queue);
            dialog.showPopupDialog(moreButton);
        });
        
        add(BorderLayout.CENTER, progress).add(BorderLayout.EAST, moreButton).add(BorderLayout.WEST, description);
        
    }

    private void update() {
        if (queue.getRunningJobs().size() > 0) {
            description.setText(queue.getRunningJobs().size()+" tasks running");
            progress.setVisible(true);
        } else {
            description.setText("");
            progress.setVisible(false);
        }
        revalidate();
    }
    
    @Override
    public void jobAdded(BackgroundJob job) {
        update();
    }

    @Override
    public void jobRemoved(BackgroundJob job) {
        update();
    }

    @Override
    public void jobChanged(BackgroundJob job) {
        update();
    }
}
