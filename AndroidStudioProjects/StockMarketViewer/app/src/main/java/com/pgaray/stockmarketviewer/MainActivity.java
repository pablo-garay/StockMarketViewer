package com.pgaray.stockmarketviewer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.FacebookSdk;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

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

import static com.pgaray.stockmarketviewer.FavoriteList.getFavoriteList;

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView autoCompleteTextView;
//    public String data;
    public ArrayAdapter<String> aAdapter;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        setContentView(R.layout.activity_main);

        /* Get a Handle to SharedPreferences */
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        /* set and show icon in ActionBar */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true); // set show icon = true
        actionBar.setIcon(R.drawable.stockmarket);

        /* ------------------ Start of code for AutoComplete feature ---------------------- */
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.autocomplete_item);
        autoCompleteTextView.setAdapter(adapter);
        /*suggest = new ArrayList<String>();*/

        final TextWatcher textWatcher = new TextWatcher(){
            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();

                if (autoCompleteTextView.isPerformingCompletion()) {
                    // An item has been selected from the list. Ignore.
                    /*Log.d("onItemClick", "onItemClick: Clicked");*/
                    return;
                }


                // Your code for a general case (suggest valid options to select)
                if (newText.length() >= 3){
                    /*Log.d("InputString", newText);*/
                    new autocompleteTextViewFiller().execute(newText);
                }
            }
        };
        autoCompleteTextView.addTextChangedListener(textWatcher);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                Stock selection = (Stock)parent.getItemAtPosition(position);
                //Do something with the selected text
                /*Log.d("ItemSelected", "You selected: " + selection.symbol);*/
                /* Remove autoCompleteTextView's textWatcher so that we can change its
                   without text without triggering extra undesired API calls onTextChanged */
                autoCompleteTextView.removeTextChangedListener(textWatcher);
                autoCompleteTextView.setText(selection.symbol);
                autoCompleteTextView.addTextChangedListener(textWatcher);
            }
        });
        /* ------------------ End AutoComplete feature ---------------------------*/

        /* Clear button functionality */
        Button clearButton = (Button) findViewById(R.id.clearButton );
        clearButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                autoCompleteTextView.setText("");
            }
        });

        /* Get Quote button functionality */
        Button getQuoteButton = (Button) findViewById(R.id.getQuoteButton);
        getQuoteButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Get Quote", "onClick: " + autoCompleteTextView.getText().toString());
                getQuote(autoCompleteTextView.getText().toString());
            }
        });

        /* Display Favorites in ListView */
        populateFavoritesListView();
    }

    private void getQuote(String symbol){
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("symbol", symbol);
        startActivity(intent);

        /*String message = "You selected: " + symbol;
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();*/
    }

    class autocompleteTextViewFiller extends AsyncTask<String,String,String>{
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... key) {
            String newText = key[0];
            StringBuilder sb = new StringBuilder();
            String json_string = null;
            final ArrayList<Stock> suggest= new ArrayList<MainActivity.Stock>();

            try{
                 /* ------------------ Loading string from server stream ------------------------ */
                URL url = new URL("http://stockstats-1256.appspot.com/stockstatsapi/json?input="+newText);
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
                /*System.out.println("arr: " + Arrays.toString(array));*/
                for (int i = 0; i < array.length(); i++) {
                    try{
                        JSONObject row = array.getJSONObject(i);

                        /*Log.d("Symbol", row.getString("Symbol"));
                        Log.d("Name", row.getString("Name"));
                        Log.d("Exchange", row.getString("Exchange"));*/
                        suggest.add(new Stock(row.getString("Symbol"), row.getString("Name"), row.getString("Exchange")));
                    } catch (JSONException e) {
                        // Oops
                        e.printStackTrace();
                    }
                }

                /*
                JSONArray jArray = new JSONArray(sb);
                for(int i=0;i<jArray.getJSONArray(1).length();i++){

                }*/

            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }finally {
                urlConnection.disconnect();
            }

            runOnUiThread(new Runnable(){
                public void run(){
                    ArrayAdapter<Stock> aAdapter = new ArrayAdapter<MainActivity.Stock>(getApplicationContext(),
                            R.layout.autocomplete_item, suggest);
//                    aAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.item,suggest);
                    /*autoCompleteTextView.setThreshold(3); *//* wait for 3 characters to show suggestions or hints */
                    autoCompleteTextView.setAdapter(aAdapter);
                    aAdapter.notifyDataSetChanged();
                }
            });
            return null;
        }
    }

    public static class Stock {
        private String symbol;
        private String description;

        public Stock(String symbol, String name, String exchange) {
            this.symbol = symbol;
            this.description = name + " (" + exchange + ")";
        }

        @Override
        public String toString() {
            return symbol + "\n" + description;
        }
    }

    private void populateFavoritesListView() {
        /* Create a list of items */
        final List<FavoriteEntry> favoritesEntries = new ArrayList<FavoriteEntry>();

        String[] favoriteList = getFavoriteList(MainActivity.this);

        for (int i = 0; i < favoriteList.length; i++) {
            favoritesEntries.add(new FavoriteEntry(favoriteList[i], "Loading...", "Loading...",
                    "Loading...", "Loading..."));
        }

//        favoritesEntries.add(new FavoriteEntry("AAPL", "Apple Inc", "$ 109.99",
//                "+0.92%", "Market Cap: 609.80 Billion"));
//        favoritesEntries.add(new FavoriteEntry("AAPL", "Apple Inc", "$ 109.99",
//                        "+0.92%", "Market Cap: 609.80 Billion"));
//        favoritesEntries.add(new FavoriteEntry("AAPL", "Apple Inc", "$ 109.99",
//                        "+0.92%", "Market Cap: 609.80 Billion"));
//        favoritesEntries.add(new FavoriteEntry("AAPL", "Apple Inc", "$ 109.99",
//                        "+0.92%", "Market Cap: 609.80 Billion"));
//        favoritesEntries.add(new FavoriteEntry("AAPL", "Apple Inc", "$ 109.99",
//                        "+0.92%", "Market Cap: 609.80 Billion"));

        /* Build Adapter */
        final ArrayAdapter<FavoriteEntry> adapter = new FavoritesEntryAdapter(this, favoritesEntries);

        /* Configure the list view */
        DynamicListView mDynamicListView = (DynamicListView) findViewById(R.id.favoritesListView);
        /* Callback for Listview items removal using Swipe to Dismiss gesture */
        mDynamicListView.enableSwipeToDismiss(
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            adapter.remove(adapter.getItem(position)) ;
                        }
                    }
                }
        );
        mDynamicListView.setAdapter(adapter);
    }


