package myapp.nearby.android.nearbyplaces.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import myapp.nearby.android.nearbyplaces.R;


/**
 * Created by Deep on 8/30/2017.
 */

public class PlacesTypeAdapter extends RecyclerView.Adapter<PlacesTypeAdapter.PlacesTypeAdapterViewHolder> {
    /*
        * An on-click handler that we've defined to make it easy for an Activity to interface with
        * our RecyclerView
        */
    private static PlaceAdapterOnClickHandler mClickHandler;
    private Context mContext;
    private String[] mImageTitle;
    private String[] mPlaceType;
    private String[] mPlaceKeyword;
    private TypedArray mImageId;


    public PlacesTypeAdapter(Context context, String[] imageTitle, TypedArray imageId, String[] placeType,String[] placeKeyword) {
        mContext = context;
        mImageTitle = imageTitle;
        this.mImageId = imageId;
        this.mPlaceType = placeType;
        this.mPlaceKeyword = placeKeyword;
    }

    // Define the method that allows the parent activity  to define the listener
    public void setOnItemClickListener(PlaceAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        if (mImageTitle == null) return 0;
        return mImageTitle.length;
    }

    @Override
    public void onBindViewHolder(PlacesTypeAdapterViewHolder placeTypeHolder, int position) {

        String strImageTitle = mImageTitle[position];
        placeTypeHolder.mTextViewPlaceTitle.setText(strImageTitle);
        placeTypeHolder.mImageViewPlaceType.setImageResource(mImageId.getResourceId(position, -1));
        //Set content description for image
        placeTypeHolder.mImageViewPlaceType.setContentDescription(strImageTitle);
    }

    @Override
    public PlacesTypeAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int layoutIdForGridItem = R.layout.places_type_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForGridItem, viewGroup, shouldAttachToParentImmediately);
        return new PlacesTypeAdapterViewHolder(view);
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface PlaceAdapterOnClickHandler {
        void onClick(String placeTitle, String placeType,String placeKeyword);
    }

    /**
     * Cache of the children views for a list item.
     */
    public class PlacesTypeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mTextViewPlaceTitle;
        private final ImageView mImageViewPlaceType;

        public PlacesTypeAdapterViewHolder(View itemView) {
            super(itemView);
            mTextViewPlaceTitle = (TextView) itemView.findViewById(R.id.place_type_title);
            mImageViewPlaceType = (ImageView) itemView.findViewById(R.id.place_type_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String placeTitle = mImageTitle[adapterPosition];
            String placeType = mPlaceType[adapterPosition];
            String placeKeyword = mPlaceKeyword[adapterPosition];
            mClickHandler.onClick(placeTitle, placeType,placeKeyword);
        }
    }
}
