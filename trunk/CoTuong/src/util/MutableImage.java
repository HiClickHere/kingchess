/*
 * MutableImage.java
 *
 * Created on October 30, 2008, 11:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

/**
 *
 * @author dong
 */
public class MutableImage {    
    public int mWidth;
    public int mHeight;
    public boolean mIsAlphaChannel;    
    
    
    /** Creates a new instance of MutableImage */
    private MutableImage() 
    {
        
    }

    public static MutableImage createImage(String imgPath) throws Exception
    {
        MutableImage aMutableImage = new MutableImage();
        return aMutableImage;
    }             
}
