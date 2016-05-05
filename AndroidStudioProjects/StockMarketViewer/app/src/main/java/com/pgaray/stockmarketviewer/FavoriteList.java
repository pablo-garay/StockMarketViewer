package com.pgaray.stockmarketviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by Pablo on 5/4/2016.
 */
public class FavoriteList {
    // variable to hold context

    public static void addToFavoriteList(Context context, String newFavorite){
        if (isInFavoriteList(context, newFavorite)) return; /* if already favorite, don't do anything */
        StringBuilder sb = new StringBuilder();
        String[] favoriteList = getFavoriteList(context); /* previous list of favorites */

        /* Add new favorite to list */
        for (int i = 0; i < favoriteList.length; i++) {
            sb.append(favoriteList[i]).append(",");
        }
        sb.append(newFavorite);

        /* update SharedPreferences */
        updateSharedPreferences(context, sb.toString());
    }

    public static void removeFavoriteFromList(Context context, String name){
        if (isInFavoriteList(context, name)){
            StringBuilder sb = new StringBuilder();
            String[] favoriteList = getFavoriteList(context); /* previous list of favorites */

            /* IF name in list, do not include it */
            for (int i = 0; i < favoriteList.length; i++) {
                if (!name.equalsIgnoreCase(favoriteList[i])){
                    sb.append(favoriteList[i]).append(",");
                }
            }
            updateSharedPreferences(context, sb.toString());
        }
    }

    private static void updateSharedPreferences(Context context, String newString){
        SharedPreferences sharedPref = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        prefsEditor.putString("favorites", newString);
        prefsEditor.commit();
    }

    public static String[] getFavoriteList(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        String concatenatedFavoriteStrings = sharedPref.getString("favorites", ""); /* get String stored in preferences */

        if (concatenatedFavoriteStrings != null)
            Log.d("SharedPref:", "getFavoriteList: Not empty");
        else
            Log.d("SharedPref:", "getFavoriteList: Empty");

        Log.d("SharedPref:Attention:", "::::::::::::::: concatenatedFavoriteStrings:" + concatenatedFavoriteStrings);
        String[] favoriteList = new String[0];
        if (!concatenatedFavoriteStrings.isEmpty()){
            favoriteList = concatenatedFavoriteStrings.split(","); /* split comma separated list of favorites */
        }
        Log.d("SharedPref:favoriteList", "--------------------> List: " + favoriteList.toString());
        Log.d("SharedPref:", "favoriteListLength--------------------> Length: " + favoriteList.length);
        Log.d("SharedPref:", "Arrays.toString:" + Arrays.toString(favoriteList));

        return favoriteList;
    }

    public static boolean isInFavoriteList(Context context, String name){
        String[] favoriteList = getFavoriteList(context);

        for (int i = 0; i < favoriteList.length; i++) {
            if (name.equalsIgnoreCase(favoriteList[i])){
                Log.d("isInFavoriteList", "..................Checking: TRUE: " + name);
                return true;
            }
        }
        Log.d("isInFavoriteList", "..................Checking: FALSE: " + name);
        return false;
    }

}
