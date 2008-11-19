package util;

/*
 * DateToString.java
 *
 * Created on October 3, 2007, 9:31 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */



import java.util.GregorianCalendar;

/**
 *
 * @author Nguyen Hoang
 */
public class DateToString {
    
    public static final int YY_MM_DD = 0;
    public static final int DD_MM_YY = 1;
    public static final int MM_DD_YY = 2;
    
    public static GregorianCalendar calendar = new GregorianCalendar();
    /** Creates a new instance of DateToString */
    
    

    public static String dateToString(){
        return dateToString(System.currentTimeMillis(), YY_MM_DD, true);
    }
    public static String dateToString(boolean isNeedMillis){
        return dateToString(System.currentTimeMillis(), YY_MM_DD, isNeedMillis);
    }
    public static String dateToString(long currentTime){
        return dateToString(currentTime, YY_MM_DD, false);
    }
    
    public static String dateToString(int formatStringID){
        return dateToString(System.currentTimeMillis(), formatStringID, false);
    }
    
    public static String dateToString(long currentTime, int formatStringID, boolean isNeedMillis){
        String sResult = "";
        calendar.setTimeInMillis(currentTime);
        
        switch (formatStringID){
            case YY_MM_DD:
                sResult += calendar.get(GregorianCalendar.YEAR)+"/"+(calendar.get(GregorianCalendar.MONTH)+1)
                    +"/"+calendar.get(GregorianCalendar.DAY_OF_MONTH);
                break;
            case DD_MM_YY:
                sResult += calendar.get(GregorianCalendar.DAY_OF_MONTH)+"/"+(calendar.get(GregorianCalendar.MONTH)+1)
                    +"/"+calendar.get(GregorianCalendar.YEAR);
                break;
            case MM_DD_YY:
                sResult += (calendar.get(GregorianCalendar.MONTH)+1)+"/"+calendar.get(GregorianCalendar.DAY_OF_MONTH)
                    +"/"+calendar.get(GregorianCalendar.YEAR);
                break;
            default: break;    
        }
        
        sResult += " "+calendar.get(GregorianCalendar.HOUR_OF_DAY)+":"+calendar.get(GregorianCalendar.MINUTE)+":"
                +calendar.get(GregorianCalendar.SECOND);
        if (isNeedMillis)
            sResult += " "+calendar.get(GregorianCalendar.MILLISECOND);
        return sResult;
              
    }
}
