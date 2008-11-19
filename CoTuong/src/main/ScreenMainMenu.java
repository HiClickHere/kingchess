package main;

import core.ChessDataOutputStream;
import core.FriendRecord;
import core.Protocol;
import core.Screen;
import core.String16;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import ui.FastDialog;
import util.ImageFont;
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
public class ScreenMainMenu extends Screen {

    private ImageFont mImageFont;
    public final static int MENU_MAIN = 0;
    public final static int MENU_OFFLINE_PLAY = 1;    
    public final static int MENU_OPTION = 3;
    public final static int MENU_HELP = 4;
    public final static int MENU_ABOUT = 5;
    private int mCurrentMenu;
    private Menu mMenu;
    private TextBox mTextBox;
    private FastDialog mDialog;
    private boolean mIsDisplayDialog = false;

    /** Creates a new instance of ScreenMainMenu */
    public ScreenMainMenu(Context aContext) {
        super(aContext);
        mDialog = new FastDialog(getWidth() - 40, getHeight() - 80);
    }

    public void onActivate() {
        mIsDisplayDialog = false;
    }

    public void setMenu(int menuType) {
        mMenu = null;
        mTextBox = null;
        mCurrentMenu = menuType;
        switch (menuType) {
            case MENU_MAIN:
                mMenu = new Menu();
                mMenu.setColors(0x5f7a7a, 0x708585);
                mMenu.addItem(0,
                        "Chơi mạng",
                        false,
                        false,
                        20,
                        90,
                        getWidth() >> 1,
                        -1,
                        Graphics.HCENTER | Graphics.VCENTER);
                mMenu.addItem(1, "Chơi đơn",
                        false,
                        false,
                        20,
                        90,
                        getWidth() >> 1,
                        -1,
                        Graphics.HCENTER | Graphics.VCENTER);
                mMenu.addItem(2, StringConst.STR_OPTION,
                        false,
                        false,
                        20,
                        90,
                        getWidth() >> 1,
                        -1,
                        Graphics.HCENTER | Graphics.VCENTER);
                mMenu.addItem(3, StringConst.STR_HELP,
                        false,
                        false,
                        20,
                        90,
                        getWidth() >> 1,
                        -1,
                        Graphics.HCENTER | Graphics.VCENTER);
                mMenu.addItem(4, StringConst.STR_ABOUT, false,
                        false,
                        20,
                        90,
                        getWidth() >> 1,
                        -1,
                        Graphics.HCENTER | Graphics.VCENTER);
                mMenu.addItem(5, StringConst.STR_EXIT, false,
                        false,
                        20,
                        90,
                        getWidth() >> 1,
                        -1,
                        Graphics.HCENTER | Graphics.VCENTER);
                mMenu.setSelection(0);
                setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
                break;
            case MENU_OFFLINE_PLAY:
                mMenu = new Menu();
                mMenu.setColors(0x5f7a7a, 0x708585);
                setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
                break;
            case MENU_OPTION:
                mMenu = new Menu();
                mMenu.setColors(0x5f7a7a, 0x708585);
                mMenu.addItem(0, StringConst.STR_SOUND_ON, false,
                        false,
                        20,
                        90,
                        getWidth() >> 1,
                        -1,
                        Graphics.HCENTER | Graphics.VCENTER);
                setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
                break;
            case MENU_HELP:
                mMenu = null;
                mTextBox = new TextBox(0x5f7a7a,
                        Utils.lightenColor(0x5f7a7a, 20),
                        getWidth() - 10,
                        getHeight() - 40,
                        Graphics.HCENTER | Graphics.VCENTER,
                        mContext.mTahomaFontCyan);
                mTextBox.addParagraph("On the margin of a lake in the " +
                        "margin of the page a margin of 600d " +
                        "to escape death by a narrow margin. ", mContext.mTahomaFontBlue);
                mTextBox.addParagraph("His health is a " +
                        "a Shakespeare " +
                        "a Vietnamese grammar. " +
                        "twice a week. ", mContext.mTahomaFontBlue);
                mTextBox.addParagraph("On the margin of a lake in the " +
                        "margin of the page a margin of 600d " +
                        "to escape death by a narrow margin. ", mContext.mTahomaFontWhite);
                mTextBox.addParagraph("His health is a " +
                        "a Shakespeare " +
                        "a Vietnamese grammar. " +
                        "twice a week. ", mContext.mTahomaFontBlue);
                mTextBox.addParagraph("On the margin of a lake in the " +
                        "margin of the page a margin of 600d " +
                        "to escape death by a narrow margin. ", mContext.mTahomaFontRed);
                mTextBox.addParagraph("His health is a " +
                        "a Shakespeare " +
                        "a Vietnamese grammar. " +
                        "twice a week. ", mContext.mTahomaFontBlue);
                mTextBox.addParagraph("On the margin of a lake in the " +
                        "margin of the page a margin of 600d " +
                        "to escape death by a narrow margin. ", mContext.mTahomaOutlineBlue);
                mTextBox.addParagraph("His health is a " +
                        "a Shakespeare " +
                        "a Vietnamese grammar. " +
                        "twice a week. ", mContext.mTahomaOutlineRed);
                mTextBox.setScrollBar(true, 0x5AD500, 0x6F6F6F);
                break;
            case MENU_ABOUT:
                mMenu = null;
                mTextBox = new TextBox(0x5f7a7a,
                        Utils.lightenColor(0x5f7a7a, 20),
                        getWidth() - 10,
                        getHeight() - 40,
                        Graphics.LEFT | Graphics.VCENTER,
                        mContext.mTahomaFontCyan);
                mTextBox.setEditable(true, 255);
                mTextBox.setEditableText("DONG");
//                mTextBox.addParagraph("On the margin of a lake in the " +
//                        "margin of the page a margin of 600d " +
//                        "to escape death by a narrow margin. ", mContext.mTahomaFontBlue);
//                mTextBox.addParagraph("His health is a " +
//                        "a Shakespeare " +
//                        "a Vietnamese grammar. " +
//                        "twice a week. ", mContext.mTahomaFontBlue);
//                mTextBox.addParagraph("On the margin of a lake in the " +
//                        "margin of the page a margin of 600d " +
//                        "to escape death by a narrow margin. ", mContext.mTahomaFontWhite);
//                mTextBox.addParagraph("His health is a " +
//                        "a Shakespeare " +
//                        "a Vietnamese grammar. " +
//                        "twice a week. ", mContext.mTahomaFontBlue);
//                mTextBox.addParagraph("On the margin of a lake in the " +
//                        "margin of the page a margin of 600d " +
//                        "to escape death by a narrow margin. ", mContext.mTahomaFontRed);
//                mTextBox.addParagraph("His health is a " +
//                        "a Shakespeare " +
//                        "a Vietnamese grammar. " +
//                        "twice a week. ", mContext.mTahomaFontBlue);
//                mTextBox.addParagraph("On the margin of a lake in the " +
//                        "margin of the page a margin of 600d " +
//                        "to escape death by a narrow margin. ", mContext.mTahomaOutlineBlue);
//                mTextBox.addParagraph("His health is a " +
//                        "a Shakespeare " +
//                        "a Vietnamese grammar. " +
//                        "twice a week. ", mContext.mTahomaOutlineRed);
                mTextBox.setScrollBar(true, 0x5AD500, 0x6F6F6F);
                break;
        }
    }

