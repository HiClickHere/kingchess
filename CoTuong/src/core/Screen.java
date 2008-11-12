/*
 * Screen.java
 *
 * Created on October 30, 2008, 5:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;

//#ifdef NOKIAUI
//# import com.nokia.mid.ui.FullCanvas;
//#endif
import main.*;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import ui.FastDialog;

/**
 *
 * @author dong
 */
//#ifdef NOKIAUI
//# public class Screen extends FullCanvas 
//#else
public class Screen //extends Canvas implements Runnable      
//#endif
{ 
    public final static int SOFTKEY_OK = 1;    
    public final static int SOFTKEY_BACK = 3;
    public final static int SOFTKEY_MENU = 4;
    public final static int SOFTKEY_CANCEL = 5;
    
    public int mLeftSoftkey;
    public int mRightSoftkey;
    public int mCenterSoftkey;    
    
    public Context mContext;
    
    protected FastDialog mDialog;
    protected boolean mIsDisplayDialog = false;
    
    /** Creates a new instance of Screen */
    public Screen(Context aContext) 
    {
        super();
        mContext = aContext;
        
        //setFullScreenMode(true);
    }
    
    public int getWidth()
    {
        return mContext.mCanvas.getWidth();
    }
    
    public int getHeight()
    {
        return mContext.mCanvas.getHeight();
    }
    
    public void onActivate()
    {
    }
    
    public void onDeactivate()
    {
    }
    
    public void paint(Graphics g)
    {        
    }
    
    public void keyPressed(int aKeyCode)
    {
        
    }
    
    public boolean onEvent(Event event)
    {
        return false;
    }
    
    public void fireEvent(Event event)
    {
        mContext.fireEvent(event);
    }
    
    public void onTick(long aMilliseconds)
    {
        
    }
    
    public void setSoftKey(int leftSoftkey, int centerSoftkey, int rightSoftkey)
    { 
        mLeftSoftkey = leftSoftkey;
        mCenterSoftkey = centerSoftkey;
        mRightSoftkey = rightSoftkey;
    }
    
    public void drawSoftkey(Graphics g)
    {
        if (mLeftSoftkey != -1)
        {
            Image softImg = mContext.mSoftkeyBack;
            if (mLeftSoftkey == SOFTKEY_MENU)
                softImg = mContext.mSoftkeyMenu;
            else if (mLeftSoftkey == SOFTKEY_OK)
                softImg = mContext.mSoftkeyOK;
            else if (mLeftSoftkey == SOFTKEY_CANCEL)
                softImg = mContext.mSoftkeyCancel;
            
            g.drawImage(softImg, 0, getHeight() - softImg.getHeight(), 0);
        }
        
        if (mRightSoftkey != -1)
        {
            Image softImg = mContext.mSoftkeyBack;
            if (mRightSoftkey == SOFTKEY_MENU)
                softImg = mContext.mSoftkeyMenu;
            else if (mRightSoftkey == SOFTKEY_OK)
                softImg = mContext.mSoftkeyOK;
            else if (mRightSoftkey == SOFTKEY_CANCEL)
                softImg = mContext.mSoftkeyCancel;
            
            g.drawImage(softImg, getWidth() - softImg.getWidth(), getHeight() - softImg.getHeight(), 0);
        }
        
        if (mCenterSoftkey != -1)
        {
            Image softImg = mContext.mSoftkeyBack;
            if (mCenterSoftkey == SOFTKEY_MENU)
                softImg = mContext.mSoftkeyMenu;
            else if (mCenterSoftkey == SOFTKEY_OK)
                softImg = mContext.mSoftkeyOK;
            else if (mCenterSoftkey == SOFTKEY_CANCEL)
                softImg = mContext.mSoftkeyCancel;
            
            g.drawImage(softImg, (getWidth() - softImg.getWidth()) >> 1, getHeight() - softImg.getHeight(), 0);
        }
    }
    
//    public void run()
//    {
//        System.out.println("call run!!!");
//        while (mIsActivated)
//        {
//            System.out.println("call paint!!!");
//            try {
//                repaint();
//                serviceRepaints();   
//                Thread.sleep(100L);
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
}
