package com.example.cchatel.appli_meteo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FavorisAdapter extends BaseAdapter {
    private ArrayList<City> cities;
    private ListView mListView;
    private Context context;
    LayoutInflater inflater;

    /**
     * Elle nous serviraà mémoriser les éléments de la liste en mémoirepour
     * qu’à chaque rafraichissementl’écran ne scintillepas
     *
     * @author patrice
     */
    private class ViewHolder {
        TextView name;
        Button delete;
        int indexCity;
    }

    public FavorisAdapter(Context context, ArrayList<City> objects, Activity activity) {
        inflater = LayoutInflater.from(context);
        this.cities = objects;
        this.mListView = (ListView) activity.findViewById(R.id.list);
        this.context = context;
    }

    /**
     * Génère la vuepour un objet
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            Log.v("test", "convertView is null");
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_favoris_sublist, null);
            holder.name = (TextView) convertView.findViewById(R.id.date);
            holder.delete = (Button) convertView.findViewById(R.id.button);
            convertView.setTag(holder);
        } else {
            Log.v("test", "convertView is not null");
            holder = (ViewHolder) convertView.getTag();
        }
        City city = cities.get(position);
        holder.name.setText(city.getName());
        holder.delete.setText("Delete");
        holder.indexCity = city.getId();

        Button buttonDelete = (Button) convertView.findViewById(R.id.button);
        buttonDelete.setFocusable(false);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CityDAO dao = new CityDAO(context);
                dao.open();
                dao.delete(holder.indexCity);
                dao.close();
                Intent intent = new Intent(context, Favoris.class);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    /**
     * Retourne le nombred'éléments
     */
    @Override
    public int getCount() {
// TODOAuto-generated method stub
        return cities.size();
    }

    /**
     * Retournel'item à laposition
     */
    @Override
    public City getItem(int position) {
// TODOAuto-generated method stub
        return cities.get(position);
    }

    /**
     * Retourne laposition del'item
     */
    @Override
    public long getItemId(int position) {
// TODOAuto-generated method stub
        return position;
    }
}