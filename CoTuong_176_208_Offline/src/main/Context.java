package main;

import ChessBoard.Chessboard;
import core.ChessDataInputStream;
import core.ChessDataOutputStream;
import core.Event;
import core.MainCanvas;
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
import ui.SoundManager;
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
//    public UnicodeFont mUnicodeFont;
//    public UnicodeFont mUnicodeFontOutline;
//    public UnicodeFont mTahomaFont;
//    public UnicodeFont mTahomaOutline;
    public UnicodeFont mTahomaFontCyan;
    //public UnicodeFont mTahomaFontRed;
    public UnicodeFont mTahomaFontGreen;
    public UnicodeFont mTahomaFontBlue;
    public UnicodeFont mTahomaFontWhite;
    //public UnicodeFont mTahomaOutlineCyan;
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
    public Image mDialogBackground;
    public Image mMenuBackground;
    public Image mMenuBanner;
    public Image mButtonOnImg;
    public Image mButtonOffImg;
    public Image mBarTopImg;
    public Image mBarBottomImg;
    public Image mSoftkeyInfo;
    public Image mChatBoxImg;
    public static Context mMe;
    public boolean mIsLoggedIn;
    //public int mUserID;
    public String mUsername;
    public String mPassword;
    public String mOpponentName;
    public boolean mIsMyTurn;
    public boolean mIsOnlinePlay;
    public int mMatchResult;
    public long mLastSendStillOnline;
    public long mLastReceivedGoodConnect;
    public final static long STILL_ONLINE_CYCLE_MAX = 60000;
    public final static long STILL_ONLINE_CYCLE_MIN = 10000;
    public final static long MAXIMUM_LOST_CONNECTION_PERIOD = 120000;
    public boolean mIsLoading = false;
    public MainCanvas mCanvas;
    public int mWidth;
    public int mHeight;
    
    public Vector mLobbyList;    
    
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
    
    public boolean mTutorialDisable = false;
    public String mVersion;
    public boolean mSoundEnable = true;
    public SoundManager mSoundManager;
    
    public Vector mCurrentMessageVector;
    public Vector mMessageBox;    
    

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
        mCanvas = new MainCanvas(this);
        mLobbyList = new Vector();
        //http://dongnh.blogdns.net:3664/services/ChessServlet                
        String auto = getProperty("Cheat", "0");
        mVersion = getProperty("MIDlet-Version", "");
        mCheatEnable = auto.equals("1");
        mTutorialDisable = false;        
        //mSoundEnable = true;
        mSoundManager = new SoundManager();
        mSoundManager.setActive(true);
        mMessageBox = new Vector();
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
        Display.getDisplay(mMIDlet).setCurrent(mCanvas);
