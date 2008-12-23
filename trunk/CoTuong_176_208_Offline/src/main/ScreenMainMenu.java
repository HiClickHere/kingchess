package main;

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
public class ScreenMainMenu extends Screen {
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
    
    public final static int BUTTON_ONLINE_PLAY = 0;
    public final static int BUTTON_OFFLINE_PLAY = 1;
    public final static int BUTTON_OPTION = 2;
    public final static int BUTTON_HELP = 3;
    public final static int BUTTON_ABOUT = 4;
    public final static int BUTTON_EXIT = 5;
    public final static int BUTTON_SOUND = 6;
    public final static int BUTTON_AUTO_BOT = 7;    
    public final static int BUTTON_VIEW_SAVED = 8;

    public void setMenu(int menuType) {
        mMenu = null;
        mTextBox = null;
        mCurrentMenu = menuType;
        switch (menuType) {
            case MENU_MAIN:
                mMenu = new Menu();
                mMenu.setColors(0x5f7a7a, 0x708585);
//                mMenu.addItem(BUTTON_ONLINE_PLAY,
//                        "Chơi mạng",
//                        false,
//                        false,
//                        20,
//                        getWidth() - 40,
//                        getWidth() >> 1,
//                        -1,
//                        Graphics.HCENTER | Graphics.VCENTER);
                mMenu.addItem(BUTTON_OFFLINE_PLAY, "Chơi đơn",
                        false,
                        false,
                        20,
                        getWidth() - 40,
                        getWidth() >> 1,
                        -1,
                        Graphics.HCENTER | Graphics.VCENTER);                
//                mMenu.addItem(BUTTON_VIEW_SAVED, 
//                        "Xem lại",
//                        false,
//                        false,
//                        20,
//                        getWidth() - 40,
//                        getWidth() >> 1,
//                        -1,
//                        Graphics.HCENTER | Graphics.VCENTER);
                mMenu.addItem(BUTTON_OPTION, StringConst.STR_OPTION,
                        false,
                        false,
                        20,
                        getWidth() - 40,
                        getWidth() >> 1,
                        -1,
                        Graphics.HCENTER | Graphics.VCENTER);
                mMenu.addItem(BUTTON_HELP, StringConst.STR_HELP,
                        false,
                        false,
                        20,
                        getWidth() - 40,
                        getWidth() >> 1,
                        -1,
                        Graphics.HCENTER | Graphics.VCENTER);
                mMenu.addItem(BUTTON_ABOUT, StringConst.STR_ABOUT, false,
                        false,
                        20,
                        getWidth() - 40,
                        getWidth() >> 1,
                        -1,
                        Graphics.HCENTER | Graphics.VCENTER);
                mMenu.addItem(BUTTON_EXIT, StringConst.STR_EXIT, false,
                        false,
                        20,
                        getWidth() - 40,
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
                String soundStatus = "Âm thanh: BẬT";
                if (!mContext.mSoundManager.isActive())
                   soundStatus = "Âm thanh: TẮT";
                mMenu.addItem(BUTTON_SOUND, 
                        soundStatus,
                        false,
                        false,
                        20,
                        getWidth() - 40,
                        getWidth() >> 1,
                        -1,
                        Graphics.HCENTER | Graphics.VCENTER);
                if (mContext.mCheatEnable)
                {
                    if (!mContext.mIsAutoBot)
                    {
                        mMenu.addItem(BUTTON_AUTO_BOT, "Tự động: TẮT", false,
                                false,
                                20,
                                getWidth() - 40,
                                getWidth() >> 1,
                                -1,
                                Graphics.HCENTER | Graphics.VCENTER);
                    }
                    else
                    {
                        mMenu.addItem(BUTTON_AUTO_BOT, "Tự động: BẬT", false,
                                false,
                                20,
                                getWidth() - 40,
                                getWidth() >> 1,
                                -1,
                                Graphics.HCENTER | Graphics.VCENTER);
                    }
                }
                setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
                break;
            case MENU_HELP:
                mMenu = null;
                mTextBox = new TextBox(0x5f7a7a,
                        Utils.lightenColor(0x5f7a7a, 20),
                        getWidth() - 20,
                        getHeight() - 60,
                        Graphics.HCENTER | Graphics.VCENTER,
                        mContext.mTahomaFontBlue);
                mTextBox.addParagraph ("Điều khiển", mContext.mTahomaOutlineRed);
                mTextBox.addParagraph("Các phím mũi tên: Điều khiển con trỏ. ", mContext.mTahomaOutlineGreen);
                mTextBox.addParagraph("Phím Select/OK: Chọn các đề mục trên menu và chọn các quân cờ/vị trí trong game. ", mContext.mTahomaOutlineGreen);
                mTextBox.addParagraph("Các phím Softkey: Thực hiện các chức năng có trên màn hình. Góc trái dưới ứng với softkey bên trái, phải dưới ứng với softkey bên phải. ", mContext.mTahomaOutlineGreen);
                mTextBox.addParagraph("Các phím số/chữ: Dùng để nhập ký tự trong các textbox. ", mContext.mTahomaOutlineGreen);
                mTextBox.addParagraph("", mContext.mTahomaOutlineGreen);
                mTextBox.addParagraph("Chúc bạn chơi vui vẻ!", mContext.mTahomaOutlineGreen);                                        
                mTextBox.setScrollBar(true, 0x5AD500, 0x6F6F6F);
                setSoftKey(SOFTKEY_BACK, -1, -1);
                break;
            case MENU_ABOUT:
                mMenu = null;
                mTextBox = new TextBox(0x5f7a7a,
                        Utils.lightenColor(0x5f7a7a, 20),
                        getWidth() - 20,
                        getHeight() - 60,
                        Graphics.HCENTER | Graphics.VCENTER,
                        mContext.mTahomaFontBlue);
                mTextBox.addParagraph ("Thông tin", mContext.mTahomaOutlineRed);                                                
                mTextBox.addParagraph("Xin cảm ơn bạn đã sử dụng sản phẩm game Cờ Tướng v" + mContext.mVersion + ". Chý ý: Phiên bản này chỉ cho phép chơi đơn.", mContext.mTahomaOutlineGreen);                                
                mTextBox.addParagraph("", mContext.mTahomaOutlineGreen);                
                mTextBox.addParagraph("Phiên bản Cờ Tướng Trực Tuyến (chỉ bao gồm chức năng chơi đơn) này có sử dụng engine mã nguồn mở XQWLight từ www.elephantbase.net với giấy phép GNU General Public License (GPL). Phiên bản này được phân phối kèm mã nguồn và mã nguồn có thể tải về từ code.google.com/p/kingchess", mContext.mTahomaOutlineGreen);
                mTextBox.addParagraph("", mContext.mTahomaOutlineGreen);                
                mTextBox.addParagraph("Lập trình", mContext.mTahomaOutlineRed);
                mTextBox.addParagraph("Nguyễn Hà Đông", mContext.mTahomaOutlineGreen);                                
                mTextBox.addParagraph("", mContext.mTahomaOutlineGreen);                
                mTextBox.addParagraph("Đồ họa", mContext.mTahomaOutlineRed);
                mTextBox.addParagraph("Nguyễn Hà Nam", mContext.mTahomaOutlineGreen);                                
                mTextBox.addParagraph("Nguyễn Hà Đông", mContext.mTahomaOutlineGreen);                                
                mTextBox.addParagraph("", mContext.mTahomaOutlineGreen);    
                mTextBox.addParagraph("Với sự giúp đỡ của", mContext.mTahomaOutlineRed);
                mTextBox.addParagraph("Nguyễn Doãn Hòa", mContext.mTahomaOutlineGreen);
                mTextBox.addParagraph("Trần Anh Tuấn", mContext.mTahomaOutlineGreen);
                mTextBox.addParagraph("Trung tâm Tính Toán Hiệu Năng Cao HPCC - Đại Học Bách Khoa Hà Nội", mContext.mTahomaOutlineGreen);                
                mTextBox.addParagraph("", mContext.mTahomaOutlineGreen);
                mTextBox.addParagraph("Mọi thắc mắc và đóng góp xin liên hệ ", mContext.mTahomaOutlineGreen);
                mTextBox.addParagraph("Nguyễn Hà Đông", mContext.mTahomaOutlineGreen);                
                mTextBox.addParagraph("nguyenhadoong@gmail.com", mContext.mTahomaOutlineGreen);                
                mTextBox.addParagraph("SĐT: 090 659 6015", mContext.mTahomaOutlineGreen);                                                
                mTextBox.setScrollBar(true, 0x5AD500, 0x6F6F6F);
                setSoftKey(SOFTKEY_BACK, -1, -1);
                break;
        }
    }

    public void onTick(long aMilliseconds) {
        //repaint();
        //serviceRepaints();
    }

    public void keyPressed(int keyCode) {
        //System.out.println("ScreenMainMenu KeyCode: " + keyCode);
        
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
                if (mMenu != null) {
                    switch (mCurrentMenu) {
                        case MENU_MAIN:
                            switch (mMenu.selectedItem()) {
                                case BUTTON_OFFLINE_PLAY:
                                    //setMenu(MENU_OFFLINE_PLAY);
//                                    ScreenLoading aScreenLoading = new ScreenLoading(mContext);
//                                    aScreenLoading.setLoadingScript(ScreenLoading.LOADING_SCRIPT_GAMEPLAY_OFFLINE);                                            
//                                    mContext.setScreen(aScreenLoading);
                                    ScreenOfflinePlay screenOffline = new ScreenOfflinePlay(mContext);
                                    mContext.setScreen(screenOffline);
                                    break;
//                                case BUTTON_VIEW_SAVED:
//                                    ScreenViewSaved viewSaved = new ScreenViewSaved(mContext);
//                                    mContext.setScreen(viewSaved);
//                                    break;
                                case BUTTON_OPTION:
                                    setMenu(MENU_OPTION);
                                    break;
                                case BUTTON_HELP:
                                    setMenu(MENU_HELP);
                                    break;
                                case BUTTON_ABOUT:
                                    setMenu(MENU_ABOUT);
                                    break;
                                case BUTTON_EXIT:
                                    mIsDisplayDialog = true;
                                    mDialog.setText("Bạn có thực sự muốn thoát?");
                                    setSoftKey(SOFTKEY_CANCEL, -1, SOFTKEY_OK);
                                    //mContext.mMIDlet.notifyDestroyed();
                                    break;
                            }
                            break;
                        case MENU_OPTION:
                            switch (mMenu.selectedItem())
                            {
                                case BUTTON_SOUND:
                                    //mContext.mSoundEnable = !mContext.mSoundEnable;
                                    mContext.mSoundManager.setActive(!mContext.mSoundManager.isActive());
                                    String soundStatus = "Âm thanh: BẬT";
                                    if (!mContext.mSoundManager.isActive())
                                        soundStatus = "Âm thanh: TẮT";
                                    mMenu.getItem(BUTTON_SOUND).mCaption = soundStatus;
                                    break;
                                case BUTTON_AUTO_BOT:
                                    mContext.mIsAutoBot = !mContext.mIsAutoBot;
                                    if (mContext.mIsAutoBot)
                                        mMenu.getItem(BUTTON_AUTO_BOT).mCaption = "Tự động: BẬT";
                                    else
                                        mMenu.getItem(BUTTON_AUTO_BOT).mCaption = "Tự động: TẮT";
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
        g.drawImage(mContext.mMenuBackground, getWidth() >> 1, getHeight() >> 1, Graphics.HCENTER | Graphics.VCENTER);

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

        g.drawImage(mContext.mMenuBanner, getWidth() >> 1, 0, Graphics.HCENTER | Graphics.TOP);
        mContext.mTahomaOutlineWhite.write(g, menu_name, getWidth() >> 1, y + (h >> 1), Graphics.HCENTER | Graphics.VCENTER);

        if (!mIsDisplayDialog && mMenu != null) {
            mMenu.paint(g,
                    mContext.mTahomaFontGreen,
                    mContext.mTahomaOutlineGreen,
                    mContext.mTahomaOutlineRed,
                    getWidth() >> 1,
                    (getHeight() - 100) >> 1,
                    Graphics.HCENTER | Graphics.VCENTER);
            
            mContext.mTahomaFontWhite.write(g, "Chú ý: Phiên bản này chỉ chơi đơn,", getWidth() >> 1, getHeight() - 25, Graphics.HCENTER | Graphics.VCENTER);
            mContext.mTahomaFontWhite.write(g, "ngoại tuyến.", getWidth() >> 1, getHeight() - 10, Graphics.HCENTER | Graphics.VCENTER);
        }

        if (mTextBox != null) {
            mTextBox.paint(g, getWidth() >> 1, getHeight() >> 1, Graphics.HCENTER | Graphics.VCENTER, false);
        }
        
        if (mIsDisplayDialog)
            mDialog.paint(g);

        drawSoftkey(g);        
    }
}
