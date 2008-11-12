/*
 * TextBoxItem.java
 *
 * Created on October 29, 2008, 3:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author dong
 */
public class TextBoxItem {
    
    public final static int SCROLLING_STEP = 5;
    public final static int SC_WIDTH = 4;
    
    int mX, mY, mWidth, mHeight;
    int mMarginX, mMarginY;
    int mParaY;
    int mViewY;
    // Specifies the text alignment.
    int mAlignment;
    // The limited number of characters allowed to this text box.
    // This is useful only when the text box is editable.
    int mLimit;
    // Specifies the type of the text box.
    // Available values are NORMAL (0) and NUMBER (1).
    int mTextType;
    // Stores the last input character for the next input processing.
    // This is useful only when the text box is editable.
    int mLastInputChar;
    // Specifies the index into the character map of a key (from 0 to 9 plus STAR and POUND).
    int mCharIndex;
    // Stores the last input milliseconds for the next input processing.
    // This is useful only when the text box is editable.
    long mLastInputMs;
    int mBackColor;
    
    UnicodeFont mFont;
    
    //ScrollBar *mScroller;
    int mScrollMin;
    int mScrollMax;
    int mScrollValue;
    int mScrollX;
    int mScrollY;
    int mScrollLength;
    
    boolean mHighlight;
    boolean mIsVisible;
    
    // Stores the editable text when the text box's mode is editable.
    // When the text box's mode is read-only, this field should be null.
    // If it is editable, only one paragraph is available.
    StringBuffer mEditableText;
    
    Vector mParagraphs;	//	Label*
    
    Vector mImages;	//	EmbeddedImage
    
    // Character map for 10 number keys and STAR and POUND key.
    public final static String[] mCharMap = {
        "0",
        "1,.!?;",
        "2ABC",
        "3DEF",
        "4GHI",
        "5JKL",
        "6MNO",
        "7PQRS",
        "8TUV",
        "9WXYZ",
        "*",
        "# "};
    
    boolean mEditable;
    
    
/*    void resetWord();
    int nextWord();
 
    int linesCount();
    //bool needScrollBar();
 
    void drawText(mlib_GL *g);*/
    
    public final static int NORMAL = 0;
    public final static int NUMBER = 1;
    
    public TextBoxItem(int x, int y, int width, int height, int backColor) {
        mIsVisible = true;
        mEditable = false;
        mCurso = 0;
        mFont = null;
        //mScroller = 0;
        mScrollValue = -1;
        mEditableText = new StringBuffer("");
        
        mCharIndex = 1;
        mLastInputChar = -1;
        mLastInputMs = 0;
        
        mParagraphs = new Vector();
        mImages = new Vector();
        
        setGeometry(x, y, width, height, 8, 10);
        setColor(backColor);
        
        setAlignment(Graphics.LEFT);
        
        setLimit(0);
        setTextType(NORMAL);
    }
    
    public void enableEditable(boolean editable) {
        mEditable = editable;
    }
    
    public boolean isEditableEnabled() {
        return mEditable;
    }
    
    public void setGeometry(int x, int y, int width, int height, int marginX, int marginY) {
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
        
        setMargin(marginX, marginY);
        
        mViewY = 0;
        
        mParaY = 0;
        // TODO: Update the paragraphs here!
    }
    
    public void setMargin(int marginX, int marginY) {
        mMarginX = marginX;
        mMarginY = marginY;
    }
    
    public void setColor(int backColor) {
        mBackColor = backColor;
    }
    
    public void setFont(UnicodeFont font) {
        mFont = font;
    }
    
    /**
     * Set the text alignment.
     * @param alignment the text alignement
     */
    public void setAlignment(int alignment) {
        mAlignment = alignment;
    }
    
    /**
     * Sets the content limitation to the text box.
     * This is useful only when the text box is editable.
     * @param limit the content limitation
     */
    public void setLimit(int limit) {
        mLimit = limit;
    }
    
    /**
     * Sets the type of acceptable text to be inputted.
     * Available types are normal text and number-only text.
     * @param type  the type of text box
     */
    public void setTextType(int type) {
        mTextType = type;
    }
    
    /**
     * Sets the editable mode for this text box. This will make the content to be empty.
     * so make sure to call this before anything else relevent to the content.
     * @param editable  the text box is editable or not
     */
    public void setEditable(boolean editable) {
        clear(true);
        mEditableText = null;
        if (editable)
            mEditableText = new StringBuffer("");
        
        mCurso = 0;
    }
    
    /**
     * How many characters in this textbox.
     * @return the number of characters in this textbox.
     */
    public int charCount() {
        return mEditableText.length();
    }
    
    /**
     * Retrieves the editable text.
     * @return the reference to the editable text
     */
    public String getEditableText() {
        return mEditableText.toString();
    }
    
    public void setEditableText(String text) {
        if (text != null)
            mEditableText = new StringBuffer(text);
        else
            mEditableText = new StringBuffer("");
        
        clear(false);
        addParagraph(mEditableText.toString(), mFont, 0, 0);
    }
    
