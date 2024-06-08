package com.example.compo;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
{

    // 프래그먼트 변수 선언
    private final int fragment_home = 1;
    private final int fragment_calendar = 2;
    private final int fragment_setting = 3;


    // 실시간 날짜 변수
    CurrentDate currentDate;
    // 컴포넌트 추가 버튼
    FloatingActionButton addButton;
    // 홈 버튼
    ImageButton homeButton;
    // 히스토리 버튼
    ImageButton calendarButton;
    // 기타 메뉴 버튼
    ImageButton settingButton;

    // 데이터베이스 변수
    DatabaseHelper databaseHelper;
    SQLiteDatabase database;


    // 메인 화면 동작
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 화면에 보여질 첫 프래그먼트 불러오기
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        HomeFragment homefragment = new HomeFragment();
        transaction.replace(R.id.fragment_view, homefragment);
        transaction.commit();




        // 메인 화면의 버튼 3개 ID 할당
        homeButton = (ImageButton) findViewById(R.id.home_icon_button);
        calendarButton = (ImageButton) findViewById(R.id.calendar_icon_button);
        settingButton = (ImageButton) findViewById(R.id.setting_icon_button);

        // 왼쪽 하단 버튼 클릭 시
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 홈 프래그먼트로 변경
                FragmentView(1);

            }
        });

        // 가운데 하단 버튼 클릭 시
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 기록 프래그먼트로 변경
                FragmentView(2);
            }
        });

        // 오른쪽 하단 버튼 클릭 시
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 기타메뉴 프래그먼트로 변경
                FragmentView(3);
            }
        });
    }

    private void FragmentView(int fragment){

        //FragmentTransactiom를 이용해 프래그먼트를 사용합니다.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment){
            case 1:
                // 첫번 째 프래그먼트 호출
                HomeFragment homefragment = new HomeFragment();
                transaction.replace(R.id.fragment_view, homefragment);
                transaction.commit();
                break;

            case 2:
                // 두번 째 프래그먼트 호출
                CalendarFragment calendarfragment = new CalendarFragment();
                transaction.replace(R.id.fragment_view, calendarfragment);
                transaction.commit();
                break;

            case 3:
                // 두번 째 프래그먼트 호출
                SettingFragment settingfragment = new SettingFragment();
                transaction.replace(R.id.fragment_view, settingfragment);
                transaction.commit();
                break;


        }

    }
}