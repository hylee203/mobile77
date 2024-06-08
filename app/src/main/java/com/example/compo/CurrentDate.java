package com.example.compo;

import java.util.Calendar;
import java.util.Locale;

public class  CurrentDate
{
    public String getDate()
    {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 일요일: 1, 월요일: 2, ..., 토요일: 7

        // 요일을 한글로 변환
        String dayOfWeekString;
        switch (dayOfWeek)
        {
            case Calendar.SUNDAY:
                dayOfWeekString = "일요일";
                break;
            case Calendar.MONDAY:
                dayOfWeekString = "월요일";
                break;
            case Calendar.TUESDAY:
                dayOfWeekString = "화요일";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeekString = "수요일";
                break;
            case Calendar.THURSDAY:
                dayOfWeekString = "목요일";
                break;
            case Calendar.FRIDAY:
                dayOfWeekString = "금요일";
                break;
            case Calendar.SATURDAY:
                dayOfWeekString = "토요일";
                break;
            default:
                dayOfWeekString = "알 수 없음";
        }
        return String.format(Locale.getDefault(), "%d월 %d일 %s", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), dayOfWeekString);
    }
}
