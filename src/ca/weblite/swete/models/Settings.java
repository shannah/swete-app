/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.models;

import ca.weblite.swete.SweteClient;
import ca.weblite.swete.validation.ValidationFailure;
import com.codename1.io.Externalizable;
import com.codename1.io.Log;
import com.codename1.io.Storage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.codename1.io.Util;



/**
 *
 * @author shannah
 */
public class Settings implements Externalizable {
    private final List<WebSite> websites=new ArrayList<WebSite>();
    private String currentSiteProxyUrl;
    private static Settings instance;
    
    public WebSite findWebSiteByProxyUrl(String proxyUrl) {
        for (WebSite website : websites) {
            if (proxyUrl.equals(website.getProxyUrl())) {
                return website;
            }
        }
        return null;
    }
    
    public void addWebSite(WebSite website) {
        websites.add(website);
        
    }
    
    public void removeWebSite(WebSite website) {
        websites.remove(website);
    }
    
    public List<WebSite> getWebSites() {
        return new ArrayList<WebSite>(websites);
    }
    
    public void save() throws IOException {
        Storage s = Storage.getInstance();
        s.writeObject("Settings", this);
    }
    
    public static Settings getInstance() throws IOException {
        if (instance == null) {
            Storage s = Storage.getInstance();
            instance = (Settings)s.readObject("Settings");
            if (instance == null) {
                instance = new Settings();
            }
        }
        return instance;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void externalize(DataOutputStream out) throws IOException {
        Util.writeObject(websites, out);
        Util.writeUTF(currentSiteProxyUrl, out);
        
        
    }

    @Override
    public void internalize(int version, DataInputStream in) throws IOException {
        websites.clear();
        websites.addAll((java.util.List)Util.readObject(in));
        currentSiteProxyUrl = Util.readUTF(in);
    }

    @Override
    public String getObjectId() {
        return "Settings";
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private ValidationFailure validationFailure;
    
    public boolean addWebSiteWithURL(String url) {
        validationFailure = new ValidationFailure();
        return addWebSiteWithURL(url, validationFailure);
    }
    
    public ValidationFailure getValidationFailure() {
        return validationFailure;
    }
    
    public boolean addWebSiteWithURL(String url, ValidationFailure error) {
        if (error != null) error.setMessage(null);
        if (findWebSiteByProxyUrl(url) != null) {
            if (error != null) {
                error.setMessage("A website with that URL already exists");
                return false;
            }
        }
        
        WebSite site = new WebSite();
        site.setProxyUrl(url);
        SweteClient client = new SweteClient(site);
        try {
            client.load();
        } catch (IOException ex) {
            Log.e(ex);
            if (error != null) error.setMessage(ex.getMessage());
            return false;
        }
        
        addWebSite(site);
        try {
            save();
        } catch (IOException ex) {
            Log.e(ex);
            throw new RuntimeException("Failed to save settings after adding site "+site, ex);
        }
        return true;
        
    }
    
}
