package chess.core;

import chess.util.Protocol;
import chess.util.PunchLogger;
import chess.core.String16;
import chess.util.ChessDataInputStream;
import chess.util.ChessDataOutputStream;
import java.io.*;
import java.sql.*;
import java.util.Vector;

public class Account
{        
    public int mDeviceType;
    public int mClientVersionMajor;
    public int mClientVersionMinor;    
    public boolean mIsOnline;
    public int mUserID = -1;
    public String mUserName;
    public String mPassword;
    public long mLastCheckInbox;
    public long mLastCheckRoomStatus;
    public long mLastStillOnline;
    public long mLastCheckGameStatus;
    public int mWinCount;
    public int mLoseCount;
    public int mDrawCount;
    public long mRegisterTime;
    public int mRoomID;
    public boolean mIsReady;
    
    public Account(String username, String password)
    {
        mUserID = 0;
        mUserName = username;
        mPassword = password;
        mIsOnline = false;
        mWinCount = 0;
        mLoseCount = 0;
        mDrawCount = 0;
        mLastCheckInbox = 0;
        mLastCheckRoomStatus = 0;
        mLastStillOnline = 0;
        mLastCheckGameStatus = 0;
        mRegisterTime = 0;
        mRoomID = 0;
        mIsReady = false;
    }
    
    public Account()
    {
        
    }
    
    public static boolean isValidUsername(String s)
    {
        if (s == null)
        {
            return false;
        }
        if (s.length() < 3)
        {
            return false;
        }

        for (char c : s.toCharArray())
        {
            if (!((c == '_') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')))
            {
                return false;
            }
        }

        return true;
    }

    public static ResultSet getAccountRecord(Connection aConnection, String aUsername) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("SELECT * FROM userinfo WHERE username = ? ;");
        ps.setString(1, aUsername);
        return ps.executeQuery();
    }
    
    public static ResultSet getAccountRecord(Connection aConnection, int aUserID) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("SELECT * FROM userinfo WHERE userid = ? ;");
        ps.setInt(1, aUserID);
        return ps.executeQuery();
    }

