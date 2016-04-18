package com.pgaray.stockmarketviewer;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

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

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView autoCompleteTextView;
    public List<String> suggest;
//    public String data;
    public ArrayAdapter<String> aAdapter;
/*    String[] countries = {
            "Afghanistan",
            "Albania",
            "Algeria",
            "Andorra",
            "Angola",
            "Antigua y Barbuda",
            "Argentina",
            "Armenia",
            "Austria",
            "Australia",
            "Azerbaijan",
    };*/

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        /*ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item, countries);

        autoCompleteTextView.setThreshold(3); *//* wait for 3 characters to show suggestions or hints *//*
        autoCompleteTextView.setAdapter(adapter);*/

        suggest = new ArrayList<String>();

        autoCompleteTextView.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                new getJson().execute(newText);
            }
        });


    }
    class getJson extends AsyncTask<String,String,String>{

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... key) {
            String newText = key[0];
            StringBuilder sb = new StringBuilder();
            String string_of_json = null;

            try{

                URL url = new URL("http://stockstats-1256.appspot.com/stockstatsapi/json?input="+newText);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                string_of_json = sb.toString();
                /*Log.d("Info", result);*/

                suggest = new ArrayList<String>();

                Log.d("Result", sb.toString());

                /* We receive a JSON array (not a JSON object), so we should create a JSONArray */
                JSONArray array = new JSONArray(string_of_json);
                /*System.out.println("arr: " + Arrays.toString(array));*/
                for (int i = 0; i < array.length(); i++) {
                    try{
                        JSONObject row = array.getJSONObject(i);

                        /*Log.d("Symbol", row.getString("Symbol"));
                        Log.d("Name", row.getString("Name"));
                        Log.d("Exchange", row.getString("Exchange"));*/
                        String SuggestKey = row.getString("Symbol") + "\n" + row.getString("Name") +
                                " (" + row.getString("Exchange") + ")";
                        suggest.add(SuggestKey);
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
                    aAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.item,suggest);
                    autoCompleteTextView.setThreshold(3); /* wait for 3 characters to show suggestions or hints */
                    autoCompleteTextView.setAdapter(aAdapter);
                    aAdapter.notifyDataSetChanged();
                }
            });

            return null;
        }

    }

//    private class getStockSuggestions extends AsyncTask<Void, Void, String> {
//        @Override
//
//    }
}