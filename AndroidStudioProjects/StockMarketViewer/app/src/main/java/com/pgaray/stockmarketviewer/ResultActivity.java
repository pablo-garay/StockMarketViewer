package com.pgaray.stockmarketviewer;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        /* the following creates a ViewPager with the 3 tabs */
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomFragmentPagerAdapter(getSupportFragmentManager(), getApplicationContext()));

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) populateStockDetailsListView();
                if (tab.getPosition() == 1) loadHistoricalChartWebView();
                if (tab.getPosition() == 2) populateNewsListView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) populateStockDetailsListView();
                if (tab.getPosition() == 1) loadHistoricalChartWebView();
                if (tab.getPosition() == 2) populateNewsListView();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) populateStockDetailsListView();
                if (tab.getPosition() == 1) loadHistoricalChartWebView();
                if (tab.getPosition() == 2) populateNewsListView();
            }
        });

        /* get Intent which is the company's symbol */
        Intent intent = getIntent();
        String stockSymbol = intent.getStringExtra("symbol");
//        TextView tv = (TextView) findViewById(R.id.textView3);
//        tv.setText(stockSymbol);
        Log.d("Received symbol", "onCreate: " + stockSymbol);

        /* populate Current Details ListView with stock details */
//        populateStockDetailsListView();

//        getList
    }

    private void populateStockDetailsListView() {
        /* Create a list of items */

        final List<StockDetailsEntry> entries = new ArrayList<StockDetailsEntry>();

        entries.add(new StockDetailsEntry("NAME", "name1", 0));
        entries.add(new StockDetailsEntry("SYMBOL", "symbol1", 0));
        entries.add(new StockDetailsEntry("LASTPRICE", "lastprice1", 0));
        entries.add(new StockDetailsEntry("CHANGE", "change1", 0));
        entries.add(new StockDetailsEntry("TIMESTAMP", "timestamp1", 0));
        entries.add(new StockDetailsEntry("MARKETCAP", "marketcap1", 0));
        entries.add(new StockDetailsEntry("VOLUME", "volume1", 0));
        entries.add(new StockDetailsEntry("CHANGEYTD", "changeytd1", 0));
        entries.add(new StockDetailsEntry("HIGH", "high1", 0));
        entries.add(new StockDetailsEntry("LOW", "low1", 0));
        entries.add(new StockDetailsEntry("OPEN", "open1", 0));

        /* Build Adapter */
        ArrayAdapter<StockDetailsEntry> adapter = new StockDetailAdapter(this, entries);

        /* Configure the list view */
        ListView list = (ListView) findViewById(R.id.stockDetailsListView);
        list.setAdapter(adapter);
    }
    private void populateNewsListView() {
        /* Create a list of items */

        final List<NewsEntry> newsEntries = new ArrayList<NewsEntry>();

        newsEntries.add(new NewsEntry("Title", "Content", "Publisher: Publisher1",
                                  "Date: 24 March 2016, 10:37:30"));
        newsEntries.add(new NewsEntry("Title", "Content", "Publisher: Publisher1",
                                  "Date: 24 March 2016, 10:37:30"));
        newsEntries.add(new NewsEntry("Title", "Content", "Publisher: Publisher1",
                                  "Date: 24 March 2016, 10:37:30"));
        newsEntries.add(new NewsEntry("Title", "Content", "Publisher: Publisher1",
                                  "Date: 24 March 2016, 10:37:30"));
        newsEntries.add(new NewsEntry("Title", "Content", "Publisher: Publisher1",
                                  "Date: 24 March 2016, 10:37:30"));
        Log.d("Created News Entries", "create news entries");

        /* Build Adapter */
        ArrayAdapter<NewsEntry> adapter = new NewsEntryAdapter(this, newsEntries);

        /* Configure the list view */
        ListView list = (ListView) findViewById(R.id.newsListView);
        list.setAdapter(adapter);
    }

    private void loadHistoricalChartWebView(){
        WebView browser = (WebView) findViewById(R.id.webView);
        /* set loading of images */
        browser.getSettings().setLoadsImagesAutomatically(true);
        /* enable JS */
        browser.getSettings().setJavaScriptEnabled(true);
        String url = "file:///android_asset/historicalchart.html";
        browser.loadUrl(url);
    }

    private class CustomFragmentPagerAdapter extends FragmentPagerAdapter {
        private String fragments [] = {"Current", "Historical", "News"};

        public CustomFragmentPagerAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new StockDetailsFragment();
                case 1:
                    return new HistoricalChartsFragment();
                case 2:
                    return new NewsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position){
            return fragments[position];
        }
    }

