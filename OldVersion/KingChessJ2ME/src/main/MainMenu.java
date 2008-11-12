/*
 * MainMenu.java
 *
 * Created on October 14, 2008, 3:29 AM
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
public class MainMenu extends List implements CommandListener, ItemCommandListener {        
    
    private String mCreateRoomStringItem;
    private String mJoinRoomStringItem;
    private String mLoginStringItem;
    private String mLogoutStringItem;
    private String mRegisterStringItem;
    private String mOptionStringItem;
    private String mHelpStringItem;    
    private String mAboutStringItem;    
    
    private Command mCommandOK;
    private Command mCommandExit;
    
    public List mList;
    
    /** Creates a new instance of MainMenu */
    public MainMenu() 
    {
        //super("Main Menu", new Item[0]);
        super("Main Menu", List.IMPLICIT);        
        
        //mScreenType = ScreenInterface.SCREEN_MAIN_MENU;
        
        mCommandOK = new Command("OK", Command.OK, 1);
        mCommandExit = new Command("Exit", Command.EXIT, 2);
        
        mCreateRoomStringItem = "Create Room";
        //mCreateRoomStringItem.addCommand(mCommandOK);
        //mCreateRoomStringItem.setItemCommandListener(this);        
        mJoinRoomStringItem = "Join Room";
        //mJoinRoomStringItem.addCommand(mCommandOK);
        //mJoinRoomStringItem.setItemCommandListener(this);
        mLoginStringItem = "Login";
        //mLoginStringItem.addCommand(mCommandOK);
        //mLoginStringItem.setItemCommandListener(this);
        mLogoutStringItem = "Logout";
        //mLogoutStringItem.addCommand(mCommandOK);
        //mLogoutStringItem.setItemCommandListener(this);
        mRegisterStringItem = "Register";
        //mRegisterStringItem.addCommand(mCommandOK);
        //mRegisterStringItem.setItemCommandListener(this);
        mOptionStringItem = "Option";
        //mOptionStringItem.addCommand(mCommandOK);
        //mOptionStringItem.setItemCommandListener(this);
        mAboutStringItem = "About";
        //mAboutStringItem.addCommand(mCommandOK);
        //mAboutStringItem.setItemCommandListener(this);        
        mHelpStringItem = "Help";
        //mHelpStringItem.addCommand(mCommandOK);
        //mHelpStringItem.setItemCommandListener(this);
        
        if (ChessMIDlet.mMe.mIsLoggedIn)
        {            
            append(mCreateRoomStringItem, null);
            append(mJoinRoomStringItem, null);            
            append(mLogoutStringItem, null);
            append(mOptionStringItem, null);
            append(mAboutStringItem, null);
            append(mHelpStringItem, null);
        }
        else 
        {            
            append(mLoginStringItem, null);
            append(mRegisterStringItem, null);
            append(mOptionStringItem, null);
            append(mAboutStringItem, null);
            append(mHelpStringItem, null);
        }                                
        
        addCommand(mCommandOK);
        addCommand(mCommandExit);        
        setSelectCommand(mCommandOK);
        
        setCommandListener(this);        
    }    
    
    public void commandAction(Command command, Displayable displayable)
    {        
        if (command == mCommandExit)
        {
            ChessMIDlet.mMe.exitMIDlet();
        } 
        else if (command == mCommandOK)
        {
           String item = this.getString(getSelectedIndex());
           if (item == mLoginStringItem) 
           {
               ChessMIDlet.mMe.setScreen(new LoginScreen());
           }
           else if (item == mRegisterStringItem)
           {               
               ChessMIDlet.mMe.setScreen(new RegisterScreen());
           }
           else if (item == mOptionStringItem)
           {
               
           }
           else if (item == mAboutStringItem)
           {
               
           }
           else if (item == mHelpStringItem)
           {
               
           }
           else if (item == mJoinRoomStringItem)
           {
               ChessMIDlet.mMe.setScreen(new WaitingScreen());
               HTTPComms aHTTPComms = new HTTPComms();
               aHTTPComms.SendRequest("t=" + HTTPComms.REQUEST_UPDATE_ROOMS_LIST);
               //ChessMIDlet.mMe.setScreen(new RoomViewScreen());
           }
           else if (item == mCreateRoomStringItem)
           {
               ChessMIDlet.mMe.setScreen(new CreateRoomScreen());
           }
           else if (item == mLogoutStringItem)
           {
               ChessMIDlet.mMe.setScreen(new WaitingScreen());
               HTTPComms aHTTPComms = new HTTPComms();
               aHTTPComms.SendRequest("t=" + HTTPComms.REQUEST_LOGOUT
                       + "&u=" + ChessMIDlet.mMe.mUsername
                       + "&p=" + ChessMIDlet.mMe.mPassword);
           }
        }
    }           
    
    public void commandAction(Command command, Item item) 
    {            
//        if (command == mCommandOK)
//        {
//           item = getSelectedIndex(getSelectedIndex());
//           if (item == mLoginStringItem) 
//           {
//               ChessMIDlet.mMe.setScreen(new LoginScreen());
//           }
//           else if (item == mRegisterStringItem)
//           {               
//               ChessMIDlet.mMe.setScreen(new RegisterScreen());
//           }
//           else if (item == mOptionStringItem)
//           {
//               
//           }
//           else if (item == mAboutStringItem)
//           {
//               
//           }
//           else if (item == mHelpStringItem)
//           {
//               
//           }
//           else if (item == mJoinRoomStringItem)
//           {
//               ChessMIDlet.mMe.setScreen(new WaitingScreen());
//               HTTPComms aHTTPComms = new HTTPComms();
//               aHTTPComms.SendRequest("t=" + HTTPComms.REQUEST_UPDATE_ROOMS_LIST);
//               //ChessMIDlet.mMe.setScreen(new RoomViewScreen());
//           }
//           else if (item == mCreateRoomStringItem)
//           {
//               ChessMIDlet.mMe.setScreen(new CreateRoomScreen());
//           }
//           else if (item == mLogoutStringItem)
//           {
//               ChessMIDlet.mMe.setScreen(new WaitingScreen());
//               HTTPComms aHTTPComms = new HTTPComms();
//               aHTTPComms.SendRequest("t=" + HTTPComms.REQUEST_LOGOUT
//                       + "&u=" + ChessMIDlet.mMe.mUsername
//                       + "&p=" + ChessMIDlet.mMe.mPassword);
//           }
//        }
    }
}
