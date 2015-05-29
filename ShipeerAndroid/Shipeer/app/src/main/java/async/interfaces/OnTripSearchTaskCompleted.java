package async.interfaces;

import java.util.ArrayList;

import model.Trip;

/**
 * Created by mifercre on 25/03/15.
 */
public interface OnTripSearchTaskCompleted {
    void onTripSearchTaskCompleted(ArrayList<Trip> result);
}
