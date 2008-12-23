/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChessBoard;

/**
 *
 * @author Hoand
 */
public class Chessboard {
    /*   0 1 2 3 4 5 6 7 8
     * 0 R-K-E-B-G-B-E-K-R
     * 1 |-|-|-|-|-|-|-|-|
     * 2 |-C-|-|-|-|-|-C-|
     * 3 P-|-P-|-P-|-P-|-P
     * 4 |-|-|-|-|-|-|-|-|
     * 5 |-|-|-|-|-|-|-|-|
     * 6 P-|-P-|-P-|-P-|-P
     * 7 |-C-|-|-|-|-|-C-|
     * 8 |-|-|-|-|-|-|-|-|
     * 9 R-K-E-B-G-B-E-K-R
     */
    protected int[] board;
    public Piece[] pieces; //Dong: change this to public
    public boolean currMove;
    public int[][] log;
    public int moveIdx;
    
    public int mUserColor;
    public final static int COLOR_RED = 0;
    public final static int COLOR_DARK = 1;
    
    public final static int FIELD_TOP = 0;
    public final static int FIELD_BOTTOM = 1;
    
    public boolean mIsEndGame;
    public int mGameResult;
    public String mResultText;
    
    public final static int RESULT_WIN = 0;
    public final static int RESULT_LOSE = 1;
    public final static int RESULT_DRAW = 2;
    public final static int RESULT_NONE = 3;
    
    public Chessboard(){
        initPieces();
    }
    
    //Dong: added this method to init user's color
    public void initGame(boolean moveFirst)
    {
        if (moveFirst)
            mUserColor = COLOR_RED;
        else
            mUserColor = COLOR_DARK;
        
        initPieces();
        resetBoard();
        mIsEndGame = false;
        //moveIdx = 198;
    }
    
    private void initPieces() {
        board = new int[16 * 16];
        // Create class
        pieces = new Piece[32];
        pieces[0] = new General(COLOR_RED); // red general
        pieces[1] = new Bishop(COLOR_RED); // red bishop
        pieces[2] = new Bishop(COLOR_RED); // red bishop
        pieces[3] = new Elephant(COLOR_RED); // red elephant
        pieces[4] = new Elephant(COLOR_RED); // red elephant
        pieces[5] = new Knight(COLOR_RED); // red knight
        pieces[6] = new Knight(COLOR_RED); // red knight
        pieces[7] = new Rook(COLOR_RED); // red rook
        pieces[8] = new Rook(COLOR_RED); // red rook
        pieces[9] = new Canon(COLOR_RED); // red canon
        pieces[10] = new Canon(COLOR_RED); // red canon
        for (int i=0; i<5; i++)
            pieces[11 + i] = new Pawn(COLOR_RED); // red pawn
        
        pieces[16 + 0] = new General(COLOR_DARK); // blue general
        pieces[16 + 1] = new Bishop(COLOR_DARK); // blue bishop
        pieces[16 + 2] = new Bishop(COLOR_DARK); // blue bishop
        pieces[16 + 3] = new Elephant(COLOR_DARK); // blue elephant
        pieces[16 + 4] = new Elephant(COLOR_DARK); // blue elephant
        pieces[16 + 5] = new Knight(COLOR_DARK); // blue knight
        pieces[16 + 6] = new Knight(COLOR_DARK); // blue knight
        pieces[16 + 7] = new Rook(COLOR_DARK); // blue rook
        pieces[16 + 8] = new Rook(COLOR_DARK); // blue rook
        pieces[16 + 9] = new Canon(COLOR_DARK); // blue canon
        pieces[16 + 10] = new Canon(COLOR_DARK); // blue canon
        for (int i=0; i<5; i++)
            pieces[16 + 11 + i] = new Pawn(COLOR_DARK); // blue pawn
        // init log for undo, redo
        log = new int [ChessConst.MAX_MOVE][5];
        for (int i=0; i<ChessConst.MAX_MOVE; i++)
            for (int j=0; j<5; j++)
                log[i][j] = -1;
        moveIdx = 0;
    }
    
    public Chessboard(Chessboard _init) {
        initPieces();
        for (int i=0; i<board.length; i++)
             board[i] = _init.board[i];
        for (int i=0; i<pieces.length; i++) {
            pieces[i].setLocation(_init.pieces[i].getLocation());
        }
    }
    
