/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.models;

import com.codename1.util.DateUtil;
import java.util.Collection;

/**
 *
 * @author shannah
 */
public class Models {
    public static WebpageStatus getMinByLastChecked(Collection<WebpageStatus> webpages) {
        WebpageStatus out = null;
        for (WebpageStatus s : webpages) {
            if (out == null || DateUtil.compare(out.getLastChecked(), s.getLastChecked()) > 0) {
                out = s;
            }
        }
        return out;
    }
    
    
    
}
