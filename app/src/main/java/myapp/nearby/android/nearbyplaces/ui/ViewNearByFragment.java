package myapp.nearby.android.nearbyplaces.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import myapp.nearby.android.nearbyplaces.R;
import myapp.nearby.android.nearbyplaces.adapter.ViewPlacesAdapter;
import myapp.nearby.android.nearbyplaces.data.PlacesContract;
import myapp.nearby.android.nearbyplaces.remote.FetchPlaces;
import myapp.nearby.android.nearbyplaces.utilities.NetworkUtils;
import myapp.nearby.android.nearbyplaces.widgets.UpdateWidgetService;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Deep on 9/1/2017.
 */

public class ViewNearByFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String PLACE_ID = "place_id";
    public static final String PLACE_NAME = "place_name";
    public static final String PHOTO_REFERENCE = "photo_reference";
    public static final String PLACE_Address = "place_address";
    public static final String ICON_PATH = "icon_path";
    private static final String TAG = ViewNearByFragment.class.getSimpleName();
    private static final int PLACE_LOADER_ID = 0;
    public static String PLACE_ID_PREF_NAME = "place_id_pref";
    final int WHAT = 1;
    public String mfirstPlaceId;
    public String mfirstPlaceName;
    public String mfirstPhoto_reference;
    public String mFirstPlaceAddress;
    private boolean mSaveInstance = false;
    public static String mNextPageToken;

    @BindView(R.id.recycler_view_near_by)
    RecyclerView mRecyclerView;
