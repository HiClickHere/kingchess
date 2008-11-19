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
}