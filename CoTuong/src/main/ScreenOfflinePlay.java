/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import core.Screen;
import javax.microedition.lcdui.Graphics;
import util.*;

/**
 *
 * @author dong
 */
public class ScreenOfflinePlay extends Screen {
    
    protected  Menu mMenu;
    
    public ScreenOfflinePlay(Context aContext)
    {
        super(aContext);
    }

    public final static int BUTTON_COLOR = 0;
    public final static int BUTTON_LEVEL = 1;   
    public final static int BUTTON_START = 2;
    
    public final static String COLOR_RED = "Màu: Đỏ";
    public final static String COLOR_BLACK = "Màu: Đen";
    
    public final static String LEVEL_EASY = "Độ khó: Dễ";
    public final static String LEVEL_MEDIUM = "Độ khó: Trung bình";
    public final static String LEVEL_HARD = "Độ khó: Khó";
    
    public String mLevel;
    public String mColor;
    
    public void onActivate()
    {
        mColor = COLOR_RED;
        mContext.mOfflineColor = 0;
        mLevel = LEVEL_EASY;
        mContext.mOfflineLevel = 0;
        
        mMenu = new Menu();
        mMenu.setColors(0x5f7a7a, 0x708585);
        mMenu.addItem(BUTTON_COLOR, mColor, false,
                    false,
                    20,
                    90,
                    getWidth() >> 1,
                    -1,
                    Graphics.HCENTER | Graphics.VCENTER);
        mMenu.addItem(BUTTON_LEVEL, mLevel, false,
                    false,
                    20,
                    90,
                    getWidth() >> 1,
                    -1,
                    Graphics.HCENTER | Graphics.VCENTER);
        mMenu.addItem(BUTTON_START, "Bắt đầu", false,
                    false,
                    20,
                    90,
                    getWidth() >> 1,
                    -1,
                    Graphics.HCENTER | Graphics.VCENTER);
        setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
    }
    
    public void paint(Graphics g)
    {
        g.setClip(0, 0, getWidth(), getHeight());
        g.setColor(0x708585);
        g.fillRect(0, 0, getWidth(), getHeight());
        String menu_name = "CHƠI ĐƠN";
        int w = getWidth() - 4;
        int h = mContext.mTahomaOutlineWhite.getHeight() + 4;
        int x = (getWidth() - w) >> 1;
        int y = 2;
        int banner_color = 0xfe9090;

        mContext.mTahomaOutlineWhite.write(g, menu_name, getWidth() >> 1, y + (h >> 1), Graphics.HCENTER | Graphics.VCENTER);

        if (mMenu != null) 
        {
            mMenu.paint(g,
                    mContext.mTahomaFontGreen,
                    mContext.mTahomaOutlineGreen,
                    mContext.mTahomaOutlineRed,
                    getWidth() >> 1,
                    (getHeight() - 100) >> 1,
                    Graphics.HCENTER | Graphics.VCENTER);
        }
        
        drawSoftkey(g);
    }
    
    public void onTick(long aMilliseconds)
    {
        
    }
    
    public void keyPressed(int aKeyCode)
    {
        switch (aKeyCode)
        {
            case Key.UP:
                mMenu.onDirectionKeys(0);
                break;
            case Key.DOWN:
                mMenu.onDirectionKeys(1);
                break;
            case Key.SOFT_LEFT:
                ScreenMainMenu aScreenMenu = new ScreenMainMenu(mContext);
                aScreenMenu.setMenu(ScreenMainMenu.MENU_MAIN);
                mContext.setScreen(aScreenMenu);
                break;
            case Key.SELECT:
            case Key.SOFT_RIGHT:
                switch (mMenu.selectedItem())
                {
                    case BUTTON_COLOR:
                        if (mMenu.getItem(BUTTON_COLOR).mCaption.equals(COLOR_RED)) {
                            mMenu.getItem(BUTTON_COLOR).mCaption = COLOR_BLACK;    
                            mContext.mOfflineColor = 1;
                        }
                        else {
                            mMenu.getItem(BUTTON_COLOR).mCaption = COLOR_RED;    
                            mContext.mOfflineColor = 0;
                        }
                        break;
                    case BUTTON_LEVEL:
                        if (mMenu.getItem(BUTTON_LEVEL).mCaption.equals(LEVEL_EASY)){
                            mContext.mOfflineLevel = 1;
                            mMenu.getItem(BUTTON_LEVEL).mCaption = LEVEL_MEDIUM;
                        }
                        else if (mMenu.getItem(BUTTON_LEVEL).mCaption.equals(LEVEL_MEDIUM)) {
                            mContext.mOfflineLevel = 2;
                            mMenu.getItem(BUTTON_LEVEL).mCaption = LEVEL_HARD;
                        }
                        else {
                            mContext.mOfflineLevel = 0;
                            mMenu.getItem(BUTTON_LEVEL).mCaption = LEVEL_EASY;
                        }
                        break;
                    case BUTTON_START:
                        ScreenLoading aScreenLoading = new ScreenLoading(mContext);
                        aScreenLoading.setLoadingScript(ScreenLoading.LOADING_SCRIPT_GAMEPLAY_OFFLINE);                                            
                        mContext.setScreen(aScreenLoading);
                        break;
                }
                break;
            
        }
    }
}
