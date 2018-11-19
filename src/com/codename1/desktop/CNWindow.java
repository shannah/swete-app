/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.desktop;

/**
 *
 * @author shannah
 */
public interface CNWindow {
    
    public void addWindowListener(CNWindowListener l);
    public void removeWindowListener(CNWindowListener l);
    public void addWindowResizeListener(CNWindowResizeListener l);
    public void removeWindowResizeListener(CNWindowResizeListener l);
    public void setVisible(boolean visible);
    public void setTitle(String title);
    public String getTitle();
    public void setSize(int width, int height);
    public com.codename1.ui.geom.Dimension getSize();
    public void setPosition(int x, int y);
    public com.codename1.ui.geom.Point getPosition();
    public void setBounds(int x, int y, int w, int h);
    public com.codename1.ui.geom.Rectangle getBounds();
    
}
