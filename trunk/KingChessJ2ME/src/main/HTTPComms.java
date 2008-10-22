package main;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.StringItem;
import main.ChessMIDlet;
 
public class HTTPComms implements Runnable {    
    
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
    
    private StringBuffer mURL;
    private String mRequest;
    private InputStream mInputStream = null;
    private HttpConnection mHTTPConnection = null;    
    private Thread mHttpThread = null;
    
    public static HTTPComms mMe;    
    
    public final static String SERVER_URL = "http://dongnh.blogdns.net:8282/services/ChessServlet?";    

    public HTTPComms() 
    {        
        //mWaitCanvas = new WaitCanvas();        
        //ChessMIDlet.mMe.getDisplay().setCurrent(mWaitCanvas);
        mMe = this;
    }
    
    public void SendRequest(String request) {  
        mRequest = request;
        mHttpThread = new Thread(this);
        mHttpThread.start();
    }
    
    private void buildURLRequest() {
        mURL = new StringBuffer();
        mURL.append(SERVER_URL);            
        mURL.append(mRequest);
    }
    
    private void parseResponse(byte[] response) {
        //WaitCanvas.mMe.mMessage = "Parsing Response...";
        DataInputStream data_in = null;
        Vector list = new Vector();
        String inputStr = new String();                         
        
        /*System.out.println("Begin:");
        for (int i = 0; i < response.length; i++)
        {
            System.out.print((char)response[i]);
        }
        System.out.println("\nEnd.");
         */
        
        try {
            ByteArrayInputStream byte_in = new ByteArrayInputStream(response);
            data_in = new DataInputStream(byte_in);
            int tag = data_in.readShort();
            switch (tag)
            {
                case RESPONSE_REGISTER_SUCCESSFULLY:
                    ChessMIDlet.mMe.setScreen(new NotifyScreen("Notify", data_in.readUTF(), new MainMenu()));
                    break;
                
                case RESPONSE_REGISTER_FAILURE:
                    ChessMIDlet.mMe.setScreen(new ErrorScreen("Error", data_in.readUTF(), new RegisterScreen()));
                    break;
                
                case RESPONSE_LOGIN_SUCCESSFULLY:
                    ChessMIDlet.mMe.mIsLoggedIn = true;                    
                    System.out.println("Login successfully.");
                    ChessMIDlet.mMe.setScreen(new NotifyScreen("Notify", data_in.readUTF(), new MainMenu()));
                    break;
                
                case RESPONSE_LOGIN_FAILURE:
                    ChessMIDlet.mMe.mIsLoggedIn = false;
                    ChessMIDlet.mMe.mUsername = "";
                    ChessMIDlet.mMe.mPassword = "";
                    System.out.println("Login failure.");
                    ChessMIDlet.mMe.setScreen(new ErrorScreen("Error", data_in.readUTF(), new LoginScreen()));                
                    break;      
                
                case RESPONSE_ROOM_CREATE_SUCCESSFULLY:                    
                    ChessMIDlet.mMe.setScreen(new NotifyScreen("Notify", data_in.readUTF(), new InRoomScreen(true)));
                    InRoomScreen.mMe.mIsHosting = true;
                    break;
                
                case RESPONSE_ROOM_CREATE_FAILURE:
                    ChessMIDlet.mMe.setScreen(new ErrorScreen("Error", data_in.readUTF(), new CreateRoomScreen()));
                    break;
                
                case RESPONSE_UPDATE_ROOM_STATUS:
                    String aStr = data_in.readUTF();
                    if (aStr.length() > 0) // if there is a opponent
                    {
                          InRoomScreen.mMe.mIsStarted = true;
                          ChessMIDlet.mMe.setScreen(new GameScreen(ChessMIDlet.mMe.mUsername, aStr));
                          InRoomScreen.mMe.mIsStarted = true;
                    } else // if still there's no opponent
                    {
                        InRoomScreen.mMe.mWaitString = new StringItem("", "Waiting for other player...");
                        InRoomScreen.mMe.mOpponentName = "";
                        InRoomScreen.mMe.deleteAll();
                        InRoomScreen.mMe.append(InRoomScreen.mMe.mWaitString);                        
                    }
                    break;
                
                case RESPONSE_UPDATE_ROOM_STATUS_FAIL:                    
                    break;
                
                case RESPONSE_UPDATE_ROOMS_LIST:
                    short numberOfRooms = data_in.readShort();
                    Vector aVectorOfRoom = new Vector();
                    for (int i = 0; i < numberOfRooms; i++)
                    {
                        Room aRoom = new Room(data_in.readUTF(), data_in.readUTF());
                        aVectorOfRoom.addElement(aRoom);
                    }
                    ChessMIDlet.mMe.setScreen(new RoomViewScreen(aVectorOfRoom));
                    break;
                
                case RESPONSE_ROOM_JOIN_SUCCESSFULLY:                    
                    String opponentName = data_in.readUTF();
                    ChessMIDlet.mMe.setScreen(new GameScreen(opponentName, ChessMIDlet.mMe.mUsername));
                    break;
                    
                case RESPONSE_ROOM_JOIN_FAILURE:
                    ChessMIDlet.mMe.setScreen(new ErrorScreen("Error", data_in.readUTF(), new MainMenu()));
                    break;
                
                case RESPONSE_MOVE_ACCEPTED:                     
                    GameScreen.mMe.mIsWaitingForOpponent = true;
                    GameScreen.mMe.startThread();
                    break;
                
                case RESPONSE_NEW_MOVE:
                    if (!ChessMIDlet.mMe.mUsername.equals(data_in.readUTF()))
                    {                        
                        GameScreen.mMe.mSelectX = GameScreen.mMe.mSavedSelectX;
                        GameScreen.mMe.mSelectY = GameScreen.mMe.mSavedSelectY;
                        GameScreen.mMe.move(data_in.readShort(), data_in.readShort(), data_in.readShort(), data_in.readShort());                    
                        GameScreen.mMe.mIsWaitingForOpponent = false;     
                        GameScreen.mMe.repaint();
                    }
                    break;
                    
                case RESPONSE_WIN_GAME:
                    GameScreen.mMe.mIsWaitingForOpponent = false;
                    ChessMIDlet.mMe.setScreen(new NotifyScreen("Congratulation", data_in.readUTF(), new MainMenu()));
                    break;
                    
                case RESPONSE_LOSE_GAME:
                    GameScreen.mMe.mIsWaitingForOpponent = false;
                    ChessMIDlet.mMe.setScreen(new NotifyScreen("Inform", data_in.readUTF(), new MainMenu()));
                    break;
                    
                case RESPONSE_LOGOUT_SUCCESSFULLY:
                    ChessMIDlet.mMe.mIsLoggedIn = false;
                    ChessMIDlet.mMe.setScreen(new NotifyScreen("Notify", data_in.readUTF(), new MainMenu()));
                    break;
                 
                case RESPONSE_LOGOUT_FAILURE:
                    ChessMIDlet.mMe.mIsLoggedIn = false;
                    ChessMIDlet.mMe.setScreen(new NotifyScreen("Notify", data_in.readUTF(), new MainMenu()));
                    break;
            }
        } catch (Exception ex) {  
            ex.printStackTrace();
        }                
        //WaitCanvas.mMe.mMessage = "Transaction End.";        
    }                          
    
