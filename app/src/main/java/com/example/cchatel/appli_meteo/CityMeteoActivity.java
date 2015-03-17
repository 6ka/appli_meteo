package com.example.cchatel.appli_meteo;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class CityMeteoActivity extends ListActivity {

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
                ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    String iString = Integer.toString(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("date", values.get("date" + iString));
                    map.put("pluie", values.get("pluie" + iString));
                    map.put("temp", values.get("temp" + iString));
                    map.put("vent", values.get("vent" + iString));
                    map.put("name", values.get("name"));
                    map.put("img", values.get("img"+ iString));
                    listItem.add(map);
                }
                SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(), listItem,
                        R.layout.activity_meteo_ville,
                        new String[]{"name", "date", "pluie", "temp", "vent", "img"}, new int[]{R.id.title, R.id.date, R.id.pluie,
                        R.id.temp, R.id.vent, R.id.imageView});
                setListAdapter(mSchedule);
            } else {
                TextView name = (TextView) findViewById(R.id.name);
                if (values.containsKey("name")) {
                    name.setText(values.get("name"));
                } else {
                    name.setText("Your Location");
                }
                City city = new City(values.get("longitude"), values.get("latitude"));
                List<NameValuePair> params = new ArrayList<>();
                new WebServiceRequestor(city, params).execute();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            Intent homeIntent = new Intent(this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
        } else if (id == R.id.fav) {
            Intent favIntent = new Intent(this, FavoriteActivity.class);
            startActivity(favIntent);
        } else if (id == R.id.location) {
            double[] location = getGPS();

            Intent cityIntent = new Intent(this, CityMeteoActivity.class);
            String latitude = String.valueOf(location[0]);
            String longitude = String.valueOf(location[1]);
            cityIntent.putExtra("latitude", latitude);
            cityIntent.putExtra("longitude", longitude);
            cityIntent.putExtra("location", "true");
            startActivity(cityIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private double[] getGPS() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location l = null;

        for (int i = providers.size() - 1; i >= 0; i--) {
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

    private String toCelsius(String kelvin) {
        double kelvinNb = Double.parseDouble(kelvin);
        NumberFormat nf = new DecimalFormat("0");
        String celsius = nf.format(kelvinNb - 273.15);
        return celsius;
    }

    private class WebServiceRequestor extends AsyncTask<String, Void, String> {
        private ProgressDialog pDialog;
        City city;
        List<NameValuePair> parameters;
        String defaultURLBeginning;
        String defaultURLEnd;

        public WebServiceRequestor(City city, List<NameValuePair> params) {
            this.defaultURLBeginning = "http://www.infoclimat.fr/public-api/gfs/json?_ll=";
            this.defaultURLEnd = "&_auth=ABoEEw5wU3ECL1ptB3FVfFE5AjcBdwUiVC" +
                    "hXNFw5Uy4Eb1Q1D28GYFE%2FA35TfFJkU34CYQswUmILYAJ6DH5RMABqBGgOZVM0" +
                    "Am1aPwcoVX5RfwJjASEFIlQwVzlcL1M4BG5ULg9uBmRROQN%2FU2JSZVNiAn0LK1JrC" +
                    "2wCZwxlUTIAYwRiDmRTNQJtWicHKFVkUWYCNwE%2BBTVUYVc3XGJTYwRiVDMPZQ" +
                    "ZjUTwDf1NmUmdTaAJnCzxSagtoAmMMflEtABoEEw5wU3ECL1ptB3FVfFE3AjwBag%3" +
                    "D%3D&_c=e0a28c0708e4309b36a9bfabf9763677";
            this.city = city;
            this.parameters = params;
        }

        @Override
        protected String doInBackground(String... params) {
            String returns = "";
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
                returns = EntityUtils.toString(httpEntity);
            } catch (Exception e) {
            }
            return returns;
        }

        @Override
        protected void onPostExecute(String results) {
            Log.i("POST", "Entre dans le post-execute");
            pDialog.dismiss();

            ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
            HashMap<String, String> map;
            map = new HashMap<>();
            String result = results;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            City currentCity = city;

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
                    map = new HashMap<>();
                    String current_date = dates.get(j);
                    map.put("date", current_date);
                    String pluie = theObject.getJSONObject(current_date + " 15:00:00").getString("pluie");
                    String temp = toCelsius(theObject.getJSONObject(current_date + " 15:00:00").getJSONObject("temperature").getString("sol"));
                    String vent = theObject.getJSONObject(current_date + " 15:00:00").getJSONObject("vent_moyen").getString("10m");
                    map.put("temp", temp + "°C");
                    map.put("pluie", pluie + "mm");
                    map.put("vent", vent + "km/h");
                    if (Double.parseDouble(pluie) > 0.2)
                        map.put("img", Integer.toString(R.drawable.pluie_petit));
                    else if (Double.parseDouble(vent) > 20)
                        map.put("img", Integer.toString(R.drawable.vent_petit));
                    else
                        map.put("img", Integer.toString(R.drawable.soleil_petit));
                    listItem.add(map);
                }

            } catch (Exception e) {
                map.put("temp", "Error during process");
                map.put("vent", currentCity.getLatitude());
                map.put("pluie", currentCity.getLongitude());
                listItem.add(map);
            }

            SimpleAdapter mSchedule = new SimpleAdapter(CityMeteoActivity.this.getBaseContext(), listItem,
                    R.layout.activity_meteo_ville,
                    new String[]{"date", "pluie", "temp", "vent", "img"}, new int[]{R.id.date, R.id.pluie,
                    R.id.temp, R.id.vent, R.id.imageView});
            CityMeteoActivity.this.setListAdapter(mSchedule);
            super.onPostExecute(results);
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(CityMeteoActivity.this);
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
