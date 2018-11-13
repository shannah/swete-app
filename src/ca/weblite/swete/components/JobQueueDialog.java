/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.components;

import ca.weblite.swete.services.BackgroundJob;
import ca.weblite.swete.services.JobQueue;
import ca.weblite.swete.services.JobQueue.JobQueueListener;
import com.codename1.components.Accordion;
import com.codename1.components.InteractionDialog;
import com.codename1.ui.CN;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import java.util.ArrayList;

/**
 *
 * @author shannah
 */
public class JobQueueDialog extends InteractionDialog implements JobQueueListener {

    /**
     * @return the removeCompletedJobs
     */
    public boolean isRemoveCompletedJobs() {
        return removeCompletedJobs;
    }

    /**
     * @param removeCompletedJobs the removeCompletedJobs to set
     */
    public void setRemoveCompletedJobs(boolean removeCompletedJobs) {
        this.removeCompletedJobs = removeCompletedJobs;
    }
    private JobQueue queue;
    //private Accordion accordion;
    private Container jobsContainer;
    private boolean removeCompletedJobs=true;
    public JobQueueDialog(JobQueue queue) {
        super(new BorderLayout());
        setTitle("Task Queue");
        setName("JobQueueDialog");
        this.queue = queue;
        this.setDisposeWhenPointerOutOfBounds(true);
        //accordion = new Accordion();
        jobsContainer = new Container(BoxLayout.y());
        jobsContainer.setScrollableY(true);
        for (BackgroundJob job : queue.getJobs()) {
            jobsContainer.add(new JobProgressBar(job));
        }
        //accordion.addContent("Tasks", jobsContainer);
        add(BorderLayout.CENTER, jobsContainer);
        setDisposeWhenPointerOutOfBounds(true);
        
    }

    @Override
    public void jobAdded(BackgroundJob job) {
        jobsContainer.add(new JobProgressBar(job));
        animateHierarchy(300);
    }

    @Override
    public void jobRemoved(BackgroundJob job) {
        JobProgressBar view = findViewForJob(job);
        if (view != null) {
            view.remove();
            animateHierarchy(300);
        }
    }

    @Override
    public void jobChanged(BackgroundJob job) {
        if (job.isComplete()) {
            JobProgressBar view = findViewForJob(job);
            if (view != null) {
                view.remove();
                animateHierarchy(300);
            }
        }
        
    }
    
    private JobProgressBar findViewForJob(BackgroundJob job) {
        for (Component c : jobsContainer) {
            if (c instanceof JobProgressBar) {
                JobProgressBar pb = (JobProgressBar)c;
                if (pb.getJob() == job) {
                    return pb;
                }
            }
        }
        return null;
    }

    private void pruneCompleted() {
        if (removeCompletedJobs) {
            ArrayList<Component> children = new ArrayList<Component>();
            for (Component c : jobsContainer) {
                children.add(c);
            }
            boolean changed = false;
            for (Component c : children) {
                if (c instanceof JobProgressBar) {
                    JobProgressBar pb = (JobProgressBar)c;
                    if (pb.getJob().isComplete()) {
                        pb.remove();
                        changed = true;
                    }
                }
            }
            if (changed) {
                revalidateWithAnimationSafety();
            }
        }
    }
    
    @Override
    protected void initComponent() {
        super.initComponent();
        queue.addListener(this);
        CN.callSerially(()->pruneCompleted());
        
    }

    @Override
    protected void deinitialize() {
        queue.removeListener(this);
        super.deinitialize(); 
    }
    
    
    
    
}
