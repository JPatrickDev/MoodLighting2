package com.jdp30.moodlighting2.Adapters;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jdp30.moodlighting2.Fragments.SetColorFragment;
import com.jdp30.moodlighting2.Fragments.StartFadeFragment;
import com.jdp30.moodlighting2.MoodLightingHomeActivity;
import com.jdp30.moodlighting2.R;

/**
 * Created by jackp on 30/05/2018.
 */

public class HomeScreenAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;

    private Card[] cards = new Card[]{
            new Card("Fade Show", R.mipmap.ic_launcher), new Card("Set Colour", R.mipmap.ic_launcher),
            new Card("Toggle Lights", R.mipmap.ic_launcher), new Card("Groups", R.mipmap.ic_launcher),
            new Card("Presets", R.mipmap.ic_launcher), new Card("Server Info", R.mipmap.ic_launcher)};

    public HomeScreenAdapter(Context c) {
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

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Card card = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.home_screen_card_layout, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(R.id.home_screen_card_layout_text);
        ImageView icon = (ImageView) convertView.findViewById(R.id.home_scree_card_layout_icon);

        // Populate the data into the template view using the data object
        text.setText(card.getTitle());
        icon.setImageResource(card.getResource());
        // Return the completed view to render on screen

       // convertView.setMinimumHeight(MoodLightingHomeActivity.height/3);
        convertView.setOnClickListener(this);
        return convertView;
    }


    @Override
    public void onClick(View v) {
        String text = (String) ((TextView)v.findViewById(R.id.home_screen_card_layout_text)).getText();
        if(text.equalsIgnoreCase("Fade Show")){
       //     Toast.makeText(mContext,"Start Fade Show",Toast.LENGTH_LONG).show();
            MoodLightingHomeActivity.instance.setCurrentFragment(new StartFadeFragment());
        }
        else if(text.equalsIgnoreCase("Set Colour")){
            MoodLightingHomeActivity.instance.setCurrentFragment(new SetColorFragment());
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