/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChessBoard;

/**
 *
 * @author Hoand
 */
public class Knight extends Piece {
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
     * 
     * 0 |-1-|-2-|
     * 1 0-|-|-|-3
     * 2 |-|-K-|-|
     * 3 7-|-|-|-4
     * 4 |-6-|-5-|
     */
    private static int[] dx  = {-18, -33, -31, -14, 18, 33, 31, 14};
    private static int[] cdx = { -1,  -16,  -16,  1,  1,  16,  16, -1}; // can ma
    
//    private boolean color;
//    private int currPos;
    
    public Knight(int _color) {
        super(_color);
        currPos = 0;
        mIsDead = false;
    }
    
    public int getType() { // General, Bishop, Elephant, Rook, Canon, Knight, Pawn
        return ChessConst.PIECE_CAVALRY;
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
               // tinh can ma
               if (_board.getPieceAt(currPos + cdx[i]) != null)
                   return false; // bi can
               // -----------
               return true;
            }
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
        return ChessConst.CAVALRY_VALUE;
    }
    
    public int[] getAvailableMove(Chessboard _board) {
        int[] result = new int [8];
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
}