//        mMainThread = new Thread(this);
//        mMainThread.start();                        
        loadDataStore();
        mIsRunning = true;
        mCanvas.start();
    }
    
    public Vector loadSavedGame()
    {
        Vector returnVector = new Vector();
        try {
            byte[] aBytes = loadRMSData("COTUONG_OFFLINE_SG");
            ByteArrayInputStream aByteArrayInput = new ByteArrayInputStream(aBytes);
            ChessDataInputStream in = new ChessDataInputStream(aByteArrayInput);            
            int numberOfGames = in.readInt();
            for (int i = 0; i < numberOfGames; i++)
            {
                BattleVector aBattle = new BattleVector();
                aBattle.mPlayer1 = in.readString16().toJavaString();
                aBattle.mPlayer2 = in.readString16().toJavaString();
                aBattle.mLog = new int[in.readInt()][3];
                for (int movIdx = 0; movIdx < aBattle.mLog.length; movIdx++)
                {
                    aBattle.mLog[movIdx][0] = in.readInt();
                    aBattle.mLog[movIdx][1] = in.readInt();
                    aBattle.mLog[movIdx][2] = in.readInt();
                }
                aBattle.mPlayer1MoveFirst = in.readBoolean();
                aBattle.mTime = in.readLong();
                returnVector.addElement(aBattle);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }   
        return returnVector;
    }
    
    public void updateSavedGame(Vector aVector)
    {
        try {                                    
            ByteArrayOutputStream aBytesOut = new ByteArrayOutputStream();
            ChessDataOutputStream out = new ChessDataOutputStream(aBytesOut);
            out.writeInt(aVector.size());
            for (int i = 0; i < aVector.size(); i++)
            {
                BattleVector aBattle = (BattleVector)aVector.elementAt(i);
                out.writeString16(new String16(aBattle.mPlayer1));
                out.writeString16(new String16(aBattle.mPlayer2));
                out.writeInt(aBattle.mLog.length);
                for (int movIdx = 0; movIdx < aBattle.mLog.length; movIdx++)
                {
                    out.writeInt(aBattle.mLog[movIdx][0]);
                    out.writeInt(aBattle.mLog[movIdx][1]);
                    out.writeInt(aBattle.mLog[movIdx][2]);
                }
                out.writeBoolean(aBattle.mPlayer1MoveFirst);
                out.writeLong(aBattle.mTime);                
            }            
            saveRMSData(aBytesOut.toByteArray(), "COTUONG_OFFLINE_SG");    
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void saveNewGame(String player1, String player2, Chessboard aChessboard, boolean player1movefirst)
    {
        Vector aVector = loadSavedGame();
        try {            
            BattleVector aBattle = new BattleVector();
            aBattle.mPlayer1 = player1;
            aBattle.mPlayer2 = player2;
            aBattle.mLog = new int[aChessboard.moveIdx + 1][3];
            for (int movIdx = 0; movIdx < aBattle.mLog.length; movIdx++)
            {
                aBattle.mLog[movIdx][0] = aChessboard.log[movIdx][0];
                aBattle.mLog[movIdx][1] = aChessboard.log[movIdx][1];
                aBattle.mLog[movIdx][2] = aChessboard.log[movIdx][2];
            }
            aBattle.mPlayer1MoveFirst = player1movefirst;
            aBattle.mTime = System.currentTimeMillis();            
            aVector.addElement(aBattle);
            
            ByteArrayOutputStream aBytesOut = new ByteArrayOutputStream();
            ChessDataOutputStream out = new ChessDataOutputStream(aBytesOut);
            out.writeInt(aVector.size());
            for (int i = 0; i < aVector.size(); i++)
            {
                aBattle = (BattleVector)aVector.elementAt(i);
                out.writeString16(new String16(aBattle.mPlayer1));
                out.writeString16(new String16(aBattle.mPlayer2));
                out.writeInt(aBattle.mLog.length);
                for (int movIdx = 0; movIdx < aBattle.mLog.length; movIdx++)
                {
                    out.writeInt(aBattle.mLog[movIdx][0]);
                    out.writeInt(aBattle.mLog[movIdx][1]);
                    out.writeInt(aBattle.mLog[movIdx][2]);
                }
                out.writeBoolean(aBattle.mPlayer1MoveFirst);
                out.writeLong(aBattle.mTime);                
            }            
            saveRMSData(aBytesOut.toByteArray(), "COTUONG_OFFLINE_SG");    
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void loadDataStore() 
    {
        try {
            byte[] aBytes = loadRMSData("COTUONG_OFFLINE");
            ByteArrayInputStream aByteArrayInput = new ByteArrayInputStream(aBytes);
            ChessDataInputStream in = new ChessDataInputStream(aByteArrayInput);
            mUsername = in.readString16().toJavaString();
            mTutorialDisable = in.readBoolean();
            int numberOfUser = in.readInt();
            for (int userIndex = 0; userIndex < numberOfUser; userIndex++)
            {
                MessageBoxRecord aBox = new MessageBoxRecord();
                aBox.mName = in.readString16().toJavaString();
                int numberOfMessage = in.readInt();
                aBox.mMessageVector = new Vector();
                for (int i = 0; i < numberOfMessage; i++)
                {
                    MessageRecord aMessage = new MessageRecord();
                    aMessage.mSenderName = in.readString16().toJavaString();
                    aMessage.mMessage = in.readString16().toJavaString();
                    aMessage.mIsRead = in.readBoolean();
                    aBox.mMessageVector.addElement(aMessage);
                }
                mMessageBox.addElement(aBox);
            }
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
            out.writeBoolean(mTutorialDisable);
            out.writeInt(mMessageBox.size());
            for (int userIndex = 0; userIndex < mMessageBox.size(); userIndex++)
            {
                MessageBoxRecord box = (MessageBoxRecord)mMessageBox.elementAt(userIndex);
                out.writeString16(new String16(box.mName));
                out.writeInt(box.mMessageVector.size());
                for (int i = 0; i < box.mMessageVector.size(); i++)
                {
                    MessageRecord aMessage = (MessageRecord)box.mMessageVector.elementAt(i);
                    out.writeString16(new String16(aMessage.mSenderName));
                    out.writeString16(new String16(aMessage.mMessage));
                    out.writeBoolean(aMessage.mIsRead);
                }
            }
            saveRMSData(aBytesOut.toByteArray(), "COTUONG_OFFLINE");            
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
        mSoundManager.stop();
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
            //System.out.println("onKeyPressed " + aKeyCode);
            if (!mIsLoading) {
                mCurrentScreen.keyPressed(aKeyCode);
            }
        }
    }    
    

    public void onTick(long aMilliseconds) {
        if (!mIsRunning) {
            return;
        }
        try {
            long now = System.currentTimeMillis();            
            
            mSoundManager.onTick(aMilliseconds);

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

class MessageBoxRecord
{
    public String mName;
    public Vector mMessageVector;
}