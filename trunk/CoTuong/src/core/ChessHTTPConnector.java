/*
 * ChessHTTPConnector.java
 *
 * Created on November 2, 2008, 2:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;

/**
 *
 * @author dong
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import main.Context;
 
public class ChessHTTPConnector implements Runnable {        
    private byte[] mRequestData;  
    private byte[] mReturnData;        
    private Thread mHttpThread = null;        
    private Network mNetwork;
    private HttpConnection mConnection = null;
    private InputStream mInputStream = null;
    private OutputStream mOutputStream = null;
    
    private static ChessHTTPConnector mInstance = null;        

    private ChessHTTPConnector(Network aNetwork) 
    {        
        mNetwork = aNetwork;
    }
    
    public byte[] getReturnData()
    {
        return mReturnData;
    }
    
    public static ChessHTTPConnector createHTTPConnector(Network aNetwork)
    {
        if (mInstance != null)
            return mInstance;
        else
            return new ChessHTTPConnector(aNetwork);
    }
    
    public void SendRequest(byte[] data) {          
        mRequestData = data;
        mHttpThread = new Thread(this);
        mHttpThread.start();
    }                                
    
    private void Send() {
        mConnection = null;
        mInputStream = null;
        mOutputStream = null;
        int rc;

        try {
            mNetwork.onHTTPEvent(Network.EVENT_SETUP_CONNECTION);
            mConnection = (HttpConnection)Connector.open(Context.mURL);

            // Set the request method and headers
            mConnection.setRequestMethod(HttpConnection.POST);                                
            // Getting the output stream may flush the headers
            mOutputStream = mConnection.openOutputStream();
            mOutputStream.write(mRequestData);
            mOutputStream.flush(); //Optional, getResponseCode will flush
            mNetwork.onHTTPEvent(Network.EVENT_SENDING);

            // Getting the response code will open the connection,
            // send the request, and read the HTTP response headers.
            // The headers are stored until requested.
            rc = mConnection.getResponseCode();
//            if (rc != HttpConnection.HTTP_OK) {
//                throw new IOException("HTTP response code: " + rc);
//            }

            if (rc == HttpConnection.HTTP_OK)
            {
                mNetwork.onHTTPEvent(Network.EVENT_RECEIVING);
                mInputStream = mConnection.openInputStream();

                // Get the ContentType
                String type = mConnection.getType();

                if (type.equals("application/octet-stream")) // Only process octet-stream
                {
                    // Get the length and process the data
                    int len = (int)mConnection.getLength();
                    if (len > 0) {
                         int actual = 0;
                         int bytesread = 0 ;
                         byte[] data = new byte[len];
                         while ((bytesread != len) && (actual != -1)) {
                            actual = mInputStream.read(data, bytesread, len - bytesread);
                            bytesread += actual;
                         }
                        //process(data);
                         mReturnData = data;
                         mNetwork.onHTTPEvent(Network.EVENT_END_COMMUNICATION);
                    } else {
                        int ch;
                        while ((ch = mInputStream.read()) != -1) {
                            //process((byte)ch);
                        }
                        mReturnData = null;
                    }
                }
                else
                    mReturnData = null;
            }
            else {
                mNetwork.onHTTPEvent(Network.EVENT_NETWORK_FAILURE);
                mReturnData = null;
            }
        } catch (Exception e) {
            mNetwork.onHTTPEvent(Network.EVENT_NETWORK_FAILURE);
            mReturnData = null;
            e.printStackTrace();
        } finally {
            try {
                if (mInputStream != null)
                    mInputStream.close();
                if (mOutputStream != null)
                    mOutputStream.close();
                if (mConnection != null)
                    mConnection.close();
            } catch (Exception e)
            {       
                e.printStackTrace();
            }
            mInputStream = null;
            mOutputStream = null;
            mConnection = null;            
        }        
    }        
    
    public void stopConnection() // only use in case of time out
    {
        try {
            if (mInputStream != null)
            {
                mInputStream.close();
                mInputStream = null;
            }
            
            if (mOutputStream != null)
            {
                mOutputStream.close();
                mOutputStream = null;
            }
            
            if (mConnection != null)
            {
                mConnection.close();
                mConnection = null;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            mInputStream = null;
            mOutputStream = null;
            mConnection = null;
        }
    }
    
    public void run() {                
        Send();        
    }
}