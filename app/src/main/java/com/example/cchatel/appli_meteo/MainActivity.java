package com.example.cchatel.appli_meteo;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        HashMap<String, String> map = (HashMap<String, String>) getListAdapter().getItem(position);
        Intent monIntent = new Intent( MainActivity.this, MeteoVille.class );
        for(String currentKey : map.keySet()) {
            monIntent.putExtra(currentKey, map.get(currentKey));
        }
        startActivity(monIntent);
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
            if (cities.size() == 0){
                setContentView(R.layout.activity_no_fav);
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
                //TextView txt = (TextView) findViewById(R.id.txt);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                City currentCity = cities.get(i);
                map.put("name", currentCity.getName());
                map.put("longitude", currentCity.getLongitude());
                map.put("latitude", currentCity.getLatitude());

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
                        map.put("date"+Integer.toString(j), dates.get(j));
                        map.put("temp"+Integer.toString(j), theObject.getJSONObject(current_date + " 15:00:00").getJSONObject("temperature").getString("sol"));
                        map.put("pluie"+Integer.toString(j), theObject.getJSONObject(current_date + " 15:00:00").getString("pluie"));
                        map.put("vent"+Integer.toString(j), theObject.getJSONObject(current_date + " 15:00:00").getJSONObject("vent_moyen").getString("10m"));
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
