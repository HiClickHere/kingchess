/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chess.core;

import chess.util.Protocol;
import chess.util.PunchLogger;
import chess.core.String16;
import chess.util.ChessDataInputStream;
import chess.util.ChessDataOutputStream;
import java.io.*;
import java.sql.*;
import java.util.Vector;

/**
 *
 * @author dong
 */
public class Room {
    public int mID;
    public Account mRoomOwner;    
    public String mRoomName;
    public String mPassword;    
    public Vector mPlayerVector;
    public Vector mRefereeVector;
    public Vector mMoveVector;
    public Vector mMessageVector;
    public long mLastUpdateMember;
    public long mLastUpdateMoves;
    public long mLastUpdateMessages;
    public boolean mIsStarted;
    
    public static Room getRoomInstance(Connection aConnection, int roomID)
    {
        try {
            Room aRoom = new Room();

            PreparedStatement ps = aConnection.prepareStatement("SELECT * FROM room WHERE roomid = ?");
            ps.setInt(1, roomID);
            ResultSet rs = ps.executeQuery();            
            if (rs != null && rs.first())
            {
                aRoom.mID = roomID;
                aRoom.mRoomName = rs.getString("roomname");
                aRoom.mPassword = rs.getString("password");                  
                aRoom.mLastUpdateMember = Long.parseLong(rs.getString("last_update_member_time"));
                aRoom.mLastUpdateMoves = Long.parseLong(rs.getString("last_update_move"));
                aRoom.mLastUpdateMessages = Long.parseLong(rs.getString("last_update_message"));
                aRoom.mIsStarted = Boolean.parseBoolean(rs.getString("started"));
                rs.close();
                ps.close();
            }
            else {
                rs.close();
                ps.close();
                return null;
            }                        
            
            aRoom.updateMemberList(aConnection);
            aRoom.updateMoveList(aConnection);

            return aRoom;
        } catch (Exception e)
        {
            return null;
        }
    }
    
