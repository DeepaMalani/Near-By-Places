package myapp.nearby.android.nearbyplaces.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myapp.nearby.android.nearbyplaces.R;
import myapp.nearby.android.nearbyplaces.data.PlacesContract;
import myapp.nearby.android.nearbyplaces.remote.FetchPlaceDetails;
import myapp.nearby.android.nearbyplaces.utilities.NetworkUtils;


/**
 * Created by Deep on 9/6/2017.
 */

public class PlaceDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

  private static final int PLACE_DETAILS_LOADER_ID = 0;
    public static String strSeparator = "__,__";
    public String mPlaceId = "";
    @BindView(R.id.scrollview)
    NestedScrollView mScrollView;
    @BindView(R.id.lable_phone_number)
    TextView mLabelPhoneNumber;
    @BindView(R.id.linear_layout_call)
    LinearLayout mLinearLayoutCall;
    @BindView(R.id.text_view_phone_number)
    TextView mTextViewPhoneNumber;
    @BindView(R.id.phone_number_divider)
    View mPhoneNumberView;
    @BindView(R.id.website_divider)
    View mWebsiteView;
    @BindView(R.id.linear_layout_website)
    LinearLayout mLinearLayoutWebsite;
    @BindView(R.id.text_view_website)
    TextView mTextViewWebsite;
    @BindView(R.id.linear_layout_directions)
    LinearLayout mLinearLayoutDirections;
    @BindView(R.id.place_image)
    ImageView mImageViewPlace;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.layout_hours)
    LinearLayout mLinearLayoutHours;
    @BindView(R.id.lable_hours)
    TextView mTextViewHours;
    @BindView(R.id.opening_hours_divider)
    View mOpeningHoursView;
    @BindView(R.id.text_view_review_header)
    TextView mTextViewReviewHeader;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFabShare;
    @BindView(R.id.layout_review)
    LinearLayout mReviewLayout;
    String mUrl;
    String mWebsite;
    private String mPhoto_reference;
    private String mPlaceAddress;
    private String mPlaceName;
    private Unbinder unbinder;
    private boolean mTwoPane = false;

    private static final int REQUEST_CALL_PHONE = 100;

    public PlaceDetailsFragment() {
        super();
    }

    public static String[] convertStringToArray(String str) {
        String[] arr = str.split(strSeparator);
        return arr;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_place_details, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        // Load the saved state if there is one
        if (savedInstanceState != null) {
            mPlaceId = savedInstanceState.getString(ViewNearByFragment.PLACE_ID);
            mPlaceName = savedInstanceState.getString(ViewNearByFragment.PLACE_NAME);
            mPhoto_reference = savedInstanceState.getString(ViewNearByFragment.PHOTO_REFERENCE);
            mPlaceAddress = savedInstanceState.getString(ViewNearByFragment.PLACE_Address);
        }
        if (mPhoto_reference != null)
            bindPlaceImage();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();

                }
            });
        }
        if (mTwoPane) {
            mToolbar.setNavigationIcon(null);
        }
        mCollapsingToolbar.setTitle(mPlaceName);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initialize loader
        getLoaderManager().initLoader(PLACE_DETAILS_LOADER_ID, null, this);

    }

    @Override
    public void onStart() {
        super.onStart();


        if (!mPlaceId.equals("")) {
            //Fetch data if it's not already inserted in database.
            Cursor cursor = getActivity().getContentResolver().query
                    (PlacesContract.PlaceDetailEntry.CONTENT_URI,
                            null,
                            PlacesContract.PlaceDetailEntry.COLUMN_PLACE_ID + " = ?",
                            new String[]{mPlaceId},
                            null);

            int countRows = cursor.getCount();


            if (countRows == 0) {
                if (NetworkUtils.isOnline(getActivity())) {
               /* Load the  data if phone is connected to internet. */
                    FetchPlaceDetails fetchPlaceDetails = new FetchPlaceDetails(getActivity(), mPlaceId);
                    fetchPlaceDetails.execute();
                } else {
                    showErrorMessage(getString(R.string.network_msg));
                }
            }
        }
    }


    /**
     * This method will make the error message visible and hide the recipe list
     * View.
     */
    private void showErrorMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

    }

    private void bindPlaceImage() {
        final String IMAGE_BASE_URL;

        if (mPhoto_reference.equals("")) {

            mImageViewPlace.setImageResource(R.drawable.imagenotavailable);

        } else {
            IMAGE_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
            Picasso
                    .with(getContext())
                    .load(NetworkUtils.buildPhotoUrl(IMAGE_BASE_URL, mPhoto_reference, "600", "800"))
                    .fit()
                    .into(mImageViewPlace);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (!mPlaceId.equals("")) {
            Uri placeDetailsUri = PlacesContract.PlaceReviewsEntry.buildPlaceDetailsWithReviews(mPlaceId);
            return new CursorLoader(
                    getActivity(),
                    placeDetailsUri,
                    null,
                    null,
                    new String[]{mPlaceId},
                    null);
        }
        else
            return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(cursor!=null) {
            int length = cursor.getCount();
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (length == 0) {

                //Display only direction, if place detail is not available.
                //  mScrollView.setVisibility(View.GONE);
                mFabShare.setVisibility(View.GONE);

                mLinearLayoutCall.setVisibility(View.GONE);
                mPhoneNumberView.setVisibility(View.GONE);

                mLinearLayoutWebsite.setVisibility(View.GONE);
                mWebsiteView.setVisibility(View.GONE);

                mTextViewHours.setVisibility(View.GONE);
                mLinearLayoutHours.setVisibility(View.GONE);
                mOpeningHoursView.setVisibility(View.GONE);

                mTextViewReviewHeader.setVisibility(View.GONE);
                mReviewLayout.setVisibility(View.GONE);

                mLinearLayoutDirections.setVisibility(View.VISIBLE);
                mLinearLayoutDirections.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openPlaceDirection();
                    }
                });
            } else {
                // mScrollView.setVisibility(View.VISIBLE);

                mFabShare.setVisibility(View.VISIBLE);

                if (cursor.moveToFirst()) {

                    // cursor.moveToFirst();
                    int indexPhoneNumber = cursor.getColumnIndex(PlacesContract.PlaceDetailEntry.COLUMN_PHONE_NUMBER);
                    int indexOpeningHours = cursor.getColumnIndex(PlacesContract.PlaceDetailEntry.COLUMN_OPENING_HOURS);
                    // int indexRating = cursor.getColumnIndex(PlacesContract.PlaceDetailEntry.COLUMN_RATING);
                    int indexWebsite = cursor.getColumnIndex(PlacesContract.PlaceDetailEntry.COLUMN_WEBSITE);
                    int indexUrl = cursor.getColumnIndex(PlacesContract.PlaceDetailEntry.COLUMN_URL);

                    final String phoneNumber = cursor.getString(indexPhoneNumber);
                    //  double rating = cursor.getDouble(indexRating);
                    String website = cursor.getString(indexWebsite);
                    final String url = cursor.getString(indexUrl);

                    String strOpeningHours = cursor.getString(indexOpeningHours);
                    if (strOpeningHours.equals("")) {
                        mTextViewHours.setVisibility(View.GONE);
                        mOpeningHoursView.setVisibility(View.GONE);
                        mLinearLayoutHours.setVisibility(View.GONE);
                    } else {
                        mTextViewHours.setVisibility(View.VISIBLE);
                        mOpeningHoursView.setVisibility(View.VISIBLE);
                        mLinearLayoutHours.setVisibility(View.VISIBLE);

                        LayoutParams lparams = new LayoutParams(
                                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

                        String[] openingHours = convertStringToArray(strOpeningHours);
                        for (final String hour : openingHours) {
                            // fill in opening hours dynamically here

                            TextView tv = new TextView(getActivity());
                            tv.setLayoutParams(lparams);
                            tv.setText(hour);
                            mLinearLayoutHours.addView(tv);

                        }

                    }


                    if (phoneNumber.equals("")) {
                        // mLabelPhoneNumber.setVisibility(View.GONE);
                        mPhoneNumberView.setVisibility(View.GONE);
                        mLinearLayoutCall.setVisibility(View.GONE);

                    } else {
                        mTextViewPhoneNumber.setText(phoneNumber);
                        mLinearLayoutCall.setVisibility(View.VISIBLE);
                        mPhoneNumberView.setVisibility(View.VISIBLE);
                        mLinearLayoutCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                requestCallPhonePermission();
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                                PackageManager packageManager = getActivity().getPackageManager();
                                if (callIntent.resolveActivity(packageManager) != null) {
                                    if (ActivityCompat.checkSelfPermission
                                            (getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        return;
                                    }
                                    startActivity(callIntent);
                                } else {
                                    return;
                                }
                            }
                        });
                    }
                    if (website.equals("")) {
                        mLinearLayoutWebsite.setVisibility(View.GONE);
                        mWebsite = "";
                        mWebsiteView.setVisibility(View.GONE);
                    } else {
                        mWebsite = website;
                        mLinearLayoutWebsite.setVisibility(View.VISIBLE);
                        mWebsiteView.setVisibility(View.VISIBLE);

                        mTextViewWebsite.setText(website);
                        mLinearLayoutWebsite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
                                websiteIntent.setData(Uri.parse(mWebsite));

                                //Check if intent can be handled from activity
                                PackageManager packageManager = getActivity().getPackageManager();
                                if (websiteIntent.resolveActivity(packageManager) != null) {
                                    startActivity(websiteIntent);
                                } else {
                                    return;
                                }
                            }
                        });

                    }

                    if (url.equals("")) {
                        mLinearLayoutDirections.setVisibility(View.GONE);
                    } else {
                        mUrl = url;
                        mLinearLayoutDirections.setVisibility(View.VISIBLE);
                        mLinearLayoutDirections.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(mUrl));

                                //Check if intent can be handled from activity
                                PackageManager packageManager = getActivity().getPackageManager();
                                if (i.resolveActivity(packageManager) != null) {
                                    startActivity(i);
                                }

                            }
                        });

                        mFabShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                                share.setType("text/plain");
                                share.putExtra(Intent.EXTRA_SUBJECT, "Check out: " + mPlaceName);
                                share.putExtra(Intent.EXTRA_TEXT, url);
                                startActivity(Intent.createChooser(share, "Share link!"));
                            }
                        });
                    }

                    mTextViewReviewHeader.setVisibility(View.VISIBLE);
                    mReviewLayout.setVisibility(View.VISIBLE);

                do {
                    String reviewAuthor = cursor.getString(cursor.getColumnIndex(PlacesContract.PlaceReviewsEntry.COLUMN_AUTHOR_NAME));
                    String reviewText = cursor.getString(cursor.getColumnIndex(PlacesContract.PlaceReviewsEntry.COLUMN_REVIEW_DESCRIPTION));
                    String reviewTime = cursor.getString(cursor.getColumnIndex(PlacesContract.PlaceReviewsEntry.COLUMN_REVIEW_TIME));
                    float placeRating = cursor.getFloat(cursor.getColumnIndex(PlacesContract.PlaceReviewsEntry.COLUMN_RATINGS));

                    View v = inflater.inflate(R.layout.review_list, null);

                    // fill in any details dynamically here
                    TextView textViewReviewAuthor = (TextView) v.findViewById(R.id.text_view_review_author);
                    textViewReviewAuthor.setText(reviewAuthor);

                    TextView textViewReviewText = (TextView) v.findViewById(R.id.text_view_review_text);
                    textViewReviewText.setText(reviewText);

                    //Set rating for rating bar
                    RatingBar ratingBar = (RatingBar) v.findViewById(R.id.rating_bar);
                    ratingBar.setRating(placeRating);

                    TextView textViewReviewTime = (TextView) v.findViewById(R.id.text_view_review_time);
                    textViewReviewTime.setText(reviewTime);


                    mReviewLayout.addView(v);

                } while (cursor.moveToNext());
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void setPlaceDetails(String placeId, String placeName, String photo_reference,String placeAddress) {
        mPlaceId = placeId;
        mPlaceName = placeName;
        mPhoto_reference = photo_reference;
        mPlaceAddress = placeAddress;
    }


    public void setTwoPane(boolean twoPane) {
        mTwoPane = twoPane;
    }

    /**
     * Save the current state of this fragment
     */
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putString(ViewNearByFragment.PLACE_ID, mPlaceId);
        currentState.putString(ViewNearByFragment.PHOTO_REFERENCE, mPhoto_reference);
        currentState.putString(ViewNearByFragment.PLACE_NAME, mPlaceName);
        currentState.putString(ViewNearByFragment.PLACE_Address, mPlaceAddress);
    }

    private void requestCallPhonePermission() {
        String callPermission = Manifest.permission.CALL_PHONE;
        int hasPermission = ContextCompat.checkSelfPermission(getActivity(), callPermission);
        String[] permissions = new String[] { callPermission };
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, REQUEST_CALL_PHONE);
        }
    }
    public void openPlaceDirection()
    {
        Uri uri = Uri.parse("geo:0,0?q=" + mPlaceAddress );
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        if(intent.resolveActivity(getActivity().getPackageManager())!=null)
            startActivity(intent);
    }
}
