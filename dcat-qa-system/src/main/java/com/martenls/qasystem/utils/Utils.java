package com.martenls.qasystem.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Utils {

    public static String calendarToXsdDate(Calendar time) {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        return "\"" + formatter.format(time.getTime()) + "\"^^xsd:date" ;
    }

    public static String calendarToXsdDateTime(Calendar time) {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return "\"" + formatter.format(time.getTime()) + "\"^^xsd:dateTime" ;
    }
}