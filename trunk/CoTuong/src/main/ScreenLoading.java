/*
 * ScreenLoading.java
 *
 * Created on October 31, 2008, 7:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package main;

import core.Screen;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import util.ImageFont;
import util.UnicodeFont;

/**
 *
 * @author dong
 */
public class ScreenLoading extends Screen {
    public final static int LOADING_SCRIPT_FIRST_INIT = 1;
    public final static int LOADING_SCRIPT_GAMEPLAY = 2;
    public final static int LOADING_SCRIPT_GAMEPLAY_OFFLINE = 3;
    
    public int mPercentCompleted;
    public int mLoadingScript;
    public int[] mARGB;
    private int mTempWidth;
    private int mTempHeight;
    
    /**
     * Creates a new instance of ScreenLoading
     */
    public ScreenLoading(Context aContext) {
        super(aContext);
    }
    
    public void onActivate()
    {
        mPercentCompleted = 0;        
        mContext.mIsLoading = true;
        mIsBusy = false;
    }
    
    public void updatePercent(int addedPercent)
    {
        mPercentCompleted += addedPercent;       
//        repaint();
//        serviceRepaints();
    }        
    
    public void setLoadingScript(int newScript)
    {
        mLoadingScript = newScript;
    }
    
    private boolean mIsBusy = false;
    
