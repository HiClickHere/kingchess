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
public class ScreenOfflineGamePlay extends Screen {

    private Position pos = new Position();
    private Search search = new Search(pos, 12);
    private String message;
    private int cursorX,  cursorY;
    private int sqSelected,  mvLast;
    private int handicap;
    private boolean flip;
    private Image imgPieces[];
    private Image imgSelected;
    private Image imgSelected2;
    private Image imgCursor;
    private Image imgCursor2;
    private static final int RESP_CLICK = 0;
    private static final int RESP_ILLEGAL = 1;
    private static final int RESP_MOVE = 2;
    private static final int RESP_MOVE2 = 3;
    private static final int RESP_CAPTURE = 4;
    private static final int RESP_CAPTURE2 = 5;
    private static final int RESP_CHECK = 6;
    private static final int RESP_CHECK2 = 7;
    private static final int RESP_WIN = 8;
    private static final int RESP_DRAW = 9;
    private static final int RESP_LOSS = 10;
    private int level = 0;
    private static final int PHASE_WAITING = 1;
    private static final int PHASE_THINKING = 2;
    private int phase;
    
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

    public ScreenOfflineGamePlay(Context aContext) {
        super(aContext);

        try {
            imgSelected = Image.createImage("/res/img/game/selected.png");
            imgSelected2 = Image.createImage("/res/img/game/selected2.png");
            imgCursor = Image.createImage("/res/img/game/cursor.png");
            imgCursor2 = Image.createImage("/res/img/game/cursor2.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        mContextMenu = new ContextMenu(mContext.mTahomaFontGreen, mContext.mTahomaOutlineGreen);        
        mContextMenu.setColors(0x5f7a7a, 0x708585);
        mContextMenu.addItem(BUTTON_CONTINUE, "Tiếp tục", false,
                            false,
                            20,
                            90,
                            getWidth() >> 1,
                            -1,
                            Graphics.HCENTER | Graphics.VCENTER);        
        mContextMenu.addItem(BUTTON_QUIT, "Thoát ra", false,
                            false,
                            20,
                            90,
                            getWidth() >> 1,
                            -1,
                            Graphics.HCENTER | Graphics.VCENTER);
        mIsOnlinePlay = mContext.mIsOnlinePlay;
        mDialog = new FastDialog(getWidth() - 40, getHeight() - 80);
        
        mDialogVector = new Vector();
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
        imgPieces[8] = mContext.mRedPieces[ScreenGamePlay.PIECE_GENERAL];
        imgPieces[9] = mContext.mRedPieces[ScreenGamePlay.PIECE_GUARD];
        imgPieces[10] = mContext.mRedPieces[ScreenGamePlay.PIECE_ELEPHANT];
        imgPieces[11] = mContext.mRedPieces[ScreenGamePlay.PIECE_CAVALRY];
        imgPieces[12] = mContext.mRedPieces[ScreenGamePlay.PIECE_ROOK];
        imgPieces[13] = mContext.mRedPieces[ScreenGamePlay.PIECE_CANNON];
        imgPieces[14] = mContext.mRedPieces[ScreenGamePlay.PIECE_SOLDIER];
        imgPieces[15] = null;

        imgPieces[16] = mContext.mBlackPieces[ScreenGamePlay.PIECE_GENERAL];
        imgPieces[17] = mContext.mBlackPieces[ScreenGamePlay.PIECE_GUARD];
        imgPieces[18] = mContext.mBlackPieces[ScreenGamePlay.PIECE_ELEPHANT];
        imgPieces[19] = mContext.mBlackPieces[ScreenGamePlay.PIECE_CAVALRY];
        imgPieces[20] = mContext.mBlackPieces[ScreenGamePlay.PIECE_ROOK];
        imgPieces[21] = mContext.mBlackPieces[ScreenGamePlay.PIECE_CANNON];
        imgPieces[22] = mContext.mBlackPieces[ScreenGamePlay.PIECE_SOLDIER];
        imgPieces[23] = null;
        
        if (flip)
        {
            responseMove();
        }
    }

    public void onTick(long aMilliseconds) 
    {
        if (!mDialogVector.isEmpty() && !mIsDisplayDialog) 
            {
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
        System.out.println("paint");
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
        
        if (mIsDisplayDialog)
        {
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
        
        if (phase == PHASE_THINKING)
        {
            g.setColor(0);
            int w = mContext.mTahomaOutlineGreen.getWidth("Suy nghĩ...");
            g.fillRect(getWidth() - w, 0, w, mContext.mTahomaOutlineGreen.getHeight());
            mContext.mTahomaOutlineGreen.write(g, "Suy nghĩ...", getWidth(), 0, Graphics.RIGHT | Graphics.TOP);
        }           
        
        if (mIsDisplayMenu)
        {
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
        if (mIsDisplayDialog)
        {
            switch (keyCode)
            {
                case Key.SOFT_LEFT:
                    dismissDialog();
                    break;
                case Key.SOFT_RIGHT:
                    dismissDialog();
                    ScreenOfflinePlay screenOffline = new ScreenOfflinePlay(mContext);
                    mContext.setScreen(screenOffline);
                    break;
            }
            return;
        }
        
        if (mIsDisplayMenu)
        {
            switch (keyCode)
            {
                case Key.UP:
                    mContextMenu.onDirectionKeys(0);
                    break;
                case Key.DOWN:
                    mContextMenu.onDirectionKeys(1);
                    break;
                case Key.SELECT:
                    switch (mContextMenu.selectedItem())
                    {
                        case BUTTON_CONTINUE:
                            mIsDisplayMenu = false;
                            break;
                        case BUTTON_QUIT:
                            addDialog("Tính năng lưu game chưa hoàn thiện. Bạn thực sự muốn thoát?", SOFTKEY_CANCEL, SOFTKEY_OK, 1);
                            break;
                    }
                    break;
                
            }
            return;
        }
            int deltaX = 0, deltaY = 0;
            switch (keyCode) {
                case Key.SELECT:
                    clickSquare();
                    break;
                case Key.UP:
                    deltaY = -1;
                    break;
                case Key.LEFT:
                    deltaX = -1;
                    break;
                case Key.RIGHT:
                    deltaX = 1;
                    break;
                case Key.DOWN:
                    deltaY = 1;
                    break;
                case Key.SOFT_RIGHT:
                    mIsDisplayMenu = true;
                    break;
            }
            cursorX = (cursorX + deltaX + 9) % 9;
            cursorY = (cursorY + deltaY + 10) % 10;

    }

    private void clickSquare() {
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
    private boolean getResult() {
        return getResult(-1);
    }

    /** Computer Move Result */
    private boolean getResult(int response) {
        if (pos.isMate()) {
            //midlet.playSound(response < 0 ? RESP_WIN : RESP_LOSS);
            message = (response < 0 ? "Chúc mừng! Bạn đã thắng." : "Bạn đã thua...");
            return true;
        }
        int vlRep = pos.repStatus(3);
        if (vlRep > 0) {
            vlRep = (response < 0 ? pos.repValue(vlRep) : -pos.repValue(vlRep));
//			midlet.playSound(vlRep > Position.WIN_VALUE ? RESP_LOSS :
//					vlRep < -Position.WIN_VALUE ? RESP_WIN : RESP_DRAW);
            message = (vlRep > Position.WIN_VALUE ? "Bạn thua" : vlRep < -Position.WIN_VALUE ? "Bạn thắng" : "Hòa");
            return true;
        }
        if (pos.moveNum > 100) {
            //midlet.playSound(RESP_DRAW);
            message = "Cờ hòa";
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

    private boolean addMove(int mv) {
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

    boolean responseMove() {
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
        return !getResult(response);
    }
}
