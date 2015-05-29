package async;

import android.os.AsyncTask;
import android.util.Log;

import com.shipeer.app.GlobalState;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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

import async.interfaces.OnRegisterNewUserTaskCompleted;
import model.UrlEncodedFormEntityUTF8;

/**
 * Created by mifercre on 15/03/15.
 */
public class RegisterNewUserTask extends AsyncTask<String, Void, String[]> {

    private static final String URL = BaseRequest.BASE_URL + "users.json";

    private OnRegisterNewUserTaskCompleted listener;

    public RegisterNewUserTask(OnRegisterNewUserTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected String[] doInBackground(String... form) {
        String[] res = postRequest(form);
        return res;
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(null);
        listener.onRegisterNewUserTaskCompleted(result);
    }

    private String[] postRequest(String... form) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);

            nameValuePairs.add(new BasicNameValuePair("firstName", URLEncoder.encode(form[0], "utf-8")));
            nameValuePairs.add(new BasicNameValuePair("lastName", URLEncoder.encode(form[1], "utf-8")));
            nameValuePairs.add(new BasicNameValuePair("email", form[2]));
            nameValuePairs.add(new BasicNameValuePair("password", form[3]));
            //nameValuePairs.add(new BasicNameValuePair("birthday", form[4]));

            httppost.setEntity(new UrlEncodedFormEntityUTF8(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            String responseStr = EntityUtils.toString(response.getEntity());
            Log.d("response", responseStr);
            if(response.getStatusLine().getStatusCode() == 201) {
                JSONObject jsonObj = new JSONObject(responseStr);

                String user = jsonObj.getString("user");
                String key = jsonObj.getString("key");
                String secret = jsonObj.getString("secret");
                String expire = jsonObj.getString("expire");

                //2015-05-07T06:39:33.256Z
                GlobalState.saveBaseUserTokens(
                        user,
                        key,
                        secret,
                        expire
                );

                Log.d("REGISTERED USER RES", user + "," + key + "," + secret + "," + expire);
                String[] res = {user, key, secret, expire};
                return res;
            } else if(response.getStatusLine().getStatusCode() == 409) {
                Log.d("USER ALREADY REGISTERED", "USER ALREADY REGISTERED");
                String[] res = {"userAlreadyRegistered"};
                return res;
            } else if(response.getStatusLine().getStatusCode() == 400) {
                Log.d("BAD REQUEST", "BAD REQUEST");
                String[] res = {"badRequest"};
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
