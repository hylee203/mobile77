package com.example.compo;

import static java.sql.DriverManager.println;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class  DatabaseHelper extends SQLiteOpenHelper {

    public static String NAME = "component";
    public static int VERSION = 1;

    // 생성자에서 데이터베이스 생성
    public DatabaseHelper(Context context)
    {
        super(context, NAME, null, VERSION);
    }

    // 데이터베이스 생성 시 테이블 생성
    public void onCreate(SQLiteDatabase database) {
        println("DatabaseHelper.onCreate() called");

        database.execSQL("create table if not exists component_type(" +
                "compo_name TEXT PRIMARY KEY, compo_description TEXT, compo_icon_xml_dir TEXT)");
        // measured가 0이면 timer, 1이면 stopwatch
        database.execSQL("create table if not exists component_favorites(favor_id integer PRIMARY KEY autoincrement, " +
                "compo_name TEXT REFERENCES component_type(compo_name), measured integer, compo_color_dir TEXT, title TEXT)");
        database.execSQL("create table if not exists component_history(hist_id integer PRIMARY KEY autoincrement, " +
                "compo_id integer REFERENCES component_favorites(favor_id), start_time TEXT, end_time TEXT, memo TEXT)");
        database.execSQL("create table if not exists user_info(user_id integer PRIMARY KEY autoincrement, " +
                "user_name TEXT, age integer, gender_male integer)"); // 성별 남자면 0, 여자면 1

        database.execSQL("insert into component_type(compo_name, compo_description, compo_icon_xml_dir) values ('공부', '공부 시간 측정', 'study')");
        database.execSQL("insert into component_type(compo_name, compo_description, compo_icon_xml_dir) values ('운동', '운동 시간 측정', 'exercise')");
        database.execSQL("insert into component_type(compo_name, compo_description, compo_icon_xml_dir) values ('게임', '게임 시간 측정', 'game')");
        database.execSQL("insert into component_type(compo_name, compo_description, compo_icon_xml_dir) values ('독서', '독서 시간 측정', 'read_book')");

    }

    // 데이터베이스 열 때
    public void onOpen(SQLiteDatabase database)
    {
        println("DatabaseHelper.onOpen() called");
    }

    // 데이터베이스의 버전이 다름을 감지할 때
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        println("DatabaseHelper.onUpgrade() called");
    }
}
