/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.ByteArrayOutputStream;

/**
 *
 * @author dong
 */
public class ChessServerResponse {
    ByteArrayOutputStream byteArray;
    ChessDataOutputStream mOut;
    
    public ChessServerResponse()
    {
        byteArray = new ByteArrayOutputStream();
        mOut = new ChessDataOutputStream(byteArray);
    }
    
    public void add(short aShort) throws Exception
    {
        mOut.writeShort(aShort);
    }
    
    public void add(int aInt) throws Exception
    {
        mOut.writeInt(aInt);
    }
    
    public void add(String16 aString16) throws Exception
    {
        mOut.writeString16(aString16);
    }
    
    public void add(byte aByte) throws Exception
    {
        mOut.writeByte(aByte);
    }
    
    public void packResponse(short responseType, ChessDataOutputStream out) throws Exception
    {
        out.writeShort(responseType);
        out.writeInt(byteArray.size());
        out.write(byteArray.toByteArray());
        
        byteArray = null;
        mOut = null;                       
        //reset the response for another pack
        byteArray = new ByteArrayOutputStream();
        mOut = new ChessDataOutputStream(byteArray);
    }
}
