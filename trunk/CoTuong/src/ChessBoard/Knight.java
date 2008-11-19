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
    private static int[] dx = {-2, -1, 1, 2, 2, 1, -1, -2};
    private static int[] dy = {-1, -2,-2,-1, 1, 2,  2,  1};
    
    private static int[] cdx = {-1, 0, 0, 1, 1, 0, 0,-1}; // can ma
    private static int[] cdy = { 0,-1,-1, 0, 0, 1, 1, 0}; // can ma
    
    private boolean color;
    private Position currPos;
    
    public Knight(boolean _color) {
        color = _color;
        currPos = new Position();
    }
    
    public int getType() { // General, Bishop, Elephant, Rook, Canon, Knight, Pawn
        return ChessConst.PIECE_CAVALRY;
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
               // tinh can ma
               if (_board.getPieceAt(currPos.x + cdx[i], currPos.y + cdy[i]) != null)
                   return false; // bi can
               // -----------
               return true;
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
        return ChessConst.CAVALRY_VALUE;
    }
    
    public int[][] getAvailableMove(Chessboard _board) {
        int[][] result = new int [8][2];
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
