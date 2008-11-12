/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import core.ChessDataInputStream;
import core.ChessDataOutputStream;
import core.Event;
import core.FriendRecord;
import core.Network;
import core.Protocol;
import core.Screen;
import core.String16;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import ui.FastDialog;
import util.Menu;
import util.Key;

/**
 *
 * @author dong
 */
public class ScreenOnlinePlay extends Screen {

    private Menu mMenu;
    private int mState;

    public ScreenOnlinePlay(Context aContext) {
        super(aContext);
        mDialog = new FastDialog(getWidth() - 40, getHeight() - 80);
        mDialogVector = new Vector();
    }
    private Vector mDialogVector;

    public void addDialog(String aString, int leftSoft, int rightSoft, int state) {
        mDialogVector.addElement(new DialogRecord(aString, leftSoft, -1, rightSoft, state));
    }

    public void removeAllDialog() {
        mDialogVector.removeAllElements();
    }

    public void dismissDialog() {
        mIsDisplayDialog = false;
        setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
    }
    public final static int BUTTON_LOGIN = 0;
    public final static int BUTTON_REGISTER = 1;
    public final static int BUTTON_RANDOM_PLAY = 2;
    public final static int BUTTON_TOP_PLAYERS = 3;
    public final static int BUTTON_BUDDY = 4;
    public final static int BUTTON_ADD_BUDDY = 5;
    public final static int BUTTON_LOGOUT = 6;
    public final static int STATE_NORMAL = 0;
    public final static int STATE_ASK_FOR_LOGOUT = 1;
    public final static int STATE_CANNOT_CONNECT_TO_SERVER = 2;
    public final static int STATE_LOGOUT_FAILURE = 3;
    public final static int STATE_LOGOUT_SUCCESSFULLY = 4;
    public final static int STATE_CONNECTING = 5;
    public final static int STATE_LOSE_CONNECTION = 6;
    public final static int STATE_WAITING_CHALLENGE_RESPONSE = 7;
    public final static int STATE_ON_CHALLENGE = 8;

    public void onActivate() {
        if (!mContext.mIsLoggedIn) {
            mMenu = new Menu();
            mMenu.setColors(0x5f7a7a, 0x708585);
            mMenu.addItem(BUTTON_LOGIN, StringConst.STR_LOGIN, false,
                    false,
                    20,
                    90,
                    getWidth() >> 1,
                    -1,
                    Graphics.HCENTER | Graphics.VCENTER);
            mMenu.addItem(BUTTON_REGISTER, StringConst.STR_REGISTER, false,
                    false,
                    20,
                    90,
                    getWidth() >> 1,
                    -1,
                    Graphics.HCENTER | Graphics.VCENTER);
            setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
        } else {
            mMenu = new Menu();
            mMenu.setColors(0x5f7a7a, 0x708585);
            mMenu.addItem(BUTTON_RANDOM_PLAY, StringConst.STR_RANDOM_PLAY, false,
                    false,
                    20,
                    90,
                    getWidth() >> 1,
                    -1,
                    Graphics.HCENTER | Graphics.VCENTER);
            mMenu.addItem(BUTTON_TOP_PLAYERS, StringConst.STR_TOP_PLAYERS, false,
                    false,
                    20,
                    90,
                    getWidth() >> 1,
                    -1,
                    Graphics.HCENTER | Graphics.VCENTER);
            mMenu.addItem(BUTTON_BUDDY, StringConst.STR_BUDDY, false,
                    false,
                    20,
                    90,
                    getWidth() >> 1,
                    -1,
                    Graphics.HCENTER | Graphics.VCENTER);
            mMenu.addItem(BUTTON_ADD_BUDDY, StringConst.STR_ADD_BUDDY, false,
                    false,
                    20,
                    90,
                    getWidth() >> 1,
                    -1,
                    Graphics.HCENTER | Graphics.VCENTER);
            mMenu.addItem(BUTTON_LOGOUT, StringConst.STR_LOGOUT, false,
                    false,
                    20,
                    90,
                    getWidth() >> 1,
                    -1,
                    Graphics.HCENTER | Graphics.VCENTER);
            setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
        }
        mIsDisplayDialog = false;
        mState = STATE_NORMAL;
    }

