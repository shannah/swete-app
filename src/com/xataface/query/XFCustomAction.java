/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xataface.query;

import com.codename1.io.ConnectionRequest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class XFCustomAction {
    private String action;
    private String requestMethod = "POST";
    private class Header {
        private String name;
        private String value;
    }
    private Map<String,String> params = new HashMap<>();
    
    public XFCustomAction(String action) {
        this.action = action;
    }
    public void setupRequest(ConnectionRequest req) {
        req.addArgument("-action", action);
        for (String key : params.keySet()) {
            req.addArgument(key, params.get(key));
        }
                
    }
    
    public XFCustomAction put(String key, String value) {
        params.put(key, value);
        return this;
    }
    
    
}
