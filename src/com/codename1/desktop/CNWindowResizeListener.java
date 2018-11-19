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
public interface CNWindowResizeListener {
    public void windowResized(int w, int h);
    public void windowMoved(int x, int y);
    public void windowShown();
    public void windowHidden();
}
