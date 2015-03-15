package com.example.cchatel.appli_meteo;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class MeteoVille extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo_ville_listview);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Set<String> keys = extras.keySet();
            HashMap<String, String> values = new HashMap<>();
            for (String key: keys){
                values.put(key, extras.getString(key));
            }
            TextView name = (TextView) findViewById(R.id.name);
            name.setText(values.get("name"));
            ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i<3; i++){
                String iString = Integer.toString(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("date", values.get("date"+ iString));
                map.put("pluie", values.get("pluie"+ iString));
                map.put("temp", values.get("temp"+ iString));
                map.put("vent", values.get("vent"+ iString));
                map.put("name", values.get("name"));
                listItem.add(map);
            }
            SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem,
                    R.layout.activity_meteo_ville,
                    new String[] {"name", "date", "pluie", "temp", "vent"}, new int[] {R.id.title, R.id.date, R.id.pluie,
                    R.id.temp, R.id.vent});
            setListAdapter(mSchedule);
        }
        List<NameValuePair> params = new ArrayList<NameValuePair>();
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
