/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import ChessBoard.ChessConst;
import ChessBoard.Chessboard;
import ChessBoard.Piece;
import ChessBoard.Position;
import core.ChessDataInputStream;
import core.ChessDataOutputStream;
import core.Event;
import core.Screen;
import core.String16;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import ui.FastDialog;
import ui.ContextMenu;
import ui.SoundManager;
import util.Key;

/**
 *
 * @author dong
 */
public class ScreenOfflineGamePlay extends ScreenGamePlay {

    public final static int BUTTON_CONTINUE = 0;
    public final static int BUTTON_SEND_MESSAGE = 1;
    public final static int BUTTON_REQUEST_DRAW_GAME = 2;
    public final static int BUTTON_LEFT_GAME = 3;
    public final static int BUTTON_QUIT = 4;
    public final static int BUTTON_SOUND = 5;
    public final static int BUTTON_HINT = 6;
    public final static int BUTTON_SAVE = 7;
    
    public final static int NOTIFY_NEXT_MOVE = 0;
    public final static int NOTIFY_PLAYER_NAME = 1;
    public final static int NOTIFY_WRONG_MOVE = 2;
    public final static int NOTIFY_CHECK = 3;
    
    OfflineThinkingServer mServer;

    public ScreenOfflineGamePlay(Context aContext) {
        super(aContext);        
        mHintOn = true;
        
        mServer = new OfflineThinkingServer(this, mContext.mOfflineLevel, !(mContext.mOfflineColor == 0));
        
        mContextMenu = new ContextMenu(mContext.mTahomaFontGreen, mContext.mTahomaOutlineGreen);
        mContextMenu.setColors(0x5f7a7a, 0x708585);
        mContextMenu.addItem(BUTTON_CONTINUE, "Tiếp tục", false,
                false,
                20,
                getWidth() - 40,
                getWidth() >> 1,
                -1,
                Graphics.HCENTER | Graphics.VCENTER);
        String soundStr = "Âm thanh: BẬT";
        if (!mContext.mSoundManager.isActive()) {
            soundStr = "Âm thanh: TẮT";
        }
        mContextMenu.addItem(BUTTON_SOUND, soundStr, false,
                false,
                20,
                getWidth() - 40,
                getWidth() >> 1,
                -1,
                Graphics.HCENTER | Graphics.VCENTER);
        String hintStr = "Gợi ý: TẮT";
        if (mHintOn)
           hintStr = "Gợi ý: BẬT";
        mContextMenu.addItem(BUTTON_HINT, 
                hintStr, 
                false, 
                false, 
                20, 
                getWidth() - 40, 
                getWidth() >> 1, 
                -1, 
                Graphics.HCENTER | Graphics.VCENTER);
        mContextMenu.addItem(BUTTON_LEFT_GAME, "Thoát ra", false,
                false,
                20,
                getWidth() - 40,
                getWidth() >> 1,
                -1,
                Graphics.HCENTER | Graphics.VCENTER);
        mDialog = new FastDialog(getWidth() - 40, getHeight() - 80);
    }

    public void onActivate() {
        super.onActivate();
        isEndGame = false;        
        mOppXsrc = -1;
        mOppYsrc = -1;
        mOppXdst = -1;
        mOppYdst = -1;
        mContext.mSoundManager.stop();
        mContext.mUsername = "Người";
        mContext.mOpponentName = "Máy";
        mContext.mIsMyTurn = mContext.mOfflineColor == 0;        
        mServer.start();
    }

    public void onDeactivate() {
    }
    protected long mLastUpdateTheGame;
//#ifdef SOCKET_SERVER
    public final static long GAME_UPDATE_CYCLE = 10000;
//#else
//#     public final static long GAME_UPDATE_CYCLE = 5000;
//#endif

    public void onTick(long aMilliseconds) {
        super.onTick(aMilliseconds);        
        
        if (mDisplayInfoTick > 0)
            mDisplayInfoTick -= aMilliseconds;
    }
    
