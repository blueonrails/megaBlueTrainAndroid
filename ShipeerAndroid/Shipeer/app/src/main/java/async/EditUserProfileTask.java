package async;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import async.interfaces.OnEditUserProfileTaskCompleted;
import model.UrlEncodedFormEntityUTF8;

/**
 * Created by mifercre on 15/03/15.
 */
public class EditUserProfileTask extends AsyncTask<String, Void, String[]> {

    private static final String URL = BaseRequest.BASE_URL + "users/";

    private OnEditUserProfileTaskCompleted listener;

    public EditUserProfileTask(OnEditUserProfileTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected String[] doInBackground(String... form) {
        String[] res = putRequest(form);
        return res;
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(null);
        listener.onEditUserProfileTaskCompleted(result);
    }

    private String[] putRequest(String... form) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpPut = new HttpPut(URL + form[0] + ".json" + "?key=" + form[1] + "&secret=" + form[2]);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            if(form.length > 3 && form[3] != null && !form[3].isEmpty()) nameValuePairs.add(new BasicNameValuePair("firstName", URLEncoder.encode(form[3], "utf-8")));
            if(form.length > 4 && form[4] != null && !form[4].isEmpty()) nameValuePairs.add(new BasicNameValuePair("lastName", URLEncoder.encode(form[4], "utf-8")));
            if(form.length > 5 && form[5] != null && !form[5].isEmpty()) nameValuePairs.add(new BasicNameValuePair("phone", form[5]));
            if(form.length > 6 && form[6] != null && !form[6].isEmpty()) nameValuePairs.add(new BasicNameValuePair("gender", form[6]));
            if(form.length > 7 && form[7] != null && !form[7].isEmpty()) nameValuePairs.add(new BasicNameValuePair("email", form[7]));

            if(form.length > 8 && form[8] != null && !form[8].isEmpty()) nameValuePairs.add(new BasicNameValuePair("profilePicture[id]", form[8]));
            if(form.length > 9 && form[9] != null && !form[9].isEmpty()) nameValuePairs.add(new BasicNameValuePair("profilePicture[version]", form[9]));

            httpPut.setEntity(new UrlEncodedFormEntityUTF8(nameValuePairs));
            HttpResponse response = httpclient.execute(httpPut);
            String responseStr = EntityUtils.toString(response.getEntity());
            Log.d("response", responseStr);
            if(response.getStatusLine().getStatusCode() == 201) {
                JSONObject jsonObj = new JSONObject(responseStr);
                /**JSONObject accessTokenJsonObj = jsonObj.getJSONObject("accessToken");

                String key = accessTokenJsonObj.getString("key");
                String secret = accessTokenJsonObj.getString("secret");
                String expire = accessTokenJsonObj.getString("expire");**/

                String[] res = null;//{key, secret, expire};
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
