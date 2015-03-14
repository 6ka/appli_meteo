package com.example.cchatel.appli_meteo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String CITY_ID="id";
    public static final String CITY_NAME = "name";
    public static final String CITY_LONGITUDE = "longitude";
    public static final String CITY_LATITUDE = "latitude";

    public static final String CITY_TABLE_NAME = "city";
    public static final String CITY_TABLE_CREATE =
            "CREATE TABLE " + CITY_TABLE_NAME + " (" +
                    CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CITY_NAME + " TEXT, " +
                    CITY_LONGITUDE + " TEXT, " +
                    CITY_LATITUDE + " TEXT);";

    public static final String METIER_TABLE_DROP = "DROP TABLE IF EXISTS " + CITY_TABLE_NAME + ";";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CITY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(METIER_TABLE_DROP);
        onCreate(db);
    }
}