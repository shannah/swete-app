/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.util;

import ca.weblite.swete.models.Snapshot;
import ca.weblite.swete.models.WebSite;
import com.codename1.components.ToastBar;
import com.codename1.io.Log;
import com.codename1.ui.BrowserComponent;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

/**
 *
 * @author shannah
 */
public class BrowserUtil {
    public static void setCookieAndWait(BrowserComponent cmp, String cookieName, String cookieValue, int days) {
        String code = "function setCookie(name,value,days) {\n" +
                "    var expires = \"\";\n" +
                "    if (days) {\n" +
                "        var date = new Date();\n" +
                "        date.setTime(date.getTime() + (days*24*60*60*1000));\n" +
                "        expires = \"; expires=\" + date.toUTCString();\n" +
                "    }\n" +
                "    document.cookie = name + \"=\" + (value || \"\")  + expires + \"; path=/\"" +
                "}\n" 
                + "setCookie(${0}, ${1}, ${2}); callback.onSuccess(true)";
        cmp.executeAndWait(code, new Object[]{cookieName, cookieValue, days});
    }
    
    public static String getCookieAndWait(BrowserComponent cmp, String cookieName) {
        String code = "function getCookie(name) {\n" +
                "    var nameEQ = name + \"=\";\n" +
                "    var ca = document.cookie.split(';');\n" +
                "    for(var i=0;i < ca.length;i++) {\n" +
                "        var c = ca[i];\n" +
                "        while (c.charAt(0)==' ') c = c.substring(1,c.length);\n" +
                "        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);\n" +
                "    }\n" +
                "    return null;\n" +
                "}\n"
                + "callback.onSuccess(getCookie(${0}))";
        return cmp.executeAndWait(code, new Object[]{cookieName}).getValue();
    }
    
    public static void eraseCookieAndWait(BrowserComponent cmp, String cookieName) {
        String code = "function eraseCookie(name) {   \n" +
                "    document.cookie = name+'=; Max-Age=-99999999; path=/';  \ndocument.cookie = name+'=; Max-Age=-99999999';" +
                "}\n"
                + "eraseCookie(${0}); callback.onSuccess(true)";
        cmp.executeAndWait(code, new Object[]{cookieName});
    }
    
    public static boolean startCapturing(WebSite website, BrowserComponent proxyBrowser) {
        
        if (checkIfCaptureEnabled(proxyBrowser)) {
            return false;
        }
        Log.p("Starting capturing... setting --swete-capture to 1");
        setCookieAndWait(proxyBrowser, "--swete-capture", "1", 0);
        return true;
    }
    
    public static boolean stopCapturing(WebSite website, BrowserComponent proxyBrowser) {
        if (!checkIfCaptureEnabled(proxyBrowser)) return false;
        eraseCookieAndWait(proxyBrowser, "--swete-capture");
        return true;
    }
    
    public static boolean checkIfSnapshotsEnabled(BrowserComponent proxyBrowser) {
        String val = BrowserUtil.getCookieAndWait(proxyBrowser, "--swete-static");
        System.out.println("--swete-static value is "+val);
        return (!"false".equals(val)); 
    }
    
    public static boolean checkIfSnapshotEnabled(BrowserComponent proxyBrowser, Snapshot snap) {
        if (!checkIfSnapshotsEnabled(proxyBrowser)) {
            return false;
        }
        String snapId = getCookieAndWait(proxyBrowser, "--swete-snapshot-id");
        if (snapId == null || Integer.parseInt(snapId) != snap.getSnapshotId()) {
            return false;
        }
        return true;
    }
    
    public static boolean checkIfCaptureEnabled(BrowserComponent proxyBrowser) {
        String val = BrowserUtil.getCookieAndWait(proxyBrowser, "--swete-capture");
        return "1".equals(val);
    }
    
    public static boolean enableSnapshots(BrowserComponent proxyBrowser) {
        return enableSnapshots(proxyBrowser, null);
    }
    
    public static boolean enableSnapshots(BrowserComponent proxyBrowser, Snapshot snapshot) {
        if (checkIfSnapshotsEnabled(proxyBrowser)) {
            String snapId = getCookieAndWait(proxyBrowser, "--swete-snapshot-id");
            int snapIdInt = -1;
            try {
                snapIdInt = snapId != null ? Integer.parseInt(snapId) : -1;
                
            } catch (Throwable t){}
            if (snapshot == null || (snapIdInt == snapshot.getSnapshotId())) {
                return false;
            }
        }
        
        BrowserUtil.eraseCookieAndWait(proxyBrowser, "--swete-static");
        if (snapshot != null) {
            BrowserUtil.setCookieAndWait(proxyBrowser, "--swete-snapshot-id", ""+snapshot.getSnapshotId(), 999);
        } else {
            BrowserUtil.eraseCookieAndWait(proxyBrowser, "--swete-snapshot-id");
        }
        
        return true;
        
    }
    
    public static boolean disableSnapshots(BrowserComponent proxyBrowser) {
        if (!checkIfSnapshotsEnabled(proxyBrowser)) {
            return false;
        }
        
        BrowserUtil.setCookieAndWait(proxyBrowser, "--swete-static", "false", 9999);
        BrowserUtil.eraseCookieAndWait(proxyBrowser, "--swete-snapshot-id");
        return true;
        
    }
    
    
    
    
}
