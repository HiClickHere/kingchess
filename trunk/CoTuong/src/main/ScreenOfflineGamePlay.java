/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javax.microedition.lcdui.Graphics;
import ui.ContextMenu;
import util.Key;

/**
 *
 * @author dong
 */
public class ScreenOfflineGamePlay extends ScreenGamePlay { 

    public ScreenOfflineGamePlay(Context aContext) {
        super(aContext);
        
        // Generate menu for offline playing mode
        mContextMenu = new ContextMenu(mContext.mTahomaFontGreen, mContext.mTahomaOutlineGreen);        
        mContextMenu.setColors(0x5f7a7a, 0x708585);
        mContextMenu.addItem(BUTTON_CONTINUE, "Tiếp tục", false,
                            false,
                            20,
                            90,
                            getWidth() >> 1,
                            -1,
                            Graphics.HCENTER | Graphics.VCENTER);        
        mContextMenu.addItem(BUTTON_QUIT, "Thoát ra", false,
                            false,
                            20,
                            90,
                            getWidth() >> 1,
                            -1,
                            Graphics.HCENTER | Graphics.VCENTER);
    }
    
    public void onActivate() {
        super.onActivate();

        // if computer go first, let him go
        if (flip)
        {
            responseMove();
        }
    }

    public void onTick(long aMilliseconds) 
    {
        super.onTick(aMilliseconds);
    }        

    public void paint(Graphics g) 
    {
        super.paint(g);
        if (phase == PHASE_THINKING)
        {
            g.setColor(0);
            int w = mContext.mTahomaOutlineGreen.getWidth("Suy nghĩ...");
            g.fillRect(getWidth() - w, 0, w, mContext.mTahomaOutlineGreen.getHeight());
            mContext.mTahomaOutlineGreen.write(g, "Suy nghĩ...", getWidth(), 0, Graphics.RIGHT | Graphics.TOP);
        }                   
    }

    public void keyPressed(int keyCode) 
    {                
        if (mIsDisplayDialog)
        {
            switch (keyCode)
            {
                case Key.SOFT_LEFT:
                    if (mState == STATUS_QUIT_GAME_DIALOG)
                        dismissDialog();                    
                    break;
                case Key.SOFT_RIGHT:
                    if (mState == STATUS_QUIT_GAME_DIALOG)
                    {
                        System.out.println("act QUIT");
                        dismissDialog();
                        ScreenOfflinePlay screenOffline = new ScreenOfflinePlay(mContext);
                        mContext.setScreen(screenOffline);
                    }
                    else if (mState == STATUS_END_GAME_DIALOG)
                    {
                        dismissDialog();
                        ScreenOfflinePlay screenOffline = new ScreenOfflinePlay(mContext);
                        mContext.setScreen(screenOffline);
                    }
                    break;
            }
            return;
        }
        
        if (mIsDisplayMenu)
        {
            switch (keyCode)
            {
                case Key.UP:
                    mContextMenu.onDirectionKeys(0);
                    break;
                case Key.DOWN:
                    mContextMenu.onDirectionKeys(1);
                    break;
                case Key.SELECT:
                    switch (mContextMenu.selectedItem())
                    {
                        case BUTTON_CONTINUE:
                            mIsDisplayMenu = false;
                            break;
                        case BUTTON_QUIT:
                            addDialog("Tính năng lưu game chưa hoàn thiện. Bạn thực sự muốn thoát?", SOFTKEY_CANCEL, SOFTKEY_OK, STATUS_QUIT_GAME_DIALOG);
                            System.out.println("added QUIT dialog");
                            break;
                    }
                    break;
                
            }
            return;
        }
            int deltaX = 0, deltaY = 0;
            switch (keyCode) {
                case Key.SELECT:
                    clickSquare();
                    break;
                case Key.UP:
                    deltaY = -1;
                    break;
                case Key.LEFT:
                    deltaX = -1;
                    break;
                case Key.RIGHT:
                    deltaX = 1;
                    break;
                case Key.DOWN:
                    deltaY = 1;
                    break;
                case Key.SOFT_RIGHT:
                    mIsDisplayMenu = true;
                    break;
            }
            cursorX = (cursorX + deltaX + 9) % 9;
            cursorY = (cursorY + deltaY + 10) % 10;
    }
        
    public final static int STATUS_END_GAME_DIALOG = 2;
    public final static int STATUS_QUIT_GAME_DIALOG = 1;

    protected boolean responseMove() {
        boolean result = super.responseMove();
        if (isEndGame)
        {
            addDialog(message, -1, SOFTKEY_OK, STATUS_END_GAME_DIALOG);
        }
        return result;
    }
}
