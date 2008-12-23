/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChessBoard;

/**
 *
 * @author Hoand
 */
public class Computer extends Chessboard {
    // computer control black 
    protected int MAX_DEEP;
    protected int[] currPoint;
    protected int[] bestMove;
    protected int deep;        
    
    public Computer() {
        super();
        resetBoard();
        MAX_DEEP = 2;
        currPoint = new int[MAX_DEEP + 1];
        bestMove = new int[4];
    }
    
    public int[] getMove() {
        for (int i=0; i<=MAX_DEEP; i++) 
            if (i % 2 == 0) currPoint[i] = -20000;
            else currPoint[i] = 20000;
        
        deep = 0;
        alphabeta();
        doMove(bestMove[0], bestMove[1]);
        return bestMove;
    }
    
    
    protected int alphabeta() {
        if (deep >= MAX_DEEP) return evaluate();
        int result = currPoint[deep];
        //if (!currMove) result = -result;
        // create move
        deep++;
        currPoint[deep] = result;
        int from, to;
        if (currMove) { from = 0; to = 16; }
        else { from = 16; to = 32; }
        
        for (int i=from; i<to; i++) 
            if (board[pieces[i].getLocation()] == i) { // check for active
                int[] tmpMove = pieces[i].getAvailableMove(this);
                for (int j=0; j<tmpMove.length; j++) 
                    if (tmpMove[j] != -1) {
                        if (isValidMove(pieces[i].getLocation(), tmpMove[j])) 
                        {
                            doMove(pieces[i].getLocation(), tmpMove[j]);
                            
                            result = alphabeta();
                            if (currMove) {
                                // red
                                if (result >= currPoint[deep - 1]) 
                                    currPoint[deep - 1] = result;
                                else { undo(); break; } 
                            }
                            else {
                                if (result <= currPoint[deep - 1]) 
                                    currPoint[deep - 1] = result;
                                else { undo(); break; }
                            }
                            undo();
                            if (deep == 1) {
                                bestMove[0] = pieces[i].getLocation();
                                bestMove[1] = tmpMove[j];
                            }
                        }
                    }
                    else break;
            } 
        deep--;
        // end create move
        return result;
    }
    
    protected int evaluate() {
        int result = 0;
        for (int i=0; i<pieces.length; i++) 
            if (board[pieces[i].getLocation()] == i) {
                if (pieces[i].getColor() == Chessboard.COLOR_RED) // red
                    result -= pieces[i].getValue();
                else
                    result += pieces[i].getValue();
            }
        return result;
    }
    
    public void setHumanMove(int srcX, int srcY, int dstX, int dstY) {
        int src = Position.TO_SQ(srcX, srcY);
        int dst = Position.TO_SQ(dstX, dstY);
        doMove(src, dst);
    }
    
    //Dong: added to check if a legal move
    /*public boolean isLegalMove(int srcX, int srcY, int dstX, int dstY)
    {
        Piece srcPiece = getPieceAt(srcX, srcY);        
        return srcPiece.canMoveTo(this, dstX, dstY);
    }
     */
}
