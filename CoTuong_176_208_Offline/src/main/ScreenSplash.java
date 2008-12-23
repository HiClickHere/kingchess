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
import ui.SoundManager;
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
public class ScreenSplash extends Screen {    

    private Image mSplashImg;    
    /** Creates a new instance of ScreenMainMenu */
    public ScreenSplash(Context aContext) {
        super(aContext);
        String size = "small";
        if (getWidth() > 212 && getHeight() > 250)
            size = "large";
        try {
            mSplashImg = Image.createImage("/res/img/game/splash.png");            
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onActivate() {
        mContext.mSoundManager.play(SoundManager.SOUND_THEME);
    }

    public void onTick(long aMilliseconds) {
    }

    public void keyPressed(int keyCode) 
    {
        switch (keyCode)
        {
            case Key.SELECT:
            case Key.NUM_5:
                ScreenMainMenu menu = new ScreenMainMenu(mContext);
                menu.setMenu(ScreenMainMenu.MENU_MAIN);
                mContext.setScreen(menu);
                break;
        }
    }
    
    private boolean need_draw = true;

    public void paint(Graphics g) 
    {
        g.setColor(0);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(mSplashImg, getWidth() >> 1, getHeight() >> 1, Graphics.HCENTER | Graphics.VCENTER);        
        mContext.mTahomaFontWhite.write(g, "MobileLabs 2008", getWidth() >> 1, 3, Graphics.HCENTER | Graphics.TOP);
        if (need_draw)
        {
            mContext.mTahomaOutlineGreen.write(g, "Ấn OK/5", getWidth() >> 1, getHeight() - 3, Graphics.HCENTER | Graphics.BOTTOM);
            need_draw = false;
        }
        else
            need_draw = true;
    }
}
