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
public class RoomViewScreen extends List implements CommandListener, ItemCommandListener {
    
    public Vector mRoomVector;
    public Vector mRoomStringItemVector;    
    public Command mCommandBack;
    public Command mCommandJoin;
    public Command mCommandUpdate;
    public List mList;
    public RoomViewScreen(Vector roomVector) {
        //super("Rooms", new Item[0]);        
        super("Rooms", List.IMPLICIT);        
        mRoomVector = roomVector;
        mRoomStringItemVector = new Vector();
        mCommandJoin = new Command("Join", Command.OK, 0);        
        for (int i = 0; i < mRoomVector.size(); i++)
        {
            Room aRoom = (Room)mRoomVector.elementAt(i);
            String aStringItem = aRoom.mRoomName;
//            aStringItem.addCommand(mCommandJoin);
//            aStringItem.setItemCommandListener(this);
            mRoomStringItemVector.addElement(aStringItem);
            append(aStringItem, null);
        }
        
        mCommandBack = new Command("Back", Command.BACK, 0);        
        mCommandUpdate = new Command("Update", Command.OK, 1);
        
        addCommand(mCommandBack);        
        addCommand(mCommandUpdate);
        addCommand(mCommandJoin);
        
        this.setSelectCommand(mCommandJoin);
        
        setCommandListener(this);
    }
    
    public void commandAction(Command command, Displayable displayable)
    {
        if (command == mCommandBack)
        {
            ChessMIDlet.mMe.setScreen(new MainMenu());            
        }
        else if (command == mCommandUpdate)
        {
            ChessMIDlet.mMe.setScreen(new WaitingScreen());
            HTTPComms aHTTPComms = new HTTPComms();
            aHTTPComms.SendRequest("t=" + HTTPComms.REQUEST_UPDATE_ROOMS_LIST 
                    + "&u=" + ChessMIDlet.mMe.mUsername
                    + "&p=" + ChessMIDlet.mMe.mPassword);
        } 
        else if (command == mCommandJoin)
        {
            Room aSI = (Room)mRoomVector.elementAt(getSelectedIndex());
            ChessMIDlet.mMe.setScreen(new WaitingScreen());
            HTTPComms aHTTPComms = new HTTPComms();
            aHTTPComms.SendRequest("t=" + HTTPComms.REQUEST_JOIN_ROOM 
                    + "&u=" + ChessMIDlet.mMe.mUsername
                    + "&p=" + ChessMIDlet.mMe.mPassword
                    + "&rn=" + aSI.mRoomName
                    + "&hd=" + aSI.mHolderName);
        }
    }
    
    public void commandAction(Command command, Item item)
    {
//        if (command == mCommandJoin)
//        {
//            StringItem aSI = (StringItem)item;
//            ChessMIDlet.mMe.setScreen(new WaitingScreen());
//            HTTPComms aHTTPComms = new HTTPComms();
//            aHTTPComms.SendRequest("t=" + HTTPComms.REQUEST_JOIN_ROOM 
//                    + "&u=" + ChessMIDlet.mMe.mUsername
//                    + "&p=" + ChessMIDlet.mMe.mPassword
//                    + "&rn=" + aSI.getLabel()
//                    + "&hd=" + aSI.getText());
//        }
    }
}
