package model;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shipeer.app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import async.PublishTripTask;

/**
 * Created by mifercre on 31/03/15.
 */
public class OwnTripsListAdapter extends BaseAdapter {

    private Activity activity;
    private Trip tempTrip;
    private ArrayList<Trip> trips;

    public OwnTripsListAdapter(Activity activity, ArrayList<Trip> trips){
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
        public RelativeLayout tripRowRelativeLayout;
        public TextView tripCityFromTextView;
        public TextView tripCityToTextView;
        public TextView tripTypeTextView;
        public TextView tripDateTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        //LayoutInflater inflater = activity.getLayoutInflater();

        tempTrip = trips.get(position);

        if(view == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.own_trips_row, null);
            holder = new ViewHolder();

            holder.tripRowRelativeLayout = (RelativeLayout) view.findViewById(R.id.own_trip_row_relativeLayout);
            holder.tripCityFromTextView = (TextView) view.findViewById(R.id.trip_city_from_textView);
            holder.tripCityToTextView = (TextView) view.findViewById(R.id.trip_city_to_textView);
            holder.tripTypeTextView = (TextView) view.findViewById(R.id.trip_type_textView);
            holder.tripDateTextView = (TextView) view.findViewById(R.id.date_textView);

            view.setTag(holder);
        } else {
            holder=(ViewHolder) view.getTag();
            Log.d("tempValues", tempTrip.getCityFrom().getName());
        }

        if(tempTrip != null){
            if(tempTrip.getCityFrom() != null) holder.tripCityFromTextView.setText(tempTrip.getCityFrom().getName());
            if(tempTrip.getCityTo() != null) holder.tripCityToTextView.setText(tempTrip.getCityTo().getName());
            switch (tempTrip.getType()) {
                case PublishTripTask.SIMPLE_GO_TRIP:
                    holder.tripTypeTextView.setText(activity.getString(R.string.simple_go_trip));
                    break;
                case PublishTripTask.SIMPLE_GO_AND_BACK_TRIP:
                    holder.tripTypeTextView.setText(activity.getString(R.string.simple_go_back_trip));
                    break;
                case PublishTripTask.RECURRENT_GO_TRIP:
                    holder.tripTypeTextView.setText(activity.getString(R.string.recurrent_go_trip));
                    break;
                case PublishTripTask.RECURRENT_GO_AND_BACK_TRIP:
                    holder.tripTypeTextView.setText(activity.getString(R.string.recurrent_go_back_trip));
                    break;
            }
            if(tempTrip.getDepartureDateAndroid() != null) {
                Date androidDepartureDate = MySimpleDateFormat.parseAndroidDate(tempTrip.getDepartureDateAndroid());
                holder.tripDateTextView.setText(MySimpleDateFormat.formatAndroidDateTime(androidDepartureDate));

                Calendar cal = Calendar.getInstance();
                Date today = cal.getTime();
                if(tempTrip.getType() == 0) {
                    if(androidDepartureDate.compareTo(today) < 0) holder.tripRowRelativeLayout.setBackgroundColor(activity.getResources().getColor(android.R.color.darker_gray));
                }
                else {
                    Date androidReturnDate = MySimpleDateFormat.parseAndroidDate(tempTrip.getReturnDateAndroid());
                    if(androidReturnDate.compareTo(today) < 0) holder.tripRowRelativeLayout.setBackgroundColor(activity.getResources().getColor(android.R.color.darker_gray));
                }
            }

        }

        return view;
    }
}
