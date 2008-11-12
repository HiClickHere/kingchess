/*
 * Transaction.java
 *
 * Created on June 29, 2007, 11:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package chess.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author hoangta
 */
public class Transaction {
   
    /** Creates a new instance of Transaction */
    public Transaction() 
    {
    }
    
    public static void beginTransaction(Connection aConnection)
    {
        try
        {
            aConnection.setAutoCommit(false);
        }
        catch(Exception e)
        {
        }
    }
    
//    public static void endTransaction(Connection aConnection)
//    {
//        try
//        {
//            aConnection.setAutoCommit(true);
//        }
//        catch(Exception e)
//        {
//        }
//    }
    
    public static void commit(Connection aConnection) throws SQLException
    {
        aConnection.commit();
    }
    
    public static void rollback(Connection aConnection) 
    {
        try
        {
            aConnection.rollback();
        }
        catch(Exception e)
        {
            PunchLogger.logException("rollback", e);
        }
    }

}