    public void addParagraph(String text, UnicodeFont font, int indent, int lineSpacing) {
        if (text != null)
            mCurso = text.length();
        else
            mCurso = 0;
        
        Label label = new Label(text, font, mWidth - indent - 6, mMarginX, lineSpacing);
        label.setBorder(false, 0xFFFFFF);
        label.setPosition(mX + indent, mParaY);
        mParagraphs.addElement(label);
        mParaY += label.getLinesHeight() + lineSpacing;
        
        // If scrollbar is needed...
        if (mParaY > (mHeight - (mMarginY * 2) + 4)) {
            int viewArea = mHeight - mMarginY * 2;
            
            mScrollMin = 0;
            mScrollMax = (short)(mParaY - viewArea + 1);
            mScrollValue = 0;
            mScrollX = (short)(mX + mWidth - SC_WIDTH - 3);
            mScrollY = (short)(mY + 3);
            mScrollLength =(short)(mHeight - 7);
            
                /*
                if (mScroller == 0)
                {                               // If no scrollbar is created before...
                        mScroller = new ScrollBar(SC_VERTICAL, 0, mParaY - viewArea + 1, 0);
                        mScroller->setGeometry(mX + mWidth - SC_WIDTH - 3, mY + 3, mHeight - 7);
                        mScroller->setColors(0x6F6F6F, 0x5AD500);
                }
                else
                        mScroller->setParams(SC_VERTICAL, 0, mParaY - viewArea + 1, 0);
                 */
        } else {
            mScrollValue = -1;
            //SAFE_DELETE(mScroller);
        }
    }
    
    public void addImage(Image image, int x, int paragraph, int line, int spacing) {
        EmbeddedImage embImage = new EmbeddedImage(image);
        embImage.setLocation(x, paragraph, line, spacing);
        mImages.addElement(embImage);
    }
    
    public void clear(boolean alsoEditableText) {
        while (mParagraphs.size() != 0) {
            Label p = (Label)mParagraphs.lastElement();
            mParagraphs.removeElement(p);
        }
        
        while (mImages.size() != 0) {
            EmbeddedImage p = (EmbeddedImage)mImages.lastElement();
            mImages.removeElement(p);
        }
        
        if ( alsoEditableText && mEditableText != null ) {
            mEditableText = new StringBuffer("");
        }
        
        mViewY = 0;
        mParaY = 0;
        mCurso = 0;
    }
    
    public void paint(Graphics g) {
        if (!mIsVisible) return;
        int cx = g.getClipX(),
                cy = g.getClipY(),
                cw = g.getClipWidth(),
                ch = g.getClipHeight();
        
        drawArea(g);
        
        g.setClip(mX + mMarginX, mY + mMarginY, mWidth - mMarginX * 2 + 1, mHeight - mMarginY * 2 + 1);
        
        for (int i = 0; i < mParagraphs.size(); i++) {
            Label para = (Label)mParagraphs.elementAt(i);
            if (para == null) continue;
            if ((para.getY() + para.getHeight() - mViewY) < 0) continue;
            if ((para.getY() - mViewY) > mHeight) break;
            para.paint(g, mAlignment, mY + mMarginY - mViewY);
        }
        
        for (int i = 0; i < mImages.size(); i++) {
            EmbeddedImage image = (EmbeddedImage)mImages.elementAt(i);
            if (image == null) continue;
            Label para = (Label)mParagraphs.elementAt(image.mParagraph);
            if (para != null) {
                int y = (para.getY() + para.getMarginY() + para.getFont().getHeight() - image.mImage.getHeight()) + image.mSpacing;
                g.drawImage(image.mImage, image.mX, mY + mMarginY + y - mViewY, 0);
            }
        }
        
        g.setClip(cx, cy, cw, ch);
        
        /*
        if (mScroller != 0)
                mScroller->paint(g);
         */
        if (mScrollValue >= 0) {
            drawScroll(g);
        }
    }
    
    /**
     * On scrolling.
     */
    public void onScroll(int direction) {
        switch (direction) {
            case 0:  // UP
                /*
                if (mScroller != 0) {
                        mViewY -= SCROLLING_STEP;
                        if (mViewY < 0) mViewY = 0;
                        mScroller->setValue(mViewY);
                }
                 */
                if (mScrollValue != -1) {
                    mViewY -= SCROLLING_STEP;
                    if (mViewY < 0) mViewY = 0;
                    mScrollValue = mViewY;
                }
                break;
                
            case 1:  // DOWN
                /*
                if (mScroller != 0) {
                        int viewArea = mHeight - mMarginY * 2;
                 
                        mViewY += SCROLLING_STEP;
                        if (mViewY > (mParaY - viewArea + 1))
                                mViewY = mParaY - viewArea + 1;
                        mScroller->setValue(mViewY);
                }
                 */
                if (mScrollValue != -1) {
                    int viewArea = mHeight - mMarginY * 2;
                    
                    mViewY += SCROLLING_STEP;
                    if (mViewY > (mParaY - viewArea + 1))
                        mViewY = mParaY - viewArea + 1;
                    mScrollValue = mViewY;
                    if (mScrollValue >= mScrollMax)
                        mScrollValue = mScrollMax-1;
                }
                break;
        }
    }
    