    private byte[] Send() {

        byte[] responseData = new byte [1];
        int rc = 0;
      	String errMsg = "";
        
        try {
            int len = -1;                        
            
            mHTTPConnection = (HttpConnection)Connector.open(mURL.toString());
            System.out.println("URL: " + mURL.toString());  
            
            //WaitCanvas.mMe.mMessage = "Open connection...";
            
            mHTTPConnection.setRequestMethod(HttpConnection.GET);            
            rc = mHTTPConnection.getResponseCode();                        
            
            if (rc != HttpConnection.HTTP_OK) {       
                System.out.println("Send fail: " + rc);
                mHTTPConnection.close();
                mHTTPConnection = null;
                return null;                
            }            
            
           // WaitCanvas.mMe.mMessage = "Sending request...";
            
            mInputStream = mHTTPConnection.openInputStream();
            
            if (mHTTPConnection instanceof HttpConnection) {
                len = (int)((HttpConnection)mHTTPConnection).getLength();
            }                                    
            
            //WaitCanvas.mMe.mMessage = "Receiving Response...";
            
            if (len != -1) {
                int actual = 0;
                int bytesread = 0 ;
                responseData = new byte[len];
                
                while ((bytesread != len) && (actual != -1)) {
                    actual = mInputStream.read(responseData, bytesread, len - bytesread);
                    bytesread += actual;
                }    
                
                System.out.println("bytesread: " + bytesread);
                
//                try {
//                    Thread.sleep(500);
//                } catch (Exception e) {                
//                }
                
            } else {
                responseData = new byte[1];
                responseData[0]=0x00;

                while ((mInputStream.read()) != -1) {
                }
            }            
            
            if (mInputStream != null) 
            {
                mInputStream.close();
            }
            if (mHTTPConnection != null) 
            {
                mHTTPConnection.close();
            }
            
            mInputStream = null;
            mHTTPConnection = null;
        } catch (Exception e) {
            ChessMIDlet.mMe.setScreen(new ErrorScreen("Error", "Could not connect to server. Please try again.", new MainMenu()));
        }                
        
        return(responseData);
    }    
    
//    private byte[] Send() {
//        byte[] responseData = new byte [1];
//        int rc = 0;
//      	String errMsg = "";
//        
//        try {
//            int len = -1;                                    
//            mHTTPConnection = (HttpConnection)Connector.open(mURL);                        
//            
//            mHTTPConnection.setRequestMethod(HttpConnection.POST);            
//            
//            DataOutputStream aOutputStream = mHTTPConnection.openOutputStream();
//            aOutputStream.write(mData);            
//            
//            rc = mHTTPConnection.getResponseCode();                        
//            
//            
//            if (rc != HttpConnection.HTTP_OK) {       
//                mHTTPConnection.close();
//                mHTTPConnection = null;
//                return null;                
//            }                                                           
//            
//            mInputStream = mHTTPConnection.openInputStream();
//            
//            if (mHTTPConnection instanceof HttpConnection) {
//                len = (int)((HttpConnection)mHTTPConnection).getLength();
//            }                                    
//            
//            if (len != -1) {
//                int actual = 0;
//                int bytesread = 0 ;
//                responseData = new byte[len];
//                
//                while ((bytesread != len) && (actual != -1)) {
//                    actual = mInputStream.read(responseData, bytesread, len - bytesread);
//                    bytesread += actual;
//                }                                                                    
//            } else {
//                responseData = new byte[1];
//                responseData[0]=0x00;
//
//                while ((mInputStream.read()) != -1) {
//                }
//            }            
//            
//            if (mInputStream != null) 
//            {
//                mInputStream.close();
//            }
//            if (mHTTPConnection != null) 
//            {
//                mHTTPConnection.close();
//            }
//            
//            mInputStream = null;
//            mHTTPConnection = null;
//        } catch (Exception e) {
//            ChessMIDlet.mMe.setScreen(new ErrorScreen("Error", "Could not connect to server. Please try again.", new MainMenu()));
//        }                
//        
//        return(responseData);
//    }    
    
    public void run() {        
        buildURLRequest();
        byte[] response = Send();
        if (response != null)
            parseResponse(response);        
    }
    
    public void closeConnection()
    {
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }

            if (mHTTPConnection != null) {
                mHTTPConnection.close();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}