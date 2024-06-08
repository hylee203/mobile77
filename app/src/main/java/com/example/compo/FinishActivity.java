package com.example.compo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FinishActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    SQLiteDatabase database;
    String tableName = "pedometer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_finish);

        Button finishBtn = findViewById(R.id.btn_finish);
        TextView projectNameTextView = findViewById(R.id.project_name);
        TextView chronoTextView = findViewById(R.id.finish_chronometer);
        TextView stepCountTextView = findViewById(R.id.step_count);
        TextView calCountTextView = findViewById(R.id.cal_count);
        EditText projectMemoEditText = findViewById(R.id.project_memo);

        Intent intent = getIntent();

        // Retrieve the project name, elapsed time, step count, and calories burned from the Intent
        String projectName = intent.getStringExtra("projectName");
        long elapsedTime = intent.getLongExtra("elapsedTime", 0);
        int stepCount = intent.getIntExtra("stepCount", 0);
        double caloriesBurned = intent.getDoubleExtra("caloriesBurned", 0.0);
        long startTime = intent.getLongExtra("startTime", 0);
        long endTime = intent.getLongExtra("endTime", 0);

        projectNameTextView.setText(projectName);

        // Format the elapsed time and set it to the TextView
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);
        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        chronoTextView.setText(formattedTime);

        // Set step count and calories burned to their respective TextViews
        stepCountTextView.setText(String.valueOf(stepCount));
        calCountTextView.setText(String.format("%.2f", caloriesBurned));

        // Format the start and end times and save them as strings
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedStartTime = sdf.format(new Date(startTime));
        String formattedEndTime = sdf.format(new Date(endTime));

        // Finish button click listener
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String memo = projectMemoEditText.getText().toString();
                memo += "\nProject Name: " + projectName;
                memo += "\nElapsed Time (seconds): " + (elapsedTime / 1000.0);
                memo += "\nStep Count: " + stepCount;
                memo += "\nCalories Burned: " + caloriesBurned;

                //
                databaseHelper = new DatabaseHelper(getApplicationContext());
                database = databaseHelper.getWritableDatabase();
                database.execSQL("insert into component_history(compo_id, start_time, end_time, memo)" +
                        "values ("+ 1 +", '" + formattedStartTime + "', '" + formattedEndTime + "', '" + memo +"')");
                //



                Intent intent = new Intent(FinishActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}