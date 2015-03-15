package com.example.cchatel.appli_meteo;

import android.app.ListActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Favoris extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_listview);
        CityDAO dao = new CityDAO(this);
        dao.open();
        ArrayList<City> cities = dao.getAllCities();
        dao.close();
        //ArrayList<String> cityNames = new ArrayList<>();
        ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
        for (City city : cities){
            HashMap<String, String> cityNames = new HashMap<>();
            cityNames.put("name", city.getName());
            cityNames.put("button", "delete");
            listItem.add(cityNames);
        }
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem,
                R.layout.activity_favoris, new String[] {"name", "button"},
                new int[] {R.id.name, R.id.button});
        setListAdapter(mSchedule);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favoris, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
