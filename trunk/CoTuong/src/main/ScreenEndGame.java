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
import javax.microedition.lcdui.Graphics;
import ui.FastDialog;
import util.*;

/**
 *
 * @author dong
 */
public class ScreenEndGame extends Screen {
    public final static int BUTTON_ADD_BUDDY = 0;
    public final static int BUTTON_BACK_TO_LOBBY = 1;
    private FastDialog mDialog = null;
    private boolean mIsDisplayDialog = false;
    
    public Menu mMenu;

    public ScreenEndGame(Context aContext) {
        super(aContext);

        mDialog = new FastDialog(getWidth() - 40, getHeight() - 80);
        mMenu = new Menu();
        
        mMenu.setColors(0x5f7a7a, 0x708585);
        mMenu.addItem(BUTTON_ADD_BUDDY,
                "Thêm bạn",
                false,
                false,
                20,
                90,
                getWidth() >> 1,
                -1,
                Graphics.HCENTER | Graphics.VCENTER);
        mMenu.addItem(BUTTON_BACK_TO_LOBBY,
                "Tiếp tục",
                false,
                false,
                20,
                90,
                getWidth() >> 1,
                -1,
                Graphics.HCENTER | Graphics.VCENTER);        
    }

    public void onActivate() {
        mIsDisplayDialog = false;
        setSoftKey(-1, -1, SOFTKEY_OK);
    }

    public void onTick(long aMilliseconds) {
    }

    public void paint(Graphics g) {
        g.setClip(0, 0, getWidth(), getHeight());
        g.setColor(0x708585);
        g.fillRect(0, 0, getWidth(), getHeight());

        String menu_name = "KẾT QUẢ";
        int w = getWidth() - 4;//mContext.mTahomaOutlineWhite.getWidth(menu_name) + 6;

        int h = mContext.mTahomaOutlineWhite.getHeight() + 4;
        int x = (getWidth() - w) >> 1;
        int y = 2;
        int banner_color = 0xfe9090;
        mContext.mTahomaOutlineWhite.write(g, menu_name, getWidth() >> 1, y + (h >> 1), Graphics.HCENTER | Graphics.VCENTER);

        int column_width[] = {
            mContext.mTahomaOutlineBlue.getWidth("wwwwwwwwwwww"),
            mContext.mTahomaOutlineBlue.getWidth("000/000"),
        };
        String column_title[] = {"Tên", "Chỉ số"};

        int width = 0;
        for (int i = 0; i < column_width.length; i++) {
            width += column_width[i];
        }
        width += 10;

        x = (getWidth() - width) >> 1;

        g.setColor(0);
        int height = 2 * (mContext.mTahomaOutlineBlue.getHeight() + 4);
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
        x = (getWidth() - width) >> 1;
        mContext.mTahomaOutlineGreen.write(g, mContext.mUsername, x, y, Graphics.LEFT | Graphics.VCENTER);
        x += column_width[0];
        x += column_width[1];
        mContext.mTahomaOutlineGreen.write(g, "100/100", x, y, Graphics.RIGHT | Graphics.VCENTER);

        y += (mContext.mTahomaOutlineBlue.getHeight() >> 1) + 4;
        x = (getWidth() - width) >> 1;
        mContext.mTahomaOutlineGreen.write(g, mContext.mOpponentName, x, y, Graphics.LEFT | Graphics.VCENTER);
        x += column_width[0];
        x += column_width[1];
        mContext.mTahomaOutlineGreen.write(g, "100/100", x, y, Graphics.RIGHT | Graphics.VCENTER);        

        mMenu.paint(g, mContext.mTahomaFontGreen,
                    mContext.mTahomaOutlineGreen,
                    mContext.mTahomaOutlineWhite,
                    getWidth() >> 1, getHeight() - 60, Graphics.HCENTER | Graphics.TOP);
        
        if (mIsDisplayDialog)
            mDialog.paint(g);
        
        drawSoftkey(g);
    }

    public void keyPressed(int aKey) {
        if (mIsDisplayDialog)
            return;
        
        switch (aKey) {
            case Key.UP:
                mMenu.onDirectionKeys(0);
                break;
            case Key.DOWN:
                mMenu.onDirectionKeys(1);
                break;
            case Key.LEFT:                
                break;
            case Key.RIGHT:                
                break;
            case Key.SOFT_RIGHT:
            case Key.SELECT:
                switch (mMenu.selectedItem()) {
                    case BUTTON_ADD_BUDDY: 
                        try {
                            ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                            ChessDataOutputStream aOutput = new ChessDataOutputStream(aByteArray);                              
                            aOutput.writeString16(new String16(mContext.mUsername));
                            aOutput.writeString16(new String16(mContext.mOpponentName));
                            mContext.mNetwork.sendMessage(Protocol.REQUEST_MAKE_FRIEND, aByteArray.toByteArray());                    
                            mDialog.setText(StringConst.STR_CONNECTING_SERVER);
                            setSoftKey(SOFTKEY_CANCEL, -1, -1);
                            mIsDisplayDialog = true;
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    case BUTTON_BACK_TO_LOBBY:
                        ScreenOnlinePlay screenOnline = new ScreenOnlinePlay(mContext);
                        mContext.setScreen(screenOnline);
                        break;
                }
                break;
        }
    }
    
    public boolean onEvent(Event event)
    {
        switch (event.mType)
        {
            case Network.EVENT_NETWORK_FAILURE:
                mIsDisplayDialog = false;
                mDialog.setText(StringConst.STR_CANNOT_CONNECT_TO_SERVER);
                mIsDisplayDialog = true;
                setSoftKey(-1, -1, SOFTKEY_OK);
                return true;
            case Network.EVENT_END_COMMUNICATION:
                ByteArrayInputStream aByteArray = new ByteArrayInputStream(event.mData);
                ChessDataInputStream in = new ChessDataInputStream(aByteArray);
                int size;
                try {
                    short returnType = in.readShort();
                    size = in.readInt();
                    switch (returnType)
                    {                    
                        case Protocol.RESPONSE_MAKE_FRIEND_SUCCESSFULLY:
                        case Protocol.RESPONSE_MAKE_FRIEND_FAILURE:
                            mIsDisplayDialog = false;
                            setSoftKey(-1, -1, SOFTKEY_OK);
                            break;
                        default:                            
                            in.skip(size);
                            break;
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                return true;
            case Network.EVENT_RECEIVING:
            case Network.EVENT_SENDING:
                return true;
            case Network.EVENT_SETUP_CONNECTION:                
                return true;            
            case Network.EVENT_TEXTBOX_FOCUS:                
                return true;            
            case Network.EVENT_TEXTBOX_INFOCUS:                
                return true;
        }
        return false;
    }
}
