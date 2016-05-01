package com.pgaray.stockmarketviewer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
        viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager(), getApplicationContext()));

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) populateStockDetailsListView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) populateStockDetailsListView();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) populateStockDetailsListView();
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

        final List<stockDetailsEntry> entries = new ArrayList<stockDetailsEntry>();

        entries.add(new stockDetailsEntry("NAME", "name1", 0));
        entries.add(new stockDetailsEntry("SYMBOL", "symbol1", 0));
        entries.add(new stockDetailsEntry("LASTPRICE", "lastprice1", 0));
        entries.add(new stockDetailsEntry("CHANGE", "change1", 0));
        entries.add(new stockDetailsEntry("TIMESTAMP", "timestamp1", 0));
        entries.add(new stockDetailsEntry("MARKETCAP", "marketcap1", 0));
        entries.add(new stockDetailsEntry("VOLUME", "volume1", 0));
        entries.add(new stockDetailsEntry("CHANGEYTD", "changeytd1", 0));
        entries.add(new stockDetailsEntry("HIGH", "high1", 0));
        entries.add(new stockDetailsEntry("LOW", "low1", 0));
        entries.add(new stockDetailsEntry("OPEN", "open1", 0));

        /* Build Adapter */
        ArrayAdapter<stockDetailsEntry> adapter = new StockDetailAdapter(this, entries);


        /*ArrayAdapter<stockDetailsEntry> adapter = new ArrayAdapter<stockDetailsEntry>(
                this,                                   *//* context *//*
                android.R.layout.simple_list_item_1,    *//* Layout to use (create) *//*
                titles);                                *//* items to be displayed */

        /* Configure the list view */
        ListView list = (ListView) findViewById(R.id.stockDetailsListView);
        list.setAdapter(adapter);
    }

    private class CustomAdapter extends FragmentPagerAdapter {
        private String fragments [] = {"Current", "Historical", "News"};

        public CustomAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
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