    public void onTick(long aMilliseconds) {
        //repaint();
        //serviceRepaints();
    }

    public void keyPressed(int keyCode) {
        System.out.println("ScreenMainMenu KeyCode: " + keyCode);
        
        if (mIsDisplayDialog)
        {
            switch (keyCode)
            {
                case Key.SOFT_LEFT:
                    if (mLeftSoftkey == SOFTKEY_CANCEL)
                    {
                        mIsDisplayDialog = false;
                        setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
                    }
                    break;
                case Key.SOFT_RIGHT:
                    if (mRightSoftkey == SOFTKEY_OK)
                    {
                        mContext.stop();
                        mContext.mMIDlet.notifyDestroyed();
                    }
                    break;
            }
            return;
        }
        
        switch (keyCode) {
            case Key.UP:
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
                if (mMenu != null) {
                    mMenu.onDirectionKeys(2);
                }
                if (mTextBox != null && mTextBox.mIsEditable) {
                    mTextBox.onInput(keyCode);
                }
                break;
            case Key.RIGHT:
                if (mMenu != null) {
                    mMenu.onDirectionKeys(3);
                }
                if (mTextBox != null && mTextBox.mIsEditable) {
                    mTextBox.onInput(keyCode);
                }
                break;
            case Key.SELECT:
            case Key.SOFT_RIGHT:
                if (keyCode == Key.SOFT_RIGHT && mRightSoftkey != SOFTKEY_OK) {
                    return;
                }
                if (mMenu != null) {
                    switch (mCurrentMenu) {
                        case MENU_MAIN:
                            switch (mMenu.selectedItem()) {
                                case 0:
                                    //setMenu(MENU_ONLINE_PLAY);
                                    ScreenOnlinePlay aScreenOnlinePlay = new ScreenOnlinePlay(mContext);
                                    mContext.setScreen(aScreenOnlinePlay);
                                    break;
                                case 1:
                                    //setMenu(MENU_OFFLINE_PLAY);
//                                    ScreenLoading aScreenLoading = new ScreenLoading(mContext);
//                                    aScreenLoading.setLoadingScript(ScreenLoading.LOADING_SCRIPT_GAMEPLAY_OFFLINE);                                            
//                                    mContext.setScreen(aScreenLoading);
                                    ScreenOfflinePlay screenOffline = new ScreenOfflinePlay(mContext);
                                    mContext.setScreen(screenOffline);
                                    break;
                                case 2:
                                    setMenu(MENU_OPTION);
                                    break;
                                case 3:
                                    setMenu(MENU_HELP);
                                    break;
                                case 4:
                                    setMenu(MENU_ABOUT);
                                    break;
                                case 5:
                                    mIsDisplayDialog = true;
                                    mDialog.setText("Bạn có thực sự muốn thoát?");
                                    setSoftKey(SOFTKEY_CANCEL, -1, SOFTKEY_OK);
                                    //mContext.mMIDlet.notifyDestroyed();
                                    break;
                            }
                            break;
                    }
                }
                break;
            case Key.SOFT_LEFT:
//                if (mMenu != null)
//                {
                switch (mCurrentMenu) {
                    case MENU_MAIN:
                        mIsDisplayDialog = true;
                        mDialog.setText("Bạn có thực sự muốn thoát?");
                        setSoftKey(SOFTKEY_CANCEL, -1, SOFTKEY_OK);
                        break;
                    case MENU_OFFLINE_PLAY:
                        if (mLeftSoftkey == SOFTKEY_BACK) {
                            setMenu(MENU_MAIN);
                        }
                        break;
                    case MENU_OPTION:
                        if (mLeftSoftkey == SOFTKEY_BACK) {
                            setMenu(MENU_MAIN);
                        }
                        break;
                    case MENU_HELP:
                        if (mLeftSoftkey == SOFTKEY_BACK) {
                            setMenu(MENU_MAIN);
                        }
                        break;
                    case MENU_ABOUT:
                        if (mLeftSoftkey == SOFTKEY_BACK) {
                            setMenu(MENU_MAIN);
                        }
                        break;
                }
//                }
                break;
            default:
                if (mTextBox != null && mTextBox.mIsEditable) {
                    mTextBox.onInput(keyCode);
                }
        }
    }

