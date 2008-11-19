/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import main.Context;

/**
 *
 * @author dong
 */
public class Network {

    public final static int EVENT_SETUP_CONNECTION = 0;
    public final static int EVENT_SENDING = 1;
    public final static int EVENT_RECEIVING = 2;
    public final static int EVENT_END_COMMUNICATION = 3;
    public final static int EVENT_NETWORK_FAILURE = 4;    
    public final static int EVENT_LOSE_CONNECTION = 5;
    
    public final static int EVENT_TEXTBOX_FOCUS = 6;
    public final static int EVENT_TEXTBOX_INFOCUS = 7;
    
    private static Network mSingleInstance = null;
    private Context mContext;      
    private ChessHTTPConnector mConnector;
    
    public Vector mRequestQueue;
    
    public boolean mIsBusy;
    
    private Network(Context aContext)
    {
        mSingleInstance = this;
        mContext = aContext;
        mConnector = ChessHTTPConnector.createHTTPConnector(this);
        mIsBusy = false;
        mRequestQueue = new Vector();
    }
    
    public static Network createNetworkHandler(Context aContext)
    {
        if (mSingleInstance != null)
            return mSingleInstance;
        else
            return new Network(aContext);
    }    
    
    private byte[] mReturnData;
    
    public ChessDataInputStream getDataStreamReturnFromServer()
    {
        ByteArrayInputStream aByteArray = new ByteArrayInputStream(mReturnData);
        return new ChessDataInputStream(aByteArray);
    }
    
    public byte[] getDataReturnFromServer()
    {
        return mReturnData;        
    }
    
    public void sendMessage(short request, byte[] dataStreamToServer)
    {        
        mRequestQueue.addElement(new RequestRecord(request, dataStreamToServer));
    }
    
    public void onTick(long aMilliseconds)
    {
        if (mIsBusy)
        {
            // Wait too long for the connection
            if (System.currentTimeMillis() - mStartConnectionClock > CONNECTION_TIME_OUT)
            {
                mConnector.stopConnection();
            }
        }
        else if (!mRequestQueue.isEmpty() && !mIsBusy)
        {        
            try {
                // combine all request in queue
                ByteArrayOutputStream aByteArray = new ByteArrayOutputStream();
                ChessDataOutputStream aOut = new ChessDataOutputStream(aByteArray);
                for (int i = 0; i < mRequestQueue.size(); i++) {
                    RequestRecord rr = (RequestRecord)mRequestQueue.elementAt(i);
                    aOut.writeShort(rr.mRequest);
                    System.out.println("Client will send request: " + rr.mRequest);
                    aOut.write(rr.mOut);
                }

                // remove all request in queue
                mRequestQueue.removeAllElements(); 

                // send it now
                mConnector.SendRequest(aByteArray.toByteArray());
            } catch (Exception e)
            {
                e.printStackTrace();
            }                    
        }
    }
    
    public void stopConnection()
    {
        if (mConnector != null)
            mConnector.stopConnection();
    }
    
    private long mStartConnectionClock;
    public final static long CONNECTION_TIME_OUT = 20000;
    
    public void onHTTPEvent(int event)
    {
        switch (event)
        {
            case EVENT_SETUP_CONNECTION:
                mStartConnectionClock = System.currentTimeMillis();
                mIsBusy = true;
                mReturnData = null;
                mContext.onNetworkEvent(new Event(event, null));
                break;
            case EVENT_SENDING:
            case EVENT_RECEIVING:
                mContext.onNetworkEvent(new Event(event, null));
                break;            
            case EVENT_END_COMMUNICATION:
                mReturnData = mConnector.getReturnData();                
                mIsBusy = false;                
                mContext.onNetworkEvent(new Event(event, null));
                break;
            case EVENT_NETWORK_FAILURE:
                mIsBusy = false;
                mContext.onNetworkEvent(new Event(event, null));
                break;
        }
    }
}

class RequestRecord
{
    public short mRequest;
    public byte[] mOut;    
    public RequestRecord(short aRequest, byte[] aOut)
    {
        mRequest = aRequest;
        mOut = aOut;
    }
}
