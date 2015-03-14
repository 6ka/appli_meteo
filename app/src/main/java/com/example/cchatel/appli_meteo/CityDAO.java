package com.example.cchatel.appli_meteo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CityDAO{
        public static final String TABLE_NAME = "city";
        public static final String KEY = "id";
        public static final String NAME = "name";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        protected final static int VERSION = 1;
        // Le nom du fichier qui représente ma base
        protected final static String NOM = "city2.db";

        protected SQLiteDatabase mDb = null;
        protected DatabaseHandler mHandler = null;

        public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, " + LONGITUDE + " TEXT, " +LATITUDE + " TEXT);";

        public static final String TABLE_DROP =  "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public CityDAO(Context pContext) {
            this.mHandler = new DatabaseHandler(pContext, NOM, null, VERSION);
        }

        public SQLiteDatabase open() {
            // Pas besoin de fermer la dernière base puisque getWritableDatabase s'en charge
            mDb = mHandler.getWritableDatabase();
            return mDb;
        }

        public void close() {
            mDb.close();
        }

        public SQLiteDatabase getDb() {
            return mDb;
        }

        public void add(City c) {
            ContentValues value = new ContentValues();
            value.put(CityDAO.NAME, c.getName());
            value.put(CityDAO.LONGITUDE, c.getLongitude());
            value.put(CityDAO.LATITUDE, c.getLatitude());
            mDb.insert(CityDAO.TABLE_NAME, null, value);
        }

        public void delete(long id) {
            mDb.delete(TABLE_NAME, KEY + " = ?", new String[] {String.valueOf(id)});
        }

        public City select(long id) {
            Cursor c = mDb.rawQuery("select * from " + TABLE_NAME + " id =?", new String[]{KEY, NAME, LONGITUDE, LATITUDE});
            c.moveToFirst();
            City city = cursorToCity(c);
            c.close();

            return city;
        }

        public ArrayList<City> getAllCities(){
            ArrayList<City> cities = new ArrayList<>();
            Cursor c = mDb.rawQuery("select * from " + TABLE_NAME, null);
            c.moveToFirst();
            while(!c.isAfterLast()){
                City city = cursorToCity(c);
                cities.add(city);
                c.moveToNext();

            }
            c.close();
            return cities;
        }

        public City cursorToCity(Cursor c){
            City city = new City();

            city.setId(c.getInt(0));
            city.setName(c.getString(1));
            city.setLongitude(c.getString(2));
            city.setLatitude(c.getString(3));

            return city;
        }
}
