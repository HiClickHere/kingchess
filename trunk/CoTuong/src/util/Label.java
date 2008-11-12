/*
 * Label.java
 *
 * Created on October 30, 2008, 3:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author dong
 */
public class Label {
    private int mX, mY;
    private int mWidth, mHeight;
    
    private int mMarginX, mMarginY;
    
    private boolean mBackground;
    private int mBackColor;
    
    private boolean mBorder;
    private int mColor;
    
    private UnicodeFont mFont;
    
    Vector mLines;
    
    public boolean isSpace(char c) {
        return ( (c == ' ') || (c == '\n') );
    }
    
    public void destroyLines() {
        while (mLines.size() != 0) {
            String p = (String)mLines.lastElement();
            mLines.removeElement(p);
        }
    }
    
    public void generateLines(String text) {
        destroyLines();
        mLines.removeAllElements();
        
        if (text == null)
            return;
        
        int length = text.length();
        int curPosition = 0;
        int width = mWidth - mMarginX * 2;
        
        while (curPosition < length) {
            int startPosition = curPosition;
            int endPosition = curPosition;
            
            // Add the first word.
            while ( (curPosition < length) && !isSpace(text.charAt(curPosition)))
                curPosition++;
            
            // Besure that the length of an individual word doesn't exceed the label width
            boolean isLineDone = false;
            
            do {
                int endOfWordPosition = curPosition;
                
                // We just added the first word.  Add the next block of whitespace.
                // The character at curPosition is whitespace.
                while ( (curPosition < length) && (text.charAt(curPosition) == ' ') )
                    curPosition++;
                
                // Check to see how we broke out of the while loop.
                if (curPosition == length) {
                    endPosition = endOfWordPosition;
                    isLineDone = true;
                } else {
                    switch (text.charAt(curPosition)) {
                        case '\n':
                            endPosition = endOfWordPosition;
                            curPosition++;
                            isLineDone = true;
                            break;
                            
                        default:
                            if (mFont.getSubWidth(text, startPosition, curPosition) > width) {
                                endPosition = endOfWordPosition;
                                isLineDone = true;
                            }
                            break;
                    }
                }
                
                if (!isLineDone) {
                    endOfWordPosition = curPosition;
                    
                    // Add the next block of characters.
                    do {
                        curPosition++;
                    } while ( (curPosition < length) && !isSpace(text.charAt(curPosition)) );
                    
                    // See if we overflow.
                    if (mFont.getSubWidth(text, startPosition, curPosition) > width) {
                        endPosition = endOfWordPosition;
                        curPosition = endOfWordPosition;
                        isLineDone = true;
                    }
                }
            } while (!isLineDone);
            
            // Add the line to the list.
            mLines.addElement(text.substring(startPosition, endPosition));
        }
        
        mHeight = mLines.size() * (mFont.getHeight() + mFont.getLineSpacing());
    }
    
    
    public Label(String text, UnicodeFont font, int width, int marginX, int marginY) {
        mMarginX = marginX;
        mMarginY = marginY;
        mWidth = width;
        
        mLines = new Vector();
        mFont = font;
        setText(text);
        
        setBackground(false, 0);
        setBorder(true, 0xFFFFFF);
    }
    
    public UnicodeFont getFont() {
        return mFont;
    }
    
    public int getX() {
        return mX;
    }
    
    public int getY() {
        return mY;
    }
    
    public int getMarginX() {
        return mMarginX;
    }
    
    public int getMarginY() {
        return mMarginY;
    }
    
    public void setPosition(int x, int y) {
        mX = x;
        mY = y;
    }
    
    public void setText(String text) {
        if (text != null)
            generateLines(text);
    }
    
    public void setTextInLine(String text) {
        destroyLines();
        mLines.removeAllElements();
        
        mLines.addElement(text);
        mHeight = mLines.size() * (mFont.getHeight() + mFont.getLineSpacing());
    }
    
    public void setBackground(boolean background, int color) {
        mBackground = background;
        mBackColor = color;
    }
    
    public void setBorder(boolean border, int color) {
        mBorder = border;
        mColor = color;
    }
    
    public int getNumLines() {
        if (mLines == null) return 0;
        return mLines.size();
    }
    
    public int getLinesHeight() {
        return mHeight;
    }
    
    public int getHeight() {
        return mHeight + (mMarginY * 2) - mFont.getLineSpacing();
    }
    
    public void paint(Graphics g, int flags, int d) {
        int yy = mY + d;
        
        if (mBackground) {
            g.setColor(mBackColor);
            g.fillRect(mX, yy, mWidth, getHeight());
        }
        
        if (mBorder) {            
            g.setColor(mColor);
            g.drawRect(mX, yy, mWidth - 1, getHeight() - 1);
        }
        
        if ( (mLines == null) || (mFont == null) ) return;
        
        int x = mX + mMarginX,
                y = yy + mMarginY;
        
        for (int i = 0; i < mLines.size(); i++) {
            String line = (String)mLines.elementAt(i);//->trim();
            if (line.length() == 0)
                continue;
            
            if ((flags & Graphics.HCENTER) == Graphics.HCENTER)
                x = mX + ((mWidth - mFont.getWidth(line)) >> 1);
            else if ((flags & Graphics.RIGHT) == Graphics.RIGHT)
                x = mX + mWidth - mMarginX - mFont.getWidth(line);
            
            mFont.write(g, line, x, y, Graphics.VCENTER);
            
            y += (mFont.getHeight() + mFont.getLineSpacing());
            
            //SAFE_DELETE(line);
        }
    }
    
}
