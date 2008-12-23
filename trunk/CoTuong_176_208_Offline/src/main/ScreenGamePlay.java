/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import ChessBoard.ChessConst;
import ChessBoard.Chessboard;
import ChessBoard.Piece;
import ChessBoard.Position;
import core.Screen;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import ui.ContextMenu;
import ui.FastDialog;
import ui.SoundManager;
import util.Key;
//import xqwlight.Position;
//import xqwlight.Search;

/**
 *
 * @author dong
 */
public class ScreenGamePlay extends Screen {
    
//    public final static int PIECE_GENERAL = 0;
//    public final static int PIECE_GUARD = 1;
//    public final static int PIECE_ELEPHANT = 2;
//    public final static int PIECE_CAVALRY = 3;
//    public final static int PIECE_ROOK = 4;
//    public final static int PIECE_CANNON = 5;
//    public final static int PIECE_SOLDIER = 6;
//    
//    public final static int BOARD_WIDTH = 9;
//    public final static int BOARD_HEIGHT = 10;

//    protected Position pos;// = new Position();
//    protected Search search; // = new Search(pos, 12);
    
    Chessboard mChessBoard;

    protected String message;
    protected int cursorX,  cursorY;
    protected int sqSelected,  mvLast;
    protected int handicap;
    protected boolean flip;    
    protected Image mCursorSelected;
    protected Image mCursorSelected2;
    protected Image mCursor;
    protected Image mCursor2;
    protected Image mHintSquare;
    protected static final int RESP_CLICK = 0;
    protected static final int RESP_ILLEGAL = 1;
    protected static final int RESP_MOVE = 2;
    protected static final int RESP_MOVE2 = 3;
    protected static final int RESP_CAPTURE = 4;
    protected static final int RESP_CAPTURE2 = 5;
    protected static final int RESP_CHECK = 6;
    protected static final int RESP_CHECK2 = 7;
    protected static final int RESP_WIN = 8;
    protected static final int RESP_DRAW = 9;
    protected static final int RESP_LOSS = 10;
    protected int level = 0;
    protected static final int PHASE_WAITING = 1;
    protected static final int PHASE_THINKING = 2;
    protected int phase;
    protected boolean isEndGame;
    public boolean mIsOnlinePlay;
    ContextMenu mContextMenu;
    public boolean mIsDisplayMenu;
    protected Vector mDialogVector;
    protected int mState;
    protected Piece mSelectedPiece;    //Dong: the selected piece
    protected boolean mHintOn;
    protected int mDisplayInfoTick;
    protected int mInfoCase;
    //protected int mTurnDisplayTick;
    protected int mMyXsrc = 0;
    protected int mMyYsrc = 0;
    protected int mMyXdst = 0;
    protected int mMyYdst = 0;
    protected int mOppXsrc = 0;
    protected int mOppYsrc = 0;
    protected int mOppXdst = 0;
    protected int mOppYdst = 0;
    
    public void showNotify(int caseNotify)
    {
        mDisplayInfoTick = 3000;
        mInfoCase = caseNotify;
    }

    public void addDialog(String aString, int leftSoft, int rightSoft, int state) {
        mDialogVector.addElement(new DialogRecord(aString, leftSoft, -1, rightSoft, state));
    }

    public void removeAllDialog() {
        mDialogVector.removeAllElements();
    }

    public void dismissDialog() {
        mIsDisplayDialog = false;
        setSoftKey(-1, -1, -1);
    }    

