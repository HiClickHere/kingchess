/*
 * GameScreen.java
 *
 * Created on October 13, 2008, 12:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package main;

//import com.nokia.mid.ui.FullCanvas;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.*;

/**
 *
 * @author dong
 */
//public class GameScreen extends FullCanvas implements Runnable, CommandListener
public class GameScreen extends Canvas implements Runnable, CommandListener
{        
    public static final int ROOK_WHITE = 1;    
    public static final int KNIGHT_WHITE = ROOK_WHITE + 1;
    public static final int BISHOP_WHITE = KNIGHT_WHITE + 1;
    public static final int QUEEN_WHITE = BISHOP_WHITE + 1;
    public static final int KING_WHITE = QUEEN_WHITE + 1;
    public static final int PAWN_WHITE = KING_WHITE + 1;
    
    public static final int ROOK_BLACK = PAWN_WHITE + 1;
    public static final int KNIGHT_BLACK = ROOK_BLACK + 1;
    public static final int BISHOP_BLACK = KNIGHT_BLACK + 1;
    public static final int QUEEN_BLACK = BISHOP_BLACK + 1;
    public static final int KING_BLACK = QUEEN_BLACK + 1;
    public static final int PAWN_BLACK = KING_BLACK + 1;
    
    public Image mPieceImages[];
    public Image mBoard;
    
    public int [][] mChessTable;
    
    int mSelectX;
    int mSelectY;
    
    int mSelectedX;
    int mSelectedY;
    
    public static GameScreen mMe;
    
    public String mHostName;
    public String mClientName;
    
    public static final int TABLE_WIDTH = 144;
    public static final int TABLE_HEIGHT = 144;
    public static final int LIGHT_COLOR = 0xffce9e;
    public static final int DARK_COLOR = 0xd18b47;
    
    public static final int LIGHT_SELECT_COLOR = 0xbec1c1;
    public static final int DARK_SELECT_COLOR = 0xff0000;
    
    private int mSelectCellColor;
    public boolean mIsWhite;
    public boolean mIsWaitingForOpponent;      
    
    public Thread mWaitThread;
    public int mSavedSelectX;
    public int mSavedSelectY;
    
    public Command mBackCommand;    
    
