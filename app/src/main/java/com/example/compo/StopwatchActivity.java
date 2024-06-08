package com.example.compo;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StopwatchActivity extends AppCompatActivity {

    Chronometer chronometer;

    // 멈춘 시각을 저장하는 속성
    private long pauseTime = 0L;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        Button finishBtn = findViewById(R.id.finishBtn);
        Button startBtn = findViewById(R.id.startBtn);
        Chronometer mainChronometer = findViewById(R.id.main_chronometer);

        //HomeFragment에서 arrayToHistory를 받음.
        Intent intent = getIntent();
        ArrayList<Object> arrayToHistory = (ArrayList<Object>) intent.getSerializableExtra("arrayToHistory");

        //compoTitle 인텐트 받아오기
        TextView compoTitleName = findViewById(R.id.project_name);
        String compoTitle = intent.getStringExtra("compoTitle");
        compoTitleName.setText(compoTitle);


        //start버튼을 누르면 크로노미터를 이용한 스톱워치가 작동함.
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainChronometer.setBase(SystemClock.elapsedRealtime()+pauseTime);
                mainChronometer.start();

                //시작시간 arrayToHistory에 집어넣기
                long startNow = System.currentTimeMillis();
                Date startDate = new Date(startNow);
                //패턴에 hh가 아닌 HH라고 설정한 이유는 HH는 24시간, hh는 12시간이기 때문임
                SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDateTime = startFormat.format(startDate);
                arrayToHistory.add(startDateTime);

                //스톱워치 실행 중 텍스트 UI에 띄우기
                TextView stopwatchText = findViewById(R.id.stopwatchText);
                stopwatchText.setText("스톱워치 실행 중");


                startBtn.setEnabled(false);
                finishBtn.setEnabled(true);
            }
        });

        //finish 버튼을 누르면 프로젝트 네임과 스톱워치 시간을 인텐트로 StopwatchFinishActivity로 보냄.
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //종료 액티비티로 넘어가는 부분 구현
                Intent intent = new Intent(StopwatchActivity.this, StopwatchFinishActivity.class);

                //종료시간 저장하고 arrayToHistory을 인텐트로 넘기기
                long finishNow = System.currentTimeMillis();
                Date finishDate = new Date(finishNow);
                SimpleDateFormat finishFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String finishDateTime = finishFormat.format(finishDate);
                arrayToHistory.add(finishDateTime);
                intent.putExtra("arrayToHistory", arrayToHistory);

                //스톱워치 시간을 인텐트로 넘기기
                String currentTime = mainChronometer.getText().toString();
                intent.putExtra("chronoTextview", currentTime);

                //Finish Activity UI에 띄울 title 인텐트로 보내기
                String compoTitle = compoTitleName.getText().toString();
                intent.putExtra("compoTitle", compoTitle);


                startActivity(intent);
                finish();
            }
        });
    }

}