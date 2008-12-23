/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChessBoard;

/**
 *
 * @author Hoand
 */
public class Rook extends Piece {
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
    
//    private boolean color;
//    private int currPos;
//    
    public Rook(int _color) {
        super(_color);
        currPos = 0;
        mIsDead = false;
    }
    
    public int getType() { // General, Bishop, Elephant, Rook, Canon, Knight, Pawn
        return ChessConst.PIECE_ROOK;
    }
    
    /* Return 
     *   - true: red
     *   - false: black
     */
//    public boolean getColor() { // Red or Black
//        return color;
//    }
//    
    public boolean canMoveTo(Chessboard _board, int sq) {
        if (!inBoard(sq)) return false;
        // Normal move
        Piece p = _board.getPieceAt(sq);
        if (p != null)
            if (p.getColor() == color) 
                return false;
        if (Position.POS_X(sq) == Position.POS_X(currPos)) {
            for (int i = Math.min(currPos, sq) + 16; i < Math.max(currPos, sq); i += 16)
                if (_board.getPieceAt(i) != null) return false;
            return true;
        }
        else
        if (Position.POS_Y(sq) == Position.POS_Y(currPos)) {
            for (int i = Math.min(currPos, sq) + 1; i < Math.max(currPos, sq); i++)
                if (_board.getPieceAt(i) != null) return false;
            return true;
        }    
        return false;
    }
    
//    public void setLocation(int sq) {
//        currPos = sq;
//    }
//    
//    public int getLocation() {
//        return currPos;
//    }
    
    public int getValue() {
        return ChessConst.ROOK_VALUE;
    }
    
    public int[] getAvailableMove(Chessboard _board) {
        int[] result = new int [20];
        for (int i=0; i<result.length; i++) {
            result[i] = -1;
        }
        int count = 0;
        for (int i=0; i<dx.length; i++) {
            for (int j = 1; j < 10; j++) 
                if (canMoveTo(_board, currPos + j * dx[i])) {
                    result[count] = currPos + j * dx[i];
                    count ++;
                    if (count == 3) 
                        System.out.println("Count is 3");
                }
                else break;
        }
        return result;
    }
}
