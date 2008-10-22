/*
* Copyright 2004 The Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/* $Id$
 *
 */

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;

/**
 * The simplest possible servlet.
 *
 * @author James Duncan Davidson
 */

public class ChessServlet extends HttpServlet implements Runnable {

    public final static int RESPONSE_LOGIN_SUCCESSFULLY = 1;    
    public final static int RESPONSE_LOGIN_FAILURE = RESPONSE_LOGIN_SUCCESSFULLY + 1;
    
    public final static int RESPONSE_LOGOUT_SUCCESSFULLY = RESPONSE_LOGIN_FAILURE + 1;
    public final static int RESPONSE_LOGOUT_FAILURE = RESPONSE_LOGOUT_SUCCESSFULLY + 1;
    
    public final static int RESPONSE_REGISTER_SUCCESSFULLY = RESPONSE_LOGOUT_FAILURE + 1;
    public final static int RESPONSE_REGISTER_FAILURE = RESPONSE_REGISTER_SUCCESSFULLY + 1;
    
    public final static int RESPONSE_MOVE_ACCEPTED = RESPONSE_REGISTER_FAILURE + 1;
    public final static int RESPONSE_MOVE_REJECTED = RESPONSE_MOVE_ACCEPTED + 1;    
    
    public final static int RESPONSE_NEW_MOVE = RESPONSE_MOVE_REJECTED + 1;
    public final static int RESPONSE_NO_MOVE = RESPONSE_NEW_MOVE + 1;
    
    public final static int RESPONSE_WIN_GAME = RESPONSE_NO_MOVE + 1;
    public final static int RESPONSE_LOSE_GAME = RESPONSE_WIN_GAME + 1;
    public final static int RESPONSE_REQUEST_DRAW_GAME = RESPONSE_LOSE_GAME + 1;
    public final static int RESPONSE_ACCEPTED_DRAW_GAME_SUCCESSFULLY = RESPONSE_REQUEST_DRAW_GAME + 1;
    public final static int RESPONSE_ACCEPTED_DRAW_GAME_FAILURE = RESPONSE_ACCEPTED_DRAW_GAME_SUCCESSFULLY + 1;
    public final static int RESPONSE_REJECTED_DRAW_GAME_SUCCESSFULLY = RESPONSE_ACCEPTED_DRAW_GAME_FAILURE + 1;
    public final static int RESPONSE_REJECTED_DRAW_GAME_FAILURE = RESPONSE_REJECTED_DRAW_GAME_SUCCESSFULLY + 1;
    
    public final static int RESPONSE_NEW_MESSAGE = RESPONSE_REJECTED_DRAW_GAME_FAILURE + 1;
    public final static int RESPONSE_NO_MESSAGE = RESPONSE_NEW_MESSAGE + 1;
    
    public final static int RESPONSE_ROOM_CREATE_SUCCESSFULLY = RESPONSE_NO_MESSAGE + 1;
    public final static int RESPONSE_ROOM_CREATE_FAILURE = RESPONSE_ROOM_CREATE_SUCCESSFULLY + 1;
    
    public final static int RESPONSE_ROOM_JOIN_SUCCESSFULLY = RESPONSE_ROOM_CREATE_FAILURE + 1;
    public final static int RESPONSE_ROOM_JOIN_FAILURE = RESPONSE_ROOM_JOIN_SUCCESSFULLY + 1;
    
    public final static int RESPONSE_UPDATE_ROOMS_LIST = RESPONSE_ROOM_JOIN_FAILURE + 1;
    public final static int RESPONSE_UPDATE_ROOMS_LIST_FAIL = RESPONSE_UPDATE_ROOMS_LIST + 1;
    
    public final static int RESPONSE_UPDATE_ROOM_STATUS = RESPONSE_UPDATE_ROOMS_LIST_FAIL + 1;
    public final static int RESPONSE_UPDATE_ROOM_STATUS_FAIL = RESPONSE_UPDATE_ROOM_STATUS + 1;
    
    public final static int RESPONSE_LEFT_ROOM_SUCCESSFULLY = RESPONSE_UPDATE_ROOM_STATUS_FAIL + 1;    
    public final static int RESPONSE_LEFT_ROOM_FAILURE = RESPONSE_LEFT_ROOM_SUCCESSFULLY + 1;
    
    public final static int RESPONSE_MOVE_SUCESSFULLY = RESPONSE_LEFT_ROOM_FAILURE + 1;    
    public final static int RESPONSE_MOVE_FAILURE = RESPONSE_MOVE_SUCESSFULLY + 1;    
    
