package async;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import async.interfaces.OnTripSearchTaskCompleted;
import model.City;
import model.MySimpleDateFormat;
import model.Trip;

/**
 * Created by mifercre on 25/03/15.
 */
public class TripSearchTask extends AsyncTask<String, Void, ArrayList<Trip>> {

    //shipeer-staging.herokuapp.com/api/v1/trips.json?fn=madrid&tn=valencia
    private static final String URL = BaseRequest.BASE_URL + "routes.json";

    private OnTripSearchTaskCompleted listener;

    public TripSearchTask(OnTripSearchTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<Trip> doInBackground(String... form) {
         ArrayList<Trip> res = getRequest(form[0], form[1]);
        return res;
    }

    @Override
    protected void onPostExecute(ArrayList<Trip> result) {
        super.onPostExecute(null);
        listener.onTripSearchTaskCompleted(result);
    }

    private ArrayList<Trip> getRequest(String cityFromName, String cityToName) {
        try {
            //http://shipeer-staging.herokuapp.com/api/v1/trips.json?from[name]=Alicante&from[coordinates]=-0.4906855000000405%7C38.3459963&to[name]=Valencia&to[coordinates]=-0.37632480000002033%7C39.46990359999999
            HttpClient httpclient = new DefaultHttpClient();
            String query = "?from[name]=" + URLEncoder.encode(cityFromName, "utf-8") + "&to[name]=" + URLEncoder.encode(cityToName, "utf-8");
            Log.d("query", query);
            HttpGet httpget = new HttpGet(URL + query);

            HttpResponse response = httpclient.execute(httpget);
            String responseStr = EntityUtils.toString(response.getEntity());
            Log.d("response", responseStr);
            if(response.getStatusLine().getStatusCode() == 200) {
                JSONArray jsonArray = new JSONArray(responseStr);
                ArrayList<Trip> res = new ArrayList<>();

                for(int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);

                    String id = jsonObj.getString("_id");

                    JSONObject tripJsonObj = jsonObj.getJSONObject("trip");
                    JSONObject userJsonObj = tripJsonObj.getJSONObject("owner");

                    boolean userHasFacebook = false;
                    JSONObject userFacebookInfoJsonObj = userJsonObj.getJSONObject("facebook");
                    String userFacebookId = null;
                    if(userFacebookInfoJsonObj.has("id")) userFacebookId = userFacebookInfoJsonObj.getString("id");
                    if(userFacebookId != null && !userFacebookId.isEmpty()) userHasFacebook = true;

                    JSONObject userInfoJsonObj = userJsonObj.getJSONObject("local");
                    String userEmail = URLDecoder.decode(userInfoJsonObj.getString("email"), "utf-8");
                    String userFirstName = URLDecoder.decode(userInfoJsonObj.getString("firstName"), "utf-8");
                    String userLastName = URLDecoder.decode(userInfoJsonObj.getString("lastName"), "utf-8");
                    String userPhone = userInfoJsonObj.getString("phone");

                    String userProfilePictureId = null;
                    String userProfilePictureVersion = null;
                    JSONObject profilePicJsonObj = userInfoJsonObj.getJSONObject("profilePicture");
                    if(!profilePicJsonObj.isNull("id")) {
                        userProfilePictureId = profilePicJsonObj.getString("id");
                    }
                    if(!profilePicJsonObj.isNull("version")) {
                        userProfilePictureVersion = profilePicJsonObj.getString("version");
                    }

                    JSONObject tripFromJsonObj = jsonObj.getJSONObject("from");
                    String cityFromString = URLDecoder.decode(tripFromJsonObj.getString("name"), "utf-8");
                    City cityFrom = new City(cityFromString, tripFromJsonObj.getString("_id"));

                    JSONArray tripFromGeo = tripFromJsonObj.getJSONArray("geo");
                    cityFrom.setLat(tripFromGeo.getDouble(0));
                    cityFrom.setLng(tripFromGeo.getDouble(1));

                    JSONObject tripToJsonObj = jsonObj.getJSONObject("to");
                    String cityToString = URLDecoder.decode(tripToJsonObj.getString("name"), "utf-8");
                    City cityTo = new City(cityToString, tripToJsonObj.getString("_id"));

                    JSONArray tripToGeo = tripToJsonObj.getJSONArray("geo");
                    cityTo.setLat(tripToGeo.getDouble(0));
                    cityTo.setLng(tripToGeo.getDouble(1));

                    String description = URLDecoder.decode(tripJsonObj.getString("description"), "utf-8");

                    JSONArray priceArray = tripJsonObj.getJSONArray("price");
                    String priceSmall = priceArray.getString(0);
                    String priceMedium = priceArray.getString(1);
                    String priceBig = priceArray.getString(2);
                    String priceExtraBig = priceArray.getString(3);

                    String startingAtISO = jsonObj.getString("startingAt"); //"2015-06-17T18:52:40.000Z"
                    String departureDatetimeAndroid = MySimpleDateFormat.convertFromISOtoAndroid(startingAtISO);

                    Trip t = new Trip(id, cityFrom, cityTo, userFirstName + " " + userLastName, userEmail, null, priceSmall, priceMedium, priceBig, priceExtraBig);
                    t.setCarrierPictureId(userProfilePictureId);
                    t.setCarrierPictureVersion(userProfilePictureVersion);
                    t.setDepartureDate(departureDatetimeAndroid);
                    t.setCarrierPhone(userPhone);
                    t.setCarrierHasFacebook(userHasFacebook);
                    t.setDescription(description);

                    JSONObject recurrencyJsonObj = tripJsonObj.getJSONObject("recurrency");

                    String departureDatetime = null;
                    int tripType = tripJsonObj.getInt("type");
                    t.setType(tripType);
                    /**switch(tripType) {
                        case PublishTripTask.SIMPLE_GO_TRIP:
                            departureDatetime = recurrencyJsonObj.getString("departureDatetime");
                            t.setDepartureDate(departureDatetime);
                            break;
                        case PublishTripTask.SIMPLE_GO_AND_BACK_TRIP:
                            departureDatetime = recurrencyJsonObj.getString("departureDatetime");

                            t.setDepartureDate(departureDatetime);
                            break;
                        case PublishTripTask.RECURRENT_GO_TRIP:

                            break;
                        case PublishTripTask.RECURRENT_GO_AND_BACK_TRIP:

                            break;
                    }**/

                    Log.d("TRIP", t.toString());

                    res.add(t);
                }
                return res;
            } else { // STATUS ERROR CODE
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
