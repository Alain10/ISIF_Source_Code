package ca.ott.al.starmap;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class GrayMonday {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        GregorianCalendar calendar = new GregorianCalendar (3132, 8, 7, 12, 0, 0);
        System.out.println("The year "+ calendar.get(Calendar.YEAR));
        System.out.println("The month "+ calendar.get(Calendar.MONTH));
        System.out.println("The day of the month " +calendar.get(Calendar.DAY_OF_MONTH));
        System.out.println("The day of the week: "+ calendar.get(Calendar.DAY_OF_WEEK)); 
    }    
    
}
