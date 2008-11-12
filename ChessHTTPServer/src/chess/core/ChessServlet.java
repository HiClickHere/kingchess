package chess.core;

import chess.util.Protocol;
import chess.core.String16;
import chess.util.ChessDataInputStream;
import chess.util.ChessDataOutputStream;
import java.io.*;
import java.sql.*;
import chess.util.PunchLogger;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import javax.sql.DataSource;

abstract public class ChessServlet extends HttpServlet
{
    public static final int REQUEST_TYPE_END_COMMUNICATION = 2000;
    public static final int IDLE_TIME_OUT = (40) * 1000;
    private String mDBURL;
    private String mDBUsername;
    private String mDBPassword;
    private String mDBDriver;

    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        System.out.println("Loading underlying JDBC driver...");
        try
        {
            // log
            String logFilename = getServletContext().getInitParameter("LogFile");
            if (logFilename != null)
            {
                PunchLogger.ERROR_LOG_FILE = logFilename;
            }                        

            PunchLogger.logException("Chess HTTP started!");
        } catch (Exception e)
        {
            PunchLogger.logException("Chess HTTP has error!");
            e.printStackTrace();
        }
        
        mDBURL = getServletContext().getInitParameter("dbURL");
        mDBUsername = getServletContext().getInitParameter("dbUsername");
        mDBPassword = getServletContext().getInitParameter("dbPassword");
        mDBDriver = getServletContext().getInitParameter("dbDriver");
        
        System.out.println("Done.");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException
    {
        Connection connection = null;

        ByteArrayOutputStream _outputBuffer;
        DataOutputStream outputStream;
        int currRequest;        

        if (verifyHTTPHeader(request))
        {            
            _outputBuffer = new ByteArrayOutputStream();
            outputStream = new DataOutputStream(_outputBuffer);

            ChessDataInputStream in = new ChessDataInputStream(request.getInputStream());
            DataOutputStream out = new DataOutputStream(response.getOutputStream());

            ByteArrayOutputStream tempBuffer = new ByteArrayOutputStream();
            ChessDataOutputStream tempOutputStream = new ChessDataOutputStream(tempBuffer);

            try
            {
                connection = getDatabaseConnection(mDBDriver, mDBURL, mDBUsername, mDBPassword);                
                while (true)
                {
                    try
                    {
                        currRequest = in.readShort();
                    }
                    catch(EOFException eEOF)
                    {
                        break;
                    }

                    tempOutputStream.reset();
                    handleNextRequest(currRequest, connection, in, tempOutputStream);                    
                }

                tempOutputStream.reset();                
                outputStream.write(tempBuffer.toByteArray());
                response.addHeader("connection", "keep-alive");
                response.setContentType("application/octet-stream");
                response.setContentLength(outputStream.size());
                out.write(_outputBuffer.toByteArray());
                response.flushBuffer();

                connection.close();
                connection = null;
            } catch (Exception e)
            {                
                PunchLogger.logException("doPost", e);
                e.printStackTrace();
            } finally
            {
                response.flushBuffer();
                if (connection != null)
                {
                    try
                    {
                        connection.close();                    
                    } catch (Exception ex)
                    {
                        PunchLogger.logException("doPost", ex);
                    }
                }
            }
        }

    }

