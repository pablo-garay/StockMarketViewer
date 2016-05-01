package com.pgaray.stockmarketviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Pablo on 5/1/2016.
 */
class NewsEntryAdapter extends ArrayAdapter<NewsEntry> {
    public NewsEntryAdapter(Context context, List<NewsEntry> items) {
        super(context, R.layout.custom_news_list_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_news_list_item, parent, false);

        NewsEntry singleItem = getItem(position);
        TextView newsTitleText = (TextView) customView.findViewById(R.id.newsTitleText);
        TextView newsContentText = (TextView) customView.findViewById(R.id.newsContentText);
        TextView newsPublisherText = (TextView) customView.findViewById(R.id.newsPublisherText);
        TextView newsDateText = (TextView) customView.findViewById(R.id.newsDateText);

        newsTitleText.setText(singleItem.getNewsTitle());
        newsContentText.setText(singleItem.getNewsContent());
        newsPublisherText.setText(singleItem.getNewsPublisher());
        newsDateText.setText(singleItem.getNewsDate());
        return customView;
    }
}
