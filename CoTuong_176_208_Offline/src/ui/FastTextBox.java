/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import util.*;
import javax.microedition.lcdui.Graphics;
import main.Context;

/**
 * This text box only use for display context information and not support for
 * entry. I recommend use this class for all textbox that need display text only.
 * @author dong
 */
public class FastTextBox extends TextBox {

    public FastTextBox(int width, int height, int text_align)
    {
        super(
                0, 
                0,                 
                width,
                height,
                text_align,
                Context.mMe.mTahomaOutlineGreen
              );    
    }    
    
    /**
     * Use with FastTextBox
     * @param content
     */
    public void addFastParagraph(String content)
    {
        this.addParagraph(content, mFont);
        if (!mScrollBarEnable)
        {
            int h = getCurrentHeight();
            if (h > mHeight)
                setScrollBar(true, 0x5AD500, 0x6F6F6F);
        }
    }
    
    public void removeAllParagraph()
    {
        this.mLines.removeAllElements();
        mScrollBarEnable = false;
    }
    
    /**
     * Paint at center of screen
     * @param g
     */
    public void fastPaint(Graphics g, boolean needBackground)
    {
        mNeedBackground = needBackground;
        paint(g, g.getClipWidth() >> 1, g.getClipHeight() >> 1, Graphics.HCENTER | Graphics.VCENTER, false);
    }
    
    private boolean mNeedBackground = false;
    
    public boolean onKeyPressed(int aKey)
    {
        if (aKey == Key.UP || aKey == Key.DOWN)
        {
            if (mScrollBarEnable)
                onScroll(aKey == Key.DOWN);
            return true;
        }               
        return false;
    }
    
