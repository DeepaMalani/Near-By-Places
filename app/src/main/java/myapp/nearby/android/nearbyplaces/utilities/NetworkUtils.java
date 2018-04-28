package myapp.nearby.android.nearbyplaces.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import myapp.nearby.android.nearbyplaces.BuildConfig;

/**
 * Created by Deep on 9/1/2017.
 */

public final class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String PLACES_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String PLACE_DETAIL_URL = "https://maps.googleapis.com/maps/api/place/details/json?";

    /**
     * Build Url to get nearby places
     * @param type
     * @param keyword
     * @param location
     * @return
     */
    public static URL buildUrl(String type,String keyword, String location) {


        final String LOCATION = "location";
        // final String RADIUS ="radius";
        final String TYPE = "type";
        final String RANK_BY = "rankby";
        final String KEY_WORD = "keyword";
        final String API_KEY = "key";

        Uri builtUri = Uri.parse(PLACES_BASE_URL).buildUpon()
                 .appendQueryParameter(LOCATION,location)
                .appendQueryParameter("radius","5000")
                  //.appendQueryParameter(TYPE, type)
                 .appendQueryParameter(KEY_WORD, keyword)
                .appendQueryParameter(API_KEY, BuildConfig.places_api_key)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

       // Log.d(TAG,"Url: " + url);
        return url;
    }
    public static URL buildUrlWithNextPageToken(String type,String keyword, String location,String nextPageToken) {


        final String LOCATION = "location";
        // final String RADIUS ="radius";
        final String TYPE = "type";
        final String RANK_BY = "rankby";
        final String KEY_WORD = "keyword";
        final String API_KEY = "key";
        final String PAGE_TOKEN = "pagetoken";

        Uri builtUri = Uri.parse(PLACES_BASE_URL).buildUpon()
                .appendQueryParameter(LOCATION,location)
                .appendQueryParameter("radius","5000")
               // .appendQueryParameter(TYPE, type)
                .appendQueryParameter(KEY_WORD, keyword)
                .appendQueryParameter(API_KEY, BuildConfig.places_api_key)
                .appendQueryParameter(PAGE_TOKEN,nextPageToken)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

      //  Log.d(TAG,"Next Page Url: " + url);
        return url;
    }
    /**
     * Build url to fetch place image
     *
     * @param imageUrl
     * @param photoReference
     * @return
     */
    public static String buildPhotoUrl(String imageUrl, String photoReference, String imageWidth, String imageHeight) {
        final String PHOTO_REFERENCE = "photoreference";
        final String MAX_WIDTH = "maxwidth";
        final String MAX_HEIGHT = "maxheight";
        final String API_KEY = "key";
        Uri builtUri = null;
        String IMAGE_BASE_URL = imageUrl;
        try {


            if (photoReference.equals("")) {
                builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon().build();
            } else {
                builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                        .appendQueryParameter(MAX_WIDTH, imageWidth)
                        .appendQueryParameter(MAX_HEIGHT, imageHeight)
                        .appendQueryParameter(PHOTO_REFERENCE, photoReference)
                        .appendQueryParameter(API_KEY, BuildConfig.places_api_key)
                        .build();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(builtUri);
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static URL buildPlaceDetailUrl(Context context, String placeId) {
        final String PLACE_ID = "placeid";
        final String API_KEY = "key";


        Uri builtUri = Uri.parse(PLACE_DETAIL_URL).buildUpon()
                .appendQueryParameter(PLACE_ID, placeId)
                .appendQueryParameter(API_KEY, BuildConfig.places_api_key)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

       // Log.d(TAG,"PlaceDetailsUrl: " + url);
        return url;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
