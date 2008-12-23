/*
 * TextBox.java
 *
 * Created on November 2, 2008, 12:03 AM
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
public class TextBox {
    public Vector mLines;
    public int mWidth;
    public int mHeight;
    public int mAlign;
    public int mBackgroundColor;
    public int mBorderColorLighter;
    public int mBorderColorDarker;
    public int mMarginX;
    public int mMarginY;
    public int mLineSpace;
    public int mScrollIndicatorColor;
    public int mScrollBarColor;
    public boolean mIsEditable;        
    public boolean mScrollBarEnable;
    public int mLimit;
    
    public final static String[] mCharMap = {
        "0 ",
        "1,.!?;",
        "2abc",
        "3def",
        "4ghi",
        "5jkl",
        "6mno",
        "7pqrs",
        "8tuv",
        "9wxyz",
        "*",
        "# "};
    
    public UnicodeFont mFont;
    
    /** Creates a new instance of TextBox */
    public TextBox(int backColor,
            int borderColor,
            int width,
            int height,
            int text_align,
            UnicodeFont texteditFont) {
        mLines = new Vector();
        mWidth = width;
        mHeight = height;
        mAlign = text_align;
        mBackgroundColor = backColor;
        mBorderColorLighter = Utils.lightenColor(borderColor, 30);
        mBorderColorDarker = Utils.darkenColor(borderColor, 30);
        mMarginX = 12;
        mLineSpace = 4;
        mMarginY = mLineSpace;        
        mStartVisibleLine = 0;
        mNumberOfVisibleLine = 0;
        mIsLockUp = true;
        mIsLockDown = false;
        mScrollBarEnable = false;
        
        mEditableText = new StringBuffer();
        mTextType = NORMAL;
        mLimit = 256;
        mFont = texteditFont;
    }
    
    public void setScrollBar(boolean enableScrollBar, int cursorColor, int lineColor) {
        mScrollBarEnable = enableScrollBar;
        mScrollIndicatorColor = cursorColor;
        mScrollBarColor = lineColor;
    }
    
    public void setEditable(boolean isEditable, int limit)
    {
        mIsEditable = isEditable;
        mLimit = limit;
    }
    
    public void setColor(int backColor, int borderColor)
    {
        mBackgroundColor = backColor;
        mBorderColorLighter = Utils.lightenColor(borderColor, 30);
        mBorderColorDarker = Utils.darkenColor(borderColor, 30);
    }
    
    public int mEndVisibleLine;
    
    public void updateScrollBar()
    {
        if (mIsLockDown)
        {
            mEndVisibleLine = mStartVisibleLine;
            return;
        }

        int i = mLines.size() - 1;
        int y = mMarginY;
        for (; i > -1 ; i--)
        {
            LineRecord aLine = (LineRecord)mLines.elementAt(i);
            y += aLine.mFont.getHeight() + mLineSpace; 
            if (y > mHeight - mMarginY)
                break;
        }
        mEndVisibleLine = i;        
    }
    
    public void addParagraph(String aParagraph, UnicodeFont aFont) {
        if (aParagraph.length() == 0) {
            mLines.addElement(new LineRecord(aParagraph, aFont));
            return;
        }
        int index = 0;
        StringBuffer aWord = new StringBuffer();
        StringBuffer aLine = new StringBuffer();
        int currentW = 0;
        while (index < aParagraph.length()) {
            aWord.setLength(0);
            while (index < aParagraph.length() && aParagraph.charAt(index) != ' ' && index < aParagraph.length()) {
                aWord.append(aParagraph.charAt(index));
//                if (currentW + aFont.getWidth(aWord.toString()) >= mWidth - 2 * mMarginX) //word too long force break line
//                {
//                    mLines.addElement(new LineRecord(aWord.toString(), aFont));
//                    currentW = 0;
//                    aLine.setLength(0);                                        
//                    aWord.setLength(0);                    
//                }
                index++;
            }
            if (aWord.length() > 0)
            {
                aWord.append(" ");
                if (currentW + aFont.getWidth(aWord.toString())< mWidth - 2 * mMarginX) {
                    currentW += aFont.getWidth(aWord.toString());
                    aLine.append(aWord.toString());
                } else {
                    mLines.addElement(new LineRecord(aLine.toString(), aFont));
                    currentW = 0;
                    aLine.setLength(0);
                    aLine.append(aWord.toString());
                    currentW += aFont.getWidth(aWord.toString());
                }
            }
            index++;
        }
        if (aLine.length() > 0) {
            mLines.addElement(new LineRecord(aLine.toString(), aFont));
            aLine.setLength(0);
        }
        aWord.setLength(0);
        
        onScroll(true);
        onScroll(false);
        updateScrollBar();
        
   //     System.out.println("mLines: " + mLines.size());
   //     System.out.println("mVisible: " + mStartVisibleLine + " " + mNumberOfVisibleLine);
    }
    
    public int getCurrentHeight()
    {
        int returnHeight = mLineSpace;        
        for (int i = 0; i < mLines.size(); i++)
        {
            LineRecord aLine = (LineRecord)mLines.elementAt(i);
            returnHeight += aLine.mFont.getHeight();
            returnHeight += mLineSpace;
        }
        return returnHeight;
    }
    
    protected int mStartVisibleLine;
    protected int mNumberOfVisibleLine;
    protected boolean mIsLockDown;
    protected boolean mIsLockUp;        
    
    public void onScroll(boolean isDown)
    {
        if (mStartVisibleLine == 0)
            mIsLockUp = true;
        else
            mIsLockUp = false;
        
        int y = mMarginY;
        for (int i = mStartVisibleLine; i < mLines.size(); i++)
        {
            LineRecord aLine = (LineRecord)mLines.elementAt(i);
            y += aLine.mFont.getHeight() + mLineSpace;                 
        }
        
        if (y >= mHeight) 
            mIsLockDown = false;
        else {
            mIsLockDown = true;
            if (mStartVisibleLine == 0)
                mNumberOfVisibleLine = mLines.size();
        }
        
        if (isDown && !mIsLockDown)
        {
            mStartVisibleLine += 1;
            y = mMarginY;
            int i = mStartVisibleLine;
            for (; i < mLines.size(); i++)
            {
                LineRecord aLine = (LineRecord)mLines.elementAt(i);
                y += aLine.mFont.getHeight() + mLineSpace;                 
                if (y > mHeight - mMarginY)
                    break;
            }
            mNumberOfVisibleLine = i - mStartVisibleLine;
        }
        else if (!isDown && !mIsLockUp)
        {
            mStartVisibleLine -= 1;
            y = mMarginY;
            int i = mStartVisibleLine;
            for (; i < mLines.size(); i++)
            {
                LineRecord aLine = (LineRecord)mLines.elementAt(i);
                y += aLine.mFont.getHeight() + mLineSpace;                 
                if (y > mHeight - mMarginY)
                    break;
            }
            mNumberOfVisibleLine = i - mStartVisibleLine;
        }
    }
    
    public StringBuffer mEditableText;
    protected int mLastInputChar;
    protected int mCurso;
    public final static int NORMAL = 0;
    public final static int NUMBER = 1;
    public int mTextType;
    protected long mLastInputMs;
    protected int mCharIndex;
    
    public String getEditableText()
    {
        return mEditableText.toString();
    }
    
    public void setEditableText(String aText)
    {
        mEditableText = new StringBuffer(aText);
        mLines.removeAllElements();
        addParagraph(mEditableText.toString(), mFont);
        mCurso = mEditableText.length();
    }
    
    public void onInput(int key) {                        
        if (mEditableText.length() > 0 && (key == Key.LEFT  || key == Key.RIGHT)) {
            mLastInputChar = -1;
            if (key == Key.LEFT) mCurso--;
            if (key == Key.RIGHT) mCurso++;
            if (mCurso < 0) mCurso = 0;
            if (mCurso > getEditableText().length())
                mCurso = getEditableText().length();
            return;
        }
        // Maps to the available value.
        if (key == Key.STAR)
            key = 10;
        else if (key == Key.POUND)
            key = 11;
        else if ( (key >= Key.NUM_0) && (key <= Key.NUM_9) )
            key -= Key.NUM_0;
        else if (key == Key.RIGHT) {
            mLastInputChar = -1;
            return;
        } else return;
        //System.out.println("KEY: " + key);
        if ( (key < 0) || (key > 11) ) return;
        
        switch (mTextType) {
            case NORMAL:
                // Characters in the same key.
                if (key == mLastInputChar) {
                    long ms = System.currentTimeMillis();
                    if ((ms - mLastInputMs) >= 1000) {     // Exceeds the delay time of 1 second
                        // Does the content exceed the limitation?
                        if (mEditableText.length() >= mLimit) return;
                        
                        //	new char
                        mCurso++;
                        
                        mCharIndex = 1;
                        if (mCharIndex >= (int)mCharMap[key].length())
                            mCharIndex = 0;
                        //mEditableText->append(mCharMap[key][mCharIndex++]);
                        mEditableText.insert(mCurso - 1, "" + mCharMap[key].charAt(mCharIndex++));
                    } else {
                        if (mCharIndex >= (int)(mCharMap[key].length()))
                            mCharIndex = 0;
                        //mEditableText->setCharAt(mEditableText->length() - 1, mCharMap[key][mCharIndex++]);
                        mEditableText.setCharAt(mCurso-1, mCharMap[key].charAt(mCharIndex++));
                    }
                    mLastInputMs = ms;
                } else {
                    // Does the content exceed the limitation?
                    if (mEditableText.length() >= mLimit) return;
                    
                    mCharIndex = 1;
                    if (mCharIndex >= (int)mCharMap[key].length())
                        mCharIndex = 0;
                    //mEditableText->append(mCharMap[key][mCharIndex++]);
                    mEditableText.insert(mCurso, "" + mCharMap[key].charAt(mCharIndex++));
                    mCurso++;
                    
                    mLastInputChar = key;
                    mLastInputMs = System.currentTimeMillis();
                }
                
                //	new char
                {
                    int lastCur = mCurso;
                    mLines.removeAllElements();
                    addParagraph(mEditableText.toString(), mFont);
                    //clear(false);
                    //addParagraph(mEditableText, mFont);
                    mCurso = lastCur;
                }
                //System.out.println("String: " + mEditableText.toString());
                break;
                
            case NUMBER:
                // Does the content exceed the limitation?
                if (mEditableText.length() >= mLimit) return;
                
                if (key < 10) {
                    //mEditableText->append(mCharMap[key][0]);
                    int lastCur = mCurso;                    
                    mEditableText.insert(mCurso, "" + mCharMap[key].charAt(0));
                        /*
                        clear(false);
                        addParagraph(mEditableText, mFont);
                         */
                    mLines.removeAllElements();
                    addParagraph(mEditableText.toString(), mFont);
                    mCurso = lastCur+1;
                }
                break;
        }        
    }
    
    public boolean onBackspace() {        
        //int len = mEditableText->length();
        if (mCurso > 0) {
            int lastCur = mCurso;            
            mEditableText.deleteCharAt(mCurso - 1);
            mLines.removeAllElements();
            addParagraph(mEditableText.toString(), mFont);
/*
                clear(false);
                addParagraph(mEditableText, mFont);
 */
            mCurso = lastCur - 1;
            if (mCurso < 0) mCurso = 0;
            
            return true;
        }
        
        return false;
    }
    
    public boolean mIsCursorLight;
    
    public void paintInDialog(Graphics g, int x, int y, int align)
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
        
        int cx = g.getClipX();
        int cy = g.getClipY();
        int cw = g.getClipWidth();
        int ch = g.getClipHeight();
        g.setClip(aX, aY, mWidth, mHeight);
        
        aY += mMarginY;
        
        int charCount = 0;
        for (int i = 0; i < mNumberOfVisibleLine; i++) {
            LineRecord aLine = (LineRecord) mLines.elementAt(i + mStartVisibleLine);

            //System.out.println(aLine.mText);
            aY += (aLine.mFont.getHeight() >> 1);
            if ((mAlign & Graphics.HCENTER) > 0) {
                aLine.mFont.write(g, aLine.mText, aX + (mWidth >> 1), aY, Graphics.HCENTER | Graphics.VCENTER);
            } else if ((mAlign & Graphics.RIGHT) > 0) {
                aLine.mFont.write(g, aLine.mText, aX + mWidth - mMarginX, aY, Graphics.RIGHT | Graphics.VCENTER);
            } else {
                aLine.mFont.write(g, aLine.mText, aX + mMarginX, aY, Graphics.LEFT | Graphics.VCENTER);
            }
            aY += (aLine.mFont.getHeight() >> 1);
            aY += mLineSpace;

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
        
        g.setColor(mBackgroundColor);
        g.fillRect(aX, aY, mWidth, mHeight);
        g.setColor(mBorderColorLighter);
        g.drawLine(aX, aY, aX, aY + mHeight);
        g.drawLine(aX, aY + mHeight, aX + mWidth, aY + mHeight);
        g.setColor(mBorderColorDarker);
        g.drawLine(aX + mWidth, aY, aX + mWidth, aY + mHeight - 1);
        g.drawLine(aX + 1, aY, aX + mWidth, aY);
        
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
