package com.example.cchatel.appli_meteo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchCity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);

        Button btn1 = (Button)findViewById(R.id.button1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editText);
                Editable cityTxt = editText.getText();
                String cityString = cityTxt.toString();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                new WebServiceRequestor("https://maps.googleapis.com/maps/api/geocode/json?address="+cityString, params).execute();
            }
        });

        Button btnFav = (Button) findViewById(R.id.buttonFav);
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SearchCity.this, Favoris.class);
                startActivity(myIntent);
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
            Log.i("ONPOST", "PostExecute");
            pDialog.dismiss();
            try {
                Log.i("ONPOST", "Avant récupération !");
                JSONObject theObject = new JSONObject(result);
                Log.i("ONPOST", "JSON récupéré !");

                JSONObject resultObject = theObject.getJSONArray("results").getJSONObject(0);
                String name = resultObject.getJSONArray("address_components").getJSONObject(0).getString("long_name");
                String longitude = resultObject.getJSONObject("geometry").getJSONObject("location").getString("lng");
                String latitude = resultObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
                Log.i("LONGITUDE", longitude);
                Log.i("NOM", name);
                City city = new City(name, longitude, latitude);
                CityDAO dao = new CityDAO(SearchCity.this);
                dao.open();
                dao.add(city);
                dao.close();
            } catch (Exception e){
                Log.i("ONPOST", e.getMessage());
            }
            super.onPostExecute(result);
            Log.i("", "FinPostExecute");
        }

        @Override
        protected void onPreExecute() {
            Log.d("PONEY", "PreExecute");
            pDialog = new ProgressDialog(SearchCity.this);
            pDialog.setMessage("Processing Request...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
            Log.i("", "FinPreExecute");
        }
        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
