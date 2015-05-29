package async.interfaces;

import java.util.ArrayList;

import model.Trip;

/**
 * Created by mifercre on 06/05/15.
 */
public interface OnUserTripsTaskCompleted {
    void onUserTripsTaskCompleted(ArrayList<Trip> result);
}
