package async;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import async.interfaces.OnFacebookLoginTaskCompleted;
import model.UrlEncodedFormEntityUTF8;

/**
 * Created by mifercre on 25/03/15.
 */
public class FacebookLoginTask extends AsyncTask<String, Void, String[]> {

    //http://shipeer-staging.herokuapp.com/api/v1/tokens.json?type=email&email=mikelfv@hotmail.com&password=rr
    private static final String URL = BaseRequest.BASE_URL + "tokens.json";
    private static final String TYPE = "facebook";

    private OnFacebookLoginTaskCompleted listener;

    public FacebookLoginTask(OnFacebookLoginTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected String[] doInBackground(String... form) {
        String[] res = getRequest(form);
        return res;
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(null);
        listener.onFacebookLoginTaskCompleted(result);
    }

    private String[] getRequest(String... form) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

            nameValuePairs.add(new BasicNameValuePair("type", TYPE));
            nameValuePairs.add(new BasicNameValuePair("facebookId", form[0]));
            nameValuePairs.add(new BasicNameValuePair("facebookToken", form[1]));

            httpPost.setEntity(new UrlEncodedFormEntityUTF8(nameValuePairs));
            HttpResponse response = httpclient.execute(httpPost);
            String responseStr = EntityUtils.toString(response.getEntity());

            if(response != null) {
                Log.d("response", responseStr);
                if(response.getStatusLine().getStatusCode() == 201) {
                    JSONObject jsonObj = new JSONObject(responseStr);

                    String user = jsonObj.getString("user");
                    String key = jsonObj.getString("key");
                    String secret = jsonObj.getString("secret");
                    String expire = jsonObj.getString("expire");

                    Log.d("FACEBOOK LOGIN RES", user + "," + key + "," + secret + "," + expire);
                    String[] res = {user, key, secret, expire};
                    return res;
                } else if(response.getStatusLine().getStatusCode() == 409) {
                    String[] res = {"Facebook incorrect"};
                    return res;
                }
            } else {
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
