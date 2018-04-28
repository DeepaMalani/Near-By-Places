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

public class FetchPlaces extends AsyncTask<Void, Void, String[]> {

    private static final String TAG = FetchPlaces.class.getSimpleName();
    private Context mContext;
    private String mType;
    private String mKeyword;
    private String mLocation;
    private double mLatitude;
    private double mLongitude;
   // private String mMessage;
   private  String[] mReturnData;
    private String mNextPageToken;
    private  boolean mIsFirstCall;
    public FetchPlaces(Context context, String type,String keyword, String location, double latitude,double longitude,String nextPageToken,boolean isFirstCall) {
        mContext = context;
        mType = type;
        mKeyword = keyword;
        mLocation = location;
        mLatitude = latitude;
        mLongitude = longitude;
        mNextPageToken = nextPageToken;
        mIsFirstCall = isFirstCall;
    }



    @Override
    protected String[] doInBackground(Void... voids) {
        if(mIsFirstCall)
            // delete  old data based on place type
            mContext.getContentResolver().delete(PlacesContract.PlaceEntry.CONTENT_URI, PlacesContract.PlaceEntry.COLUMN_PLACE_TYPE + " = ?", new String[]{mType});

        URL placesRequestUrl;
        if(mNextPageToken.equals(""))
            placesRequestUrl = NetworkUtils.buildUrl(mType, mKeyword, mLocation);
        else
            placesRequestUrl = NetworkUtils.buildUrlWithNextPageToken(mType,mKeyword,mLocation,mNextPageToken);
        try {


            String jsonPlacesResponse = NetworkUtils
                    .getResponseFromHttpUrl(placesRequestUrl);

            mReturnData = OpenPlacesJsonUtils.getPlacesDataFromJson(jsonPlacesResponse, mContext, mType,mLatitude,mLongitude);
        } catch (Exception e) {
            e.printStackTrace();
           // return e.getMessage().toString();
        }
        return mReturnData;
    }


    @Override
    protected void onPostExecute(String[] resultMessage) {
        if(resultMessage!=null) {
            if (!resultMessage[0].equals("")) {

                ViewNearByFragment.mLoadingIndicator.setVisibility(View.INVISIBLE);
                ViewNearByFragment.mTextViewNoResults.setText(resultMessage[0]);
                ViewNearByFragment.mTextViewNoResults.setVisibility(View.VISIBLE);
            }

            ViewNearByFragment.mNextPageToken = resultMessage[1].toString();
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