    /**
     * Creates a new instance of GameScreen
     */
    public GameScreen(String host, String client) 
    {        
        mMe = this;
        mHostName = host;
        mClientName = client;
        mChessTable = new int[8][8];
        mSelectX = 0;
        mSelectY = 7;
        mIsWhite = true;
        if (mHostName == ChessMIDlet.mMe.mUsername) {
            mSavedSelectX = mSelectX;
            mSavedSelectY = mSelectY;
            mIsWaitingForOpponent = false;
        }
        else {
            mSavedSelectX = 0;
            mSavedSelectY = 0;
            mSelectX = -1;
            mSelectY = -1;
            mIsWaitingForOpponent = true;
            mWaitThread = new Thread(this);
            mWaitThread.start();
        }
        
        mSelectedX = -1;
        mSelectedY = -1;
        
        mSelectCellColor = 0x00FF00;
        mPieceImages = new Image[PAWN_BLACK];
        
        try {
            mBoard = Image.createImage("/img/board.png");
            for (int i = 0; i < PAWN_BLACK; i++)
                mPieceImages[i] = Image.createImage("/img/" + (i + 1) + ".png");           
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        
        mChessTable[0][0] = ROOK_BLACK;
        mChessTable[0][1] = KNIGHT_BLACK;
        mChessTable[0][2] = BISHOP_BLACK;
        mChessTable[0][3] = QUEEN_BLACK;
        mChessTable[0][4] = KING_BLACK;
        mChessTable[0][5] = BISHOP_BLACK;
        mChessTable[0][6] = KNIGHT_BLACK;
        mChessTable[0][7] = ROOK_BLACK;
        mChessTable[1][0] = PAWN_BLACK;
        mChessTable[1][1] = PAWN_BLACK;
        mChessTable[1][2] = PAWN_BLACK;
        mChessTable[1][3] = PAWN_BLACK;
        mChessTable[1][4] = PAWN_BLACK;
        mChessTable[1][5] = PAWN_BLACK;
        mChessTable[1][6] = PAWN_BLACK;
        mChessTable[1][7] = PAWN_BLACK;
        
        mChessTable[7][0] = ROOK_WHITE;
        mChessTable[7][1] = KNIGHT_WHITE;
        mChessTable[7][2] = BISHOP_WHITE;
        mChessTable[7][3] = QUEEN_WHITE;
        mChessTable[7][4] = KING_WHITE;
        mChessTable[7][5] = BISHOP_WHITE;
        mChessTable[7][6] = KNIGHT_WHITE;
        mChessTable[7][7] = ROOK_WHITE;
        mChessTable[6][0] = PAWN_WHITE;
        mChessTable[6][1] = PAWN_WHITE;
        mChessTable[6][2] = PAWN_WHITE;
        mChessTable[6][3] = PAWN_WHITE;
        mChessTable[6][4] = PAWN_WHITE;
        mChessTable[6][5] = PAWN_WHITE;
        mChessTable[6][6] = PAWN_WHITE;
        mChessTable[6][7] = PAWN_WHITE;                 
        
        setFullScreenMode(true);
        mBackCommand = new Command("Back", Command.BACK, 0);
        addCommand(mBackCommand);
        setCommandListener(this);
    }
    
    public void paint(Graphics g)
    {
        g.setColor(0xFFFFFF);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        int x = (getWidth() - TABLE_WIDTH) >> 1;
        int y = (getHeight() - TABLE_HEIGHT) >> 1;
        
        int color = DARK_COLOR;
        
        //g.drawImage(mBoard, (getWidth() >> 1) - 80 - 7, (getHeight() >> 1) - 80, Graphics.TOP | Graphics.LEFT);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (color == LIGHT_COLOR)
                    color = DARK_COLOR;
                else
                    color = LIGHT_COLOR;
                g.setColor(color);
                g.fillRect(x + j * 18, y + i * 18, 18, 18);
                if (mChessTable[i][j] > 0)
                {
                    g.drawImage(mPieceImages[mChessTable[i][j] - 1], x + j * 18 + 1, y + i * 18 + 1, 0);
                }
            }
            if (color == LIGHT_COLOR)
                color = DARK_COLOR;
            else
                color = LIGHT_COLOR;
        }
        
        //if (mSelectCellColor == LIGHT_SELECT_COLOR)
            mSelectCellColor = DARK_SELECT_COLOR;
        //else
//            mSelectCellColor = LIGHT_SELECT_COLOR;
        g.setColor(mSelectCellColor);
        if (!mIsWaitingForOpponent)
            g.drawRect(x + mSelectX * 18, y + mSelectY * 18, 18, 18);
        