//    @BindView(R.id.button_browse_more)
//    Button mButtonBrowseMore;

    public static ProgressBar mLoadingIndicator;
    public static TextView mTextViewNoResults;

    // Define a new interface OnPlaceNameClickListener that triggers a callback in the host activity
    OnPlaceNameClickListener mCallback;
    // Define a new interface DisplayFirstRecord that triggers a callback in the host activity
    DisplayFirstRecord mDisplayFirstRecord;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT)
                mDisplayFirstRecord.replaceFirstRecordFragment(mfirstPlaceId, mfirstPlaceName, mfirstPhoto_reference,mFirstPlaceAddress);

        }
    };
    private ViewPlacesAdapter mViewPlacesAdapter;
    private String mPlaceType;
    private String mPlaceKeyword;
    private String mTitle;
    private String mLocation;
    private boolean mTwoPane = true;
    private double mLatitude;
    private double mLongitude;
    private boolean mDataDate;

    public ViewNearByFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_near_by, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            mSaveInstance = savedInstanceState.getBoolean("SaveInstance");
            mNextPageToken = savedInstanceState.getString("NextPageToken");

        }
        else
            mNextPageToken = "";

        mLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.pb_loading_indicator);
        mTextViewNoResults = (TextView) rootView.findViewById(R.id.text_view_no_result);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MainActivity.TITLE) && intent.hasExtra(MainActivity.TYPE)) {
            mPlaceType = intent.getStringExtra(MainActivity.TYPE);
            mPlaceKeyword = intent.getStringExtra(MainActivity.KEYWORD);
            mTitle = intent.getStringExtra(MainActivity.TITLE);
            mLatitude = intent.getDoubleExtra(MainActivity.LATITUDE, 0.0);
            mLongitude = intent.getDoubleExtra(MainActivity.LONGITUDE, 0.0);
            mLocation = String.valueOf(mLatitude) + "," + String.valueOf(mLongitude);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            //RecipeStepsAdapter is  for linking the data with recycler views
            mViewPlacesAdapter = new ViewPlacesAdapter(getContext(), null);


            showLoading();
            //Attach adapter to recycler
            mRecyclerView.setAdapter(mViewPlacesAdapter);
            mViewPlacesAdapter.setOnItemClickListener(new ViewPlacesAdapter.ViewPlacesAdapterOnClickHandler() {
                @Override
                public void onClick(String placeId, String placeName, String photo_reference, String placeAddress) {
                    // Trigger the callback method and pass in the position that was clicked
                    mCallback.onPlaceNameSelected(placeId, placeName, photo_reference,placeAddress);
                    savePlaceDetails(placeId, placeName, photo_reference);
                }

                @Override
                public void viewMorePlacesClick(String nextPageToken) {
                    getNextPageData(nextPageToken);
                   // Toast.makeText(getActivity(),"Welcome",Toast.LENGTH_SHORT).show();
                }
            });


        }
        //Set activity title
        getActivity().setTitle(mTitle);

        /* Load the  data if phone is connected to internet. */
            if (NetworkUtils.isOnline(getActivity())) {
                if (!mSaveInstance) {
                        FetchPlaces places = new FetchPlaces(getActivity(), mPlaceType, mPlaceKeyword, mLocation, mLatitude, mLongitude,mNextPageToken,true);
                        places.execute();
                    }
            }

         else {
            showErrorMessage(getString(R.string.network_msg));

        }
        return rootView;
    }


    private void savePlaceDetails(String placeId, String placeName, String photo_refrence) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(PLACE_ID_PREF_NAME, MODE_PRIVATE).edit();
        editor.putString("placeId", placeId);
        editor.putString("placeName", placeName);
        editor.putString("photo_reference", photo_refrence);

        editor.commit();
        UpdateWidgetService.startActionUpdatePlaceWidgets(getActivity());
    }

    /**
     * This method will make the View for the places data visible and hide the error message and
     * loading indicator.

     */
    private void showPlacesDataView() {

        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    /**
     * This method will make the loading indicator visible and hide the weather View and error
     * message.
     */
    private void showLoading() {

        /* Then, hide the  data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initialize loader
        getLoaderManager().initLoader(PLACE_LOADER_ID, null, this);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putBoolean("SaveInstance", true);
        currentState.putString("NextPageToken" , mNextPageToken);
    }

    /**
     * This method will make the error message visible and hide the recipe list
     * View.
     */
    private void showErrorMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        mLoadingIndicator.setVisibility(View.INVISIBLE);

    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnPlaceNameClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnPlaceNameClickListener");
        }

        try {
            mDisplayFirstRecord = (DisplayFirstRecord) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement firstrecord");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri placeUri = PlacesContract.PlaceEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                placeUri,
                null,
                PlacesContract.PlaceEntry.COLUMN_PLACE_TYPE + " = ?",
                new String[]{mPlaceType},
                PlacesContract.PlaceEntry.COLUMN_DISTANCE);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mViewPlacesAdapter.swapCursor(data);

        if (data != null) {
              if (data.getCount() != 0) {
                showPlacesDataView();
                data.moveToFirst();
                mfirstPlaceId = data.getString(ViewNearByPlacesActivity.INDEX_PLACE_ID);
                mfirstPlaceName = data.getString(ViewNearByPlacesActivity.INDEX_PLACE_NAME);
                mfirstPhoto_reference = data.getString(ViewNearByPlacesActivity.INDEX_PHOTO_REFERENCE);
                mFirstPlaceAddress = data.getString(ViewNearByPlacesActivity.INDEX_PLACE_ADDRESS);
                  handler.sendEmptyMessage(WHAT);
                  mViewPlacesAdapter.setNextPageToken(mNextPageToken);

//                  if(!mNextPageToken.equals("")) {
//                      mButtonBrowseMore.setVisibility(View.VISIBLE);
//                      mButtonBrowseMore.setOnClickListener(new View.OnClickListener() {
//                          @Override
//                          public void onClick(View view) {
//                              getNextPageData();
//                          }
//                      });
//                  }
//                  else
//                      mButtonBrowseMore.setVisibility(View.GONE);

              }
        }

    }
    private void getNextPageData(String nextPageToken)
    {
        if (NetworkUtils.isOnline(getActivity())) {
            FetchPlaces places = new FetchPlaces(getActivity(), mPlaceType, mPlaceKeyword, mLocation, mLatitude, mLongitude,nextPageToken,false);
            places.execute();

        }
        else {
            showErrorMessage(getString(R.string.network_msg));

        }


      getLoaderManager().restartLoader(PLACE_LOADER_ID, null, this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mViewPlacesAdapter.swapCursor(null);
    }

    // OnPlaceNameClickListener interface, calls a method in the host activity
    public interface OnPlaceNameClickListener {
        void onPlaceNameSelected(String placeId, String placeName, String photo_reference,String placeAddress);
    }


    // OnPlaceNameClickListener interface, calls a method in the host activity
    public interface DisplayFirstRecord {
        void replaceFirstRecordFragment(String placeId, String placeName, String photo_reference,String placeAddress);
    }


}
