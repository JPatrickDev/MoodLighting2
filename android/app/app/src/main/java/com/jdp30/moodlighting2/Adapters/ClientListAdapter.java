package com.jdp30.moodlighting2.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jdp30.moodlighting2.Model.Group;
import com.jdp30.moodlighting2.R;

import java.util.ArrayList;
import java.util.List;

public class ClientListAdapter extends BaseAdapter {
    private Activity activity;

    public List<String> clients;


    public ClientListAdapter(Activity activity, ArrayList<String> clients) {
        this.activity = activity;
        this.clients = clients;
    }

    public int getCount() {
        return clients.size();
    }

    public String getItem(int position) {
        return clients.get(position);
    }

    public long getItemId(int position) {
        return clients.get(position).hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(activity).inflate(R.layout.moodlighting_groups_client_name_list_item, parent, false);
        TextView v = ((TextView) convertView.findViewById(R.id.moodlighting_groups_client_name_list_item_text));
        v.setText(clients.get(position));
        return convertView;

    }

}