    public void onTick(long aMilliseconds) {
        if (!mDialogVector.isEmpty() && !mIsDisplayDialog) {
            DialogRecord aDialog = (DialogRecord) mDialogVector.elementAt(0);
            mDialogVector.removeElementAt(0);
            mDialog.setText(aDialog.mMessage);
            setSoftKey(aDialog.mLeftSoftkey, -1, aDialog.mRightSoftkey);
            mState = aDialog.mState;
            mIsDisplayDialog = true;
        }
    }

    public void paint(Graphics g) {
        g.setClip(0, 0, getWidth(), getHeight());
        g.setColor(0x708585);
        g.fillRect(0, 0, getWidth(), getHeight());
        String menu_name = StringConst.STR_TITLE_ONLINE_PLAY_MENU;
        int w = getWidth() - 4;
        int h = mContext.mTahomaOutlineWhite.getHeight() + 4;
        int x = (getWidth() - w) >> 1;
        int y = 2;
        int banner_color = 0xfe9090;

        mContext.mTahomaOutlineWhite.write(g, menu_name, getWidth() >> 1, y + (h >> 1), Graphics.HCENTER | Graphics.VCENTER);

        if (mMenu != null && !mIsDisplayDialog) {
            mMenu.paint(g,
                    mContext.mTahomaFontGreen,
                    mContext.mTahomaOutlineGreen,
                    mContext.mTahomaOutlineRed,
                    getWidth() >> 1,
                    (getHeight() - 100) >> 1,
                    Graphics.HCENTER | Graphics.VCENTER);
        } else if (mIsDisplayDialog) {
            mDialog.paint(g);
        }

        drawSoftkey(g);
    }

