package com.example.cchatel.appli_meteo;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
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
        if (extras != null) {
            Set<String> keys = extras.keySet();
            HashMap<String, String> values = new HashMap<>();
            for (String key : keys) {
                values.put(key, extras.getString(key));
            }
            if (!values.containsKey("location")) {
                TextView name = (TextView) findViewById(R.id.name);
                name.setText(values.get("name"));
                ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < 3; i++) {
                    String iString = Integer.toString(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("date", values.get("date" + iString));
                    map.put("pluie", values.get("pluie" + iString));
                    map.put("temp", values.get("temp" + iString));
                    map.put("vent", values.get("vent" + iString));
                    map.put("name", values.get("name"));
                    listItem.add(map);
                }
                SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(), listItem,
                        R.layout.activity_meteo_ville,
                        new String[]{"name", "date", "pluie", "temp", "vent"}, new int[]{R.id.title, R.id.date, R.id.pluie,
                        R.id.temp, R.id.vent});
                setListAdapter(mSchedule);
            } else {
                TextView name = (TextView) findViewById(R.id.name);
                name.setText("Your Location");
                City city = new City(values.get("longitude"), values.get("latitude"));
                ArrayList<City> cities = new ArrayList<>();
                cities.add(city);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                new WebServiceRequestor(cities, params).execute();
            }
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
        int id = item.getItemId();
        if (id == R.id.home) {
            Log.i("MENU", "Clic sur home");
            Intent homeIntent = new Intent(this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
        } else if (id == R.id.fav) {
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

    private class WebServiceRequestor extends AsyncTask<String, Void, ArrayList<String>> {
        private ProgressDialog pDialog;
        ArrayList<City> cities;
        List<NameValuePair> parameters;
        String defaultURLBeginning;
        String defaultURLEnd;

        public WebServiceRequestor(ArrayList<City> cities, List<NameValuePair> params) {
            this.defaultURLBeginning = "http://www.infoclimat.fr/public-api/gfs/json?_ll=";
            this.defaultURLEnd = "&_auth=ABoEEw5wU3ECL1ptB3FVfFE5AjcBdwUiVC" +
                    "hXNFw5Uy4Eb1Q1D28GYFE%2FA35TfFJkU34CYQswUmILYAJ6DH5RMABqBGgOZVM0" +
                    "Am1aPwcoVX5RfwJjASEFIlQwVzlcL1M4BG5ULg9uBmRROQN%2FU2JSZVNiAn0LK1JrC" +
                    "2wCZwxlUTIAYwRiDmRTNQJtWicHKFVkUWYCNwE%2BBTVUYVc3XGJTYwRiVDMPZQ" +
                    "ZjUTwDf1NmUmdTaAJnCzxSagtoAmMMflEtABoEEw5wU3ECL1ptB3FVfFE3AjwBag%3" +
                    "D%3D&_c=e0a28c0708e4309b36a9bfabf9763677";
            this.cities = cities;
            this.parameters = params;
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> returns = new ArrayList<>();
            for (City city : cities) {
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpEntity httpEntity = null;
                    HttpResponse httpResponse = null;
                    String URL = this.defaultURLBeginning + city.getLatitude() + "," + city.getLongitude() + this.defaultURLEnd;
                    HttpPost httpPost = new HttpPost(URL);
                    if (parameters != null) {
                        httpPost.setEntity(new UrlEncodedFormEntity(parameters));
                    }
                    httpResponse = httpClient.execute(httpPost);
                    httpEntity = httpResponse.getEntity();
                    returns.add(EntityUtils.toString(httpEntity));
                    //return EntityUtils.toString(httpEntity);
                } catch (Exception e) {
                }
                Log.i("BACKGROUND", Integer.toString(cities.size()));
                Log.i("BACKGROUND", Integer.toString(returns.size()));
            }
            return returns;
        }

        @Override
        protected void onPostExecute(ArrayList<String> results) {
            Log.i("POST", "Entre dans le post-execute");
            pDialog.dismiss();

            ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;
            for (int i = 0; i < cities.size(); i++) {
                map = new HashMap<String, String>();
                String result = results.get(i);
                //TextView txt = (TextView) findViewById(R.id.txt);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                City currentCity = cities.get(i);
                map.put("name", currentCity.getName());
                map.put("longitude", currentCity.getLongitude());
                map.put("latitude", currentCity.getLatitude());
                Log.i("POSTEXECUTE", currentCity.getLongitude());

                String date_today = sdf.format(new Date());
                Calendar c = Calendar.getInstance();
                Date dt = new Date();
                c.setTime(dt);
                c.add(Calendar.DATE, 1);
                dt = c.getTime();
                String date_tomorrow = sdf.format(dt);
                c.setTime(dt);
                c.add(Calendar.DATE, 1);
                dt = c.getTime();
                String date_2_days_later = sdf.format(dt);
                ArrayList<String> dates = new ArrayList<String>();
                dates.add(date_today);
                dates.add(date_tomorrow);
                dates.add(date_2_days_later);

                try {
                    JSONObject theObject = new JSONObject(result);
                    for (int j = 0; j < dates.size(); j++) {
                        String current_date = dates.get(j);
                        map.put("date", dates.get(j));
                        map.put("temp", theObject.getJSONObject(current_date + " 15:00:00").getJSONObject("temperature").getString("sol"));
                        map.put("pluie", theObject.getJSONObject(current_date + " 15:00:00").getString("pluie"));
                        map.put("vent", theObject.getJSONObject(current_date + " 15:00:00").getJSONObject("vent_moyen").getString("10m"));
                        listItem.add(map);
                    }

                } catch (Exception e) {
                    map.put("temp", "Error during process");
                }
            }
            SimpleAdapter mSchedule = new SimpleAdapter(MeteoVille.this.getBaseContext(), listItem,
                    R.layout.activity_meteo_ville,
                    new String[]{"date", "pluie", "temp", "vent"}, new int[]{R.id.date, R.id.pluie,
                    R.id.temp, R.id.vent});
            MeteoVille.this.setListAdapter(mSchedule);
            super.onPostExecute(results);
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MeteoVille.this);
            pDialog.setMessage("Processing Request...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
