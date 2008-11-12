/*
 * RoomViewScreen.java
 *
 * Created on October 14, 2008, 4:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package main;

import java.util.Vector;
import javax.microedition.lcdui.*;

/**
 *
 * @author dong
 */
public class InRoomScreen extends ScreenInterface implements CommandListener, Runnable {
    
    public Command mCommandBack;    
    public Command mCommandStart;
    public Command mCommandUpdate;
    
    public String mOpponentName;    
    public StringItem mWaitString;
    public boolean mIsHosting;
    
    public static InRoomScreen mMe;
    public boolean mIsStarted;
    
    public Thread mUpdateThread;
    
    public InRoomScreen(boolean isHosting) {                
        super("In Room", new Item[0]);
        
        mMe = this;
        mIsHosting = isHosting;
        
        mWaitString = new StringItem("", "Waiting for other player...");
        mOpponentName = "";        
        
        append(mWaitString);
        
        mCommandBack = new Command("Back", Command.BACK, 0);        
//        if (mIsHosting)
//            mCommandStart = new Command("Start", Command.OK, 0);
//        else
        mCommandStart = new Command("Ready", Command.OK, 0);
        mCommandUpdate = new Command("Update", Command.OK, 1);
        
        addCommand(mCommandBack);
        addCommand(mCommandStart);                        
        
        setCommandListener(this);      
        mIsStarted = false;
    }
    
    public void commandAction(Command command, Displayable displayable)
    {
        if (command == mCommandBack)
        {             
            mIsStarted = true;
            HTTPComms aHTTPComms = new HTTPComms();
            aHTTPComms.SendRequest("t=" + HTTPComms.REQUEST_LEFT_ROOM 
                    + "&u=" + ChessMIDlet.mMe.mUsername
                    + "&p=" + ChessMIDlet.mMe.mPassword);
            ChessMIDlet.mMe.setScreen(new MainMenu());
        }
        else if (command == mCommandStart)
        {
//            if (mOpponentName.length() == 0)
//            {
//                Ticker aTicker = new Ticker("The game will start with 2 players.");
//                setTicker(aTicker);
//            }
//            else {
            removeCommand(mCommandStart);
            mUpdateThread = new Thread(this);
            mUpdateThread.start();                
//            }
        }
        else if (command == mCommandUpdate)
        {
            HTTPComms aHTTPComms = new HTTPComms();
            aHTTPComms.SendRequest("t=" + HTTPComms.REQUEST_UPDATE_ROOM_STATUS 
                    + "&u=" + ChessMIDlet.mMe.mUsername
                    + "&p=" + ChessMIDlet.mMe.mPassword);
        }
    }  
    
    public void run()
    {
        try {
            while (!mIsStarted)
            {
                HTTPComms aHTTPComms = new HTTPComms();
                aHTTPComms.SendRequest("t=" + HTTPComms.REQUEST_UPDATE_ROOM_STATUS + "&u=" + ChessMIDlet.mMe.mUsername);            
                Thread.sleep(5000);
            }            
        } catch (Exception e)
        {
           e.printStackTrace(); 
        }
    }
}
