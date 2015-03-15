package com.example.cchatel.appli_meteo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;

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
            cityNames.put("button", "delete");
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
            }});
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
