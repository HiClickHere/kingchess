/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chess.core;

import chess.core.String16;
import chess.util.ChessDataInputStream;
import chess.util.ChessDataOutputStream;

/**
 *
 * @author dong
 */
public class MoveRecord {
    public int mMoveID;
    public int mUserID;
    public String16 mUserName;
    public short mXSrc;
    public short mYSrc;
    public short mXDst;
    public short mYDst;
    public long mTime;
    
    public void writeToStream(ChessDataOutputStream out) throws Exception
    {
        out.writeInt(mMoveID);
        out.writeInt(mUserID);
        out.writeString16(mUserName);
        out.writeShort(mXSrc);
        out.writeShort(mYSrc);
        out.writeShort(mXDst);
        out.writeShort(mYDst);
        out.writeLong(mTime);
    }
    
    public static MoveRecord readFromStream(ChessDataInputStream in) throws Exception
    {
        MoveRecord aMove = new MoveRecord();
        aMove.mMoveID = in.readInt();
        aMove.mUserID = in.readInt();
        aMove.mUserName = in.readString16();
        aMove.mXSrc = in.readShort();
        aMove.mYSrc = in.readShort();
        aMove.mXDst = in.readShort();
        aMove.mYDst = in.readShort();
        aMove.mTime = in.readLong();
        return aMove;
    }
}