    /**
     * Wrong paint. Please don't use with FastTextBox
     * @param g
     * @param x
     * @param y
     * @param align
     * @param drawCursor
     */
    public void paint(Graphics g, int x, int y, int align, boolean drawCursor) 
    {
        int aX = x;
        int aY = y;                
        
        if ((align & Graphics.HCENTER) > 0)
        {
            aX -= (mWidth >> 1);
        }
        else if ((align & Graphics.RIGHT) > 0)
        {
            aX -= mWidth;
        }
        
        if ((align & Graphics.VCENTER) > 0)
        {
            aY -= (mHeight >> 1);
        }
        else if ((align & Graphics.BOTTOM) > 0)
        {
            aY -= (mHeight);
        }
        
        x = aX;
        y = aY;
        int w = mWidth;
        int h = mHeight;
        
        if (mNeedBackground)
        {
            g.setColor(0x5f7a7a);
            g.fillRect(x, y, w, h);
            g.setColor(Utils.darkenColor(0x5f7a7a, 40));
            g.drawRect(x, y, w, h);
        }
        
        int cx = g.getClipX();
        int cy = g.getClipY();
        int cw = g.getClipWidth();
        int ch = g.getClipHeight();
        g.setClip(aX, aY, mWidth, mHeight);
        
        aY += mMarginY;
        
        int charCount = 0;
        for (int i = 0; i < mNumberOfVisibleLine; i++)
        {
            LineRecord aLine = (LineRecord)mLines.elementAt(i + mStartVisibleLine);
            if ((charCount + aLine.mText.length() >= mCurso - 1) && mIsEditable && drawCursor)
            {                
                aY += (aLine.mFont.getHeight() >> 1);
                if ((mAlign & Graphics.HCENTER) > 0)
                {
                    //aLine.mFont.write(g, aLine.mText, aX + (mWidth >> 1), aY, Graphics.HCENTER | Graphics.VCENTER);
                    int xx = aX + (mWidth >> 1) - (aLine.mFont.getWidth(aLine.mText) >> 1);
                    for (int j = 0; j < aLine.mText.length(); j++)
                    {
                        aLine.mFont.write(g, "" + aLine.mText.charAt(j), xx, aY, Graphics.LEFT | Graphics.VCENTER);                        
                        if (charCount + j == mCurso)
                        {
                            if (mIsCursorLight)
                            {
                                g.setColor(0xFFFFFF);
                                mIsCursorLight = false;
                            }
                            else
                            {
                                g.setColor(0x555555);
                                mIsCursorLight = true;
                            }
                            g.drawLine(xx, 
                                    aY + (aLine.mFont.getHeight() >> 1) + 2, 
                                    xx + aLine.mFont.getWidth(aLine.mText.charAt(j)), 
                                    aY + (aLine.mFont.getHeight() >> 1) + 2);
                        }
                        xx += aLine.mFont.getWidth(aLine.mText.charAt(j));  
                    }
                    if (charCount + aLine.mText.length() + 1 == mCurso)
                    {
                            g.setColor(0x0000FF);
                            g.drawLine(xx, 
                                    aY + (aLine.mFont.getHeight() >> 1) + 2, 
                                    xx + aLine.mFont.getWidth(' '), 
                                    aY + (aLine.mFont.getHeight() >> 1) + 2);
                    }
                }
                else if ((mAlign & Graphics.RIGHT) > 0)
                {
                    aLine.mFont.write(g, aLine.mText, aX + mWidth - mMarginX, aY, Graphics.RIGHT | Graphics.VCENTER);
                }
                else 
                {
                    aLine.mFont.write(g, aLine.mText, aX + mMarginX, aY, Graphics.LEFT | Graphics.VCENTER);
                }     
            }
            else
            {
                //System.out.println(aLine.mText);
                aY += (aLine.mFont.getHeight() >> 1);
                if ((mAlign & Graphics.HCENTER) > 0)
                {
                    aLine.mFont.write(g, aLine.mText, aX + (mWidth >> 1), aY, Graphics.HCENTER | Graphics.VCENTER);
                }
                else if ((mAlign & Graphics.RIGHT) > 0)
                {
                    aLine.mFont.write(g, aLine.mText, aX + mWidth - mMarginX, aY, Graphics.RIGHT | Graphics.VCENTER);
                }
                else 
                {
                    aLine.mFont.write(g, aLine.mText, aX + mMarginX, aY, Graphics.LEFT | Graphics.VCENTER);
                }     
                aY += (aLine.mFont.getHeight() >> 1);
                aY += mLineSpace;
            }
        }
        
        g.setClip(cx, cy, cw, ch);
        
        if (mScrollBarEnable)
        {            
            g.setColor(mScrollBarColor);
            g.drawLine(x + mWidth - 6, y, x + mWidth - 6, y + mHeight - 1);            
            g.drawLine(x + mWidth - 5, y, x + mWidth - 5, y + mHeight - 1);
            g.setColor(Utils.lightenColor(mScrollBarColor, 30));
            g.drawLine(x + mWidth - 4, y, x + mWidth - 4, y + mHeight - 1);            
            g.drawLine(x + mWidth - 3, y, x + mWidth - 3, y + mHeight - 1);
            g.setColor(Utils.lightenColor(mScrollBarColor, 60));
            g.drawLine(x + mWidth - 2, y, x + mWidth - 2, y + mHeight - 1);            
            g.drawLine(x + mWidth - 1, y, x + mWidth - 1, y + mHeight - 1);            
            
            int yy = (mStartVisibleLine) * (mHeight - 8) / (mEndVisibleLine + 1) + y + 8;
            g.setColor(Utils.darkenColor(mScrollIndicatorColor, 30));    // The border
            g.fillRect(x + mWidth - 8, yy - 8, 10, 10);
            g.setColor(mScrollIndicatorColor);    // The border
            g.fillRect(x + mWidth - 5, yy - 5, 4, 4);
//            g.setColor(Utils.lightenColor(mScrollIndicatorColor, 30));    // The border
//            g.fillRect(x + mWidth - 2, yy - 2, 2, 2);
        }
    }
}
