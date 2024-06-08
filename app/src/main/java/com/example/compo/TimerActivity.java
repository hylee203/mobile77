package com.example.compo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {
    //아직 타이머가 끝났을 때 메시지나 알람이 울리는 기능은 구현하지 못했습니다.

    private int duration = 00;
    private boolean timerRunning =false;
    CountDownTimer countDownTimer = null;

    // 멈춘 시각을 저장하는 속성
    private long pauseTime = 0L;

    //시작시간과 종료시간
    long startNow;
    long finishNow;
    long elapsedTimerTime;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        SeekBar seekBarDuration = findViewById(R.id.seekBarDuration);
        final TextView hour = findViewById(R.id.hour);
        final TextView min = findViewById(R.id.min);
        final TextView sec = findViewById(R.id.sec);

        Button finishBtn = findViewById(R.id.finishBtn);
        Button startBtn = findViewById(R.id.startBtn);

        //HomeFragment에서 arrayToHistory를 받음.
        Intent intent = getIntent();
        ArrayList<Object> arrayToHistory = (ArrayList<Object>) intent.getSerializableExtra("arrayToHistory");

        //HomeFragment에서 compoTitle을 받음
        TextView projectName = findViewById(R.id.project_name);
        String compoTitle = getIntent().getStringExtra("compoTitle");
        projectName.setText(compoTitle);

        //타이머의 길이를 정할 seekBar를 설정하는 메소드
        seekBarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int hours = progress/60;
                int minutes = progress%60;
                //SeekBar를 분 단위로 변환함.

                hour.setText(String.format("%02d", hours));
                min.setText(String.format("%02d", minutes));
                sec.setText("00");

                duration = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //start버튼을 누르면 seekBar에서 설정한 길이를 시,분,초 단위로 나누어 실행시킴
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerRunning){
                    timerRunning = true;

                    //시작시간 arrayToHistory에 집어넣기
                    startNow = System.currentTimeMillis();
                    Date startDate = new Date(startNow);
                    //패턴에 hh가 아닌 HH라고 설정한 이유는 HH는 24시간, hh는 12시간이기 때문임
                    SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String startDateTime = startFormat.format(startDate);
                    arrayToHistory.add(startDateTime);

                    //타이머 실행 중 텍스트 UI에 띄우기
                    TextView stopwatchText = findViewById(R.id.timerText);
                    stopwatchText.setText("타이머 실행 중");

                    new CountDownTimer(duration*1000*60,1000){

                        @Override
                        public void onTick(long millisUntilFinished) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String time=  String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)-
                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)),
                                            Locale.getDefault());

                                    final String[] hourMinSec = time.split(":");

                                    hour.setText(hourMinSec[0]);
                                    min.setText(hourMinSec[1]);
                                    sec.setText(hourMinSec[2]);
                                }
                            });
                        }

                        @Override
                        public void onFinish() {
                            //타이머 시간 초기화
                            duration = 00;
                            timerRunning = false;

                            //종료 액티비티로 넘어가는 부분 구현
                            Intent intent = new Intent(TimerActivity.this, TimerFinishActivity.class);

                            //종료시간 저장하고 arrayToHistory을 인텐트로 넘기기
                            finishNow = System.currentTimeMillis();
                            Date finishDate = new Date(finishNow);
                            SimpleDateFormat finishFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String finishDateTime = finishFormat.format(finishDate);
                            arrayToHistory.add(finishDateTime);
                            intent.putExtra("arrayToHistory", arrayToHistory);

                            //Finish Activity UI에 띄울 title 인텐트로 보내기
                            TextView projectNameTv = findViewById(R.id.project_name);
                            String projectName = projectNameTv.getText().toString();
                            intent.putExtra("compoTitle", projectName);

                            //사용시간을 finish 액티비티에 띄우기 위해 인텐트를 넘기는 부분
                            elapsedTimerTime = finishNow-startNow;
                            intent.putExtra("elapsedTimerTime", elapsedTimerTime);

                            startActivity(intent);
                            finish();

                        }
                    }.start();
                }
                else{
                    Toast.makeText(TimerActivity.this, "이미 타이머가 작동 중입니다", Toast.LENGTH_SHORT).show();
                }

                startBtn.setEnabled(false);
                finishBtn.setEnabled(true);
                seekBarDuration.setEnabled(false);
            }
        });

        //finish버튼을 눌렀을 때 프로젝트 이름과 타이머의 남은 시간을 인텐트로 TimerFinishActivity로 보냄.
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //종료 액티비티로 넘어가는 부분 구현
                Intent intent = new Intent(TimerActivity.this, TimerFinishActivity.class);

                //종료시간 저장하고 arrayToHistory을 인텐트로 넘기기
                finishNow = System.currentTimeMillis();
                Date finishDate = new Date(finishNow);
                SimpleDateFormat finishFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String finishDateTime = finishFormat.format(finishDate);
                arrayToHistory.add(finishDateTime);
                intent.putExtra("arrayToHistory", arrayToHistory);

                //Finish Activity UI에 띄울 title 인텐트로 보내기
                TextView projectNameTv = findViewById(R.id.project_name);
                String projectName = projectNameTv.getText().toString();
                intent.putExtra("compoTitle", projectName);

                //사용시간을 finish 액티비티에 띄우기 위해 인텐트를 넘기는 부분
                elapsedTimerTime = finishNow-startNow;
                intent.putExtra("elapsedTimerTime", elapsedTimerTime);

                startActivity(intent);
                finish();
            }
        });
    }

}