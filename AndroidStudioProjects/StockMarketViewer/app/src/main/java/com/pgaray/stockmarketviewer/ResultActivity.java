package com.pgaray.stockmarketviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

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
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private String stockSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Facebook init should be done right here at this place*/
       init_facebook();
        /* End of Facebook init */
        setContentView(R.layout.activity_result);

        /* Enable the back button in app */
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* get Intent which is the company's symbol */
        Intent intent = getIntent();
        stockSymbol = intent.getStringExtra("symbol");
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
                loadTabContent(tab.getPosition(), stockSymbol);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                loadTabContent(tab.getPosition(), stockSymbol);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                loadTabContent(tab.getPosition(), stockSymbol);
            }
        });

        /* After creating the tabs, set current tab */
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.select();
    }

    private void init_facebook() {
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional -- register callback to manage result after Share dialog
        // user interaction is done
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                if (result.getPostId() != null){
                    /* Content has been shared and posted */
                    Toast.makeText(ResultActivity.this, "You shared this post", Toast.LENGTH_LONG).show();
                } else {
                    /* Not posted e.g. User hit cancel Button */
                    Toast.makeText(ResultActivity.this, "Post not shared", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancel() {
                /* User turned back from FB share offer page */
                Toast.makeText(ResultActivity.this, "The post has not been shared", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(ResultActivity.this, "Error while trying to share post", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadTabContent(int position, String stockSymbol){
        if (position == 0) new StockDetailsFragmentFiller().execute(stockSymbol);
        else if (position == 1) loadHistoricalChartWebView(stockSymbol);
        else if (position == 2) new NewsFeedListViewFiller().execute(stockSymbol);
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
                /* DONT DISPLAY text when favorite removed as requested by client */
                /*Toast.makeText(ResultActivity.this, "Removed from Favorites", Toast.LENGTH_LONG).show();*/
                return true;

            case R.id.action_share_facebook:

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("http://chart.finance.yahoo.com/t?s=" + stockSymbol + "&lang=en-US&width=1200&height=1200"))
                            .setContentTitle("Current Stock Price of Facebook, Inc., $ 123")
                            .setImageUrl(Uri.parse("http://chart.finance.yahoo.com/t?s=" + stockSymbol + "&lang=en-US&width=1200&height=1200"))
                            .setContentDescription(
                                    "Stock Information of Facebook, Inc.")
                            .build();

                    shareDialog.show(linkContent /*, ShareDialog.Mode*/); /* Show Facebook Share Dialog */
                }

                /*Toast.makeText(ResultActivity.this, "You pressed the FB button", Toast.LENGTH_LONG).show();*/
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

    class StockDetailsFragmentFiller extends AsyncTask<String,String,String> {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... key) {
            final String companySymbol = key[0];
            StringBuilder sb = new StringBuilder();
            String json_string = null;
            final List<StockDetailsEntry> entries = new ArrayList<StockDetailsEntry>();
            int val;

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
                    entries.add(new StockDetailsEntry("CHANGE",
                            resultObject.get("Change (Change Percent)").toString(), (int) resultObject.get("Change Indicator")));
                    entries.add(new StockDetailsEntry("TIMESTAMP", resultObject.get("Time and Date").toString(), 0));
                    entries.add(new StockDetailsEntry("MARKETCAP", resultObject.get("Market Cap").toString(), 0));
                    entries.add(new StockDetailsEntry("VOLUME", resultObject.get("Volume").toString(), 0));
                    entries.add(new StockDetailsEntry("CHANGEYTD",
                            resultObject.get("Change YTD (Change Percent YTD)").toString(),
                            (int) resultObject.get("Change YTD Indicator")));
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

                    /* show Image in a ImageView */
                    ImageView chartImageView = (ImageView) findViewById(R.id.todayStockChartImageView);
                    new DownloadImageTask(chartImageView)
                            .execute("http://chart.finance.yahoo.com/t?s=" + companySymbol + "&lang=en-US&width=1200&height=1200");
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
                                    "Date: " + row.getString("Date"),
                                    row.getString("Url")));

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

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                            NewsEntry singleItem = (NewsEntry) adapter.getItemAtPosition(position);
                            String url = singleItem.getNewsUrl();
                            /* open URL */
                            Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
                }
            });
            return null;
        }
    }
}
