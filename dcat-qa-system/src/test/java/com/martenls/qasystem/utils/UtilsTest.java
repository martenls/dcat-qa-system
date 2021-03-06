package com.martenls.qasystem.utils;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void calendarToXsdDate() {
        Calendar date = new GregorianCalendar(2020, Calendar.JUNE, 23);
        assertEquals("\"2020-06-23\"^^<http://www.w3.org/2001/XMLSchema#date>", Utils.calendarToXsdDate(date));
    }

    @Test
    void calendarToXsdDateTime() {
        Calendar date = new GregorianCalendar(2020, Calendar.JUNE, 23, 16, 0);
        assertEquals("\"2020-06-23T16:00:00.000\"^^<http://www.w3.org/2001/XMLSchema#dateTime>", Utils.calendarToXsdDateTime(date));
    }
}