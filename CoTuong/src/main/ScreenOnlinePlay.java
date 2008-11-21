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
import javax.microedition.lcdui.TextField;
import ui.FastDialog;
import util.Menu;
import util.Key;

/**
 *
 * @author dong
 */
public class ScreenOnlinePlay extends Screen {

    protected  Menu mMenu;
    protected int mState;

    public ScreenOnlinePlay(Context aContext) {
        super(aContext);
        mDialog = new FastDialog(getWidth() - 40, getHeight() - 80);
        mDialogVector = new Vector();
    }
    protected Vector mDialogVector;

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
    public final static int STATE_NOTIFY = 9;
    
    
    public String mReceiplientName;

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
        String menu_name = "CHƠI MẠNG";
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
                if (mState == STATE_CONNECTING) {
                    dismissDialog();
                }
                addDialog(StringConst.STR_CANNOT_CONNECT_TO_SERVER, -1, SOFTKEY_OK, STATE_CANNOT_CONNECT_TO_SERVER);
                return true;

            case Network.EVENT_END_COMMUNICATION:
                ByteArrayInputStream aByteArray = new ByteArrayInputStream(event.mData);
                ChessDataInputStream in = new ChessDataInputStream(aByteArray);
                try {
                    while (in.available() > 0) {
                        short returnType = in.readShort();
                        int size = in.readInt();
                        System.out.println("Response: " + returnType + " at size " + size);
                        switch (returnType)
                        {
                            case Protocol.RESPONSE_LOGOUT_SUCCESSFULLY:                                                                
                                mContext.mIsLoggedIn = false;
                                addDialog("Đăng xuất thành công.",
                                    -1,
                                    SOFTKEY_OK,
                                    STATE_LOGOUT_SUCCESSFULLY);
                                if (mState == STATE_CONNECTING) {
                                    dismissDialog();
                                }
                                break;
                            case Protocol.RESPONSE_LOGOUT_FAILURE:                                
                                mContext.mIsLoggedIn = false;
                                addDialog(in.readString16().toJavaString(),
                                    -1,
                                    SOFTKEY_OK,
                                    STATE_LOGOUT_FAILURE);
                                if (mState == STATE_CONNECTING) {
                                    dismissDialog();
                                }
                                break;
                            case Protocol.RESPONSE_STILL_ONLINE_SUCCESSFULLY:                                
                                mContext.mLastReceivedGoodConnect = System.currentTimeMillis();
                                break;
                            case Protocol.RESPONSE_SEND_CHALLENGE_SUCCESSFULLY:                                
                                dismissDialog();                                
                                addDialog("Đang chờ đợi trả lời từ " + mContext.mOpponentName + "...", -1, -1, STATE_WAITING_CHALLENGE_RESPONSE);
                                break;
                            case Protocol.RESPONSE_SEND_CHALLENGE_FAILURE:                                
                                dismissDialog();
                                addDialog(in.readString16().toJavaString(), -1, SOFTKEY_OK, STATE_CANNOT_CONNECT_TO_SERVER);
                                break;
                            case Protocol.RESPONSE_NEW_CHALLENGE:                                
                                mContext.mOpponentName = in.readString16().toJavaString();
                                addDialog("Cờ thủ " + mContext.mOpponentName + " thách đấu với bạn. " +
                                    "Bạn có chấp nhận lời thách đấu không?",
                                    SOFTKEY_CANCEL, SOFTKEY_OK,
                                    STATE_ON_CHALLENGE);
                                break;
                            case Protocol.RESPONSE_ACCEPT_CHALLENGE_SUCCESSFULLY:                                
                                mContext.mIsMyTurn = false;
                                mContext.mIsOnlinePlay = true;
                                mContext.mOfflineColor = 1;
                                ScreenLoading aScreenLoading = new ScreenLoading(mContext);
                                aScreenLoading.setLoadingScript(ScreenLoading.LOADING_SCRIPT_GAMEPLAY);
                                mContext.setScreen(aScreenLoading);
                                break;
                            case Protocol.RESPONSE_REJECT_CHALLENGE_SUCCESSFULLY:                                
                                if (mState == STATE_CONNECTING)
                                {
                                    dismissDialog();
                                }
                                break;
                            case Protocol.RESPONSE_ACCEPT_CHALLENGE_FAILURE:                                
                                if (mState == STATE_CONNECTING) {
                                    dismissDialog();
                                }
                                addDialog(in.readString16().toJavaString(), -1, SOFTKEY_OK, STATE_CANNOT_CONNECT_TO_SERVER);
                                break;
                            case Protocol.RESPONSE_CHALLENGE_TIMEOUT:                                
                                if (mState == STATE_WAITING_CHALLENGE_RESPONSE) {
                                    dismissDialog();
                                }
                                addDialog("Lời thách đấu của bạn gửi đã quá thời gian cho phép nhưng vẫn chưa có " +
                                        "trả lời. Bạn vui lòng thử lại.", -1, SOFTKEY_OK, STATE_CANNOT_CONNECT_TO_SERVER);
                                break;
                            case Protocol.RESPONSE_ACCEPT_CHALLENGE:                                
                                mContext.mIsMyTurn = true;
                                mContext.mIsOnlinePlay = true;
                                mContext.mOfflineColor = 0;
                                aScreenLoading = new ScreenLoading(mContext);
                                aScreenLoading.setLoadingScript(ScreenLoading.LOADING_SCRIPT_GAMEPLAY);
                                mContext.setScreen(aScreenLoading);
                                break;
                            case Protocol.RESPONSE_REJECT_CHALLENGE:                                
                                if (mState == STATE_WAITING_CHALLENGE_RESPONSE) {
                                    dismissDialog();
                                }
                                addDialog("Cờ thủ " + mContext.mOpponentName + " không chấp nhận lời thách đấu của bạn." +
                                        " Vui lòng thử với cờ thủ khác.", -1, SOFTKEY_OK, STATE_CANNOT_CONNECT_TO_SERVER);
                                break;         
                            case Protocol.RESPONSE_TOP_PLAYERS_LIST:                                
                            case Protocol.RESPONSE_NEW_FRIENDS_LIST:     
                                mContext.mLobbyList.removeAllElements();
                                int numberOfPlayer = in.readInt();                             
                                for (int i = 0; i < numberOfPlayer; i++)
                                {
                                    FriendRecord aPlayer = new FriendRecord();
                                    aPlayer.mID = in.readInt();
                                    aPlayer.mUsername = in.readString16().toJavaString();
                                    aPlayer.mStatus = in.readInt();
                                    mContext.mLobbyList.addElement(aPlayer);
                                }
                                if (returnType == Protocol.RESPONSE_NEW_FRIENDS_LIST && numberOfPlayer == 0)
                                {
                                    if (mState == STATE_CONNECTING) {
                                        dismissDialog();
                                    }
                                    addDialog("Chưa có ai trong danh sách bạn bè.", -1, SOFTKEY_OK, STATE_NOTIFY);
                                }
                                else
                                    if (!(this instanceof ScreenLobby))
                                    {
                                        ScreenLobby aLobby = new ScreenLobby(mContext, (returnType == Protocol.RESPONSE_TOP_PLAYERS_LIST) ? 
                                            ScreenLobby.LIST_TOP_PLAYER : ScreenLobby.LIST_FRIENDS);
                                        mContext.setScreen(aLobby);
                                    }
                                break;
                            
                            case Protocol.RESPONSE_MAKE_FRIEND_SUCCESSFULLY:
                                if (mState == STATE_CONNECTING)
                                    dismissDialog();
                                addDialog("Đã thêm vào danh sách bạn bè." , -1, SOFTKEY_OK, STATE_NOTIFY);
                                break;
                            
                            case Protocol.RESPONSE_MAKE_FRIEND_FAILURE:    
                                System.out.println("called me");
                                if (mState == STATE_CONNECTING)
                                    dismissDialog();
                                addDialog(in.readString16().toJavaString(), -1, SOFTKEY_OK, STATE_NOTIFY);
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
                if (mContext.mInputScreen.mTextBox.getLabel().equals("Ten")) // it's add buddy textbox
                {
                    try {
                        String name = mContext.mInputScreen.mTextBox.getString().toLowerCase();
                        ByteArrayOutputStream aByteOutputArray = new ByteArrayOutputStream();
                        ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteOutputArray);
                        aOutput.writeString16(new String16(mContext.mUsername));
                        aOutput.writeString16(new String16(name));
                        mContext.mNetwork.sendMessage(Protocol.REQUEST_MAKE_FRIEND, aByteOutputArray.toByteArray());
                        addDialog(StringConst.STR_CONNECTING_SERVER,
                                                    SOFTKEY_CANCEL, -1,
                                                    STATE_CONNECTING);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (mContext.mInputScreen.mTextBox.getLabel().equals("Tin nhan"))
                {
                    try {
                        ByteArrayOutputStream aByteOutputArray = new ByteArrayOutputStream();
                        ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteOutputArray);
                        aOutput.writeString16(new String16(mContext.mUsername));
                        aOutput.writeString16(new String16(mReceiplientName));
                        aOutput.writeString16(new String16(mContext.mInputScreen.mTextBox.getString()));
                        mContext.mNetwork.sendMessage(Protocol.REQUEST_SEND_MESSAGE, aByteOutputArray.toByteArray());                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }                    
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
                                addDialog("Bạn có muốn đăng xuất không?",
                                        SOFTKEY_CANCEL,
                                        SOFTKEY_OK,
                                        STATE_ASK_FOR_LOGOUT);
                                break;
                            case BUTTON_RANDOM_PLAY:
//                                try {
//                                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
//                                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
////                                    aOutput.writeInt(mContext.mUserID);
//                                    //aOutput.writeInt(-1); // -1 meaning random user
//                                    aOutput.writeString16(new String16(mContext.mUsername));
//                                    mContext.mOpponentName = "dongnh";
//                                    aOutput.writeString16(new String16(mContext.mOpponentName));
//
//                                    mContext.mNetwork.sendMessage(Protocol.REQUEST_SEND_CHALLENGE, aByteArray.toByteArray());
////                                        mDialog.setText(StringConst.STR_CONNECTING_SERVER);
////                                        setSoftKey(SOFTKEY_CANCEL, -1, -1);
////                                        mIsDisplayDialog = true;
////                                        mState = STATE_CONNECTING;
//                                    addDialog(StringConst.STR_CONNECTING_SERVER,
//                                            SOFTKEY_CANCEL, -1,
//                                            STATE_CONNECTING);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
                                addDialog("Chức năng này chưa hoàn thành.", -1, SOFTKEY_OK, STATE_NOTIFY);
                                break;
                            case BUTTON_TOP_PLAYERS:
                                try {
                                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                                    //aOutput.writeString16(new String16(mContext.mUsername));                                    
                                    mContext.mNetwork.sendMessage(Protocol.REQUEST_TOP_PLAYERS, aByteArray.toByteArray());
                                    addDialog(StringConst.STR_CONNECTING_SERVER,
                                            SOFTKEY_CANCEL, -1,
                                            STATE_CONNECTING);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case BUTTON_BUDDY:   
                                try {
                                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                                    aOutput.writeString16(new String16(mContext.mUsername));                                    
                                    mContext.mNetwork.sendMessage(Protocol.REQUEST_NEED_FRIENDS_LIST, aByteArray.toByteArray());
                                    addDialog(StringConst.STR_CONNECTING_SERVER,
                                            SOFTKEY_CANCEL, -1,
                                            STATE_CONNECTING);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case BUTTON_ADD_BUDDY:
                                mContext.mInputScreen.mTextBox.setLabel("Ten");
                                mContext.mInputScreen.mTextBox.setConstraints(TextField.ANY);
                                mContext.mInputScreen.mTextBox.setMaxSize(12);
                                mContext.mInputScreen.mTextBox.setString("");
                                mContext.setDisplayTextBox();
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
                                if (mRightSoftkey == SOFTKEY_OK) {
                                    dismissDialog();
                                    //remove all bellow challenge
                                    int dialog_index = 1; // skip this dialog

                                    while (dialog_index < mDialogVector.size()) {
                                        DialogRecord aDialog = (DialogRecord) mDialogVector.elementAt(dialog_index);
                                        if (aDialog.mState == STATE_ON_CHALLENGE) {
                                            mDialogVector.removeElementAt(dialog_index);
                                        } else {
                                            dialog_index++;
                                        }
                                    }

                                    try {
                                        ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                                        ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                                        aOutput.writeString16(new String16(mContext.mUsername));
                                        aOutput.writeString16(new String16(mContext.mOpponentName));
                                        mContext.mNetwork.sendMessage(Protocol.REQUEST_ACCEPT_CHALLENGE, aByteArray.toByteArray());
                                        addDialog(StringConst.STR_CONNECTING_SERVER,
                                                SOFTKEY_CANCEL, -1,
                                                STATE_CONNECTING);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case STATE_NOTIFY:
                                if (mRightSoftkey == SOFTKEY_OK) {
                                    dismissDialog();                                    
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
                            if (mLeftSoftkey == SOFTKEY_CANCEL) {
                                dismissDialog();

                                try {
                                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                                    aOutput.writeString16(new String16(mContext.mUsername));
                                    aOutput.writeString16(new String16(mContext.mOpponentName));
                                    mContext.mNetwork.sendMessage(Protocol.REQUEST_REJECT_CHALLENGE, aByteArray.toByteArray());
//                                    addDialog(StringConst.STR_CONNECTING_SERVER,
//                                            SOFTKEY_CANCEL, -1,
//                                            STATE_CONNECTING);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                mContext.mOpponentName = "";
                            }
                            break;
                        case STATE_NOTIFY:
                            break;
                         
                        
                    }
                }
        }
    }
}
