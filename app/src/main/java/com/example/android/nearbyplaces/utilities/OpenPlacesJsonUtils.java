package com.example.android.nearbyplaces.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.android.nearbyplaces.data.PlacesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Deep on 9/1/2017.
 */

public final class OpenPlacesJsonUtils {

    private static final String TAG = OpenPlacesJsonUtils.class.getSimpleName();

    /**
     * Take the String representing the complete places data in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * @param placeJsonStr
     * @param context
     * @throws JSONException
     */
    public static void getPlacesDataFromJson(String placeJsonStr, Context context, String type)
            throws JSONException {


        // These are the names of the JSON objects that need to be extracted.
        final String PLACES_RESULTS = "results";
        final String PLACE_ID = "place_id";
        final String NAME = "name";
        final String ADDRESS = "vicinity";
        final String RATING = "rating";
        final String OPENING_HOURS = "opening_hours";
        final String OPEN_NOW = "open_now";
        final String PHOTOS = "photos";
        final String PHOTO_REFERENCE = "photo_reference";
        final String ICON = "icon";

        try {
            JSONObject placesJson = new JSONObject(placeJsonStr);
            JSONArray placesArray = placesJson.getJSONArray(PLACES_RESULTS);

            // delete  old data based on place type
            context.getContentResolver().delete(PlacesContract.PlaceEntry.CONTENT_URI, PlacesContract.PlaceEntry.COLUMN_PLACE_TYPE + " = ?", new String[]{type});


            for (int i = 0; i < placesArray.length(); i++) {

                String place_id;
                String name;
                String address;
                boolean open_now;
                double rating;
                String photo_reference;
                String icon;
                // Get the JSON object representing the movie result
                JSONObject resultPlace = placesArray.getJSONObject(i);
                place_id = resultPlace.getString(PLACE_ID);
                name = resultPlace.getString(NAME);
                address = resultPlace.getString(ADDRESS);
                if (resultPlace.has(RATING))
                    rating = resultPlace.getDouble(RATING);
                else
                    rating = 0;
                if (resultPlace.has(OPENING_HOURS)) {
                    JSONObject resultOpening_Hours = resultPlace.getJSONObject(OPENING_HOURS);
                    open_now = resultOpening_Hours.getBoolean(OPEN_NOW);
                } else {
                    open_now = false;
                }
                if (resultPlace.has(PHOTOS)) {

                    JSONArray photo_array = resultPlace.getJSONArray(PHOTOS);
                    JSONObject resultPhoto = photo_array.getJSONObject(0);
                    photo_reference = resultPhoto.getString(PHOTO_REFERENCE);

                } else {
                    photo_reference = "";
                }
                icon = resultPlace.getString(ICON);
                //listPlace.add(new Places(type,name,address,open_now,rating,photo_reference,icon));
                ContentValues placeContentValue = new ContentValues();
                placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_PLACE_ID, place_id);
                placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_PLACE_NAME, name);
                placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_PLACE_TYPE, type);
                placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_ADDRESS, address);
                placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_RATING, rating);
                placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_PHOTO_REFERENCE, photo_reference);
                // placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_ICON_PATH,icon);


               /* Insert our new  data into place ContentProvider */
                context.getContentResolver().insert(PlacesContract.PlaceEntry.CONTENT_URI, placeContentValue);
                Log.d(TAG, "Data inserted successfully");
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

}
