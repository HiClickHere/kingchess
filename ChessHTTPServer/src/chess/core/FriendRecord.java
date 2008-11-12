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
public class FriendRecord {
    public int mID;
    public String16 mName;
    public boolean mIsOnline;
    public int mWinCount;
    public int mLoseCount;
    public int mDrawCount;
    public boolean mIsRequest;    
    
    public void writeToStream(ChessDataOutputStream out) throws Exception
    {
        out.writeInt(mID);
        out.writeString16(mName);
        out.writeBoolean(mIsOnline);
        out.writeInt(mWinCount);
        out.writeInt(mLoseCount);
        out.writeInt(mDrawCount);
        out.writeBoolean(mIsRequest);
    }
    
    public static FriendRecord readFromStream(ChessDataInputStream in) throws Exception
    {
        FriendRecord aFriend = new FriendRecord();
        aFriend.mID = in.readInt();
        aFriend.mName = in.readString16();
        aFriend.mIsOnline = in.readBoolean();
        aFriend.mWinCount = in.readInt();
        aFriend.mLoseCount = in.readInt();
        aFriend.mDrawCount = in.readInt();
        aFriend.mIsRequest = in.readBoolean();
        return aFriend;
    }
}
