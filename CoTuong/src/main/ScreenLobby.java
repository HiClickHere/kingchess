/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import core.ChessDataOutputStream;
import core.FriendRecord;
import core.Protocol;
import core.Screen;
import core.String16;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;
import util.Key;
import util.Menu;
import util.Utils;

/**
 *
 * @author dong
 */
public class ScreenLobby extends ScreenOnlinePlay {

    private Vector mLobbyList;
    private boolean mNeedScrollDown;
    private boolean mNeedScrollUp;
    private int mStartDisplayIndex;
    private int mEndOfDisplayIndex;
    private int mNumberOfDisplay;
    public int mSelectedPlayerIndex;
    public Menu mMenu;
    public final static int MAX_RECORD_DISPLAY = 4;
    public boolean mIsDisplayMenu;
    public final static int LIST_TOP_PLAYER = 0;
    public final static int LIST_FRIENDS = 1;
    public int mCurrentList;
    
    public final static int BUTTON_LOBBY_SEND_CHALLENGE = 0;
    public final static int BUTTON_LOBBY_ADD_BUDDY = 1;
    public final static int BUTTON_LOBBY_SEND_MESSAGE = 2;

    public ScreenLobby(Context aContext, int list) {
        super(aContext);
        mLobbyList = mContext.mLobbyList;
        mSelectedPlayerIndex = 0;
        mMenu = new Menu();
        mIsDisplayMenu = false;
        mCurrentList = list;
    }

    public void addTestRecord() {
        Random aRan = new Random();
        String name[] = {
            "dongnh",
            "mercury",
            "nguoidoi",
            "nguoi",
            "sieunhan",
            "namga",
            "absd",
            "eeepc",
            "trananh",
            "fuck",
            "chim"
        };
        for (int i = 0; i < 19; i++) {
            FriendRecord aFriendRecord = new FriendRecord();
            String aName = name[(Math.abs(aRan.nextInt()) % name.length)] + "" + (Math.abs(aRan.nextInt()) % 100);
            aFriendRecord.mID = i;
            aFriendRecord.mUsername = aName;
            aFriendRecord.mStatus = (Math.abs(aRan.nextInt()) % 4);
        }

        if (true) {
            FriendRecord aFriendRecord = new FriendRecord();
            String aName = "dongnh";
            aFriendRecord.mID = 19;
            aFriendRecord.mUsername = aName;
            aFriendRecord.mStatus = (Math.abs(aRan.nextInt()) % 4);
            addLobbyToList(aFriendRecord);
        }
    }

    public void addLobbyToList(FriendRecord aRecord) {
        boolean found = false;
        for (int i = 0; i < mLobbyList.size(); i++) {
            FriendRecord r = (FriendRecord) mLobbyList.elementAt(i);
            //update
            if (r.mUsername.equals(aRecord.mUsername)) {
                r.mID = aRecord.mID;
                r.mUsername = aRecord.mUsername;
                r.mStatus = aRecord.mStatus;
                break;
            }
        }

        if (!found) {
            mLobbyList.addElement(aRecord);
        }
    }

