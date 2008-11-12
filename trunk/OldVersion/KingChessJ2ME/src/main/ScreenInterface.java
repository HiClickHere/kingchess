/*
 * ScreenInterface.java
 *
 * Created on October 14, 2008, 3:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package main;

import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;

/**
 *
 * @author dong
 */
public class ScreenInterface extends Form
{
    public final static int SCREEN_MAIN_MENU = 0;
    public final static int SCREEN_GAME = SCREEN_MAIN_MENU + 1;
    public final static int SCREEN_LOGIN = SCREEN_GAME + 1;
    public final static int SCREEN_REGISTER = SCREEN_LOGIN + 1;
    public final static int SCREEN_OPTION = SCREEN_REGISTER + 1;
    public final static int SCREEN_HELP = SCREEN_OPTION + 1;
    public final static int SCREEN_ABOUT = SCREEN_HELP + 1;
    public final static int SCREEN_ROOMS = SCREEN_ABOUT + 1;
    public final static int SCREEN_ERROR = SCREEN_ROOMS + 1;
    public final static int SCREEN_CREATE_ROOM = SCREEN_ERROR + 1;
    public final static int SCREEN_NOTIFY = SCREEN_CREATE_ROOM + 1;    
    
    public int mScreenType;
    public ScreenInterface mNextScreen;
    public ScreenInterface mPrevScreen;
    
    public ScreenInterface(String title, Item [] itemList)
    {
        super(title, itemList);
    }
}
