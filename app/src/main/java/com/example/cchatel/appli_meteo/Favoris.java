package com.example.cchatel.appli_meteo;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Favoris extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);
        final ListView myListView = (ListView) findViewById(R.id.list);
        CityDAO dao = new CityDAO(this);
        dao.open();
        ArrayList<City> cities = dao.getAllCities();
        dao.close();
        //ArrayList<String> cityNames = new ArrayList<>();
        final FavorisAdapter adapter = new FavorisAdapter(this, cities, this);
        myListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ListView listView1 = (ListView)findViewById(R.id.list);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = adapter.getItem(position);
                Intent intentMeteo = new Intent(Favoris.this, MeteoVille.class);
                intentMeteo.putExtra("latitude", city.getLatitude());
                intentMeteo.putExtra("longitude", city.getLongitude());
                intentMeteo.putExtra("location", true);
                intentMeteo.putExtra("name", city.getName());
                startActivity(intentMeteo);
            }
        });

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
        int id = item.getItemId();
        if (id == R.id.home) {
            Log.i("MENU", "Clic sur home");
            Intent homeIntent = new Intent(this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
        }
        else if (id == R.id.fav) {
            Log.i("MENU", "Clic sur favoris");
            Intent favIntent = new Intent(this, Favoris.class);
            startActivity(favIntent);
        }
        else if (id == R.id.location){
            Log.i("MENU", "Clic sur location");
            double[] location = getGPS();

            Intent cityIntent = new Intent(this, MeteoVille.class);
            String latitude = String.valueOf(location[0]);
            String longitude = String.valueOf(location[1]);
            cityIntent.putExtra("latitude",latitude);
            cityIntent.putExtra("longitude",longitude);
            cityIntent.putExtra("location", "true");
            Log.i("LOCATION", latitude);
            Log.i("LOCATION", longitude);
            startActivity(cityIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private double[] getGPS() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

/* Loop over the array backwards, and if you get an accurate location, then break                 out the loop*/
        Location l = null;

        for (int i=providers.size()-1; i>=0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        double[] gps = new double[2];
        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        return gps;
    }
}
