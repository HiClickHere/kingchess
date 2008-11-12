/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import core.Screen;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import util.ContextMenu;
import util.Key;

/**
 *
 * @author dong
 */
public class ScreenGamePlay extends Screen {    
    
    public final static int PIECE_GENERAL = 0;
    public final static int PIECE_GUARD = 1;
    public final static int PIECE_ELEPHANT = 2;
    public final static int PIECE_CAVALRY = 3;
    public final static int PIECE_ROOK = 4;
    public final static int PIECE_CANNON = 5;
    public final static int PIECE_SOLDIER = 6;
    
    public final static int BOARD_WIDTH = 9;
    public final static int BOARD_HEIGHT = 10;
    
    public final static int BLACK_SHIFT = 10;
    
    public int mBoard[][];
    
    public Image mBoardImage;
    public Image mRedPieces[];
    public Image mBlackPieces[];     
    
    public int mSelectedX;
    public int mSelectedY;
    public int mSelectingX;
    public int mSelectingY;
    
    ContextMenu mContextMenu;
    
    public boolean mIsDisplayMenu;

    public ScreenGamePlay(Context aContext)
    {
        super(aContext);
        mBoard = new int[BOARD_HEIGHT][BOARD_WIDTH];
        mBoardImage = mContext.mBoardImage;
        mRedPieces = mContext.mRedPieces;
        mBlackPieces = mContext.mBlackPieces;
        
        mContextMenu = new ContextMenu(mContext.mTahomaFontGreen, mContext.mTahomaOutlineGreen);        
        mContextMenu.setColors(0x5f7a7a, 0x708585);
        mContextMenu.addItem(0, "Tiếp tục", false,
                            false,
                            20,
                            90,
                            getWidth() >> 1,
                            -1,
                            Graphics.HCENTER | Graphics.VCENTER);
        mContextMenu.addItem(1, "Gửi tin nhắn", false,
                            false,
                            20,
                            90,
                            getWidth() >> 1,
                            -1,
                            Graphics.HCENTER | Graphics.VCENTER);
        mContextMenu.addItem(2, "Thoát ra", false,
                            false,
                            20,
                            90,
                            getWidth() >> 1,
                            -1,
                            Graphics.HCENTER | Graphics.VCENTER);
    }
    
    public void resetBoard()
    {
        for (int i = 0; i < BOARD_HEIGHT; i++)
            for (int j = 0; j < BOARD_WIDTH; j++)
                mBoard[i][j] = -1;
        
        mBoard[0][0] = BLACK_SHIFT + PIECE_ROOK;
        mBoard[0][1] = BLACK_SHIFT + PIECE_CAVALRY;
        mBoard[0][2] = BLACK_SHIFT + PIECE_ELEPHANT;
        mBoard[0][3] = BLACK_SHIFT + PIECE_GUARD;
        mBoard[0][4] = BLACK_SHIFT + PIECE_GENERAL;
        mBoard[0][5] = BLACK_SHIFT + PIECE_GUARD;
        mBoard[0][6] = BLACK_SHIFT + PIECE_ELEPHANT;
        mBoard[0][7] = BLACK_SHIFT + PIECE_CAVALRY;
        mBoard[0][8] = BLACK_SHIFT + PIECE_ROOK;
        
        mBoard[2][1] = BLACK_SHIFT + PIECE_CANNON;
        mBoard[2][7] = BLACK_SHIFT + PIECE_CANNON;
        
        mBoard[3][0] = BLACK_SHIFT + PIECE_SOLDIER;
        mBoard[3][2] = BLACK_SHIFT + PIECE_SOLDIER;
        mBoard[3][4] = BLACK_SHIFT + PIECE_SOLDIER;
        mBoard[3][6] = BLACK_SHIFT + PIECE_SOLDIER;
        mBoard[3][8] = BLACK_SHIFT + PIECE_SOLDIER;
        
        mBoard[BOARD_HEIGHT - 1][0] = PIECE_ROOK;
        mBoard[BOARD_HEIGHT - 1][1] = PIECE_CAVALRY;
        mBoard[BOARD_HEIGHT - 1][2] = PIECE_ELEPHANT;
        mBoard[BOARD_HEIGHT - 1][3] = PIECE_GUARD;
        mBoard[BOARD_HEIGHT - 1][4] = PIECE_GENERAL;
        mBoard[BOARD_HEIGHT - 1][5] = PIECE_GUARD;
        mBoard[BOARD_HEIGHT - 1][6] = PIECE_ELEPHANT;
        mBoard[BOARD_HEIGHT - 1][7] = PIECE_CAVALRY;
        mBoard[BOARD_HEIGHT - 1][8] = PIECE_ROOK;
        
        mBoard[7][1] = PIECE_CANNON;
        mBoard[7][7] = PIECE_CANNON;
        
        mBoard[6][0] = PIECE_SOLDIER;
        mBoard[6][2] = PIECE_SOLDIER;
        mBoard[6][4] = PIECE_SOLDIER;
        mBoard[6][6] = PIECE_SOLDIER;
        mBoard[6][8] = PIECE_SOLDIER;
    }
    
