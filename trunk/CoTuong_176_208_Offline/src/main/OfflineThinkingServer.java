/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import ChessBoard.ChessConst;
import xqwlight.Position;
import xqwlight.Search;

/**
 *
 * @author dong
 */
public class OfflineThinkingServer implements Runnable {
    
    protected Position mPosition;
    protected Search mSearch;
    protected int mLevel;    
    protected boolean mIsMyTurn;   
    
    private boolean mIsWorking;
    public ScreenOfflineGamePlay mParent;
        
    public OfflineThinkingServer(ScreenOfflineGamePlay parent, int level, boolean isComputerMoveFirst)
    {
        mPosition = new Position();
        mSearch = new Search(mPosition, 12);
        mLevel = level;
        mPosition.fromFen(Position.STARTUP_FEN[0]);
        mPosition.setIrrev();
        mIsMyTurn = isComputerMoveFirst;
        mIsWorking = false;
        mParent = parent;
    }
    
    public void start()
    {
        mIsWorking = true;
        Thread t = new Thread(this);
        t.start();
    }
    
    public void stop()
    {
        mIsWorking = false;
    }
    
    public void doMove(int srcX, int srcY, int dstX, int dstY)
    {
        System.out.println("Usermove " + srcX + " " + srcY + " " + dstX + " " + dstY);
        
        int mv = Position.MOVE(
                Position.COORD_XY(srcX + 3, srcY + 3), 
                Position.COORD_XY(dstX + 3, dstY + 3));
        
        if (mPosition.legalMove(mv)) {
            if (mPosition.makeMove(mv)) 
            {
                System.out.println("Good move!!!");
                mIsMyTurn = true;
            }
        }
        else
            System.out.println("Wrong user move");
    }

    public void run()
    {
        try {
            while (mIsWorking)
            {                
                if (mIsMyTurn)
                {
                    System.out.println("Server: Oh, it's my turn");
                    int move = mSearch.searchMain(1000 << (mLevel << 1));
                    mPosition.makeMove(move);
                    mParent.doOppMove(
                            ChessConst.BOARD_WIDTH - (Position.FILE_X(Position.SRC(move)) - Position.FILE_LEFT) - 1, 
                            ChessConst.BOARD_HEIGHT - (Position.RANK_Y(Position.SRC(move)) - Position.RANK_TOP) - 1, 
                            ChessConst.BOARD_WIDTH - (Position.FILE_X(Position.DST(move)) - Position.FILE_LEFT) - 1, 
                            ChessConst.BOARD_HEIGHT - (Position.RANK_Y(Position.DST(move)) - Position.RANK_TOP) - 1);
                    mIsMyTurn = false;
                }
                Thread.sleep(2000);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
