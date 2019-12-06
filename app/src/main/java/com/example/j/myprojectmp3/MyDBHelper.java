package com.example.j.myprojectmp3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "groupDB1";
    private static final int VERSION = 1;

    //데이타베이스 생성함
    public MyDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    //테이블 생성함
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE  groupTBL ( gSingerName CHAR(20) PRIMARY KEY, gSingName CHAR(20),gNumber INT(30),gJangR CHAR(20));");

    }

    //테이블을 삭제하고 다시 생성함
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS groupTBL");
        onCreate(db);
    }
}