    public void resetBoard() {
        // reset id
        for (int i=0; i<board.length; i++)
            board[i] = -1;
        // ID on board		
        // xac lap vi tri cua R,K,E,M,K....
        /*   0 1 2 3 4 5 6 7 8
         * 0 R-K-E-B-G-B-E-K-R
         * 1 |-|-|-|-|-|-|-|-|
         * 2 |-C-|-|-|-|-|-C-|
         * 3 P-|-P-|-P-|-P-|-P
         * 4 |-|-|-|-|-|-|-|-|
         * 5 |-|-|-|-|-|-|-|-|
         * 6 P-|-P-|-P-|-P-|-P
         * 7 |-C-|-|-|-|-|-C-|
         * 8 |-|-|-|-|-|-|-|-|
         * 9 R-K-E-B-G-B-E-K-R
         */        
        
        if (mUserColor == COLOR_RED)        
        {            
            board[192 + 3 + 4] = 0; pieces[0].setLocation(192 + 3 + 4);// red general            
            board[192 + 3 + 3] = 1; pieces[1].setLocation(192 + 3 + 3);// red bishop
            board[192 + 3 + 5] = 2; pieces[2].setLocation(192 + 3 + 5);// red bishop
            board[192 + 3 + 2] = 3; pieces[3].setLocation(192 + 3 + 2);// red Elephant
            board[192 + 3 + 6] = 4; pieces[4].setLocation(192 + 3 + 6);// red elephant
            board[192 + 3 + 1] = 5; pieces[5].setLocation(192 + 3 + 1);// red knight
            board[192 + 3 + 7] = 6; pieces[6].setLocation(192 + 3 + 7);// red knight
            board[192 + 3 + 0] = 7; pieces[7].setLocation(192 + 3 + 0);// red rook
            board[192 + 3 + 8] = 8; pieces[8].setLocation(192 + 3 + 8);// red rook

            board[160 + 3 + 1] = 9;  pieces[9].setLocation(160 + 3 + 1);// red canon
            board[160 + 3 + 7] = 10; pieces[10].setLocation(160 + 3 + 7);// red canon
            for (int i=0; i<5; i++) {
                board[144 + 3 + i*2] = i + 11; // red pawn
                pieces[i + 11].setLocation(144 + 3 + i*2);
            }

            board[48 + 3 + 4] = 16 + 0; pieces[16].setLocation(48 + 3 + 4); // Blue general
            board[48 + 3 + 3] = 16 + 1; pieces[17].setLocation(48 + 3 + 3);// Blue bishop
            board[48 + 3 + 5] = 16 + 2; pieces[18].setLocation(48 + 3 + 5);// Blue bishop
            board[48 + 3 + 2] = 16 + 3; pieces[19].setLocation(48 + 3 + 2);// Blue Elephant
            board[48 + 3 + 6] = 16 + 4; pieces[20].setLocation(48 + 3 + 6);// Blue elephant
            board[48 + 3 + 1] = 16 + 5; pieces[21].setLocation(48 + 3 + 1);// Blue knight
            board[48 + 3 + 7] = 16 + 6; pieces[22].setLocation(48 + 3 + 7);// Blue knight
            board[48 + 3 + 0] = 16 + 7; pieces[23].setLocation(48 + 3 + 0);// Blue rook
            board[48 + 3 + 8] = 16 + 8; pieces[24].setLocation(48 + 3 + 8);// Blue rook

            board[80 + 3 + 1] = 16 + 9;  pieces[25].setLocation(80 + 3 + 1);// Blue canon
            board[80 + 3 + 7] = 16 + 10; pieces[26].setLocation(80 + 3 + 7);// Blue canon
            for (int i=0; i<5; i++) {
                board[96 + 3 + i*2] = i + 16 + 11; // Blue pawn
                pieces[i + 27].setLocation(96 + 3 + i*2);
            }
            
            for (int i = 0; i < 16; i++)
            {
                pieces[i].setSide(FIELD_BOTTOM);
                pieces[i + 16].setSide(FIELD_TOP);                
            }
        }
        else
        {
            board[192 + 3 + 4] = 16 + 0; pieces[16 + 0].setLocation(192 + 3 + 4);// red general            
            board[192 + 3 + 3] = 16 + 1; pieces[16 + 1].setLocation(192 + 3 + 3);// red bishop
            board[192 + 3 + 5] = 16 + 2; pieces[16 + 2].setLocation(192 + 3 + 5);// red bishop
            board[192 + 3 + 2] = 16 + 3; pieces[16 + 3].setLocation(192 + 3 + 2);// red Elephant
            board[192 + 3 + 6] = 16 + 4; pieces[16 + 4].setLocation(192 + 3 + 6);// red elephant
            board[192 + 3 + 1] = 16 + 5; pieces[16 + 5].setLocation(192 + 3 + 1);// red knight
            board[192 + 3 + 7] = 16 + 6; pieces[16 + 6].setLocation(192 + 3 + 7);// red knight
            board[192 + 3 + 0] = 16 + 7; pieces[16 + 7].setLocation(192 + 3 + 0);// red rook
            board[192 + 3 + 8] = 16 + 8; pieces[16 + 8].setLocation(192 + 3 + 8);// red rook

            board[160 + 3 + 1] = 16 + 9;  pieces[16 + 9].setLocation(160 + 3 + 1);// red canon
            board[160 + 3 + 7] = 16 + 10; pieces[16 + 10].setLocation(160 + 3 + 7);// red canon
            for (int i=0; i<5; i++) {
                board[144 + 3 + i*2] = i + 16 + 11; // red pawn
                pieces[i + 16 + 11].setLocation(144 + 3 + i*2);
            }

            board[48 + 3 + 4] = 0; pieces[0].setLocation(48 + 3 + 4); // Blue general
            board[48 + 3 + 3] = 1; pieces[1].setLocation(48 + 3 + 3);// Blue bishop
            board[48 + 3 + 5] = 2; pieces[2].setLocation(48 + 3 + 5);// Blue bishop
            board[48 + 3 + 2] = 3; pieces[3].setLocation(48 + 3 + 2);// Blue Elephant
            board[48 + 3 + 6] = 4; pieces[4].setLocation(48 + 3 + 6);// Blue elephant
            board[48 + 3 + 1] = 5; pieces[5].setLocation(48 + 3 + 1);// Blue knight
            board[48 + 3 + 7] = 6; pieces[6].setLocation(48 + 3 + 7);// Blue knight
            board[48 + 3 + 0] = 7; pieces[7].setLocation(48 + 3 + 0);// Blue rook
            board[48 + 3 + 8] = 8; pieces[8].setLocation(48 + 3 + 8);// Blue rook

            board[80 + 3 + 1] = 9;  pieces[9].setLocation(80 + 3 + 1);// Blue canon
            board[80 + 3 + 7] = 10; pieces[10].setLocation(80 + 3 + 7);// Blue canon
            for (int i=0; i<5; i++) {
                board[96 + 3 + i*2] = i + 11; // Blue pawn
                pieces[i + 11].setLocation(96 + 3 + i*2);
            }
            
            for (int i = 0; i < 16; i++)
            {
                pieces[i].setSide(FIELD_TOP);
                pieces[i + 16].setSide(FIELD_BOTTOM);                
            }
        }
        
        //Dong: let all pieces be alive
        for (int i = 0; i < pieces.length; i++)
        {
            pieces[i].mIsDead = false;
        }
        
        // end of reset
        //Dong: move order based on user's color
        if (mUserColor == COLOR_RED)
            currMove = true; // red move
        else
            currMove = false;
    }
    
