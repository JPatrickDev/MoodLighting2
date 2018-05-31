package com.jdp30.moodlighting2.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by jackp on 30/03/2017.
 */
public class ColorGridListAdapter  extends BaseAdapter {
    private Context mContext;

    public ArrayList<Integer> colors = new ArrayList<Integer>();

    public ColorGridListAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return colors.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(64, 64, 64, 64);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setBackgroundColor(colors.get(position));
        return imageView;
    }
}