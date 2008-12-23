/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 *
 * @author dong
 */
public class ChessDataInputStream extends DataInputStream {
    public ChessDataInputStream(InputStream is)
    {
        super(is);
    }
    
    public String16 readString16() throws IOException
    {
        String16 aString16 = new String16();
        int length = readInt();
        for (int i = 0; i < length; i++) {
            aString16.addChar((char)readShort());
        }
        return aString16;
    }
}
