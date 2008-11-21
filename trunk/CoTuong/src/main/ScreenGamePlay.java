/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import core.Screen;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import ui.ContextMenu;
import ui.FastDialog;
import util.Key;
import xqwlight.Position;
import xqwlight.Search;

/**
 *
 * @author dong
 */
public class ScreenGamePlay extends Screen {

    protected Position pos;// = new Position();
    protected Search search; // = new Search(pos, 12);

    protected String message;
    protected int cursorX,  cursorY;
    protected int sqSelected,  mvLast;
    protected int handicap;
    protected boolean flip;
    protected Image imgPieces[];
    protected Image imgSelected;
    protected Image imgSelected2;
    protected Image imgCursor;
    protected Image imgCursor2;
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
    public final static int BUTTON_CONTINUE = 0;
    public final static int BUTTON_SEND_MESSAGE = 1;
    public final static int BUTTON_REQUEST_DRAW_GAME = 2;
    public final static int BUTTON_LEFT_GAME = 3;
    public final static int BUTTON_QUIT = 4;

    public ScreenGamePlay(Context aContext) {
        super(aContext);

        try {
            imgSelected = Image.createImage("/res/img/game/selected.png");
            imgSelected2 = Image.createImage("/res/img/game/selected2.png");
            imgCursor = Image.createImage("/res/img/game/cursor.png");
            imgCursor2 = Image.createImage("/res/img/game/cursor2.png");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //mIsOnlinePlay = mContext.mIsOnlinePlay;
        mDialog = new FastDialog(getWidth() - 40, getHeight() - 80);

        mDialogVector = new Vector();

        resetGame(); // reset for new game

    }

    public void resetGame() {
        // reset for new game
        isEndGame = false;
        //message = "Chúc mừng! Bạn đã thắng.";// : "Bạn đã thua...";
        pos = new Position();
        search = new Search(pos, 12);
    }

    public void onActivate() {
        cursorX = cursorY = 7;
        sqSelected = mvLast = 0;
        handicap = 0; // không chấp

        level = mContext.mOfflineLevel; // độ khó dễ

        flip = (mContext.mOfflineColor == 0) ? false : true; //không lật bàn cờ, human đi quân đỏ

        pos.fromFen(Position.STARTUP_FEN[handicap]);
        if (flip) {
            //pos.changeSide();
        }
        pos.setIrrev();

        imgPieces = new Image[24];
        for (int i = 0; i < 8; i++) {
            imgPieces[i] = null;
        }
        imgPieces[8] = mContext.mRedPieces[ScreenOnlineGamePlay.PIECE_GENERAL];
        imgPieces[9] = mContext.mRedPieces[ScreenOnlineGamePlay.PIECE_GUARD];
        imgPieces[10] = mContext.mRedPieces[ScreenOnlineGamePlay.PIECE_ELEPHANT];
        imgPieces[11] = mContext.mRedPieces[ScreenOnlineGamePlay.PIECE_CAVALRY];
        imgPieces[12] = mContext.mRedPieces[ScreenOnlineGamePlay.PIECE_ROOK];
        imgPieces[13] = mContext.mRedPieces[ScreenOnlineGamePlay.PIECE_CANNON];
        imgPieces[14] = mContext.mRedPieces[ScreenOnlineGamePlay.PIECE_SOLDIER];
        imgPieces[15] = null;

        imgPieces[16] = mContext.mBlackPieces[ScreenOnlineGamePlay.PIECE_GENERAL];
        imgPieces[17] = mContext.mBlackPieces[ScreenOnlineGamePlay.PIECE_GUARD];
        imgPieces[18] = mContext.mBlackPieces[ScreenOnlineGamePlay.PIECE_ELEPHANT];
        imgPieces[19] = mContext.mBlackPieces[ScreenOnlineGamePlay.PIECE_CAVALRY];
        imgPieces[20] = mContext.mBlackPieces[ScreenOnlineGamePlay.PIECE_ROOK];
        imgPieces[21] = mContext.mBlackPieces[ScreenOnlineGamePlay.PIECE_CANNON];
        imgPieces[22] = mContext.mBlackPieces[ScreenOnlineGamePlay.PIECE_SOLDIER];
        imgPieces[23] = null;

//        if (flip)
//        {
//            responseMove();
//        }
    }

    public void onTick(long aMilliseconds) {        
        if (!mDialogVector.isEmpty() && !mIsDisplayDialog) {            
            DialogRecord aDialog = (DialogRecord) mDialogVector.elementAt(0);
            mDialogVector.removeElementAt(0);
            mDialog.setText(aDialog.mMessage);
            setSoftKey(aDialog.mLeftSoftkey, -1, aDialog.mRightSoftkey);
            mState = aDialog.mState;
            mIsDisplayDialog = true;
        }
    }
    private int left;
    private int top;
    private int squareSize;

    public void paint(Graphics g) {
        //System.out.println("paint");
        g.setColor(0x708585);
        g.fillRect(0, 0, getWidth(), getHeight());
        int x = (getWidth() - mContext.mBoardImage.getWidth()) >> 1;
        int y = (getHeight() - mContext.mBoardImage.getHeight()) >> 1;
        g.drawImage(mContext.mBoardImage, x, y, Graphics.LEFT | Graphics.TOP);
        x += 1;
        y += 1;

        left = x;
        top = y;
        squareSize = 19;

        if (mIsDisplayDialog) {
            mDialog.paint(g);
            drawSoftkey(g);
            return;
        }

        for (int sq = 0; sq < 256; sq++) {
            if (Position.IN_BOARD(sq)) {
                int pc = pos.squares[sq];
                if (pc > 0) {
                    drawSquare(g, imgPieces[pc], sq);
                }
            }
        }

        int sqSrc = 0;
        int sqDst = 0;

        if (mvLast > 0) {
            sqSrc = Position.SRC(mvLast);
            sqDst = Position.DST(mvLast);
            drawSquare(g, (pos.squares[sqSrc] & 8) == 0 ? imgSelected : imgSelected2, sqSrc);
            drawSquare(g, (pos.squares[sqDst] & 8) == 0 ? imgSelected : imgSelected2, sqDst);
        } else if (sqSelected > 0) {
            drawSquare(g, (pos.squares[sqSelected] & 8) == 0 ? imgSelected : imgSelected2, sqSelected);
        }

        int sq = Position.COORD_XY(cursorX + Position.FILE_LEFT, cursorY + Position.RANK_TOP);
        if (flip) {
            sq = Position.SQUARE_FLIP(sq);
        }
        if (sq == sqSrc || sq == sqDst || sq == sqSelected) {
            //System.out.println("draw Cursor 1");
            drawSquare(g, (pos.squares[sq] & 8) == 0 ? imgCursor2 : imgCursor, sq);
        } else {
            //System.out.println("draw Cursor 2");
            drawSquare(g, (pos.squares[sq] & 8) == 0 ? imgCursor : imgCursor2, sq);
        }

        if (mIsDisplayMenu) {
            mContextMenu.paint(g, getWidth(), getHeight(), Graphics.RIGHT | Graphics.BOTTOM);
        }
    }

    private void drawSquare(Graphics g, Image image, int sq) {
        int sqFlipped = (flip ? Position.SQUARE_FLIP(sq) : sq);
        int sqX = left + (Position.FILE_X(sqFlipped) - Position.FILE_LEFT) * squareSize;
        int sqY = top + (Position.RANK_Y(sqFlipped) - Position.RANK_TOP) * squareSize;
        g.drawImage(image, sqX, sqY, Graphics.HCENTER | Graphics.VCENTER);
    }

    public void keyPressed(int keyCode) {
    }

    protected void clickSquare() {
        int sq = Position.COORD_XY(cursorX + Position.FILE_LEFT, cursorY + Position.RANK_TOP);
        if (flip) {
            sq = Position.SQUARE_FLIP(sq);
        }
        int pc = pos.squares[sq];
        if ((pc & Position.SIDE_TAG(pos.sdPlayer)) != 0) {
            //midlet.playSound(RESP_CLICK);
            mvLast = 0;
            sqSelected = sq;
        } else {
            if (sqSelected > 0 && addMove(Position.MOVE(sqSelected, sq)) && !responseMove()) {
                //midlet.rsData[0] = 0;
                //phase = PHASE_EXITTING;
            }
        }
    }

    /** Player Move Result */
    protected boolean getResult() {
        return getResult(-1);
    }

    /** Computer Move Result */
    protected boolean getResult(int response) {
        if (pos.isMate()) {
            //midlet.playSound(response < 0 ? RESP_WIN : RESP_LOSS);
            message = (response < 0 ? "Xin chúc mừng! Bạn đã thắng." : "Quân Tướng của bạn đã bị bắt. Bạn đã thua...");
            isEndGame = true;
            return true;
        }
        int vlRep = pos.repStatus(3);
        if (vlRep > 0) {
            vlRep = (response < 0 ? pos.repValue(vlRep) : -pos.repValue(vlRep));
//			midlet.playSound(vlRep > Position.WIN_VALUE ? RESP_LOSS :
//					vlRep < -Position.WIN_VALUE ? RESP_WIN : RESP_DRAW);
            message = (vlRep > Position.WIN_VALUE ? "Quân Tướng của bạn đã bị bắt. Bạn đã thua..." : vlRep < -Position.WIN_VALUE ? "Xin chúc mừng! Bạn đã thắng." : "Ván cờ này nên kết thúc ở kết quả hòa.");
            isEndGame = true;
            return true;
        }
        if (pos.moveNum > 100) {
            //midlet.playSound(RESP_DRAW);
            message = "Ván cờ đã vượt quá số nước đi quy định mà không phân định được thắng thua. Ván cờ kết thúc hòa.";
            isEndGame = true;
            return true;
        }
        if (response >= 0) {
            //midlet.playSound(response);
            // Backup Retract Status
            //System.arraycopy(midlet.rsData, 0, retractData, 0, XQWLMIDlet.RS_DATA_LEN);
            // Backup Record-Score Data
            //midlet.rsData[0] = 1;
            //System.arraycopy(pos.squares, 0, midlet.rsData, 256, 256);
        }
        return false;
    }

    protected boolean addMove(int mv) {
        if (pos.legalMove(mv)) {
            if (pos.makeMove(mv)) {
//				midlet.playSound(pos.inCheck() ? RESP_CHECK :
//						pos.captured() ? RESP_CAPTURE : RESP_MOVE);
                if (pos.captured()) {
                    pos.setIrrev();
                }
                sqSelected = 0;
                mvLast = mv;
                return true;
            }
//			midlet.playSound(RESP_ILLEGAL);
        }
        return false;
    }

    protected boolean responseMove() {
        if (getResult()) {
            return false;
        }
        phase = PHASE_THINKING;
        mContext.mCanvas.repaint();
        mContext.mCanvas.serviceRepaints();
        mvLast = search.searchMain(1000 << (level << 1));
        pos.makeMove(mvLast);
        int response = pos.inCheck() ? RESP_CHECK2 : pos.captured() ? RESP_CAPTURE2 : RESP_MOVE2;
        if (pos.captured()) {
            pos.setIrrev();
        }
        phase = PHASE_WAITING;
        mContext.mCanvas.repaint();
        mContext.mCanvas.serviceRepaints();
        boolean result = !getResult(response);
        return result;
    }
}
