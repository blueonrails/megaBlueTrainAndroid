package async;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.shipeer.app.GlobalState;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import async.interfaces.OnDeleteOwnTripTaskCompleted;

/**
 * Created by mifercre on 15/03/15.
 */
public class DeleteOwnTripTask extends AsyncTask<String, Void, String[]> {

    private static final String URL = BaseRequest.BASE_URL + "trips/";

    private OnDeleteOwnTripTaskCompleted listener;

    public DeleteOwnTripTask(OnDeleteOwnTripTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected String[] doInBackground(String... form) {
        String[] res = null;
        if (form != null && form.length > 0) {
            res = postRequest(form);
        } else {
            Log.d("DELETE TRIP TASK", "ERROR IN FORM");
        }
        return res;
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(null);
        listener.onDeleteOwnTripTaskCompleted(result);
    }

    private String[] postRequest(String... form) {
        try {
            SharedPreferences preferences = GlobalState.getSharedPreferences();
            String userKey = preferences.getString("BaseUserKey", null);
            String userSecret = preferences.getString("BaseUserSecret", null);

            if (userKey != null && userSecret != null) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpDelete httpDelete = new HttpDelete(URL + form[0] + ".json?key=" + userKey + "&secret=" + userSecret);

                Log.d("AUTH", "KEY=" + userKey + ", SECRET=" + userSecret);

                HttpResponse response = httpclient.execute(httpDelete);
                String responseStr = EntityUtils.toString(response.getEntity());
                Log.d("response", responseStr);
                if (response.getStatusLine().getStatusCode() == 200) {
                    String[] res = {"code", "200"};
                    return res;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
