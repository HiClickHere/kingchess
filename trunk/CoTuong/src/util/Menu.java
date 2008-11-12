/*
 * Menu.java
 *
 * Created on November 1, 2008, 2:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import main.Context;

/**
 *
 * @author dong
 */
public class Menu {
    // Stores the current selected menu item.
    protected int mCurrent;

    // The background color when the menu is enabled.
    protected int mColor;

    // The background color when the menu is disabled.
    protected int mDisabledColor;

    // Stores display text for toggle values, don't free me.
    String mTextOn;
    String mTextOff;

    // Stores the list of items.
    protected Vector mItems;
    
    int mChooserArrX;
    /**
     * Constructs a menu.
     */
    public Menu() {
        mCurrent = 0;
        mTextOn = mTextOff = "";
        
        mItems = new Vector();	//	MenuItem *
    }
    
    /**
     * Adds a menu item.
     * @param item    reference to a string represents the menu item
     * @param toggle  is the added menu item a toggle item?
     * @param checked if it is a toggle item, is it checked or not right now?
     * @param distance  the distance between this item and the item follows right after,
     *                  a value of 20 is specified as default
     * @param width	  the width of the black box of item
     */
    public void addItem(int id, String item, boolean toggle, boolean checked, 
            int distance, int width, int aX, int aY, int align) {
        int checkValue = 0;
        if (toggle) {
            if (checked)
                checkValue = 1;
            else checkValue = -1;
        }
        
        mItems.addElement(new MenuItem(id, item, checkValue, 
                true, distance, width, aX, aY, align));
    }    
    
    public void setItemText(int id, String text) {
        MenuItem item = getItem(id);
        if (item != null)
            item.mCaption = text;
    }
    
    
    /**
     * Sets the display text for toggle menu items.
     * @param textOn  the ON text
     * @param textOff the OFF text
     */
    public void setToggleTexts(String textOn, String textOff) {
        mTextOn = textOn;
        mTextOff = textOff;
    }
    
    
    /**
     * Sets the enabled color and disabled color for the menu background.
     * @param color         the enabled color
     * @param disabledColor the disabled color
     */
    public void setColors(int color, int disabledColor) {
        mColor = color;
        mDisabledColor = disabledColor;
    }
    
    
    /**
     * Retrieve the enabled state of a menu item.
     * @param item  the item to be retrieved its enabled state
     */
    public boolean isEnabled(int id) {
        MenuItem menuItem = getItem(id);
        if (menuItem == null) return false;
        return menuItem.mEnabled;
    }
    
    
    /**
     * Sets a item to be enabled or not.
     * @param item    the item to be set enabled
     * @param enabled enabled or not
     */
    public void setEnabled(int id, boolean enabled) {
        MenuItem menuItem = getItem(id);
        if (menuItem != null) {
            menuItem.mEnabled = enabled;
            if (!enabled && mCurrent >= 0) {
                MenuItem curr = (MenuItem)mItems.elementAt(mCurrent);
                if ( curr == menuItem)
                    mCurrent = nextEnabledItem(mCurrent, true);
            }
        }
    }
    
