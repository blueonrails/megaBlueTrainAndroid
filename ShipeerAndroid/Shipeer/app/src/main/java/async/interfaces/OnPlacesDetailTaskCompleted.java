package async.interfaces;

import model.City;

/**
 * Created by mifercre on 15/03/15.
 */
public interface OnPlacesDetailTaskCompleted {
    void onPlacesDetailTaskCompleted(int type, City city);
}
