/*
 * ScreenLogin.java
 *
 * Created on November 1, 2008, 3:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package main;

import core.ChessDataInputStream;
import core.ChessDataOutputStream;
import core.Event;
import core.Network;
import core.Protocol;
import core.Screen;
import core.String16;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.microedition.lcdui.Graphics;
import ui.FastDialog;
import util.Menu;
import util.*;

/**
 *
 * @author dong
 */
public class ScreenLogin extends Screen {
    
    private Menu mMenu;
    private TextBox mTextBoxes[];
    private TextBox mFocusTextBox;
    private FastDialog mDialog = null;
    private boolean mIsDisplayDialog = false;
    
    /** Creates a new instance of ScreenLogin */
    public ScreenLogin(Context aContext) {
        super(aContext);
        mTextBoxes = new TextBox[2];
        mDialog = new FastDialog(getWidth() - 40, getHeight() - 80);
    }        

    public void onActivate() {
        mMenu = new Menu();
        mMenu.setColors(0x5f7a7a, 0x708585);
        mMenu.addItem(0, StringConst.STR_USERNAME, false, 
                        false, 
                        20, 
                        90, 
                        getWidth() >> 1,
                        50,
                        Graphics.HCENTER | Graphics.VCENTER); 
        mMenu.addItem(1, StringConst.STR_PASSWORD, false, 
                        false, 
                        20, 
                        90, 
                        getWidth() >> 1,
                        100,
                        Graphics.HCENTER | Graphics.VCENTER);         
        setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
        mTextBoxes[0] = new TextBox(0x5f7a7a, Utils.lightenColor(0x5f7a7a, 30), 
                getWidth() - 60, 18, Graphics.HCENTER | Graphics.VCENTER, 
                mContext.mTahomaFontBlue);
        //mTextBoxes[0].setEditable(true);
        mTextBoxes[0].mMarginY = 1;
        mTextBoxes[0].mMarginX = 1;
        mTextBoxes[0].mLineSpace = 0;
        mTextBoxes[0].setEditable(true, 12);
        if (mContext.mUsername == null || mContext.mUsername.length() == 0)
            mTextBoxes[0].setEditableText("username");
        else
            mTextBoxes[0].setEditableText(mContext.mUsername);
        mTextBoxes[1] = new TextBox(0x5f7a7a, Utils.lightenColor(0x5f7a7a, 30), 
                getWidth() - 60, 18, Graphics.HCENTER | Graphics.VCENTER, 
                mContext.mTahomaFontBlue);
        mTextBoxes[1].mMarginY = 1;
        mTextBoxes[1].mMarginX = 1;
        mTextBoxes[1].mLineSpace = 0;
        mTextBoxes[1].setEditable(true, 6);
        if (mContext.mPassword == null || mContext.mPassword.length() == 0)
            mTextBoxes[1].setEditableText("password");
        else
            mTextBoxes[1].setEditableText(mContext.mPassword);
        mFocusTextBox = mTextBoxes[0];
        mFocusTextBox.setColor(0x005500, Utils.lightenColor(0x005500, 30));
        mTextBoxes[1].setColor(0x5f7a7a, Utils.lightenColor(0x5f7a7a, 30));
    }  
    
