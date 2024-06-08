package com.example.compo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class StopwatchFinishActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    SQLiteDatabase database;

    @Override
    public void onCreate(Bundle savedInsatanceState){
        super.onCreate(savedInsatanceState);
        setContentView(R.layout.activity_stopwatch_finish);

        Button finishBtn = findViewById(R.id.btn_finish);
        TextView projectNameTextView = findViewById(R.id.project_name);
        TextView chronoTextView = findViewById(R.id.finish_chronometer);

        Intent intent = getIntent();

        //compoTitle을 인텐트로 받기
        String projectName = intent.getStringExtra("compoTitle");
        projectNameTextView.setText(projectName);

        //타이머 시간을 인텐트로 받기
        String chronoTime = intent.getStringExtra("chronoTextview");
        chronoTextView.setText(chronoTime);

        //arrayToHistory 인텐트로 받기
        ArrayList<Object> arrayToHistory = (ArrayList<Object>)  intent.getSerializableExtra("arrayToHistory");


        //finish 버튼을 클릭하면 MainActivity로 넘어가고, 스택에 쌓인 액티비티들을 모두 삭제함(MainActivity만 남게 함).
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                //arrayToHistory에 memo에 적힌 글 저장
                EditText memoField = findViewById(R.id.project_memo);
                String memo = memoField.getText().toString();
                arrayToHistory.add(memo);

                //compo_history DB에 arrayToHistory에 저장된 값들 집어넣기
                int favor_id = (int) arrayToHistory.get(0);
                String start_time = (String) arrayToHistory.get(1);
                String end_time = (String) arrayToHistory.get(2);
                String me_mo = (String) arrayToHistory.get(3);

                databaseHelper = new DatabaseHelper(getApplicationContext());
                database = databaseHelper.getWritableDatabase();
                database.execSQL("insert into component_history(compo_id, start_time, end_time, memo)" +
                        "values ("+ favor_id +", '" + start_time + "', '" + end_time + "', '" + me_mo +"')");


                startActivity(intent);
                finish();
            }
        });
    }
}
