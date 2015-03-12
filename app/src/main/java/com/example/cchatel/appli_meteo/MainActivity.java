package com.example.cchatel.appli_meteo;

import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://www.infoclimat.fr/public-api/gfs/json?_ll=48.85341,2.3488&_auth=ABoEEw5wU3ECL1ptB3FVfFE5AjcBdwUiVChXNFw5Uy4Eb1Q1D28GYFE%2FA35TfFJkU34CYQswUmILYAJ6DH5RMABqBGgOZVM0Am1aPwcoVX5RfwJjASEFIlQwVzlcL1M4BG5ULg9uBmRROQN%2FU2JSZVNiAn0LK1JrC2wCZwxlUTIAYwRiDmRTNQJtWicHKFVkUWYCNwE%2BBTVUYVc3XGJTYwRiVDMPZQZjUTwDf1NmUmdTaAJnCzxSagtoAmMMflEtABoEEw5wU3ECL1ptB3FVfFE3AjwBag%3D%3D&_c=e0a28c0708e4309b36a9bfabf9763677");
        try {
            HttpResponse response = httpclient.execute(httpget);
            if(response != null) {
                String line = "";
                HttpEntity httpEntity = response.getEntity();
                line = EntityUtils.toString(httpEntity);
                Log.i("Main", "lines " + line);
                JSONObject theObject = new JSONObject(line);
                TextView mTxtDisplay = (TextView) findViewById(R.id.txt);
                mTxtDisplay.setText("Response is: "+ theObject.getJSONObject("2015-03-12 09:00:00").getJSONObject("temperature").getString("sol"));
            } else {
                Toast.makeText(this, "Unable to complete your request", Toast.LENGTH_LONG).show();
            }
        } catch (ClientProtocolException e) {
            Toast.makeText(this, "Caught ClientProtocolException", Toast.LENGTH_SHORT).show();
            Log.e("test",e.toString());
        } catch (IOException e) {
            Toast.makeText(this, "Caught IOException", Toast.LENGTH_SHORT).show();
            Log.e("test",e.toString());
        } catch (Exception e) {
            Toast.makeText(this, "Caught Exception", Toast.LENGTH_SHORT).show();
            Log.e("test",e.toString());
        }
            }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