    public MenuItem getItem(int id) {
        for (int i = 0; i < mItems.size(); i++) {
            MenuItem item = (MenuItem)mItems.elementAt(i);
            if (item.mId == id)
                return item;
        }
        return null;
    }
    
    
    /**
     * Retrieves the checked state of a menu item.
     * @param item  the item to be retrieved its checked state
     */
    public boolean isChecked(int id) {
        MenuItem menuItem = getItem(id);
        
        if (menuItem == null) return false;
        if (menuItem.mChecked <= 0)
            return false;
        return true;
    }
    
    
    /**
     * Sets a toggle item to be checked or not. If the item is not a toggle item,
     * then it will become a toggle item.
     * @param item    the item to be set checked
     * @param checked checked or not
     */
    public void setChecked(int id, boolean checked) {
        MenuItem menuItem = getItem(id);
        if (menuItem != null) {
            if (checked)
                menuItem.mChecked = 1;
            else
                menuItem.mChecked = -1;
        }
    }
    
    
    /**
     * Gets the current selected item.
     * @return the current selected items
     */
    public int selectedItem() {
        if (mCurrent >= 0) {
            MenuItem menuItem = (MenuItem)mItems.elementAt(mCurrent);
            return menuItem.mId;
        }
        return -1;
    }
    
    
    /**
     * Sets an item to be selected.
     * @param item  new item to be selected
     */
    public void setSelection(int id) {
        for (int i = 0; i < mItems.size(); i++) {
            MenuItem item = (MenuItem)mItems.elementAt(i);
            if (item.mId == id) {
                mCurrent = i;
                return;
            }
        }
        
        mCurrent = 0;
        
        return;
    }
    
    
    /**
     * Gets the number of items in the menu.
     * @return the number of items
     */
    public int itemsCount() {
        return mItems.size();
    }
    
    
    /**
     * Draws a toggle slot for correspondent toggle menu item.
     * @param g             pointer to the graphics library used to draw everything
     * @param font            font used to draw the toggle text
     * @param x               the x-coordinate of the slot
     * @param y               the y-coordinate of the slot
     * @param highlightColor  the highlight color
     * @param select          is the correspondent menu item selected or not
     * @param toggleText      the text to be displayed for toggle (often "ON" or "OFF")
     */
    public void drawToggleSlot(Graphics g, UnicodeFont font, int x, int y, int highlightColor, boolean select, String toggleText, boolean drawArrows) {
        int w = 39;
        int h = 14;
        g.setColor(1908006);//0x0);
        g.fillRect(x, y, w, h);
        g.setColor(0x2A2C2F);
        g.fillRect(x, y, w, 1);
        g.setColor(0x696E71);
        g.fillRect(x + w, y, 1, h);
        g.setColor(0x959494);
        g.fillRect(x, y + h, w, 1);
        g.setColor(0x2A2C2F);
        g.fillRect(x, y, 1, h);
        
        if (drawArrows) {
            //	2 arrows
            int tmp[] = {5, 3, 2, 4};
            mChooserArrX++;
            if (mChooserArrX >= 28) mChooserArrX = 0;
            Image arrow_right = Context.mMe.mArrowRight;
            Image arrow_left = Context.mMe.mArrowLeft;
            g.drawImage(arrow_right, x, y + 4, 0);
            g.drawImage(arrow_left, x + 34, y + 4, 0);
        }
        
        if (toggleText != null)
            font.write(g, toggleText, x + ((w+1) >> 1), y + (h >> 1), Graphics.HCENTER | Graphics.VCENTER);
    }
    
    /**
     * Draws a menu item.
     * @param g           pointer to the graphics library used to draw everything
     * @param font          font used to draw the title
     * @param fontHL        font used to draw the highlight title
     * @param fontToggle	 font used to draw the toggle slot
     * @param item          the menu item to be drawed
     * @param y             the y-coordinate of the panel
     * @parma width         the screen's width
     * @param color         the background color of the panel
     * @param borderColor   the border color of the panel
     * @param alignment     the text alignment
     */
    public void drawItem(Graphics g, UnicodeFont font, UnicodeFont fontHL, 
            UnicodeFont fontToggle, int item, int x, int y, 
            int background_color, 
            int border_color_lighter, 
            int border_color_darkter,
            int alignment) 
    {
        if ( (item < 0) || (item >= itemsCount()) ) return;
        
        MenuItem menuItem = (MenuItem)mItems.elementAt(item);
        if (menuItem == null) return;
        
        if (!menuItem.mEnabled) return;
        
        String text = menuItem.mCaption;
        
        int w = menuItem.mWidth;
        int h = font.getHeight() + 4;
        boolean highlight = (item == mCurrent);
        
        //drawBlackRect(g, menuItem.mX, y, w, h, color);
        if ((alignment & Graphics.HCENTER) > 0)
        {
            x -= (menuItem.mWidth >> 1);
        }
        else if ((alignment & Graphics.RIGHT) > 0)
        {
            x -= menuItem.mWidth;
        }
        
        if ((alignment & Graphics.VCENTER) > 0)
        {
            y -= (h >> 1);
        }
        else if ((alignment & Graphics.BOTTOM) > 0)
        {
            y -= h;
        }
        
        g.setColor(background_color);
        g.fillRect(x, y, w, h);
        g.setColor(border_color_lighter);
        g.drawLine(x, y, x, y + h);
        g.drawLine(x, y + h, x + w, y + h);
        g.setColor(border_color_darkter);
        g.drawLine(x + w, y, x + w, y + h - 1);
        g.drawLine(x + 1, y, x + w, y);
        
        if (!highlight) {
            if ((menuItem.mAlign & Graphics.HCENTER) > 0)
                font.write(g, text, x + (w >> 1), y + (h >> 1), Graphics.HCENTER | Graphics.VCENTER);
            else if ((menuItem.mAlign & Graphics.RIGHT) > 0)
                font.write(g, text, x + w, y + (h >> 1), Graphics.RIGHT | Graphics.VCENTER);
            else
                font.write(g, text, x, y + (h >> 1), Graphics.LEFT | Graphics.VCENTER);
        } else {
            if ((menuItem.mAlign & Graphics.HCENTER) > 0)
                fontHL.write(g, text, x + (w >> 1), y + (h >> 1), Graphics.HCENTER | Graphics.VCENTER);
            else if ((menuItem.mAlign & Graphics.RIGHT) > 0)
                fontHL.write(g, text, x + w, y + (h >> 1), Graphics.RIGHT | Graphics.VCENTER);
            else
                fontHL.write(g, text, x, y + (h >> 1), Graphics.LEFT | Graphics.VCENTER);            
        }
        
//        MenuItem current = (MenuItem)mItems.elementAt(mCurrent);
//        // Draws the toggle slot if necessary.
//        int check = menuItem.mChecked;
//        int xx = 20;
//        if (check == 1)
//            drawToggleSlot(g, fontToggle, 126-xx, y - 1, color, highlight, mTextOn, current == menuItem);
//        else if (check == -1)
//            drawToggleSlot(g, fontToggle, 126-xx, y - 1, color, highlight, mTextOff, current == menuItem);
    }         
    
