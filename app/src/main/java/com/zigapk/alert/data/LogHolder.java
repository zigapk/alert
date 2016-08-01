package com.zigapk.alert.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by zigapk on 8/1/16.
 */
public class LogHolder {
    public String log;
    public String timestamp;

    public LogHolder(String log){
        this.log = log;
        timestamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}
