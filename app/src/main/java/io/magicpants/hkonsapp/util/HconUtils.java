package io.magicpants.hkonsapp.util;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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

    //Util method for opening or hiding the soft input keyboard
    public static void showHideKeyboard(Boolean show, Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if ((show) && (v != null)) {
            v.requestFocus();
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        } else if ((!show) && v != null) {
            v.clearFocus();
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


}
