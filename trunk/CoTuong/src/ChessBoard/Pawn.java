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
    
    private static int[] dx = {-1, 0, 1, 0};
    private static int[] dy = { 0, 1, 0,-1};
    
    private boolean color;
    private Position currPos;
    
    public Pawn(boolean _color) {
        color = _color;
        currPos = new Position();
    }
    
    public int getType() { // General, Mandarin, Elephant, Rook, Canon, Knight, Pawn
        return ChessConst.PIECE_PAWN;
    }
    
    /* Return 
     *   - true: red
     *   - false: black
     */
    public boolean getColor() { // Red or Black
        return color;
    }
    
    public boolean canMoveTo(Chessboard _board, int x, int y) {
        if (!inBoard(x, y))
            return false;
        // Normal move
        for (int i=0; i<dx.length; i++) {
            if ((currPos.x + dx[i] == x) && (currPos.y + dy[i] == y)) {
                if (color) {
                    // red, bottom
                    if (i == 1) return false;// go back
                    else if (i == 3) return true; // go forward
                    else if (y <= 4) return true; // Cross river
                }
                else {
                    // black, top
                    if (i == 3) return false;// go back
                    else if (i == 1) return true; // go forward
                    else if (y >= 5) return true; // Cross river
                }
            }
        }
        return false;
    }
    
    public void setLocation(int x, int y) {
        currPos.x = x;
        currPos.y = y;
    }
    
    public Position getLocation() {
        return currPos;
    }
    
    public int getValue() {
        if (color) { // red
            if (currPos.y <= 4) return ChessConst.PAWN_VALUE_2; // Cross river
            else return ChessConst.PAWN_VALUE_1;
        }
        else { // black
            if (currPos.y >= 4) return ChessConst.PAWN_VALUE_2; // Cross river
            else return ChessConst.PAWN_VALUE_1;
        }
    }
    
    public int[][] getAvailableMove(Chessboard _board) {
        int[][] result = new int [4][2];
        for (int i=0; i<result.length; i++) {
            result[i][0] = -1;
            result[i][1] = -1;
        }
        int count = 0;
        for (int i=0; i<dx.length; i++) {
            if (canMoveTo(_board, currPos.x + dx[i], currPos.y + dy[i])) {
                result[count][0] = currPos.x + dx[i];
                result[count][1] = currPos.y + dy[i];
                count ++;
            }
        }
        return result;
    }
}
