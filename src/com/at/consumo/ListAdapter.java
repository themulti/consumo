package com.at.consumo;

/**
 * Created with IntelliJ IDEA.
 * User: at
 * Date: 4/18/13
 * Time: 8:47 PM
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<DescriptionData> {


    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.simplerow, null);
        }

        DescriptionData item = getItem(position);

        if (item != null) {
            TextView title = (TextView) v.findViewById(R.id.rowTitle);
            TextView value = (TextView) v.findViewById(R.id.rowValue);

            if (title != null) {
                title.setText(item.getDescription());
            }
            if (value != null) {
                value.setText(item.getValue());
            }
        }

        return v;
    }
}