    public void paint(Graphics g, UnicodeFont font, UnicodeFont mHLFont, UnicodeFont fontToggle, int x, int y, int item_align)
    {
        if (font == null)
            return;
        
        int numItems = itemsCount();        
        
        for (int i = 0; i < numItems; i++) {
            MenuItem item = (MenuItem)mItems.elementAt(i);
            if (item == null)
                continue;
            if (!item.mEnabled)
                continue;
            int backColor = item.mEnabled ? mColor : mDisabledColor;
            
            
            drawItem(g, font, mHLFont, fontToggle, 
                     i, 
                     x,
                     (item.mY != -1) ? item.mY : y, 
                     backColor, 
                     Utils.lightenColor(backColor, 30), 
                     Utils.darkenColor(backColor, 30),
                     item_align);
            y += item.mDistance;
        }
    }             
    
    /**
     * Direction keys slot.
     * @param direction specifies UP/DOWN/LEFT/RIGHT (0/1/2/3) direction key is pressed
     */
    public void onDirectionKeys(int direction) {
        MenuItem item;
        switch (direction) {
            case 0:   // UP
                mCurrent = nextEnabledItem(mCurrent, false);
                break;
                
            case 1:   // DOWN
                mCurrent = nextEnabledItem(mCurrent, true);
                break;
            case 2:	//	RIGHT
            case 3:
                item = (MenuItem)mItems.elementAt(mCurrent);
                if (item != null && item.mChecked != 0)
                    item.mChecked = -item.mChecked;
                break;
        }
    }
    
    
    /**
     * On current item selection.
     */
    public void onSelect() {
        /*
        MenuItem *item = (MenuItem*)mItems->elementAt(mCurrent);
        if (!item) return;
        if (item->mChecked != 0)
                item->mChecked = -item->mChecked;
         */
    }
    
    
    /**
     * Finds an enabled item follows right after the specified item.
     * @param item     the specified item index
     * @param forward  the direction is forward or backward?
     * @return the index of the next enabled item to be highlighted, -1 if an error occurs
     */
    public int nextEnabledItem(int item, boolean forward) {
        int count = 0;
        
        boolean loop = true;  // For not receiving the warning "conditional expression is constant".
        while (loop) {
            if (forward) {
                item++;
                if (item == mItems.size())
                    item = 0;
            } else {
                item--;
                if (item < 0)
                    item = mItems.size() - 1;
            }
            
            MenuItem menuItem = (MenuItem)mItems.elementAt(item);
            if (menuItem == null) return -1;
            
            if (menuItem.mEnabled)
                return item;
            
            // In case all the items are disabled, then reports an error.
            count++;
            if (count >= mItems.size()) return -1;
        }
        
        return 0;
    }
    
}
