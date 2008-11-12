/*
 * ImageFont.java
 *
 * Created on October 30, 2008, 3:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author dong
 */
public class ImageFont {
    
    public final static int HLEFT = Graphics.LEFT;
    public final static int HCENTER = Graphics.HCENTER;
    public final static int HRIGHT = Graphics.RIGHT;
    public final static int VTOP = Graphics.TOP;
    public final static int VCENTER = Graphics.VCENTER;
    public final static int VBOTTOM = Graphics.BOTTOM;
    public final static int NUM_CHARACTERS = 58;
    
    // Stores the widths of the characters: 26 letters, 10 digits, 12 symbols of !@#$%^&*()+-=/<>?.,:;'
    public int[] mWidths;
    
    // Space width between the 2 continous words.
    public int mSpaceWidth;
    
    // Spacing width between the 2 continous characters.
    public int mSpacingWidth;
    
    // Stores the line spacing height.
    public int mLineSpacing;
    
    // Stores the height of the characters.
    public int mHeight;
    
    // The font's image comes here.
    public Image mImage;
    
    public int getIndex(char c) {
        int index = -1;
        switch (c) {
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
                index = c - 'A';
                break;
                
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                index = c - '0' + 26;
                break;
                
            case '!':
                index = 36;  // The first symbol starts after 26 letters and 10 digits.
                break;
            case '@':
                index = 37;
                break;
            case '#':
                index = 38;
                break;
            case '$':
                index = 39;
                break;
            case '%':
                index = 40;
                break;
            case '^':
                index = 41;
                break;
            case '&':
                index = 42;
                break;
            case '*':
                index = 43;
                break;
            case '(':
                index = 44;
                break;
            case ')':
                index = 45;
                break;
            case '+':
                index = 46;
                break;
            case '-':
                index = 47;
                break;
            case '=':
                index = 48;
                break;
            case '/':
                index = 49;
                break;
            case '<':
                index = 50;
                break;
            case '>':
                index = 51;
                break;
            case '?':
                index = 52;
                break;
            case '.':
                index = 53;
                break;
            case ',':
                index = 54;
                break;
            case ':':
                index = 55;
                break;
            case ';':
                index = 56;
                break;
            case '\'':
                index = 57;
                break;
        }
        
        return index;
    }
    
    public ImageFont() {
        
    }
    
    public ImageFont(Image aImage) throws Exception {
        mWidths = new int[NUM_CHARACTERS];
        for (int i = 0; i < NUM_CHARACTERS; i++)
            mWidths[i] = 0;
        mImage = aImage;
        mHeight = mSpaceWidth = mSpacingWidth = 0;
    }
    
    public ImageFont clone() {
        ImageFont font = new ImageFont();
        
        for (int i = 0; i < NUM_CHARACTERS; i++)
            font.mWidths[i] = mWidths[i];
        
        font.mHeight = mHeight;
        
        font.mSpaceWidth = mSpaceWidth;
        font.mSpacingWidth = mSpacingWidth;
        font.mLineSpacing = mLineSpacing;
        
        font.mImage = mImage;
        
        return font;
    }
    
    public void setColor(int idx, int color) {
//            byte r = (byte)((color & 0xff0000) >> 16);
//            byte g = (byte)((color & 0x00ff00) >> 8);
//            byte b = (byte)((color & 0x0000ff) >> 0);
//
//            setColor(idx, r, g, b);
    }
    
    public void setColor(int idx, int r, int g, int b) {
        //mImage.getGraphics().replaceColorByIndex(idx, r, g, b);
    }
    
    public void setOutlineColor(int color) {
        
    }
    
    /**
     * Sets the outline color for this font.
     * @param r red part of the specified color
     * @param g green part of the specified color
     * @param b blue part of the specified color
     */
    public void setOutlineColor(int r, int g, int b) {
        
    }
    
    /**
     * Gets the width of a specified character.
     * @param c the specified character
     * @return the width of the input character
     */
    public int getWidth(char c) {
        if (c == ' ')
            return mSpaceWidth;
        
        int index = getIndex(c);
        if (index == -1) return 0;
        return mWidths[index] + mSpacingWidth;
    }
    
    /**
     * Gets the width of a specified text.
     * @param text the specified text
     * @return the width of the input text
     */
    public int getWidth(String text) {
        int width = 0;
        int length = text.length();
        
        for (int i = 0; i < length; i++) {
            if (text.charAt(i) == '[')	//	see tutorial font for more detail
            {
                i+=2;
            } else if (text.charAt(i) == ']')
                i++;
            width += getWidth(text.charAt(i));
        }
        
        return width;
    }
    
