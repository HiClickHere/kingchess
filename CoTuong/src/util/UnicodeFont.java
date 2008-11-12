/*
 * UnicodeFont.java
 *
 * Created on October 31, 2008, 10:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

import java.io.DataInputStream;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author dong
 */
public class UnicodeFont {
    
// Stores the widths of the characters: 26 letters, 10 digits, 12 symbols of !@#$%^&*()+-=/<>?.,:;'
    public int mWidths[];
    
    public int mImgOffset[];
    
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
    
    // The number of characters
    public int mNumberCharacters;
    
    public char mUnicodeCode[];
    public int mCharIndex[];
    
    public boolean mIsOutline;
    
    public UnicodeFont(String imgPath, String dataPath, boolean isOutline, int color, int outlineColor) throws Exception {
        mIsOutline = isOutline;
        mImage = Image.createImage(imgPath);
        
        Image aImg = Image.createImage(mImage.getWidth(), mImage.getHeight());
        Graphics aG = aImg.getGraphics();
        aG.setColor(0xFF00FF);
        aG.fillRect(0, 0, aImg.getWidth(), aImg.getHeight());
        aG.drawImage(mImage, 0, 0, 0);
        
        int aARGB[] = new int[mImage.getWidth() * mImage.getHeight()];
        aImg.getRGB(aARGB, 0, mImage.getWidth(), 0, 0, mImage.getWidth(), mImage.getHeight());
        int aARGB2[] = new int[mImage.getWidth() * mImage.getHeight()];
        for (int i = 0; i < aARGB.length; i++)
        {
            if (!mIsOutline)
            {
                if (aARGB[i] == 0xFF000000)
                    aARGB2[i] = color;
                else
                    aARGB2[i] = 0x0;
            }
            else
            {
                if (aARGB[i] == 0xFFFFFFFF)
                    aARGB2[i] = color;
                else if (aARGB[i] == 0xFFFF0000)
                    aARGB2[i] = outlineColor;
                else    
                    aARGB2[i] = 0x0;
            }
        }
        mImage = Image.createRGBImage(aARGB2, mImage.getWidth(), mImage.getHeight(), true);
        
        InputStream is = this.getClass().getResourceAsStream(dataPath);
        DataInputStream aDataInput = new DataInputStream(is);        
        
        mNumberCharacters = aDataInput.readShort();
        mWidths = new int[mNumberCharacters];
        mUnicodeCode = new char[mNumberCharacters];
        mImgOffset = new int[mNumberCharacters];
        mCharIndex = new int[mNumberCharacters];
        
        mImgOffset[0] = 0;
        for (int i = 0; i < mNumberCharacters; i++) {
            mCharIndex[i] = i;
            mUnicodeCode[i] = aDataInput.readChar();
            mWidths[i] = aDataInput.readShort();            
            // caculating offset of font image for each characters
            if (i > 0) {
                mImgOffset[i] = mImgOffset[i - 1] + mWidths[i - 1] + 4;                
            } else {
                mImgOffset[i] = 4;                
            }                                    
            //if (mIsOutline)
                //System.out.println("mWidth: " + mUnicodeCode[i] + " " + mWidths[i] + " " + mImgOffset[i]);
        }
        
        if (mIsOutline)
            for (int i = 0; i < mNumberCharacters; i++) {            
                mWidths[i] += 2;
                mImgOffset[i] -= 1;
            }
                
        mHeight = mImage.getHeight();
        mSpaceWidth = mWidths[0];
        if (mIsOutline)
            mSpacingWidth = -1;
        else
            mSpacingWidth = 0;
        //do sort for binary search
        for (int i = 0; i < mNumberCharacters - 1; i++)
            for (int j = i + 1; j < mNumberCharacters; j++)
                if (mUnicodeCode[i] > mUnicodeCode[j]) {
            char mid = mUnicodeCode[i];
            mUnicodeCode[i] = mUnicodeCode[j];
            mUnicodeCode[j] = mid;
            
            int imid = mCharIndex[i];
            mCharIndex[i] = mCharIndex[j];
            mCharIndex[j] = imid;
        }        
        aDataInput.close();
    }
    
    
    /**
     * Sets the color for this font.
     * @param color the specified color in hex format
     */
    public void setColor(int idx, int color) {
    }
    
    
    /**
     * Sets the color for this font.
     * @param r red part of the specified color
     * @param g green part of the specified color
     * @param b blue part of the specified color
     */
    public void setColor(int idx, int r, int g, int b) {
    }
    
    
    /**
     * Gets the width of a specified character.
     * @param c the specified character
     * @return the width of the input character
     */
    public int getWidth(char c) {
        int index = getIndex(c);
        if (index == -1)
            return mSpaceWidth;        
        return mWidths[index];
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
            width += (getWidth(text.charAt(i)) + mSpacingWidth);
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
            width += getWidth(text.charAt(i));
        }
        
        return width;
    }
    
    /**
     * Gets the space width between the 2 contious words.
     * @return the space width
     */
    public int getSpaceWidth() {
        return mSpaceWidth;
    }
    
    
    /**
     * Gets the spacing width between the 2 continous characters.
     * @return the spacing width
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
    
    public void write(Graphics g, String text, int x, int y, int flags) {
        if (text == null) return;
        
        int tx = x,
                ty = y;
        if ((flags & Graphics.HCENTER) == Graphics.HCENTER)
            tx -= (getWidth(text) >> 1);
        else if ((flags & Graphics.RIGHT) == Graphics.RIGHT)
            tx -= getWidth(text);
        if ((flags & Graphics.VCENTER) == Graphics.VCENTER)
            ty -= (getHeight() >> 1);
        else if ((flags & Graphics.BOTTOM) == Graphics.BOTTOM)
            ty -= getHeight();
        
        int cx = g.getClipX(),
                cy = g.getClipY(),
                cw = g.getClipWidth(),
                ch = g.getClipHeight();
        
        int length = text.length();
        
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            int char_index = getIndex(c);
            
            // cannot recognize, treat it as space
            if (char_index == -1) {
                c = ' ';
                char_index = 0;
            }
            
            int char_width = mWidths[char_index];
            int char_offset = mImgOffset[char_index];
            
            // skip the space
            if (c != ' ') {
                g.setClip(tx, ty, char_width, getHeight());
                g.drawImage(mImage, tx - char_offset, ty, 0);
            }
            
            tx += char_width + mSpacingWidth;
        }
        
        g.setClip(cx, cy, cw, ch); // restore the clip
    }            
    
    /**
     * Retrieves the order of the specified character in the image.
     * @param c the specified character
     * @return the order of the character in the image
     */
    public int getIndex(char c) {
        // binary search
        int start = 0;
        int end = mNumberCharacters;
        int mid;
        while (start < end - 1) {
            mid = (end + start)/2;
            if (mUnicodeCode[mid] > c) {
                end = mid;
            } else if (mUnicodeCode[mid] < c) {
                start = mid;
            } else
                return mCharIndex[mid];
        }
        
        if (mUnicodeCode[start] == c)
            return mCharIndex[start];
        
        return -1; //not found
    }
    
}
