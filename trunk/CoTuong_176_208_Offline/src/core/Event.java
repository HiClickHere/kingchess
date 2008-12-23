/*
 * Event.java
 *
 * Created on October 30, 2008, 6:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package core;

/**
 *
 * @author dong
 */
public class Event {            
    public int mType;    
    public byte mData[];    
    
    public Event(int type, byte[] data)
    {
        mType = type;
        mData = data;
    }
}
