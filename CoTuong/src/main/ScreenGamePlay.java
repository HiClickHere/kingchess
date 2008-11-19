/*
 * To change this template, choose Tools | Templates
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
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import ui.FastDialog;
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
    
    public boolean mIsOnlinePlay;
    
    ContextMenu mContextMenu;
    
    public boolean mIsDisplayMenu;
    
    protected Vector mDialogVector;
    protected int mState;

    public void addDialog(String aString, int leftSoft, int rightSoft, int state) {
        mDialogVector.addElement(new DialogRecord(aString, leftSoft, -1, rightSoft, state));
    }

    public void removeAllDialog() {
        mDialogVector.removeAllElements();
    }

    public void dismissDialog() {
        mIsDisplayDialog = false;
        setSoftKey(-1, -1, -1);
    }
    
    public final static int BUTTON_CONTINUE = 0;
    public final static int BUTTON_SEND_MESSAGE = 1;
    public final static int BUTTON_REQUEST_DRAW_GAME = 2;
    public final static int BUTTON_LEFT_GAME = 3;
    public final static int BUTTON_QUIT = 4;

    public ScreenGamePlay(Context aContext)
    {
        super(aContext);
        mBoard = new int[BOARD_HEIGHT][BOARD_WIDTH];
        mBoardImage = mContext.mBoardImage;
        mRedPieces = mContext.mRedPieces;
        mBlackPieces = mContext.mBlackPieces;
        
        mContextMenu = new ContextMenu(mContext.mTahomaFontGreen, mContext.mTahomaOutlineGreen);        
        mContextMenu.setColors(0x5f7a7a, 0x708585);
        mContextMenu.addItem(BUTTON_CONTINUE, "Tiếp tục", false,
                            false,
                            20,
                            90,
                            getWidth() >> 1,
                            -1,
                            Graphics.HCENTER | Graphics.VCENTER);
        if (mContext.mIsOnlinePlay)
        {
            mContextMenu.addItem(BUTTON_SEND_MESSAGE, "Gửi tin nhắn", false,
                                false,
                                20,
                                90,
                                getWidth() >> 1,
                                -1,
                                Graphics.HCENTER | Graphics.VCENTER);
            mContextMenu.addItem(BUTTON_REQUEST_DRAW_GAME, "Xin hòa", false, 
                                false, 
                                20, 
                                90, 
                                getWidth() >> 1, 
                                -1, 
                                Graphics.HCENTER | Graphics.VCENTER);
        }
        mContextMenu.addItem(BUTTON_LEFT_GAME, "Thoát ra", false,
                            false,
                            20,
                            90,
                            getWidth() >> 1,
                            -1,
                            Graphics.HCENTER | Graphics.VCENTER);
        mIsOnlinePlay = mContext.mIsOnlinePlay;
        mDialog = new FastDialog(getWidth() - 40, getHeight() - 80);
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
        mDialogVector = new Vector();
        mIsDisplayDialog = false;             
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
    
    protected long mLastUpdateTheGame;
    public final static long GAME_UPDATE_CYCLE = 5000;
    
    public void onTick(long aMilliseconds)
    {
        //repaint();
        //serviceRepaints();
        if (mContext.mIsOnlinePlay)
        {
            //if (!mContext.mIsMyTurn)
            {
                if (System.currentTimeMillis() - mLastUpdateTheGame  > GAME_UPDATE_CYCLE)
                {
                    try {
                        ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                        ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                        aOutput.writeString16(new String16(mContext.mUsername));
                        //aOutput.writeString16(new String16(mContext.mOpponentName));
                        mContext.mNetwork.sendMessage(Protocol.REQUEST_UPDATE_MY_GAME, aByteArray.toByteArray());                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mLastUpdateTheGame = System.currentTimeMillis();                  
                }                    
            }
            
            if (!mDialogVector.isEmpty() && !mIsDisplayDialog) 
            {
                DialogRecord aDialog = (DialogRecord) mDialogVector.elementAt(0);
                mDialogVector.removeElementAt(0);
                mDialog.setText(aDialog.mMessage);
                setSoftKey(aDialog.mLeftSoftkey, -1, aDialog.mRightSoftkey);
                mState = aDialog.mState;
                mIsDisplayDialog = true;
            }
        }
    }
    
    public final static int STATE_LOSE_CONNECTION = 1;
    public final static int STATE_MOVE_FAIL = 2;
    public final static int STATE_ASK_FOR_DRAW_GAME = 3;
    public final static int STATE_ASK_FOR_LEAVE_GAME = 4;
    public final static int STATE_END_THE_GAME = 5;
    public final static int STATE_MESSAGE = 6;
    
    public boolean onEvent(Event event) {
        switch (event.mType) {
            
            case Network.EVENT_NETWORK_FAILURE:
                return true;

            case Network.EVENT_END_COMMUNICATION:
                ByteArrayInputStream aByteArray = new ByteArrayInputStream(event.mData);
                ChessDataInputStream in = new ChessDataInputStream(aByteArray);
                try {
                    while (in.available() > 0) {
                        short returnType = in.readShort();
                        int size = in.readInt();
                        switch (returnType)
                        {
                            case Protocol.RESPONSE_NEW_MOVES:
                                int xsrc = in.readInt();
                                int ysrc = in.readInt();
                                int xdst = in.readInt();
                                int ydst = in.readInt();
                                if (!mContext.mIsMyTurn)
                                {
                                    doMove(xsrc, ysrc, xdst, ydst);
                                    mContext.mIsMyTurn = true;
                                }
                                break;
                            case Protocol.RESPONSE_PLEASE_DO_A_MOVE:
                                break;
                            case Protocol.RESPONSE_I_DID_A_MOVE_SUCCESSFULLY:
                                mContext.mIsMyTurn = false;
                                break;
                            case Protocol.RESPONSE_I_DID_A_MOVE_FAILURE:
                                //removeAllDialog();
                                //dismissDialog();
                                addDialog("Lỗi: Xin bạn vui lòng thực hiện lại nước đi.",
                                        -1,
                                        SOFTKEY_OK,
                                        STATE_MOVE_FAIL);
                                mContext.mIsMyTurn = true;
                                break;
                            case Protocol.RESPONSE_YOU_WIN_THE_GAME:
                                //removeAllDialog();
                                //dismissDialog();
                                addDialog("Chúc mừng! Đối thủ đã hết cờ, bạn là người chiến thắng.",
                                        -1,
                                        SOFTKEY_OK,
                                        STATE_END_THE_GAME);  
                                mContextMenu = null;
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
                                mContext.mMatchResult = Context.RESULT_WIN;
                                break;
                            case Protocol.RESPONSE_NEW_MESSAGES:              
                                int numberOfMessage = in.readInt();
                                for (int i = 0; i < numberOfMessage; i++)
                                {
                                    addDialog("Tin nhắn từ " + in.readString16().toJavaString() + " : " + in.readString16().toJavaString(), -1, SOFTKEY_OK, STATE_MESSAGE);
                                }
                                break;
                            case Protocol.RESPONSE_I_HAVE_NO_MOVE_SUCCESSFULLY:
                                //removeAllDialog();
                                //dismissDialog();
                                addDialog("Bạn đã thua...",
                                        -1,
                                        SOFTKEY_OK,
                                        STATE_END_THE_GAME);                
                                mContextMenu = null;
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
                                mContext.mMatchResult = Context.RESULT_LOSE;                                
                                break;                            
                            default:
                                in.skip(size);
                                break;
                        }                        
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case Network.EVENT_RECEIVING:
            case Network.EVENT_SENDING:
                return true;
            case Network.EVENT_SETUP_CONNECTION:
                return true;
            case Network.EVENT_LOSE_CONNECTION:                                
                mContext.mIsLoggedIn = false;
                removeAllDialog();
                dismissDialog();
                mContext.mIsLoggedIn = false;
                mContext.mOpponentName = "";
                addDialog("Lỗi: Mất liên lạc với máy chủ.",
                        -1,
                        SOFTKEY_OK,
                        STATE_LOSE_CONNECTION);
                return true;
            case Network.EVENT_TEXTBOX_FOCUS:
                return true;
            case Network.EVENT_TEXTBOX_INFOCUS:
                try {
                    ByteArrayOutputStream aByteOutputArray = new ByteArrayOutputStream();
                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteOutputArray);
                    aOutput.writeString16(new String16(mContext.mUsername));
                    aOutput.writeString16(new String16(mContext.mOpponentName));
                    aOutput.writeString16(new String16(mContext.mInputScreen.mTextBox.getString()));
                    mContext.mNetwork.sendMessage(Protocol.REQUEST_SEND_MESSAGE, aByteOutputArray.toByteArray());                        
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mIsDisplayMenu = false;
                return true;
        }
        return false;
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
        
        if (mIsDisplayDialog)
        {
            mDialog.paint(g);
            drawSoftkey(g);
            return;
        }
        
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
                
                if (i == mSelectingY && j == mSelectingX && mContext.mIsMyTurn)
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
        
        if (mContext.mIsMyTurn && mContext.mIsOnlinePlay)
        {
            try {
                ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                aOutput.writeString16(new String16(mContext.mUsername));
                aOutput.writeString16(new String16(mContext.mOpponentName));
                aOutput.writeInt(srcX);
                aOutput.writeInt(srcY);
                aOutput.writeInt(dstX);
                aOutput.writeInt(dstY);
                mContext.mNetwork.sendMessage(Protocol.REQUEST_I_DID_A_MOVE, aByteArray.toByteArray());
                mContext.mIsMyTurn = false;
            } catch (Exception e) {
                e.printStackTrace();
            }            
        }
    }
    
    public void keyPressed(int keyCode)
    {
        if (mIsDisplayDialog)
        {
            switch (keyCode)
            {
                case Key.UP:                    
                case Key.DOWN:
                    mDialog.onKeyPressed(keyCode);
                    break;
                case Key.SOFT_LEFT:
                    switch (mState)
                    {
                        case STATE_ASK_FOR_LEAVE_GAME:
                            if (mLeftSoftkey == SOFTKEY_CANCEL)
                                dismissDialog();
                            break;
                        case STATE_ASK_FOR_DRAW_GAME:
                            if (mLeftSoftkey == SOFTKEY_CANCEL)
                                dismissDialog();
                            break;                        
                    }
                    break;
                case Key.SELECT:
                case Key.SOFT_RIGHT:
                    switch (mState)
                    {
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
                        case STATE_ASK_FOR_DRAW_GAME:
                            if (mRightSoftkey == SOFTKEY_OK)
                            {                                
                                try {
                                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                                    aOutput.writeString16(new String16(mContext.mUsername));
                                    aOutput.writeString16(new String16(mContext.mOpponentName));
                                    mContext.mNetwork.sendMessage(Protocol.REQUEST_I_HAVE_NO_MOVE, aByteArray.toByteArray());                        
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dismissDialog();
                            }
                            break;
                        case STATE_ASK_FOR_LEAVE_GAME:
                            if (mRightSoftkey == SOFTKEY_OK)
                            {
                                ScreenOnlinePlay screenOnline = new ScreenOnlinePlay(mContext);
                                mContext.setScreen(screenOnline);
                            }
                            break;
                        case STATE_END_THE_GAME:
                        case STATE_MESSAGE:
                            if (mRightSoftkey == SOFTKEY_OK)
                            {
                                dismissDialog();
                            }
                            break;                        
                    }
                    break;
            }
        }
        else
        {
            if (!mIsDisplayMenu)
            {
                if (mContext.mIsMyTurn)
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
                        case Key.SOFT_RIGHT:
                            mIsDisplayMenu = true;
                            break;
                    }
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
                            case BUTTON_CONTINUE:
                                mIsDisplayMenu = false;
                                break;
                            case BUTTON_SEND_MESSAGE:
                                mContext.mInputScreen.mTextBox.setLabel("Tin nhan");
                                mContext.mInputScreen.mTextBox.setString("");
                                mContext.mInputScreen.mTextBox.setConstraints(TextField.ANY);
                                mContext.mInputScreen.mTextBox.setMaxSize(256);
                                mContext.setDisplayTextBox();
                                break;
                            case BUTTON_REQUEST_DRAW_GAME:
                                addDialog("Bạn muốn thỏa thuận hòa ván cờ này?",
                                            SOFTKEY_CANCEL,
                                            SOFTKEY_OK,
                                            STATE_ASK_FOR_DRAW_GAME);
                                break;
                            case BUTTON_LEFT_GAME:
                                if (!mContext.mIsOnlinePlay)
                                {
                                    addDialog("Bạn có muốn thoát không?",
                                            SOFTKEY_CANCEL,
                                            SOFTKEY_OK,
                                            STATE_ASK_FOR_LEAVE_GAME);
                                }
                                else
                                {
                                    addDialog("Thoát khỏi ván chơi bạn sẽ bị xử thua. Bạn có muốn thoát không? ",
                                            SOFTKEY_CANCEL,
                                            SOFTKEY_OK,
                                            STATE_ASK_FOR_LEAVE_GAME);
                                }
                                break;
                            case BUTTON_QUIT:
                                ScreenEndGame screenEnd = new ScreenEndGame(mContext);
                                mContext.setScreen(screenEnd);
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
}
