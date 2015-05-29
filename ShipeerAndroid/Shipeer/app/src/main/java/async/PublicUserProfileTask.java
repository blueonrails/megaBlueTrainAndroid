package async;

import android.os.AsyncTask;
import android.util.Log;

import com.shipeer.app.GlobalState;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import async.interfaces.OnPublicUserProfileTaskCompleted;

/**
 * Created by mifercre on 15/03/15.
 */
public class PublicUserProfileTask extends AsyncTask<String, Void, String[]> {

    private static final String URL = BaseRequest.BASE_URL + "users/";

    private OnPublicUserProfileTaskCompleted listener;

    public PublicUserProfileTask(OnPublicUserProfileTaskCompleted listener) {
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
        listener.onPublicUserProfileTaskCompleted(result);
    }

    private String[] getRequest(String... form) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(URL + form[0] + ".json?key=" + form[1] + "&secret=" + form[2]);

            HttpResponse response = httpclient.execute(httpGet);
            String responseStr = EntityUtils.toString(response.getEntity());
            Log.d("response", responseStr);
            if(response.getStatusLine().getStatusCode() == 200) {
                JSONObject jsonObj = new JSONObject(responseStr);

                String facebookId = null;
                String facebookToken = null;
                if(jsonObj.has("facebook")){
                    JSONObject facebookJsonObj = jsonObj.getJSONObject("facebook");
                    if(facebookJsonObj != null && facebookJsonObj.has("id") && !facebookJsonObj.isNull("id")) {
                        facebookId = facebookJsonObj.getString("id");
                        facebookToken = facebookJsonObj.getString("token");
                    }
                }

                JSONObject localJsonObj = jsonObj.getJSONObject("local");

                String email = URLDecoder.decode(localJsonObj.getString("email"), "utf-8");
                String gender = localJsonObj.isNull("gender")? null : Integer.toString(localJsonObj.getInt("gender"));
                String birthday = localJsonObj.isNull("birthday")? null : localJsonObj.getString("birthday");
                String firstName = URLDecoder.decode(localJsonObj.getString("firstName"), "utf-8");
                String lastName = localJsonObj.isNull("lastName")? null : URLDecoder.decode(localJsonObj.getString("lastName"), "utf-8");
                String phone = localJsonObj.isNull("phone")? null : localJsonObj.getString("phone");

                JSONObject profilePictureJsonObj = localJsonObj.getJSONObject("profilePicture");
                String profilePicVersion = profilePictureJsonObj.isNull("version")? null : profilePictureJsonObj.getString("version");
                String profilePicId = profilePictureJsonObj.isNull("id")? null : profilePictureJsonObj.getString("id");

                GlobalState.saveBaseUserData(null, gender, firstName, lastName, birthday, email, profilePicId, profilePicVersion);
                GlobalState.saveBaseUserPhone(phone);
                GlobalState.saveBaseUserFacebookId(facebookId);
                GlobalState.saveBaseUserFacebookToken(facebookToken);
                String[] res = {email, firstName, lastName, phone};
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
