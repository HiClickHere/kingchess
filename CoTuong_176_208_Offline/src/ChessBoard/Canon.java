/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChessBoard;

/**
 *
 * @author Hoand
 */
public class Canon extends Piece {
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
    
//    private boolean color;
//    private int currPos;
    
    public Canon(int _color) {
        super(_color);
        currPos = 0;
        mIsDead = false;
    }
    
    public int getType() { // General, Bishop, Elephant, Rook, Canon, Knight, Pawn
        return ChessConst.PIECE_CANNON;
    }
    
    /* Return 
     *   - true: red
     *   - false: black
     */
//    public boolean getColor() { // Red or Black
//        return color;
//    }
    
    public boolean canMoveTo(Chessboard _board, int sq) {
        //System.out.println("Check move: " + currPos.x + " " + currPos.y + " " + x + " " + y);
        if (!inBoard(sq)) return false;
        
        Piece dstPiece = _board.getPieceAt(sq);
        if (dstPiece != null) {
            // special move, eat
            if (dstPiece.mSide != mSide) {
                if (Position.POS_X(sq) == Position.POS_X(currPos)) {
                    int count = 0;
                    for (int i=Math.min(currPos, sq) + 16; i<Math.max(currPos, sq); i += 16)
                        if (_board.getPieceAt(i) != null) count++;
                    return (count == 1);
                }
                else
                if (Position.POS_Y(sq) == Position.POS_Y(currPos)) {
                    int count = 0;
                    for (int i= Math.min(currPos, sq) + 1; i < Math.max(currPos, sq); i++)
                        if (_board.getPieceAt(i) != null) count++;
                    return (count == 1);
                }
            }
            else 
                return false;
        }
        else {
            // Normal move
            if (Position.POS_X(sq) == Position.POS_X(currPos)) {
                for (int i=Math.min(currPos, sq) + 16; i<Math.max(currPos, sq); i += 16)
                    if (_board.getPieceAt(i) != null) return false;
                return true;
            }
            else
            if (Position.POS_Y(sq) == Position.POS_Y(currPos)) {
                for (int i=Math.min(currPos, sq) + 1; i<Math.max(currPos, sq); i++)
                    if (_board.getPieceAt(i) != null) return false;
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
        return ChessConst.CANNON_VALUE;
    }
    
    public int[] getAvailableMove(Chessboard _board) {
        int[] result = new int [20];
        for (int i=0; i<result.length; i++) {
            result[i] = -1;
        }
        
        int count = 0;
        // di chuyen doc
//        for (int i=Position.POS_X(currPos); i<ChessConst.BOARD_HEIGHT; i+=ChessConst.BOARD_WIDTH) {
//            if (canMoveTo(_board, i)) {
//                result[count] = i;
//                count ++;
//            }
//        }
//        // di chuyen ngang
//        for (int i=Position.POS_Y(currPos); i<ChessConst.BOARD_WIDTH; i++) {
//            if (canMoveTo(_board, i)) {
//                result[count] = i; 
//                count ++;
//            }
//        }
        for (int i = 0; i < ChessConst.BOARD_WIDTH; i++)
        {
            if (canMoveTo(_board, Position.TO_SQ(i, Position.POS_Y(currPos))))
            {
                result[count] = Position.TO_SQ(i, Position.POS_Y(currPos));
                count++;
            }                        
        }
        
        for (int i = 0; i < ChessConst.BOARD_HEIGHT; i++)
        {
            if (canMoveTo(_board, Position.TO_SQ(Position.POS_X(currPos), i)))
            {
                result[count] = Position.TO_SQ(Position.POS_X(currPos), i);
                count++;
            }                        
        }
        
        return result;
    }
}
