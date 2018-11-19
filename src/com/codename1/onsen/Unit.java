/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen;

/**
 *
 * @author shannah
 */
public enum Unit {
    Percent("%"),
    Pixels("px"),
    Millimetres("mm"),
    Inches("in"),
    Points("pt"),
    Em("em");
    
    String abbrev;
    
    Unit(String abbrev) {
        this.abbrev = abbrev;
    }
    
    
    public String toString() {
        return abbrev;
    }
   
}
