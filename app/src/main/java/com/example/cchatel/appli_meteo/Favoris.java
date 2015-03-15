package com.example.cchatel.appli_meteo;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.HashMap;


public class Favoris extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);
        CityDAO dao = new CityDAO(this);
        dao.open();
        ArrayList<City> cities = dao.getAllCities();
        dao.close();
        //ArrayList<String> cityNames = new ArrayList<>();
        ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
        for (City city : cities){
            HashMap<String, String> cityNames = new HashMap<>();
            cityNames.put("name", city.getName());
            cityNames.put("button", "Delete");
            listItem.add(cityNames);
        }
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem,
                R.layout.activity_favoris_sublist, new String[] {"name", "button"},
                new int[] {R.id.name, R.id.button});
        setListAdapter(mSchedule);

        Button buttonAdd = (Button) findViewById(R.id.button2);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent monIntent = new Intent(Favoris.this, SearchCity.class);
                startActivity(monIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                Log.i("MENU", "Clic sur home");
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(homeIntent);
            case R.id.fav:
                Log.i("MENU", "Clic sur favoris");
                Intent favIntent = new Intent(this, Favoris.class);
                startActivity(favIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