    public void paint(Graphics g) {
        //g.drawImage(mContext.mBackgroundImage, 0, 0, 0);
        g.setClip(0, 0, getWidth(), getHeight());
        g.setColor(0x708585);
        g.fillRect(0, 0, getWidth(), getHeight());

        String menu_name = null;
        switch (mCurrentMenu) {
            case MENU_MAIN:
                menu_name = StringConst.STR_TITLE_APP;
                break;
            case MENU_OFFLINE_PLAY:
                menu_name = StringConst.STR_TITLE_OFFLINE_PLAY_MENU;
                break;
            case MENU_OPTION:
                menu_name = StringConst.STR_TITLE_OPTION_MENU;
                break;
            case MENU_ABOUT:
                menu_name = StringConst.STR_TITLE_ABOUT_MENU;
                break;
            case MENU_HELP:
                menu_name = StringConst.STR_TITLE_HELP_MENU;
                break;
        }

        int w = getWidth() - 4;//mContext.mTahomaOutlineWhite.getWidth(menu_name) + 6;

        int h = mContext.mTahomaOutlineWhite.getHeight() + 4;
        int x = (getWidth() - w) >> 1;
        int y = 2;
        int banner_color = 0xfe9090;
        //g.setColor(banner_color);
        //g.fillRect(x, y, w, h);
//        g.setColor(Utils.lightenColor(0x708585, 30));
//        g.drawLine(x, y, x, y + h - 1);
//        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
//        g.setColor(Utils.darkenColor(0x708585, 30));
//        g.drawLine(x + w - 1, y, x + w - 1, y + h - 2);
//        g.drawLine(x + 1, y, x + w - 1, y);        

        mContext.mTahomaOutlineWhite.write(g, menu_name, getWidth() >> 1, y + (h >> 1), Graphics.HCENTER | Graphics.VCENTER);

        if (mMenu != null) {
            mMenu.paint(g,
                    mContext.mTahomaFontGreen,
                    mContext.mTahomaOutlineGreen,
                    mContext.mTahomaOutlineRed,
                    getWidth() >> 1,
                    (getHeight() - 100) >> 1,
                    Graphics.HCENTER | Graphics.VCENTER);
        }

        if (mTextBox != null) {
            mTextBox.paint(g, getWidth() >> 1, getHeight() >> 1, Graphics.HCENTER | Graphics.VCENTER, false);
        }
        
        if (mIsDisplayDialog)
            mDialog.paint(g);

        drawSoftkey(g);
    }
}