    public void onActivate() {
        super.onActivate();
        mIsDisplayMenu = false;
        mMenu.setColors(0x5f7a7a, 0x708585);
        mMenu.addItem(0,
                StringConst.STR_CHALLENGE,
                false,
                false,
                20,
                90,
                getWidth() >> 1,
                -1,
                Graphics.HCENTER | Graphics.VCENTER);
        mMenu.addItem(1,
                StringConst.STR_ADD_BUDDY,
                false,
                false,
                20,
                90,
                getWidth() >> 1,
                -1,
                Graphics.HCENTER | Graphics.VCENTER);
        mMenu.addItem(2,
                StringConst.STR_SEND_MESSAGE,
                false,
                false,
                20,
                90,
                getWidth() >> 1,
                -1,
                Graphics.HCENTER | Graphics.VCENTER);
        setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_MENU);
    }
    private long mLastUpdateStatusList = 0;

    public void onTick(long aMilliseconds) {
        super.onTick(aMilliseconds);
        //mStartDisplayIndex = 0;
        if (!mIsDisplayDialog) {
            mNumberOfDisplay =
                    ((mLobbyList.size() - mStartDisplayIndex) > MAX_RECORD_DISPLAY)
                    ? MAX_RECORD_DISPLAY
                    : (mLobbyList.size() - mStartDisplayIndex);
            mEndOfDisplayIndex = mLobbyList.size() - mNumberOfDisplay;

            if (mStartDisplayIndex == 0) {
                mNeedScrollUp = false;
            } else {
                mNeedScrollUp = true;
            }
            if (mStartDisplayIndex < mEndOfDisplayIndex) {
                mNeedScrollDown = true;
            } else {
                mNeedScrollDown = false;
            }
        }

        // cập nhật trạng thái player 1 phút 1 lần
        if (System.currentTimeMillis() - mLastUpdateStatusList > 60000) {
            if (mCurrentList == LIST_TOP_PLAYER) {
                try {
                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                    mContext.mNetwork.sendMessage(Protocol.REQUEST_TOP_PLAYERS, aByteArray.toByteArray());
                    mLastUpdateStatusList = System.currentTimeMillis();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                    aOutput.writeString16(new String16(mContext.mUsername));
                    mContext.mNetwork.sendMessage(Protocol.REQUEST_NEED_FRIENDS_LIST, aByteArray.toByteArray());
                    mLastUpdateStatusList = System.currentTimeMillis();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void keyPressed(int keyCode) {
        if (!mIsDisplayDialog) {
            switch (keyCode) {
                case Key.UP:
                    if (!mIsDisplayMenu) {
                        if (mSelectedPlayerIndex > mStartDisplayIndex) {
                            mSelectedPlayerIndex--;
                        } else {
                            if (mSelectedPlayerIndex > 0) {
                                mSelectedPlayerIndex--;
                            //System.out.println("UP: " + mStartDisplayIndex);
                            }
                            if (mStartDisplayIndex > 0) {
                                mStartDisplayIndex--;
                                mNeedScrollDown = true;
                            } else {
                                mNeedScrollUp = false;
                                mNeedScrollDown = true;
                            }
                        }
                    } else {
                        mMenu.onDirectionKeys(0);
                    }
                    break;
                case Key.DOWN:
                    if (!mIsDisplayMenu) {
                        if (mSelectedPlayerIndex < mStartDisplayIndex + mNumberOfDisplay - 1) {
                            mSelectedPlayerIndex++;
                        } else {
                            //System.out.println("DOWN: " + mStartDisplayIndex);
                            if (mStartDisplayIndex < mEndOfDisplayIndex) {
                                mStartDisplayIndex++;
                                mNeedScrollUp = true;
                            } else {
                                mNeedScrollUp = true;
                                mNeedScrollDown = false;
                            }
                        }
                    } else {
                        mMenu.onDirectionKeys(1);
                    }
                    break;
                case Key.SOFT_LEFT:
                    if (!mIsDisplayMenu) {
                        ScreenOnlinePlay aOnlinePlayScreen = new ScreenOnlinePlay(mContext);
                        mContext.setScreen(aOnlinePlayScreen);
                    } else {
                        mIsDisplayMenu = false;
                        setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_MENU);
                    }
                    break;
                case Key.SELECT:
                case Key.SOFT_RIGHT:
                    if (!mIsDisplayMenu) {
                        mIsDisplayMenu = true;
                        setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
                    } else {
                        switch (mMenu.selectedItem()) {
                            case BUTTON_LOBBY_SEND_CHALLENGE:
                                try {
                                    FriendRecord aFriend = (FriendRecord) mLobbyList.elementAt(mSelectedPlayerIndex);
                                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                                    //                                    aOutput.writeInt(mContext.mUserID);
                                    //aOutput.writeInt(-1); // -1 meaning random user
                                    aOutput.writeString16(new String16(mContext.mUsername));
                                    mContext.mOpponentName = aFriend.mUsername;
                                    aOutput.writeString16(new String16(mContext.mOpponentName));

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
                            case BUTTON_LOBBY_ADD_BUDDY:
                                try {
                                    FriendRecord aFriend = (FriendRecord) mLobbyList.elementAt(mSelectedPlayerIndex);
                                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                                    ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);
                                    aOutput.writeString16(new String16(mContext.mUsername));
                                    aOutput.writeString16(new String16(aFriend.mUsername));
                                    mContext.mNetwork.sendMessage(Protocol.REQUEST_MAKE_FRIEND, aByteArray.toByteArray());
                                    mDialog.setText(StringConst.STR_CONNECTING_SERVER);
                                    setSoftKey(SOFTKEY_CANCEL, -1, -1);
                                    mIsDisplayDialog = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case BUTTON_LOBBY_SEND_MESSAGE:     
                                FriendRecord aFriend = (FriendRecord) mLobbyList.elementAt(mSelectedPlayerIndex);
                                mReceiplientName = aFriend.mUsername;
                                mContext.mInputScreen.mTextBox.setLabel("Tin nhan");
                                mContext.mInputScreen.mTextBox.setString("");
                                mContext.mInputScreen.mTextBox.setConstraints(TextField.ANY);
                                mContext.setDisplayTextBox();                                
                                break;
                        }
                    }
                    break;
            }
        } else {
            switch (keyCode) {
                case Key.UP:
                case Key.DOWN:
                    mDialog.onKeyPressed(keyCode);
                    break;
                case Key.SELECT:
                case Key.SOFT_RIGHT:
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
                        }
                    }
                    break;
                case Key.SOFT_LEFT:
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
                                    addDialog(StringConst.STR_CONNECTING_SERVER,
                                            SOFTKEY_CANCEL, -1,
                                            STATE_CONNECTING);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                mContext.mOpponentName = "";
                            }
                            break;
                    }
                    break;
            }
        }
    }

    public void paint(Graphics g) {
        g.setClip(0, 0, getWidth(), getHeight());
        g.setColor(0x708585);
        g.fillRect(0, 0, getWidth(), getHeight());
        String menu_name = StringConst.STR_TITLE_LOBBY;

        if (mCurrentList == LIST_FRIENDS) {
            menu_name = "BẠN BÈ";
        }
        int w = getWidth() - 4;//mContext.mTahomaOutlineWhite.getWidth(menu_name) + 6;

        int h = mContext.mTahomaOutlineWhite.getHeight() + 4;
        int x = (getWidth() - w) >> 1;
        int y = 2;
        int banner_color = 0xfe9090;
        mContext.mTahomaOutlineWhite.write(g, menu_name, getWidth() >> 1, y + (h >> 1), Graphics.HCENTER | Graphics.VCENTER);

        int column_width[] = {
            mContext.mTahomaOutlineBlue.getWidth("Hạng"),
            mContext.mTahomaOutlineBlue.getWidth("WWWWWWWWWW"),
        };
        String column_title[] = {"Hạng", "Tên"};

        int width = 0;
        for (int i = 0; i < column_width.length; i++) {
            width += column_width[i];
        }
        width += 10;

        x = (getWidth() - width) >> 1;

        g.setColor(0);
        int height = (MAX_RECORD_DISPLAY + 1) * (mContext.mTahomaOutlineBlue.getHeight() + 4);
        y = 30;

        g.setColor(0x5f7a7a);
        g.fillRect(x, y, width, height);
        g.setColor(Utils.lightenColor(0x5f7a7a, 20));
        g.drawLine(x, y, x, y + height);
        g.drawLine(x, y + height, x + width, y + height);
        g.setColor(Utils.lightenColor(0x5f7a7a, 20));
        g.drawLine(x + width, y, x + width, y + height - 1);
        g.drawLine(x + 1, y, x + width, y);

        width -= 10;
        x = (getWidth() - width) >> 1;
        y = 30;
        //g.fillRect(x, y, width, mLobbyList.size());        
        y += (mContext.mTahomaOutlineBlue.getHeight() >> 1);

        mContext.mTahomaOutlineRed.write(g, column_title[0], x, y, Graphics.LEFT | Graphics.VCENTER);
        x += column_width[0];
        x += column_width[1];
        mContext.mTahomaOutlineRed.write(g, column_title[1], x, y, Graphics.RIGHT | Graphics.VCENTER);

        g.setColor(0xff6699ff);

        y += (mContext.mTahomaOutlineBlue.getHeight() >> 1) + 4;
        for (int i = 0; i < mNumberOfDisplay; i++) {
            FriendRecord aFriend = (FriendRecord) mLobbyList.elementAt(i + mStartDisplayIndex);
            x = (getWidth() - width) >> 1;

            if (mSelectedPlayerIndex == i + mStartDisplayIndex) {
                g.setColor(Utils.lightenColor(0x5f7a7a, 30));
                g.fillRect(x - 5, y, width + 10, mContext.mTahomaFontBlue.getHeight() + 4);
            }

            y += (mContext.mTahomaOutlineBlue.getHeight() >> 1);

            if (mSelectedPlayerIndex == i + mStartDisplayIndex) {
                g.drawImage(mContext.mArrowRight, x - 10, y, Graphics.HCENTER | Graphics.VCENTER);
            }

            mContext.mTahomaOutlineBlue.write(g, "" + aFriend.mID, x, y, Graphics.LEFT | Graphics.VCENTER);
            x += (column_width[0] >> 1);
            x += (column_width[0] >> 1);

            x += (column_width[1] >> 1);
            x += (column_width[1] >> 1);
            if (aFriend.mStatus == 1) {
                mContext.mTahomaOutlineGreen.write(g, "" + aFriend.mUsername, x, y, Graphics.RIGHT | Graphics.VCENTER);
            } else if (aFriend.mStatus == 0) {
                mContext.mTahomaOutlineBlue.write(g, "" + aFriend.mUsername, x, y, Graphics.RIGHT | Graphics.VCENTER);
            } else {
                mContext.mTahomaOutlineRed.write(g, "" + aFriend.mUsername, x, y, Graphics.RIGHT | Graphics.VCENTER);
            }
            y += (mContext.mTahomaOutlineBlue.getHeight() >> 1) + 4;
        }

        if (mNeedScrollUp) {
            g.drawImage(mContext.mArrowUp, getWidth() >> 1, 30, Graphics.HCENTER | Graphics.BOTTOM);
        }
        if (mNeedScrollDown) {
            g.drawImage(mContext.mArrowDown, getWidth() >> 1,
                    30 + height, Graphics.HCENTER | Graphics.TOP);
        }
        if (mIsDisplayMenu) {
            mMenu.paint(g, mContext.mTahomaFontGreen,
                    mContext.mTahomaOutlineGreen,
                    mContext.mTahomaOutlineWhite,
                    getWidth() >> 1, 30 + height + 10, Graphics.HCENTER | Graphics.TOP);
        }

        if (mIsDisplayDialog) {
            mDialog.paint(g);
        }

        drawSoftkey(g);
    }
}
