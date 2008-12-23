package ui;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

/**
 * mlib2_SoundManager<br>
 *
 * Platform-independent interface for managing sound clips. The types of sound currently handed
 * are WAV and AMR.
 */
public class SoundManager implements PlayerListener {
    
    public final static int NUMBER_SOUNDS = 7;
    public final static int SOUND_MOVE = 0;
    public final static int SOUND_ILLEGAL = 1;
    public final static int SOUND_CHECK = 2;
    public final static int SOUND_EAT = 3;
    public final static int SOUND_THEME = 4;
    public final static int SOUND_BELL = 5;
    public final static int SOUND_MAIL = 6;

    // True if the manager is active.
    private boolean mIsActive;
    // Vector of sound names.  These names uniquely identify midis.
    // NOTE: the strings are compared at the object level, not string compared!
    // The same exact object must be used to load/unload/play a sound!
    //private /*Vector*/String[] mNames;
    // Vector of sound players.  One for each sound.
//    private /*Vector*/Player[] mPlayers ;
    byte[][] mSoundData = null;
    private boolean mIsIdle;
    private boolean mIsLooping;
    Player mCurPlayer;
    Player mPlayers[];
    //String mCurrentSound;
    int mCurrentSoundID = -1;

    // Constructor.
    /**
     * Creates a SoundManager object
     */
    public SoundManager() {
        mCurrentSoundID = -1;
        //mNames = new String[16];//new Vector();
//        mPlayers = new Player[16];//new Vector();
        int numSound = getNumberOfSounds();
        mSoundData = new byte[numSound][];
        mPlayers = new Player[numSound];
        for (int i = 0; i < numSound; i++) {
            //mNames[i] = null;
//            mPlayers[i] = null;
            mSoundData[i] = null;
            mPlayers[i] = null;
        }
        //mCurrentSound = "";
        mIsActive = false;
        mIsIdle = true;
        mCurPlayer = null;
    }

    public int getNumberOfSounds() {
        return NUMBER_SOUNDS;
    }

    private Player createSound(int soundID, int loopCount, String playerType) {
        if (mSoundData[soundID] == null) {
            load(soundID);
        }
        if (mSoundData[soundID] == null) {
            return null;
        }
        if (mPlayers[soundID] != null && mPlayers[soundID].getState() != Player.CLOSED) {
            return mPlayers[soundID];
        }
        Player player = null;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(mSoundData[soundID]);

            player = Manager.createPlayer(in, playerType);

            // Preload the player.
            player.realize();
            player.prefetch();    // causes an exception on the emulator: "Symbian OS error: -1"    

            player.setLoopCount(loopCount);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPlayers[soundID] = player;
        return player;
    }
    // Load a sound.

