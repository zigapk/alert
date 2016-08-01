package com.zigapk.alert.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by zigapk on 8/1/16.
 */
public class ExceptionHolder {
    public String exception;
    public String timestamp;

    public ExceptionHolder(Exception e){
        exception = e.toString();
        timestamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}
