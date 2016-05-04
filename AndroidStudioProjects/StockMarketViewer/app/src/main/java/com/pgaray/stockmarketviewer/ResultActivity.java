package com.pgaray.stockmarketviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        /* Enable the back button in app */
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* get Intent which is the company's symbol */
        Intent intent = getIntent();
        final String stockSymbol = intent.getStringExtra("symbol");
//        TextView tv = (TextView) findViewById(R.id.textView3);
//        tv.setText(stockSymbol);
        Log.d("Received symbol", "onCreate: " + stockSymbol);

        /* change actionbar title */
        setTitle("Facebook, Inc.");

        /* the following creates a ViewPager with the 3 tabs */
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomFragmentPagerAdapter(getSupportFragmentManager(), getApplicationContext()));

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) new StockDetailsListViewFiller().execute(stockSymbol);
                if (tab.getPosition() == 1) loadHistoricalChartWebView(stockSymbol);
                if (tab.getPosition() == 2) populateNewsListView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) new StockDetailsListViewFiller().execute(stockSymbol);
                if (tab.getPosition() == 1) loadHistoricalChartWebView(stockSymbol);
                if (tab.getPosition() == 2) populateNewsListView();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) new StockDetailsListViewFiller().execute(stockSymbol);
                if (tab.getPosition() == 1) loadHistoricalChartWebView(stockSymbol);
                if (tab.getPosition() == 2) populateNewsListView();
            }
        });

        /* populate Current Details ListView with stock details */
//        new StockDetailsListViewFiller().execute(stockSymbol);

