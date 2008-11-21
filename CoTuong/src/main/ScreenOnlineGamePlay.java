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
import ui.ContextMenu;
import util.Key;
import xqwlight.Position;

/**
 *
 * @author dong
 */
public class ScreenOnlineGamePlay extends ScreenGamePlay {

    public final static int BUTTON_CONTINUE = 0;
    public final static int BUTTON_SEND_MESSAGE = 1;
    public final static int BUTTON_REQUEST_DRAW_GAME = 2;
    public final static int BUTTON_LEFT_GAME = 3;
    public final static int BUTTON_QUIT = 4;

    public ScreenOnlineGamePlay(Context aContext) {
        super(aContext);

        mContextMenu = new ContextMenu(mContext.mTahomaFontGreen, mContext.mTahomaOutlineGreen);
        mContextMenu.setColors(0x5f7a7a, 0x708585);
        mContextMenu.addItem(BUTTON_CONTINUE, "Tiếp tục", false,
                false,
                20,
                90,
                getWidth() >> 1,
                -1,
                Graphics.HCENTER | Graphics.VCENTER);
        if (mContext.mIsOnlinePlay && mContext.mIsMyTurn) {
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
        mDialog = new FastDialog(getWidth() - 40, getHeight() - 80);
    }

    public void onActivate() {
        super.onActivate();
    }

    public void onDeactivate() {
    }
    protected long mLastUpdateTheGame;
    public final static long GAME_UPDATE_CYCLE = 10000;

    public void onTick(long aMilliseconds) {
        super.onTick(aMilliseconds);

        if (mContext.mIsOnlinePlay) {
            if (System.currentTimeMillis() - mLastUpdateTheGame > GAME_UPDATE_CYCLE) {
                try {
                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                    aOutput.writeString16(new String16(mContext.mUsername));
                    mContext.mNetwork.sendMessage(Protocol.REQUEST_UPDATE_MY_GAME, aByteArray.toByteArray());                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mLastUpdateTheGame = System.currentTimeMillis();
            }
        }
    }
    public final static int STATE_LOSE_CONNECTION = 1;
    public final static int STATE_MOVE_FAIL = 2;
    public final static int STATE_ASK_FOR_DRAW_GAME = 3;
    public final static int STATE_ASK_FOR_LEAVE_GAME = 4;
    public final static int STATE_END_THE_GAME = 5;
    public final static int STATE_MESSAGE = 6;
    public final static int STATE_DRAW_REQUEST = 7;

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
                        switch (returnType) {
                            case Protocol.RESPONSE_NEW_MOVES:
                                int xsrc = in.readInt();
                                int ysrc = in.readInt();
                                int xdst = in.readInt();
                                int ydst = in.readInt();
                                String move_player_name = in.readString16().toJavaString();
                                if (!mContext.mIsMyTurn && mContext.mOpponentName.equals(move_player_name)) {
                                    mOppXsrc = xsrc;
                                    mOppYsrc = ysrc;
                                    mOppXdst = xdst;
                                    mOppYdst = ydst;
                                    responseMove();
                                    mContext.mIsMyTurn = true;
                                }
                                break;
                            
                            case Protocol.RESPONSE_STILL_ONLINE_SUCCESSFULLY:                                
                                mContext.mLastReceivedGoodConnect = System.currentTimeMillis();
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
                                for (int i = 0; i < numberOfMessage; i++) {
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

                            case Protocol.RESPONSE_REQUEST_DRAW_GAME:
                                addDialog("Đối thủ của bạn muốn thỏa thuận hòa ván này. Bạn có đồng ý không?",
                                        SOFTKEY_CANCEL, SOFTKEY_OK, STATE_DRAW_REQUEST);
                                break;

                            case Protocol.RESPONSE_THIS_IS_DRAW_GAME:
                                addDialog("Kết thúc. Ván cờ hòa.",
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

    public void paint(Graphics g) {
        super.paint(g);
        
        if (!mContext.mIsMyTurn)
        {
            g.setColor(0);
            int w = mContext.mTahomaOutlineGreen.getWidth("Đang đợi...");
            g.fillRect(getWidth() - w, 0, w, mContext.mTahomaOutlineGreen.getHeight());
            mContext.mTahomaOutlineGreen.write(g, "Đang đợi...", getWidth(), 0, Graphics.RIGHT | Graphics.TOP);
        }
    }

    public void doMove(int srcX, int srcY, int dstX, int dstY) {
           if (mContext.mIsMyTurn && mContext.mIsOnlinePlay) {
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
        } else {
            if (isEndGame) {
//                try {
//                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
//                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
//                    aOutput.writeString16(new String16(mContext.mUsername));
//                    aOutput.writeString16(new String16(mContext.mOpponentName));
//                    mContext.mNetwork.sendMessage(Protocol.REQUEST_I_HAVE_NO_MOVE, aByteArray.toByteArray());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }        
    }
    
    protected void clickSquare() {
        int sq = Position.COORD_XY(cursorX + Position.FILE_LEFT, cursorY + Position.RANK_TOP);
        if (flip) {
            sq = Position.SQUARE_FLIP(sq);
        }
        int pc = pos.squares[sq];
        if ((pc & Position.SIDE_TAG(pos.sdPlayer)) != 0) {
            mvLast = 0;
            sqSelected = sq;
            mMyXsrc = cursorX;
            mMyYsrc = cursorY;
        } else {
            if (sqSelected > 0 && addMove(Position.MOVE(sqSelected, sq))/* && !responseMove()*/) { // online mode don't need CPU
                mMyXdst = cursorX;
                mMyYdst = cursorY;
                
                doMove(mMyXsrc, mMyYsrc, mMyXdst, mMyYdst);
            }
        }
    }
    
    protected int mMyXsrc = 0;
    protected int mMyYsrc = 0;
    protected int mMyXdst = 0;
    protected int mMyYdst = 0;
    
    protected int mOppXsrc = 0;
    protected int mOppYsrc = 0;
    protected int mOppXdst = 0;
    protected int mOppYdst = 0;
    
    protected boolean responseMove() {  
        int sqSel = Position.COORD_XY(mOppXsrc + Position.FILE_LEFT, mOppYsrc + Position.RANK_TOP);
        if (!flip) {
            sqSel = Position.SQUARE_FLIP(sqSel);
        }
        int sqNew = Position.COORD_XY(mOppXdst + Position.FILE_LEFT, mOppYdst + Position.RANK_TOP);
        if (!flip) {
            sqNew = Position.SQUARE_FLIP(sqNew);
        }                
        
        int move = Position.MOVE(sqSel, sqNew);                        
        
        if (!pos.legalMove(move)) {
            System.out.println("not a legal move for opponent player, exit now");
            return false; //not a legal move for opponent player, exit now
        }

        mvLast = move;        
        pos.makeMove(mvLast);

        
        int response = pos.inCheck() ? RESP_CHECK2 : pos.captured() ? RESP_CAPTURE2 : RESP_MOVE2;
        if (pos.captured()) {
            pos.setIrrev();
        }

        boolean result = !getResult(response); 
        
        System.out.println("message: " + message + " " + isEndGame);
        
        if (isEndGame)
        {
            System.out.println("isEndGame!");
            if (message.indexOf("thua") != -1)
            {
                System.out.println("No move!");
                try {
                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                    aOutput.writeString16(new String16(mContext.mUsername));
                    aOutput.writeString16(new String16(mContext.mOpponentName));
                    mContext.mNetwork.sendMessage(Protocol.REQUEST_I_HAVE_NO_MOVE, aByteArray.toByteArray());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }            
        }
        
        return result;
    }

    public void keyPressed(int keyCode) 
    {
        if (mIsDisplayDialog) {
            switch (keyCode) {
                case Key.UP:
                case Key.DOWN:
                    mDialog.onKeyPressed(keyCode);
                    break;
                case Key.SOFT_LEFT:
                    switch (mState) {
                        case STATE_ASK_FOR_LEAVE_GAME:
                            if (mLeftSoftkey == SOFTKEY_CANCEL) {
                                dismissDialog();
                            }
                            break;
                        case STATE_ASK_FOR_DRAW_GAME:
                            if (mLeftSoftkey == SOFTKEY_CANCEL) {
                                dismissDialog();
                            }
                            break;
                        case STATE_DRAW_REQUEST:
                            if (mLeftSoftkey == SOFTKEY_CANCEL) {
                                dismissDialog();
                                try {
                                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                                    aOutput.writeString16(new String16(mContext.mUsername));
                                    aOutput.writeString16(new String16(mContext.mOpponentName));
                                    mContext.mNetwork.sendMessage(Protocol.REQUEST_I_DENY_DRAW_GAME, aByteArray.toByteArray());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                    }
                    break;
                case Key.SELECT:
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
                        case STATE_ASK_FOR_DRAW_GAME:
                            if (mRightSoftkey == SOFTKEY_OK) {
                                try {
                                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                                    aOutput.writeString16(new String16(mContext.mUsername));
                                    aOutput.writeString16(new String16(mContext.mOpponentName));
                                    mContext.mNetwork.sendMessage(Protocol.REQUEST_DRAW_GAME, aByteArray.toByteArray());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dismissDialog();
                            }
                            break;
                        case STATE_ASK_FOR_LEAVE_GAME:
                            if (mRightSoftkey == SOFTKEY_OK) {
                                ScreenOnlinePlay screenOnline = new ScreenOnlinePlay(mContext);
                                mContext.setScreen(screenOnline);
                            }
                            break;

                        case STATE_END_THE_GAME:
                        case STATE_MESSAGE:
                            if (mRightSoftkey == SOFTKEY_OK) {
                                dismissDialog();
                            }
                            break;


                        case STATE_DRAW_REQUEST:
                            if (mRightSoftkey == SOFTKEY_OK) {
                                dismissDialog();
                                try {
                                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                                    aOutput.writeString16(new String16(mContext.mUsername));
                                    aOutput.writeString16(new String16(mContext.mOpponentName));
                                    mContext.mNetwork.sendMessage(Protocol.REQUEST_I_AGREE_DRAW_GAME, aByteArray.toByteArray());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                    }
                    break;
            }
        } else {
            if (!mIsDisplayMenu) {
                if (mContext.mIsMyTurn) {
                    int deltaX = 0;
                    int deltaY = 0;
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
                } else {
                    switch (keyCode) {
                        case Key.SOFT_RIGHT:
                            mIsDisplayMenu = true;
                            break;
                    }
                }
            } else {
                switch (keyCode) {
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
                        switch (mContextMenu.selectedItem()) {
                            case BUTTON_CONTINUE:
                                mIsDisplayMenu = false;
                                break;
                            case BUTTON_SEND_MESSAGE:
                                mContext.mInputScreen.mTextBox.setConstraints(TextField.ANY);
                                mContext.mInputScreen.mTextBox.setLabel("Tin nhan");
                                mContext.mInputScreen.mTextBox.setString("");
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
                                if (!mContext.mIsOnlinePlay) {
                                    addDialog("Bạn có muốn thoát không?",
                                            SOFTKEY_CANCEL,
                                            SOFTKEY_OK,
                                            STATE_ASK_FOR_LEAVE_GAME);
                                } else {
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