    public static boolean isExist(Connection aConnection, int roomID) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("SELECT * FROM room WHERE roomid = ?");
        ps.setInt(1, roomID);
        ResultSet rs = ps.executeQuery();
        if (rs != null && rs.first())
        {
            rs.close();
            ps.close();
            return true;
        }
        rs.close();
        ps.close();
        return false;
    }
    
    public void updateMoveList(Connection aConnection) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("SELECT * FROM move WHERE roomid = ?");
        ps.setInt(1, mID);
        ResultSet rs = ps.executeQuery(); 
        mMoveVector = new Vector();
        while (rs != null && rs.next())
        {
            MoveRecord aMove = new MoveRecord();
            aMove.mMoveID = Integer.parseInt(rs.getString("moveid"));
            aMove.mUserID = Integer.parseInt(rs.getString("userid"));
            Account aAcc = Account.getAccountInstance(aConnection, aMove.mUserID);
            aMove.mUserName = new String16(aAcc.mUserName);
            aMove.mXSrc = Short.parseShort(rs.getString("xsrc"));
            aMove.mYSrc = Short.parseShort(rs.getString("ysrc"));
            aMove.mXDst = Short.parseShort(rs.getString("xdst"));
            aMove.mYDst = Short.parseShort(rs.getString("ydst"));
            aMove.mTime = Long.parseLong(rs.getString("time"));
            mMoveVector.addElement(aMove);
        }
        rs.close();
        ps.close();
    }
    
    public void updateMemberList(Connection aConnection) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("SELECT * FROM user_room WHERE roomid = ?");
        ps.setInt(1, mID);
        ResultSet rs = ps.executeQuery();        
        mPlayerVector = new Vector();
        mRefereeVector = new Vector();
        while (rs != null && rs.next())
        {
             int userid = Integer.parseInt(rs.getString("userid"));
             int role = Integer.parseInt(rs.getString("role"));  
             boolean isReady = Boolean.parseBoolean(rs.getString("ready"));
             Account aAcc = Account.getAccountInstance(aConnection, userid);
             aAcc.mIsReady = isReady;
             if (role == 0 || role == 1) {                          
                mPlayerVector.addElement(aAcc);
                if (role == 0)
                    mRoomOwner = aAcc;
             } 
             else
             {
                 mRefereeVector.addElement(aAcc);
             }
        }
        rs.close();
        ps.close();
    }
    
    public void updateMessageList(Connection aConnection) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("SELECT * FROM inroom_message WHERE roomid = ?");
        ps.setInt(1, mID);
        ResultSet rs = ps.executeQuery();        
        mMessageVector = new Vector();
        while (rs != null && rs.next())
        {
            MessageRecord aMessage = new MessageRecord();
            aMessage.mMessage = new String16(rs.getString("message"));
            aMessage.mSenderID = Integer.parseInt(rs.getString("userid"));
            Account aAcc = Account.getAccountInstance(aConnection, aMessage.mSenderID);
            aMessage.mSenderName = new String16(aAcc.mUserName);
            aMessage.mTimeSent = Long.parseLong(rs.getString("time"));
            mMessageVector.addElement(aMessage);
        }
        rs.close();
        ps.close();
    }
    
    public boolean responseMemberList(Connection aConnection, ChessDataInputStream in, ChessDataOutputStream out)
    {
        boolean isSuccess = true;
        try {
            if (mPlayerVector == null || mRefereeVector == null)
                updateMemberList(aConnection);
            out.writeShort(Protocol.RESPONSE_LIST_MEMBERS);
            out.writeInt(mPlayerVector.size());
            for (int i = 0; i < mPlayerVector.size(); i++)
            {
                Account aPlayer = (Account)mPlayerVector.elementAt(i);
                out.writeInt(aPlayer.mUserID);
                out.writeString16(new String16(aPlayer.mUserName));
                out.writeInt(aPlayer.mWinCount);
                out.writeInt(aPlayer.mLoseCount);
                out.writeInt(aPlayer.mDrawCount);
                out.writeBoolean(aPlayer.mIsReady);
            }
            out.writeInt(mRefereeVector.size());
            for (int i = 0; i < mRefereeVector.size(); i++)
            {
                Account aReferee = (Account)mRefereeVector.elementAt(i);
                out.writeInt(aReferee.mUserID);
                out.writeString16(new String16(aReferee.mUserName));
                out.writeInt(aReferee.mWinCount);
                out.writeInt(aReferee.mLoseCount);
                out.writeInt(aReferee.mDrawCount);
                out.writeBoolean(aReferee.mIsReady);
            }
        } catch (Exception e)
        {
            isSuccess = false;
            
            PunchLogger.logException("responseMemberList : " + e); 
            try {
                out.writeShort(Protocol.RESPONSE_MEMBER_QUERY_FAILURE);
                out.writeString16(new String16("Loi: Khong the tim thay danh sach nguoi choi."));
            } catch (Throwable e1)
            {

            }
        }
        return isSuccess;
    }
    
    public boolean responseMoveList(long lastUpdateMoveTime, Connection aConnection, ChessDataInputStream in, ChessDataOutputStream out)
    {
        boolean isSuccess = true;
        try {
            if (mMoveVector == null)
                updateMoveList(aConnection);            
            Vector returnMoveVector = new Vector();
            for (int i = 0; i < mMoveVector.size(); i++)
            {
                MoveRecord aMove = (MoveRecord)mMoveVector.elementAt(i);
                if (aMove.mTime > lastUpdateMoveTime)
                {                    
                    returnMoveVector.addElement(aMove);
                }
            }
            out.writeShort(Protocol.RESPONSE_NEW_MOVES); 
            out.writeInt(returnMoveVector.size());
            for (int i = 0; i < returnMoveVector.size(); i++)
            {
                MoveRecord aMove = (MoveRecord)returnMoveVector.elementAt(i);
                aMove.writeToStream(out);
            }        
            returnMoveVector.removeAllElements();
            isSuccess = true;
        } catch (Exception e)
        {
            isSuccess = false;
            
            PunchLogger.logException("responseMoveList : " + e); 
            try {
                out.writeShort(Protocol.RESPONSE_MOVE_QUERY_FAILURE);
                out.writeString16(new String16("Loi: Khong the tim thay danh sach nuoc di."));
            } catch (Throwable e1)
            {

            }
        }
        return isSuccess;
    }
    
    public boolean responseMessageList(long lastUpdateMessageTime, Connection aConnection, ChessDataInputStream in, ChessDataOutputStream out)
    {
        boolean isSuccess = true;
        try {
            if (mMessageVector == null)
                updateMessageList(aConnection);            
            Vector returnMessageList = new Vector();
            for (int i = 0; i < mMessageVector.size(); i++)
            {
                MessageRecord aMessage = (MessageRecord)mMessageVector.elementAt(i);
                if (aMessage.mTimeSent > lastUpdateMessageTime)
                {                    
                    returnMessageList.addElement(aMessage);
                }
            }
            out.writeShort(Protocol.RESPONSE_NEW_MESSAGES); 
            out.writeInt(returnMessageList.size());
            for (int i = 0; i < returnMessageList.size(); i++)
            {
                MessageRecord aMessage = (MessageRecord)returnMessageList.elementAt(i);
                aMessage.writeToStream(out);
            }          
            returnMessageList.removeAllElements();
            isSuccess = true;
        } catch (Exception e)
        {
            isSuccess = false;
            
            PunchLogger.logException("responseMessageList : " + e); 
            try {
                out.writeShort(Protocol.RESPONSE_MESSAGES_QUERY_FAILURE);
                out.writeString16(new String16("Loi: Khong the tim thay danh sach tin nhan."));
            } catch (Throwable e1)
            {

            }
        }
        return isSuccess;
    }
    
    public static boolean doMove(Connection aConnection, ChessDataInputStream in, ChessDataOutputStream out)
    {
        boolean isSuccess = true;
        try {                        
            long time = System.currentTimeMillis();
            PreparedStatement ps = aConnection.prepareStatement("INSERT INTO " +
                    "move(userid, time, xsrc, ysrc, xdst, ydst, roomid)" +
                    "  VALUES(?, ?, ?, ?, ?, ?, ?);");
            ps.setInt(1, in.readInt()); // userid
            int roomid = in.readInt();
            ps.setInt(7, roomid); // roomid
            ps.setLong(2, time); 
            ps.setShort(3, in.readShort()); //xsrc
            ps.setShort(4, in.readShort()); //ysrc
            ps.setShort(5, in.readShort()); //xdst
            ps.setShort(6, in.readShort()); //ydst
            ps.executeUpdate();            
            ps.close();
            
            ps = aConnection.prepareStatement("UPDATE room SET last_update_move = ? WHERE roomid = ?;");
            ps.setLong(1, time);
            ps.setInt(2, roomid);
            ps.executeUpdate();
            ps.close();
            
            out.writeShort(Protocol.RESPONSE_I_DID_A_MOVE_SUCCESSFULLY);            
            isSuccess = true;
        } catch (Exception e)
        {
            isSuccess = false;
            
            PunchLogger.logException("doMove : " + e); 
            try {
                out.writeShort(Protocol.RESPONSE_I_DID_A_MOVE_FAILURE);
                out.writeString16(new String16("Loi: May chu dang ban."));
            } catch (Throwable e1)
            {

            }
        }
        return isSuccess;
    }
    
    public static boolean sendMessage(Connection aConnection, ChessDataInputStream in, ChessDataOutputStream out)
    {
        boolean isSuccess = true;
        try {                        
            long time = System.currentTimeMillis();
            PreparedStatement ps = aConnection.prepareStatement("INSERT INTO " +
                    "inroom_message(userid, roomid, message, time)" +
                    "  VALUES(?, ?, ?, ?);");
            ps.setInt(1, in.readInt()); // userid
            int roomid = in.readInt();
            ps.setInt(2, roomid); // roomid            
            ps.setString(3, in.readString16().toJavaString()); //xsrc
            ps.setLong(4, time); 
            ps.executeUpdate();            
            ps.close();
            
            ps = aConnection.prepareStatement("UPDATE room SET last_update_message = ? WHERE roomid = ?;");
            ps.setLong(1, time);
            ps.setInt(2, roomid);
            ps.executeUpdate();
            ps.close();
            
            out.writeShort(Protocol.RESPONSE_SEND_MESSAGE_INROM_SUCCESSFULLY);      
            isSuccess = true;
        } catch (Exception e)
        {
            isSuccess = false;
            
            PunchLogger.logException("doMove : " + e); 
            try {
                out.writeShort(Protocol.RESPONSE_SEND_MESSAGE_INROM_FAILURE);
                out.writeString16(new String16("Loi: May chu dang ban."));
            } catch (Throwable e1)
            {

            }
        }
        return isSuccess;
    }        
    
    public static boolean joinRoom(Connection aConnection, ChessDataInputStream in, ChessDataOutputStream out)
    {
        boolean isSuccess = true;
        try {                        
            long time = System.currentTimeMillis();
            PreparedStatement ps = aConnection.prepareStatement("INSERT INTO " +
                    "user_room(userid, roomid, role, ready)" +
                    "  VALUES(?, ?, ?, ?);");
            ps.setInt(1, in.readInt()); // userid
            int roomid = in.readInt();
            ps.setInt(2, roomid); // roomid   
            ps.setInt(3, in.readInt());            
            ps.setBoolean(4, in.readBoolean()); 
            ps.executeUpdate();            
            ps.close();
            
            ps = aConnection.prepareStatement("UPDATE room SET last_update_member_time = ? WHERE roomid = ?;");
            ps.setLong(1, time);
            ps.setInt(2, roomid);
            ps.executeUpdate();
            ps.close();
            
            out.writeShort(Protocol.RESPONSE_JOIN_ROOM_SUCCESSFULLY);      
            isSuccess = true;
        } catch (Exception e)
        {
            isSuccess = false;
            
            PunchLogger.logException("joinRoom : " + e); 
            try {
                out.writeShort(Protocol.RESPONSE_JOIN_ROOM_FAILURE);
                out.writeString16(new String16("Loi: May chu dang ban."));
            } catch (Throwable e1)
            {

            }
        }
        return isSuccess;
    }
    
    public static boolean leftRoom(Connection aConnection, ChessDataInputStream in, ChessDataOutputStream out)
    {
        boolean isSuccess = true;
        try {                        
            long time = System.currentTimeMillis();
            PreparedStatement ps = aConnection.prepareStatement("DELETE FROM " +
                    "user_room " +
                    "  WHERE userid = ? AND roomid = ?;");
            ps.setInt(1, in.readInt()); // userid
            int roomid = in.readInt();
            ps.setInt(2, roomid); // roomid               
            ps.executeUpdate();            
            ps.close();
            
            ps = aConnection.prepareStatement("UPDATE room SET last_update_member_time = ? WHERE roomid = ?;");
            ps.setLong(1, time);
            ps.setInt(2, roomid);
            ps.executeUpdate();
            ps.close();
            
            out.writeShort(Protocol.RESPONSE_LEFT_ROOM_SUCCESSFULLY);      
            isSuccess = true;
        } catch (Exception e)
        {
            isSuccess = false;
            
            PunchLogger.logException("leftRoom : " + e); 
            try {
                out.writeShort(Protocol.RESPONSE_LEFT_ROOM_FAILURE);
                out.writeString16(new String16("Loi: May chu dang ban."));
            } catch (Throwable e1)
            {

            }
        }
        return isSuccess;
    }
    
    public static boolean createRoom(Connection aConnection, ChessDataInputStream in, ChessDataOutputStream out)
    {
        boolean isSuccess = true;
        try {          
            long time = System.currentTimeMillis();
            PreparedStatement ps = aConnection.prepareStatement("INSERT INTO room(roomname, " +
                    "ownerid," +
                    "password," +
                    "last_update_member_time," +
                    "last_update_move," +
                    "last_update_message," +
                    "started) " +
                    "VALUES(?," +
                    "?," +
                    "?," +
                    "?," +
                    "?," +
                    "?," +
                    "?);");
            String roomName = in.readString16().toJavaString();
            ps.setString(1, in.readString16().toJavaString());
            int ownerid = in.readInt();
            ps.setInt(2, ownerid);
            ps.setString(3, in.readString16().toJavaString());
            ps.setLong(4, time);
            ps.setLong(5, time);
            ps.setLong(6, time);
            ps.setBoolean(7, false);            
            ps.executeUpdate();            
            ps.close();
            
            ps = aConnection.prepareStatement("SELECT * FROM room WHERE roomname = ? AND ownerid = ?;");
            ps.setString(1, roomName);
            ps.setInt(2, ownerid);
            ResultSet rs = ps.executeQuery();            
            int roomid = 0;
            if (rs.first())
                roomid = Integer.parseInt(rs.getString("roomid"));
            rs.close();
            ps.close();
            
            if (roomid != 0)
            {                                        
                ps = aConnection.prepareStatement("INSERT INTO user_room(roomid, userid, role, ready) " +
                        "VALUES(?, ?, ?, ?);");
                ps.setInt(1, roomid);
                ps.setInt(2, ownerid);
                ps.setInt(3, 0);
                ps.setBoolean(4, false);
                ps.executeUpdate();
                ps.close();                                
                
                out.writeShort(Protocol.RESPONSE_CREATE_ROOM_SUCCESSFULLY);      
                isSuccess = true;
            }                        
            else
            {
                out.writeShort(Protocol.RESPONSE_CREATE_ROOM_FAILURE);
                out.writeString16(new String16("Loi: Khong xac nhan duoc phong choi vua tao."));
                isSuccess = false;
            }
        } catch (Exception e)
        {
            isSuccess = false;            
            PunchLogger.logException("createRoom : " + e); 
            try {
                out.writeShort(Protocol.RESPONSE_CREATE_ROOM_FAILURE);
                out.writeString16(new String16("Loi: May chu dang ban."));
            } catch (Throwable e1)
            {
            }
        }
        return isSuccess;
    }        
}
