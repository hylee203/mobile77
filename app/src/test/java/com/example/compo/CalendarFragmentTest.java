package com.example.compo;

import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class CalendarFragmentTest extends TestCase {

    CalendarFragment calendarFragment = new CalendarFragment();

    public void test_monthYearFromDate()
    {
        CalendarUtil.calendarDate = Calendar.getInstance();
        assertEquals("2024년 5월 ", calendarFragment.monthYearFromDate(CalendarUtil.calendarDate));
    }
}