package main;

import main.*;
import core.ChessDataOutputStream;
import core.Screen;
import core.String16;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import ui.FastDialog;
import util.*;
/*
 * ScreenMainMenu.java
 *
 * Created on October 29, 2008, 3:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author dong
 */
public class ScreenSoundAsk extends Screen {    
    private Menu mMenu;
    private TextBox mTextBox;
    private FastDialog mDialog;
    private boolean mIsDisplayDialog = false;
    public final static int BUTTON_SOUND = 0;

    /** Creates a new instance of ScreenMainMenu */
    public ScreenSoundAsk(Context aContext) {
        super(aContext);
        mDialog = new FastDialog(getWidth() - 40, getHeight() - 80);
    }

    public void onActivate() {
        mIsDisplayDialog = false;
        mMenu = new Menu();
        mMenu.setColors(0x5f7a7a, 0x708585);
        
        mDialog.setText("Bạn có muốn bật âm thanh không?");
        setSoftKey(SOFTKEY_CANCEL, -1, SOFTKEY_OK);
        mIsDisplayDialog = true;
    }

    public void onTick(long aMilliseconds) {
        //repaint();
        //serviceRepaints();
    }

    public void keyPressed(int keyCode) {
        if (mIsDisplayDialog) {
            switch (keyCode) {
                case Key.SOFT_LEFT:
                    if (mLeftSoftkey == SOFTKEY_CANCEL) {
                        //mIsDisplayDialog = false;
                        //setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
                        //mContext.mSoundEnable = false;                        
                        mContext.mSoundManager.setActive(false);
                        ScreenSplash splash = new ScreenSplash(mContext);
                        mContext.setScreen(splash);
                    }
                    break;
                case Key.SOFT_RIGHT:
                    if (mRightSoftkey == SOFTKEY_OK) {
//                        mContext.stop();
//                        mContext.mMIDlet.notifyDestroyed();
                        mContext.mSoundManager.setActive(true);
                        ScreenSplash splash = new ScreenSplash(mContext);
                        mContext.setScreen(splash);
                    }
                    break;
            }
            return;
        }

        switch (keyCode) {
            case Key.UP:
            case Key.NUM_2:
                if (mMenu != null) {
                    mMenu.onDirectionKeys(0);
                }
                if (mTextBox != null) {
                    mTextBox.onScroll(false);
                    if (mTextBox.mIsEditable) {
                        mTextBox.onInput(keyCode);
                    }
                }
                break;
            case Key.DOWN:
            case Key.NUM_8:
                if (mMenu != null) {
                    mMenu.onDirectionKeys(1);
                }
                if (mTextBox != null) {
                    mTextBox.onScroll(true);
                    if (mTextBox.mIsEditable) {
                        mTextBox.onInput(keyCode);
                    }
                }
                break;
            case Key.LEFT:
            case Key.NUM_4:
                if (mMenu != null) {
                    mMenu.onDirectionKeys(2);
                }
                if (mTextBox != null && mTextBox.mIsEditable) {
                    mTextBox.onInput(keyCode);
                }
                break;
            case Key.RIGHT:
            case Key.NUM_6:
                if (mMenu != null) {
                    mMenu.onDirectionKeys(3);
                }
                if (mTextBox != null && mTextBox.mIsEditable) {
                    mTextBox.onInput(keyCode);
                }
                break;
            case Key.SELECT:
            case Key.SOFT_RIGHT:
            case Key.NUM_5:
                if (keyCode == Key.SOFT_RIGHT && mRightSoftkey != SOFTKEY_OK) {
                    return;
                }                
                break;
            case Key.SOFT_LEFT:
                ScreenMainMenu menu = new ScreenMainMenu(mContext);
                mContext.setScreen(menu);
                break;
            default:
                if (mTextBox != null && mTextBox.mIsEditable) {
                    mTextBox.onInput(keyCode);
                }
        }
    }

    public void paint(Graphics g) {
        g.setClip(0, 0, getWidth(), getHeight());
        g.setColor(0x708585);        
        g.fillRect(0, 0, getWidth(), getHeight());               
        g.drawImage(mContext.mMenuBackground, getWidth() >> 1, getHeight() >> 1, Graphics.HCENTER | Graphics.VCENTER);
        
        if (mIsDisplayDialog) {
            mDialog.paint(g);
        }
        drawSoftkey(g);
    }
}
