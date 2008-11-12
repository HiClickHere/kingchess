/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author dong
 */
public class ChessDataOutputStream extends DataOutputStream {
    public ChessDataOutputStream(OutputStream aOS) 
    {
        super(aOS);
    }
    
    public void writeString16(String16 aString16) throws IOException
    {
        aString16.writeToStream(this);
    }
    
    public void reset()
    {
        this.reset();
    }
}
