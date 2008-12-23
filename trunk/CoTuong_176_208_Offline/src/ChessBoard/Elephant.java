/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChessBoard;

/**
 *
 * @author Hoand
 */
public class Elephant extends Piece {
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
    private static int[] dx  = {-17, -15, 17, 15};
    
//    private boolean color;
//    private int currPos;
    
    public Elephant(int _color) {
        super(_color);
        currPos = 0;
        mIsDead = false;
    }
    
    public int getType() { // General, Mandarin, Elephant, Rook, Canon, Knight, Pawn
        return ChessConst.PIECE_ELEPHANT;
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
            if (currPos + 2*dx[i] == sq) {
               if (_board.getPieceAt(currPos + dx[i]) != null)
                   return false; // bi can
               if (mSide == Chessboard.FIELD_BOTTOM)  //Dong: side is clear to understand
                   return (Position.POS_Y(sq) >= 5); //red, bottom
               else 
                   return (Position.POS_Y(sq) <= 4); //black, upper
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
        return ChessConst.ELEPHANT_VALUE;
    }
    
    public int[] getAvailableMove(Chessboard _board) {
        int[] result = new int [4];
        for (int i=0; i<result.length; i++) {
            result[i] = -1;
        }
        int count = 0;
        for (int i=0; i<dx.length; i++) {
            if (canMoveTo(_board, currPos + 2*dx[i])) {
                result[count] = currPos + 2*dx[i];
                count ++;
            }
        }
        return result;
    }
}
