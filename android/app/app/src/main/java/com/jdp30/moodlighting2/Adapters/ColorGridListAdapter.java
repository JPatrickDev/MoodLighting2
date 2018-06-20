package com.jdp30.moodlighting2.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.jdp30.moodlighting2.R;

import java.util.ArrayList;

/**
 * Created by jackp on 30/03/2017.
 */
public class ColorGridListAdapter extends BaseAdapter {
    private int padding = 64;
    private Context mContext;
    private boolean showAddButton = true;
    public ArrayList<Integer> colors = new ArrayList<Integer>();

    public ColorGridListAdapter(Context c) {
        mContext = c;
    }

    public ColorGridListAdapter(Context c, int padding) {
        this(c);
        showAddButton = false;
        this.padding = padding;
    }

    public int getCount() {
        if (showAddButton)
            return colors.size() + 1;
        else
            return colors.size();
    }

    public Object getItem(int position) {
        return colors.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == colors.size() && showAddButton) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.moodlighting_add_color_grid_element, parent, false);
            convertView.setMinimumHeight(padding);
            convertView.setPadding(padding, padding, padding, padding);
            return convertView;
        }
        ImageView imageView;

        imageView = new ImageView(mContext);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(padding, padding, padding, padding);


        imageView.setBackgroundColor(colors.get(position));
        return imageView;
    }

    public void addColor(int c) {
        this.colors.add(c);
        notifyDataSetInvalidated();
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        this.colors.remove(position);
        notifyDataSetInvalidated();
        notifyDataSetChanged();
    }
}