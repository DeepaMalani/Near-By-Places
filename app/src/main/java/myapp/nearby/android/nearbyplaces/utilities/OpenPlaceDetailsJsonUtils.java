package myapp.nearby.android.nearbyplaces.utilities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import myapp.nearby.android.nearbyplaces.data.PlacesContract;

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
        final String REVIEWS = "reviews";
        final String URL = "url";
        final String WEBSITE = "website";
        final String AUTHOR_NAME= "author_name";
        final String REVIEW_TIME ="relative_time_description";
        final String REVIEW_DESCRIPTION ="text";

        try {
            String place_id;
            String phone_number;
            //double rating;
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
            Uri insertedUri = context.getContentResolver().insert(PlacesContract.PlaceDetailEntry.CONTENT_URI, placeDetailContentValue);
            // The resulting URI contains the ID for the row.  Extract the placeDetailsId from the Uri.
           long placeDetailsId = ContentUris.parseId(insertedUri);

            if(resultPlaceDetails.has(REVIEWS))
            {
                JSONArray reviewsArray = resultPlaceDetails.getJSONArray(REVIEWS);
                String authorName;
                double rating;
                String reviewTimeDescription;
                String reviewDescription;

                // Insert the new review information into the database
                Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewsArray.length());

                for (int i = 0; i < reviewsArray.length(); i++) {
                    JSONObject review = reviewsArray.getJSONObject(i);
                    authorName = review.getString(AUTHOR_NAME);
                    rating = review.getDouble(RATING);
                    reviewTimeDescription = review.getString(REVIEW_TIME);
                    reviewDescription = review.getString(REVIEW_DESCRIPTION);

                    ContentValues reviewValues = new ContentValues();
                    reviewValues.put(PlacesContract.PlaceReviewsEntry.COLUMN_PLACE_DETAILS_KEY,placeDetailsId);
                    reviewValues.put(PlacesContract.PlaceReviewsEntry.COLUMN_AUTHOR_NAME,authorName);
                    reviewValues.put(PlacesContract.PlaceReviewsEntry.COLUMN_RATINGS,rating);
                    reviewValues.put(PlacesContract.PlaceReviewsEntry.COLUMN_REVIEW_TIME,reviewTimeDescription);
                    reviewValues.put(PlacesContract.PlaceReviewsEntry.COLUMN_REVIEW_DESCRIPTION,reviewDescription);
                   cVVector.add(reviewValues);
                }
                int inserted = 0;
                // add to database
                if ( cVVector.size() > 0 ) {
                    // BulkInsert to add the reviewsEntries to the database.
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = context.getContentResolver().bulkInsert(PlacesContract.PlaceReviewsEntry.CONTENT_URI, cvArray);
                }

            }

        } catch (JSONException e) {

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
