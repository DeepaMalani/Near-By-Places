package com.example.android.nearbyplaces.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.android.nearbyplaces.data.PlacesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Deep on 9/5/2017.
 */

public class OpenPlaceDetailsJsonUtils {
    private static final String TAG = OpenPlaceDetailsJsonUtils.class.getSimpleName();
    private static String strSeparator = "__,__";

    /**
     * Take the String representing the complete places data in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * @param placeJsonStr
     * @param context
     * @throws JSONException
     */
    public static void getPlaceDetailDataFromJson(String placeJsonStr, Context context, String type)
            throws JSONException {


        // These are the names of the JSON objects that need to be extracted.
        final String PLACES_RESULTS = "result";
        final String PLACE_ID = "place_id";
        final String PHONE_NUMBER = "formatted_phone_number";
        final String OPENING_HOURS = "opening_hours";
        final String WEEKDAY_TEXT = "weekday_text";
        final String RATING = "rating";
        final String URL = "url";
        final String WEBSITE = "website";

        try {
            String place_id;
            String phone_number;
            double rating;
            String website;
            String url;
            String openingHours;

            JSONObject placeDetailsJson = new JSONObject(placeJsonStr);
            JSONObject resultPlaceDetails = placeDetailsJson.getJSONObject(PLACES_RESULTS);

            place_id = resultPlaceDetails.getString(PLACE_ID);

            if (resultPlaceDetails.has(PHONE_NUMBER))
                phone_number = resultPlaceDetails.getString(PHONE_NUMBER);
            else
                phone_number = "";

            if (resultPlaceDetails.has(OPENING_HOURS)) {
                JSONObject resultOpening_Hours = resultPlaceDetails.getJSONObject(OPENING_HOURS);
                JSONArray weekday_text_array = resultOpening_Hours.getJSONArray(WEEKDAY_TEXT);
                String[] weekdays = new String[weekday_text_array.length()];
                for (int i = 0; i < weekday_text_array.length(); i++) {
                    weekdays[i] = weekday_text_array.getString(i);
                }
                openingHours = convertArrayToString(weekdays);

            } else
                openingHours = "";

            if (resultPlaceDetails.has(RATING))
                rating = resultPlaceDetails.getDouble(RATING);
            else
                rating = 0.0;

            if (resultPlaceDetails.has(WEBSITE))
                website = resultPlaceDetails.getString(WEBSITE);
            else
                website = "";

            if (resultPlaceDetails.has(URL))
                url = resultPlaceDetails.getString(URL);
            else
                url = "";

            ContentValues placeDetailContentValue = new ContentValues();
            placeDetailContentValue.put(PlacesContract.PlaceDetailEntry.COLUMN_PLACE_ID, place_id);
            placeDetailContentValue.put(PlacesContract.PlaceDetailEntry.COLUMN_PHONE_NUMBER, phone_number);
            placeDetailContentValue.put(PlacesContract.PlaceDetailEntry.COLUMN_OPENING_HOURS, openingHours);
            placeDetailContentValue.put(PlacesContract.PlaceDetailEntry.COLUMN_WEBSITE, website);
            placeDetailContentValue.put(PlacesContract.PlaceDetailEntry.COLUMN_URL, url);

            //Insert data into database
            context.getContentResolver().insert(PlacesContract.PlaceDetailEntry.CONTENT_URI, placeDetailContentValue);
            Log.d(TAG, "Data inserted successfully");

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    public static String convertArrayToString(String[] array) {
        String str = "";
        for (int i = 0; i < array.length; i++) {
            str = str + array[i];
            // Do not append comma at the end of last element
            if (i < array.length - 1) {
                str = str + strSeparator;
            }
        }
        return str;
    }

}
