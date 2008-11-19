/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChessBoard;

/**
 *
 * @author Hoand
 */
public class ChessConst {
    public final static int PIECE_GENERAL   = 0;
    public final static int PIECE_BISHOP    = 1;
    public final static int PIECE_ELEPHANT  = 2;
    public final static int PIECE_CAVALRY   = 3;
    public final static int PIECE_ROOK      = 4;
    public final static int PIECE_CANNON    = 5;
    public final static int PIECE_PAWN      = 6;
    
    public final static int GENERAL_VALUE   = 10000;
    public final static int BISHOP_VALUE    = 4;
    public final static int ELEPHANT_VALUE  = 5;
    public final static int CAVALRY_VALUE   = 9;
    public final static int ROOK_VALUE      = 10;
    public final static int CANNON_VALUE    = 20;
    public final static int PAWN_VALUE_1    = 2; // before cross river
    public final static int PAWN_VALUE_2    = 4; // cross river
    
    public final static int BOARD_WIDTH = 9;
    public final static int BOARD_HEIGHT = 10;
    
    public final static int MAX_MOVE = 300;
}
