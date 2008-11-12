/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import core.FriendRecord;
import core.Screen;
import core.String16;
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import util.Key;
import util.Menu;
import util.Utils;

/**
 *
 * @author dong
 */
public class ScreenBuddy extends Screen {

    private Vector mBuddyList;
    private boolean mNeedScrollDown;
    private boolean mNeedScrollUp;
    private int mStartDisplayIndex;
    private int mEndOfDisplayIndex;
    private int mNumberOfDisplay;
    public int mSelectedPlayerIndex;
    public Menu mMenu;
    public final static int MAX_RECORD_DISPLAY = 4;
    public boolean mIsDisplayMenu;

    public ScreenBuddy(Context aContext) {
        super(aContext);
        mBuddyList = new Vector();
        mSelectedPlayerIndex = 0;
        mMenu = new Menu();
        mIsDisplayMenu = false;
    }

    public void addTestRecord() {
        Random aRan = new Random();
        String name[] = {
            "DONGNH",
            "MERCURY",
            "NGUOIDOI",
            "NGUOI",
            "SIEUNHAN",
            "NAMGA",
            "ABDS",
            "EEEPC",
            "TRANANH",
            "FUCK",
            "CHIM"
        };
        for (int i = 0; i < 20; i++) {
            FriendRecord aFriendRecord = new FriendRecord();
            String aName = name[(Math.abs(aRan.nextInt()) % name.length)] + "" + (Math.abs(aRan.nextInt()) % 100);
            aFriendRecord.mName = new String16(aName);
            aFriendRecord.mIsOnline = ((Math.abs(aRan.nextInt()) % 2) == 0);
            aFriendRecord.mRangkingIndex = i;
            aFriendRecord.mWinCount = Math.abs(aRan.nextInt()) % 1000;
            aFriendRecord.mLoseCount = Math.abs(aRan.nextInt()) % 1000;
            aFriendRecord.mDrawCount = Math.abs(aRan.nextInt()) % 1000;
            addLobbyToList(aFriendRecord);
        }
    }

    public void addLobbyToList(FriendRecord aRecord) {
        boolean found = false;
        for (int i = 0; i < mBuddyList.size(); i++) {
            FriendRecord r = (FriendRecord) mBuddyList.elementAt(i);
            //update
            if (r.mName == aRecord.mName) {
                r.mID = aRecord.mID;
                r.mIsOnline = aRecord.mIsOnline;
                r.mWinCount = aRecord.mWinCount;
                r.mDrawCount = aRecord.mDrawCount;
                r.mLoseCount = aRecord.mLoseCount;
                r.mIsRequest = aRecord.mIsRequest;
                found = true;
                break;
            }
        }

        if (!found) {
            mBuddyList.addElement(aRecord);
        }
    }

    public void onActivate() {
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

    public void onTick(long aMilliseconds) {
        //mStartDisplayIndex = 0;
        mNumberOfDisplay =
                ((mBuddyList.size() - mStartDisplayIndex) > MAX_RECORD_DISPLAY)
                ? MAX_RECORD_DISPLAY
                : (mBuddyList.size() - mStartDisplayIndex);
        mEndOfDisplayIndex = mBuddyList.size() - mNumberOfDisplay;

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
        //repaint();
        //serviceRepaints();
    }

    public void keyPressed(int keyCode) {
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
                }
                break;
        }
    }

    public void paint(Graphics g) {
        //System.out.println("go here");
        g.setClip(0, 0, getWidth(), getHeight());
        g.setColor(0x708585);
        g.fillRect(0, 0, getWidth(), getHeight());
        String menu_name = StringConst.STR_TITLE_BUDDY;
        int w = getWidth() - 4;//mContext.mTahomaOutlineWhite.getWidth(menu_name) + 6;

        int h = mContext.mTahomaOutlineWhite.getHeight() + 4;
        int x = (getWidth() - w) >> 1;
        int y = 2;
        int banner_color = 0xfe9090;
        mContext.mTahomaOutlineWhite.write(g, menu_name, getWidth() >> 1, y + (h >> 1), Graphics.HCENTER | Graphics.VCENTER);
        //System.out.println("go here " + menu_name);

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
        //g.fillRect(x, y, width, mBuddyList.size());        
        y += (mContext.mTahomaOutlineBlue.getHeight() >> 1);

        mContext.mTahomaOutlineRed.write(g, column_title[0], x, y, Graphics.LEFT | Graphics.VCENTER);
        x += column_width[0];
        x += column_width[1];
        mContext.mTahomaOutlineRed.write(g, column_title[1], x, y, Graphics.RIGHT | Graphics.VCENTER);

        g.setColor(0xff6699ff);

        y += (mContext.mTahomaOutlineBlue.getHeight() >> 1) + 4;
        for (int i = 0; i < mNumberOfDisplay; i++) {
            FriendRecord aFriend = (FriendRecord) mBuddyList.elementAt(i + mStartDisplayIndex);
            x = (getWidth() - width) >> 1;

            if (mSelectedPlayerIndex == i + mStartDisplayIndex) {
                g.setColor(Utils.lightenColor(0x5f7a7a, 30));
                g.fillRect(x - 5, y, width + 10, mContext.mTahomaFontBlue.getHeight() + 4);
            }

            y += (mContext.mTahomaOutlineBlue.getHeight() >> 1);

            if (mSelectedPlayerIndex == i + mStartDisplayIndex) {
                g.drawImage(mContext.mArrowRight, x - 10, y, Graphics.HCENTER | Graphics.VCENTER);
            }

            mContext.mTahomaOutlineBlue.write(g, "" + aFriend.mRangkingIndex, x, y, Graphics.LEFT | Graphics.VCENTER);
            x += (column_width[0] >> 1);
            x += (column_width[0] >> 1);

            x += (column_width[1] >> 1);
            x += (column_width[1] >> 1);
            if (aFriend.mIsOnline) {
                mContext.mTahomaOutlineGreen.write(g, "" + aFriend.mName.toJavaString(), x, y, Graphics.RIGHT | Graphics.VCENTER);
            } else {
                mContext.mTahomaOutlineBlue.write(g, "" + aFriend.mName.toJavaString(), x, y, Graphics.RIGHT | Graphics.VCENTER);
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

        drawSoftkey(g);
    }
}
