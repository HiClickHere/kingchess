package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ManagedDataOutputStream extends DataOutputStream 
{
    ByteArrayOutputStream mainBuffer;
    DataOutputStream    mainDataOutputStream;
    ByteArrayOutputStream currentMsgBuffer;
    
    /** Creates a new instance of ManagedDataOutputStream */
    public ManagedDataOutputStream(ByteArrayOutputStream buf)
    {
        super(buf);
        currentMsgBuffer = buf;

        mainBuffer = new ByteArrayOutputStream();
        mainDataOutputStream = new DataOutputStream(mainBuffer);
    }
    
    public void reset()
    {
        currentMsgBuffer.reset();
        mainBuffer.reset();
    }
    
    private void moveToMainBuffer() throws IOException 
    {
        //the previous mgs is finished, move to the main buffer and insert msg size.
        if (currentMsgBuffer.size() < 4) return;

        //System.out.println("currentMsgBuffer... :"+ (currentMsgBuffer.size() - 4) + "--------");
        
        DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(currentMsgBuffer.toByteArray()));
        
        int msgID = dataIn.readInt();
        mainDataOutputStream.writeInt(msgID);
        //System.out.println("MsgID:"+ msgID  + "--------");
        mainDataOutputStream.writeShort(currentMsgBuffer.size() - 4); //2 for header (dataIn.readInt())

        while(dataIn.available() > 0)
            mainDataOutputStream.writeByte(dataIn.readByte());
    
        currentMsgBuffer.reset();
    }
    
    public void writeRequestID(int id) throws IOException 
    {
        if (currentMsgBuffer.size() > 4)
            moveToMainBuffer();

        super.writeInt(id);
        super.flush();
    }

    public void writeRequestID(short id) throws IOException 
    {
        if (currentMsgBuffer.size() > 4)
            moveToMainBuffer();

        super.writeInt(id);
        super.flush();
    }    
    
    public byte[] getDataToSend() throws IOException 
    {
        super.flush();
        moveToMainBuffer();
        
        return mainBuffer.toByteArray();
    }
}
