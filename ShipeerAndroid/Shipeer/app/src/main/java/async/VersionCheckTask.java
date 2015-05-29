package async;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import async.interfaces.OnVersionCheckTaskCompleted;

/**
 * Created by mifercre on 25/03/15.
 */
public class VersionCheckTask extends AsyncTask<Void, Void, Integer> {

    private static final String URL = BaseRequest.BASE_URL + "android/versions.json";

    private OnVersionCheckTaskCompleted listener;

    public VersionCheckTask(OnVersionCheckTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(Void... form) {
        Integer res = getRequest();
        return res;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(null);
        listener.onVersionCheckTaskCompleted(result);
    }

    private Integer getRequest() {
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(URL);

            HttpResponse response = httpclient.execute(httpget);
            String responseStr = EntityUtils.toString(response.getEntity());
            Log.d("response", responseStr);
            if(response.getStatusLine().getStatusCode() == 200) {
                JSONObject jsonObject = new JSONObject(responseStr);
                int version = jsonObject.getInt("version");
                return version;
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