//        getList
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this add items to the action bar if is present */
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /* show or hide Add and Remove Favorite buttons according to whether the element if favorite */
        menu.findItem(R.id.action_add_favorite).setVisible(!isFavorite);
        menu.findItem(R.id.action_remove_favorite).setVisible(isFavorite);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_add_favorite:
                isFavorite = true;
                /*updateFavorite();*/
                supportInvalidateOptionsMenu();
                Toast.makeText(ResultActivity.this, "Bookmarked Favorite", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_remove_favorite:
                isFavorite = false;
                /*updateFavorite();*/
                supportInvalidateOptionsMenu();
                Toast.makeText(ResultActivity.this, "Removed from Favorites", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_share_facebook:
                Toast.makeText(ResultActivity.this, "You pressed the FB button", Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateNewsListView() {
        /* Create a list of items */

//        final List<NewsEntry> newsEntries = new ArrayList<NewsEntry>();
//
//        newsEntries.add(new NewsEntry("Title", "Content", "Publisher: Publisher1",
//                                  "Date: 24 March 2016, 10:37:30"));
//        newsEntries.add(new NewsEntry("Title", "Content", "Publisher: Publisher1",
//                                  "Date: 24 March 2016, 10:37:30"));
//        newsEntries.add(new NewsEntry("Title", "Content", "Publisher: Publisher1",
//                                  "Date: 24 March 2016, 10:37:30"));
//        newsEntries.add(new NewsEntry("Title", "Content", "Publisher: Publisher1",
//                                  "Date: 24 March 2016, 10:37:30"));
//        newsEntries.add(new NewsEntry("Title", "Content", "Publisher: Publisher1",
//                                  "Date: 24 March 2016, 10:37:30"));
//        Log.d("Created News Entries", "create news entries");
//
//        /* Build Adapter */
//        ArrayAdapter<NewsEntry> adapter = new NewsEntryAdapter(this, newsEntries);
//
//        /* Configure the list view */
//        ListView list = (ListView) findViewById(R.id.newsListView);
//        list.setAdapter(adapter);

        new NewsFeedListViewFiller().execute("AAPL");
    }

    private void loadHistoricalChartWebView(String stockSymbol){
        WebView browser = (WebView) findViewById(R.id.webView);
        /* set loading of images */
        browser.getSettings().setLoadsImagesAutomatically(true);
        /* enable JS */
        browser.getSettings().setJavaScriptEnabled(true);
        String url = null;

        try {
            url = "file:///android_asset/historicalchart.html?symbol=" +
                          URLEncoder.encode(stockSymbol, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap imageBitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                imageBitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return imageBitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    class StockDetailsListViewFiller extends AsyncTask<String,String,String> {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... key) {
            String companySymbol = key[0];
            StringBuilder sb = new StringBuilder();
            String json_string = null;
            final List<StockDetailsEntry> entries = new ArrayList<StockDetailsEntry>();

            try{
                /* ------------------ Loading string from server content ------------------------ */
                URL url = new URL("http://stockstats-1256.appspot.com/stockstatsapi/json?symbol="+companySymbol);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                json_string = sb.toString();
                /*Log.d("Info", result);*/
                /* ------------- Finished. String fully loaded from server response ------------- */
                Log.d("Result", sb.toString());

                /* We receive a JSON object (not a JSON array), so we should create a JSONObject */
                JSONObject resultObject = new JSONObject(json_string);
                /*System.out.println("arr: " + Arrays.toString(array));*/

                try {
                    /* Create a list of items */
                    entries.add(new StockDetailsEntry("NAME", resultObject.get("Name").toString(), 0));
                    entries.add(new StockDetailsEntry("SYMBOL", resultObject.get("Symbol").toString(), 0));
                    entries.add(new StockDetailsEntry("LASTPRICE", resultObject.get("Last Price").toString(), 0));
                    entries.add(new StockDetailsEntry("CHANGE", resultObject.get("Change (Change Percent)").toString(), 0));
                    entries.add(new StockDetailsEntry("TIMESTAMP", resultObject.get("Time and Date").toString(), 0));
                    entries.add(new StockDetailsEntry("MARKETCAP", resultObject.get("Market Cap").toString(), 0));
                    entries.add(new StockDetailsEntry("VOLUME", resultObject.get("Volume").toString(), 0));
                    entries.add(new StockDetailsEntry("CHANGEYTD", resultObject.get("Change YTD (Change Percent YTD)").toString(), 0));
                    entries.add(new StockDetailsEntry("HIGH", resultObject.get("High").toString(), 0));
                    entries.add(new StockDetailsEntry("LOW", resultObject.get("Low").toString(), 0));
                    entries.add(new StockDetailsEntry("OPEN", resultObject.get("Open").toString(), 0));
//                    Log.d("Name", resultObject.get("Name").toString());
//                    Log.d("Symbol", resultObject.get("Symbol").toString());
//                    Log.d("Last Price", resultObject.get("Last Price").toString());
//                    Log.d("Change (Change Percent)", resultObject.get("Change (Change Percent)").toString());
//                    Log.d("Change Indicator", resultObject.get("Change Indicator").toString());
//                    Log.d("Time and Date", resultObject.get("Time and Date").toString());
//                    Log.d("Market Cap", resultObject.get("Market Cap").toString());
//                    Log.d("Volume", resultObject.get("Volume").toString());
//                    Log.d("ChangeYTD", resultObject.get("Change YTD (Change Percent YTD)").toString());
//                    Log.d("Change YTD Indicator", resultObject.get("Change YTD Indicator").toString());
//                    Log.d("High", resultObject.get("High").toString());
//                    Log.d("Low", resultObject.get("Low").toString());
//                    Log.d("Open", resultObject.get("Open").toString());

                } catch (JSONException e) {
                    // Oops
                    e.printStackTrace();
                }

            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }finally {
                urlConnection.disconnect();
            }

            runOnUiThread(new Runnable(){
                public void run(){
                    /* populate Stock Details ListView */
                    /* Build Adapter */
                    ArrayAdapter<StockDetailsEntry> adapter = new StockDetailAdapter(ResultActivity.this, entries);

                    /* Configure the list view */
                    NonScrollListView list = (NonScrollListView) findViewById(R.id.stockDetailsListView);
                    list.setAdapter(adapter);

                    /* show The Image in a ImageView */
                    ImageView chartImageView = (ImageView) findViewById(R.id.todayStockChartImageView);
                    new DownloadImageTask(chartImageView)
                            .execute("http://chart.finance.yahoo.com/t?s=AAPL&lang=en-US&width=1200&height=1200");
                }
            });
            return null;
        }
    }

    class NewsFeedListViewFiller extends AsyncTask<String,String,String> {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... key) {
            String companySymbol = key[0];
            StringBuilder sb = new StringBuilder();
            String json_string = null;
            final List<NewsEntry> newsListEntries = new ArrayList<NewsEntry>();

            try{
                /* ------------------ Loading string from server content ------------------------ */
                URL url = new URL("http://stockstats-1256.appspot.com/stockstatsapi/json?newsq="+companySymbol);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                json_string = sb.toString();
                /*Log.d("Info", result);*/
                /* ------------- Finished. String fully loaded from server response ------------- */
                Log.d("Result", sb.toString());

                /* We receive a JSON array (not a JSON object), so we should create a JSONArray */
                JSONArray array = new JSONArray(json_string);
                System.out.println("arr: " + array.toString());

                try {
                    /* Create a list of items */
                    for (int i = 0; i < array.length(); i++) {
                        try{
                            /* Important note: The elements of the array are objects */
                            JSONObject row = array.getJSONObject(i);
                            /*Log.d("News Row::::::::", row.toString());*/
                            /*Log.d("Title", row.getString("Title"));
                            Log.d("Description", row.getString("Description"));
                            Log.d("Source", row.getString("Source"));
                            Log.d("Date", row.getString("Date"));*/
                            newsListEntries.add(new NewsEntry(
                                    row.getString("Title"),
                                    row.getString("Description"),
                                    "Publisher: " + row.getString("Source"),
                                    "Date: " + row.getString("Date") ));

                        } catch (JSONException e) {
                            // Oops
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    // Oops
                    e.printStackTrace();
                }

            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }finally {
                urlConnection.disconnect();
            }

            runOnUiThread(new Runnable(){
                public void run(){
                    /* populate News ListView */
                    /* Build Adapter */
                    ArrayAdapter<NewsEntry> adapter = new NewsEntryAdapter(ResultActivity.this,
                                                                            newsListEntries);
                    /* Configure the list view */
                    ListView list = (ListView) findViewById(R.id.newsListView);
                    list.setAdapter(adapter);
                }
            });
            return null;
        }
    }
}
