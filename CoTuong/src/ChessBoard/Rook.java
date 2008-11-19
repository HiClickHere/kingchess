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
    private static int[] dx = {-1, 0, 1, 0};
    private static int[] dy = { 0,-1, 0, 1};
    
    private boolean color;
    private Position currPos;
    
    public Rook(boolean _color) {
        color = _color;
        currPos = new Position();
    }
    
    public int getType() { // General, Bishop, Elephant, Rook, Canon, Knight, Pawn
        return ChessConst.PIECE_ROOK;
    }
    
    /* Return 
     *   - true: red
     *   - false: black
     */
    public boolean getColor() { // Red or Black
        return color;
    }
    
    public boolean canMoveTo(Chessboard _board, int x, int y) {
        if (!inBoard(x, y)) return false;
        // Normal move
        Piece p = _board.getPieceAt(x, y);
        if (p != null)
            if (p.getColor() == color) return false;
        if (currPos.x == x) {
            for (int i=Math.min(currPos.y, y) + 1; i<Math.max(currPos.y, y); i++)
                if (_board.getPieceAt(x, i) != null) return false;
            return true;
        }
        else
        if (currPos.y == y) {
            for (int i=Math.min(currPos.x, x) + 1; i<Math.max(currPos.x, x); i++)
                if (_board.getPieceAt(i, y) != null) return false;
            return true;
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
        return ChessConst.ROOK_VALUE;
    }
    
    public int[][] getAvailableMove(Chessboard _board) {
        int[][] result = new int [20][2];
        for (int i=0; i<result.length; i++) {
            result[i][0] = -1;
            result[i][1] = -1;
        }
        int count = 0;
        for (int i=0; i<dx.length; i++) {
            for (int j=1; j<10; j++) 
                if (canMoveTo(_board, currPos.x + j*dx[i], currPos.y + j*dy[i])) {
                    result[count][0] = currPos.x + j*dx[i];
                    result[count][1] = currPos.y + j*dy[i];
                    count ++;
                    if (count == 3) 
                        System.out.println("Count is 3");
                }
                else break;
        }
        return result;
    }
}
