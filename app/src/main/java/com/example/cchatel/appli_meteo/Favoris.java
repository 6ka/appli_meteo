package com.example.cchatel.appli_meteo;

import android.app.ListActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Favoris extends ListActivity {


    String[] FAVORIS = getFavoris();

    static public String[] getFavoris(){
        ArrayList<String> favoris = new ArrayList<String>();
        try {
            //FileReader fileReader = new FileReader("C:/Users/chate_000/Documents/Centrale/OMIS/Android/appli_meteo/app/src/main/java/com/example/cchatel/appli_meteo/favoris.txt");
            FileReader fileReader = new FileReader("favoris.txt");
            Log.i("FAVORIS","A récupéré le fichier");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                favoris.add(line);
            }
            bufferedReader.close();
            Log.i("FAVORIS", "A pu récupérer lire les lignes");
        }
        catch(Exception e){
            Log.i("FAVORIS", "Erreur");
        }
        return favoris.toArray(new String[favoris.size()]);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView myListView = getListView();
        ArrayAdapter<String> aa = new ArrayAdapter < String > (this,
        R.layout.activity_favoris, FAVORIS);
        myListView.setAdapter(aa);
        myListView.setTextFilterEnabled(true);
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
