/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChessBoard;

/**
 *
 * @author Hoand
 */

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

public class Position {
    public int x = 0;
    public int y = 0;
    
    public Position() {
        x = 0;
        y = 0;
    }
    
    public Position(int _x, int _y) {
        x = _x;
        y = _y;
    }
    
    public final static int IN_BOARD[] = 
    {
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
        0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,
        0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,
        0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,
        0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,
        0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,
        0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,
        0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,
        0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,
        0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,
        0,  0,  0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  0,  0,  0,
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0        
    };
    // convert from int to X
    public static int POS_X(int square) { 
        return (square % 16) - 3;
    }
    
    public static int POS_Y(int square) { 
        return (square / 16) - 3;
    }
    
    public static int TO_SQ(int curX, int curY) {
        return ((curY + 3) * 16 + curX + 3);
    }
    
    public static int ROTATE(int square)
    {
        int x = POS_X(square);
        int y = POS_Y(square);
        x = ChessConst.BOARD_WIDTH - x - 1;
        y = ChessConst.BOARD_HEIGHT - y - 1;
        return TO_SQ(x, y);
    }
            
}