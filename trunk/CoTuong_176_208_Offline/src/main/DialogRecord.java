/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

/**
 *
 * @author dong
 */
public class DialogRecord 
{
    public String mMessage;
    public int mLeftSoftkey;
    public int mCenterSoftkey;
    public int mRightSoftkey;    
    public int mState;
    public DialogRecord(String aString, int aLeftSoft, int aCenterSoft, int aRightSoft, int aState)
    {
        mMessage = aString;
        mLeftSoftkey = aLeftSoft;
        mCenterSoftkey = aCenterSoft;
        mRightSoftkey = aRightSoft;
        mState = aState;
    }
}
