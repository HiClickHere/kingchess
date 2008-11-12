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
public class MessageRecord {
    public int mSenderID;
    public String16 mSenderName;
    public String16 mMessage;
    public long mTimeSent;
    
    public void writeToStream(ChessDataOutputStream out) throws Exception
    {
        out.writeInt(mSenderID);
        out.writeString16(mSenderName);
        out.writeString16(mMessage);   
        out.writeLong(mTimeSent);
    }
    
    public static MessageRecord readFromStream(ChessDataInputStream in) throws Exception
    {
        MessageRecord aMessage = new MessageRecord();
        aMessage.mSenderID = in.readInt();
        aMessage.mSenderName = in.readString16();
        aMessage.mMessage = in.readString16();
        aMessage.mTimeSent = in.readLong();
        return aMessage;
    }
}
