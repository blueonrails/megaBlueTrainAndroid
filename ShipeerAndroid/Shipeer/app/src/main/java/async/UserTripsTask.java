package async;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import async.interfaces.OnUserTripsTaskCompleted;
import model.City;
import model.MySimpleDateFormat;
import model.Trip;

/**
 * Created by mifercre on 15/03/15.
 */
public class UserTripsTask extends AsyncTask<String, Void, ArrayList<Trip>> {

    //http://shipeer-staging.herokuapp.com/api/v1/users/55474cb79847300e001c50da/routes.json
    private static final String URL = BaseRequest.BASE_URL + "users/";

    private OnUserTripsTaskCompleted listener;

    public UserTripsTask(OnUserTripsTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<Trip> doInBackground(String... form) {
        ArrayList<Trip> res = getRequest(form);
        return res;
    }

    @Override
    protected void onPostExecute(ArrayList<Trip> result) {
        super.onPostExecute(null);
        listener.onUserTripsTaskCompleted(result);
    }

    private ArrayList<Trip> getRequest(String... form) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(URL + form[0] + "/trips.json");

            HttpResponse response = httpclient.execute(httpGet);
            String responseStr = EntityUtils.toString(response.getEntity());
            Log.d("response", responseStr);
            if(response.getStatusLine().getStatusCode() == 200) {
                JSONArray jsonArray = new JSONArray(responseStr);
                ArrayList<Trip> res = new ArrayList<>();

                for(int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);

                    String id = jsonObj.getString("_id");

                    JSONArray prices = jsonObj.getJSONArray("price");

                    String priceSmall = String.valueOf(prices.get(0));
                    String priceMedium = String.valueOf(prices.get(1));
                    String priceBig = String.valueOf(prices.get(2));
                    String priceExtraBig = String.valueOf(prices.get(3));

                    JSONArray locations = jsonObj.getJSONArray("locations");

                    City cityFrom = null;
                    City cityTo = null;

                    if(locations != null && locations.length() > 0) {
                        JSONObject fromObject = locations.getJSONObject(0);
                        JSONArray tripFromGeo = fromObject.getJSONArray("geo");

                        String cityFromName = URLDecoder.decode(fromObject.getString("name"), "utf-8");
                        Log.d("CITY FROM", cityFromName);
                        cityFrom = new City(cityFromName, fromObject.getString("_id"));
                        cityFrom.setLat(tripFromGeo.getDouble(0));
                        cityFrom.setLng(tripFromGeo.getDouble(1));

                        JSONObject toObject = locations.getJSONObject(locations.length() - 1);
                        JSONArray tripToGeo = toObject.getJSONArray("geo");

                        String cityToName = URLDecoder.decode(toObject.getString("name"), "utf-8");
                        Log.d("CITY TO", cityToName);
                        cityTo = new City(cityToName, toObject.getString("_id"));
                        cityTo.setLat(tripToGeo.getDouble(0));
                        cityTo.setLng(tripToGeo.getDouble(1));
                    }

                    Trip t = new Trip(id, cityFrom, cityTo, null, null, null, priceSmall, priceMedium, priceBig, priceExtraBig);

                    String description = null;
                    if(jsonObj.has("description") && !jsonObj.isNull("description")) description = jsonObj.getString("description");
                    t.setDescription(description);

                    int type = jsonObj.getInt("type");
                    t.setType(type);

                    JSONObject recurrency = jsonObj.getJSONObject("recurrency");

                    if(type == 0) { //GO TRIP
                        String departureDatetimeISO = recurrency.getString("departureDatetime");
                        String departureDatetimeAndroid = MySimpleDateFormat.convertFromISOtoAndroid(departureDatetimeISO);
                        t.setDepartureDate(departureDatetimeAndroid);
                    }
                    else if(type == 1) { //GO AND BACK TRIP
                        String departureDatetimeISO = recurrency.getString("departureDatetime");
                        String departureDatetimeAndroid = MySimpleDateFormat.convertFromISOtoAndroid(departureDatetimeISO);
                        t.setDepartureDate(departureDatetimeAndroid);

                        String returnDatetimeISO = recurrency.getString("returnDatetime");
                        String returnDatetimeAndroid = MySimpleDateFormat.convertFromISOtoAndroid(returnDatetimeISO);
                        t.setReturnDate(returnDatetimeAndroid);
                    }
                    else if(type == 2) { //GO RECURRENT TRIP
                        String departureDatetimeISO = recurrency.getString("departureDatetime");
                        String departureDatetimeAndroid = MySimpleDateFormat.convertFromISOtoAndroid(departureDatetimeISO);
                        t.setDepartureDate(departureDatetimeAndroid);

                        JSONArray departureDaysArray = recurrency.getJSONArray("departureDays");

                        boolean [] departureDays = new boolean[7];
                        for(int j=0; j<departureDaysArray.length(); j++) {
                            departureDays[j] = departureDaysArray.getBoolean(j);
                        }
                        t.setRecurrentGoDays(departureDays);
                    }
                    else if(type == 3) { //GO AND BACK RECURRENT TRIP
                        String departureDatetimeISO = recurrency.getString("departureDatetime");
                        String departureDatetimeAndroid = MySimpleDateFormat.convertFromISOtoAndroid(departureDatetimeISO);
                        t.setDepartureDate(departureDatetimeAndroid);

                        JSONArray departureDaysArray = recurrency.getJSONArray("departureDays");
                        boolean [] departureDays = new boolean[7];
                        for(int j=0; j<departureDays.length; j++) {
                            departureDays[j] = departureDaysArray.getBoolean(j);
                        }
                        t.setRecurrentGoDays(departureDays);

                        String returnDatetimeISO = recurrency.getString("returnDatetime");
                        String returnDatetimeAndroid = MySimpleDateFormat.convertFromISOtoAndroid(returnDatetimeISO);
                        t.setReturnDate(returnDatetimeAndroid);

                        JSONArray returnDaysArray = recurrency.getJSONArray("returnDays");
                        boolean [] returnDays = new boolean[7];
                        for(int j=0; j<returnDays.length; j++) {
                            returnDays[j] = returnDaysArray.getBoolean(j);
                        }
                        t.setRecurrentBackDays(returnDays);
                    }

                    Log.d("TRIP", t.toString() + "");

                    res.add(t);
                }
                return res;
            } else {
                return null;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