//    class FavoriteListViewFiller extends AsyncTask<String,String,String> {
//        HttpURLConnection urlConnection;
//
//        @Override
//        protected String doInBackground(String... key) {
//            final String companySymbol = key[0];
//            StringBuilder sb = new StringBuilder();
//            String json_string = null;
//            final List<StockDetailsEntry> entries = new ArrayList<StockDetailsEntry>();
//            int val;
//
//            try{
//                /* ------------------ Loading string from server content ------------------------ */
//                URL url = new URL("http://stockstats-1256.appspot.com/stockstatsapi/json?symbol="+companySymbol);
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
//                    favoritesEntries.add(new FavoriteEntry(favoriteList[i], "Apple Inc", "$ 109.99",
//                            "+0.92%", "Market Cap: 609.80 Billion"));
//                    /* Create a list of items */
//                    entries.add(new StockDetailsEntry("NAME", resultObject.get("Name").toString(), 0));
//                    entries.add(new StockDetailsEntry("SYMBOL", resultObject.get("Symbol").toString(), 0));
//                    entries.add(new StockDetailsEntry("LASTPRICE", resultObject.get("Last Price").toString(), 0));
//                    entries.add(new StockDetailsEntry("CHANGE",
//                            resultObject.get("Change (Change Percent)").toString(), (int) resultObject.get("Change Indicator")));
//                    entries.add(new StockDetailsEntry("MARKETCAP", resultObject.get("Market Cap").toString(), 0));
//
//                } catch (JSONException e) {
//                    // Oops
//                    e.printStackTrace();
//                }
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
//                    /* Build Adapter */
//                    ArrayAdapter<StockDetailsEntry> adapter = new StockDetailAdapter(ResultActivity.this, entries);
//
//                    /* Configure the list view */
//                    NonScrollListView list = (NonScrollListView) findViewById(R.id.stockDetailsListView);
//                    list.setAdapter(adapter);
//
//                    /* show Image in a ImageView */
//                    ImageView chartImageView = (ImageView) findViewById(R.id.todayStockChartImageView);
//                    new DownloadImageTask(chartImageView)
//                            .execute("http://chart.finance.yahoo.com/t?s=" + companySymbol + "&lang=en-US&width=1200&height=1200");
//                }
//            });
//            return null;
//        }
//    }
}