package com.jdp30.moodlighting2.Adapters;


import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jdp30.moodlighting2.Model.Group;
import com.jdp30.moodlighting2.R;
import com.jdp30.moodlighting2.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackp on 30/03/2017.
 */
public class GroupsListAdapter extends BaseAdapter {
    private Activity activity;

    public List<Group> groups;

    public GroupsListAdapter(Activity c) {
        activity = c;
    }

    public int getCount() {
        return groups.size();
    }

    public Group getItem(int position) {
        return groups.get(position);
    }

    public long getItemId(int position) {
        return groups.get(position).getGroupID().hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(activity).inflate(R.layout.moodlighting_groups_grid_item, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.moodlighting_groups_grid_item_name);
        name.setText(getItem(position).getName());
        ListView v = (ListView) convertView.findViewById(R.id.moodlighting_groups_grid_item_list);
        v.setAdapter(new ClientListAdapter(activity,getItem(position).getClients()));
        return convertView;

    }

}