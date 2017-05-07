package com.example.infinity.pixie.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.infinity.pixie.Pixie;
import com.example.infinity.pixie.R;

import java.util.ArrayList;

/**
 * Created by infinity on 5/5/17.
 */

public class ProfileAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<Integer> icons = new ArrayList<>();
    private String itemName;
    private int iconString;

    ViewHolder holder;

    static class ViewHolder {
        public TextView tileItem;
        public Button tileIcon;
    }

    public ProfileAdapter(Activity context, ArrayList<String> initialItems, ArrayList<Integer> menuIcons)
    {
        super();
        this.context = context;
        this.items = initialItems;
        this.icons = menuIcons;
    }
    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int i) {
        return this.items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        itemName  = this.items.get(i);
        iconString = this.icons.get(i);
        if(rowView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.profile_list_item, null);

            holder = new ViewHolder();
            holder.tileItem = (TextView) rowView.findViewById(R.id.menuItem);
            holder.tileIcon = (Button) rowView.findViewById(R.id.menuIcon);
            rowView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.tileItem.setText(itemName);
        holder.tileIcon.setText(iconString);
        holder.tileIcon.setTypeface(Pixie.fontawesome);
        return rowView;
    }
}
