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
    protected int[][] board;
    protected Piece[] pieces;
    protected boolean currMove;
    protected int[][] log;
    protected int moveIdx;
    
    public Chessboard(){
        initPieces();
    }
    
    private void initPieces() {
        board = new int[10][9];
        // Create class
        pieces = new Piece[32];
        pieces[0] = new General(true); // red general
        pieces[1] = new Bishop(true); // red bishop
        pieces[2] = new Bishop(true); // red bishop
        pieces[3] = new Elephant(true); // red elephant
        pieces[4] = new Elephant(true); // red elephant
        pieces[5] = new Knight(true); // red knight
        pieces[6] = new Knight(true); // red knight
        pieces[7] = new Rook(true); // red rook
        pieces[8] = new Rook(true); // red rook
        pieces[9] = new Canon(true); // red canon
        pieces[10] = new Canon(true); // red canon
        for (int i=0; i<5; i++)
            pieces[11 + i] = new Pawn(true); // red pawn
        
        pieces[16 + 0] = new General(false); // blue general
        pieces[16 + 1] = new Bishop(false); // blue bishop
        pieces[16 + 2] = new Bishop(false); // blue bishop
        pieces[16 + 3] = new Elephant(false); // blue elephant
        pieces[16 + 4] = new Elephant(false); // blue elephant
        pieces[16 + 5] = new Knight(false); // blue knight
        pieces[16 + 6] = new Knight(false); // blue knight
        pieces[16 + 7] = new Rook(false); // blue rook
        pieces[16 + 8] = new Rook(false); // blue rook
        pieces[16 + 9] = new Canon(false); // blue canon
        pieces[16 + 10] = new Canon(false); // blue canon
        for (int i=0; i<5; i++)
            pieces[16 + 11 + i] = new Pawn(false); // blue pawn
        // init log for undo, redo
        log = new int [ChessConst.MAX_MOVE][5];
        for (int i=0; i<ChessConst.MAX_MOVE; i++)
            for (int j=0; j<5; j++)
                log[i][j] = -1;
        moveIdx = 0;
    }
    
    public Chessboard(Chessboard _init) {
        initPieces();
        for (int i=0; i<ChessConst.BOARD_HEIGHT; i++)
            for (int j=0; j<ChessConst.BOARD_WIDTH; j++)
                board[i][j] = _init.board[i][j];
        for (int i=0; i<pieces.length; i++) {
            pieces[i].setLocation(_init.pieces[i].getLocation().x, 
                        _init.pieces[i].getLocation().y);
        }
    }
    
    public void resetBoard() {
        // reset id
        for (int i=0; i<10; i++)
            for (int j=0; j<9; j++)
                    board[i][j] = -1;
        // ID on board		
        // xac lap vi tri cua R,K,E,M,K....
        board[9][4] = 0; pieces[0].setLocation(4, 9); // red general
        board[9][3] = 1; pieces[1].setLocation(3, 9);// red bishop
        board[9][5] = 2; pieces[2].setLocation(5, 9);// red bishop
        board[9][2] = 3; pieces[3].setLocation(2, 9);// red Elephant
        board[9][6] = 4; pieces[4].setLocation(6, 9);// red elephant
        board[9][1] = 5; pieces[5].setLocation(1, 9);// red knight
        board[9][7] = 6; pieces[6].setLocation(7, 9);// red knight
        board[9][0] = 7; pieces[7].setLocation(0, 9);// red rook
        board[9][8] = 8; pieces[8].setLocation(8, 9);// red rook
        
        board[7][1] = 9; pieces[9].setLocation(1, 7);// red canon
        board[7][7] = 10; pieces[10].setLocation(7, 7);// red canon
        for (int i=0; i<5; i++) {
            board[6][i*2] = i + 11; // red pawn
            pieces[i + 11].setLocation(i*2, 6);
        }
        
        board[0][4] = 16 + 0; pieces[16].setLocation(4, 0); // Blue general
        board[0][3] = 16 + 1; pieces[17].setLocation(3, 0);// Blue bishop
        board[0][5] = 16 + 2; pieces[18].setLocation(5, 0);// Blue bishop
        board[0][2] = 16 + 3; pieces[19].setLocation(2, 0);// Blue Elephant
        board[0][6] = 16 + 4; pieces[20].setLocation(6, 0);// Blue elephant
        board[0][1] = 16 + 5; pieces[21].setLocation(1, 0);// Blue knight
        board[0][7] = 16 + 6; pieces[22].setLocation(7, 0);// Blue knight
        board[0][0] = 16 + 7; pieces[23].setLocation(0, 0);// Blue rook
        board[0][8] = 16 + 8; pieces[24].setLocation(8, 0);// Blue rook
        
        board[2][1] = 16 + 9;  pieces[25].setLocation(1, 2);// Blue canon
        board[2][7] = 16 + 10; pieces[26].setLocation(7, 2);// Blue canon
        for (int i=0; i<5; i++) {
            board[3][i*2] = i + 16 + 11; // Blue pawn
            pieces[i + 27].setLocation(i*2, 3);
        }
        
        // end of reset
        currMove = true; // red move
    }
    
    public Piece getPieceAt(int x, int y) {
        if (board[y][x] >= 0 && board[y][x] < 32)
            return pieces[board[y][x]];
        else
            return null;
    }
    
    public boolean canSelect(int x, int y) {
        Piece p = getPieceAt(x, y);
        if (p == null) return false;
        return (p.getColor() == currMove);
    }
    
    public boolean isCheckMate(boolean _color) {
       int from, to, tx, ty;
       if (_color) {
           // check for red
           tx = pieces[0].getLocation().x;
           ty = pieces[0].getLocation().y;
           from = 16;
           to = 32;
       }
       else {
           // black
           tx = pieces[16].getLocation().x;
           ty = pieces[16].getLocation().y;
           from = 0;
           to = 16;
       }
       
       for (int i=from; i<to; i++) {
           Position pos = pieces[i].getLocation();
           if (board[pos.y][pos.x] == i) // check for active on board
               if (pieces[i].canMoveTo(this, tx, ty)) return true;
       }
       return false;
    }
    
    public boolean isEnd(boolean _color) {
        int from, to;
        if (_color) { // red
            from = 0;  to = 16;
        }
        else { // black
            from = 16; to = 32;
        }
        
        for (int i=from; i<to; i++) 
            if (board[pieces[i].getLocation().y][pieces[i].getLocation().x] == i) { // active
                int[][] move = pieces[i].getAvailableMove(this);
                for (int j=0; j<move.length; j++)
                    if (move[j][0] != -1) {
                        if (isValidMove(pieces[i].getLocation().x, pieces[i].getLocation().y,
                                move[j][0], move[j][1]))
                            return false;
                    }
                    else break;
            }
        return true;
    }
    
    public boolean isEnd() {
        return isEnd(currMove);
    }
            
    public boolean isValidMove(int srcX, int srcY, int dstX, int dstY) {
        //if (!(Piece.inBoard(srcX, srcY) && (Piece.inBoard(dstX, dstY)))) return false;

        Piece srcPiece = getPieceAt(srcX, srcY);
        if (srcPiece == null) return false;
        Piece desPiece = getPieceAt(dstX, dstY);
        if (desPiece != null) {
            if (srcPiece.getColor() == desPiece.getColor())
                return false;
        }
        
        if (srcPiece.canMoveTo(this, dstX, dstY)) {
            boolean result;
            // try to move
            srcPiece.setLocation(dstX, dstY);
            int oldPiece = board[dstY][dstX];
            board[dstY][dstX] = board[srcY][srcX];
            board[srcY][srcX] = -1;
            // -----------------
            result = isCheckMate(currMove); // check mate
            // undo move
            board[srcY][srcX] = board[dstY][dstX];
            board[dstY][dstX] = oldPiece;
            srcPiece.setLocation(srcX, srcY);
            //currMove = !currMove; // change other player move
            return !result;
        }
        
        return false;
    }
    
    protected void saveMove(int srcX, int srcY, int dstX, int dstY) {
        log[moveIdx][0] = srcX;
        log[moveIdx][1] = srcY;
        log[moveIdx][2] = dstX;
        log[moveIdx][3] = dstY;
        log[moveIdx][4] = board[dstY][dstX];
        moveIdx++;        
    }
    
    public void undo() {
        if (moveIdx <= 0) return;
        moveIdx--;
        Piece p = getPieceAt(log[moveIdx][2], log[moveIdx][3]);
        p.setLocation(log[moveIdx][0], log[moveIdx][1]);
        board[log[moveIdx][1]][log[moveIdx][0]] = board[log[moveIdx][3]][log[moveIdx][2]];
        board[log[moveIdx][3]][log[moveIdx][2]] = log[moveIdx][4];
        
        currMove = !currMove;
    }
    
    protected void doMove(int srcX, int srcY, int dstX, int dstY) {
        saveMove(srcX, srcY, dstX, dstY); // save log

        Piece srcPiece = getPieceAt(srcX, srcY);
        srcPiece.setLocation(dstX, dstY);
        board[dstY][dstX] = board[srcY][srcX];
        board[srcY][srcX] = -1;
        currMove = !currMove; // change other player move
    }
    
    public boolean tryMove(int srcX, int srcY, int dstX, int dstY) {
        if (isValidMove(srcX, srcY, dstX, dstY)) {
            doMove(srcX, srcY, dstX, dstY);
            
            if (isEnd(currMove))
                System.out.println("Het co roai !!!");
            return true;
        }
        return false;
    }
    
}
