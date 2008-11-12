/*
 * Dialog.java
 *
 * Created on November 1, 2008, 4:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

import core.Screen;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import main.Context;

/**
 *
 * @author dong
 */
public class Dialog extends Screen {    
    TextBox mTextBox;
    UnicodeFont mTextFont;
    UnicodeFont mTitleFont;
    String mContent;
    String mTitle;
    Screen mBackScreen;
    Screen mNextScreen;
    int mWidth;
    int mHeight;
    
    Image mBackgroundImage;
    /** Creates a new instance of Dialog */
    public Dialog(
            Context aContext,
            String title, 
            String content, 
            UnicodeFont textFont, 
            UnicodeFont titleFont,
            int width,
            int height,
            Screen backScreen,
            Screen nextScreen,
            Screen previousScreen
            )
    {
        super(aContext);
        mTextFont = textFont;
        mTitleFont = titleFont;
        mTitle = title;
        mContent = content;
        
        mWidth = width;
        mHeight = height;
        
        mTextBox = new TextBox(
                0, 
                Utils.darkenColor(0x00FF00, 30),                 
                mWidth,
                mHeight,
                Graphics.HCENTER | Graphics.VCENTER,
                mTextFont
                );               
        mTextBox.mLineSpace = 2;
        mTextBox.mMarginY = 4;
        mTextBox.addParagraph(mTitle, mTitleFont);
        mTextBox.addParagraph(mContent, mTextFont);
        
        // reallocation for better align
        mHeight = mTextBox.mLines.size() * mTextFont.getHeight() + (mTextBox.mLines.size() - 1) * mTextBox.mLineSpace
                + 2  * mTextBox.mMarginY;
        mTextBox.mHeight = mHeight;
        
        mBackScreen = backScreen;
        mNextScreen = nextScreen;            
        try {
            mBackgroundImage = Image.createImage(getWidth(), getHeight());
            Graphics g = mBackgroundImage.getGraphics();
            previousScreen.setSoftKey(-1, -1, -1);
            previousScreen.paint(g);            
        } catch (Exception e)
        {
            mBackgroundImage = null;
        }
        setSoftKey(-1, -1, SOFTKEY_OK);
    }    
    
    public void keyPressed(int keyCode)
    {
        if (keyCode == Key.SELECT || keyCode == Key.SOFT_RIGHT
                && mRightSoftkey == SOFTKEY_OK)
        {
            if (mNextScreen != null)
                mContext.setScreen(mNextScreen);
        }                        
        else if (keyCode == Key.SOFT_LEFT && 
                (mLeftSoftkey == SOFTKEY_BACK || mLeftSoftkey == SOFTKEY_CANCEL))
        {
            if (mBackScreen != null)
                mContext.setScreen(mBackScreen);
        }
    }
    
    public void onTick(long aMilliseconds)
    {
        //repaint();
        //serviceRepaints();
    }
    
    public void paint(Graphics g)
    {
        paint(g, getWidth() >> 1, getHeight() >> 1, Graphics.HCENTER | Graphics.VCENTER);
    }
    
    public void paint(Graphics g, int x, int y, int align)
    {
        int aX = x;
        int aY = y;
        if (mBackgroundImage != null)
        {
            g.drawImage(mBackgroundImage, 0, 0, 0);
        }
        else
        {
            g.setColor(0x0);
            g.fillRect(0, 0, getWidth(), getHeight());
        }   
        
        if ((align & Graphics.HCENTER) > 0)
        {
            x -= mTextBox.mWidth >> 1;
        }
        else if ((align & Graphics.RIGHT) > 0)
        {
            x -= mTextBox.mWidth;
        }
        
        int w = mTextBox.mWidth;
        int h = mTextBox.mLines.size() * (mTextBox.mFont.mHeight + 4);
        
        if ((align & Graphics.VCENTER) > 0)
        {
            y -= h >> 1;
        }
        else if ((align & Graphics.BOTTOM) > 0)
        {
            y -= h;
        }
        
        g.setColor(0x5f7a7a);
        g.fillRect(x, y, w, h);
        g.setColor(Utils.darkenColor(0x5f7a7a, 40));
        g.drawRect(x, y, w, h);
        
        mTextBox.paintInDialog(g, x, y, 0);
        
        drawSoftkey(g);
    }
}
