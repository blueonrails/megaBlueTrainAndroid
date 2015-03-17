package async;

import java.util.ArrayList;

import model.City;

/**
 * Created by mifercre on 15/03/15.
 */
public interface OnPlacesTaskCompleted {
    void onPlacesTaskCompleted(ArrayList<City> result);
}
