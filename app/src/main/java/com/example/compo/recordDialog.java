package com.example.compo;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

public class recordDialog extends Dialog {
    private TextView txt_contents;
    private TextView txt_record_name;
    private Button shutdownClick;
    SQLiteDatabase database;
    DatabaseHelper databaseHelper;

    public recordDialog(@NotNull Context context, int history_id) {
        super(context);
        setContentView(R.layout.record_dialog);

        databaseHelper = new DatabaseHelper(getContext());
        database = databaseHelper.getWritableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT component_history.hist_id, component_favorites.favor_id, component_favorites.compo_name, component_favorites.measured, " +
                "component_favorites.compo_color_dir, component_favorites.title, component_history.start_time, component_history.end_time, " +
                "component_history.memo " +
                "FROM component_history " +
                "JOIN component_favorites ON component_history.compo_id = component_favorites.favor_id WHERE hist_id = " + Integer.toString(history_id), null);

        cursor.moveToNext();
        String compo = cursor.getString(2); // 컴포 이름 ex) 공부
        String title = cursor.getString(5); // 즐겨찾기 제목 ex) 사물인터넷 공부
        String stringStartTime = cursor.getString(6);
        String stringEndTime = cursor.getString(7);
        String memo = cursor.getString(8);

        txt_record_name = findViewById(R.id.recordName);
        txt_record_name.setText(title);

        txt_contents = findViewById(R.id.txt_contents);
        txt_contents.setText(memo);
        shutdownClick = findViewById(R.id.btn_shutdown);
        shutdownClick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}