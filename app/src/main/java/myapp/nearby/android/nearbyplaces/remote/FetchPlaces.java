package myapp.nearby.android.nearbyplaces.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import java.net.URL;

import myapp.nearby.android.nearbyplaces.data.PlacesContract;
import myapp.nearby.android.nearbyplaces.ui.ViewNearByFragment;
import myapp.nearby.android.nearbyplaces.utilities.NetworkUtils;
import myapp.nearby.android.nearbyplaces.utilities.OpenPlacesJsonUtils;

/**
 * Created by Deep on 9/1/2017.
 */

public class FetchPlaces extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = FetchPlaces.class.getSimpleName();
    private Context mContext;
    private String mType;
    private String mKeyword;
    private String mLocation;
    private double mLatitude;
    private double mLongitude;
    private boolean mTotalRecords;

    public FetchPlaces(Context context, String type,String keyword, String location, double latitude,double longitude) {
        mContext = context;
        mType = type;
        mKeyword = keyword;
        mLocation = location;
        mLatitude = latitude;
        mLongitude = longitude;
    }

//    @Override
//    protected Void doInBackground(Void... voids) {
//        URL placesRequestUrl = NetworkUtils.buildUrl(mContext, mType, mLocation);
//        try {
//            String jsonPlacesResponse = NetworkUtils
//                    .getResponseFromHttpUrl(placesRequestUrl);
//
//         mtTotalRecords = OpenPlacesJsonUtils.getPlacesDataFromJson(jsonPlacesResponse, mContext, mType);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return null;
//    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        URL placesRequestUrl = NetworkUtils.buildUrl( mType,mKeyword, mLocation);
        try {

            // delete  old data based on place type
            mContext.getContentResolver().delete(PlacesContract.PlaceEntry.CONTENT_URI, PlacesContract.PlaceEntry.COLUMN_PLACE_TYPE + " = ?", new String[]{mType});

            String jsonPlacesResponse = NetworkUtils
                    .getResponseFromHttpUrl(placesRequestUrl);

            String nextPageToken = OpenPlacesJsonUtils.getNextPageTokenFromJson(jsonPlacesResponse);

//            if(!nextPageToken.equals(""))
//            {
//                //Log.d(TAG, "NextPageToken: " + nextPageToken);
//
//                URL nextPageplacesRequestUrl = NetworkUtils.buildUrlWithNextPageToken( mType,mKeyword, mLocation,nextPageToken);
//
//
//                Thread.sleep(2000);
//                         /*Since the token can be used after a short time it has been  generated*/
//
//                String jsonPlacesResponse2 = NetworkUtils
//                        .getResponseFromHttpUrl(nextPageplacesRequestUrl);
//
//                //Log.d(TAG,"response2: "+ jsonPlacesResponse2);
//
//                mTotalRecords = OpenPlacesJsonUtils.getPlacesDataFromJson(jsonPlacesResponse2, mContext, mType,mLatitude,mLongitude);
//            }

            mTotalRecords = OpenPlacesJsonUtils.getPlacesDataFromJson(jsonPlacesResponse, mContext, mType,mLatitude,mLongitude);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return mTotalRecords;
    }


    @Override
    protected void onPostExecute(Boolean result) {

     mTotalRecords = result.booleanValue();
        if(!mTotalRecords) {

//            ViewNearByFragment.mLoadingIndicator.setVisibility(View.INVISIBLE);
//            ViewNearByFragment.mTextViewNoResults.setText(mContext.getString(R.string.no_data));
//            ViewNearByFragment.mTextViewNoResults.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ViewNearByFragment.mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        ViewNearByFragment.mLoadingIndicator.setVisibility(View.VISIBLE);
    }
}

