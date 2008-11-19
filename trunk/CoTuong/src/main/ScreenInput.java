/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextField;

/**
 *
 * @author dong
 */
public class ScreenInput extends Form implements CommandListener {

    public TextField mTextBox;
    public Command mCommandOK;    
    private Context mContext;
    
    public ScreenInput(Context aContext)
    {
        super("", new Item[0]);
        mContext = aContext;
        mTextBox = new TextField("", null, 10000, TextField.ANY);  
        append(mTextBox);
        
        mCommandOK = new Command("Done", Command.OK, 0);        
        
        addCommand(mCommandOK);                        
        
        setCommandListener(this);
    }
    
    public void commandAction(Command command, Displayable displayable)
    {
        if (command == mCommandOK)
        {                        
            mContext.setDisplayMainCanvas();
            mContext.mCanvas.setFullScreenMode(true);
        } 
    }           
    
}
