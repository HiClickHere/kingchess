/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChessBoard;

/**
 *
 * @author Hoand
 */
public class Pawn extends Piece {
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
    
    private static int[] dx = {-1, 16, 1, -16};
    
//    private boolean color;
//    private int currPos;
    
    public Pawn(int _color) {
        super(_color);
        currPos = 0;
        mIsDead = false;
    }
    
    public int getType() { // General, Mandarin, Elephant, Rook, Canon, Knight, Pawn
        return ChessConst.PIECE_PAWN;
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
                //if (color == Chessboard.COLOR_RED) 
                if (mSide == Chessboard.FIELD_BOTTOM)
                {
                    // red, bottom
                    if (i == 1) 
                        return false;// go back
                    else if (i == 3) 
                        return true; // go forward
                    else if (y <= 4) 
                        return true; // Cross river
                }
                else {
                    // black, top
                    if (i == 3) 
                        return false;// go back
                    else if (i == 1) 
                        return true; // go forward
                    else if (y >= 5) 
                        return true; // Cross river
                }
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
        int y = Position.POS_Y(currPos);
        if (color == Chessboard.COLOR_RED) { // red
            if (y <= 4) return ChessConst.PAWN_VALUE_2; // Cross river
            else return ChessConst.PAWN_VALUE_1;
        }
        else { // black
            if (y >= 4) return ChessConst.PAWN_VALUE_2; // Cross river
            else return ChessConst.PAWN_VALUE_1;
        }
    }
    
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
}
