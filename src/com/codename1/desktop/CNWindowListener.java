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
public interface CNWindowListener {
    public void windowActivated(CNWindowEvent e);
    public void windowClosed(CNWindowEvent e);
    public void windowClosing(CNWindowEvent e);
    public void windowDeactivated(CNWindowEvent e);
    public void windowDeiconified(CNWindowEvent e);
    public void windowIconified(CNWindowEvent e);
    public void windowOpened(CNWindowEvent e);
}
