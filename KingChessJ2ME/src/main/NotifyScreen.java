/*
 * NotifyScreen.java
 *
 * Created on October 14, 2008, 4:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package main;

import javax.microedition.lcdui.*;

/**
 *
 * @author dong
 */
public class NotifyScreen extends ScreenInterface implements CommandListener {
    
    public StringItem mStringItem;
    public Displayable mNextScreen;
    public Command mCommandOK;    
    
    /** Creates a new instance of NotifyScreen */
    public NotifyScreen(String title, String content, Displayable nextScreen) 
    {
        super("Notify", new Item[0]);
        mScreenType = ScreenInterface.SCREEN_NOTIFY;
        mNextScreen = nextScreen;
        mStringItem = new StringItem(title, "\n" + content);
        append(mStringItem);
        mCommandOK = new Command("Dismiss", Command.OK, 0);
        addCommand(mCommandOK);
        setCommandListener(this);
    }        

    public void commandAction(Command command, Displayable displayable) 
    {
        if (command == mCommandOK)
        {
            ChessMIDlet.mMe.setScreen(mNextScreen);
        }
    }
}
