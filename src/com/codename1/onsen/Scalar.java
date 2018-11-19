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
public class Scalar {
    private double magnitude;
    private Unit unit;
    
    public static Scalar px(double value) {
        Scalar out = new Scalar();
        out.magnitude = value;
        out.unit = Unit.Pixels;
        return out;
    }
    
    public static Scalar em(double value) {
        Scalar out = new Scalar();
        out.magnitude = value;
        out.unit = Unit.Em;
        return out;
    }
    
    public static Scalar pt(double value) {
        Scalar out = new Scalar();
        out.magnitude = value;
        out.unit = Unit.Points;
        return out;
    }
    
    public static Scalar in(double value) {
        Scalar out = new Scalar();
        out.magnitude = value;
        out.unit = Unit.Inches;
        return out;
    }
    
    public static Scalar pct(double value) {
        Scalar out = new Scalar();
        out.magnitude = value;
        out.unit = Unit.Percent;
        return out;
    }

    @Override
    public String toString() {
        return magnitude+unit.toString();
    }
    
    
}
