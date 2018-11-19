/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.htmlform;

/**
 *
 * @author shannah
 */
public interface HTMLPageController {
    
    public static interface Factory {
        public HTMLPageController createPageController(HTMLForm context, String url);
    }
    
    public void onLoad(HTMLForm context, String url);
    public void onUnload(HTMLForm context, String url);
}
