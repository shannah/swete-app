/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.services;

import com.codename1.ui.CN;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.util.EasyThread;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shannah
 */
public class JobQueue implements ActionListener {
    private final List<BackgroundJob> jobs = new ArrayList<BackgroundJob>();
    EasyThread thread;
    private final List<JobQueueListener> listeners = new ArrayList<JobQueueListener>();
    public static interface JobQueueListener {
        public void jobAdded(BackgroundJob job);
        public void jobRemoved(BackgroundJob job);
        public void jobChanged(BackgroundJob job);
    }
    
    public JobQueue() {
        thread = EasyThread.start("Job Queue");
    }
    
    private void fireJobAdded(BackgroundJob job) {
        if (!CN.isEdt()) {
            CN.callSerially(()->fireJobAdded(job));
            return;
        }
        List<JobQueueListener> ll = new ArrayList<JobQueueListener>(listeners);
        for (JobQueueListener l : ll) {
            l.jobAdded(job);
        }
    }
    
    private void fireJobRemoved(BackgroundJob job) {
        if (!CN.isEdt()) {
            CN.callSerially(()->fireJobRemoved(job));
        }
        List<JobQueueListener> ll = new ArrayList<JobQueueListener>(listeners);
        for (JobQueueListener l : ll) {
            l.jobRemoved(job);
        }
    }
    
    private void fireJobChanged(BackgroundJob job) {
        if (!CN.isEdt()) {
            CN.callSerially(()->fireJobChanged(job));
        }
        List<JobQueueListener> ll = new ArrayList<JobQueueListener>(listeners);
        for (JobQueueListener l : ll) {
            l.jobChanged(job);
        }
    }
    
    public void add(BackgroundJob job) {
        job.addProgressListener(this);
        jobs.add(job);
        fireJobAdded(job);
        thread.run(job);
    }
    
    public void remove(BackgroundJob job) {
        job.removeProgressListener(this);
        jobs.remove(job);
        fireJobRemoved(job);
        
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() instanceof BackgroundJob) {
            fireJobChanged((BackgroundJob)evt.getSource());
        }
    }
    
    public BackgroundJob getCurrentlyRunningJob() {
        for (BackgroundJob job : jobs) {
            if (job.isInProgress()) {
                return job;
            }
        }
        return null;
    }
    public List<BackgroundJob> getRunningJobs() {
        List<BackgroundJob> out = new ArrayList<BackgroundJob>();
        for (BackgroundJob job : jobs) {
            if (job.isInProgress()) {
                out.add(job);
            }
        }
        return out;
    }
    
    public List<BackgroundJob> getJobs() {
        return new ArrayList<BackgroundJob>(jobs);
    }
    
    public void addListener(JobQueueListener l) {
        listeners.add(l);
    }
    
    public void removeListener(JobQueueListener l) {
        listeners.remove(l);
    }
}
