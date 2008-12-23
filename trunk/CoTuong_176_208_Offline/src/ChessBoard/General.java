/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChessBoard;

/**
 *
 * @author Hoand
 */
public class General extends Piece {
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
    private static int[] dx = {-1, -16, 1, 16};
    
    //private boolean color;
//    private int currPos;
    
    public General(int _color) {
        super(_color);
        currPos = 0;
        mIsDead = false;
    }
    
    public int getType() { // General, Mandarin, Elephant, Rook, Canon, Knight, Pawn
        return ChessConst.PIECE_GENERAL;
    }
    
    /* Return 
     *   - true: red
     *   - false: black
     */
//    public boolean getColor() { // Red or Black
//        return color;
//    }
    
    public boolean canMoveTo(Chessboard _board, int sq) {
        if (!inBoard(sq)) return false;
        // Normal move
        for (int i=0; i<dx.length; i++) {
            if (currPos + dx[i] == sq) {
                int x = Position.POS_X(sq);
                int y = Position.POS_Y(sq);
                if ((x >= 3)&& (x<=5)) {
                    //if (color == Chessboard.COLOR_RED) 
                    if (mSide == Chessboard.FIELD_BOTTOM)
                        return (y >= 7); //red, bottom
                    else 
                        return (y <= 2); //black, upper
                }
                else return false;
            }
        }
        // special move, eat oponent general
        if (Position.POS_X(currPos) == Position.POS_X(sq)) {
            Piece tmp = _board.getPieceAt(sq);
            if (tmp == null) return false;
            if (tmp.getType() == ChessConst.PIECE_GENERAL) {
                for (int i = Math.min(currPos, sq) + 16; i < Math.max(currPos, sq); i += 16)
                    if (_board.getPieceAt(i) != null) return false;
                return true;
            }
        }
            
        // ------------------------------
        return false;
    }
    
//    public void setLocation(int sq) {
//        currPos = sq;
//    }
    
    public int[] getAvailableMove(Chessboard _board) {
        int[] result = new int [4];
        for (int i=0; i<result.length; i++) {
            result[i] = -1;
        }
        int count = 0;
        for (int i=0; i<dx.length; i++) {
            if (canMoveTo(_board, currPos + dx[i])) {
                result[count] = currPos + dx[i];
                count ++;
            }
        }
        return result;
    }
    
    public int getValue() {
        return ChessConst.GENERAL_VALUE;
    }
    
//    public int getLocation() {
//        return currPos;
//    }
    
}
