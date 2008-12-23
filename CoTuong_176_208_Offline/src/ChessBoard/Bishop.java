/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChessBoard;

/**
 *
 * @author Hoand
 */
public class Bishop extends Piece {
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
    private static int[] dx = {-17, -15, 17, 15};
    //private static int[] dy = {-1, 1,-1, 1};
    
//    private boolean color;
//    private int currPos;
    
    public Bishop(int _color) {
        super(_color);
        currPos = 0;
        mIsDead = false;
    }
    
    public int getType() { // General, Mandarin, Elephant, Rook, Canon, Knight, Pawn
        return ChessConst.PIECE_BISHOP;
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
        for (int i=0; i< dx.length; i++) {
            if ((currPos + dx[i] == sq)) {
                int x = Position.POS_X(sq);
                int y = Position.POS_Y(sq);
                if ((x >= 3)&& (x<=5)) {
                    if (mSide == Chessboard.FIELD_BOTTOM) 
                        return (y >= 7); //red, bottom
                    else 
                        return (y <= 2); //black, upper
                }
                else return false;
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
        return ChessConst.BISHOP_VALUE;
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