    protected boolean verifyHTTPHeader(HttpServletRequest aRequest)
    {
        return true;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " +
                "Transitional//EN\">\n" +
                "<HTML>\n" +
                "<HEAD><TITLE>Chess Online</TITLE></HEAD>\n" +
                "<BODY>\n" +
                "<H1>This feature is not supported!</H1>\n");
        out.println(request.getLocalAddr());
        out.println("</BODY></HTML>");
    }
    
    public boolean handleNextRequest(int requestType, Connection aConnection, ChessDataInputStream in, ChessDataOutputStream out) throws Exception
    {
        boolean isSuccess = true;
        Account aAccount;
        Room aRoom;
        switch (requestType)
        {
            case Protocol.REQUEST_REGISTER:
                aAccount = new Account(in.readString16().toJavaString(), in.readString16().toJavaString());
                isSuccess = aAccount.createAccount(aConnection, in, out);
                aAccount = null;
                break;            
            case Protocol.REQUEST_LOGIN:
                aAccount = new Account(in.readString16().toJavaString(), in.readString16().toJavaString());
                isSuccess = aAccount.login(aConnection, in, out);
                aAccount = null;
                break;
            case Protocol.REQUEST_LOGOUT:
                aAccount = new Account(in.readString16().toJavaString(), in.readString16().toJavaString());
                isSuccess = aAccount.logout(aConnection, in, out);
                aAccount = null;
                break;
            case Protocol.REQUEST_UPDATE_MAILBOX:
                aAccount = Account.getAccountInstance(aConnection, in.readInt());
                if (!aAccount.mIsOnline)
                    aAccount.queryMessages(aConnection, in, out);
                else {
                    out.writeShort(Protocol.RESPONSE_MESSAGES_QUERY_FAILURE);
                    out.writeString16(new String16("Loi: Ban phai dang nhap."));
                }
                aAccount = null;
                break;       
            case Protocol.REQUEST_NEED_FRIENDS_LIST:
                aAccount = Account.getAccountInstance(aConnection, in.readInt());
                if (!aAccount.mIsOnline)                    
                    aAccount.queryFriendList(aConnection, in, out);
                else {
                    out.writeShort(Protocol.RESPONSE_FRIENDS_QUERY_FAILURE);
                    out.writeString16(new String16("Loi: Ban phai dang nhap."));
                }
                aAccount = null;
                break;
            case Protocol.REQUEST_STILL_ONLINE:
                aAccount = Account.getAccountInstance(aConnection, in.readInt());
                if (!aAccount.mIsOnline)                    
                    aAccount.doUpdateStillOnline(aConnection, in, out);
                else {
                    out.writeShort(Protocol.RESPONSE_STILL_ONLINE_FAILURE);
                    out.writeString16(new String16("Loi: Ban phai dang nhap."));
                }
                aAccount = null;
                break;
            case Protocol.REQUEST_UPDATE_MY_GAME:
                aAccount = Account.getAccountInstance(aConnection, in.readInt());
                if (!aAccount.mIsOnline)
                {
                    aRoom = Room.getRoomInstance(aConnection, in.readInt());
                    if (aAccount.mLastCheckRoomStatus <= aRoom.mLastUpdateMember)
                    {
                        aRoom.responseMemberList(aConnection, in, out);                    
                    }                
                    if (aAccount.mLastCheckRoomStatus <= aRoom.mLastUpdateMoves)
                    {
                        aRoom.responseMoveList(aAccount.mLastCheckRoomStatus, aConnection, in, out);
                    }
                    if (aAccount.mLastCheckRoomStatus <= aRoom.mLastUpdateMessages)
                    {
                        aRoom.responseMessageList(aAccount.mLastCheckRoomStatus, aConnection, in, out);
                    }
                }
                break;
            case Protocol.REQUEST_CREATE_ROOM:
                Room.createRoom(aConnection, in, out);
                break;
            case Protocol.REQUEST_JOIN_ROOM:
                Room.joinRoom(aConnection, in, out);
                break;
            case Protocol.REQUEST_LEFT_ROOM:
                Room.leftRoom(aConnection, in, out);
                break;
            case Protocol.REQUEST_SEND_MESSAGE_INROOM:
                Room.sendMessage(aConnection, in, out);
                break;
            case Protocol.REQUEST_I_DID_A_MOVE:
                Room.doMove(aConnection, in, out);                
                break;
        }
        return isSuccess;
    }
    
    private Connection getDatabaseConnection(String driver, String url, String username, String password) throws Exception
    {
        Class.forName(driver).newInstance();
        return DriverManager.getConnection(url, username, password);                                        
    }
}
