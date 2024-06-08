package com.example.compo;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AddComponentActivity extends AppCompatActivity {


    String title = null;
    String type = null;
    String color = null;
    int measured = -1;
    Button acceptButton;
    Button colorSelectButton;
    Spinner typeSelectSpinner;
    Spinner colorSelectSpinner;
    RadioGroup radioGroup;
    RadioButton timerButton;
    RadioButton stopwatchButton;
    DatabaseHelper databaseHelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_component);


        // ID 할당
        acceptButton = findViewById(R.id.accept_button);

        typeSelectSpinner = (Spinner) findViewById(R.id.component_select_spinner);
        colorSelectSpinner = (Spinner) findViewById(R.id.color_select_spinner);
        radioGroup = findViewById(R.id.type_select_button);
        timerButton = findViewById(R.id.timer);
        stopwatchButton = findViewById(R.id.stopwatch);

        // 컴포넌트 타입 선택 (데이터베이스에서 불러오기)
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select compo_name from component_type", null);
        String[] component_list = new String[cursor.getCount() + 1];
        component_list[0] = "선택하세요...";
        for(int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            component_list[i + 1] = cursor.getString(0);
        }

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, component_list);
        typeSelectSpinner.setAdapter(typeAdapter);

        typeSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id)
            {
                type = component_list[position];
            }
            @Override
            public void onNothingSelected(AdapterView adapterView)
            {
            }
        });

        // 컴포넌트 컬러 선택
        // 데이터베이스 기능으로 리팩토링 필요
        String[] color_list = {"beige", "pink", "blue", "green", "purple", "mint"};
        String[] color_code_list = {"#FFF4CA", "#FFDDCA", "#CAE2FF", "#BBF2D4", "#DECAFF", "#CAFCFF"};
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, color_list);
        colorSelectSpinner.setAdapter(colorAdapter);
        colorSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id)
            {
                color = color_code_list[position];
            }
            @Override
            public void onNothingSelected(AdapterView adapterView)
            {
            }
        });



        // 확인 버튼 클릭 
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // 제목을 변수에 저장
                EditText editText = findViewById(R.id.edit_text);
                title = editText.getText().toString();

                // 측정 타입을 변수에 저장
                int radioId = radioGroup.getCheckedRadioButtonId();
                if(timerButton.getId() == radioId)
                {
                    measured = 0;
                }
                else
                {
                    measured = 1;
                }

                // 입력한 제목, 타입, 컬러를 즐겨찾기 테이블에 저장 (확인 필요)
                if(!title.equals("") && !type.equals("선택하세요...") && measured != -1)
                {
                    databaseHelper = new DatabaseHelper(getApplicationContext());
                    database = databaseHelper.getWritableDatabase();
                    database.execSQL("insert into component_favorites(compo_name, measured, compo_color_dir, title) values ('" + type + "'," +
                            " " + measured + ", '" + color + "', '" + title + "')");
                }

                // MainActivity로 돌아가기
                Intent intent = new Intent();
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}