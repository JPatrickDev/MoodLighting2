package com.jdp30.moodlighting2.Adapters;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.jdp30.moodlighting2.Fragments.PresetsFragment;
import com.jdp30.moodlighting2.Model.FadePreset;
import com.jdp30.moodlighting2.Model.Group;
import com.jdp30.moodlighting2.Model.Preset;
import com.jdp30.moodlighting2.R;
import com.jdp30.moodlighting2.Util;

import java.util.List;

/**
 * Created by jackp on 30/03/2017.
 */
public class PresetListAdapter extends BaseAdapter {
    private Activity mContext;

    public List<Preset> presets;

    public PresetListAdapter(Activity c) {
        mContext = c;
    }

    public int getCount() {
        return presets.size();
    }

    public Preset getItem(int position) {
        return presets.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Preset p = getItem(position);
        if (p instanceof FadePreset) {
            final FadePreset preset = (FadePreset) p;
            convertView = LayoutInflater.from(mContext).inflate(R.layout.moodlighting_fade_preset_list_item_layout, parent, false);
            ColorGridListAdapter adapter = new ColorGridListAdapter(mContext, 16);
            adapter.colors = preset.getColors();
            ((GridView) convertView.findViewById(R.id.color_grid)).setNumColumns(8);
            ((GridView) convertView.findViewById(R.id.color_grid)).setAdapter(adapter);
            ((TextView) convertView.findViewById(R.id.moodlighting_fade_preset_name)).setText(preset.getName());
            ((TextView) convertView.findViewById(R.id.moodlighting_fade_preset_list_item_pause_time)).setText("Pause: " + preset.getPauseTime() + "s");
            ((TextView) convertView.findViewById(R.id.moodlighting_fade_preset_list_item_fade_time)).setText("Fade: " + preset.getFadeTime() + "s");
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.API_startPreset(preset.getId(),mContext);
                }
            });
        }
        return convertView;

    }

}