package io.magicpants.hkonsapp;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * utility class
 * Created by Erik on 31.12.2017.
 */

public class HconUtils {

    public static String normalizeMetricDate(String dateText){
        if(dateText != null){
            String dateStr = dateText.substring(9, dateText.length());
            dateStr = dateStr.replace(dateStr.substring(6, 8), "");
            dateStr = dateStr.replace("/", ".");
            return dateStr;
        }
        else {return null;}
    }

    public static String formatDateForDataBase(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Calendar rightNowGmtOne = Calendar.getInstance(TimeZone.getTimeZone("GMT+1"));
        return dateFormat.format(rightNowGmtOne.getTime());
    }

}
