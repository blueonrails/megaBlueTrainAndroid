package async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import model.City;

/**
 * Created by mifercre on 15/03/15.
 */
public class PlacesDetailTask extends AsyncTask<String, Void, Void> {

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDwI3RVBYFxq-goRpk_MuFgh5Jvm-U0Bzc";

    public static final int TYPE_FROM = 1;
    public static final int TYPE_TO = 2;

    private int type;
    private City city;

    private OnPlacesDetailTaskCompleted listener;

    public PlacesDetailTask(OnPlacesDetailTaskCompleted listener, City city) {
        this.listener = listener;
        this.city = city;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    protected Void doInBackground(String... place) {
        downloadPlacesDetails(place[0], type);
        return null;
    }

    @Override
    protected void onPostExecute(Void a) {
        super.onPostExecute(null);
        //upgradeLocation(type);
        listener.onPlacesDetailTaskCompleted(type, city);
    }

    private void downloadPlacesDetails(String placeId, int type) {
        //https://maps.googleapis.com/maps/api/place/details/json?placeid={placeid}&key={key}
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
            sb.append("?placeid=" + URLEncoder.encode(placeId, "utf8"));
            sb.append("&key=" + API_KEY);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("Log", "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e("Log", "Error connecting to Places API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            Log.d("JSON", jsonObj.toString());
            JSONObject resultJsonArray = jsonObj.getJSONObject("result");
            JSONObject geometry = resultJsonArray.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");

            city.setLat(location.getDouble("lat"));
            city.setLng(location.getDouble("lng"));

        } catch (JSONException e) {
            Log.e("Log", "Cannot process JSON results", e);
        }
    }
}
