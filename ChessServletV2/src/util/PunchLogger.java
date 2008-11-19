package util;

/*
 * Logger.java
 *
 * Created on June 29, 2007, 1:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */



import java.io.FileWriter;
import java.io.PrintWriter;
/**
 *
 * @author hoangta
 */
public class PunchLogger {
    
    public static String ERROR_LOG_FILE = "d:/error.log";

    /** Creates a new instance of Logger */
    public PunchLogger() {
    }

    //Generic exception logging
    public static void logException(String userName, String functionName, Throwable e) 
    {
        try{
            PrintWriter errlog = new PrintWriter(new FileWriter(ERROR_LOG_FILE, true));
            errlog.println();
            errlog.println("<Exception User=\""+userName+"\" Time=\"" + DateToString.dateToString(true) + "\" Function =\"" + functionName + "\">");
            e.printStackTrace(errlog);
            errlog.println("</Exception>");
            errlog.close();
            
        }catch(Exception ex){
            System.out.println("Error at logException()");
            ex.printStackTrace();
        }
    }
    
    public static void logException(String functionName, Throwable e) 
    {
        logException("", functionName, e);
    }

    //Generic exception logging
    public static void logException(String userName, String functionName, Exception e) 
    {
        try{
            PrintWriter errlog = new PrintWriter(new FileWriter(ERROR_LOG_FILE, true));
            errlog.println();
            errlog.println("<Exception User=\""+userName+"\" Time=\"" + DateToString.dateToString(true) + "\" Function =\"" + functionName + "\">");
            e.printStackTrace(errlog);
            errlog.println("</Exception>");
            errlog.close();
        }catch(Exception ex){
            System.out.println("Error at logException()");
            ex.printStackTrace();
        }
    }

    //Generic exception logging
    public static void logException(String functionName, Exception e) 
    {
        logException("", functionName, e);
    }
    
    public static void logException(String userName, String msg)
    {        
        try{
            PrintWriter errlog = new PrintWriter(new FileWriter(ERROR_LOG_FILE, true));
            errlog.println();
            errlog.print("<Message Time=\"" + DateToString.dateToString(true) + "\" Log =\"" + msg + "\"/>");
            errlog.close();
        }catch(Exception ex){
            System.out.println("Error at logException()");
            ex.printStackTrace();
        }
    }
    
    public static void createLogFile(String name)
    {
        try {
            PrintWriter errorLog = new PrintWriter(ERROR_LOG_FILE);
            errorLog.close();            
        } catch (Exception ex)
        {
            System.out.println("Error at logException()");
            ex.printStackTrace();
        }
    }

    public static void logException(String msg)
    {
        logException("", msg);
    }
}
