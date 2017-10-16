package myapp.nearby.android.nearbyplaces.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import java.net.URL;

import myapp.nearby.android.nearbyplaces.R;
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
    private String mLocation;
    private boolean mTotalRecords;

    public FetchPlaces(Context context, String type, String location) {
        mContext = context;
        mType = type;
        mLocation = location;
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
        URL placesRequestUrl = NetworkUtils.buildUrl(mContext, mType, mLocation);
        try {
            String jsonPlacesResponse = NetworkUtils
                    .getResponseFromHttpUrl(placesRequestUrl);

            mTotalRecords = OpenPlacesJsonUtils.getPlacesDataFromJson(jsonPlacesResponse, mContext, mType);
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
            Toast.makeText(mContext, mContext.getString(R.string.no_data), Toast.LENGTH_SHORT).show();
            ViewNearByFragment.mLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }
}

