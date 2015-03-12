package com.example.cchatel.appli_meteo;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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


public class MeteoVille extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo_ville);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meteo_ville, menu);
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


    private class WebServiceRequestor extends AsyncTask<String, Void, String> {
        private ProgressDialog pDialog;
        String URL;
        List<NameValuePair> parameters;
        public WebServiceRequestor(String url, List<NameValuePair> params)
        {
            this.URL = url;
            this.parameters = params;
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpEntity httpEntity = null;
                HttpResponse httpResponse = null;
                HttpPost httpPost = new HttpPost(URL);
                if (parameters != null)
                {
                    httpPost.setEntity(new UrlEncodedFormEntity(parameters));
                }
                httpResponse = httpClient.execute(httpPost);
                httpEntity = httpResponse.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (Exception e)
            {
            }
            return "";
        }
        @Override
        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
                for (int i = 0; i < dates.size(); i++) {
                    String current_date = dates.get(i);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("temperature", theObject.getJSONObject(current_date + " 12:00:00").getJSONObject("temperature").getString("sol"));
                    map.put("pluie", theObject.getJSONObject(current_date + " 12:00:00").getString("pluie"));
                    map.put("vent", theObject.getJSONObject(current_date + " 12:00:00").getJSONObject("vent_moyen").getString("10m"));
                    listItem.add(map);
                    SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem,
                            R.layout.activity_meteo_ville,
                            new String[] {"temperature", "pluie", "vent"}, new int[] {R.id.temp, R.id.pluie,
                            R.id.vent});
                    setListAdapter(mSchedule);

                }
            } catch (Exception e){
                list.setText("Error during process");
            }
            super.onPostExecute(result);
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
