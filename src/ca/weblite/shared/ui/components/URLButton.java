/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.ui.components;

import com.codename1.io.URL;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.FontImage;

/**
 *
 * @author shannah
 */
public class URLButton extends Button {
    
    public URLButton(String url) {
        this(url, null, "Button");
    }
    
    public URLButton(String url, String label) {
        this(url, label, "Button");
    }
    
    public URLButton(String url, String label, String uiid) {
        super(FontImage.MATERIAL_OPEN_IN_BROWSER, uiid);
        if (label != null) {
            setText(label);
        }
        if (url != null) {
            addActionListener(e->{
                try {
                    CN.execute(new URL(url).toString());
                } catch (Throwable t){}
            });
        } else {
            setEnabled(false);
        }
    }
}