        g.setColor(0x000000);     
        g.drawString(mHostName, getWidth() >> 1, (getHeight() >> 1) + (TABLE_HEIGHT >> 1) + g.getFont().getHeight() + 3, Graphics.BOTTOM | Graphics.HCENTER);
        g.drawString(mClientName, getWidth() >> 1, (getHeight() >> 1) - (TABLE_HEIGHT >> 1) - g.getFont().getHeight() - 3, Graphics.TOP | Graphics.HCENTER);
    }
    
    public int getPieceOnPos(int x, int y)
    {
        return mChessTable[x][y];
    }
    
    public void setPieceToPos(int xSrc, int ySrc, int xDest, int yDest)
    {
        mChessTable[xDest][yDest] = mChessTable[xSrc][ySrc];
        mChessTable[xSrc][ySrc] = 0;
    }
    
    public boolean isARightMove(int xSrc, int ySrc, int xDest, int yDest)
    {
        switch (getPieceOnPos(xSrc, ySrc))
        {
            case ROOK_WHITE:
            case ROOK_BLACK:
                return true;                
            case KNIGHT_WHITE:
            case KNIGHT_BLACK:
                return true;                
            case BISHOP_WHITE:
            case BISHOP_BLACK:
                return true;                
            case QUEEN_WHITE:
            case QUEEN_BLACK:
                return true;                
            case KING_BLACK:
            case KING_WHITE:
                return true;                
            case PAWN_WHITE:
            case PAWN_BLACK:                
                return true;                
            default:
                return true;                
        }
    }
    
    public void move(int xSrc, int ySrc, int xDest, int yDest)
    {
        System.out.println("move: " + xSrc + " " + ySrc + " " + xDest + " " + yDest);
        if (isARightMove(xSrc, ySrc, xDest, yDest))
        {
            setPieceToPos(xSrc, ySrc, xDest, yDest);            
          
            if (!mIsWaitingForOpponent)
            {
                HTTPComms aHTTPComms = new HTTPComms();
                aHTTPComms.SendRequest("t=" + aHTTPComms.REQUEST_MOVE 
                        + "&hn=" + mHostName 
                        + "&cl=" + mClientName 
                        + "&u=" + ChessMIDlet.mMe.mUsername
                        + "&sx=" + xSrc + "&sy=" + ySrc + "&dx=" + xDest + "&dy=" + yDest);
            }
        }
        repaint();
    }    

    public void keyPressed(int keyCode) 
    {
        if (mIsWaitingForOpponent)
            return;
        System.out.println("keyCode: " + keyCode);
        switch (keyCode)
        {
            case UP:
            case -1:
            case Canvas.KEY_NUM2:
                //System.out.println("keyCode: UP");
                if (mSelectY > 0)
                    mSelectY--;
                break;                
            case DOWN:
            case -2:
            case Canvas.KEY_NUM8:
                //System.out.println("keyCode: DOWN");
                if (mSelectY < 7)
                    mSelectY++;
                break;
            case LEFT:
            case -3:
            case Canvas.KEY_NUM4:
                //System.out.println("keyCode: LEFT");
                if (mSelectX > 0)
                    mSelectX--;
                break;
            case RIGHT:
            case -4:
            case Canvas.KEY_NUM6:
                //System.out.println("keyCode: RIGHT");
                if (mSelectX < 7)
                    mSelectX++;
                break;
            case -5:
            case Canvas.KEY_NUM5:
                if (mSelectedX == -1)
                {
                    mSelectedX = mSelectY;
                    mSelectedY = mSelectX;
                } else {
                    move(mSelectedX, mSelectedY, mSelectY, mSelectX);
                    mSelectedX = -1;
                    mSelectedY = -1;
                }
                break;
        }
        repaint();
    }
    
    public void run()
    {
        try {
            while (mIsWaitingForOpponent)
            {                
                repaint();
                HTTPComms aHTTPComms = new HTTPComms();
                aHTTPComms.SendRequest("t=" + aHTTPComms.REQUEST_UPDATE_GAME 
                        + "&u=" + ChessMIDlet.mMe.mUsername
                        + "&p=" + ChessMIDlet.mMe.mPassword
                        + "&hn=" + mHostName 
                        + "&cl=" + mClientName);
                Thread.sleep(5000L);                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void startThread()
    {                
        if (mIsWaitingForOpponent)
        {               
            mSavedSelectX = mSelectX;
            mSavedSelectY = mSelectY;
            mSelectX = -1;
            mSelectX = -1;                
            mWaitThread = new Thread(this);
            mWaitThread.start();
            System.out.println("Thread started.");
         }
    }        

    public void commandAction(Command command, Displayable displayable) {
        if (command == mBackCommand)
        {
            mIsWaitingForOpponent = false;
            HTTPComms aHTTPComms = new HTTPComms();
            aHTTPComms.SendRequest("t=" + aHTTPComms.REQUEST_LEFT_ROOM 
                     + "&u=" + ChessMIDlet.mMe.mUsername
                     + "&p=" + ChessMIDlet.mMe.mPassword
            );
        }
    }
}
