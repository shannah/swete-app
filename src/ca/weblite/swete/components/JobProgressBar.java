/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.components;

import ca.weblite.swete.services.BackgroundJob;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.Slider;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;

/**
 *
 * @author shannah
 */
public class JobProgressBar extends Container implements ActionListener {
    private final BackgroundJob job;
    private final Label description;
    private final Label progressLabel;
    private final Slider progressBar;
    private final Button cancelButton;
    
    public JobProgressBar(BackgroundJob job) {
        super(BoxLayout.y());
        this.job = job;
        description = new Label();
        progressLabel = new Label();
        progressBar = new Slider();
        progressBar.setMinValue(0);
        progressBar.setMaxValue(100);
        cancelButton = new Button("Cancel");
        cancelButton.addActionListener(e->{
            CN.callSerially(()->cancel());
        });
        add(description).add(progressLabel).add(BorderLayout.center(progressBar).add(BorderLayout.EAST, cancelButton));
        update();
        
    }
    
    
    private void cancel() {
        if (!job.isCancelled() && !job.isComplete()) {
            job.cancel();
        }
    }
    private void update() {
        description.setText(job.getJobDescription());
        if (job.isCancelled()) {
            progressLabel.setText("Cancelled");
            progressBar.setVisible(false);
            cancelButton.setVisible(false);
        } else if (job.isComplete()) {
            progressLabel.setText("Complete");
            progressBar.setVisible(false);
            cancelButton.setVisible(false);
        } else if (job.isInProgress()) {
            progressBar.setVisible(true);
            progressBar.setProgress(job.getProgressPercent());
            progressBar.setInfinite(false);
            cancelButton.setVisible(true);
            if (job.getComplete() == job.getSucceeded()) {
                progressLabel.setText("Processing "+job.getComplete()+" of "+job.getTotal());
            } else {
                progressLabel.setText("Processing "+job.getComplete()+" of "+job.getTotal()+". "+(job.getComplete()-job.getSucceeded())+" failed.");
            }
        } else {
            progressLabel.setText("Queued");
            cancelButton.setVisible(true);
            progressBar.setVisible(false);
        }
        
        //revalidateWithAnimationSafety();

    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == job) {
            update();
            revalidateWithAnimationSafety();
            if (job.isCancelled() || job.isComplete()) {
                job.removeProgressListener(this);
            }
        }
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        job.addProgressListener(this);
        CN.callSerially(()->update());
    }

    @Override
    protected void deinitialize() {
        job.removeProgressListener(this);
        super.deinitialize(); 
    }
    
    
    public BackgroundJob getJob() {
        return job;
    }
    
}
