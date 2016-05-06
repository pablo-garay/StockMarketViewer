package com.pgaray.stockmarketviewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

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
import java.util.Timer;
import java.util.TimerTask;

import static com.pgaray.stockmarketviewer.FavoriteList.getFavoriteList;

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView autoCompleteTextView;
    //    public String data;
    public ArrayAdapter<String> aAdapter;
    private ArrayAdapter<FavoriteEntry> favoritesListViewAdapter;

    /**
     * Called when the activity is first created.
     */
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

        final TextWatcher textWatcher = new TextWatcher() {
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
                if (newText.length() >= 3) {
                    /*Log.d("InputString", newText);*/
                    new autocompleteTextViewFiller().execute(newText);
                }
            }
        };
        autoCompleteTextView.addTextChangedListener(textWatcher);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                Stock selection = (Stock) parent.getItemAtPosition(position);
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
        Button clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                autoCompleteTextView.setText("");
            }
        });

        /* Get Quote button functionality */
        Button getQuoteButton = (Button) findViewById(R.id.getQuoteButton);
        getQuoteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Get Quote", "onClick: " + autoCompleteTextView.getText().toString());
                getQuote(autoCompleteTextView.getText().toString());
            }
        });

        /* Refresh imageButton functionality */
        ImageButton imageButton = (ImageButton) findViewById(R.id.refreshImageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(MainActivity.this, "Clicked Refresh Button!", Toast.LENGTH_LONG).show();*/

                refreshFavoriteListView(favoritesListViewAdapter);
            }
        });

        /* Autorefresh switch functionality */
        addAutoRefreshButtonFunctionality();

        /* Display Favorites in ListView */
        populateFavoritesListView();
    }

    private void addAutoRefreshButtonFunctionality() {
        final Timer timer = new Timer();
        final TimerTask timerTask;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                //refresh Favorites List
                refreshFavoriteListView(favoritesListViewAdapter);
            }
        };


        Switch onOffSwitch = (Switch)  findViewById(R.id.autorefreshSwitch);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked){
                    timer.schedule(timerTask, 0, 10000);
                } else {
                    timer.cancel();
                }
            }
        });
    }

    private void getQuote(String symbol) {

        new stockDataGetter().execute(symbol);

        /*String message = "You selected: " + symbol;
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();*/
    }


    class stockDataGetter extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;
        boolean validationError = false;
        String companySymbol;

        @Override
        protected String doInBackground(String... key) {
            companySymbol = key[0];
            StringBuilder sb = new StringBuilder();
            String json_string = null;

            try {
                /* ------------------ Loading string from server content ------------------------ */
                URL url = new URL("http://stockstats-1256.appspot.com/stockstatsapi/json?symbol=" + companySymbol);
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
                    String errorStr = resultObject.get("Error").toString();
                    validationError = true;

                } catch (NullPointerException e) {
                    validationError = false;
                }

//                try {
//                    /* Create a list of items */
////                    entries.add(new StockDetailsEntry("NAME", resultObject.get("Name").toString(), 0));
////                    entries.add(new StockDetailsEntry("SYMBOL", resultObject.get("Symbol").toString(), 0));
////                    entries.add(new StockDetailsEntry("LASTPRICE", resultObject.get("Last Price").toString(), 0));
////                    entries.add(new StockDetailsEntry("CHANGE",
////                            resultObject.get("Change (Change Percent)").toString(), (int) resultObject.get("Change Indicator")));
////                    entries.add(new StockDetailsEntry("TIMESTAMP", resultObject.get("Time and Date").toString(), 0));
////                    entries.add(new StockDetailsEntry("MARKETCAP", resultObject.get("Market Cap").toString(), 0));
////                    entries.add(new StockDetailsEntry("VOLUME", resultObject.get("Volume").toString(), 0));
////                    entries.add(new StockDetailsEntry("CHANGEYTD",
////                            resultObject.get("Change YTD (Change Percent YTD)").toString(),
////                            (int) resultObject.get("Change YTD Indicator")));
////                    entries.add(new StockDetailsEntry("HIGH", resultObject.get("High").toString(), 0));
////                    entries.add(new StockDetailsEntry("LOW", resultObject.get("Low").toString(), 0));
////                    entries.add(new StockDetailsEntry("OPEN", resultObject.get("Open").toString(), 0));
////                    Log.d("Name", resultObject.get("Name").toString());
////                    Log.d("Symbol", resultObject.get("Symbol").toString());
////                    Log.d("Last Price", resultObject.get("Last Price").toString());
////                    Log.d("Change (Change Percent)", resultObject.get("Change (Change Percent)").toString());
////                    Log.d("Change Indicator", resultObject.get("Change Indicator").toString());
////                    Log.d("Time and Date", resultObject.get("Time and Date").toString());
////                    Log.d("Market Cap", resultObject.get("Market Cap").toString());
////                    Log.d("Volume", resultObject.get("Volume").toString());
////                    Log.d("ChangeYTD", resultObject.get("Change YTD (Change Percent YTD)").toString());
////                    Log.d("Change YTD Indicator", resultObject.get("Change YTD Indicator").toString());
////                    Log.d("High", resultObject.get("High").toString());
////                    Log.d("Low", resultObject.get("Low").toString());
////                    Log.d("Open", resultObject.get("Open").toString());
//
//                } catch (JSONException e) {
//                    // Oops
//                    e.printStackTrace();
//                }

            } catch (Exception e) {
                Log.w("Error", e.getMessage());
            } finally {
                urlConnection.disconnect();
            }

            runOnUiThread(new Runnable() {
                public void run() {
                    if (validationError) {
                        Toast.makeText(MainActivity.this,
                                "Failed to fetch data for symbol provided",
                                Toast.LENGTH_LONG).show();
                    } else {
                        /*Toast.makeText(MainActivity.this, "No Error: Data fetched", Toast.LENGTH_LONG).show();*/
                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                        intent.putExtra("symbol", companySymbol);
                        startActivity(intent);
                    }
                }
            });
            return null;
        }
    }

    class autocompleteTextViewFiller extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... key) {
            String newText = key[0];
            StringBuilder sb = new StringBuilder();
            String json_string = null;
            final ArrayList<Stock> suggest = new ArrayList<MainActivity.Stock>();

            try {
                 /* ------------------ Loading string from server stream ------------------------ */
                URL url = new URL("http://stockstats-1256.appspot.com/stockstatsapi/json?input=" + newText);
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
                    try {
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

            } catch (Exception e) {
                Log.w("Error", e.getMessage());
            } finally {
                urlConnection.disconnect();
            }

            runOnUiThread(new Runnable() {
                public void run() {
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

        /* Add favorites to favorite entries list */
        for (int i = 0; i < favoriteList.length; i++) {
            favoritesEntries.add(new FavoriteEntry(favoriteList[i], "Loading...", "", "", 0, ""));
        }

//        favoritesEntries.add(new FavoriteEntry("AAPL", "Apple Inc", "$ 109.99",
//                "+0.92%", "Market Cap: 609.80 Billion"));

        /* Build Adapter */
        favoritesListViewAdapter = new FavoritesEntryAdapter(this, favoritesEntries);

        /* Configure the list view */
        DynamicListView mDynamicListView = (DynamicListView) findViewById(R.id.favoritesListView);
        /* Callback for Listview items removal using Swipe to Dismiss gesture */
        mDynamicListView.enableSwipeToDismiss(
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            /* get item dismissed */
                            final FavoriteEntry dismissedItem = favoritesListViewAdapter.getItem(position);

                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Confirm deletion")
                                    .setMessage("Do you really want to delete " + dismissedItem.getFavoriteName() + " from Favorites?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            /* User pressed confirmation button, so delete */
                                            /*Toast.makeText(MainActivity.this, "Yaay", Toast.LENGTH_SHORT).show();*/

                                            /* remove favorite from SharedPreferences*/
                                            FavoriteList.removeFavoriteFromList(MainActivity.this, dismissedItem.getFavoriteSymbol());
                                            /* remove favorite from ListView */
                                            favoritesListViewAdapter.remove(dismissedItem);

                                            /* resize Favorite ListView according to updated content */
                                            resizeFavoriteListView();
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();
                        }
                    }
                }
        );
        mDynamicListView.setAdapter(favoritesListViewAdapter);

        /* resize Favorite ListView according to updated content */
        resizeFavoriteListView();

        /* on select favorite, show Stock details functionality */
        mDynamicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                FavoriteEntry singleItem = (FavoriteEntry) adapter.getItemAtPosition(position);
                /* get Quote from retrieved Symbol from item */
                getQuote(singleItem.getFavoriteSymbol());
            }
        });

        /* refresh with fresh data from API */
        refreshFavoriteListView(favoritesListViewAdapter);
    }

    private void refreshFavoriteListView(ArrayAdapter<FavoriteEntry> adapter) {
        for (int position = 0; position < adapter.getCount(); position++) {

            FavoriteEntry singleItem = adapter.getItem(position);
            new StockDetailsFragmentFiller().execute(new MyTaskParams(singleItem, adapter));
        }
    }

    private class MyTaskParams {
        FavoriteEntry singleItem;
        ArrayAdapter<FavoriteEntry> adapter;

        MyTaskParams(FavoriteEntry singleItem, ArrayAdapter<FavoriteEntry> adapter) {
            this.singleItem = singleItem;
            this.adapter = adapter;
        }
    }


    class StockDetailsFragmentFiller extends AsyncTask<MyTaskParams,String,String> {
        HttpURLConnection urlConnection;
        private String name;
        private String symbol;
        private String stockValue;
        private String changePercent;
        private int changeIndicator;
        private String marketCap;

        @Override
        protected String doInBackground(MyTaskParams... params) {
            /* retrieve data from parameters passed */
            final FavoriteEntry singleItem = params[0].singleItem;
            final ArrayAdapter adapter = params[0].adapter;
            String companySymbol = singleItem.getFavoriteSymbol();
            /* End of making order here */

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

                /* retrieve data to update content of Favorite */
                try {
                    name = resultObject.get("Name").toString();
                    symbol = resultObject.get("Symbol").toString();
                    stockValue = resultObject.get("Last Price").toString();
                    String completeString = resultObject.get("Change (Change Percent)").toString();
                    changePercent = completeString.substring(completeString.indexOf("(") + 1, completeString.indexOf(")"));
                    changeIndicator = (int) resultObject.get("Change Indicator");
                    marketCap = resultObject.get("Market Cap").toString();

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
                    /* Set new values */
                    singleItem.setFavoriteEntry(symbol, name, stockValue, changePercent,
                                                changeIndicator, "Market Cap: " + marketCap);

                    /* IMPORTANT!!!!! Notify that data has changed */
                    adapter.notifyDataSetChanged();

                    /* resize Favorite ListView according to updated content */
                    resizeFavoriteListView();
                }
            });
            return null;
        }
    }

    private void resizeFavoriteListView(){
        /* get favorite listview and pass it to function to resize */
        ListView favoriteListView = (DynamicNonScrollListView) findViewById(R.id.favoritesListView);
        justifyListViewHeightBasedOnChildren(favoriteListView);
    }


    public void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }
}