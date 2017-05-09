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
 * Created by infinity on 5/8/17.
 */

public class AccountInfoAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<String> keys = new ArrayList<>();
    private ArrayList<String> values = new ArrayList<>();
    private String key;
    private String value;

    ViewHolder holder;

    static class ViewHolder {
        public TextView keyInfo;
        public TextView valueInfo;
    }

    public AccountInfoAdapter(Activity context, ArrayList<String> keyItems, ArrayList<String> valueItems)
    {
        super();
        this.context = context;
        this.keys = keyItems;
        this.values = valueItems;
    }
    @Override
    public int getCount() {
        return this.keys.size();
    }

    @Override
    public Object getItem(int i) {
        return this.keys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        key  = this.keys.get(i);
        value = this.values.get(i);
        if(rowView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.account_information_item, null);

            holder = new ViewHolder();
            holder.keyInfo = (TextView) rowView.findViewById(R.id.keyInfo);
            holder.valueInfo = (TextView) rowView.findViewById(R.id.valueInfo);
            rowView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) rowView.getTag();
        }
            holder.keyInfo.setText(key);
            holder.valueInfo.setText(value);
        return rowView;
    }
}
