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
public class LoginScreen extends ScreenInterface implements CommandListener {
    
    TextField mUserNameField;
    TextField mPasswordField;
    
    Command mCommandOK;
    Command mCommandBack;
    
    /** Creates a new instance of RegisterScreen */
    public LoginScreen() 
    {
        super("Login", new Item[0]);
        mUserNameField = new TextField("Username", null, 120, TextField.ANY);        
        mPasswordField = new TextField("Password", null, 120, TextField.NUMERIC);
        
        append(mUserNameField);
        append(mPasswordField);
        
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
            ChessMIDlet.mMe.mUsername = mUserNameField.getString();
            ChessMIDlet.mMe.mPassword = mPasswordField.getString();
            
            System.out.println("Login");
            System.out.println(ChessMIDlet.mMe.mUsername);
            System.out.println(ChessMIDlet.mMe.mPassword);
            
            ChessMIDlet.mMe.setScreen(new WaitingScreen());
            HTTPComms aHTTPComms = new HTTPComms();
            aHTTPComms.SendRequest("t=" + HTTPComms.REQUEST_LOGIN 
                    + "&u=" + mUserNameField.getString()
                    + "&p=" + mPasswordField.getString());
        } 
        else if (command == mCommandBack)
        {
            ChessMIDlet.mMe.getDisplay().setCurrent(new MainMenu());
        }
    }    
}