    public final static int STATE_LOSE_CONNECTION = 1;
    public final static int STATE_MOVE_FAIL = 2;
    public final static int STATE_ASK_FOR_DRAW_GAME = 3;
    public final static int STATE_ASK_FOR_LEAVE_GAME = 4;
    public final static int STATE_END_THE_GAME = 5;
    public final static int STATE_MESSAGE = 6;
    public final static int STATE_DRAW_REQUEST = 7;
    public final static int STATE_CONNECTING = 8;    
    
    public boolean mIsIndicatorUp;

    public void paint(Graphics g) {
        super.paint(g);

        if (!mContext.mIsMyTurn && !isEndGame) {
            g.setColor(0);
            int w = mContext.mTahomaOutlineGreen.getWidth("Đang đợi...");
            g.fillRect(getWidth() - w, 0, w, mContext.mTahomaOutlineGreen.getHeight());
            mContext.mTahomaOutlineGreen.write(g, "Đang đợi...", getWidth(), 0, Graphics.RIGHT | Graphics.TOP);
        }                
        
        if (mDisplayInfoTick > 0)
        {       
            switch (mInfoCase)
            {
                case NOTIFY_PLAYER_NAME:
                    g.drawImage(mContext.mBarTopImg, 0, 0, Graphics.TOP | Graphics.LEFT);
                    mContext.mTahomaOutlineGreen.write(g, 
                            mContext.mOpponentName, getWidth() >> 1, 
                            mContext.mBarTopImg.getHeight() >> 1, 
                            Graphics.HCENTER | Graphics.VCENTER);
                    g.drawImage(mContext.mBarBottomImg, 0, getHeight(), Graphics.BOTTOM | Graphics.LEFT);
                    mContext.mTahomaOutlineGreen.write(g, 
                            mContext.mUsername, getWidth() >> 1, 
                            getHeight() - (mContext.mBarBottomImg.getHeight() >> 1), 
                            Graphics.HCENTER | Graphics.VCENTER);
                    break;
                case NOTIFY_NEXT_MOVE:
                    if (mContext.mIsMyTurn)
                    {
                        g.drawImage(mContext.mBarBottomImg, 0, getHeight(), Graphics.BOTTOM | Graphics.LEFT);
                            mContext.mTahomaOutlineGreen.write(g, 
                            mContext.mUsername + " - Nước: " + (mChessBoard.moveIdx + 1), getWidth() >> 1, 
                            getHeight() - (mContext.mBarBottomImg.getHeight() >> 1), 
                            Graphics.HCENTER | Graphics.VCENTER);
                    }
                    else
                    {
                        g.drawImage(mContext.mBarTopImg, 0, 0, Graphics.TOP | Graphics.LEFT);
                            mContext.mTahomaOutlineGreen.write(g, 
                            mContext.mOpponentName + " - Nước: " + (mChessBoard.moveIdx + 1), getWidth() >> 1, 
                            mContext.mBarTopImg.getHeight() >> 1, 
                            Graphics.HCENTER | Graphics.VCENTER);
                    }
                    break;
                case NOTIFY_CHECK:
                    if (mChessBoard.isEnd(mChessBoard.mUserColor) && mChessBoard.currMove)
                    {
                        g.drawImage(mContext.mBarBottomImg, 0, getHeight(), Graphics.BOTTOM | Graphics.LEFT);
                            mContext.mTahomaOutlineGreen.write(g, 
                            "Bạn đã hết cờ.", getWidth() >> 1, 
                            getHeight() - (mContext.mBarBottomImg.getHeight() >> 1), 
                            Graphics.HCENTER | Graphics.VCENTER);
                        mServer.stop();
                    }
                    else if (mChessBoard.isEnd(1 - mChessBoard.mUserColor) && !mChessBoard.currMove)
                    {
                        g.drawImage(mContext.mBarTopImg, 0, 0, Graphics.TOP | Graphics.LEFT);
                            mContext.mTahomaOutlineGreen.write(g, 
                            "Hết cờ!", getWidth() >> 1, 
                            mContext.mBarTopImg.getHeight() >> 1, 
                            Graphics.HCENTER | Graphics.VCENTER);
                        mServer.stop();
                    }
                    else if (mChessBoard.isCheckMate(mChessBoard.mUserColor))
                    {
                        g.drawImage(mContext.mBarBottomImg, 0, getHeight(), Graphics.BOTTOM | Graphics.LEFT);
                            mContext.mTahomaOutlineGreen.write(g, 
                            "Bạn đang bị chiếu tướng!", getWidth() >> 1, 
                            getHeight() - (mContext.mBarBottomImg.getHeight() >> 1), 
                            Graphics.HCENTER | Graphics.VCENTER);
                    }
                    else if (mChessBoard.isCheckMate(1 - mChessBoard.mUserColor))
                    {
                        g.drawImage(mContext.mBarTopImg, 0, 0, Graphics.TOP | Graphics.LEFT);
                            mContext.mTahomaOutlineGreen.write(g, 
                            "Chiếu tướng!", getWidth() >> 1, 
                            mContext.mBarTopImg.getHeight() >> 1, 
                            Graphics.HCENTER | Graphics.VCENTER);
                    }
                    break;
                case NOTIFY_WRONG_MOVE:
                    g.drawImage(mContext.mBarBottomImg, 0, getHeight(), Graphics.BOTTOM | Graphics.LEFT);
                            mContext.mTahomaOutlineGreen.write(g, 
                            "Nước đi không hợp lệ.", getWidth() >> 1, 
                            getHeight() - (mContext.mBarBottomImg.getHeight() >> 1), 
                            Graphics.HCENTER | Graphics.VCENTER);
                    break;
            }            
        }
    }
    
