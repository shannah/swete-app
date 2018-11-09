/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.components;

import com.codename1.components.Switch;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;

/**
 *
 * @author shannah
 */
public class BrowserToolbar extends Container {

    /**
     * @return the refresh
     */
    public Button getRefresh() {
        return refresh;
    }

    /**
     * @return the openSrcInBrowser
     */
    public Button getOpenSrcInBrowser() {
        return openSrcInBrowser;
    }

   
    /**
     * @return the openProxyInBrowser
     */
    public Button getOpenProxyInBrowser() {
        return openProxyInBrowser;
    }

   

    /**
     * @return the captureSwitch
     */
    public Switch getCaptureSwitch() {
        return captureSwitch;
    }

    /**
     * @return the snapshotsSwitch
     */
    public Switch getSnapshotsSwitch() {
        return snapshotsSwitch;
    }
    private Switch captureSwitch, snapshotsSwitch;
    private Button openSrcInBrowser, openProxyInBrowser, refresh;
    
    
    public BrowserToolbar() {
        super(new BorderLayout());
        captureSwitch = new Switch();
        Label captureLabel = new Label("Capture");
        Label snapshotsLabel = new Label("Snapshots");
        snapshotsSwitch = new Switch();
        
        captureSwitch.setLabelForComponent(captureLabel);
        snapshotsSwitch.setLabelForComponent(snapshotsLabel);
        
        openProxyInBrowser = new Button("Open in Browser");
        openProxyInBrowser.setMaterialIcon(FontImage.MATERIAL_OPEN_IN_BROWSER, 3f);
        openProxyInBrowser.setCursor(Component.HAND_CURSOR);
        
        openSrcInBrowser = new Button("Open in Browser");
        openSrcInBrowser.setMaterialIcon(FontImage.MATERIAL_OPEN_IN_BROWSER, 3f);
        openSrcInBrowser.setCursor(Component.HAND_CURSOR);
        
        refresh = new Button("Refresh");
        refresh.setMaterialIcon(FontImage.MATERIAL_REFRESH, 3f);
        refresh.setCursor(Component.HAND_CURSOR);
        
        Container left = new Container(BoxLayout.x());
        left.add(openSrcInBrowser);
        left.add(refresh);
        
        
        Container right = new Container(BoxLayout.x());
        right
                .add(openProxyInBrowser)
                .add(captureLabel).add(captureSwitch).
                add(snapshotsLabel).add(snapshotsSwitch);
        add(BorderLayout.EAST, right).add(BorderLayout.WEST, left);
        
    }
}
