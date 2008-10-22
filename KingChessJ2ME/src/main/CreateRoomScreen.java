/*
 * RegisterScreen.java
 *
 * Created on October 14, 2008, 4:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package main;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

/**
 *
 * @author dong
 */
public class CreateRoomScreen extends ScreenInterface implements CommandListener {    
    TextField mRoomName;    
    
    Command mCommandOK;
    Command mCommandBack;
    
    /** Creates a new instance of RegisterScreen */
    public CreateRoomScreen() 
    {
        super("Create Room", new Item[0]);
        mRoomName = new TextField("Room name", null, 120, TextField.ANY);        
        
        append(mRoomName);
        
        mCommandOK = new Command("Done", Command.OK, 0);
        mCommandBack = new Command("Back", Command.BACK, 0);
        
        addCommand(mCommandOK);
        addCommand(mCommandBack);
        
        setCommandListener(this);
    }
    
    public void commandAction(Command command, Displayable displayable)
    {
        if (command == mCommandOK)
        {            
            ChessMIDlet.mMe.setScreen(new WaitingScreen());
            HTTPComms aHTTPComms = new HTTPComms();
            aHTTPComms.SendRequest("t=" + HTTPComms.REQUEST_CREATE_ROOM 
                    + "&u=" + ChessMIDlet.mMe.mUsername
                    + "&p=" + ChessMIDlet.mMe.mPassword
                    + "&rn=" + mRoomName.getString());
        } 
        else if (command == mCommandBack)
        {
            ChessMIDlet.mMe.getDisplay().setCurrent(new MainMenu());
        }
    }    
}