    public void onActivate()
    {
        resetBoard();
        mSelectingX = 0;
        mSelectingY = 0;
        mSelectedX = -1;
        mSelectedY = -1;
        mIsDisplayMenu = false;
    }
    
    public void onDeactivate()
    {
    }
    
    public void onTick(long aMilliseconds)
    {
        //repaint();
        //serviceRepaints();
    }
    
    public boolean mIsIndicatorUp;
    
    public void paint(Graphics g)
    {
        g.setColor(0x708585);
        g.fillRect(0, 0, getWidth(), getHeight());
        int x = (getWidth() - mContext.mBoardImage.getWidth()) >> 1;
        int y = (getHeight() - mContext.mBoardImage.getHeight()) >> 1;
        g.drawImage(mContext.mBoardImage, x, y, Graphics.LEFT | Graphics.TOP);
        x += 1;
        y += 1;
        for (int i = 0; i < BOARD_HEIGHT; i++)
            for (int j = 0; j < BOARD_WIDTH; j++)
            {
                if (mBoard[i][j] > -1)
                {
                    if (mBoard[i][j] >= 10)
                    {
                        if (j != mSelectedX || i != mSelectedY || mIsIndicatorUp)
                            g.drawImage(mBlackPieces[mBoard[i][j] - 10], x + j * 19, y + i * 19, Graphics.HCENTER | Graphics.VCENTER);                                                    
                    }
                    else
                    {
                        if (j != mSelectedX || i != mSelectedY || mIsIndicatorUp)
                            g.drawImage(mRedPieces[mBoard[i][j]], x + j * 19, y + i * 19, Graphics.HCENTER | Graphics.VCENTER);
                    }
                }
                
                if (i == mSelectingY && j == mSelectingX)
                {
                    g.drawImage(mContext.mArrowDown, 
                            x + j * 19, 
                            y + i * 19 - (mIsIndicatorUp ? 2 : 0), Graphics.HCENTER | Graphics.VCENTER);
                    mIsIndicatorUp = !mIsIndicatorUp;
                }
            }
        if (mIsDisplayMenu)
        {
            mContextMenu.paint(g, getWidth(), getHeight(), Graphics.RIGHT | Graphics.BOTTOM);
        }
    }
    
    public void doMove(int srcX, int srcY, int dstX, int dstY)
    {
        mBoard[dstY][dstX] = mBoard[srcY][srcX];
        mBoard[srcY][srcX] = -1;
    }
    
    public void keyPressed(int keyCode)
    {
        if (!mIsDisplayMenu)
        {
            switch (keyCode)
            {
                case Key.UP:      
                    if (mSelectingY > 0)
                        mSelectingY--;
                    break;
                case Key.DOWN:
                    if (mSelectingY < BOARD_HEIGHT - 1)
                        mSelectingY++;
                    break;
                case Key.LEFT:
                    if (mSelectingX > 0)
                        mSelectingX--;
                    break;
                case Key.RIGHT:
                    if (mSelectingX < BOARD_WIDTH - 1)
                        mSelectingX++;
                    break;
                case Key.SELECT:
                    if (mSelectedX == -1)
                    {
                        mSelectedX = mSelectingX;
                        mSelectedY = mSelectingY;
                    }
                    else
                    {
                        doMove(mSelectedX, mSelectedY, mSelectingX, mSelectingY);
                        mSelectedX = -1;
                        mSelectedY = -1;
                    }
                    break;
                case Key.SOFT_RIGHT:
                    mIsDisplayMenu = true;
                    break;
            }
        }
        else
        {
            switch (keyCode)
            {
                case Key.UP:      
                    mContextMenu.onDirectionKeys(0);
                    break;
                case Key.DOWN:
                    mContextMenu.onDirectionKeys(1);
                    break;
                case Key.LEFT:
                    mIsDisplayMenu = false;
                    break;
                case Key.RIGHT:
                    mIsDisplayMenu = false;
                    break;
                case Key.SELECT:
                    switch (mContextMenu.selectedItem())
                    {
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                    }
                    break;
                case Key.SOFT_RIGHT:
                    mIsDisplayMenu = false;
                    break;
            }
        }
    }
}
