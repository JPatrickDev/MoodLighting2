package com.jdp30.moodlighting2.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jdp30.moodlighting2.Fragments.GroupsFragment;
import com.jdp30.moodlighting2.Fragments.PresetsFragment;
import com.jdp30.moodlighting2.Fragments.SetColorFragment;
import com.jdp30.moodlighting2.Fragments.StartFadeFragment;
import com.jdp30.moodlighting2.MoodLightingHomeActivity;
import com.jdp30.moodlighting2.R;
import com.jdp30.moodlighting2.Util;

/**
 * Created by jackp on 30/05/2018.
 */

public class HomeScreenAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity mContext;

    private Card[] cards = new Card[]{
            new Card("Fade Show", R.mipmap.ic_launcher), new Card("Set Colour", R.mipmap.ic_launcher),
            new Card("Toggle Lights", R.mipmap.ic_launcher), new Card("Groups", R.mipmap.ic_launcher),
            new Card("Presets", R.mipmap.ic_launcher), new Card("Server Info", R.mipmap.ic_launcher)};

    public HomeScreenAdapter(Activity c) {
        mContext = c;
    }

    public int getCount() {
        return cards.length;
    }

    public Card getItem(int position) {
        return cards[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Card card = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.moodlighting_home_screen_card_layout, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(R.id.home_screen_card_layout_text);
        ImageView icon = (ImageView) convertView.findViewById(R.id.home_scree_card_layout_icon);

        text.setText(card.getTitle());
        icon.setImageResource(card.getResource());
        convertView.setOnClickListener(this);
        return convertView;
    }


    @Override
    public void onClick(View v) {
        String text = (String) ((TextView) v.findViewById(R.id.home_screen_card_layout_text)).getText();
        if (text.equalsIgnoreCase("Fade Show")) {
            MoodLightingHomeActivity.instance.setCurrentFragment(new StartFadeFragment());
        } else if (text.equalsIgnoreCase("Set Colour")) {
            MoodLightingHomeActivity.instance.setCurrentFragment(new SetColorFragment());
        }else if(text.equalsIgnoreCase("Toggle Lights")){
            Util.API_toggleLight(mContext,"40,40,40");
        }else if(text.equalsIgnoreCase("Groups")){
            MoodLightingHomeActivity.instance.setCurrentFragment(new GroupsFragment());
        }else if(text.equalsIgnoreCase("Presets")){
            MoodLightingHomeActivity.instance.setCurrentFragment(new PresetsFragment());
        }
    }
}

class Card {
    private String title;
    private int resource;

    public Card(String title, int resource) {
        this.title = title;
        this.resource = resource;
    }

    public String getTitle() {
        return title;
    }

    public int getResource() {
        return resource;
    }

}