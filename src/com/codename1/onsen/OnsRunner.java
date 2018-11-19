/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen;

/**
 *
 * @author shannah
 */
public class OnsRunner implements Runnable {
    private OnsRunnable r;
    private OnsApplication app;
    public OnsRunner(OnsRunnable r, OnsApplication app){
        this.r = r;
        this.app = app;
    }

    @Override
    public void run() {
        r.run(app);
    }
    
    
}