    public void doOppMove(int srcX, int srcY, int dstX, int dstY)
    {
        System.out.println("OppMove " + srcX + " " + srcY + " " + dstX + " " + dstY);
        mOppXsrc = srcX;
        mOppYsrc = srcY;
        mOppXdst = dstX;
        mOppYdst = dstY;                
        
        int srcSquare = Position.ROTATE(Position.TO_SQ(srcX, srcY));
        int dstSquare = Position.ROTATE(Position.TO_SQ(dstX, dstY));
        
        if (mChessBoard.mUserColor == Chessboard.COLOR_DARK)
        {
            srcSquare = Position.ROTATE(srcSquare);
            dstSquare = Position.ROTATE(dstSquare); 
            
            mOppXsrc = ChessConst.BOARD_WIDTH - srcX - 1;
            mOppYsrc = ChessConst.BOARD_HEIGHT - srcY - 1;
            mOppXdst = ChessConst.BOARD_WIDTH - dstX - 1;
            mOppYdst = ChessConst.BOARD_HEIGHT - dstY - 1;  
        }
        
        if (mChessBoard.tryMove(srcSquare, dstSquare) && !mChessBoard.mIsEndGame) {
            mContext.mIsMyTurn = true; //Dong: fix bug not a legal move for sometimes                                                                                                                        
//                                        mTurnDisplayTick = 1000;                                        

            showNotify(NOTIFY_NEXT_MOVE);

            mContext.mSoundManager.play(SoundManager.SOUND_MOVE);

            if (mChessBoard.moveIdx > 0 && mChessBoard.log[mChessBoard.moveIdx - 1][2] != -1) // a capture
            {
                mContext.mSoundManager.pushSound(SoundManager.SOUND_EAT, false);
            }

            if (mChessBoard.isCheckMate(mChessBoard.mUserColor)) {
                showNotify(NOTIFY_CHECK);
                mContext.mSoundManager.pushSound(SoundManager.SOUND_CHECK, false);
            }

            int result = mChessBoard.checkForEndGame();

            if (result == Chessboard.RESULT_DRAW) {
                System.out.println("draw");
                mServer.stop();
            } else if (result == Chessboard.RESULT_LOSE) {
                System.out.println("lose");
                mServer.stop();
            }
        }
    }

