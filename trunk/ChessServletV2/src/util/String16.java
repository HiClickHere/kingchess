package util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;


/**
 *
 * @author dong 
 * Created this class for syncronize the 16bit char between J2ME and BREW client.
 * Remember char type in JAVA is 2byte.
 */
public class String16 {
    private Vector mCharBuffer;
    
    public String16(String aString)
    {
        mCharBuffer = new Vector();
        for (int i = 0; i < aString.length(); i++)
            mCharBuffer.addElement(new BigChar(aString.charAt(i)));
    }
    
    public String16()
    {
        mCharBuffer = new Vector();        
    }
    
    public void addChar(char aChar)
    {
        mCharBuffer.addElement(new BigChar(aChar));
    }
    
    public String toJavaString()
    {
        StringBuffer aStringBuffer = new StringBuffer();
        for (int i = 0; i < mCharBuffer.size(); i++)
        {
            BigChar aChar = (BigChar)(mCharBuffer.elementAt(i));
            aStringBuffer.append(aChar.mChar);
        }
        return aStringBuffer.toString();
    }
    
    public void writeToStream(DataOutputStream aOS) throws IOException
    {
        aOS.writeInt(mCharBuffer.size()); // write string length first
        for (int i = 0; i < mCharBuffer.size(); i++)
        {
            BigChar aChar = (BigChar)(mCharBuffer.elementAt(i));
            aOS.writeShort((short)aChar.mChar);
        }
    }
}
class BigChar
{
    public char mChar;
    public BigChar(char aChar)
    {
        mChar = aChar;
    }
}
