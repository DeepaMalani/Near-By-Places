package myapp.nearby.android.nearbyplaces.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;

import myapp.nearby.android.nearbyplaces.data.PlacesContract;

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
    public static boolean getPlacesDataFromJson(String placeJsonStr, Context context, String type,double currentLatitude,double currentLongitude)
            throws JSONException {


        // These are the names of the JSON objects that need to be extracted.
        int totalRecords = 0;
        final String NEXT_PAGE_TOKEN = "next_page_token";
        final String PLACES_RESULTS = "results";
        final String PLACE_ID = "place_id";
        final String NAME = "name";
        final String ADDRESS = "vicinity";
        final String RATING = "rating";
        final String OPENING_HOURS = "opening_hours";
        final String OPEN_NOW = "open_now";
        final String PHOTOS = "photos";
        final String PHOTO_REFERENCE = "photo_reference";
        final String  GEOMETRY = "geometry";
        final String LOCATION = "location";
        final String LAT = "lat";
        final String LNG = "lng";


        try {
            JSONObject placesJson = new JSONObject(placeJsonStr);

            if(placesJson.has(NEXT_PAGE_TOKEN))
            {
                String pageToken = placesJson.getString(NEXT_PAGE_TOKEN);
                //Log.d(TAG,"PageToken: " + pageToken);
            }

            JSONArray placesArray = placesJson.getJSONArray(PLACES_RESULTS);

            // delete  old data based on place type
            //context.getContentResolver().delete(PlacesContract.PlaceEntry.CONTENT_URI, PlacesContract.PlaceEntry.COLUMN_PLACE_TYPE + " = ?", new String[]{type});


            totalRecords = placesArray.length();

                for (int i = 0; i < placesArray.length(); i++) {

                    String place_id;
                    String name;
                    String address;
                    boolean open_now;
                    double rating;
                    String photo_reference;
                    String icon;
                    double placeLat;
                    double placeLNG;
                    double distance = 0.0;
                    // Get the JSON object representing the place result
                    JSONObject resultPlace = placesArray.getJSONObject(i);

                    //Get place location
                    if(resultPlace.has(GEOMETRY))
                    {
                        JSONObject resultGeometry = resultPlace.getJSONObject(GEOMETRY);
                        if(resultGeometry.has(LOCATION))
                        {
                            JSONObject resultLocation = resultGeometry.getJSONObject(LOCATION);
                            placeLat = resultLocation.getDouble(LAT);
                            placeLNG = resultLocation.getDouble(LNG);
                            distance = getDistance(currentLatitude,currentLongitude,placeLat,placeLNG);
                        }
                    }

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
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = sdf.format(new Date(System.currentTimeMillis()));

                    //listPlace.add(new Places(type,name,address,open_now,rating,photo_reference,icon));
                    ContentValues placeContentValue = new ContentValues();
                    placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_PLACE_ID, place_id);
                    placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_PLACE_NAME, name);
                    placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_PLACE_TYPE, type);
                    placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_ADDRESS, address);
                    placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_RATING, rating);
                    placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_PHOTO_REFERENCE, photo_reference);
                    placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_DISTANCE,distance);
                    placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_CURRENT_LAT,currentLatitude);
                    placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_CURRENT_LONG,currentLongitude);
                    placeContentValue.put(PlacesContract.PlaceEntry.COLUMN_CURRENT_DATE,currentDate);



               /* Insert our new  data into place ContentProvider */
                    context.getContentResolver().insert(PlacesContract.PlaceEntry.CONTENT_URI, placeContentValue);

                }


        } catch (JSONException e) {

            e.printStackTrace();
        }
        if (totalRecords==0)
            return false;
        else
            return true;

    }

    public static String getNextPageTokenFromJson(String placeJsonStr)
            throws JSONException {

        final String NEXT_PAGE_TOKEN = "next_page_token";
        String pageToken = "";
        try {


            JSONObject placesJson = new JSONObject(placeJsonStr);

            if (placesJson.has(NEXT_PAGE_TOKEN)) {
                 pageToken = placesJson.getString(NEXT_PAGE_TOKEN);

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        return pageToken;
    }

    private static double getDistance(double lat1,double long1,double lat2,double long2)
    {
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(long1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(long2);

        double distanceInMeters = loc1.distanceTo(loc2);
        double distanceInMiles = distanceInMeters/1609.344;
       // double distanceInKm = loc1.distanceTo(loc2)/1000;


        return distanceInMiles;
    }

    }