    /**
     * Gets the width of a sub string of the given text.
     * @param text  the given text
     * @param start the start position
     * @param end   the end position
     * @return the width of the sub string
     */
    public int getSubWidth(String text, int start, int end) {
        int width = 0;
        for (int i = start; i < end; i++) {
            if (text.charAt(i) == '[')	//	see tutorial font for more detail
            {
                i++;
                continue;
            } else if (text.charAt(i) == ']') {
                continue;
            }
            width += getWidth(text.charAt(i));
        }
        
        return width;
    }
    
    /**
     * Gets the space width.
     * @return the space width
     */
    public int getSpaceWidth() {
        return mSpaceWidth;
    }
    
    /**
     * Gets the space width.
     * @return the space width
     */
    public int getSpacingWidth() {
        return mSpacingWidth;
    }
    
    /**
     * Gets the line spacing height.
     * @return the line spacing height
     */
    public int getLineSpacing() {
        return mLineSpacing;
    }
    
    /**
     * Gets the height of this font.
     * @return the height of the font
     */
    public int getHeight() {
        return mHeight;
    }
    
    /**
     * Sets the parameters for this font, includes an array of widths of the characters and the height.
     * @param widths the widths of the characters, the characters are in the following order:
     * 26 letters, 10 digits, 12 symbols of !@#$%^&*()+-=/<>?.,:;'
     * @param height the height of the characters
     */
    public void setParams(int widths[], int height, int spaceWidth, int spacingWidth, int lineSpacing) {
        for (int i = 0; i < NUM_CHARACTERS; i++)
            mWidths[i] = widths[i];
        mHeight = height;
        mSpaceWidth = spaceWidth;
        mSpacingWidth = spacingWidth;
        mLineSpacing = lineSpacing;
    }
    
    public void write(Graphics g, String text, int x, int y, int flags) {
        if (text == null) return;
        
        int tx = x,
                ty = y;
        if ((flags & HCENTER) == HCENTER)
            tx -= (getWidth(text) >> 1);
        else if ((flags & HRIGHT) == HRIGHT)
            tx -= getWidth(text);
        if ((flags & VCENTER) == VCENTER)
            ty -= (getHeight() >> 1);
        else if ((flags & VBOTTOM) == VBOTTOM)
            ty -= getHeight();                
        
        int cx = g.getClipX(),
                cy = g.getClipY(),
                cw = g.getClipWidth(),
                ch = g.getClipHeight();
        
        int length = text.length();
        for (int i = 0; i < length; i++) {            
            char c = text.charAt(i);
            //System.out.println("" + c);
            
            if (c == ' ') {
                tx += mSpaceWidth;
                continue;
            }
            
            int index = getIndex(c),
                    width = mWidths[index],
                    height = mHeight;
            
            //System.out.println("index: " + index);
            
            if (index == -1) continue;
            
            int clipX = tx,
                    clipY = ty;
            
            // Interlace with the current clipping area.
            // Clip horizontally
            if (tx < cx) {
                int d = cx - tx;
                clipX += d;
                width -= d;
            }
            if ((tx + width) > (cx + cw)) {
                int d = tx + width - cx - cw;
                width -= d;
            }
            
            // Clip vertically
            if (ty < cy) {
                int d = cy - ty;
                clipY += d;
                height -= d;
            }
            if ((ty + height) > (cy + ch)) {
                int d = ty + height - cy - ch;
                height -= d;
            }
            //if (height <= 0) continue;
            
            // Draw the character
            if (width > 0 && height > 0) {
                g.setClip(clipX, clipY, width, height);
                g.drawImage(mImage, tx, ty - (mHeight * index), 0);
                //System.out.println("" + clipX + " " + clipY + " " + width + " " + height);
            }
            
            tx += (mWidths[index] + mSpacingWidth);
        }
        
        g.setClip(cx, cy, cw, ch);
    }
    
    public void write(Graphics g, int number, int x, int y, int flags, int digits) {
        StringBuffer s1 = new StringBuffer();
        StringBuffer s2 = new StringBuffer();
        
        s1.append(number);
        
        int numZeros = digits - s1.length();
        if (numZeros > 0) {
            for (int i = 0; i < numZeros; i++)
                s2.append("0");
            s2.append(s1);
            write(g, s2.toString(), x, y, flags);
        } else
            write(g, s1.toString(), x, y, flags);
    }
    
}