    public final static int REQUEST_LOGIN = 100;
    public final static int REQUEST_LOGOUT = REQUEST_LOGIN + 1;
    public final static int REQUEST_REGISTER = REQUEST_LOGOUT + 1;    
    public final static int REQUEST_MOVE = REQUEST_REGISTER + 1;    
    public final static int REQUEST_UPDATE_ROOMS_LIST = REQUEST_MOVE + 1;
    public final static int REQUEST_UPDATE_ROOM_STATUS = REQUEST_UPDATE_ROOMS_LIST + 1;
    public final static int REQUEST_CREATE_ROOM = REQUEST_UPDATE_ROOM_STATUS + 1;
    public final static int REQUEST_JOIN_ROOM = REQUEST_CREATE_ROOM + 1;
    public final static int REQUEST_LEFT_ROOM = REQUEST_JOIN_ROOM + 1;  
    public final static int REQUEST_UPDATE_GAME = REQUEST_LEFT_ROOM + 1;   
    public final static int REQUEST_CHECK_MESSAGE_BOX = REQUEST_UPDATE_GAME + 1;
    public final static int REQUEST_ACCEPTED_DRAW_GAME = REQUEST_CHECK_MESSAGE_BOX + 1;
    public final static int REQUEST_REJECTED_DRAW_GAME = REQUEST_ACCEPTED_DRAW_GAME + 1;
    
    public Connection mConnection;
    public Thread mRunningThread;
    public boolean mIsRunning;
    