    //Dong: it seem like we don't need this method, RED always move first
//    public void setFirstMove(boolean side) {
//        currMove = side; // true: red, false: black
//    }
    
    public Piece getPieceAt(int x, int y) {
        return getPieceAt(Position.TO_SQ(x, y));
    }
    
    public Piece getPieceAt(int sq) {
        if (!(sq >= 0 && sq < board.length))
            return null;                 
        if (board[sq] >= 0 && board[sq] < 32)
            return pieces[board[sq]];
        else
            return null;
    }
    
    public boolean canSelect(int x, int y) {
        Piece p = getPieceAt(x, y);
        if (p == null) return false;
        return (p.getColor() == mUserColor && currMove);
    }
    
    public boolean isCheckMate(int _color) {
        Piece general = 
                (pieces[ChessConst.PIECE_GENERAL].getColor() == _color) ?
                    pieces[ChessConst.PIECE_GENERAL]
                    : pieces[16 + ChessConst.PIECE_GENERAL];        
       
       
       for (int i = 0; i < pieces.length; i++) {
           if (!pieces[i].mIsDead && pieces[i].getColor() != _color) {
               int[] move = pieces[i].getAvailableMove(this);
               for (int j = 0; j < move.length; j++)
                   if (move[j] == general.getLocation() && pieces[i].canMoveTo(this, move[j]))
                   {
                       return true;
                   }
           }
       }
       return false;
    }
    
    
    public boolean isEnd(int color) 
    {                
        for (int i=0; i< pieces.length; i++) 
            if (!pieces[i].mIsDead && pieces[i].getColor() == color)
            //if (board[pieces[i].getLocation()] == i) // active
            { 
                int[] move = pieces[i].getAvailableMove(this);
                for (int j=0; j<move.length; j++)
                    if (move[j] != -1) {
                        if (isValidMove(pieces[i].getLocation(), move[j]))
                            return false;
                    }                    
            }
        return true;
    }        
            
