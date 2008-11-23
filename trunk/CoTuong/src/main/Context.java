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
import javax.microedition.rms.RecordStore;
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
public class Context //        implements Runnable 
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
    public String mUsername;
    public String mPassword;
    public String mOpponentName;
    public boolean mIsMyTurn;
    public boolean mIsOnlinePlay;
    public int mMatchResult;
    public Network mNetwork;
    public long mLastSendStillOnline;
    public long mLastReceivedGoodConnect;
    public final static long STILL_ONLINE_CYCLE_MAX = 60000;
    public final static long STILL_ONLINE_CYCLE_MIN = 5000;
    public final static long MAXIMUM_LOST_CONNECTION_PERIOD = 120000;
    public boolean mIsLoading = false;
    public MainCanvas mCanvas;
    public int mWidth;
    public int mHeight;
    public Vector mMessageBox;
    public Vector mLobbyList;
    public static String mURL;    
    
    public int mOfflineLevel;
    public int mOfflineColor;
    
    public int mMyWinCount;
    public int mMyLoseCount;
    public int mMyDrawCount;
    
    public int mHisWinCount;
    public int mHisLoseCount;
    public int mHisDrawCount;
    
    public boolean mIsAutoBot = false;
    public boolean mCheatEnable = false;

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
        //http://dongnh.blogdns.net:3664/services/ChessServlet
        mURL = getProperty("Server-URL", "http://localhost:8282/ChessServletV2/ChessServlet");
        String auto = getProperty("Cheat", "0");
        mCheatEnable = auto.equals("1");
    }

    private String getProperty(String name, String defaultVal) {
        String tmp = mMIDlet.getAppProperty(name);
        if (tmp == null || tmp.length() == 0) {
            return defaultVal;
        }
        return tmp;
    }

    public void start() {
        ScreenLoading screenLoading = new ScreenLoading(this);
        screenLoading.setLoadingScript(ScreenLoading.LOADING_SCRIPT_FIRST_INIT);
        setScreen(screenLoading);
        mInputScreen = new ScreenInput(this);
        Display.getDisplay(mMIDlet).setCurrent(mCanvas);
//        mMainThread = new Thread(this);
//        mMainThread.start();                        
        loadDataStore();
        mIsRunning = true;
        mCanvas.start();
    }

    public void loadDataStore() 
    {
        try {
            byte[] aBytes = loadRMSData("COTUONG");
            ByteArrayInputStream aByteArrayInput = new ByteArrayInputStream(aBytes);
            ChessDataInputStream in = new ChessDataInputStream(aByteArrayInput);
            mUsername = in.readString16().toJavaString();
            System.out.println("LOAD: " + mUsername);
        } catch (Exception e)
        {
            mUsername = "";
        }        
    }

    public void saveDataStore() 
    {
        try {
            ByteArrayOutputStream aBytesOut = new ByteArrayOutputStream();
            ChessDataOutputStream out = new ChessDataOutputStream(aBytesOut);
            out.writeString16(new String16(mUsername));
            saveRMSData(aBytesOut.toByteArray(), "COTUONG");
            System.out.println("SAVE: " + mUsername);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Load a byte array from the RMS.
     * An array of bytes is loaded from a given record store in the RMS.
     * The byte array must be located in the first record in the record store.
     * For performance reasons only the first record is used.
     * <p>
     * If a record store with the given name does not exist an exception is
     * thrown. If the record store exists, but it does not contain any records
     * an exception is thrown.
     * 
     * @param recordStoreName record store name
     * @return byte array found in the first record or <code>null</code>
     * @throws NullPointerException if the record store could not be found
     * @throws Exception on any other error
     */
    public static byte[] loadRMSData(String recordStoreName) throws Exception {
        RecordStore recordStore = null;
        byte[] result;

        // Load data from the recordstore
        try {

            // Open a recordstore instance
            recordStore = RecordStore.openRecordStore(recordStoreName, false);

            // If there are no records present the method throws an exception
            if (recordStore == null || recordStore.getNumRecords() < 1) {
                throw new NullPointerException();
            // Read the 1st record from the store.
            // Only the 1st record is used for this record store.
            }
            result = recordStore.getRecord(1);
        } catch (Exception ex) {
            // On any errors rethrow the exception
            throw ex;
        } finally {
            // Clean up, i.e. close the store in any situation.
            // This block is not allowed to throw any exceptions.
            try {
                if (recordStore != null) {
                    recordStore.closeRecordStore();
                }
            } catch (Exception e) {
            }
        }
        // Return the result
        return result;
    }

    /**
     * Save a byte array to the RMS.
     * An array of bytes is saved to a given record store in the RMS.
     * <p>
     * The byte array is stored into the first record in the record store.
     * If the record store does not exist it is created. The contents of the
     * first record of an existing record store are overwritten.
     * 
     * @param data the byte array to save
     * @param recordStoreName record store name
     * @throws Exception on any error
     */
    public static void saveRMSData(byte[] data, String recordStoreName)
            throws Exception {
        RecordStore recordStore = null;

        try {
            // Open the recordstore
            recordStore = RecordStore.openRecordStore(recordStoreName, true);

            // If there are no records present one record is created.
            // The record will have recordid 1.
            if (recordStore.getNumRecords() < 1) {
                recordStore.addRecord(null, 0, 0);
            // Store the data into the 1st record.
            // Only the 1st record is used for this record store.
            }
            recordStore.setRecord(1, data, 0, data.length);
        } catch (Exception ex) {
            // On any errors rethrow the exception
            throw ex;
        } finally {
            // Clean up, i.e. close the store in any situation.
            // This block is not allowed to throw any exceptions.
            try {
                if (recordStore != null) {
                    recordStore.closeRecordStore();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Ensures that the data for a given record store is removed.
     * It is ensured that calling <code>loadRMSData()</code> for the same record
     * store after deleting will throw an exception.
     * <p>
     * This method throws no exceptions.
     * 
     * @param recordStoreName the name of the recordstore to delete
     */
    public static void deleteRMSData(String recordStoreName) {
        /*
         * The implementation here is very simple and it could very easily be
         * implemented application specifically. However, if the Mobile API is
         * transferred to another platform than J2ME we need an interface for this
         * functionality.
         */
        try {
            RecordStore.deleteRecordStore(recordStoreName);
        } catch (Exception e) {
            // Exceptions do not need to be handled.
        }
    }

    public void stop() {
        mIsRunning = false;
        mCanvas.stop();
        saveDataStore();
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

    public void onKeyPressed(int aKeyCode) {
        if (mCurrentScreen != null) {
            System.out.println("onKeyPressed " + aKeyCode);
            if (!mIsLoading) {
                mCurrentScreen.keyPressed(aKeyCode);
            }
        }
    }
    public ScreenInput mInputScreen = null;

    public void setDisplayTextBox() {
        fireEvent(new Event(Network.EVENT_TEXTBOX_FOCUS, null));
        Display.getDisplay(mMIDlet).setCurrent(mInputScreen);
    }

    public void setDisplayMainCanvas() {
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
                } else if (now - mLastSendStillOnline > STILL_ONLINE_CYCLE_MIN) {
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

            if (mCurrentScreen != null) {
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