    /**
     * On inputting a character.
     * @key the input key
     */
    public void onInput(int key) {
        if (!mIsVisible) return;
        if (mEditableText != null && (key == Key.LEFT  || key == Key.RIGHT)) {
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
                    Label label = (Label)mParagraphs.elementAt(0);
                    label.setTextInLine(mEditableText.toString());
                    //clear(false);
                    //addParagraph(mEditableText, mFont);
                    mCurso = lastCur;
                }
                
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
                    Label label = (Label)mParagraphs.elementAt(0);
                    label.setTextInLine(mEditableText.toString());
                    mCurso = lastCur+1;
                }
                break;
        }
    }
    
    /**
     * On deleting a character from right-to-left.
     */
    public boolean onBackspace() {
        if (!mIsVisible) return false;
        //int len = mEditableText->length();
        if (mCurso > 0) {
            int lastCur = mCurso;            
            mEditableText.deleteCharAt(mCurso - 1);
            Label label = (Label)mParagraphs.elementAt(0);
            label.setTextInLine(mEditableText.toString());
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
    
    
    /**
     * Draw the black background in textbox.
     * @param g Pointer to the graphic library, used to draw everything to the screen
     */
    public void drawArea(Graphics g) {
        //g->setColor(mBackColor);
        //g->fillRect(mX, mY, mWidth, mHeight);
        //g->setColor(Utils::lightenColor(mBackColor, 65));
        //g->drawRect(mX, mY, mWidth - 1, mHeight - 1);
        
        //g->setColor(0x0);
        g.setColor(1908006);
        g.fillRect(mX, mY, mWidth, mHeight);
        g.setColor(0x2A2C2F);
        g.fillRect(mX, mY, mWidth, 1);
        g.setColor(0x696E71);
        g.fillRect(mX + mWidth, mY, 1, mHeight);
        g.setColor(0x959494);
        g.fillRect(mX, mY + mHeight, mWidth, 1);
        g.setColor(0x2A2C2F);
        g.fillRect(mX, mY, 1, mHeight);
        
        if (mHighlight) {
            g.setColor(0x72C71E);
            g.drawRect(mX, mY, mWidth, mHeight);
        }
    }
    
    public void drawScroll(Graphics g) {
        //0x6F6F6F, 0x5AD500
        int backColor = 0x6F6F6F;
        int foreColor = 0x5AD500;
        int range = mScrollMax - mScrollMin + 1;
        int mIndLength = mScrollLength - range;
        if (mIndLength < 8)
            mIndLength = 8;
        // Draw the background of the scrollbar.
        int color = backColor;
        for (int i = 0; i < SC_WIDTH; i++) {
            if (i == 0)
                color = backColor;
            else color = Utils.lightenColor(color, 50);
            g.setColor(color);
            g.drawLine(mScrollX + i, mScrollY + 1, mScrollX + i, mScrollY + 1 + mScrollLength - 1);
        }
        
        int x = mScrollX,
                y = mScrollY + (mScrollValue * (mScrollLength - mIndLength)) / (mScrollMax - mScrollMin + 1) + 1;
        
        if (mScrollValue >= mScrollMax)
            y = mY + mScrollLength - mIndLength;
        
        // Draw the scrollbar's indicator.
        g.setColor(Utils.darkenColor(foreColor, 30));    // The border
        g.drawRect(x, y, SC_WIDTH - 1, mIndLength - 1);
        g.setColor(foreColor);                            // The surface
        g.fillRect(x + 1, y + 1, SC_WIDTH - 2, mIndLength - 2);
        g.setColor(Utils.lightenColor(foreColor, 45));   // The lighter lines
        g.drawLine(x + 1, y + 1, x + 1 + SC_WIDTH - 2, y + 1);
        g.drawLine(x + 1, y + 1, x + 1, y + 1 + mIndLength - 3);
    }
    /**
     * Set highlight this textbox
     */
    public void setHighlight(boolean isHighlight) {
        mHighlight = isHighlight;
    }
    
    public boolean isHighlight() {
        return mHighlight;
    }
    
    public void drawCursor(Graphics g, int flag) {
        if (mParagraphs.size() == 0)
            return;
        Label para = (Label)mParagraphs.elementAt(0);
        mBlinkTime -= 10;
        if (para != null && mBlinkTime <= 0) {
            if (mBlinkTime < -600)
                mBlinkTime = 400;
            UnicodeFont font = para.getFont();
            int strW = font.getSubWidth(mEditableText.toString(), 0, mEditableText.length());
            int rightW = font.getSubWidth(mEditableText.toString(), mCurso, mEditableText.length());
            char sz[] = {'%', 0};
            int x = 0;
            if (flag == Graphics.HCENTER)
                x = mWidth/2 + strW/2 - 3;
            
            x -= rightW;
            
            //font->write(g, sz, mX + x, mY + mHeight/2 - 2);
            x = mX + x - 1;
            g.setColor(115, 125, 131);
            g.drawLine(x, mY + 2, x, mY + mHeight - 3);
        }
    }
    
    int mBlinkTime;
    int mCurso;
}