    public boolean onEvent(Event event)
    {
        switch (event.mType)
        {
            case Network.EVENT_NETWORK_FAILURE:
                mIsDisplayDialog = false;
                mDialog.setText(StringConst.STR_CANNOT_CONNECT_TO_SERVER);
                mIsDisplayDialog = true;
                setSoftKey(-1, -1, SOFTKEY_OK);
                return true;
            case Network.EVENT_END_COMMUNICATION:
                ByteArrayInputStream aByteArray = new ByteArrayInputStream(event.mData);
                ChessDataInputStream in = new ChessDataInputStream(aByteArray);
                try {
                    short returnType = in.readShort();
                    if (returnType == Protocol.RESPONSE_LOGIN_SUCCESSFULLY)
                    {                        
                        mContext.mIsLoggedIn = true;
                        mContext.mLastReceivedGoodConnect = System.currentTimeMillis();
                        mContext.mUserID = in.readInt();
                        mIsDisplayDialog = false;
                        mDialog.setText("Đăng nhập thành công.");
                        mIsDisplayDialog = true;
                        setSoftKey(-1, -1, SOFTKEY_OK);
                    }
                    else if (returnType == Protocol.RESPONSE_LOGIN_FAILURE)
                    {
                        mContext.mIsLoggedIn = false;
                        mIsDisplayDialog = false;
                        mDialog.setText(in.readString16().toJavaString());
                        mIsDisplayDialog = true;
                        setSoftKey(-1, -1, SOFTKEY_OK);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                return true;
            case Network.EVENT_RECEIVING:
            case Network.EVENT_SENDING:
                return true;
            case Network.EVENT_SETUP_CONNECTION:                
                return true;
        }
        return false;
    }
    
    public void keyPressed(int keyCode)
    {        
        ScreenMainMenu aMainMenu;
        //System.out.println("KeyCode: " + keyCode);        
        switch (keyCode)
        {
            case Key.UP:
                if (!mIsDisplayDialog)
                    mMenu.onDirectionKeys(0);                
                else
                    mDialog.onKeyPressed(keyCode);
                break;
            case Key.DOWN:
                if (!mIsDisplayDialog)
                    mMenu.onDirectionKeys(1);
                else
                    mDialog.onKeyPressed(keyCode);
                break;
            case Key.LEFT:
                if (mFocusTextBox != null)
                    mFocusTextBox.onInput(keyCode);
                //mMenu.onDirectionKeys(2);                
                break;
            case Key.RIGHT:              
                //mMenu.onDirectionKeys(3);
                if (mFocusTextBox != null)
                    mFocusTextBox.onInput(keyCode);
                break;
            case Key.SELECT:                
            case Key.SOFT_RIGHT: 
                //mContext.mIsLoggedIn = true;
                //aMainMenu = new ScreenMainMenu(mContext);
                //aMainMenu.setMenu(ScreenMainMenu.MENU_ONLINE_PLAY);
                //mContext.setScreen(aMainMenu);
                if (!mIsDisplayDialog)
                {
                    try {
                        ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                        ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);  
                        mContext.mUsername = mTextBoxes[0].getEditableText();
                        mContext.mPassword = mTextBoxes[1].getEditableText();
                        aOutput.writeString16(new String16(mContext.mUsername));
                        aOutput.writeString16(new String16(mContext.mPassword));
                        mContext.mNetwork.sendMessage(Protocol.REQUEST_LOGIN, aByteArray.toByteArray());                    
                        mDialog.setText(StringConst.STR_CONNECTING_SERVER);
                        setSoftKey(SOFTKEY_CANCEL, -1, -1);
                        mIsDisplayDialog = true;
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                } 
                else {
                    if (mRightSoftkey == SOFTKEY_OK)
                    {
                        if (mContext.mIsLoggedIn)
                        {
                            mIsDisplayDialog = false;
                            ScreenOnlinePlay aScreenOnline = new ScreenOnlinePlay(mContext);
                            mContext.setScreen(aScreenOnline);                            
                        }
                        else
                        {
                            setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
                            mIsDisplayDialog = false;
                        }
                    }
                }
                break;
            case Key.SOFT_LEFT:
                if (!mIsDisplayDialog)
                {
                    ScreenOnlinePlay screen = new ScreenOnlinePlay(mContext);
                    mContext.setScreen(screen);
                }
                else 
                {
                    if (mLeftSoftkey == SOFTKEY_CANCEL) {
                        mIsDisplayDialog = false;
                        mContext.mNetwork.stopConnection();
                    }                    
                }
                break;          
            case Key.BACK:
                if (mFocusTextBox != null)
                    mFocusTextBox.onBackspace();
                break;
            case Key.NUM_0:
            case Key.NUM_1:
            case Key.NUM_2:
            case Key.NUM_3:
            case Key.NUM_4:
            case Key.NUM_5:
            case Key.NUM_6:
            case Key.NUM_7:
            case Key.NUM_8:
            case Key.NUM_9:
                if (mFocusTextBox != null)
                    mFocusTextBox.onInput(keyCode);
                break;
        }
        
        switch (mMenu.selectedItem())
        {
            case 0:
                mFocusTextBox = mTextBoxes[0];    
                mFocusTextBox.setColor(0x005500, Utils.lightenColor(0x005500, 30));
                mTextBoxes[1].setColor(0x5f7a7a, Utils.lightenColor(0x5f7a7a, 30));
                break;
            case 1:
                mFocusTextBox = mTextBoxes[1];
                mFocusTextBox.setColor(0x005500, Utils.lightenColor(0x005500, 30));
                mTextBoxes[0].setColor(0x5f7a7a, Utils.lightenColor(0x5f7a7a, 30));
                break;
        }
    }      

    public void onTick(long aMilliseconds) {
        //repaint();
        //serviceRepaints();
    }
    
    public void paint(Graphics g)
    {
        g.setClip(0, 0, getWidth(), getHeight());
        g.setColor(0x708585);
        g.fillRect(0, 0, getWidth(), getHeight());  
        String menu_name = StringConst.STR_TITLE_LOGIN;
        int w = getWidth() - 4;//mContext.mTahomaOutlineWhite.getWidth(menu_name) + 6;
        int h = mContext.mTahomaOutlineWhite.getHeight() + 4;
        int x = (getWidth() - w) >> 1;
        int y = 2;
        int banner_color = 0xfe9090;        
        mContext.mTahomaOutlineWhite.write(g, menu_name, getWidth() >> 1, y + (h >> 1), Graphics.HCENTER | Graphics.VCENTER);
        
        mMenu.paint(g, 
                mContext.mTahomaFontGreen, 
                mContext.mTahomaOutlineGreen, 
                mContext.mTahomaOutlineRed, 
                getWidth() >> 1, 
                (getHeight() - 100) >> 1, 
                Graphics.HCENTER | Graphics.VCENTER);
       
        if (!mIsDisplayDialog)
        {
            if (mFocusTextBox == mTextBoxes[0])
            {
                mTextBoxes[0].paint(g, getWidth() >> 1, 70, Graphics.HCENTER | Graphics.VCENTER, true);
                mTextBoxes[1].paint(g, getWidth() >> 1, 120, Graphics.HCENTER | Graphics.VCENTER, false);
            }
            else
            {
                mTextBoxes[0].paint(g, getWidth() >> 1, 70, Graphics.HCENTER | Graphics.VCENTER, false);
                mTextBoxes[1].paint(g, getWidth() >> 1, 120, Graphics.HCENTER | Graphics.VCENTER, true);
            }
        } 
        else 
        {
            mDialog.paint(g);
        }
        
        drawSoftkey(g);
    }
            
}
