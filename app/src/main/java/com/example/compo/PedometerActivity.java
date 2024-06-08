package com.example.compo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PedometerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private TextView stepCountView, calView, getNameView;
    private Button startBtn, finishBtn;
    private Chronometer chronometer;

    private boolean isRunning = false;
    private int currentSteps = 0;
    private double caloriesBurned = 0.0;
    private long pauseOffset = 0;
    private final double CALORIES_PER_STEP = 0.04;

    private long startTime, endTime;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);

        stepCountView = findViewById(R.id.stepCountView);
        calView = findViewById(R.id.calView);
        startBtn = findViewById(R.id.startBtn);
        finishBtn = findViewById(R.id.finishBtn);
        chronometer = findViewById(R.id.main_chronometer);
        getNameView = findViewById(R.id.get_name);

        // Retrieve and set the title
        String title = getIntent().getStringExtra("TITLE");
        if (title != null) {
            getNameView.setText(title);
        }

        // Activity recognition permission check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        // Connect to the step counter sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // Check if the device has a step counter sensor
        if (stepCountSensor == null) {
            Toast.makeText(this, "No Step Sensor", Toast.LENGTH_SHORT).show();
        }

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChronometer();
                sensorManager.registerListener(PedometerActivity.this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
                startBtn.setEnabled(false);
                finishBtn.setEnabled(true);
                startTime = System.currentTimeMillis();  // 기록 시작 시간
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTime = System.currentTimeMillis();  // 기록 끝 시간
                // Pass the title, step count, calories burned, and times to FinishActivity
                Intent intent = new Intent(PedometerActivity.this, FinishActivity.class);
                intent.putExtra("projectName", getNameView.getText().toString());
                intent.putExtra("elapsedTime", SystemClock.elapsedRealtime() - chronometer.getBase());
                intent.putExtra("stepCount", currentSteps);
                intent.putExtra("caloriesBurned", caloriesBurned);
                intent.putExtra("startTime", startTime);
                intent.putExtra("endTime", endTime);
                startActivity(intent);

                resetChronometer();
                resetSteps(); // Reset steps and calories after passing them to FinishActivity
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (stepCountSensor != null && isRunning) {
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (stepCountSensor != null) {
            sensorManager.unregisterListener(this, stepCountSensor);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0f) {
                currentSteps++;
                caloriesBurned += CALORIES_PER_STEP;
                stepCountView.setText(String.valueOf(currentSteps));
                calView.setText(String.format("%.2f", caloriesBurned));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    private void startChronometer() {
        if (!isRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            isRunning = true;
        }
    }

    private void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        chronometer.stop();
        isRunning = false;
    }

    private void resetSteps() {
        currentSteps = 0;
        caloriesBurned = 0.0;
        stepCountView.setText(String.valueOf(currentSteps));
        calView.setText(String.format("%.2f", caloriesBurned));
    }
}
