/*
 * WaitingScreen.java
 *
 * Created on October 14, 2008, 4:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package main;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

/**
 *
 * @author dong
 */
public class WaitingScreen extends ScreenInterface {
    
    StringItem mWaitString;
    /** Creates a new instance of WaitingScreen */
    public WaitingScreen() {
        super("Please wait...", new Item[0]);
        mWaitString = new StringItem("Network", "\nServer contacting...");
        append(mWaitString);
    }
    
}
