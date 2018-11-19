/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.htmlform;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class SimpleHTMLPageControllerFactory implements HTMLPageController.Factory {
    private Map<String,Class<? extends HTMLPageController>> registeredControllers = new HashMap<>();
    
    public SimpleHTMLPageControllerFactory register(String url, Class<? extends HTMLPageController> cls) {
        registeredControllers.put(url, cls);
        return this;
    }
    
    @Override
    public HTMLPageController createPageController(HTMLForm context, String url) {
        //HTMLPageController controller = registeredControllers.
        return null;
    }
    
}
