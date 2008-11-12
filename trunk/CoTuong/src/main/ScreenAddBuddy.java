/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import core.FriendRecord;
import core.Screen;
import javax.microedition.lcdui.Graphics;
import util.TextBox;
import util.Utils;
import util.Key;

/**
 *
 * @author dong
 */
public class ScreenAddBuddy extends Screen {

    FriendRecord mAddingFriendRecord;
    TextBox mTextBox;

    public ScreenAddBuddy(Context aContext, FriendRecord aFriendRecord) {
        super(aContext);
        mAddingFriendRecord = aFriendRecord;

        mTextBox = null;
        mTextBox = new TextBox(0x5f7a7a, Utils.lightenColor(0x5f7a7a, 30),
                getWidth() - 60, 18, Graphics.HCENTER | Graphics.VCENTER,
                mContext.mTahomaFontBlue);
        mTextBox.mMarginY = 1;
        mTextBox.mMarginX = 1;
        mTextBox.mLineSpace = 0;
        mTextBox.setEditable(true, 12);
        if (mAddingFriendRecord == null) {
            mTextBox.setEditableText("buddyname");
        } else {
            mTextBox.setEditableText(mAddingFriendRecord.mName.toJavaString());
        }
    }

    public void onActivate() {
        setSoftKey(SOFTKEY_BACK, -1, SOFTKEY_OK);
    }

    public void onTick(long aMilliseconds) {
        //repaint();
        //serviceRepaints();
    }

    public void paint(Graphics g) {
        g.setClip(0, 0, getWidth(), getHeight());
        g.setColor(0x708585);
        g.fillRect(0, 0, getWidth(), getHeight());
        String menu_name = StringConst.STR_TITLE_ADD_BUDDY;
        int w = getWidth() - 4;//mContext.mTahomaOutlineWhite.getWidth(menu_name) + 6;

        int h = mContext.mTahomaOutlineWhite.getHeight() + 4;
        int x = (getWidth() - w) >> 1;
        int y = 2;
        int banner_color = 0xfe9090;
        mContext.mTahomaOutlineWhite.write(g, menu_name, getWidth() >> 1, y + (h >> 1), Graphics.HCENTER | Graphics.VCENTER);

        y = 60;

        if (mTextBox != null) {
            mContext.mTahomaOutlineGreen.write(g, "Tên", getWidth() >> 1, y, Graphics.HCENTER | Graphics.VCENTER);
            mTextBox.paint(g, getWidth() >> 1, y + 19, Graphics.HCENTER | Graphics.VCENTER, true);
        } else {
        }

        drawSoftkey(g);
    }

    public void keyPressed(int keyCode) {
        if (keyCode == Key.NUM_0 || keyCode == Key.NUM_1 || keyCode == Key.NUM_2 || keyCode == Key.NUM_3 || keyCode == Key.NUM_4 || keyCode == Key.NUM_5 || keyCode == Key.NUM_6 || keyCode == Key.NUM_7 || keyCode == Key.NUM_8 || keyCode == Key.NUM_9) {
            mTextBox.onInput(keyCode);
        } else if (keyCode == Key.LEFT || keyCode == Key.RIGHT) {
            mTextBox.onInput(keyCode);
        } else if (keyCode == Key.BACK) {
            mTextBox.onBackspace();
        } else if (keyCode == Key.SOFT_LEFT) {
            ScreenOnlinePlay aScreen = new ScreenOnlinePlay(mContext);
            mContext.setScreen(aScreen);
        } else if (keyCode == Key.SELECT || keyCode == Key.SOFT_RIGHT) {
        }
    }
}