    public boolean onEvent(Event event) {
        switch (event.mType) {
            case Network.EVENT_NETWORK_FAILURE:
//                mIsDisplayDialog = false;
//                mDialog.setText(StringConst.STR_CANNOT_CONNECT_TO_SERVER);
//                mIsDisplayDialog = true;
//                mState = STATE_CANNOT_CONNECT_TO_SERVER;
//                setSoftKey(-1, -1, SOFTKEY_OK);
                if (mState == STATE_CONNECTING) {
                    dismissDialog();
                }
                addDialog(StringConst.STR_CANNOT_CONNECT_TO_SERVER, -1, SOFTKEY_OK, STATE_CANNOT_CONNECT_TO_SERVER);
                return true;
            case Network.EVENT_END_COMMUNICATION:
                ByteArrayInputStream aByteArray = new ByteArrayInputStream(event.mData);
                ChessDataInputStream in = new ChessDataInputStream(aByteArray);
                try {
                    while (true) {
                        short returnType = in.readShort();
                        if (returnType == Protocol.RESPONSE_LOGOUT_SUCCESSFULLY) {
                            mContext.mIsLoggedIn = false;
                            mContext.mUserID = in.readInt();

//                            mIsDisplayDialog = false;
//                            mDialog.setText("Đăng xuất thành công.");
//                            mIsDisplayDialog = true;
//                            setSoftKey(-1, -1, SOFTKEY_OK);
//                            mState = STATE_LOGOUT_SUCCESSFULLY;                            
                            addDialog("Đăng xuất thành công.",
                                    -1,
                                    SOFTKEY_OK,
                                    STATE_LOGOUT_SUCCESSFULLY);
                            if (mState == STATE_CONNECTING) {
                                dismissDialog();
                            }
                        } else if (returnType == Protocol.RESPONSE_LOGOUT_FAILURE) {
                            mContext.mIsLoggedIn = false;
//                            mIsDisplayDialog = false;
//                            mDialog.setText(in.readString16().toJavaString());
//                            mIsDisplayDialog = true;
//                            setSoftKey(-1, -1, SOFTKEY_OK);
//                            mState = STATE_LOGOUT_FAILURE;                            
                            addDialog(in.readString16().toJavaString(),
                                    -1,
                                    SOFTKEY_OK,
                                    STATE_LOGOUT_FAILURE);
                            if (mState == STATE_CONNECTING) {
                                dismissDialog();
                            }
                        } else if (returnType == Protocol.RESPONSE_STILL_ONLINE_SUCCESSFULLY) {
                            mContext.mLastReceivedGoodConnect = System.currentTimeMillis();
//                            in.readInt();
                        } else if (returnType == Protocol.RESPONSE_SEND_CHALLENGE_SUCCESSFULLY)
                        {
                            dismissDialog();
                            String opponent = in.readString16().toJavaString();
                            addDialog("Đang chờ đợi trả lời từ " + opponent + "...", -1, -1, STATE_WAITING_CHALLENGE_RESPONSE);
                        } else if (returnType == Protocol.RESPONSE_SEND_CHALLENGE_FAILURE)
                        {
                            dismissDialog();
                            addDialog(in.readString16().toJavaString(), -1, SOFTKEY_OK, STATE_CANNOT_CONNECT_TO_SERVER);
                        } else if (returnType == Protocol.RESPONSE_NEW_CHALLENGE)
                        {
                            FriendRecord aPlayer = FriendRecord.readFromStream(in);
                            mContext.mChallengerList.addElement(aPlayer);
                            //dismissDialog();                            
                            addDialog("Cờ thủ " + aPlayer.mName + " thách đấu với bạn. " +
                                    "Bạn có chấp nhận lời thách đấu không?", 
                                    SOFTKEY_CANCEL, SOFTKEY_OK, 
                                    STATE_ON_CHALLENGE);
                        } else if (returnType == Protocol.RESPONSE_ACCEPT_CHALLENGE_SUCCESSFULLY)
                        {
                            ScreenLoading aScreenLoading = new ScreenLoading(mContext);
                            aScreenLoading.setLoadingScript(ScreenLoading.LOADING_SCRIPT_GAMEPLAY);                                            
                            mContext.setScreen(aScreenLoading); 
                        } else if (returnType == Protocol.RESPONSE_ACCEPT_CHALLENGE_FAILURE)
                        {
                            if (mState == STATE_CONNECTING)
                                dismissDialog();
                            addDialog(in.readString16().toJavaString(), -1, SOFTKEY_OK, STATE_CANNOT_CONNECT_TO_SERVER);
                        } else if (returnType == Protocol.RESPONSE_CHALLENGE_TIMEOUT)
                        {
                            if (mState == STATE_WAITING_CHALLENGE_RESPONSE)
                                dismissDialog();
                            addDialog(in.readString16().toJavaString(), -1, SOFTKEY_OK, STATE_CANNOT_CONNECT_TO_SERVER);
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
//                ScreenMainMenu aMainMenu = new ScreenMainMenu(mContext);
//                aMainMenu.setMenu(ScreenMainMenu.MENU_MAIN);
//                mContext.setScreen(aMainMenu);
//                mDialog.setText("Lỗi: Mất liên lạc với máy chủ.");
//                mIsDisplayDialog = true;
//                setSoftKey(-1, -1, SOFTKEY_OK);
//                mState = STATE_LOSE_CONNECTION;
                removeAllDialog();
                dismissDialog();      
                mContext.mIsLoggedIn = false;
                mContext.mChallengerList.removeAllElements();
                addDialog("Lỗi: Mất liên lạc với máy chủ.",
                        -1,
                        SOFTKEY_OK,
                        STATE_LOSE_CONNECTION);
                return true;
        }
        return false;
    }

    public void keyPressed(int aKeyCode) {
        switch (aKeyCode) {
            case Key.UP:
            case Key.DOWN:
                if (mIsDisplayDialog) {
                    mDialog.onKeyPressed(aKeyCode);
                } else {
                    if (aKeyCode == Key.UP) {
                        mMenu.onDirectionKeys(0);
                    } else {
                        mMenu.onDirectionKeys(1);
                    }
                }
                break;
            case Key.LEFT:
            case Key.RIGHT:
                break;
            case Key.SELECT:
            case Key.SOFT_RIGHT:
                if (!mIsDisplayDialog) {
                    if (mRightSoftkey == SOFTKEY_OK) {
                        switch (mMenu.selectedItem()) {
                            case BUTTON_LOGIN:
                                ScreenLogin aScreenLogin = new ScreenLogin(mContext);
                                mContext.setScreen(aScreenLogin);
                                break;
                            case BUTTON_REGISTER:
                                ScreenRegister aScreenRegister = new ScreenRegister(mContext);
                                mContext.setScreen(aScreenRegister);
                                break;
                            case BUTTON_LOGOUT:
//                                mDialog.setText("Bạn có muốn đăng xuất không?");
//                                setSoftKey(SOFTKEY_CANCEL, -1, SOFTKEY_OK);
//                                mIsDisplayDialog = true;
//                                mState = STATE_ASK_FOR_LOGOUT;                                
                                addDialog("Bạn có muốn đăng xuất không?",
                                        SOFTKEY_CANCEL,
                                        SOFTKEY_OK,
                                        STATE_ASK_FOR_LOGOUT);
                                break;
                            case BUTTON_RANDOM_PLAY:
                                try {
                                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                                    aOutput.writeInt(mContext.mUserID);
                                    aOutput.writeInt(-1); // -1 meaning random user
                                    mContext.mNetwork.sendMessage(Protocol.REQUEST_SEND_CHALLENGE, aByteArray.toByteArray());
//                                        mDialog.setText(StringConst.STR_CONNECTING_SERVER);
//                                        setSoftKey(SOFTKEY_CANCEL, -1, -1);
//                                        mIsDisplayDialog = true;
//                                        mState = STATE_CONNECTING;
                                    addDialog(StringConst.STR_CONNECTING_SERVER,
                                            SOFTKEY_CANCEL, -1,
                                            STATE_CONNECTING);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case BUTTON_TOP_PLAYERS:
                                ScreenLobby aLobby = new ScreenLobby(mContext);
                                aLobby.addTestRecord();
                                mContext.setScreen(aLobby);
                                break;
                            case BUTTON_BUDDY:
                                ScreenBuddy aBuddyScreen = new ScreenBuddy(mContext);
                                aBuddyScreen.addTestRecord();
                                mContext.setScreen(aBuddyScreen);
                                break;
                            case BUTTON_ADD_BUDDY:
                                ScreenAddBuddy aScreenAddBuddy = new ScreenAddBuddy(mContext, null);
                                mContext.setScreen(aScreenAddBuddy);
                                break;
                        }
                    }
                } else {
                    if (mRightSoftkey == SOFTKEY_OK) {
                        switch (mState) {
                            case STATE_ASK_FOR_LOGOUT:
                                if (mRightSoftkey == SOFTKEY_OK) {
                                    dismissDialog();
                                    try {
                                        ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                                        ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                                        aOutput.writeString16(new String16(mContext.mUsername));
                                        aOutput.writeString16(new String16(mContext.mPassword));
                                        mContext.mNetwork.sendMessage(Protocol.REQUEST_LOGOUT, aByteArray.toByteArray());
//                                        mDialog.setText(StringConst.STR_CONNECTING_SERVER);
//                                        setSoftKey(SOFTKEY_CANCEL, -1, -1);
//                                        mIsDisplayDialog = true;
//                                        mState = STATE_CONNECTING;
                                        addDialog(StringConst.STR_CONNECTING_SERVER,
                                                SOFTKEY_CANCEL, -1,
                                                STATE_CONNECTING);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case STATE_CANNOT_CONNECT_TO_SERVER:
                                if (mRightSoftkey == SOFTKEY_OK) {
                                    dismissDialog();
                                    setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
                                }
                                break;
                            case STATE_LOGOUT_SUCCESSFULLY:
                            case STATE_LOGOUT_FAILURE:
                            case STATE_LOSE_CONNECTION:
                                if (mRightSoftkey == SOFTKEY_OK) {
                                    dismissDialog();
                                    ScreenMainMenu aMainMenu = new ScreenMainMenu(mContext);
                                    aMainMenu.setMenu(ScreenMainMenu.MENU_MAIN);
                                    mContext.setScreen(aMainMenu);
                                }
                                break;
                            case STATE_ON_CHALLENGE:
                                if (mRightSoftkey == SOFTKEY_OK)
                                {
                                    dismissDialog();
                                    FriendRecord player = (FriendRecord)mContext.mChallengerList.elementAt(0);
                                    //remove all bellow challenge
                                    mContext.mChallengerList.removeAllElements();
                                    //remove all bellow challenge
                                    int dialog_index = 1; // skip this dialog
                                    while (dialog_index < mDialogVector.size())
                                    {
                                        DialogRecord aDialog = (DialogRecord)mDialogVector.elementAt(dialog_index);
                                        if (aDialog.mState == STATE_ON_CHALLENGE)
                                        {
                                            mDialogVector.removeElementAt(dialog_index);
                                        } else
                                            dialog_index++;
                                    }
                                    
                                    try {
                                        ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                                        ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                                        //aOutput.writeString16(new String16(mContext.mUsername));
                                        //aOutput.writeString16(new String16(mContext.mPassword));
                                        aOutput.writeInt(mContext.mUserID);
                                        aOutput.writeInt(player.mID);
                                        mContext.mNetwork.sendMessage(Protocol.REQUEST_ACCEPT_CHALLENGE, aByteArray.toByteArray());
//                                        mDialog.setText(StringConst.STR_CONNECTING_SERVER);
//                                        setSoftKey(SOFTKEY_CANCEL, -1, -1);
//                                        mIsDisplayDialog = true;
//                                        mState = STATE_CONNECTING;
                                        addDialog(StringConst.STR_CONNECTING_SERVER,
                                                SOFTKEY_CANCEL, -1,
                                                STATE_CONNECTING);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }   
                                break;
                        }
                    }
                }
                break;
            case Key.SOFT_LEFT:
                if (!mIsDisplayDialog) {
                    if (mLeftSoftkey == SOFTKEY_BACK) {
                        if (mContext.mIsLoggedIn) {
                            addDialog("Bạn có muốn đăng xuất không?",
                                    SOFTKEY_CANCEL,
                                    SOFTKEY_OK,
                                    STATE_ASK_FOR_LOGOUT);
                        } else {
                            ScreenMainMenu aScreenMenu = new ScreenMainMenu(mContext);
                            aScreenMenu.setMenu(ScreenMainMenu.MENU_MAIN);
                            mContext.setScreen(aScreenMenu);
                        }
                    }
                    break;
                } else {
                    switch (mState) {
                        case STATE_ASK_FOR_LOGOUT:
                            if (mLeftSoftkey == SOFTKEY_CANCEL) {
                                dismissDialog();
                                setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
                            }
                            break;
                        case STATE_CONNECTING:
                            if (mLeftSoftkey == SOFTKEY_CANCEL) {
                                dismissDialog();
                                mContext.mNetwork.stopConnection();
                                setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
                            }
                            break;
                        case STATE_ON_CHALLENGE:
                            if (mLeftSoftkey == SOFTKEY_CANCEL)
                            {
                                dismissDialog();
                                mContext.mChallengerList.removeElementAt(0);
                            }   
                            break;
                    }
                }
        }
    }
}
