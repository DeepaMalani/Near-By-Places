package myapp.nearby.android.nearbyplaces.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import myapp.nearby.android.nearbyplaces.ui.ViewNearByPlacesActivity;
import myapp.nearby.android.nearbyplaces.utilities.NetworkUtils;

/**
 * Created by Deep on 9/1/2017.
 */

public class ViewPlacesAdapter extends RecyclerView.Adapter<ViewPlacesAdapter.ViewPlacesAdapterViewHolder> {

    /*
      * An on-click handler that we've defined to make it easy for an Activity to interface with
      * our RecyclerView
      */
    private static ViewPlacesAdapterOnClickHandler mClickHandler;
    private Context mContext;
    private Cursor mCursor;
    private SparseBooleanArray selectedItems;

    public ViewPlacesAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;

    }

    // Define the method that allows the parent activity  to define the listener
    public void setOnItemClickListener(ViewPlacesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public ViewPlacesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int layoutIdForGridItem = myapp.nearby.android.nearbyplaces.R.layout.list_places_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForGridItem, viewGroup, shouldAttachToParentImmediately);
        return new ViewPlacesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewPlacesAdapterViewHolder viewPlacesAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);
        String placeName = mCursor.getString(ViewNearByPlacesActivity.INDEX_PLACE_NAME);
        viewPlacesAdapterViewHolder.mPlaceName.setText(placeName);

        String placeAddress = mCursor.getString(ViewNearByPlacesActivity.INDEX_PLACE_ADDRESS);
        viewPlacesAdapterViewHolder.mAddress.setText(placeAddress);

        //Set rating for rating bar
        float placeRating = mCursor.getFloat(ViewNearByPlacesActivity.INDEX_RATING);
        viewPlacesAdapterViewHolder.mRatingBar.setRating(placeRating);
        //Set rating for textview
        if (placeRating != 0.0)
            viewPlacesAdapterViewHolder.mRatingTextView.setText(String.valueOf(placeRating));
        else
            viewPlacesAdapterViewHolder.mRatingTextView.setText("");

        String photoReference = mCursor.getString(ViewNearByPlacesActivity.INDEX_PHOTO_REFERENCE);
        //String iconPath = mCursor.getString(ViewNearByPlacesActivity.INDEX_ICON_PATH);
        final String IMAGE_BASE_URL;
        if (photoReference.equals("")) {
            viewPlacesAdapterViewHolder.mPlaceImage.setImageResource(myapp.nearby.android.nearbyplaces.R.drawable.imagenotavailable);

        } else {
            IMAGE_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
            Picasso
                    .with(mContext)
                    .load(NetworkUtils.buildPhotoUrl(IMAGE_BASE_URL, photoReference, "500", "500"))
                    .fit()
                    .into(viewPlacesAdapterViewHolder.mPlaceImage);
        }
        //Set content description for place image
        viewPlacesAdapterViewHolder.mPlaceImage.setContentDescription(placeName);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        Log.d("Count", String.valueOf(mCursor.getCount()));
        return mCursor.getCount();
    }

    /**
     * Swaps the cursor used by the Adapter for its place data. This method is called by
     * Activity after a load has finished, as well as when the Loader responsible for loading
     * the  data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newCursor the new cursor to use as Adapter's data source
     */
    public void swapCursor(Cursor newCursor) {
        if (newCursor != null) {
            mCursor = newCursor;
            notifyDataSetChanged();
        }
    }

    public String getFirstRecord(Cursor newCursor) {
        newCursor.moveToFirst();
        String placeId = newCursor.getString(ViewNearByPlacesActivity.INDEX_PLACE_ID);
        return placeId;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface ViewPlacesAdapterOnClickHandler {
        void onClick(String placeId, String placeName, String photo_reference);
    }

    /**
     * Cache of the children views for a list item.
     */
    public class ViewPlacesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final LinearLayout mlayoutPlaceItem;
        private final TextView mPlaceName;
        private final TextView mAddress;
        private final TextView mOpenNow;
        private final ImageView mPlaceImage;
        private final TextView mRatingTextView;
        private final RatingBar mRatingBar;

        public ViewPlacesAdapterViewHolder(View itemView) {
            super(itemView);
            mlayoutPlaceItem = (LinearLayout) itemView.findViewById(myapp.nearby.android.nearbyplaces.R.id.layout_place_item);
            mPlaceName = (TextView) itemView.findViewById(myapp.nearby.android.nearbyplaces.R.id.text_view_name);
            mAddress = (TextView) itemView.findViewById(myapp.nearby.android.nearbyplaces.R.id.text_view_address);
            mOpenNow = (TextView) itemView.findViewById(myapp.nearby.android.nearbyplaces.R.id.text_view_open_now);
            mPlaceImage = (ImageView) itemView.findViewById(myapp.nearby.android.nearbyplaces.R.id.image_view_place);
            mRatingTextView = (TextView) itemView.findViewById(myapp.nearby.android.nearbyplaces.R.id.text_view_rating);
            mRatingBar = (RatingBar) itemView.findViewById(myapp.nearby.android.nearbyplaces.R.id.rating_bar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int adapterPosition = getAdapterPosition();

            mCursor.moveToPosition(adapterPosition);
            String placeId = mCursor.getString(ViewNearByPlacesActivity.INDEX_PLACE_ID);
            String placeName = mCursor.getString(ViewNearByPlacesActivity.INDEX_PLACE_NAME);
            String photo_reference = mCursor.getString(ViewNearByPlacesActivity.INDEX_PHOTO_REFERENCE);
            // String icon_path  = mCursor.getString(ViewNearByPlacesActivity.INDEX_ICON_PATH);
            mClickHandler.onClick(placeId, placeName, photo_reference);


        }

    }

}