    public boolean isValidMove(int src, int dst) {        
        Piece srcPiece = getPieceAt(src);                        
        if (srcPiece == null) return false;
        
        System.out.println("isValidMove_0 " + srcPiece.getColor() + " " + mUserColor + " " + currMove);                
        
        //Dong: check move order
        if (!((srcPiece.getColor() == mUserColor && currMove)
            ||(srcPiece.getColor() != mUserColor && !currMove)))
            return false;
        
        System.out.println("isValidMove_1");
        
        Piece desPiece = getPieceAt(dst);
        if (desPiece != null) {
            if (srcPiece.getColor() == desPiece.getColor())
                return false;
        }
        
        System.out.println("isValidMove_2");
        
        if (srcPiece.canMoveTo(this, dst)) {
            boolean result;
            // try to move
            srcPiece.setLocation(dst);
            int oldPiece = board[dst];
            board[dst] = board[src];
            board[src] = -1;
            // -----------------
            result = isCheckMate(srcPiece.getColor()); // check mate
            // undo move
            board[src] = board[dst];
            board[dst] = oldPiece;
            srcPiece.setLocation(src);
            //currMove = !currMove; // change other player move
            return !result;
        }        
        
        System.out.println("isValidMove_3");
        return false;
    }
    
    protected void saveMove(int src, int dst) {
        log[moveIdx][0] = src;
        log[moveIdx][1] = dst;
        log[moveIdx][2] = board[dst];
        moveIdx++;        
    }
    
    public void undo() {
        if (moveIdx <= 0) return;
        moveIdx--;
        Piece p = getPieceAt(log[moveIdx][1]);
        p.setLocation(log[moveIdx][0]);
        board[log[moveIdx][0]] = board[log[moveIdx][1]];
        board[log[moveIdx][1]] = log[moveIdx][2];
        Piece old_p = getPieceAt(log[moveIdx][1]);
        if (old_p != null)
            old_p.mIsDead = false;        
        currMove = !currMove;
    }
    
    protected void doMove(int src, int dst) {
        saveMove(src, dst); // save log
        
        // Dong: do capture
        if (getPieceAt(dst) != null)
            getPieceAt(dst).mIsDead = true;
        
        Piece srcPiece = getPieceAt(src);
        srcPiece.setLocation(dst);
        board[dst] = board[src];
        board[src] = -1;
        currMove = !currMove; // change other player move
    }
    
    public boolean tryMove(int src, int dst) {
        if (isValidMove(src, dst)) {
            doMove(src, dst);                        
            System.out.println("valid move");
            return true;
        }
        System.out.println("invalid move!!!");
        return false;
    }
    
    public int checkForEndGame()
    {
        if (moveIdx >= ChessConst.MAX_MOVE) 
        {
            mIsEndGame = true;
            return RESULT_DRAW;
        }
        
        if (isEnd(mUserColor)) 
        {
            mIsEndGame = true;
            return RESULT_LOSE;
        }
//        else if (isEnd(1 - mUserColor)) 
//        {
//            mIsEndGame = true;
//            return RESULT_WIN;
//        }
        else 
        {
            mIsEndGame = false;
            return RESULT_NONE;
        }
    }
}