    /**
     * Loads the sound with the given file name
     *
     * @param aFilename the filename of the sound file to be loaded
     */
    public void load(int soundID) {
        System.out.println("LoadSound: " + soundID);
        String aFilename = getSoundName(soundID);
        if (aFilename == null) 
        {
            return;
        }
        
        Player player = null;
        try {
            // Create the player.
            if (mSoundData[soundID] == null) {
                InputStream is = getClass().getResourceAsStream("/sounds/" + aFilename);

                if (is == null) {
                    int kk = 0;
                }
                mSoundData[soundID] = new byte[is.available()];


                is.read(mSoundData[soundID]);
                is.close();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
 
    public String getSoundName(int soundID)
    {
        switch(soundID){
            case SOUND_MOVE:
                return "move.wav";
            case SOUND_ILLEGAL:
                return "illegal.wav";
            case SOUND_CHECK:
                return "check.mid";
            case SOUND_EAT:
                return "capture.wav";
            case SOUND_THEME:
                return "theme.mid";
            case SOUND_BELL:
                return "bell_ring.mid";
            case SOUND_MAIL:
                return "mail.mid";
        }
        return null;
    }

    public void loadMidi(int soundID) 
    {
        load(soundID);
    }


    // Play a sound.
    /**
     * Plays a sound if it is loaded.
     *
     * @param aFilename the filename of the file to be played
     */
    public void play(int soundID) {
        System.out.println("PlaySound: " + soundID);
//        if (soundID == SOUND_EAT)
//            return;
        play(soundID, 1);
    }
    // set static by Tien Phan to fixed ROS_J2ME-326
    public static boolean mIsPlaying = false;

    public void play(int soundID, int loopCount) {
        if (!mIsActive) 
        {
            return;
        }
        String aFilename = null;
        aFilename = getSoundName(soundID);
        if (aFilename == null) {
            stop();//Tuan Bui: stop the current sound anyways

            return;
        }
        if (mIsActive) {
            if (soundID == mCurrentSoundID) {//Tuan Bui: removed loopCount == -1 && 

                if (mCurPlayer != null && mCurPlayer.getState() == Player.STARTED) //if(mCurPlayer!=null && mCurPlayer.getState() == Player.STARTED)
                {
                    return;
                }
            }
            stop();
            try {
                Thread.sleep(50);//tuan Bui, changed 500->50, ROS_J2ME-398

            } catch (Exception e) {
            }
            mCurPlayer = createSound(soundID, loopCount, getMimeType(aFilename));
            try {
                if (mCurPlayer != null) {
                    if (loopCount == -1) {
                        mIsLooping = true;
                    } else {
                        mIsLooping = false;
                    }
                    mCurPlayer.setMediaTime(0);
                    mCurPlayer.prefetch();//Tuan Bui
                    mCurrentSoundID = soundID;
                    mCurPlayer.start();
                    mIsPlaying = true;
                }
            } catch (Throwable e) {
                mCurPlayer = null;
            }
        }
    }

    // Loop a sound until stopped.
    /**
     * Loops a sound infinitely until stopped by SoundManager. This function is
     * appropriate for background music that is usally in midi format
     *
     * @param aFilename the filename of the sound to be looped
     */
    public void loop(int soundID) {
        play(soundID, -1);
    }

    // Stop playing.
    /**
     * Stops playing all sounds. This is usually used to stop the background music
     * which has been playing in a loop.
     */
    public void stop() {
        try {
            mIsIdle = true;
            mIsLooping = false;
            if (mCurPlayer != null) 
            {
                mCurPlayer.stop();                
                Thread.sleep(100);
//                mCurPlayer.setMediaTime(0);
                mIsPlaying = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Test if the sound is on.
    /**
     * Determines whether the SoundManager is active or not
     *
     * @return true if active, false otherwise.
     */
    public boolean isActive() 
    {
        return mIsActive;
    }

    // Turn sound on/off.
    /**
     * Sets the state of the SoundManager to active or dormant.
     *
     * @param aIsActive sets the SoundManager to active if true, dormant if false
     */
    public void setActive(boolean aIsActive) {
        mIsActive = aIsActive;

        if (!mIsActive) {
            stop();
        }
    }

    // Suspend all sounds.
    /**
     * Suspends all sounds. Used for when the application is interrupted by the phone.
     */
    public void suspend() {
        try {
            mIsIdle = true;
            if (mCurPlayer != null)
            {
                mCurPlayer.setMediaTime(0);
                mCurPlayer.stop();
                if (mCurPlayer != null) {
                    mCurPlayer.close();
                }
                if (mCurrentSoundID >= 0) {
                    mPlayers[mCurrentSoundID] = null;//                
                }
            }

        } catch (Throwable e) 
        {
        } 
        finally 
        {
            mCurPlayer = null;
            mIsPlaying = false;
        }
    }

    synchronized public void onTick(long aMilliseconds) {
        if (!isActive()) {
            return;
        }
        if (mSoundQueue.size() > 0) {
            if (mCurPlayer == null || (mCurPlayer != null && mCurPlayer.getState() != Player.STARTED)) {
                int soundID = ((Integer) mSoundQueue.elementAt(mSoundQueue.size() - 1)).intValue();
                mSoundQueue.removeElementAt(mSoundQueue.size() - 1);
                boolean isLoop = ((Boolean) mSoundQueueLoop.elementAt(mSoundQueueLoop.size() - 1)).booleanValue();
                mSoundQueueLoop.removeElementAt(mSoundQueueLoop.size() - 1);
                play(soundID, isLoop ? -1 : 1);
            }
        }
    }

    // Return the mime type of the file.
    private String getMimeType(String aFilename) {
        String extension = aFilename.substring(aFilename.length() - 3, aFilename.length()).toUpperCase();

        if (extension.equals("AMR")) {
            return "audio/AMR";
        }
        //DuongNT add
        if (extension.equals("WAV")) {
            return "audio/X-WAV";
        } else {
            return "audio/midi";
        }
    }

    public boolean isIdle() {
        return mIsIdle;
    }

    public void playerUpdate(Player player, String string, Object object) 
    {
    }

    public void unloadSound(int aSoundID) 
    {
    }
    
    private Vector mSoundQueue = new Vector();
    private Vector mSoundQueueLoop = new Vector();

    public void pushSound(int soundID, boolean isLoop) {
        if (!isActive()) {
            return;
        }
        if (getSoundName(soundID) == null) {
            return;
        }
        mSoundQueue.addElement(new Integer(soundID));
        mSoundQueueLoop.addElement(new Boolean(isLoop));
    }

    public void clearStack() {
        mSoundQueue.removeAllElements();
        mSoundQueueLoop.removeAllElements();
    }
}
