/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ChessBoard;

/**
 *
 * @author Hoand
 */
public class Piece {
    public boolean mIsDead; //Dong: this is useful for render
    protected int color; //Dong: move to this class
    protected int currPos;
    protected int mSide;
    
    public Piece(int color)
    {
        this.color = color;        
    }
    
    public void setSide(int side)
    {
        mSide = side;
    }
    
    public int getType()// General, Mandarin, Elephant, Rook, Canon, Knight, Pawn
    {
        return 0;
    }
    
    //public abstract boolean getColor(); // Red or Black
    public int getColor()//Dong: new implement need this
    {
        return color;
    }
    
    public boolean canMoveTo(Chessboard _board, int sq)
    {
        return false;
    }
    
    public void setLocation(int sq) {
        currPos = sq;
    }
    
    public int getLocation() {
        return currPos;
    }
    
    public int[] getAvailableMove(Chessboard _board)
    {
        return null;
    }
    
    public int getValue()
    {
        return 0;
    }
    
    public static boolean inBoard(int x, int y) {
         return inBoard(Position.TO_SQ(x, y));
    }
    
    public static boolean inBoard(int sq) {
         return (Position.IN_BOARD[sq] == 1);
    }
}
