package async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import model.City;

/**
 * Created by mifercre on 15/03/15.
 */
public class PlacesTask extends AsyncTask<String, Void, ArrayList<City>> {

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDwI3RVBYFxq-goRpk_MuFgh5Jvm-U0Bzc";

    private static ArrayList<City> cities = null;

    private OnPlacesTaskCompleted listener;

    public PlacesTask(OnPlacesTaskCompleted listener) {
        this.listener = listener;
        cities = new ArrayList<City>();
    }

    @Override
    protected ArrayList<City> doInBackground(String... place) {
        return autocomplete(place[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<City> result) {
        super.onPostExecute(result);
        listener.onPlacesTaskCompleted(result);
    }

    public static ArrayList<City> autocomplete(String input) {
        //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=Vict&types=(cities)&language=pt_BR&key=API_KEY
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?sensor=false&key=" + API_KEY);
            sb.append("&components=country:es");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&types(geocodes)&language=es_ES");

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
            return cities;
        } catch (IOException e) {
            Log.e("Log", "Error connecting to Places API", e);
            return cities;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            //Log.d("JSON", jsonObj.toString());

            // Extract the Place descriptions from the results
            cities = new ArrayList<City>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                String cityName = predsJsonArray.getJSONObject(i).getString("description");
                String placeId = predsJsonArray.getJSONObject(i).getString("place_id");
                cities.add(new City(cityName, placeId));
            }
        } catch (JSONException e) {
            Log.e("Log", "Cannot process JSON results", e);
        }

        return cities;
    }

}