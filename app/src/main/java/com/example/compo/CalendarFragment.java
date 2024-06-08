package com.example.compo;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarFragment extends Fragment implements dateClickListener {

    // History 에서 불러온 데이터들로 TextView를 만들 때, 그 View들의 id 저장.
    private ArrayList<Integer> textViewIDs = new ArrayList<>();

    private recordDialog recorddialog;
    TextView monthYearText; //년월 텍스트뷰

    RecyclerView recyclerView;
    RelativeLayout recordView;

    SQLiteDatabase database;

    DatabaseHelper databaseHelper;
    ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_calendar,container,false);

        //초기화
        monthYearText = rootView.findViewById(R.id.monthYearText);
        ImageButton preBtn = rootView.findViewById(R.id.pre_btn);
        ImageButton nextBtn = rootView.findViewById(R.id.next_btn);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recordView = rootView.findViewById(R.id.recordTable);

        // 날짜 초기화. 처음 이 프래그먼트를 시행했을 때, 실제 현재 날짜로 선택됨
        CalendarUtil.calendarDate = Calendar.getInstance();
        CalendarUtil.selectedDate = Calendar.getInstance();

        // Calendar 타입 데이터를 형식에 맞춰 string으로 변환.
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        String currentformattedDate = sdf.format(Calendar.getInstance().getTime());

        // 캘린더 달력 A화면 설정
        setMonthView();

        // 선택된 날짜의 record 불러오기
        setRecordView(currentformattedDate);

        //이전 달 버튼 이벤트
        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //-1한 월을 넣어준다. (2월 -> 1월)
                CalendarUtil.calendarDate.add(Calendar.WEEK_OF_MONTH, -1);// -1
                setMonthView();
            }
        });

        //다음 달 버튼 이벤트
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //+1한 월을 넣어준다.(2월 -> 3월)
                CalendarUtil.calendarDate.add(Calendar.WEEK_OF_MONTH, 1); //+1
                setMonthView();
            }
        });

        return rootView;
    }//onCreate

    //날짜 타입 설정(4월 2020)
    public String monthYearFromDate(Calendar calendar){

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        String monthYear = year + "년 "+ month + "월 ";

        return monthYear;
    }


    //화면 설정
    private void setMonthView(){

        //년월 텍스트뷰 셋팅
        monthYearText.setText(monthYearFromDate(CalendarUtil.calendarDate));

        //해당 월 날짜 가져오기
        ArrayList<Date> dayList = daysInMonthArray();

        //어뎁터 데이터 적용
        CalendarAdapter adapter = new CalendarAdapter(dayList,this);

        //레이아웃 설정(열 7개)
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), 7);

        //레이아웃 적용
        recyclerView.setLayoutManager(manager);

        //어뎁터 적용
        recyclerView.setAdapter(adapter);

    }

    //날짜 생성
    public ArrayList<Date> daysInMonthArray(){

        ArrayList<Date> dayList = new ArrayList<>();

        //날짜 복사해서 변수 생성
        Calendar monthCalendar = (Calendar) CalendarUtil.calendarDate.clone();


        //1일로 셋팅 (4월 1일)
        monthCalendar.set(Calendar.DAY_OF_WEEK, 1);

        //요일 가져와서 -1 일요일:1, 월요일:2
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) -1;

        //날짜 셋팅 (-5일전)
        monthCalendar.add(Calendar.DAY_OF_WEEK, -firstDayOfMonth);

        // 7번 반복.
        while(dayList.size() < 7){

            //리스트에 날짜 등록
            dayList.add(monthCalendar.getTime());

            //1일씩 늘린 날짜로 변경 1일 -> 2일 -> 3일
            monthCalendar.add(Calendar.DAY_OF_WEEK, 1);
        }

        return dayList;
    }

    public void setRecordView(String currentTime){

        // DB 연결.
        databaseHelper = new DatabaseHelper(getContext());
        database = databaseHelper.getWritableDatabase();

        // 들어온 날짜와 같은 시작 시간을 가진 record를 찾음.
        Cursor cursor = database.rawQuery("SELECT * FROM component_history WHERE substr(start_time, 0, 11) = '" + currentTime + "'", null);

        int rowCount = cursor.getCount();

        // 찾은 횟수 만큼 TextView 생성.
        for (int i = 0; i < rowCount; i++){
            cursor.moveToNext();
            int hist_id = cursor.getInt(0); // hist_id
            int compo_id = cursor.getInt(1); // compo_id
            String stringStartTime = cursor.getString(2);
            String stringEndTime = cursor.getString(3);
            String memo = cursor.getString(4);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date dateStartTime = null;
            try {
                dateStartTime = formatter.parse(stringStartTime);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Date dateEndTime = null;
            try {
                dateEndTime = formatter.parse(stringEndTime);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            int startHour = dateStartTime.getHours();
            int startMinute = dateStartTime.getMinutes();
            int minuteDifference = (60 * dateEndTime.getHours() + dateEndTime.getMinutes()) - (60 * dateStartTime.getHours() + dateStartTime.getMinutes());
            int hourDifference = dateEndTime.getHours() - dateStartTime.getHours();

            //1. 텍스트뷰 객체생성
            TextView textViewNm = new TextView(getContext());

            //2. 텍스트뷰에 들어갈 문자설정
            textViewNm.setText(memo);


            //5. 텍스트뷰 ID설정
            textViewNm.setId(hist_id);

            //6. 레이아웃 설정. 하나의 row의 크기는 180dp. margin이 위 아래로 1dp. 만약 두 row가 붙어 있다면 180 + 2 + 180 dp. 180dp * 시간차(분단위) 를 60으로 나눠 분에 따른 정확한 시간 계산이 되도록 함.
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,347,getResources().getDisplayMetrics()) - 2
                    ,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,Math.round(180 * (minuteDifference) / 60) + 2 * (hourDifference),getResources().getDisplayMetrics()));
            param.setMarginStart((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,61,getResources().getDisplayMetrics()) + 1);
            param.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,Math.round(180 * (startMinute) / 60)  + 2 * (startHour) + 180 * (startHour) + 1,getResources().getDisplayMetrics());

            // 7. 설정한 레이아웃 텍스트뷰에 적용
            textViewNm.setLayoutParams(param);

            //8. 텍스트뷰 백그라운드색상 설정
            textViewNm.setBackgroundColor(Color.rgb(184,236,184));

            //9. 텍스트뷰 글자크기 설정
            float textSizeValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(180 * (minuteDifference) / 60) + 2 * (hourDifference),getResources().getDisplayMetrics());
            float textViewValue10Min = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,Math.round(180 * (10) / 60) + 2 * (hourDifference),getResources().getDisplayMetrics());
            if(textSizeValue < textViewValue10Min){
                textViewNm.setTextSize(textSizeValue/4);//텍스트 크기
            }
            else{
                textViewNm.setTextSize(24);//텍스트 크기
                textViewNm.setTypeface(null, Typeface.BOLD);
            }

            //10. 생성된 TextView의 클릭 리스너 생성.
            textViewNm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleTextViewClick(hist_id, v); // 클릭된 TextView에 대한 처리
                }
            });

            //11. 생성및 설정된 텍스트뷰 레이아웃에 적용
            recordView.addView(textViewNm);

            //12. record의 hist_id 저장.
            textViewIDs.add(hist_id);


        }

        cursor.close();

    }

    // ID를 받아 TextView를 삭제하는 메서드
    private void removeViewByID(int viewID) {
        View viewToRemove = recordView.findViewById(viewID);
        if (viewToRemove != null) {
            ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);
        }
    }

    // 모든 자식 뷰를 삭제하는 메서드 정의
    private void removeAllRecordViews() {
        for (int id : textViewIDs) {
            removeViewByID(id);
        }

        textViewIDs.clear();
    }

    // 인터페이스 오버라이드. CalendarAdapter에서 날짜 클릭 리스너를 타고 넘어옴.
    @Override
    public void onDateCilcked() {

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        String currentformattedDate = sdf.format(CalendarUtil.selectedDate.getTime());

        // 뷰삭제
        removeAllRecordViews();

        //화면 설정
        setMonthView();
        setRecordView(currentformattedDate);
    }

    // record onClickListener가 실행되었을 때, 다이어로그를 띄우는 메서드.
    public void handleTextViewClick(int history_id, View view) {
        recorddialog = new recordDialog(view.getContext(),history_id);
        recorddialog.show();

        Toast.makeText(view.getContext(), "Clicked", Toast.LENGTH_SHORT).show();
    }
}
