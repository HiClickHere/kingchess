/*
 * MenuItem.java
 *
 * Created on November 1, 2008, 2:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

import javax.microedition.lcdui.Graphics;

/**
 *
 * @author dong
 */
public class MenuItem {
    
// Specifies the distance between this item and the item follows right after.
    public int mDistance;
    
    // Specifies the item is a toggle menu item or not.
    // Available values for each item are 0, -1, 1.
    // A value of 0 means the correspondent menu item is not a toggle item,
    // a value of -1 means the correspondent menu item is a toggle item, and it is not checked,
    // a value of 1 means the correspondent menu item is a toggle item, and it is checked.
    public int mChecked;
    
    // Specifies the item is enabled or not.
    public boolean mEnabled;
    
    // Reference to a string represents the menu item's caption.
    // Make sure don't free this from inside.
    public String mCaption;
    
    // Width of the item black area
    public int mWidth;
    public int mId;
    public int mY;
    public int mX;
    public int mAlign;
    /**
     * Constructs a menu item with the specified caption and states.
     * @param caption   caption of the item
     * @param checked   toggle state of the item
     * @param enabled   enabled state of the item
     * @param distance  the distance between this item and the item follows right after,
     *                  a value of 20 is specified as default
     * @param width		the width of the item box
     */
    public MenuItem(int id, String caption, int checked, boolean enabled, int distance, int width, int aX, int aY, int align) {
        mAlign = align;
        mX = aX;
        mY = aY;
        mId = id;
        mCaption = caption;
        mChecked = checked;
        mEnabled = enabled;
        mDistance = distance;
        mWidth = width;
    }
}
