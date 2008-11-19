package main;

import core.ChessDataInputStream;
import core.ChessDataOutputStream;
import core.Event;
import core.MainCanvas;
import core.Network;
import core.Protocol;
import core.Screen;
import core.String16;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Stack;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import util.ImageFont;
import util.UnicodeFont;
/*
 * Context.java
 *
 * Created on October 29, 2008, 3:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author dong
 */
public class Context 
//        implements Runnable 
{
    
    public final static int RESULT_LOSE = 0;
    public final static int RESULT_DRAW = 1;
    public final static int RESULT_WIN = 2;    

    //public Thread mMainThread;
    public boolean mIsRunning;
    public Screen mCurrentScreen;
    public Vector mEventQueue;
    public ChessMidlet mMIDlet;
    public UnicodeFont mUnicodeFont;
    public UnicodeFont mUnicodeFontOutline;
    public UnicodeFont mTahomaFont;
    public UnicodeFont mTahomaOutline;
    public UnicodeFont mTahomaFontCyan;
    public UnicodeFont mTahomaFontRed;
    public UnicodeFont mTahomaFontGreen;
    public UnicodeFont mTahomaFontBlue;
    public UnicodeFont mTahomaFontWhite;
    public UnicodeFont mTahomaOutlineCyan;
    public UnicodeFont mTahomaOutlineWhite;
    public UnicodeFont mTahomaOutlineGreen;
    public UnicodeFont mTahomaOutlineRed;
    public UnicodeFont mTahomaOutlineBlue;
    public Image mSoftkeyOK;
    public Image mSoftkeyBack;
    public Image mSoftkeyMenu;
    public Image mSoftkeyCancel;
    public Image mArrowUp;
    public Image mArrowDown;
    public Image mArrowLeft;
    public Image mArrowRight;
    public Image mBackgroundImage;
    public Image mBlackPieces[];
    public Image mRedPieces[];
    public Image mBoardImage;
    public static Context mMe;
    public boolean mIsLoggedIn;
    //public int mUserID;
    public String mUsername = "dongnh";
    public String mPassword = "1234";
    public String mOpponentName;
    public boolean mIsMyTurn;
    public boolean mIsOnlinePlay;
    public int mMatchResult;
    public Network mNetwork;
    public long mLastSendStillOnline;
    public long mLastReceivedGoodConnect;
    public final static long STILL_ONLINE_CYCLE = 10000;
    public final static long MAXIMUM_LOST_CONNECTION_PERIOD = 120000;
    public boolean mIsLoading = false;
    public MainCanvas mCanvas;
    public int mWidth;
    public int mHeight;    
    
    public Vector mLobbyList;
    
    //public Vector mChallengerList;

    /** Creates a new instance of Context */
    public Context(ChessMidlet aMIDlet) {
        mMe = this;
        mMIDlet = aMIDlet;
        mIsRunning = false;
        mCurrentScreen = null;
        mEventQueue = new Vector();
        mIsLoggedIn = false;
        mRedPieces = new Image[7];
        mBlackPieces = new Image[7];
        mNetwork = Network.createNetworkHandler(this);
        mCanvas = new MainCanvas(this);        
        mLobbyList = new Vector();
    }

    public void start() {
        ScreenLoading screenLoading = new ScreenLoading(this);
        screenLoading.setLoadingScript(ScreenLoading.LOADING_SCRIPT_FIRST_INIT);
        setScreen(screenLoading);
        mInputScreen = new ScreenInput(this);
        Display.getDisplay(mMIDlet).setCurrent(mCanvas);
//        mMainThread = new Thread(this);
//        mMainThread.start();
        mIsRunning = true;
        mCanvas.start();        
    }

    public void stop() {
        mIsRunning = false;
        mCanvas.stop();
    }

    public void setScreen(Screen aScreen) {
        if (mCurrentScreen != null) {
            mCurrentScreen.onDeactivate();
            mCurrentScreen = null;
            System.gc();
        }
        mCurrentScreen = aScreen;
        mCurrentScreen.onActivate();
    }

    public void fireEvent(Event event) {
        mEventQueue.addElement(event);
    }

    public void onNetworkEvent(Event event) {
        switch (event.mType) {
            case Network.EVENT_SETUP_CONNECTION:
                System.out.println("NETWORK: setup connection");
                fireEvent(event);
                break;
            case Network.EVENT_SENDING:
                System.out.println("NETWORK: seding...");
                fireEvent(event);
                break;
            case Network.EVENT_RECEIVING:
                System.out.println("NETWORK: receiving...");
                fireEvent(event);
                break;
            case Network.EVENT_END_COMMUNICATION:
                try {
                    System.out.println("NETWORK: data return from server...");
                    ChessDataInputStream in = mNetwork.getDataStreamReturnFromServer();
                    System.out.println("NETWORK: Type of Response: " + in.readShort());
                    System.out.println("NETWORK: Data size: " + in.available());
                    in.close();
                    fireEvent(new Event(event.mType, mNetwork.getDataReturnFromServer()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Network.EVENT_NETWORK_FAILURE:
                System.out.println("NETWORK: failure");
                fireEvent(event);
                break;
        }
    }

    public void renderScreen(Graphics g) {
        if (mCurrentScreen != null) {
            //if (!mIsLoading) 
            {
                mCurrentScreen.paint(g);
            }
        }
    }
    
    public void onKeyPressed(int aKeyCode)
    {
        if (mCurrentScreen != null)
        {
            System.out.println("onKeyPressed " + aKeyCode);
            if (!mIsLoading)
                mCurrentScreen.keyPressed(aKeyCode);
        }
    }
    
    public ScreenInput mInputScreen = null;
    
    public void setDisplayTextBox()
    {   
        fireEvent(new Event(Network.EVENT_TEXTBOX_FOCUS, null));
        Display.getDisplay(mMIDlet).setCurrent(mInputScreen);        
    }
    
    public void setDisplayMainCanvas()
    {
        fireEvent(new Event(Network.EVENT_TEXTBOX_INFOCUS, null));
        Display.getDisplay(mMIDlet).setCurrent(mCanvas);        
    }

    public void onTick(long aMilliseconds) {
        if (!mIsRunning) {
            return;
        }
        try {
            long now = System.currentTimeMillis();

            if (mIsLoggedIn) {
                if (now - mLastReceivedGoodConnect > MAXIMUM_LOST_CONNECTION_PERIOD) {
                    fireEvent(new Event(Network.EVENT_LOSE_CONNECTION, null));
                } else if (now - mLastSendStillOnline > STILL_ONLINE_CYCLE) {
                    ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                    ChessDataOutputStream aOut = new ChessDataOutputStream(aByteArray);
//                    aOut.writeInt(mUserID);
                    aOut.writeString16(new String16(mUsername));
                    aOut.close();
                    mNetwork.sendMessage(Protocol.REQUEST_STILL_ONLINE, aByteArray.toByteArray());
                    mLastSendStillOnline = now;
                }
            }
            mNetwork.onTick(aMilliseconds);

            if (mCurrentScreen != null) 
            {
                //if (!mIsLoading) 
                {
                    mCurrentScreen.onTick(aMilliseconds);
                    if (!mEventQueue.isEmpty()) {
                        if (mCurrentScreen.onEvent((Event) mEventQueue.firstElement())) {
                            mEventQueue.removeElementAt(0);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
