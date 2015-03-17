package com.example.cchatel.appli_meteo;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        CityDAO dao = new CityDAO(this);
        dao.open();
        ArrayList<City> cities = dao.getAllCities();
        dao.close();
        new WebServiceRequestor(cities, params).execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.i("MENU", "Création menu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        HashMap<String, String> map = (HashMap<String, String>) getListAdapter().getItem(position);
        Intent monIntent = new Intent( MainActivity.this, MeteoVille.class );
        for(String currentKey : map.keySet()) {
            monIntent.putExtra(currentKey, map.get(currentKey));
        }
        startActivity(monIntent);
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

    private String toCelsius(String kelvin){
        double kelvinNb = Double.parseDouble(kelvin);
        NumberFormat nf = new DecimalFormat("0");
        String celsius = nf.format(kelvinNb - 273.15);
        return celsius;
    }

    private class WebServiceRequestor extends AsyncTask<String, Void, ArrayList<String>> {
        private ProgressDialog pDialog;
        ArrayList<City> cities;
        List<NameValuePair> parameters;
        String defaultURLBeginning;
        String defaultURLEnd;
        public WebServiceRequestor(ArrayList<City> cities, List<NameValuePair> params)
        {
            this.defaultURLBeginning = "http://www.infoclimat.fr/public-api/gfs/json?_ll=";
            this.defaultURLEnd = "&_auth=ABoEEw5wU3ECL1ptB3FVfFE5AjcBdwUiVC"+
            "hXNFw5Uy4Eb1Q1D28GYFE%2FA35TfFJkU34CYQswUmILYAJ6DH5RMABqBGgOZVM0"+
            "Am1aPwcoVX5RfwJjASEFIlQwVzlcL1M4BG5ULg9uBmRROQN%2FU2JSZVNiAn0LK1JrC"+
            "2wCZwxlUTIAYwRiDmRTNQJtWicHKFVkUWYCNwE%2BBTVUYVc3XGJTYwRiVDMPZQ"+
            "ZjUTwDf1NmUmdTaAJnCzxSagtoAmMMflEtABoEEw5wU3ECL1ptB3FVfFE3AjwBag%3"+
            "D%3D&_c=e0a28c0708e4309b36a9bfabf9763677";
            this.cities = cities;
            this.parameters = params;
        }

        @Override
        protected ArrayList<String> doInBackground(String... params)
        {
            if (cities.size() == 0) {
                setContentView(R.layout.activity_no_fav);
                Button buttonAdd = (Button) findViewById(R.id.button3);
                buttonAdd.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent monIntent = new Intent(MainActivity.this, SearchCity.class);
                        startActivity(monIntent);
                    }
                });
            }
            else {
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
            return new ArrayList<String>();
        }
        @Override
        protected void onPostExecute(ArrayList<String> results)
        {
            Log.i("POST","Entre dans le post-execute");
            pDialog.dismiss();

            ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;
            for (int i = 0; i < cities.size(); i++) {
                map = new HashMap<String, String>();
                String result = results.get(i);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                City currentCity = cities.get(i);
                map.put("name", currentCity.getName());
                map.put("longitude", currentCity.getLongitude());
                map.put("latitude", currentCity.getLatitude());

                String date_today = sdf.format(new Date());
                //Calendar c = Calendar.getInstance();
                GregorianCalendar c = new java.util.GregorianCalendar();
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
                        map.put("date"+Integer.toString(j), dates.get(j));
                        map.put("temp"+Integer.toString(j), toCelsius(theObject.getJSONObject(current_date + " 15:00:00").getJSONObject("temperature").getString("sol")) + "°C");
                        map.put("pluie"+Integer.toString(j), theObject.getJSONObject(current_date + " 15:00:00").getString("pluie") + "mm");
                        map.put("vent"+Integer.toString(j), theObject.getJSONObject(current_date + " 15:00:00").getJSONObject("vent_moyen").getString("10m") + "km/h");
                    }

                } catch (Exception e) {
                    map.put("temp", "Error during process");
                }
                listItem.add(map);
            }
            SimpleAdapter mSchedule = new SimpleAdapter (MainActivity.this.getBaseContext(), listItem,
                    R.layout.activity_main,
                    new String[] {"name", "temp0"}, new int[] {R.id.name, R.id.txt});
            MainActivity.this.setListAdapter(mSchedule);
            super.onPostExecute(results);
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
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
