package model;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.android.Utils;
import com.shipeer.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mifercre on 31/03/15.
 */
public class SearchResultsListAdapter extends BaseAdapter {

    private Activity activity;
    private Trip tempTrip;
    private ArrayList<Trip> trips;

    private Cloudinary cloudinary;

    public SearchResultsListAdapter(Activity activity, ArrayList<Trip> trips){
        super();
        this.activity = activity;
        this.trips = trips;
    }

    @Override
    public int getCount() {
        return trips.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView carrierNameTextView;
        public TextView tripCityFromTextView;
        public TextView tripCityToTextView;
        public ImageView carrierProfilePicImageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        //LayoutInflater inflater = activity.getLayoutInflater();

        tempTrip = trips.get(position);

        if(view == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.search_result_row, null);
            holder = new ViewHolder();

            holder.carrierNameTextView = (TextView) view.findViewById(R.id.driver_name_textView);
            holder.tripCityFromTextView = (TextView) view.findViewById(R.id.trip_city_from_textView);
            holder.tripCityToTextView = (TextView) view.findViewById(R.id.trip_city_to_textView);

            holder.carrierProfilePicImageView = (ImageView) view.findViewById(R.id.driver_profile_pic_imageView);
            view.setTag(holder);
        } else {
            holder=(ViewHolder) view.getTag();
            Log.d("tempValues", tempTrip.getCityFrom().getName());
        }

        if(tempTrip != null){
            if(tempTrip.getCarrierName() != null) holder.carrierNameTextView.setText(tempTrip.getCarrierName());
            else holder.carrierNameTextView.setVisibility(View.GONE);
            if(tempTrip.getCityFrom() != null) holder.tripCityFromTextView.setText(tempTrip.getCityFrom().getName());
            if(tempTrip.getCityTo() != null) holder.tripCityToTextView.setText(tempTrip.getCityTo().getName());
            String picId = tempTrip.getCarrierPictureId();
            String picVersion = tempTrip.getCarrierPictureVersion();
            if(picId != null && !picId.isEmpty() && picVersion != null && !picVersion.isEmpty()) {
                cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(activity));
                String url = cloudinary.url().transformation(new Transformation().width(200).height(200).crop("fill")).version(picVersion).generate(picId);
                Log.d("URL", url);
                holder.carrierProfilePicImageView.setImageResource(0);
                holder.carrierProfilePicImageView.setImageDrawable(null);
                Picasso.with(activity).load(url).transform(new CircleTransform()).into(holder.carrierProfilePicImageView);
            }
        }

        return view;
    }
}
