/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

public class LineRecord {
    public String mText;
    public UnicodeFont mFont;
    public int mRealWidth;
    public int mHeight;
    
    public LineRecord(String aText, UnicodeFont aFont) {
        mText = aText;
        mFont = aFont;
        mRealWidth = mFont.getWidth(mText);
        mHeight = mFont.getHeight();
    }
}