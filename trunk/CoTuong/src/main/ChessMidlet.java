package main;
/*
 * ChessMidlet.java
 *
 * Created on October 29, 2008, 3:31 PM
 */

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author  dong
 * @version
 */
public class ChessMidlet extends MIDlet {        
    public Context mContext;
    
    public void startApp() {
        System.out.println("startApp");
        if (mContext == null)
        {
            mContext = new Context(this);
            mContext.start();
        }
        else
            mContext.start();
    }
    
    public void pauseApp() {
        System.out.println("pauseApp");
        if (mContext != null)
            mContext.stop();
    }
    
    public void destroyApp(boolean unconditional) {
        System.out.println("destroyApp");
        if (mContext != null)
        {
            mContext.stop();
            mContext = null;
            System.gc();
        }        
    }        
}
