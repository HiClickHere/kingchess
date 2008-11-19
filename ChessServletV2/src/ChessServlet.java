
import util.ChessDataOutputStream;
import util.ChessDataInputStream;
import util.PunchLogger;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import util.ChessServerResponse;
import util.String16;

/**
 * Chess Servlet.
 *
 * @author dong
 */
public class ChessServlet
        extends HttpServlet
        implements Runnable {

    public Connection mConnection;
    public static final int IDLE_TIME_OUT = (60) * 1000;
    public static final int CHALLENGE_TIMEOUT = 30000;
    public static final int STATUS_OFFLINE = 0;
    public static final int STATUS_ONLINE = 1;
    public static final int STATUS_PLAYING = 2;
    public static final int STATUS_WAITING = 3;
    public static final int MYTURN_OFF = 0;
    public static final int MYTURN_ON = 1;
    public static final int MYTURN_WAIT = 2;
    private String mDBURL;
    private String mDBUsername;
    private String mDBPassword;
    private String mDBDriver;
    private boolean mIsStarted;

    @Override
    public void init() throws ServletException {
        mDBURL = "jdbc:mysql://localhost:3306/chess2";
        mDBUsername = "root";
        mDBPassword = "dddddd";
        mDBDriver = "com.mysql.jdbc.Driver";

        mIsStarted = true;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void destroy() {
        mIsStarted = false;
    }

    private Connection getDatabaseConnection(String driver, String url, String username, String password) throws Exception {
        Class.forName(driver).newInstance();
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        //PunchLogger.logException("ADMIN: Handle REQUEST by doPost.");
        ChessDataOutputStream dos = new ChessDataOutputStream(response.getOutputStream());
        ChessDataInputStream in = new ChessDataInputStream(request.getInputStream());

        PreparedStatement ps;

        try {
            mConnection = getDatabaseConnection(mDBDriver, mDBURL, mDBUsername, mDBPassword);
        } catch (Exception e) {
            mConnection = null;
            e.printStackTrace();
            PunchLogger.logException("ERROR: Cannot connect to database");
        }

        ResultSet rs;

        while (true) {
            short requestType = -1;

            try {
                requestType = in.readShort();
            } catch (Exception e) {
                //PunchLogger.logException("ADMIN: End of request queue.");
                e.printStackTrace();
                requestType = -1;
            }


            //PunchLogger.logException("ADMIN: Handle request: " + requestType + ".");

            if (requestType != -1) {
                try {
                    handleNextRequest(requestType, mConnection, in, dos);
                } catch (Exception e) {
                    e.printStackTrace();
                    PunchLogger.logException("ERROR: Handle request exception: " + e.toString());
                }
            } else {
                break;
            }
        }

        try {
            mConnection.close();
            mConnection = null;
        } catch (Exception e) {
        }
        ;

        // write to real output
        response.addHeader("connection", "keep-alive");
        response.setContentType("application/octet-stream");
        response.setContentLength(dos.size());
        response.flushBuffer();
    //PunchLogger.logException("ADMIN: Return " + dos.size() + "bytes completed!");
    }

    public boolean handleNextRequest(short requestType, Connection aConnection, ChessDataInputStream in, ChessDataOutputStream out) throws Exception {
        boolean isSuccess = true;
        PreparedStatement ps;
        ResultSet rs;

        String16 username;
        String16 password;
        String opponent_name;
        long now = System.currentTimeMillis();
        ChessServerResponse response = new ChessServerResponse();

        switch (requestType) {
            case Protocol.REQUEST_REGISTER:
                ps = aConnection.prepareStatement("INSERT INTO " +
                        "user_info" +
                        "(" +
                        "username," +
                        "password" +
                        ")" +
                        " VALUES" +
                        "(" +
                        "?," +
                        "?" +
                        ");");
                username = in.readString16();
                password = in.readString16();
                ps.setString(1, username.toJavaString());
                ps.setString(2, password.toJavaString());
                ps.executeUpdate();
                ps.close();
                //out.writeShort(Protocol.RESPONSE_REGISTER_SUCCESSFULLY);                  
                response.packResponse(Protocol.RESPONSE_REGISTER_SUCCESSFULLY, out);
                break;

            case Protocol.REQUEST_LOGIN:
                username = in.readString16();
                password = in.readString16();

                ps = aConnection.prepareStatement("SELECT * FROM user_info WHERE username=? AND password=?;");
                ps.setString(1, username.toJavaString());
                ps.setString(2, password.toJavaString());
                rs = ps.executeQuery();
                boolean is_exist = false;
                if (rs != null && rs.next()) {
                    is_exist = true;
                }
                rs.close();
                ps.close();

                ps = aConnection.prepareStatement("UPDATE user_info SET " +
                        "status = ?, " +
                        "opponent_name = ?, " +
                        "is_your_turn = ?, " +
                        "last_update_status = ? " +
                        "WHERE username = ?;");
                ps.setInt(1, STATUS_ONLINE); // set as online

                ps.setString(2, "");
                ps.setInt(3, MYTURN_OFF);
                ps.setLong(4, now);
                ps.setString(5, username.toJavaString());
                ps.executeUpdate();
                ps.close();

                // response client login successfully
                //out.writeShort(Protocol.RESPONSE_LOGIN_SUCCESSFULLY);
                response.packResponse(Protocol.RESPONSE_LOGIN_SUCCESSFULLY, out);
                break;

            case Protocol.REQUEST_LOGOUT:
                username = in.readString16();
                password = in.readString16();

                ps = aConnection.prepareStatement("SELECT * FROM user_info WHERE username=? AND password=?;");
                ps.setString(1, username.toJavaString());
                ps.setString(2, password.toJavaString());
                rs = ps.executeQuery();
                boolean exist = false;
                if (rs != null && rs.first()) {
                    exist = true;
                }
                rs.close();
                ps.close();

                if (exist) {
                    ps = aConnection.prepareStatement("UPDATE user_info SET " +
                            "status = ?, opponent_name=? WHERE username = ?;");
                    ps.setInt(1, STATUS_OFFLINE); // set as offline

                    ps.setString(2, "");
                    ps.setString(3, username.toJavaString());
                    ps.executeUpdate();
                    ps.close();

                    // response client logout successfully
                    //out.writeShort(Protocol.RESPONSE_LOGOUT_SUCCESSFULLY);
                    response.packResponse(Protocol.RESPONSE_LOGOUT_SUCCESSFULLY, out);
                } else {
//                    out.writeShort(Protocol.RESPONSE_LOGOUT_FAILURE);
//                    tempOut.writeString16(new String16("Lỗi: Không tìm thấy tên đăng nhập."));      
                    response.add(new String16("Lỗi: Không tìm thấy tên đăng nhập."));
                    response.packResponse(Protocol.RESPONSE_LOGOUT_FAILURE, out);
                }
                break;

            case Protocol.REQUEST_UPDATE_MY_GAME:
                username = in.readString16();

                int need_move = MYTURN_OFF;
                opponent_name = "";
                int status = 0;
                long last_update_game = 0;
                boolean no_move = false;

                boolean need_update_time = true;

                ps = aConnection.prepareStatement("SELECT * FROM user_info WHERE username=?");
                ps.setString(1, username.toJavaString());
                rs = ps.executeQuery();
                if (rs != null && rs.next()) {
                    status = Integer.parseInt(rs.getString("status"));
                    need_move = Integer.parseInt(rs.getString("is_your_turn"));
                    opponent_name = rs.getString("opponent_name");
                    last_update_game = Long.parseLong(rs.getString("last_update_move"));
                }
                rs.close();
                ps.close();

                System.out.println("REQUEST_UPDATE_MY_GAME: " + username.toJavaString() + " " + status + " " + need_move + " " + last_update_game);

                if (status == STATUS_PLAYING) {
                    ps = aConnection.prepareStatement("SELECT * FROM message " +
                            " WHERE receiplient_name = ? AND " +
                            " sender_username = ? AND " +
                            " send_time > ?;");
                    ps.setString(1, username.toJavaString());
                    ps.setString(2, opponent_name);
                    ps.setLong(3, last_update_game);
                    rs = ps.executeQuery();
                    Vector messageVector = new Vector();
                    while (rs != null && rs.next()) {
                        MessageRecord aMessage = new MessageRecord();
                        aMessage.mMessageID = Integer.parseInt(rs.getString("message_id"));
                        aMessage.mSenderName = rs.getString("sender_username");
                        aMessage.mText = rs.getString("text");
                        messageVector.addElement(aMessage);
                    }
                    rs.close();
                    ps.close();

                    response.add(messageVector.size());
                    for (int i = 0; i < messageVector.size(); i++) {
                        MessageRecord aMessage = (MessageRecord) messageVector.elementAt(i);
                        // clear this message
                        ps = aConnection.prepareStatement("DELETE FROM message WHERE message_id = ?;");
                        ps.setInt(1, aMessage.mMessageID);
                        ps.executeUpdate();
                        ps.close();
                        response.add(new String16(aMessage.mSenderName));
                        response.add(new String16(aMessage.mText));
                    }
                    response.packResponse(Protocol.RESPONSE_NEW_MESSAGES, out);
                }

                if (status == STATUS_PLAYING) {
                    if (need_move == MYTURN_OFF) {
                        System.out.println("REQUEST_UPDATE_MY_GAME: wait for other player!!!");
                    } else if (need_move == MYTURN_ON) {
                        //out.writeShort(Protocol.RESPONSE_PLEASE_DO_A_MOVE);                        
                        response.packResponse(Protocol.RESPONSE_PLEASE_DO_A_MOVE, out);

                        ps = aConnection.prepareStatement("UPDATE user_info SET " +
                                "is_your_turn = ? WHERE username = ?");
                        ps.setInt(1, MYTURN_WAIT);
                        ps.setString(2, username.toJavaString());
                        ps.executeUpdate();
                        ps.close();
                        System.out.println("REQUEST_UPDATE_MY_GAME: my turn!!!");

                    } else if (need_move == MYTURN_WAIT) {
                        System.out.println("REQUEST_UPDATE_MY_GAME: not my turn");
                        int xsrc = 0, ysrc = 0, xdst = 0, ydst = 0;
                        String move_player_name = "";
                        long move_time = 0;
                        boolean move_already = false;
                        boolean room_found = false;

                        ps = aConnection.prepareStatement("SELECT * FROM room WHERE player1_name = ? AND player2_name = ?;");
                        ps.setString(1, username.toJavaString());
                        ps.setString(2, opponent_name);
                        rs = ps.executeQuery();
                        if (rs != null && rs.next()) {
                            room_found = true;
                            move_player_name = rs.getString("move_playername");
                            xsrc = Integer.parseInt(rs.getString("move_src_x"));
                            ysrc = Integer.parseInt(rs.getString("move_src_y"));
                            xdst = Integer.parseInt(rs.getString("move_dst_x"));
                            ydst = Integer.parseInt(rs.getString("move_dst_y"));
                            move_time = Long.parseLong(rs.getString("move_time"));
                            no_move = Integer.parseInt(rs.getString("no_move")) == 1;
                        }
                        rs.close();
                        ps.close();

                        if (!room_found) {
                            ps = aConnection.prepareStatement("SELECT * FROM room WHERE player1_name = ? AND player2_name = ?;");
                            ps.setString(1, opponent_name);
                            ps.setString(2, username.toJavaString());
                            rs = ps.executeQuery();
                            if (rs != null && rs.next()) {
                                room_found = true;
                                move_player_name = rs.getString("move_playername");
                                xsrc = Integer.parseInt(rs.getString("move_src_x"));
                                ysrc = Integer.parseInt(rs.getString("move_src_y"));
                                xdst = Integer.parseInt(rs.getString("move_dst_x"));
                                ydst = Integer.parseInt(rs.getString("move_dst_y"));
                                move_time = Long.parseLong(rs.getString("move_time"));
                                no_move = Integer.parseInt(rs.getString("no_move")) == 1;
                            }
                            rs.close();
                            ps.close();
                        }


                        System.out.println("REQUEST_UPDATE_MY_GAME: " + move_player_name + " " + xsrc + " " + ysrc + " " + xdst + " " + ydst + " " + move_time + " " + no_move);

                        if (!room_found) {
                            //TODO: end game
                            System.out.println("REQUEST_UPDATE_MY_GAME: room not found!!!");
                        } else {
                            if (move_player_name != null && move_player_name.equals(opponent_name)) {
                                move_already = true;
                            } else {
                                move_already = false;
                            }
                            if (move_already) {
                                if (!no_move) {
                                    System.out.println("REQUEST_UPDATE_MY_GAME: move already!!!" + move_player_name);
                                    response.add(xsrc);
                                    response.add(ysrc);
                                    response.add(xdst);
                                    response.add(ydst);
                                    response.packResponse(Protocol.RESPONSE_NEW_MOVES, out);
                                } else {
                                    ps = aConnection.prepareStatement("UPDATE user_info SET " +
                                            " opponent_name = ?, " +
                                            " status = ?," +
                                            " last_update_move = ? " +
                                            " WHERE username = ?");
                                    ps.setString(1, "");
                                    ps.setInt(2, STATUS_ONLINE);
                                    ps.setLong(3, now);
                                    ps.setString(4, username.toJavaString());
                                    ps.executeUpdate();
                                    ps.close();
                                    System.out.println("REQUEST_UPDATE_MY_GAME: no move, u win!!!" + move_player_name);
                                    response.packResponse(Protocol.RESPONSE_YOU_WIN_THE_GAME, out);

                                    need_update_time = false;
                                }
                            }
                        }
                    }
                }


                if (need_update_time) {
                    ps = aConnection.prepareStatement("UPDATE user_info SET " +
                            "last_update_move = ? WHERE username = ?");
                    ps.setLong(1, now);
                    ps.setString(2, username.toJavaString());
                    ps.executeUpdate();
                    ps.close();
                }

                System.out.println("REQUEST_UPDATE_MY_GAME: done!!!");

                response.packResponse(Protocol.RESPONSE_UPDATE_MY_GAME_SUCCESSFULLY, out);
                break;

            case Protocol.REQUEST_STILL_ONLINE:
                username = in.readString16();

                ps = aConnection.prepareStatement("UPDATE user_info SET " +
                        "last_update_status = ? WHERE username = ?;");
                ps.setLong(1, now); // set as offline

                ps.setString(2, username.toJavaString());
                ps.executeUpdate();
                ps.close();

                //out.writeShort(Protocol.RESPONSE_STILL_ONLINE_SUCCESSFULLY);
                response.packResponse(Protocol.RESPONSE_STILL_ONLINE_SUCCESSFULLY, out);

                ps = aConnection.prepareStatement("SELECT * FROM user_info WHERE username=?;");
                ps.setString(1, username.toJavaString());
                rs = ps.executeQuery();
                status = 0;
                opponent_name = "";
                long challenge_request_time = 0;
                if (rs != null && rs.first()) {
                    status = Integer.parseInt(rs.getString("status"));
                    opponent_name = rs.getString("opponent_name");
                    challenge_request_time = Long.parseLong(rs.getString("challenge_request_time"));
                }
                rs.close();
                ps.close();

                if (status == STATUS_ONLINE) {
                    if (opponent_name.length() > 0) // there is an challenge
                    {
                        boolean is_challenge_timeout = false;
                        if (now - challenge_request_time > CHALLENGE_TIMEOUT) {
                            is_challenge_timeout = true;
                        } else {
                            PunchLogger.logException("RESPONSE_NEW_CHALLENGE: " + opponent_name);
//                            out.writeShort(Protocol.RESPONSE_NEW_CHALLENGE);
//                            tempOut.writeString16(new String16(opponent_name));

                            response.add(new String16(opponent_name));
                            response.packResponse(Protocol.RESPONSE_NEW_CHALLENGE, out);
                        }
                        // change status to wait response challenge
                        if (!is_challenge_timeout) {
                            ps = aConnection.prepareStatement("UPDATE user_info SET " +
                                    "status = ? WHERE username = ?");
                            ps.setInt(1, STATUS_WAITING); // as waiting response for challenge

                            ps.setString(2, username.toJavaString());
                            ps.executeUpdate();
                            ps.close();
                        } else // if timeout then clear the challenge
                        {
                            ps = aConnection.prepareStatement("UPDATE user_info SET " +
                                    "status = ?, opponent_name = ? WHERE username = ?");
                            ps.setInt(1, STATUS_ONLINE); // as online and no event

                            ps.setString(2, ""); // clear opponent_name

                            ps.setString(3, username.toJavaString());
                            ps.executeUpdate();
                            ps.close();
                        }
                    }
                } else if (status == STATUS_WAITING) {
                    if (opponent_name.length() > 0) {
                        boolean is_challenge_timeout = false;

                        if (now - challenge_request_time > CHALLENGE_TIMEOUT) {
                            is_challenge_timeout = true;
                            out.writeShort(Protocol.RESPONSE_CHALLENGE_TIMEOUT);
                        } else if (opponent_name.length() == 0) {
                            is_challenge_timeout = true;
                            //out.writeShort(Protocol.RESPONSE_REJECT_CHALLENGE_SUCCESSFULLY);
                            response.packResponse(Protocol.RESPONSE_REJECT_CHALLENGE_SUCCESSFULLY, out);
                        }

                        if (is_challenge_timeout) {
                            ps = aConnection.prepareStatement("UPDATE user_info SET " +
                                    "status = ?, opponent_name = ? WHERE username = ?");
                            ps.setInt(1, STATUS_ONLINE); // as online and no event

                            ps.setString(2, ""); // clear opponent_name

                            ps.setString(3, username.toJavaString());
                            ps.executeUpdate();
                            ps.close();
                        } else {
                            int opponent_status = 0;
                            String opponent_opponent_name = "";
                            ps = aConnection.prepareStatement("SELECT * FROM user_info WHERE username=?");
                            ps.setString(1, opponent_name);
                            rs = ps.executeQuery();
                            if (rs != null && rs.next()) {
                                opponent_status = Integer.parseInt(rs.getString("status"));
                                opponent_opponent_name = rs.getString("opponent_name");
                            }
                            rs.close();
                            ps.close();

                            if (opponent_status == STATUS_PLAYING && opponent_opponent_name.equals(username.toJavaString())) {
                                //out.writeShort(Protocol.RESPONSE_ACCEPT_CHALLENGE);
                                ps = aConnection.prepareStatement("UPDATE user_info SET " +
                                        "status = ? WHERE username = ?");
                                ps.setInt(1, STATUS_PLAYING); // as online and no event

                                ps.setString(2, username.toJavaString());
                                ps.executeUpdate();
                                ps.close();
                                response.packResponse(Protocol.RESPONSE_ACCEPT_CHALLENGE, out);
                            }
                        }
                    } else {
                        //out.writeShort(Protocol.RESPONSE_REJECT_CHALLENGE);
                        response.packResponse(Protocol.RESPONSE_REJECT_CHALLENGE, out);

                        ps = aConnection.prepareStatement("UPDATE user_info SET " +
                                "status = ?, opponent_name = ? WHERE username = ?");
                        ps.setInt(1, STATUS_ONLINE); // as online and no event

                        ps.setString(2, ""); // clear opponent_name

                        ps.setString(3, username.toJavaString());
                        ps.executeUpdate();
                        ps.close();
                    }
                } else if (status == STATUS_PLAYING) {
//                    int need_move = MYTURN_OFF;
//                    opponent_name = "";
//
//                    ps = aConnection.prepareStatement("SELECT * FROM user_info WHERE username=?");
//                    ps.setString(1, username.toJavaString());
//                    rs = ps.executeQuery();
//                    if (rs != null && rs.next())
//                    {
//                        status = Integer.parseInt(rs.getString("status"));
//                        need_move = Integer.parseInt(rs.getString("is_your_turn"));
//                        opponent_name = rs.getString("opponent_name");
//                    }
//                    rs.close();
//                    ps.close();
//
//                    if (status == STATUS_PLAYING)
//                    {                
//                        if (need_move == MYTURN_ON)
//                        {
//                            out.writeShort(Protocol.RESPONSE_PLEASE_DO_A_MOVE);
//                            ps = aConnection.prepareStatement("UPDATE user_info SET " +
//                                    "is_your_turn = ? WHERE username = ?");
//                            ps.setInt(1, MYTURN_WAIT);
//                            ps.setString(2, username.toJavaString());
//                            ps.executeUpdate();
//                            ps.close();
//                        }
//                        else if (need_move == MYTURN_OFF)
//                        {
//                            ps = aConnection.prepareStatement("SELECT * FROM user_info WHERE username = ?");
//                            ps.setString(1, opponent_name);
//                            rs = ps.executeQuery();                            
//                            boolean move_already = false;
//                            int xsrc = 0, ysrc = 0, xdst = 0, ydst = 0;
//                            if (rs != null && rs.next())
//                            {
//                                move_already = (rs.getString("is_your_turn").equals("" + MYTURN_OFF));
//                                xsrc = Integer.parseInt(rs.getString("move_src_x"));
//                                ysrc = Integer.parseInt(rs.getString("move_src_y"));
//                                xdst = Integer.parseInt(rs.getString("move_dst_x"));
//                                ydst = Integer.parseInt(rs.getString("move_dst_y"));
//                            }
//                            rs.close();
//                            ps.close();
//
//                            if (move_already)
//                            {
//                                ps = aConnection.prepareStatement("UPDATE user_info SET " +
//                                    "is_your_turn = ? WHERE username = ?");
//                                ps.setInt(1, MYTURN_ON);
//                                ps.setString(2, username.toJavaString());
//                                ps.executeUpdate();
//                                ps.close();
//
//                                out.writeShort(Protocol.RESPONSE_NEW_MOVES);
//                                out.writeInt(xsrc);
//                                out.writeInt(ysrc);
//                                out.writeInt(xdst);
//                                out.writeInt(ydst);
//                            }                               
//                        }
//                    }
                }
                break;

            case Protocol.REQUEST_I_DID_A_MOVE:
                username = in.readString16();
                opponent_name = in.readString16().toJavaString();
                int xsrc = in.readInt();
                int ysrc = in.readInt();
                int xdst = in.readInt();
                int ydst = in.readInt();

                System.out.println("REQUEST_I_DID_A_MOVE: " + username.toJavaString() + " " + xsrc + " " + ysrc + " " + xdst + " " + ydst);

                ps = aConnection.prepareStatement("UPDATE room SET " +
                        " move_playername = ?," +
                        " move_src_x = ?," +
                        " move_src_y = ?," +
                        " move_dst_x = ?," +
                        " move_dst_y = ?," +
                        " move_time = ?" +
                        " WHERE player1_name = ? AND player2_name = ?;");
                ps.setString(1, username.toJavaString());
                ps.setInt(2, xsrc);
                ps.setInt(3, ysrc);
                ps.setInt(4, xdst);
                ps.setInt(5, ydst);
                ps.setLong(6, now);
                ps.setString(7, username.toJavaString());
                ps.setString(8, opponent_name);
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("UPDATE room SET " +
                        " move_playername = ?," +
                        " move_src_x = ?," +
                        " move_src_y = ?," +
                        " move_dst_x = ?," +
                        " move_dst_y = ?," +
                        " move_time = ?" +
                        " WHERE player1_name = ? AND player2_name = ?;");
                ps.setString(1, username.toJavaString());
                ps.setInt(2, xsrc);
                ps.setInt(3, ysrc);
                ps.setInt(4, xdst);
                ps.setInt(5, ydst);
                ps.setLong(6, now);
                ps.setString(7, opponent_name);
                ps.setString(8, username.toJavaString());
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("UPDATE user_info SET " +
                        " is_your_turn = ?" +
                        " WHERE username = ?");
                ps.setInt(1, MYTURN_OFF);
                ps.setString(2, username.toJavaString());
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("UPDATE user_info SET " +
                        " is_your_turn = ? " +
                        " WHERE username = ?");
                ps.setInt(1, MYTURN_ON);
                ps.setString(2, opponent_name);
                ps.executeUpdate();
                ps.close();

                //out.writeShort(Protocol.RESPONSE_I_DID_A_MOVE_SUCCESSFULLY);
                response.packResponse(Protocol.RESPONSE_I_DID_A_MOVE_SUCCESSFULLY, out);
                break;

            case Protocol.REQUEST_SEND_CHALLENGE:
                username = in.readString16();
                opponent_name = in.readString16().toJavaString();

                // check status of opponent
                ps = aConnection.prepareStatement("SELECT * FROM user_info WHERE username=?;");
                ps.setString(1, opponent_name);
                rs = ps.executeQuery();
                status = 0;
                String opponent_name2 = "";
                challenge_request_time = 0;
                if (rs != null && rs.next()) {
                    status = Integer.parseInt(rs.getString("status"));
                    opponent_name2 = rs.getString("opponent_name");
                }
                rs.close();
                ps.close();

                if (status != STATUS_ONLINE || opponent_name2.length() > 0) // busy
                {
                    //out.writeShort(Protocol.RESPONSE_SEND_CHALLENGE_FAILURE);
                    //tempOut.writeString16(new String16("Cờ thủ này không sẵn sàng để thách đấu. Bạn vui lòng thử với người khác."));
                    response.add(new String16("Cờ thủ này không sẵn sàng để thách đấu. Bạn vui lòng thử với người khác."));
                    response.packResponse(Protocol.RESPONSE_SEND_CHALLENGE_FAILURE, out);
                } else // available
                {
                    ps = aConnection.prepareStatement("UPDATE user_info SET " +
                            "opponent_name = ?, challenge_request_time = ?, status = ? WHERE username = ?;");
                    ps.setString(1, opponent_name);
                    ps.setLong(2, now);
                    ps.setInt(3, STATUS_WAITING);
                    ps.setString(4, username.toJavaString());
                    ps.executeUpdate();
                    ps.close();

                    ps = aConnection.prepareStatement("UPDATE user_info SET " +
                            "opponent_name = ?, challenge_request_time = ? WHERE username = ?;");
                    ps.setString(1, username.toJavaString());
                    ps.setLong(2, now);
                    ps.setString(3, opponent_name);
                    ps.executeUpdate();
                    ps.close();

                    //out.writeShort(Protocol.RESPONSE_SEND_CHALLENGE_SUCCESSFULLY);  
                    response.packResponse(Protocol.RESPONSE_SEND_CHALLENGE_SUCCESSFULLY, out);
                }
                break;

            case Protocol.REQUEST_ACCEPT_CHALLENGE:
                username = in.readString16();
                opponent_name = in.readString16().toJavaString();

                ps = aConnection.prepareStatement("UPDATE user_info SET " +
                        "status = ?, is_your_turn = ? WHERE username = ?;");
                ps.setInt(1, STATUS_PLAYING);
                ps.setInt(2, MYTURN_OFF);
                ps.setString(3, username.toJavaString());
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("UPDATE user_info SET " +
                        "is_your_turn = ? WHERE username = ?;");
                ps.setInt(1, MYTURN_ON);
                ps.setString(2, opponent_name);
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("" +
                        "DELETE FROM room WHERE player1_name = ? OR player2_name = ?;");
                ps.setString(1, opponent_name);
                ps.setString(2, opponent_name);
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("" +
                        "DELETE FROM room WHERE player1_name = ? OR player2_name = ?;");
                ps.setString(1, username.toJavaString());
                ps.setString(2, username.toJavaString());
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("" +
                        "INSERT INTO room(player1_name, player2_name) VALUES(?, ?);");
                ps.setString(1, opponent_name);
                ps.setString(2, username.toJavaString());
                ps.executeUpdate();
                ps.close();

                //out.writeShort(Protocol.RESPONSE_ACCEPT_CHALLENGE_SUCCESSFULLY);
                response.packResponse(Protocol.RESPONSE_ACCEPT_CHALLENGE_SUCCESSFULLY, out);
                break;

            case Protocol.REQUEST_REJECT_CHALLENGE:
                System.out.println("REQUEST_REJECT_CHALLENGE");
                username = in.readString16();
                opponent_name = in.readString16().toJavaString();

                System.out.println("Username: " + username.toJavaString());
                System.out.println("Opponent Name: " + opponent_name);

                ps = aConnection.prepareStatement("UPDATE user_info SET " +
                        "status = ?, opponent_name = ? WHERE username = ?;");
                ps.setInt(1, STATUS_ONLINE);
                ps.setString(2, "");
                ps.setString(3, username.toJavaString());
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("UPDATE user_info SET " +
                        "opponent_name = ? WHERE username = ?;");
                ps.setString(1, "");
                ps.setString(2, opponent_name);
                ps.executeUpdate();
                ps.close();

                System.out.println("DONE!");

                //out.writeShort(Protocol.RESPONSE_REJECT_CHALLENGE_SUCCESSFULLY);
                response.packResponse(Protocol.RESPONSE_REJECT_CHALLENGE_SUCCESSFULLY, out);
                break;

            case Protocol.REQUEST_TOP_PLAYERS:
                Vector aVector = new Vector();
                ps = aConnection.prepareStatement("SELECT * FROM user_info;");
                rs = ps.executeQuery();
                while (rs != null && rs.next()) {
                    PlayerRecord aPlayer = new PlayerRecord();
                    aPlayer.mID = Integer.parseInt(rs.getString("id"));
                    aPlayer.mUsername = rs.getString("username");
                    aPlayer.mStatus = Integer.parseInt(rs.getString("status"));
                    aVector.addElement(aPlayer);
                }
                rs.close();
                ps.close();

                response.add(aVector.size());
                for (int i = 0; i < aVector.size(); i++) {
                    PlayerRecord aPlayer = (PlayerRecord) aVector.elementAt(i);
                    response.add(aPlayer.mID);
                    response.add(new String16(aPlayer.mUsername));
                    response.add(aPlayer.mStatus);
                }
                response.packResponse(Protocol.RESPONSE_TOP_PLAYERS_LIST, out);
                break;


            case Protocol.REQUEST_NEED_FRIENDS_LIST:
                username = in.readString16();
                aVector = new Vector();
                ps = aConnection.prepareStatement("SELECT * FROM user_info u, relationship r WHERE " +
                        "u.username = r.username2 AND r.username1 = ?;");
                ps.setString(1, username.toJavaString());
                rs = ps.executeQuery();
                while (rs != null && rs.next()) {
                    PlayerRecord aPlayer = new PlayerRecord();
                    aPlayer.mID = Integer.parseInt(rs.getString("id"));
                    aPlayer.mUsername = rs.getString("username");
                    aPlayer.mStatus = Integer.parseInt(rs.getString("status"));
                    aVector.addElement(aPlayer);
                }
                rs.close();
                ps.close();

                response.add(aVector.size());
                for (int i = 0; i < aVector.size(); i++) {
                    PlayerRecord aPlayer = (PlayerRecord) aVector.elementAt(i);
                    response.add(aPlayer.mID);
                    response.add(new String16(aPlayer.mUsername));
                    response.add(aPlayer.mStatus);
                }
                response.packResponse(Protocol.RESPONSE_NEW_FRIENDS_LIST, out);
                break;

            case Protocol.REQUEST_I_HAVE_NO_MOVE:
                username = in.readString16();
                opponent_name = in.readString16().toJavaString();

                ps = aConnection.prepareStatement("UPDATE room SET " +
                        " move_playername = ?," +
                        " move_time = ?," +
                        " no_move = ?" +
                        " WHERE player1_name = ? AND player2_name = ?;");
                ps.setString(1, username.toJavaString());
                ps.setLong(2, now);
                ps.setBoolean(3, true);
                ps.setString(4, username.toJavaString());
                ps.setString(5, opponent_name);
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("UPDATE room SET " +
                        " move_playername = ?," +
                        " move_time = ?," +
                        " no_move = ?" +
                        " WHERE player1_name = ? AND player2_name = ?;");
                ps.setString(1, username.toJavaString());
                ps.setLong(2, now);
                ps.setBoolean(3, true);
                ps.setString(4, opponent_name);
                ps.setString(5, username.toJavaString());
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("UPDATE user_info SET " +
                        " opponent_name = ?," +
                        " status = ?," +
                        " is_your_turn = ?" +
                        " WHERE username = ?");
                ps.setString(1, "");
                ps.setInt(2, STATUS_ONLINE);
                ps.setInt(3, MYTURN_OFF);
                ps.setString(4, username.toJavaString());
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("UPDATE user_info SET " +
                        " is_your_turn = ? " +
                        " WHERE username = ?");
                ps.setInt(1, MYTURN_ON);
                ps.setString(2, opponent_name);
                ps.executeUpdate();
                ps.close();

                response.packResponse(Protocol.RESPONSE_I_HAVE_NO_MOVE_SUCCESSFULLY, out);
                break;

            case Protocol.REQUEST_SEND_MESSAGE:
                PunchLogger.logException("send message");
                username = in.readString16();
                opponent_name = in.readString16().toJavaString();
                String text = in.readString16().toJavaString();
                ps = aConnection.prepareStatement("INSERT INTO message(receiplient_name, sender_username, text, send_time)" +
                        " VALUES(?, ?, ?, ?);");
                ps.setString(1, opponent_name);
                ps.setString(2, username.toJavaString());
                ps.setString(3, text);
                ps.setLong(4, now);
                ps.executeUpdate();
                ps.close();
                PunchLogger.logException("send message done!");
                response.packResponse(Protocol.RESPONSE_SEND_MESSAGE_SUCCESSFULLY, out);
                break;

            case Protocol.REQUEST_MAKE_FRIEND:
                try {
                    username = in.readString16();
                    String buddyName = in.readString16().toJavaString();

                    ps = aConnection.prepareStatement("INSERT INTO relationship(username1, username2)" +
                            " VALUES(?, ?);");
                    ps.setString(1, username.toJavaString());
                    ps.setString(2, buddyName);
                    ps.executeUpdate();
                    ps.close();

                    response.packResponse(Protocol.RESPONSE_MAKE_FRIEND_SUCCESSFULLY, out);
                } catch (Exception e)
                {
                    response.packResponse(Protocol.RESPONSE_MAKE_FRIEND_FAILURE, out);
                }
                break;

            case Protocol.REQUEST_ACCEPT_MAKE_FRIEND:
                username = in.readString16();
                String buddyName = in.readString16().toJavaString();

                ps = aConnection.prepareStatement("INSERT INTO relationship(username1, username2) " +
                        "VALUES(?, ?);");
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("UPDATE relationship SET is_on_request = ? WHERE " +
                        "username1 = ? AND username2 = ?;");
                ps.setInt(1, 0);
                ps.setString(2, username.toJavaString());
                ps.setString(3, buddyName);
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("UPDATE relationship SET is_on_request = ? WHERE " +
                        "username1 = ? AND username2 = ?;");
                ps.setInt(1, 0);
                ps.setString(2, buddyName);
                ps.setString(3, username.toJavaString());
                ps.executeUpdate();
                ps.close();

                response.packResponse(Protocol.RESPONSE_ACCEPT_MAKE_FRIEND_SUCCESSFULLY, out);
                break;

            case Protocol.REQUEST_REJECT_MAKE_FRIEND:
                username = in.readString16();
                buddyName = in.readString16().toJavaString();

                ps = aConnection.prepareStatement("DELETE FROM relationship WHERE username1 = ? AND username2 = ?;");
                ps.setString(1, username.toJavaString());
                ps.setString(2, buddyName);
                ps.executeUpdate();
                ps.close();

                ps = aConnection.prepareStatement("DELETE FROM relationship WHERE username1 = ? AND username2 = ?;");
                ps.setString(1, buddyName);
                ps.setString(2, username.toJavaString());
                ps.executeUpdate();
                ps.close();

                response.packResponse(Protocol.RESPONSE_REJECT_MAKE_FRIEND_SUCCESSFULLY, out);
                break;
        }
        return isSuccess;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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

    public void run() {
        try {
            long last_cycle = 0;
            //PunchLogger.logException("start auto-logout function thread..." + mIsStarted);
            while (mIsStarted) {
                long now = System.currentTimeMillis();
                //PunchLogger.logException("start find out");                
                long timeout = now - this.IDLE_TIME_OUT;
                Connection aConnection = getDatabaseConnection(mDBDriver, mDBURL, mDBUsername, mDBPassword);
                Vector needToLogoutUsernames = new Vector();
                PreparedStatement ps = aConnection.prepareStatement("SELECT * FROM user_info WHERE last_update_status < ? AND status <> ?;");
                ps.setLong(1, timeout);
                ps.setInt(2, STATUS_OFFLINE);
                ResultSet rs = ps.executeQuery();

                //PunchLogger.logException("find user idle ");

                while (rs != null && rs.next()) {
                    PlayerRecord player = new PlayerRecord();
                    player.mID = Integer.parseInt(rs.getString("id"));
                    player.mUsername = rs.getString("username");
                    player.mStatus = Integer.parseInt(rs.getString("status"));

                    if (player.mStatus != STATUS_OFFLINE) {
                        needToLogoutUsernames.addElement(player);
                    }
                //PunchLogger.logException("will force offline " + player.mUsername);
                }

                rs.close();
                ps.close();

                for (int i = 0; i < needToLogoutUsernames.size(); i++) {
                    PlayerRecord player = (PlayerRecord) needToLogoutUsernames.elementAt(i);

                    ps = aConnection.prepareStatement("UPDATE user_info SET " +
                            "status = ?, opponent_name=? WHERE username = ?;");
                    ps.setInt(1, STATUS_OFFLINE); // set as offline

                    ps.setString(2, "");
                    ps.setString(3, player.mUsername);
                    ps.executeUpdate();
                    ps.close();
                //PunchLogger.logException("force offline " + player.mUsername);
                }

                aConnection.close();

                Thread.sleep(60000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 *
 * @author dong
 */
class PlayerRecord {

    public int mID;
    public String mUsername;
    public int mStatus;
}

class MessageRecord {

    public int mMessageID;
    public String mSenderName;
    public String mText;
}
