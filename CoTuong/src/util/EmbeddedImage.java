/*
 * EmbeddedImage.java
 *
 * Created on October 30, 2008, 5:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

import javax.microedition.lcdui.Image;

/**
 *
 * @author dong
 */
public class EmbeddedImage {
    

    public int mX;

    public int mParagraph;

    public int mLine;

    public int mSpacing;

    // Reference to the image to be embedded. Don't free me from inside!
    public Image mImage;

    public EmbeddedImage(Image image)
    {
        setLocation(0, 0, 0, 0);
	mImage = image;
    }

    void setLocation(int x, int paragraph, int line, int spacing)
    {
        mX = x;
	mParagraph = paragraph;
	mLine = line;
	mSpacing = spacing;
    }
}
