package async;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mifercre on 15/03/15.
 */
public interface OnRouteFromToTaskCompleted {
    void onRouteFromToTaskCompleted(List<List<HashMap<String, String>>> routes);
}