    public static ResultSet getAccountRecord(Connection aConnection, String aUsername, String fields) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("SELECT " + fields + " FROM userinfo WHERE username = ? ;");
        ps.setString(1, aUsername);
        return ps.executeQuery();
    }
    
    public static ResultSet getAccountRecord(Connection aConnection, int aUserID, String fields) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("SELECT " + fields + " FROM userinfo WHERE userid = ? ;");
        ps.setInt(1, aUserID);
        return ps.executeQuery();
    }

    public static boolean isExist(Connection aConnection, String aUsername) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("SELECT userid FROM userinfo WHERE username = ? ;");
        ps.setString(1, aUsername);
        ResultSet rs = ps.executeQuery();
        boolean result = rs.first();
        rs.close();
        ps.close();

        return result;
    }
    
    public static boolean isExist(Connection aConnection, int aUserID) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("SELECT userid FROM userinfo WHERE userid = ? ;");
        ps.setInt(1, aUserID);
        ResultSet rs = ps.executeQuery();
        boolean result = rs.first();
        rs.close();
        ps.close();

        return result;
    }

    /**
     * Handle request to create an account
     *
     * @param aConnection
     * @param aIn
     * @param aOut
     */
    public boolean createAccount(Connection aConnection, ChessDataInputStream aIn, ChessDataOutputStream aOut)
    {
        boolean isSuccess = true;
        try
        {
            if (aConnection == null)
            {
                aOut.writeShort(Protocol.RESPONSE_REGISTER_FAILURE);
                aOut.writeString16(new String16("L?i: Máy ch? quá t?i."));
                return false;
            }

            mUserName = aIn.readUTF().toUpperCase();
            mPassword = aIn.readUTF().toUpperCase();

            if (mUserName == null || mPassword == null)
            {
                aOut.writeShort(Protocol.RESPONSE_REGISTER_FAILURE);
                aOut.writeString16(new String16("L?i: Tên truy nh?p ho?c m?t kh?u không h?p l?."));                
                return false;
            }

            if (!isValidUsername(mUserName) || !isValidUsername(mPassword))
            {
                aOut.writeShort(Protocol.RESPONSE_REGISTER_FAILURE);
                aOut.writeString16(new String16("L?i: Tên truy nh?p và m?t kh?u ch? bao g?m s?, ch? cái và g?ch d??i.")); 
                return false;
            }
            
            if (isExist(aConnection, mUserName))
            {
                aOut.writeShort(Protocol.RESPONSE_REGISTER_FAILURE);
                aOut.writeString16(new String16("L?i: Tên truy nh?p ?ã ???c s? d?ng.")); 
                return false;
            }
            else
            {
                PreparedStatement ps = aConnection.prepareStatement(
                        "INSERT INTO userinfo(username, password, online, " +
                        "last_received_online_time, roomid, last_check_message_time, " +
                        "last_check_game_status, last_check_room_status, " +
                        "wincount, losecount, drawcount, register_time) VALUES " +
                        "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
                ps.setString(1, mUserName);
                ps.setString(2, mPassword);
                ps.setBoolean(3, false);
                ps.setLong(4, 0);
                ps.setInt(5, 0); // room 0 will never be exist!!!
                ps.setLong(6, 0);
                ps.setLong(7, 0);
                ps.setLong(8, 0);
                ps.setInt(9, 0);
                ps.setInt(10, 0);
                ps.setInt(11, 0);
                ps.setLong(12, System.currentTimeMillis());
                
                int result = ps.executeUpdate();
                ps.close();
                
                if (result != 1) // insert fail
                {
                    aOut.writeShort(Protocol.RESPONSE_REGISTER_FAILURE);
                    aOut.writeString16(new String16("L?i: Tên truy nh?p ho?c m?t kh?u không h?p l?."));                
                    return false;
                }
                else
                {
                    ResultSet rsTemp = getAccountRecord(aConnection, mUserName, "userid");
                    if (rsTemp != null && rsTemp.first())
                    {
                        int userid = Integer.parseInt(rsTemp.getString("userid"));
                        aOut.writeShort(Protocol.RESPONSE_REGISTER_SUCCESSFULLY);
                        aOut.writeInt(userid);    
                        rsTemp.close();
                        return true;
                    }
                    else
                    {
                        aOut.writeShort(Protocol.RESPONSE_REGISTER_FAILURE);
                        aOut.writeString16(new String16("L?i: Máy ch? quá t?i."));
                        return false;
                    }
                }
            }                         
        } catch (Exception e)
        {
            isSuccess = false;
            PunchLogger.logException("Create account", e);
            try
            {                
                aOut.writeShort(Protocol.RESPONSE_REGISTER_FAILURE);
                aOut.writeString16(new String16("L?i: Máy ch? quá t?i."));
            } catch (Throwable e1)
            {

            }
        }
        return isSuccess;
    }
    
    /**
     * Login an acount. Just update the online status and still online time
     * @param aConnection
     * @param aIn
     * @param aOut
     * @param aContext
     * @return
     */
    public boolean login(Connection aConnection, ChessDataInputStream aIn, ChessDataOutputStream aOut)
    {
        boolean isSuccess = true;
        try {
            PreparedStatement ps = aConnection.prepareStatement("SELECT * FROM userinfo WHERE username = ? AND password = ?;");
            ps.setString(1, mUserName);
            ps.setString(2, mPassword);
            ResultSet rs = ps.executeQuery();
            if (rs != null && rs.first())
            {            
                rs.close();
                ps.close();
                PreparedStatement updatePS = aConnection.prepareStatement("UPDATE userinfo SET online = ?, last_received_online_time = ? WHERE username = ?;");
                updatePS.setBoolean(1, true);
                updatePS.setLong(2, System.currentTimeMillis());
                updatePS.setString(3, mUserName);
                updatePS.executeUpdate();
                updatePS.close();   
                aOut.writeShort(Protocol.RESPONSE_LOGIN_SUCCESSFULLY);
                aOut.writeInt(mUserID);
                isSuccess = true;
            }
            else
            {
                rs.close();
                ps.close();
                aOut.writeShort(Protocol.RESPONSE_LOGIN_FAILURE);
                aOut.writeString16(new String16("Loi: Khong tim thay ten dang nhap hoac mat khau khong dung."));
                isSuccess = false;
            }
        } catch (Exception e)
        {
            isSuccess = false;
            PunchLogger.logException("Login account ", e);
            try
            {                
                aOut.writeShort(Protocol.RESPONSE_LOGIN_FAILURE);
                aOut.writeString16(new String16("L?i: Máy ch? quá t?i."));
            } catch (Throwable e1)
            {

            }
        }
        return isSuccess;
    }    
    
    /**
     * Logout an acount. Just update the online status and still online time
     * @param aConnection
     * @param aIn
     * @param aOut
     * @param aContext
     * @return
     */
    public boolean logout(Connection aConnection, ChessDataInputStream aIn, ChessDataOutputStream aOut)
    {
        boolean isSuccess = true;
        try {
            PreparedStatement ps = aConnection.prepareStatement("SELECT * FROM userinfo WHERE username = ? AND password = ?;");
            ps.setString(1, mUserName);
            ps.setString(2, mPassword);
            ResultSet rs = ps.executeQuery();
            if (rs != null && rs.first())
            {            
                rs.close();
                ps.close();
                PreparedStatement updatePS = aConnection.prepareStatement("UPDATE userinfo SET online = ?, last_received_online_time = ? WHERE username = ?;");
                updatePS.setBoolean(1, false);
                updatePS.setLong(2, System.currentTimeMillis());
                updatePS.setString(3, mUserName);
                updatePS.executeUpdate();
                updatePS.close();   
                aOut.writeShort(Protocol.RESPONSE_LOGOUT_SUCCESSFULLY);
                aOut.writeInt(mUserID);                                
                
                isSuccess = true;
            }
            else
            {
                rs.close();
                ps.close();
                aOut.writeShort(Protocol.RESPONSE_LOGOUT_FAILURE);
                aOut.writeString16(new String16("Loi: Khong tim thay ten dang nhap."));
                isSuccess = false;
            }
        } catch (Exception e)
        {
            isSuccess = false;
            PunchLogger.logException("Logout account ", e);
            try
            {                
                aOut.writeShort(Protocol.RESPONSE_LOGOUT_FAILURE);
                aOut.writeString16(new String16("L?i: Máy ch? quá t?i."));
            } catch (Throwable e1)
            {

            }
        }
        return isSuccess;
    }  
    
    /**
     * Query new messages from last checking time.
     * @param aConnection
     * @param aIn
     * @param aOut
     * @param aContext
     * @return
     */
    public boolean queryMessages(Connection aConnection, ChessDataInputStream aIn, ChessDataOutputStream aOut)
    {
        boolean isSuccess = true;
        try {
            PreparedStatement ps = aConnection.prepareStatement("SELECT * FROM messages WHERE receiplientid = ? AND time > ?;");
            ps.setInt(1, mUserID);
            ps.setLong(2, mLastCheckInbox);
            ResultSet rs = ps.executeQuery();
            Vector messageVector = new Vector();
            while (rs != null && rs.next())
            {
                MessageRecord aMessage = new MessageRecord();
                aMessage.mSenderID = Integer.parseInt(rs.getString("senderid"));
                aMessage.mSenderName = new String16(rs.getString("sendername"));
                aMessage.mMessage = new String16(rs.getString("message"));
                aMessage.mTimeSent = Long.parseLong("time");
                messageVector.addElement(aMessage);
            }                                    
            rs.close();
            ps.close();
            
            long temp_time = System.currentTimeMillis();
            updateLastCheckInbox(aConnection, mUserID, temp_time);
            mLastCheckInbox = temp_time;
            
            aOut.writeShort(Protocol.RESPONSE_NEW_MESSAGES);
            aOut.writeInt(messageVector.size());
            for (int i = 0; i < messageVector.size(); i++)            
            {
                MessageRecord aMessage = (MessageRecord)messageVector.elementAt(i);
                aMessage.writeToStream(aOut);
            }
            messageVector.removeAllElements();
            messageVector = null;
            isSuccess = true;
            
        } catch (Exception e)
        {
            isSuccess = false;
            PunchLogger.logException("Query messages ", e);
            try
            {                
                aOut.writeShort(Protocol.RESPONSE_MESSAGES_QUERY_FAILURE);
                aOut.writeString16(new String16("Loi: May chu qua tai."));
            } catch (Throwable e1)
            {

            }
        }        
        return isSuccess;
    }
    
    public boolean queryFriendList(Connection aConnection, ChessDataInputStream aIn, ChessDataOutputStream aOut)
    {
        boolean isSuccess = true;
        try {
            PreparedStatement ps = aConnection.prepareStatement("SELECT * FROM friend WHERE userid1 = ?;");
            ps.setInt(1, mUserID);            
            ResultSet rs = ps.executeQuery();
            Vector friendVector = new Vector();            
            while (rs != null && rs.next())
            {                
                FriendRecord aFriend = new FriendRecord();                                
                aFriend.mID = Integer.parseInt(rs.getString("userid2"));
                aFriend.mIsRequest = Boolean.parseBoolean(rs.getString("stillrequest"));                
                friendVector.addElement(aFriend);                
            }
            rs.close();
            ps.close();            
            
            aOut.writeShort(Protocol.RESPONSE_NEW_FRIENDS_LIST);
            aOut.writeInt(friendVector.size());
            for (int i = 0; i < friendVector.size(); i++)
            {
                FriendRecord aFriend = (FriendRecord) friendVector.elementAt(i);
                Account aFriendAccount = Account.getAccountInstance(aConnection, aFriend.mID);                
                aFriend.mName = new String16(aFriendAccount.mUserName);                                
                aFriend.mIsOnline = aFriendAccount.mIsOnline;                
                aFriend.mWinCount = aFriendAccount.mWinCount;                
                aFriend.mLoseCount = aFriendAccount.mLoseCount;                
                aFriend.mDrawCount = aFriendAccount.mDrawCount;                
                aFriend.writeToStream(aOut);
            }  
            friendVector.removeAllElements();
            friendVector = null;
            isSuccess = true;
        } catch (Exception e)
        {
            isSuccess = false;
            PunchLogger.logException("Query friend list ", e);
            try
            {                
                aOut.writeShort(Protocol.RESPONSE_FRIENDS_QUERY_FAILURE);
                aOut.writeString16(new String16("Loi: May chu qua tai."));
            } catch (Throwable e1)
            {

            }
        }
        return isSuccess;
    }

    public static void delete(Connection aConnection, String aName) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("DELETE FROM userinfo WHERE username = ?");
        ps.setString(1, aName);
        ps.executeUpdate();
        ps.close();
    }
    
    public static void delete(Connection aConnection, int aUserID) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("DELETE FROM userinfo WHERE userid = ?");
        ps.setInt(1, aUserID);
        ps.executeUpdate();
        ps.close();
    }     
    
    public static Account getAccountInstance(Connection aConnection, String aUsername) throws SQLException
    {
        Account aNewAccount = new Account();
        aNewAccount.getAccountInformation(aConnection, aUsername);
        return aNewAccount;
    }
    
    public static Account getAccountInstance(Connection aConnection, int aUserId) throws SQLException
    {
        Account aNewAccount = new Account();
        aNewAccount.getAccountInformation(aConnection, aUserId);
        return aNewAccount;
    }
    
    public boolean getAccountInformation(Connection aConnection, int aUserID) throws SQLException
    {
        boolean userExist = false;
        
        ResultSet rs = getAccountRecord(aConnection, aUserID);
        if (rs != null && rs.first())
        {
            mUserID = Integer.parseInt(rs.getString("userid"));
            mUserName = rs.getString("username");
            mPassword = rs.getString("password");
            mIsOnline = Boolean.parseBoolean(rs.getString("online"));
            mWinCount = Integer.parseInt(rs.getString("wincount"));
            mLoseCount = Integer.parseInt(rs.getString("losecount"));
            mDrawCount = Integer.parseInt(rs.getString("drawcount"));            
            mRoomID = Integer.parseInt(rs.getString("roomid"));
            mLastStillOnline = Long.parseLong(rs.getString("last_received_online_time"));
            mLastCheckInbox = Long.parseLong(rs.getString("last_check_message_time"));
            mLastCheckRoomStatus = Long.parseLong("last_check_room_status");
            mLastCheckGameStatus = Long.parseLong("last_check_game_status");
            
            userExist = true;
        }
        rs.close();
        
        
        
        return userExist;
    }
    
    public boolean getAccountInformation(Connection aConnection, String aUserName) throws SQLException
    {
        boolean userExist = false;
        
        ResultSet rs = getAccountRecord(aConnection, aUserName);
        if (rs != null && rs.first())
        {
            mUserID = Integer.parseInt(rs.getString("userid"));
            mUserName = rs.getString("username");
            mPassword = rs.getString("password");
            mIsOnline = Boolean.parseBoolean(rs.getString("online"));
            mWinCount = Integer.parseInt(rs.getString("wincount"));
            mLoseCount = Integer.parseInt(rs.getString("losecount"));
            mDrawCount = Integer.parseInt(rs.getString("drawcount"));            
            mRoomID = Integer.parseInt(rs.getString("roomid"));
            mLastStillOnline = Long.parseLong(rs.getString("last_received_online_time"));
            mLastCheckInbox = Long.parseLong(rs.getString("last_check_message_time"));
            mLastCheckRoomStatus = Long.parseLong("last_check_room_status");
            mLastCheckGameStatus = Long.parseLong("last_check_game_status");
            
            userExist = true;
        }
        rs.close();
        
        return userExist;
    }
    
    public static void updateStillOnline(Connection aConnection, int aUserID, long aTime) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("UPDATE userinfo SET last_received_online_time = ? WHERE userid = ?;");
        ps.setLong(1, aTime);
        ps.setInt(2, aUserID);
        ps.executeUpdate();
        ps.close();
    }            
    
    public boolean doUpdateStillOnline(Connection aConnection, ChessDataInputStream aIn, ChessDataOutputStream aOut)
    {
        boolean isSuccess = true;
        try {
            updateStillOnline(aConnection, mUserID, System.currentTimeMillis());
            aOut.writeShort(Protocol.RESPONSE_STILL_ONLINE_SUCCESSFULLY);
            isSuccess = true;
        } catch (Exception e)
        {
            isSuccess = false;
            PunchLogger.logException("doUpdateStillOnline ", e);
            try
            {                
                aOut.writeShort(Protocol.RESPONSE_STILL_ONLINE_FAILURE);
                aOut.writeString16(new String16("Loi: May chu qua tai."));
            } catch (Throwable e1)
            {

            }
        }
        return isSuccess;
    }
    
    public static void updateLastCheckInbox(Connection aConnection, int aUserID, long aTime) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("UPDATE userinfo SET last_check_message_time = ? WHERE userid = ?;");
        ps.setLong(1, aTime);
        ps.setInt(2, aUserID);
        ps.executeUpdate();
        ps.close();
    }           
    
    public static void updateLastCheckRoomStatus(Connection aConnection, int aUserID, long aTime) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("UPDATE userinfo SET last_check_room_status = ? WHERE userid = ?;");
        ps.setLong(1, aTime);
        ps.setInt(2, aUserID);
        ps.executeUpdate();
        ps.close();
    }   
    
    public static void updateLastCheckGameStatus(Connection aConnection, int aUserID, long aTime) throws SQLException
    {
        PreparedStatement ps = aConnection.prepareStatement("UPDATE userinfo SET last_check_game_status = ? WHERE userid = ?;");
        ps.setLong(1, aTime);
        ps.setInt(2, aUserID);
        ps.executeUpdate();
        ps.close();
    }        
}


