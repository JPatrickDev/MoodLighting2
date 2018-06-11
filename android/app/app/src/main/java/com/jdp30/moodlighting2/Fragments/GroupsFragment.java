package com.jdp30.moodlighting2.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jdp30.moodlighting2.Adapters.GroupsListAdapter;
import com.jdp30.moodlighting2.Model.Group;
import com.jdp30.moodlighting2.R;
import com.jdp30.moodlighting2.Util;
import com.jdp30.moodlighting2.Views.ColorSelectorView;

import java.util.List;

/**
 * Created by jackp on 11/06/2018.
 */

public class GroupsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.moodlighting_groups_fragment, container, false);
        Util.GroupsCallback c = new Util.GroupsCallback() {
            @Override
            public void callback(List<Group> groups) {
                GroupsListAdapter adapter = new GroupsListAdapter(getActivity());
                adapter.groups = groups;
                adapter.notifyDataSetChanged();
                ((ListView)v.findViewById(R.id.moodlighting_groups_fragment_listview)).setAdapter(adapter);

            }
        };
        Util.getGroups(c,this.getActivity());
        return v;
    }

}