    public void init() throws ServletException
    {        
        mIsRunning = true;
        mRunningThread = new Thread(this);
        mRunningThread.start();        
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        //ResourceBundle rb = ResourceBundle.getBundle("LocalStrings",request.getLocale());                
        DataOutputStream dos = new DataOutputStream(response.getOutputStream());        
        
        PreparedStatement ps;
        Connection con = null;        
        ResultSet rs;
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chess", 
                "root", 
                "dddddd");                                        
        } catch (Exception e)
        {
            PunchLogger.logException(e.toString());            
        }
        
        int tag = Integer.parseInt(request.getParameter("t"));        
        switch (tag)
        {
            
            case REQUEST_LOGIN:
                PunchLogger.logException("REQUEST_LOGIN with " 
                        + "username=" + request.getParameter("u")
                        + " password= " + request.getParameter("p"));
                try {
                    ps = con.prepareStatement("SELECT * FROM user_info WHERE username=? AND password=?;");
                    ps.setString(1, request.getParameter("u"));
                    ps.setString(2, request.getParameter("p"));
                    rs = ps.executeQuery();                    
                    
                    if (rs.first()) {
                        ps.close();
                        
                        ps = con.prepareStatement("UPDATE user_info SET is_online='1' WHERE username=? AND password=?");
                        ps.setString(1, request.getParameter("u"));
                        ps.setString(2, request.getParameter("p"));
                        ps.execute();
                        ps.close();
                        
                        //out.print("t=" + RESPONSE_REGISTER_FAILURE + "&c=Someone has register this username. Please try again with another username.");                        
                        updateActivityLog(request.getParameter("u"), con);
                        dos.writeShort(RESPONSE_LOGIN_SUCCESSFULLY);                        
                        dos.writeUTF("Logged in successfully.");                                                                        
                    } 
                    else
                    {
                        ps.close();    
                        dos.writeShort(RESPONSE_LOGIN_FAILURE);                        
                        dos.writeUTF("Login fail. Please check your username/password.");
                    }                                                  
                } catch (Exception e)
                {
                    dos.writeShort(RESPONSE_LOGIN_FAILURE);
                    dos.writeUTF("Login fail. There is an internal server problem.");
                    e.printStackTrace();
                    PunchLogger.logException("Error: " + e.toString());
                    for (int i = 0; i < e.getStackTrace().length; i++)
                        PunchLogger.logException("Error: " + e.getStackTrace()[i].toString());
                }
                break;
                
            case REQUEST_LOGOUT:
                PunchLogger.logException("REQUEST_LOGOUT with " 
                        + "username=" + request.getParameter("u")
                        + " password= " + request.getParameter("p"));
                try {
                    ps = con.prepareStatement("SELECT * FROM user_info WHERE username=? AND password=?;");
                    ps.setString(1, request.getParameter("u"));
                    ps.setString(2, request.getParameter("p"));
                    rs = ps.executeQuery();                    
                    
                    if (rs.first()) {
                        ps.close();
                        
                        ps = con.prepareStatement("UPDATE user_info SET is_online='0' WHERE username=? AND password=?");
                        ps.setString(1, request.getParameter("u"));
                        ps.setString(2, request.getParameter("p"));
                        ps.execute();
                        ps.close();
                        
                        ps = con.prepareStatement("DELETE FROM room WHERE user_name=?;");
                        ps.setString(1, request.getParameter("u"));
                        ps.execute();
                        ps.close();
                        
                        ps = con.prepareStatement("UPDATE room SET join_player=NULL WHERE join_player=?;");
                        ps.setString(1, request.getParameter("u"));
                        ps.execute();
                        ps.close();                                
                        
                        //out.print("t=" + RESPONSE_REGISTER_FAILURE + "&c=Someone has register this username. Please try again with another username.");                        
                        updateActivityLog(request.getParameter("u"), con);
                        dos.writeShort(RESPONSE_LOGOUT_SUCCESSFULLY);                        
                        dos.writeUTF("Logged out successfully.");                        
                    } 
                    else
                    {
                        ps.close();    
                        dos.writeShort(RESPONSE_LOGOUT_FAILURE);                        
                        dos.writeUTF("Login fail. Please check your username/password.");
                    }                                                                                                  
                } catch (Exception e)
                {
                    dos.writeShort(RESPONSE_LOGOUT_FAILURE);
                    dos.writeUTF("Login fail. There is an internal server problem.");
                    e.printStackTrace();
                    PunchLogger.logException("Error: " + e.toString());
                    for (int i = 0; i < e.getStackTrace().length; i++)
                        PunchLogger.logException("Error: " + e.getStackTrace()[i].toString());
                }
                break;
            
            case REQUEST_REGISTER:
                PunchLogger.logException("REQUEST_REGISTER with username=" 
                        + request.getParameter("u")
                        + " password= " + request.getParameter("p"));
                try {
                    ps = con.prepareStatement("SELECT * FROM user_info WHERE username=?;");
                    ps.setString(1, request.getParameter("u"));
                    rs = ps.executeQuery();                    
                    
                    if (rs.first()) {
                        //out.print("t=" + RESPONSE_REGISTER_FAILURE + "&c=Someone has register this username. Please try again with another username.");                        
                        dos.writeShort(RESPONSE_REGISTER_FAILURE);                        
                        dos.writeUTF("Someone registered this username. Please try again with another username.");
                        ps.close();
                        return;
                    }                    
                    
                    ps.close();
                    
                    ps = con.prepareStatement("" +
                                              "INSERT INTO user_info(username, password, is_online, last_active_time)" +
                                              "VALUES(?, ?, ?, ?);");
                    ps.setString(1, request.getParameter("u"));
                    ps.setString(2, request.getParameter("p"));
                    ps.setString(3, "0");
                    ps.setLong(4, System.currentTimeMillis());

                    ps.execute();                    
                    
                    //out.print("t=" + RESPONSE_REGISTER_SUCCESSFULLY);
                    ps.close();                                        
                    updateActivityLog(request.getParameter("u"), con);
                    dos.writeShort(RESPONSE_REGISTER_SUCCESSFULLY); 
                    dos.writeUTF("Your account was created successfully.");                                        
                } catch (Exception e)
                {
                    e.printStackTrace();
                    PunchLogger.logException("Error: " + e.toString());
                    for (int i = 0; i < e.getStackTrace().length; i++)
                        PunchLogger.logException("Error: " + e.getStackTrace()[i].toString());
                }
                break;
            
            case REQUEST_CREATE_ROOM:
                PunchLogger.logException("REQUEST_CREATE_ROOM of username " 
                        + request.getParameter("u")
                        + "password " + request.getParameter("p")
                        + "room_name " + request.getParameter("rn"));
                try {      
                    ps = con.prepareStatement("SELECT * FROM user_info WHERE username=? AND password=?");
                    ps.setString(1, request.getParameter("u"));
                    ps.setString(2, request.getParameter("p"));
                    rs = ps.executeQuery();
                    
                    if (rs != null && rs.next())
                    {
                        if (Integer.parseInt(rs.getString("is_online")) == 0)
                        {
                            ps.close();
                            dos.writeShort(RESPONSE_ROOM_CREATE_FAILURE);
                            dos.writeUTF("You must login first.");
                            return;
                        }
                    }
                    else {
                        ps.close();
                        dos.writeShort(RESPONSE_ROOM_CREATE_FAILURE);
                        dos.writeUTF("Your account is suspended.");
                        return;
                    }                    
                    ps.close();
                    
                    // delete all rooms that is owned by this user
                    ps = con.prepareStatement("DELETE FROM room WHERE user_name=?;");
                    ps.setString(1, request.getParameter("u"));
                    ps.execute();
                    ps.close();
                    
                    // insert one room
                    ps = con.prepareStatement("" +
                                              "INSERT INTO room(room_name, user_name)" +
                                              "VALUES(?, ?);");
                    ps.setString(1, request.getParameter("rn"));
                    ps.setString(2, request.getParameter("u"));
                    ps.execute();                                                                                
                    
                    ps.close();                                        
                    
                    updateActivityLog(request.getParameter("u"), con);
                    dos.writeShort(RESPONSE_ROOM_CREATE_SUCCESSFULLY); 
                    dos.writeUTF("Your room was created successfully.");
                    
                } catch (Exception e)
                {
                    e.printStackTrace();
                    
                    dos.writeShort(RESPONSE_ROOM_CREATE_FAILURE); 
                    dos.writeUTF("Unable to create room.");
                    
                    PunchLogger.logException("Error: " + e.toString());
                    for (int i = 0; i < e.getStackTrace().length; i++)
                        PunchLogger.logException("Error: " + e.getStackTrace()[i].toString());
                }
                break;
           
            case REQUEST_JOIN_ROOM:
                PunchLogger.logException("REQUEST_JOIN_ROOM of username " 
                        + request.getParameter("u")
                        + " password" + request.getParameter("p"));
                try {
                    ps = con.prepareStatement("SELECT * FROM room WHERE user_name=? AND room_name=? AND join_player IS NULL;");
                    ps.setString(1, request.getParameter("hd"));
                    ps.setString(2, request.getParameter("rn"));
                    rs = ps.executeQuery();
                    if (rs != null && rs.next())
                    {                        
                    } 
                    else {
                        dos.writeShort(RESPONSE_ROOM_JOIN_FAILURE);
                        dos.writeUTF("The room was full.");
                        ps.close();
                        return;
                    }
                    ps.close();
                    
                    ps = con.prepareStatement("UPDATE room SET join_player=? WHERE user_name=? AND room_name=?;");
                    ps.setString(1, request.getParameter("u"));
                    ps.setString(2, request.getParameter("hd"));
                    ps.setString(3, request.getParameter("rn"));
                    ps.execute();                                                            
                    ps.close();
                    
                    updateActivityLog(request.getParameter("u"), con);
                    dos.writeShort(RESPONSE_ROOM_JOIN_SUCCESSFULLY);
                    dos.writeUTF(request.getParameter("hd"));
                } catch (Exception e)
                {
                    dos.writeShort(RESPONSE_ROOM_JOIN_FAILURE);
                    dos.writeUTF("The room was full or it was destroyed.");
                    e.printStackTrace();                                        
                    PunchLogger.logException("Error: " + e.toString());
                    for (int i = 0; i < e.getStackTrace().length; i++)
                        PunchLogger.logException("Error: " + e.getStackTrace()[i].toString());
                }
                break;
                
            case REQUEST_UPDATE_ROOM_STATUS:
                PunchLogger.logException("REQUEST_UPDATE_ROOM_STATUS of username " 
                        + request.getParameter("u"));
                try {                                        
                    ps = con.prepareStatement("SELECT * FROM room WHERE user_name=?;");
                    ps.setString(1, request.getParameter("u"));
                    rs = ps.executeQuery();                    
                    
                    if (rs != null && rs.next()) {                 
                       dos.writeShort(RESPONSE_UPDATE_ROOM_STATUS);                        
                       if (rs.getString("join_player") != null)
                       {
                            dos.writeUTF(rs.getString("join_player"));
                       } else
                           dos.writeUTF("");
                    } else
                    {
                        dos.writeShort(RESPONSE_UPDATE_ROOM_STATUS_FAIL);
                        dos.writeUTF("The room was destroyed.");
                    }
                    
                    ps.close();
                } catch (Exception e)
                {
                    dos.writeShort(RESPONSE_UPDATE_ROOM_STATUS_FAIL);
                    dos.writeUTF("Unable to update room status.");
                    e.printStackTrace();                                        
                    PunchLogger.logException("Error: " + e.toString());
                    for (int i = 0; i < e.getStackTrace().length; i++)
                        PunchLogger.logException("Error: " + e.getStackTrace()[i].toString());
                }
                break;
                
            case REQUEST_LEFT_ROOM:
                PunchLogger.logException("REQUEST_LEFT_ROOM of username " 
                        + request.getParameter("u"));
                try {
                    ps = con.prepareStatement("DELETE FROM room WHERE user_name=?;");
                    ps.setString(1, request.getParameter("u"));
                    ps.execute();                                                                                
                    ps.close();
                    
                    ps = con.prepareStatement("UPDATE room SET join_player=NULL WHERE join_player=?;");
                    ps.setString(1, request.getParameter("u"));
                    ps.execute();                    
                    ps.close();
                    
                    updateActivityLog(request.getParameter("u"), con);
                    dos.writeShort(RESPONSE_LEFT_ROOM_SUCCESSFULLY);                                            
                } catch (Exception e)                        
                {
                    dos.writeShort(RESPONSE_LEFT_ROOM_FAILURE);                    
                    e.printStackTrace();                                        
                    PunchLogger.logException("Error: " + e.toString());
                    for (int i = 0; i < e.getStackTrace().length; i++)
                        PunchLogger.logException("Error: " + e.getStackTrace()[i].toString());
                }
                break;
            case REQUEST_UPDATE_ROOMS_LIST:
                PunchLogger.logException("REQUEST_UPDATE_ROOMS_LIST of username ");
                try {
                    ps = con.prepareStatement("SELECT COUNT(room_id) AS 'count' FROM room WHERE join_player IS NULL;");
                    rs = ps.executeQuery();                       
                    
                    rs.next();
                    short count = (short)Integer.parseInt(rs.getString("count"));
                    ps.close();  
                    
                    ps = con.prepareStatement("SELECT * FROM room WHERE join_player IS NULL;");                    
                    rs = ps.executeQuery();                                           
                    
                    dos.writeShort(RESPONSE_UPDATE_ROOMS_LIST);    
                    dos.writeShort(count);
                    
                    while (rs != null && rs.next())                    
                    {
                       dos.writeUTF(rs.getString("room_name"));
                       dos.writeUTF(rs.getString("user_name"));
                    }
                    ps.close();
                    
                    updateActivityLog(request.getParameter("u"), con);                    
                } catch (Exception e)                        
                {
                    dos.writeShort(RESPONSE_LEFT_ROOM_FAILURE);
                    e.printStackTrace();                                        
                    PunchLogger.logException("Error: " + e.toString());
                    for (int i = 0; i < e.getStackTrace().length; i++)
                        PunchLogger.logException("Error: " + e.getStackTrace()[i].toString());
                }
                break;
            case REQUEST_MOVE:
                PunchLogger.logException("REQUEST_MOVE of username = " + 
                        request.getParameter("u"));
                try {                                        
                    ps = con.prepareStatement("SELECT * FROM room WHERE user_name=? AND join_player=?;");  
                    ps.setString(1, request.getParameter("hn"));
                    ps.setString(2, request.getParameter("cl"));
                    rs = ps.executeQuery();                                                               
                    
                    int room_id = -1;
                    
                    if (rs != null && rs.next())
                    {
                        room_id = Integer.parseInt(rs.getString("room_id"));                        
                        ps.close();    
                    }                    
                    
                    if (room_id != -1)
                    {                                                                                       
                        ps = con.prepareStatement("UPDATE room SET " +
                                    "move_user_name=?, " +
                                    "move_xsrc=?, " +
                                    "move_ysrc=?," +
                                    "move_xdest=?," +
                                    "move_ydest=? " +
                                    "WHERE room_id=?");
                            ps.setString(1, request.getParameter("u"));
                            ps.setString(2, request.getParameter("sx"));
                            ps.setString(3, request.getParameter("sy"));
                            ps.setString(4, request.getParameter("dx"));
                            ps.setString(5, request.getParameter("dy"));
                            ps.setString(6, ""+room_id);
                        ps.execute();
                        ps.close();
                        updateActivityLog(request.getParameter("u"), con);
                        dos.writeShort(RESPONSE_MOVE_ACCEPTED); 
                    }
                } catch (Exception e)                        
                {                    
                    e.printStackTrace();                                        
                    PunchLogger.logException("Error: " + e.toString());
                    for (int i = 0; i < e.getStackTrace().length; i++)
                        PunchLogger.logException("Error: " + e.getStackTrace()[i].toString());
                }
                break;
            case REQUEST_UPDATE_GAME:
                PunchLogger.logException("REQUEST_UPDATE_GAME of username ");
                try {                                        
                    ps = con.prepareStatement("SELECT * FROM room WHERE user_name=?;");  
                    ps.setString(1, request.getParameter("hn"));
                    //ps.setString(2, request.getParameter("cl"));
                    rs = ps.executeQuery();                                                               
                    
                    int room_id = -1;
                    
                    if (rs != null && rs.next())
                    {
                        room_id = Integer.parseInt(rs.getString("room_id"));                        
                        if (rs.getString("move_user_name") == null || rs.getString("move_user_name").equals(request.getParameter("u")))
                        {
                            dos.writeShort(RESPONSE_NO_MOVE);
                        } 
                        else
                        {
                            dos.writeShort(RESPONSE_NEW_MOVE);
                            dos.writeUTF(rs.getString("move_user_name"));
                            dos.writeShort((short)Integer.parseInt(rs.getString("move_xsrc")));
                            dos.writeShort((short)Integer.parseInt(rs.getString("move_ysrc")));
                            dos.writeShort((short)Integer.parseInt(rs.getString("move_xdest")));
                            dos.writeShort((short)Integer.parseInt(rs.getString("move_ydest")));                                                       
                        }                            
                    } else 
                    {
                       dos.writeShort(RESPONSE_WIN_GAME);
                       dos.writeUTF("Your opponent quited the game. So you are the winner.");
                    }
                    ps.close();
                } catch (Exception e)                        
                {        
                    dos.writeShort(RESPONSE_NO_MOVE);
                    e.printStackTrace();                                        
                    PunchLogger.logException("Error: " + e.toString());
                    for (int i = 0; i < e.getStackTrace().length; i++)
                        PunchLogger.logException("Error: " + e.getStackTrace()[i].toString());
                }
                break;            
        }
        
        try {
            con.close();
            con = null;
        } catch (Exception e){};
        
        // write to real output
        //response.addHeader("connection", "keep-alive");
        response.setContentType("application/octet-stream");
        response.setContentLength(dos.size()); 
        //PunchLogger.logException("OutputStream size: " + dos.size());
        response.flushBuffer();                
    }    
   
    
    public boolean isLoggedOut(String username, Connection con) throws Exception
    {        
        PreparedStatement ps = con.prepareStatement("SELECT * FROM user_info WHERE username=?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs != null && rs.next())
        {
            if (Integer.parseInt(rs.getString("is_online")) == 0)
            {
                return true;
            }
        }
        ps.close();
        return false;        
    }
    
    public void updateActivityLog(String username, Connection con) throws Exception
    {
        PreparedStatement ps = con.prepareStatement("UPDATE user_info SET last_active_time=? WHERE username=?");
        ps.setLong(1, System.currentTimeMillis());
        ps.setString(2, username);
        ps.execute();
        //ResultSet rs = ps.executeQuery();
        //rs.updateLong("last_active_time", System.currentTimeMillis());
        ps.close();
    }
    
    public void logoutAllTimeoutUser(Connection con) throws Exception
    {
        long clock = System.currentTimeMillis();
        long outdate = clock - 60 * 1000 * 30; // log out all user non activity in 10 minutes
        
        PreparedStatement ps = con.prepareStatement("SELECT * FROM user_info WHERE is_online=1 AND last_active_time<=?");
        ps.setLong(1, outdate);
        ResultSet rs = ps.executeQuery();
        while (rs != null && rs.next())
        {
            doLogout(rs.getString("username"), con);
        }
        ps.close();
    }    
    
    public void doLogout(String username, Connection con) throws Exception
    {
        PreparedStatement ps = con.prepareStatement("UPDATE user_info SET is_online=0 WHERE username=?");
        ps.setString(1, username);
        ps.execute();
        ps.close();
        
        ps = con.prepareStatement("DELETE FROM room WHERE user_name=?;");
        ps.setString(1, username);
        ps.execute();
        ps.close();
                        
        ps = con.prepareStatement("UPDATE room SET join_player=NULL WHERE join_player=?;");
        ps.setString(1, username);
        ps.execute();
        ps.close();
    }
    
    public void destroy()
    {
        mIsRunning = false;
    }
    
    public void run()
    {
        try {
            while (mIsRunning)
            {
                PunchLogger.logException("Do server loop...");
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chess", 
                    "root", 
                    "dddddd");                                        

                logoutAllTimeoutUser(con);  
                con.close();
                Thread.sleep(30000);
            }            
        } catch (Exception e)
        {
             PunchLogger.logException(e.toString());            
        }
    }
}