    public void doMove(int srcX, int srcY, int dstX, int dstY) {
        System.out.println("Do Move " + mContext.mIsMyTurn);
        if (mContext.mIsMyTurn) {            
            try {                                
                
                if (mChessBoard.mUserColor == Chessboard.COLOR_RED)
                    mServer.doMove(srcX, srcY, dstX, dstY);
                else
                    mServer.doMove(
                            ChessConst.BOARD_WIDTH - srcX - 1, 
                            ChessConst.BOARD_HEIGHT - srcY - 1, 
                            ChessConst.BOARD_WIDTH - dstX - 1, 
                            ChessConst.BOARD_HEIGHT - dstY - 1);
                
                mContext.mIsMyTurn = false;
                showNotify(NOTIFY_NEXT_MOVE);                
                mContext.mSoundManager.play(SoundManager.SOUND_MOVE);                
                if (mChessBoard.moveIdx > 0 && mChessBoard.log[mChessBoard.moveIdx - 1][2] != -1) // a capture
                {
                    mContext.mSoundManager.pushSound(SoundManager.SOUND_EAT, false);
                }                
                if (mChessBoard.isCheckMate(1 - mChessBoard.mUserColor)) {
                    showNotify(NOTIFY_CHECK);
                    mContext.mSoundManager.pushSound(SoundManager.SOUND_CHECK, false);
                }                
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (isEndGame) 
            {
            }
        }
    }        
    
    public void doClick(int x, int y) {
        if (mSelectedPiece != null) {
            mMyXsrc = Position.POS_X(mSelectedPiece.getLocation());
            mMyYsrc = Position.POS_Y(mSelectedPiece.getLocation());

            mMyXdst = x;
            mMyYdst = y;
        }

        if (mSelectedPiece != null &&
                mChessBoard.tryMove(mSelectedPiece.getLocation(),
                Position.TO_SQ(x, y))) {
            mSelectedPiece = null;            
            doMove(mMyXsrc, mMyYsrc, mMyXdst, mMyYdst);                                    
        } else if (mChessBoard.canSelect(x, y)) {
            mSelectedPiece = mChessBoard.getPieceAt(x, y);
            
            //Get all posible move to display hint
            mPosibleMove = mSelectedPiece.getAvailableMove(mChessBoard);
                for (int i = 0; i < mPosibleMove.length; i++)
                {
                    int m = mPosibleMove[i];
                    if (m != -1 && mChessBoard.isValidMove(mSelectedPiece.getLocation(), m))
                    {                        
                    }
                    else
                        mPosibleMove[i] = -1;
                }
        } else if (mSelectedPiece != null)
        {
            showNotify(NOTIFY_WRONG_MOVE);
            mContext.mSoundManager.play(SoundManager.SOUND_ILLEGAL);
        }
    }
    private int mNumOfInfo;

    public void keyPressed(int keyCode) {
        if (mIsDisplayDialog) {
            switch (keyCode) {
                case Key.UP:
                case Key.NUM_2:
                case Key.DOWN:
                case Key.NUM_8:
                    mDialog.onKeyPressed(keyCode);
                    break;
                case Key.SOFT_LEFT:
                    switch (mState) {
                        case STATE_ASK_FOR_LEAVE_GAME:
                            if (mLeftSoftkey == SOFTKEY_CANCEL) {
                                dismissDialog();
                                mIsDisplayMenu = false;
                            }
                            break;
                    }
                    break;
                case Key.SELECT:
                case Key.NUM_5:
                case Key.SOFT_RIGHT:
                    switch (mState) {
                        case STATE_LOSE_CONNECTION:
                            if (mRightSoftkey == SOFTKEY_OK) {
                                dismissDialog();
                                ScreenMainMenu aMainMenu = new ScreenMainMenu(mContext);
                                aMainMenu.setMenu(ScreenMainMenu.MENU_MAIN);
                                mContext.setScreen(aMainMenu);
                            }
                            break;
                        case STATE_MOVE_FAIL:
                            if (mRightSoftkey == SOFTKEY_OK) {
                                dismissDialog();
                            }
                            break;
                        case STATE_ASK_FOR_LEAVE_GAME:
                            if (mRightSoftkey == SOFTKEY_OK) {                                
                                ScreenOfflinePlay screenOffline = new ScreenOfflinePlay(mContext);
                                mContext.setScreen(screenOffline);
                            }
                            break;

                        case STATE_END_THE_GAME:
                        case STATE_MESSAGE:
                            if (mRightSoftkey == SOFTKEY_OK) {
                                dismissDialog();
                            }
                            break;
                    }
                    break;
            }
        } else {
            if (!mIsDisplayMenu) {
                int deltaX = 0;
                int deltaY = 0;
                switch (keyCode) {
                    case Key.SELECT:
                    case Key.NUM_5:
                        System.out.println("" + mChessBoard.currMove + " " + mChessBoard.mIsEndGame);
                        if (mChessBoard.currMove && !mChessBoard.mIsEndGame) 
                        { 
                            doClick(cursorX, cursorY);
                        }
                        break;
                    case Key.UP:
                    case Key.NUM_2:
                        deltaY = -1;
                        break;
                    case Key.LEFT:
                    case Key.NUM_4:
                        deltaX = -1;
                        break;
                    case Key.RIGHT:
                    case Key.NUM_6:
                        deltaX = 1;
                        break;
                    case Key.DOWN:
                    case Key.NUM_8:
                        deltaY = 1;
                        break;
                    case Key.SOFT_RIGHT:
                        mDisplayInfoTick = 0;
                        mIsDisplayMenu = true;
                        mContextMenu.setSelection(0);
                        break;
                    case Key.SOFT_LEFT:
                        mDisplayInfoTick = 5000;
                        break;
                }
                cursorX = (cursorX + deltaX + 9) % 9;
                cursorY = (cursorY + deltaY + 10) % 10;
            } else {
                switch (keyCode) {
                    case Key.UP:
                    case Key.NUM_2:
                        mContextMenu.onDirectionKeys(0);
                        break;
                    case Key.DOWN:
                    case Key.NUM_8:
                        mContextMenu.onDirectionKeys(1);
                        break;
                    case Key.LEFT:
                    case Key.NUM_4:
                        mIsDisplayMenu = false;
                        break;
                    case Key.RIGHT:
                    case Key.NUM_6:
                        mIsDisplayMenu = false;
                        break;
                    case Key.SELECT:
                    case Key.NUM_5:
                        switch (mContextMenu.selectedItem()) {
                            case BUTTON_CONTINUE:
                                mIsDisplayMenu = false;
                                break;
                            case BUTTON_LEFT_GAME:
                                addDialog("Bạn có muốn thoát không?",
                                        SOFTKEY_CANCEL,
                                        SOFTKEY_OK,
                                        STATE_ASK_FOR_LEAVE_GAME);                                
                                break;
                            case BUTTON_SOUND:
                                mContext.mSoundManager.setActive(!mContext.mSoundManager.isActive());
                                String soundStr = "Âm thanh: BẬT";
                                if (!mContext.mSoundManager.isActive()) {
                                    soundStr = "Âm thanh: TẮT";
                                }
                                mContextMenu.getItem(BUTTON_SOUND).mCaption = soundStr;
                                break;
                            case BUTTON_HINT:
                                mHintOn = !mHintOn;
                                String hintStr = "Gợi ý: BẬT";
                                if (!mHintOn) {
                                    hintStr = "Gợi ý: TẮT";
                                }
                                mContextMenu.getItem(BUTTON_HINT).mCaption = hintStr;
                                break;
                            case BUTTON_SAVE:
                                mContext.saveNewGame(mContext.mUsername, mContext.mOpponentName, mChessBoard, (mChessBoard.mUserColor == 0));                                
                                addDialog("Đã lưu xong.", -1, SOFTKEY_OK, STATE_MESSAGE);                                
                                break;
                        }
                        mIsDisplayMenu = false;
                        break;
                    case Key.SOFT_RIGHT:
                        mIsDisplayMenu = false;
                        break;
                }
            }
        }
    }
}
