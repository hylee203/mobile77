package com.example.compo;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>{

    ArrayList<Date> dayList;

    CalendarFragment calendarFragment = new CalendarFragment();

    public CalendarAdapter(ArrayList<Date> dayList) {
        this.dayList = dayList;
    }

    private dateClickListener mListener;

    public CalendarAdapter(ArrayList<Date> dayList, dateClickListener listener) {
        this.dayList = dayList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.calendar_cell, parent, false);

        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {

        //날짜 변수에 담기
        Date monthDate = dayList.get(position);

        // 선택한 날짜
        Calendar selectedDate;
        selectedDate = CalendarUtil.selectedDate;

        // 달력 초기화
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);

        // 현재 날짜 달력
        Calendar currentDate;
        currentDate = Calendar.getInstance();

        // 선택한 년 월 일
        int selectedDay = selectedDate.get(Calendar.DAY_OF_MONTH);
        int selectedMonth = selectedDate.get(Calendar.MONTH)+1;
        int selectedYear = selectedDate.get(Calendar.YEAR);

        // 실제 날짜 달력
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
        int currentMonth = currentDate.get(Calendar.MONTH)+1;
        int currentYear = currentDate.get(Calendar.YEAR);

        // 넘어온 달력 데이터
        int displayDay = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH)+1;
        int displayYear = dateCalendar.get(Calendar.YEAR);

        //비교해서 실제 년이 같으면 연한 회색으로 변경, 월까지 같으면 초록색으로 변경
        if(displayYear == currentYear){

            holder.parentView.setBackgroundColor(Color.parseColor("#F6F6F6"));

            //월 맞으면 색상 표시
            if(displayMonth == currentMonth){
                holder.itemView.setBackgroundColor(Color.parseColor("#CEFBC9"));
            }

        }else{ // 년이 다르면 진한 회색
            holder.parentView.setBackgroundColor(Color.parseColor("#D5D5D5"));
        }

        // 실제 날짜와 같으면 맞으면 색상 표시
        if (displayDay == currentDay && displayMonth == currentMonth && displayYear == currentYear) {
            holder.itemView.setBackgroundColor(Color.parseColor("#f9fbc9"));
        }

        // 선택한 날짜면 빨간색 색상 표시
        if (displayDay == selectedDay && displayMonth == selectedMonth && displayYear == selectedYear) {
            holder.itemView.setBackgroundColor(Color.parseColor("#ff6e6e"));
        }

        //날짜 변수에 담기
        int dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);

        holder.dayText.setText(String.valueOf(dayNo));


        //텍스트 색상 지정
        if( (position + 1) % 7 == 0){ //토요일 파랑

            holder.dayText.setTextColor(Color.BLUE);

        }else if( position == 0 || position % 7 == 0){ //일요일 빨강

            holder.dayText.setTextColor(Color.RED);
        }

        //날짜 클릭 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                테스트 메시지 출력 용 코드
//                String iYear = Integer.toString(dateCalendar.get(Calendar.YEAR)); //년
//                String iMonth = Integer.toString(dateCalendar.get(Calendar.MONTH) + 1);//월
//                String iDay = Integer.toString(dateCalendar.get(Calendar.DAY_OF_MONTH));//일
//                String yearMonDay = iYear + "-" +iMonth + "-" + iDay;

                // 선택한 날짜가 selectedDate에 들어감. 그 뒤 인터페이스를 통해 CalendarFragment로 가서 view 재생성
                CalendarUtil.selectedDate = dateCalendar;

                mListener.onDateCilcked();




//                Toast.makeText(holder.itemView.getContext(),CalendarUtil.selectedDate.getTime().toString(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder{

        TextView dayText;

        View parentView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);

            dayText = itemView.findViewById(R.id.dayText);

            parentView = itemView.findViewById(R.id.parentView);
        }
    }
}