//    class getStockDetailsJson extends AsyncTask<String,String,String> {
//
//        HttpURLConnection urlConnection;
//
//        @Override
//        protected String doInBackground(String... key) {
//            String companySymbol = key[0];
//            StringBuilder sb = new StringBuilder();
//            String json_string = null;
//
//            try{
//                /* ------------------ Loading string from server content ------------------------ */
//                URL url = new URL("http://stockstats-1256.appspot.com/stockstatsapi/json?symbol=TSLA"+companySymbol);
//                urlConnection = (HttpURLConnection) url.openConnection();
//                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line);
//                }
//                json_string = sb.toString();
//                /*Log.d("Info", result);*/
//                /* ------------- Finished. String fully loaded from server response ------------- */
//                Log.d("Result", sb.toString());
//
//                /* We receive a JSON object (not a JSON array), so we should create a JSONObject */
//                JSONObject resultObject = new JSONObject(json_string);
//                /*System.out.println("arr: " + Arrays.toString(array));*/
//
//                try {
//                    resultObject.get("Name");
//                    resultObject.get("Symbol");
//                    resultObject.get("Last Price");
//                    resultObject.get("Change (Change Percent)");
//                    resultObject.get("Change Indicator");
//                    resultObject.get("Time and Date");
//                    resultObject.get("Market Cap");
//                    resultObject.get("Volume");
//                    resultObject.get("Change YTD (Change Percent YTD)");
//                    resultObject.get("Change YTD Indicator");
//                    resultObject.get("High");
//                    resultObject.get("Low");
//                    resultObject.get("Open");
//                } catch (JSONException e) {
//                    // Oops
//                    e.printStackTrace();
//                }
//
//                /*for (int i = 0; i < array.length(); i++) {
//                    try{
//                        JSONObject row = array.getJSONObject(i);
//
//                        *//*Log.d("Symbol", row.getString("Symbol"));
//                        Log.d("Name", row.getString("Name"));
//                        Log.d("Exchange", row.getString("Exchange"));*//*
//                        Stock SuggestKey;
//                        suggest.add(new Stock(row.getString("Symbol"), row.getString("Name"), row.getString("Exchange")));
//                    } catch (JSONException e) {
//                        // Oops
//                        e.printStackTrace();
//                    }
//                }*/
//
//                /*
//                JSONArray jArray = new JSONArray(sb);
//                for(int i=0;i<jArray.getJSONArray(1).length();i++){
//
//                }*/
//
//            }catch(Exception e){
//                Log.w("Error", e.getMessage());
//            }finally {
//                urlConnection.disconnect();
//            }
//
//            runOnUiThread(new Runnable(){
//                public void run(){
//                    /* populate Stock Details ListView */
//
///*                    ArrayAdapter<Stock> aAdapter = new ArrayAdapter<MainActivity.Stock>(getApplicationContext(),R.layout.item, suggest);
////                    aAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.item,suggest);
//                    *//*autoCompleteTextView.setThreshold(3); *//**//* wait for 3 characters to show suggestions or hints *//*
//                    autoCompleteTextView.setAdapter(aAdapter);
//                    aAdapter.notifyDataSetChanged();*/
//                }
//            });
//            return null;
//        }
//    }


}
