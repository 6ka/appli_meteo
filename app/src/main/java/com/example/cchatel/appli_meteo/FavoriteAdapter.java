package com.example.cchatel.appli_meteo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class FavoriteAdapter extends BaseAdapter {
    private ArrayList<City> cities;
    private ListView mListView;
    private Context context;
    LayoutInflater inflater;

    private class ViewHolder {
        TextView name;
        Button delete;
        int indexCity;
    }

    public FavoriteAdapter(Context context, ArrayList<City> objects, Activity activity) {
        inflater = LayoutInflater.from(context);
        this.cities = objects;
        this.mListView = (ListView) activity.findViewById(R.id.list);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_favoris_sublist, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.delete = (Button) convertView.findViewById(R.id.button);
            convertView.setTag(holder);
        } else {
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
                new AlertDialog.Builder(context)
                        .setTitle("Delete city")
                        .setMessage("Are you sure you want to delete this city?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                CityDAO dao = new CityDAO(context);
                                dao.open();
                                dao.delete(holder.indexCity);
                                dao.close();
                                Intent intent = new Intent(context, FavoriteActivity.class);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        return convertView;
    }

    /**
     * Retourne le nombred'éléments
     */
    @Override
    public int getCount() {
        return cities.size();
    }

    /**
     * Retournel'item à laposition
     */
    @Override
    public City getItem(int position) {
        return cities.get(position);
    }

    /**
     * Retourne laposition del'item
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
}