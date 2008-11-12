/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

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
    public int mRangkingIndex;
    public boolean mIsRequest;    
    
    public void writeToStream(ChessDataOutputStream out) throws Exception
    {
        out.writeInt(mID);
        out.writeString16(mName);
        out.writeBoolean(mIsOnline);
        out.writeInt(mWinCount);
        out.writeInt(mLoseCount);
        out.writeInt(mDrawCount);
        out.writeInt(mRangkingIndex);
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
        aFriend.mRangkingIndex = in.readInt();
        aFriend.mIsRequest = in.readBoolean();
        return aFriend;
    }
}
