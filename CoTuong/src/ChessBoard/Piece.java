/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChessBoard;

/**
 *
 * @author Hoand
 */
public abstract class Piece {
 
    public abstract int getType(); // General, Mandarin, Elephant, Rook, Canon, Knight, Pawn
    public abstract boolean getColor(); // Red or Black
    public abstract boolean canMoveTo(Chessboard _board, int x, int y);
    public abstract void setLocation(int x, int y);
    public abstract Position getLocation();
    public abstract int[][] getAvailableMove(Chessboard _board);
    public abstract int getValue();
    
    public static boolean inBoard(int x, int y) {
         if ((x < 0) || (y < 0) || 
                (x >= ChessConst.BOARD_WIDTH) || (y >= ChessConst.BOARD_HEIGHT))
            return false;
        
        return true;
    }
    
}