    public void onTick(long aMilliseconds)
    {
        if (mIsBusy)
            return;
                        
        mIsBusy = true;
        
        switch (mLoadingScript)
        {
            case LOADING_SCRIPT_FIRST_INIT:
//                int aARGB[] = null;
//                if (mARGB != null)
//                    aARGB = new int[mARGB.length];                
                switch (mPercentCompleted)
                {                    
                    case 0:
                        
//                        try {
//                            //mContext.mUnicodeFont = new UnicodeFont("/res/img/fonts/verdana.png", "/res/img/fonts/verdana.dat", false, 0xFF808080, 0xFFFFFFFF);
//                            //mContext.mUnicodeFontOutline = new UnicodeFont("/res/img/fonts/verdana_outline.png", "/res/img/fonts/verdana.dat", true, 0xff9CFF38, 0xFF2D5900);
//                            //mContext.mTahomaFont = new UnicodeFont("/res/img/fonts/tahoma.png", "/res/img/fonts/tahoma.dat", false, 0xFF808080, 0xFFFFFFFF);
//                            //mContext.mTahomaOutline = new UnicodeFont("/res/img/fonts/tahoma_outline.png", "/res/img/fonts/tahoma.dat", true, 0xff9CFF38, 0xFF2D5900);
//                        } catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
                        updatePercent(10);
                        break;
                    case 10:                             
                        int colorcyan = 0xFF6699FF;
                        int colorCyanHL1 = 0xff6699ff;
                        int colorCyanHL2 = 0xFF10244d ;
                        try {
                            mContext.mTahomaFontCyan = new UnicodeFont("/res/img/fonts/tahoma.png", "/res/img/fonts/tahoma.dat", false, colorcyan, colorcyan);
                            mContext.mTahomaOutlineCyan = new UnicodeFont("/res/img/fonts/tahoma_outline.png", "/res/img/fonts/tahoma.dat", true, colorCyanHL1, colorCyanHL2);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        updatePercent(10);
                        break;
                    case 20:                        
                        int colorred = 0xFFE54C2E;  
                        int colorRedHL1 = 0xffc44127;
                        int colorRedHL2 = 0xFF4d0d00 ;
                        try {
                            mContext.mTahomaFontRed = new UnicodeFont("/res/img/fonts/tahoma.png", "/res/img/fonts/tahoma.dat", false, colorred, colorred);
                            mContext.mTahomaOutlineRed = new UnicodeFont("/res/img/fonts/tahoma_outline.png", "/res/img/fonts/tahoma.dat", true, colorRedHL1, colorRedHL2);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        updatePercent(10);
                        break;
                    case 30:                        
                        int colorgreen = 0xff9cff39;   
                        int colorGreenHL1 = 0xff9cff38;
                        int colorGreenHL2 = 0xFF2d5900;
                        try {
                            mContext.mTahomaFontGreen = new UnicodeFont("/res/img/fonts/tahoma.png", "/res/img/fonts/tahoma.dat", false, colorgreen, colorgreen);
                            mContext.mTahomaOutlineGreen = new UnicodeFont("/res/img/fonts/tahoma_outline.png", "/res/img/fonts/tahoma.dat", true, colorGreenHL1, colorGreenHL2);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        updatePercent(10);
                        break;
                     case 40:                        
                        int colorblue = 0xff6699ff;
                        int colorBlueHL1 = 0xff6699ff;
                        int colorBlueHL2 = 0xFF10244d ;
                        try {
                            mContext.mTahomaFontBlue = new UnicodeFont("/res/img/fonts/tahoma.png", "/res/img/fonts/tahoma.dat", false, colorblue, colorblue);
                            mContext.mTahomaOutlineBlue = new UnicodeFont("/res/img/fonts/tahoma_outline.png", "/res/img/fonts/tahoma.dat", true, colorBlueHL1, colorBlueHL2);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }                        
                        updatePercent(10);
                        break;
                     case 50:                 
                        try {
                            mContext.mArrowUp = Image.createImage("/res/img/ui/arrow_up.png");
                            mContext.mArrowDown = Image.createImage("/res/img/ui/arrow_down.png");
                            mContext.mArrowLeft = Image.createImage("/res/img/ui/arrow_left.png");
                            mContext.mArrowRight = Image.createImage("/res/img/ui/arrow_right.png");
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }                        
                        updatePercent(10);
                        break;   
                     case 60:               
                        try {
                            Image aImage = Image.createImage("/res/img/ui/background_line.png");
                            mContext.mBackgroundImage = Image.createImage(getWidth(), getHeight());
                            Graphics g = mContext.mBackgroundImage.getGraphics();
                            for (int i = 0; i < getWidth(); i++)
                                g.drawImage(aImage, i, 0, 0);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }                                
                        updatePercent(10);
                        break; 
                     case 70:    
                         try {            
                            mContext.mSoftkeyOK = Image.createImage("/res/img/ui/softkey_ok.png");
                            mContext.mSoftkeyBack = Image.createImage("/res/img/ui/softkey_back.png");
                            mContext.mSoftkeyMenu = Image.createImage("/res/img/ui/softkey_menu.png");            
                            mContext.mSoftkeyCancel = Image.createImage("/res/img/ui/softkey_cancel.png"); 
                        } catch (Exception e)
                        {            
                            e.printStackTrace();
                        }        
                        updatePercent(10);
                        break;      
                    case 80:              
                        updatePercent(10);
                        break;   
                    case 90:           
                        int colorWhite = 0xFF859ca1;
                        int colorWhiteHL1 = 0xffffffff;
                        int colorWhiteHL2 = 0xFF859ca1 ;
                        try {
                            mContext.mTahomaFontWhite = new UnicodeFont("/res/img/fonts/tahoma.png", "/res/img/fonts/tahoma.dat", false, colorWhite, colorWhite);
                            mContext.mTahomaOutlineWhite = new UnicodeFont("/res/img/fonts/tahoma_outline.png", "/res/img/fonts/tahoma.dat", true, colorWhiteHL1, colorWhiteHL2);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }                        
                        updatePercent(10);
                        break;                      
                    case 100:            
                        mContext.mIsLoading = false;
                        ScreenMainMenu mainMenu = new ScreenMainMenu(mContext);
                        mainMenu.setMenu(ScreenMainMenu.MENU_MAIN);                        
                        mContext.setScreen(mainMenu);
                        break;                    
                }               
                System.gc();
                break;
            case LOADING_SCRIPT_GAMEPLAY:
                switch (mPercentCompleted)
                {
                    case 0:
                        try
                        {
                            for (int i = 0; i < 7; i++)
                            {
                                mContext.mRedPieces[i] = Image.createImage("/res/img/game/red" + i + ".png");
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        System.gc();
                        updatePercent(20);
                        break;
                    case 20:
                        updatePercent(20);
                        break;
                    case 40:
                        try
                        {
                            for (int i = 0; i < 7; i++)
                            {
                                mContext.mBlackPieces[i] = Image.createImage("/res/img/game/black" + i + ".png");
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        System.gc();
                        updatePercent(20);
                        break;
                    case 60:
                        updatePercent(20);
                        break;
                    case 80:
                        updatePercent(20);
                        break;
                    case 100:
                        try {
                            mContext.mBoardImage = Image.createImage("/res/img/game/board.png");
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }                        
                        System.gc();                        
                        mContext.mIsLoading = false;
                        mContext.setScreen(new ScreenGamePlay(mContext));
                        break;
                }                
                break;           
            case LOADING_SCRIPT_GAMEPLAY_OFFLINE:
                switch (mPercentCompleted)
                {
                    case 0:
                        try
                        {
                            for (int i = 0; i < 7; i++)
                            {
                                mContext.mRedPieces[i] = Image.createImage("/res/img/game/red" + i + ".png");
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        System.gc();
                        updatePercent(20);
                        break;
                    case 20:
                        updatePercent(20);
                        break;
                    case 40:
                        try
                        {
                            for (int i = 0; i < 7; i++)
                            {
                                mContext.mBlackPieces[i] = Image.createImage("/res/img/game/black" + i + ".png");
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        System.gc();
                        updatePercent(20);
                        break;
                    case 60:
                        updatePercent(20);
                        break;
                    case 80:
                        updatePercent(20);
                        break;
                    case 100:
                        try {
                            mContext.mBoardImage = Image.createImage("/res/img/game/board.png");
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }                        
                        System.gc();                        
                        mContext.mIsLoading = false;
                        mContext.setScreen(new ScreenOfflineGamePlay(mContext));
                        break;
                }                
                break;     
        }          
        
        mIsBusy = false;
    }
    
    public void paint(Graphics g)
    {
        g.setColor(0);        
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(0x00FF00);
        g.drawRect((getWidth() - 104) >> 1, (getHeight() - 5) >> 1, 104, 5);
        g.fillRect((getWidth() - 100) >> 1, (getHeight() - 2) >> 1, mPercentCompleted, 2);
    }    
}
