/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xataface.ui;

import com.codename1.ui.Component;
import com.codename1.ui.Display;
import com.codename1.ui.geom.Point;
import com.codename1.ui.geom.Rectangle;

/**
 *
 * @author shannah
 */
public class UIUtilities {
    public static Rectangle convertRect(Rectangle rectIn, Rectangle rectOut, Component src, Component target) {
        rectOut.setX(convertX(rectIn.getX(), src, target));
        rectOut.setY(convertY(rectIn.getY(), src, target));
        rectOut.setWidth(rectIn.getWidth());
        rectOut.setHeight(rectIn.getHeight());
        return rectOut;
    }
    
    public static Rectangle convertRect(Rectangle rect, Component src, Component target) {
        return convertRect(rect, rect, src, target);
    }
    
    public static Point convertPoint(Point pIn, Point pOut, Component src, Component target) {
        pOut.setX(convertX(pIn.getX(), src, target));
        pOut.setY(convertY(pIn.getY(), src, target));
        return pOut;
    }
    
    public static Point convertPoint(Point p, Component src, Component target) {
        return convertPoint(p, p, src, target);
    }
    
    public static int convertX(int x, Component src, Component target) {
        return x - absX(target) + absX(src);
    }
    
    public static int convertY(int y, Component src, Component target) {
        return y - absY(target) + absY(src);
    }
    
    private static int absX(Component c) {
        return c == null ? 0 : c.getAbsoluteX();
    }
    
    private static int absY(Component c) {
        return c == null ? 0 : c.getAbsoluteY();
    }
    
    public static Rectangle getAbsoluteBounds(Rectangle out, Component c) {
        out.setX(c.getAbsoluteX());
        out.setY(c.getAbsoluteY());
        out.setWidth(c.getWidth());
        out.setHeight(c.getHeight());
        return out;
    }
    
    public static Rectangle getAbsoluteBounds(Component c) {
        return getAbsoluteBounds(new Rectangle(), c);
    }
    
    public static Rectangle getBounds(Rectangle out, Component c) {
        out.setX(c.getX());
        out.setY(c.getY());
        out.setWidth(c.getWidth());
        out.setHeight(c.getHeight());
        return out;
    }
    
    public static Rectangle getBounds(Component c) {
        return getBounds(new Rectangle(), c);
    }
    
    public static Rectangle getAbsoluteOuterBounds(Rectangle out, Component c) {
        out.setX(c.getOuterX() + absX(c.getParent()));
        out.setY(c.getOuterY() + absY(c.getParent()));
        out.setWidth(c.getOuterWidth());
        out.setHeight(c.getOuterHeight());
        return out;
    }
    
    public static Rectangle getAbsoluteOuterBounds(Component c) {
        return getAbsoluteOuterBounds(new Rectangle(), c);
    }
    
    public static boolean isNearPreferredOuterHeight(Rectangle r, Component c, float thresholdMM) {
        return Math.abs(c.getOuterPreferredH() - r.getHeight()) <= px(thresholdMM);
    }
    
    public static boolean isNearPreferredOuterWidth(Rectangle r, Component c, float thresholdMM) {
        return Math.abs(c.getOuterPreferredW() - r.getWidth()) <= px(thresholdMM);
    }
    
    public static int px (float mm) {
        return Display.getInstance().convertToPixels(mm);
    }
    
    public static int nonZero(int val, int defaultValue) {
        return val == 0 ? defaultValue : val;
    }
}