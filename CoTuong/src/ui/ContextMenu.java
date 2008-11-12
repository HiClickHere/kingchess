/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import util.*;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author dong
 */
public class ContextMenu extends Menu {
    
    public UnicodeFont mNormalFont;
    public UnicodeFont mHighLightFont;

    public ContextMenu(UnicodeFont aNormalFont, UnicodeFont aHighLightFont)
    {
        super();
        mNormalFont = aNormalFont;
        mHighLightFont = aHighLightFont;
    }
    
    public void paint(Graphics g, UnicodeFont font, UnicodeFont mHLFont, UnicodeFont fontToggle, int x, int y, int item_align)
    {
        //do nothing
    }
    
    public int getWidth()
    {
        int width = 0;
        for (int i = 0; i < itemsCount(); i++) {
            MenuItem item = (MenuItem)mItems.elementAt(i);
            if (item == null)
                continue;
            if (!item.mEnabled)
                continue;
            int awidth = mHighLightFont.getWidth(item.mCaption);
            width = (width > awidth) ? width : awidth;
        }
        return width;
    }
    
    public int getHeight()
    {
        return itemsCount() * (mHighLightFont.getHeight() + 4);
    }
    
    public void paint(Graphics g, int x, int y, int align)
    {
        int numItems = itemsCount();                 
        
        int w = getWidth();
        int h = getHeight();
        
        if ((align & Graphics.HCENTER) > 0)
        {
            x -= (w >> 1);            
        }        
        else if ((align & Graphics.RIGHT) > 0)
        {
            x -= w;
        }
        
        if ((align & Graphics.VCENTER) > 0)
        {
            y -= (h >> 1);
        }
        else if ((align & Graphics.BOTTOM) > 0)
        {
            y -= h;
        }
        
        g.setColor(mColor);
        g.fillRect(x, y, w, h);
        g.setColor(Utils.darkenColor(mColor, 40));
        g.drawRect(x, y, w, h);
        
        for (int i = 0; i < numItems; i++) {
            MenuItem item = (MenuItem)mItems.elementAt(i);
            if (item == null)
                continue;
            if (!item.mEnabled)
                continue;
            int backColor = item.mEnabled ? mColor : mDisabledColor;                           
            y += 2;
            if (i != mCurrent) {
                if ((item.mAlign & Graphics.HCENTER) > 0)
                    mNormalFont.write(g, item.mCaption, x + (w >> 1), y + (mHighLightFont.getHeight() >> 1), Graphics.HCENTER | Graphics.VCENTER);
                else if ((item.mAlign & Graphics.RIGHT) > 0)
                    mNormalFont.write(g, item.mCaption, x + w, y + (mHighLightFont.getHeight() >> 1), Graphics.RIGHT | Graphics.VCENTER);
                else
                    mNormalFont.write(g, item.mCaption, x, y + (mHighLightFont.getHeight() >> 1), Graphics.LEFT | Graphics.VCENTER);
            } else {
                if ((item.mAlign & Graphics.HCENTER) > 0)
                    mHighLightFont.write(g, item.mCaption, x + (w >> 1), y + (mHighLightFont.getHeight() >> 1), Graphics.HCENTER | Graphics.VCENTER);
                else if ((item.mAlign & Graphics.RIGHT) > 0)
                    mHighLightFont.write(g, item.mCaption, x + w, y + (mHighLightFont.getHeight() >> 1), Graphics.RIGHT | Graphics.VCENTER);
                else
                    mHighLightFont.write(g, item.mCaption, x, y + (mHighLightFont.getHeight() >> 1), Graphics.LEFT | Graphics.VCENTER);            
            }
            y += mHighLightFont.getHeight() + 2;
        }
    }
}
