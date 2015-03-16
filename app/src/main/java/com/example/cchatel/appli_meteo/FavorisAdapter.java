package com.example.cchatel.appli_meteo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class FavorisAdapter extends BaseAdapter {
    private ArrayList<City> cities;
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
    }

    public FavorisAdapter(Context context, ArrayList<City> objects) {
        inflater = LayoutInflater.from(context);
        this.cities = objects;
    }

    /**
     * Génère la vuepour un objet
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            Log.v("test", "convertView is null");
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_favoris_sublist, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.delete = (Button) convertView.findViewById(R.id.button);
            convertView.setTag(holder);
        } else {
            Log.v("test", "convertView is not null");
            holder = (ViewHolder) convertView.getTag();
        }
        City city = cities.get(position);
        holder.name.setText(city.getName());
        holder.delete.setText("Delete");
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