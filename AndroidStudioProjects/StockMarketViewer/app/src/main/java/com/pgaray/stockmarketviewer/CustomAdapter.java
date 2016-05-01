package com.pgaray.stockmarketviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Pablo on 4/30/2016.
 */
class StockDetailAdapter extends ArrayAdapter<stockDetailsEntry>{
    public StockDetailAdapter(Context context, List<stockDetailsEntry> items) {
            super(context, R.layout.custom_list_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_list_item, parent, false);

        stockDetailsEntry singleItem = getItem(position);
        TextView titleText = (TextView) customView.findViewById(R.id.titleText);
        TextView valueText = (TextView) customView.findViewById(R.id.valueText);

        titleText.setText(singleItem.getTitle());
        valueText.setText(singleItem.getValue());
        return customView;
    }
}
