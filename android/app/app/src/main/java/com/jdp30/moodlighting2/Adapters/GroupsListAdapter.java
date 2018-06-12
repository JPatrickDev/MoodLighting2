package com.jdp30.moodlighting2.Adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
    private Activity mContext;

    public List<Group> groups;

    public GroupsListAdapter(Activity c) {
        mContext = c;
    }

    public int getCount() {
        int i = groups.size();
        for (Group g : groups) {
            i += g.getClients().size();
        }
        return i;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        int i = 0;
        int gP = 0;
        String clientID = null;
        String groupID = null;
        String groupName = null;
        while (gP < groups.size()) {
            Group g = groups.get(gP);
            if (i == position) {
                groupID = g.getGroupID();
                groupName = g.getName();
                break;
            }
            for (String s : g.getClients()) {
                i++;
                if (i == position) {
                    clientID = s;
                    groupID = g.getGroupID();
                    groupName = g.getName();
                    break;
                }
            }
            i++;
            gP++;
        }
        if (clientID != null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.moodlighting_groups_client_name_list_item, parent, false);
            TextView clientName = (TextView) convertView.findViewById(R.id.moodlighting_groups_client_name_list_item_text);
            clientName.setText(clientID);
            final String finalClientID = clientID;
            final String finalGroupID = groupID;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.API_removeClientFromGroup(finalClientID, finalGroupID,mContext);
                }
            });
            return convertView;
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.moodlighting_groups_group_name_list_item, parent, false);
            TextView clientName = (TextView) convertView.findViewById(R.id.moodlighting_groups_group_name_list_item_text);
            clientName.setText(groupName);
            return convertView;
        }
    }

}