    public ScreenGamePlay(Context aContext) {
        super(aContext);
        String size = "small";
        if (getWidth() > 212)
           size = "large";
        try {
            mCursorSelected = Image.createImage("/res/img/game/selected.png");
            mCursorSelected2 = Image.createImage("/res/img/game/selected2.png");
            mCursor = Image.createImage("/res/img/game/cursor.png");
            mCursor2 = Image.createImage("/res/img/game/cursor2.png");
            mHintSquare = Image.createImage("/res/img/game/hintsquare.png");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //mIsOnlinePlay = mContext.mIsOnlinePlay;
        mDialog = new FastDialog(getWidth() - 40, getHeight() - 80);

        mDialogVector = new Vector();

        resetGame(); // reset for new game

    }

    public void resetGame() {
//        // reset for new game
//        isEndGame = false;
//        //message = "Chúc mừng! Bạn đã thắng.";// : "Bạn đã thua...";
//        pos = new Position();
//        search = new Search(pos, 12);
        mChessBoard = new Chessboard();        
        cursorX = cursorY = 7;
        mSelectedPiece = null;
        mDisplayInfoTick = 3000;
        mOppXsrc = -1;
        mOppYsrc = -1;
        mOppXdst = -1;
        mOppYdst = -1;
    }

    public void onActivate() {        
//        sqSelected = mvLast = 0;
//        handicap = 0; // không chấp
//
//        level = mContext.mOfflineLevel; // độ khó dễ
//
//        flip = (mContext.mOfflineColor == 0) ? false : true; //không lật bàn cờ, human đi quân đỏ
//
//        pos.fromFen(Position.STARTUP_FEN[handicap]);
//        if (flip) {
//            //pos.changeSide();
//        }
//        pos.setIrrev();
//
//        imgPieces = new Image[24];
//        for (int i = 0; i < 8; i++) {
//            imgPieces[i] = null;
//        }
//        imgPieces[8] = mContext.mRedPieces[PIECE_GENERAL];
//        imgPieces[9] = mContext.mRedPieces[PIECE_GUARD];
//        imgPieces[10] = mContext.mRedPieces[PIECE_ELEPHANT];
//        imgPieces[11] = mContext.mRedPieces[PIECE_CAVALRY];
//        imgPieces[12] = mContext.mRedPieces[PIECE_ROOK];
//        imgPieces[13] = mContext.mRedPieces[PIECE_CANNON];
//        imgPieces[14] = mContext.mRedPieces[PIECE_SOLDIER];
//        imgPieces[15] = null;
//
//        imgPieces[16] = mContext.mBlackPieces[PIECE_GENERAL];
//        imgPieces[17] = mContext.mBlackPieces[PIECE_GUARD];
//        imgPieces[18] = mContext.mBlackPieces[PIECE_ELEPHANT];
//        imgPieces[19] = mContext.mBlackPieces[PIECE_CAVALRY];
//        imgPieces[20] = mContext.mBlackPieces[PIECE_ROOK];
//        imgPieces[21] = mContext.mBlackPieces[PIECE_CANNON];
//        imgPieces[22] = mContext.mBlackPieces[PIECE_SOLDIER];
//        imgPieces[23] = null;        
        mChessBoard.initGame(mContext.mOfflineColor == Chessboard.COLOR_RED);
    }

    public void onTick(long aMilliseconds) {        
        if (!mDialogVector.isEmpty() && !mIsDisplayDialog) {            
            DialogRecord aDialog = (DialogRecord) mDialogVector.elementAt(0);
            mDialogVector.removeElementAt(0);
            mDialog.setText(aDialog.mMessage);
            setSoftKey(aDialog.mLeftSoftkey, -1, aDialog.mRightSoftkey);
            mState = aDialog.mState;
            mIsDisplayDialog = true;
            mDisplayInfoTick = 0;
            mContext.mSoundManager.play(SoundManager.SOUND_MAIL);
        }
    }
    private int left;
    private int top;
    private int squareSize;
    
    protected int mPosibleMove[];

    public void paint(Graphics g) {
        //System.out.println("paint");
        g.setColor(0x708585);        
        g.fillRect(0, 0, getWidth(), getHeight());               
        g.drawImage(mContext.mMenuBackground, getWidth() >> 1, getHeight() >> 1, Graphics.HCENTER | Graphics.VCENTER);
        
        if (mIsDisplayDialog) {
            mDialog.paint(g);
            drawSoftkey(g);
            return;
        }
        
        int x = (getWidth() - mContext.mBoardImage.getWidth()) >> 1;
        int y = (getHeight() - mContext.mBoardImage.getHeight()) >> 1;
        g.drawImage(mContext.mBoardImage, x, y, Graphics.LEFT | Graphics.TOP);        
        x += 1;
        y += 1;

        left = x;
        top = y;
        if (getWidth() > 212 && getHeight() > 250)
            squareSize = 26;
        else
            squareSize = 19;        

//        for (int sq = 0; sq < 256; sq++) {
//            if (Position.IN_BOARD(sq)) {
//                int pc = pos.squares[sq];
//                if (pc > 0) {
//                    drawSquare(g, imgPieces[pc], sq);
//                }
//            }
//        }
//
//        int sqSrc = 0;
//        int sqDst = 0;
//
//        if (mvLast > 0) {
//            sqSrc = Position.SRC(mvLast);
//            sqDst = Position.DST(mvLast);
//            drawSquare(g, (pos.squares[sqSrc] & 8) == 0 ? imgSelected : imgSelected2, sqSrc);
//            drawSquare(g, (pos.squares[sqDst] & 8) == 0 ? imgSelected : imgSelected2, sqDst);
//        } else if (sqSelected > 0) {
//            drawSquare(g, (pos.squares[sqSelected] & 8) == 0 ? imgSelected : imgSelected2, sqSelected);
//        }
//
//        int sq = Position.COORD_XY(cursorX + Position.FILE_LEFT, cursorY + Position.RANK_TOP);
//        if (flip) {
//            sq = Position.SQUARE_FLIP(sq);
//        }
//
//        if (sq == sqSrc || sq == sqDst || sq == sqSelected) {
//            //System.out.println("draw Cursor 1");
//            drawSquare(g, (pos.squares[sq] & Position.SIDE_TAG(pos.sdPlayer)) == 0 ? imgCursor2 : imgCursor, sq);
//        } else {
//            //System.out.println("draw Cursor 2");            
//            drawSquare(g, (pos.squares[sq] & Position.SIDE_TAG(pos.sdPlayer)) == 0 ? imgCursor : imgCursor2, sq);            
//        }
        
        
        for (int i = 0; i < mChessBoard.pieces.length; i++)
        {
            Piece aPiece = mChessBoard.pieces[i];
            if (!aPiece.mIsDead)
            {
                int py = Position.POS_Y(aPiece.getLocation());
                int px = Position.POS_X(aPiece.getLocation());
                
                if (aPiece.getColor() == Chessboard.COLOR_RED) //is red
                {
                    g.drawImage(mContext.mRedPieces[aPiece.getType()], 
                            left + px * squareSize, 
                            top + py * squareSize, 
                            Graphics.HCENTER | Graphics.VCENTER);
                }
                else
                {
                    g.drawImage(mContext.mBlackPieces[aPiece.getType()], 
                            left + px * squareSize, 
                            top + py * squareSize, 
                            Graphics.HCENTER | Graphics.VCENTER);
                }
            }
        }                                 
        
        if (mSelectedPiece != null)
        {
            int px = Position.POS_X(mSelectedPiece.getLocation());
            int py = Position.POS_Y(mSelectedPiece.getLocation());
            g.drawImage(mCursorSelected, 
                    left + px * squareSize, 
                    top + py * squareSize, 
                    Graphics.HCENTER | Graphics.VCENTER);
            if (mHintOn && mPosibleMove != null)
            {
                for (int i = 0; i < mPosibleMove.length; i++)
                    if (mPosibleMove[i] != -1)
                        g.drawImage(mHintSquare, 
                                left + Position.POS_X(mPosibleMove[i]) * squareSize, 
                                top + Position.POS_Y(mPosibleMove[i]) * squareSize, 
                                Graphics.HCENTER | Graphics.VCENTER);
            }
        }   
        
        if (mContext.mIsMyTurn && mOppXsrc != -1)
        {
            g.drawImage(mCursorSelected, 
                    left + (ChessConst.BOARD_WIDTH - mOppXsrc - 1) * squareSize, 
                    top + (ChessConst.BOARD_HEIGHT - mOppYsrc - 1) * squareSize, 
                    Graphics.HCENTER | Graphics.VCENTER);
            g.drawImage(mCursorSelected, 
                    left + (ChessConst.BOARD_WIDTH - mOppXdst - 1) * squareSize, 
                    top + (ChessConst.BOARD_HEIGHT - mOppYdst - 1) * squareSize, 
                    Graphics.HCENTER | Graphics.VCENTER);
        }
        
            g.drawImage(mCursorSelected2, 
                    left + cursorX * squareSize, 
                    top + cursorY * squareSize, 
                    Graphics.HCENTER | Graphics.VCENTER);
        

        if (mIsDisplayMenu) {
            mContextMenu.paint(g, getWidth(), getHeight(), Graphics.RIGHT | Graphics.BOTTOM);
        }
        else
        {
            g.drawImage(mContext.mSoftkeyInfo, 0, getHeight(), Graphics.LEFT | Graphics.BOTTOM);
            g.drawImage(mContext.mArrowDown, getWidth(), getHeight(), Graphics.RIGHT | Graphics.BOTTOM);
        }
    }

    //Dong: I don't like this
//    private void drawSquare(Graphics g, Image image, int sq) {                
//        int sqFlipped = (flip ? Position.SQUARE_FLIP(sq) : sq);
//        int sqX = left + (Position.FILE_X(sqFlipped) - Position.FILE_LEFT) * squareSize;
//        int sqY = top + (Position.RANK_Y(sqFlipped) - Position.RANK_TOP) * squareSize;
//        g.drawImage(image, sqX, sqY, Graphics.HCENTER | Graphics.VCENTER);
//    }

    public void keyPressed(int keyCode) 
    {        
    }        

     //Dong: I don't like this
//    protected void clickSquare() {
//        int sq = Position.COORD_XY(cursorX + Position.FILE_LEFT, cursorY + Position.RANK_TOP);
//        if (flip) {
//            sq = Position.SQUARE_FLIP(sq);
//        }
//        int pc = pos.squares[sq];
//        if ((pc & Position.SIDE_TAG(pos.sdPlayer)) != 0) {
//            //midlet.playSound(RESP_CLICK);
//            mvLast = 0;
//            sqSelected = sq;
//        } else {
//            if (sqSelected > 0 && addMove(Position.MOVE(sqSelected, sq)) && !responseMove()) {
//                //midlet.rsData[0] = 0;
//                //phase = PHASE_EXITTING;
//            }
//            else if (sqSelected > 0)
//            {
//                mContext.mSoundManager.play(SoundManager.SOUND_ILLEGAL);
//            }
//        }
//    }

//    /** Player Move Result */
//    protected boolean getResult() {
//        return getResult(-1);
//    }
//
//    /** Computer Move Result */
//    protected boolean getResult(int response) {
//        if (pos.isMate()) {
//            //midlet.playSound(response < 0 ? RESP_WIN : RESP_LOSS);
//            message = (response < 0 ? "Xin chúc mừng! Bạn đã thắng." : "Quân Tướng của bạn đã bị bắt. Bạn đã thua...");
//            isEndGame = true;
//            return true;
//        }
//        int vlRep = pos.repStatus(3);
//        if (vlRep > 0) {
//            vlRep = (response < 0 ? pos.repValue(vlRep) : -pos.repValue(vlRep));
////			midlet.playSound(vlRep > Position.WIN_VALUE ? RESP_LOSS :
////					vlRep < -Position.WIN_VALUE ? RESP_WIN : RESP_DRAW);
//            message = (vlRep > Position.WIN_VALUE ? "Quân Tướng của bạn đã bị bắt. Bạn đã thua..." : vlRep < -Position.WIN_VALUE ? "Xin chúc mừng! Bạn đã thắng." : "Ván cờ này nên kết thúc ở kết quả hòa.");
//            isEndGame = true;
//            return true;
//        }
//        if (pos.moveNum > 100) {
//            //midlet.playSound(RESP_DRAW);
//            message = "Ván cờ đã vượt quá số nước đi quy định mà không phân định được thắng thua. Ván cờ kết thúc hòa.";
//            isEndGame = true;
//            return true;
//        }
//        if (response >= 0) {
//            //midlet.playSound(response);
//            // Backup Retract Status
//            //System.arraycopy(midlet.rsData, 0, retractData, 0, XQWLMIDlet.RS_DATA_LEN);
//            // Backup Record-Score Data
//            //midlet.rsData[0] = 1;
//            //System.arraycopy(pos.squares, 0, midlet.rsData, 256, 256);
//        }
//        return false;
//    }

//    protected boolean addMove(int mv) {
//        if (pos.legalMove(mv)) {
//            if (pos.makeMove(mv)) {
////				midlet.playSound(pos.inCheck() ? RESP_CHECK :
////						pos.captured() ? RESP_CAPTURE : RESP_MOVE);
//                int playSound;
//                if (pos.captured()) {
//                    playSound = SoundManager.SOUND_MOVE;
//                    pos.setIrrev();
//                }
//                else 
//                    playSound = SoundManager.SOUND_MOVE;
//                // check my general
//                if (pos.checked())
//                    playSound = SoundManager.SOUND_CHECK;
//                
//                mContext.mSoundManager.play(playSound);
//                
//                sqSelected = 0;
//                mvLast = mv;
//                return true;
//            }
////			midlet.playSound(RESP_ILLEGAL);
//        }
//        return false;
//    }
//
//    protected boolean responseMove() {
//        if (getResult()) {
//            return false;
//        }
//        phase = PHASE_THINKING;
//        mContext.mCanvas.repaint();
//        mContext.mCanvas.serviceRepaints();
//        mvLast = search.searchMain(1000 << (level << 1));
//        pos.makeMove(mvLast);
//        int response = pos.inCheck() ? RESP_CHECK2 : pos.captured() ? RESP_CAPTURE2 : RESP_MOVE2;
//        
//        int playSound;
//        if (pos.captured()) {
//            playSound = SoundManager.SOUND_MOVE;
//            pos.setIrrev();
//        }
//        else 
//            playSound = SoundManager.SOUND_MOVE;
//        // check my general
//        if (pos.checked())
//            playSound = SoundManager.SOUND_CHECK;
//        
//        mContext.mSoundManager.play(playSound);
//        
//        phase = PHASE_WAITING;
//        mContext.mCanvas.repaint();
//        mContext.mCanvas.serviceRepaints();
//        boolean result = !getResult(response);
//        return result;
//    }
}
