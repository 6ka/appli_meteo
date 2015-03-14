package com.example.cchatel.appli_meteo;

import android.content.ContentValues;
import android.database.Cursor;

public class CityDAO extends DAOBase{
        public static final String TABLE_NAME = "city";
        public static final String KEY = "id";
        public static final String NAME = "name";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";

        public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, " + LONGITUDE + " REAL" +LATITUDE + " REAL" +");";

        public static final String TABLE_DROP =  "DROP TABLE IF EXISTS " + TABLE_NAME + ";";


        /**
         * @param m le métier à ajouter à la base
         */
        public void add(City c) {
            ContentValues value = new ContentValues();
            value.put(CityDAO.NAME, c.getName());
            value.put(CityDAO.LONGITUDE, c.getLongitude());
            value.put(CityDAO.LATITUDE, c.getLatitude());
            mDb.insert(CityDAO.TABLE_NAME, null, value);
        }

        /**
         * @param id l'identifiant du métier à supprimer
         */
        public void delete(long id) {
            mDb.delete(TABLE_NAME, KEY + " = ?", new String[] {String.valueOf(id)});
        }

        /**
         * @param id l'identifiant du métier à récupérer
         */
        public City select(long id) {
            Cursor c = mDb.rawQuery("select * from " + TABLE_NAME + " id =?", new String[]{KEY, NAME, LONGITUDE, LATITUDE});
            c.moveToFirst();
            City city = cursorToCity(c);
            c.close();

            return city;
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
