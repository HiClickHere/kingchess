/*
 * ChessMIDlet.java
 *
 * Created on October 12, 2008, 11:20 PM
 */

package main;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author dong
 */
public class ChessMIDlet extends MIDlet
{        
    public ChessMIDlet() 
    {
        mMe = this;
        initialize();
    }
    
    public static boolean mIsLoggedIn;
    public static ChessMIDlet mMe; 
    
    public String mUsername;
    public String mPassword;

    private void initialize() 
    {        
        setScreen(new MainMenu());
    }
    
    public Display getDisplay() 
    {
        return Display.getDisplay(this);
    }
    
    public void setScreen(Displayable aDisplayable)
    {
        getDisplay().setCurrent(aDisplayable);
    }
    
    public void exitMIDlet() 
    {
        //getDisplay().setCurrent(null);
        destroyApp(true);
        notifyDestroyed();
    }
    
    public void startApp() 
    {
        mIsLoggedIn = false;
    }
    
    public void pauseApp() 
    {        
    }
    
    public void destroyApp(boolean unconditional) 
    {        
    }    
}
