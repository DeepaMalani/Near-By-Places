package com.example.android.nearbyplaces.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.nearbyplaces.utilities.NetworkUtils;
import com.example.android.nearbyplaces.utilities.OpenPlaceDetailsJsonUtils;

import java.net.URL;

/**
 * Created by Deep on 9/5/2017.
 */

public class FetchPlaceDetails extends AsyncTask<Void, Void, Void> {


    private static final String TAG = FetchPlaceDetails.class.getSimpleName();
    private Context mContext;
    private String mPlaceId;

    public FetchPlaceDetails(Context context, String placeId) {
        mContext = context;
        mPlaceId = placeId;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        URL placeDetailRequestUrl = NetworkUtils.buildPlaceDetailUrl(mContext, mPlaceId);
        try {
            String jsonPlaceDetailResponse = NetworkUtils
                    .getResponseFromHttpUrl(placeDetailRequestUrl);
            Log.d(TAG, "Results: " + jsonPlaceDetailResponse);
            OpenPlaceDetailsJsonUtils.getPlaceDetailDataFromJson(jsonPlaceDetailResponse, mContext, mPlaceId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


}
