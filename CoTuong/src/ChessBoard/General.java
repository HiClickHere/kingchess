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
    private static int[] dx = {-1, 0, 1, 0};
    private static int[] dy = { 0, 1, 0,-1};
    
    private boolean color;
    private Position currPos;
    
    public General(boolean _color) {
        color = _color;
        currPos = new Position();
    }
    
    public int getType() { // General, Mandarin, Elephant, Rook, Canon, Knight, Pawn
        return ChessConst.PIECE_GENERAL;
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
        for (int i=0; i<dx.length; i++) {
            if ((currPos.x + dx[i] == x) && (currPos.y + dy[i] == y)) {
                if ((x >= 3)&& (x<=5)) {
                    if (color) return (y >= 7); //red, bottom
                    else return (y <= 2); //black, upper
                }
                else return false;
            }
        }
        // special move, eat oponent general
        if (currPos.x == x) {
            Piece tmp = _board.getPieceAt(x, y);
            if (tmp == null) return false;
            if (tmp.getType() == ChessConst.PIECE_GENERAL) {
                for (int i=Math.min(currPos.y, y) + 1; i<Math.max(currPos.y, y); i++)
                    if (_board.getPieceAt(x, i) != null) return false;
                return true;
            }
        }
            
        // ------------------------------
        return false;
    }
    
    public void setLocation(int x, int y) {
        currPos.x = x;
        currPos.y = y;
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
    
    public int getValue() {
        return ChessConst.GENERAL_VALUE;
    }
    
    public Position getLocation() {
        return currPos;
    }
    
}
