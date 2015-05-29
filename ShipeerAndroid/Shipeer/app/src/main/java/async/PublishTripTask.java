package async;

import android.content.SharedPreferences;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import async.interfaces.OnPublishTripTaskTaskCompleted;
import model.UrlEncodedFormEntityUTF8;

/**
 * Created by mifercre on 15/03/15.
 */
public class PublishTripTask extends AsyncTask<String, Void, String[]> {

    private static final String URL = BaseRequest.BASE_URL + "trips.json";
    public static final int SIMPLE_GO_TRIP = 0;
    public static final int SIMPLE_GO_AND_BACK_TRIP = 1;
    public static final int RECURRENT_GO_TRIP = 2;
    public static final int RECURRENT_GO_AND_BACK_TRIP = 3;

    private OnPublishTripTaskTaskCompleted listener;

    public PublishTripTask(OnPublishTripTaskTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected String[] doInBackground(String... form) {
        String[] res = null;
        if (form != null && form.length > 0) {
            res = postRequest(form);
        } else {
            Log.d("PUBLISH TRIP TASK", "ERROR IN FORM");
        }
        return res;
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(null);
        listener.onPublishTripTaskCompleted(result);
    }

    private String[] postRequest(String... form) {
        //http://shipeer-staging.herokuapp.com/api/v1/routes.json?key=463186d36d3b28c4102a73a613e9d46c2e3278fc1b8c3ad86547290b5b7d85c81ab857ef&secret=648748ad8fce165fcd79640bb07ef4cf74ad48894f82f25c2b99e4ce588e8e0d6260a231
        try {
            SharedPreferences preferences = GlobalState.getSharedPreferences();
            String userKey = preferences.getString("BaseUserKey", null);
            String userSecret = preferences.getString("BaseUserSecret", null);

            if (userKey != null && userSecret != null) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL + "?key=" + userKey + "&secret=" + userSecret);

                Log.d("AUTH", "KEY=" + userKey + ", SECRET=" + userSecret);

                List<NameValuePair> nameValuePairs = null;

                int tripType = Integer.parseInt(form[2]);
                switch (tripType) {
                    case SIMPLE_GO_TRIP:
                        nameValuePairs = setSimpleGoTripForm(form);
                        break;
                    case SIMPLE_GO_AND_BACK_TRIP:
                        nameValuePairs = setSimpleGoAndBackTripForm(form);
                        break;
                    case RECURRENT_GO_TRIP:
                        nameValuePairs = setRecurrentGoTripForm(form);
                        break;
                    case RECURRENT_GO_AND_BACK_TRIP:
                        nameValuePairs = setRecurrentGoAndBackTripForm(form);
                        break;
                }

                httppost.setEntity(new UrlEncodedFormEntityUTF8(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                String responseStr = EntityUtils.toString(response.getEntity());
                Log.d("response", responseStr);
                if (response.getStatusLine().getStatusCode() == 201) {

                    String[] res = {"fill", "this" };
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

    private List<NameValuePair> setRecurrentGoTripForm(String[] form) {
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            String departureDatetime = parseDateToddMMyyyy(form[7]);
            String startingDate = parseDateToddMMyyyy(form[8]);
            String finishingDate = parseDateToddMMyyyy(form[9]);

            nameValuePairs.add(new BasicNameValuePair("trip[locations][0][name]", URLEncoder.encode(form[0], "utf-8")));
            nameValuePairs.add(new BasicNameValuePair("trip[locations][1][name]", URLEncoder.encode(form[1], "utf-8")));

            nameValuePairs.add(new BasicNameValuePair("trip[tripType]", form[2]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][0]", form[3]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][1]", form[4]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][2]", form[5]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][3]", form[6]));

            nameValuePairs.add(new BasicNameValuePair("trip[departureDatetime]", departureDatetime));//"Wed Jun 17 2015 20:52:40 GMT+0200 (CEST)"));//form[6]));
            nameValuePairs.add(new BasicNameValuePair("trip[startingDate]", startingDate));//"Wed Jun 17 2015 20:52:40 GMT+0200 (CEST)"));//form[6]));
            nameValuePairs.add(new BasicNameValuePair("trip[finishingDate]", finishingDate));//"Wed Jun 17 2015 20:52:40 GMT+0200 (CEST)"));//form[6]));

            nameValuePairs.add(new BasicNameValuePair("trip[description]", URLEncoder.encode(form[10], "utf-8")));

            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][0]", form[11]));
            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][1]", form[12]));
            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][2]", form[13]));
            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][3]", form[14]));
            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][4]", form[15]));
            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][5]", form[16]));
            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][6]", form[17]));

            return nameValuePairs;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<NameValuePair> setRecurrentGoAndBackTripForm(String[] form) {
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            String departureDatetime = parseDateToddMMyyyy(form[7]);
            String returnDatetime = parseDateToddMMyyyy(form[8]);
            String startingDate = parseDateToddMMyyyy(form[9]);
            String finishingDate = parseDateToddMMyyyy(form[10]);

            nameValuePairs.add(new BasicNameValuePair("trip[locations][0][name]", URLEncoder.encode(form[0], "utf-8")));
            nameValuePairs.add(new BasicNameValuePair("trip[locations][1][name]", URLEncoder.encode(form[1], "utf-8")));

            nameValuePairs.add(new BasicNameValuePair("trip[tripType]", form[2]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][0]", form[3]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][1]", form[4]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][2]", form[5]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][3]", form[6]));

            nameValuePairs.add(new BasicNameValuePair("trip[departureDatetime]", departureDatetime));//"Wed Jun 17 2015 20:52:40 GMT+0200 (CEST)"));//form[6]));
            nameValuePairs.add(new BasicNameValuePair("trip[returnDatetime]", returnDatetime));//"Wed Jun 17 2015 20:52:40 GMT+0200 (CEST)"));//form[6]));
            nameValuePairs.add(new BasicNameValuePair("trip[startingDate]", startingDate));//"Wed Jun 17 2015 20:52:40 GMT+0200 (CEST)"));//form[6]));
            nameValuePairs.add(new BasicNameValuePair("trip[finishingDate]", finishingDate));//"Wed Jun 17 2015 20:52:40 GMT+0200 (CEST)"));//form[6]));

            nameValuePairs.add(new BasicNameValuePair("trip[description]", URLEncoder.encode(form[11], "utf-8")));

            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][0]", form[12]));
            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][1]", form[13]));
            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][2]", form[14]));
            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][3]", form[15]));
            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][4]", form[16]));
            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][5]", form[17]));
            nameValuePairs.add(new BasicNameValuePair("trip[departureDays][6]", form[18]));

            nameValuePairs.add(new BasicNameValuePair("trip[returnDays][0]", form[19]));
            nameValuePairs.add(new BasicNameValuePair("trip[returnDays][1]", form[20]));
            nameValuePairs.add(new BasicNameValuePair("trip[returnDays][2]", form[21]));
            nameValuePairs.add(new BasicNameValuePair("trip[returnDays][3]", form[22]));
            nameValuePairs.add(new BasicNameValuePair("trip[returnDays][4]", form[23]));
            nameValuePairs.add(new BasicNameValuePair("trip[returnDays][5]", form[24]));
            nameValuePairs.add(new BasicNameValuePair("trip[returnDays][6]", form[25]));

            return nameValuePairs;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<NameValuePair> setSimpleGoTripForm(String[] form) {
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            String departureDatetime = parseDateToddMMyyyy(form[7]);
            Log.d("TRIP DATE B4 PARSE", form[7]);
            Log.d("TRIP DATE AFTER", departureDatetime);

            nameValuePairs.add(new BasicNameValuePair("trip[locations][0][name]", URLEncoder.encode(form[0], "utf-8")));
            nameValuePairs.add(new BasicNameValuePair("trip[locations][1][name]", URLEncoder.encode(form[1], "utf-8")));

            nameValuePairs.add(new BasicNameValuePair("trip[tripType]", form[2]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][0]", form[3]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][1]", form[4]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][2]", form[5]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][3]", form[6]));

            nameValuePairs.add(new BasicNameValuePair("trip[departureDatetime]", departureDatetime));//"Wed Jun 17 2015 20:52:40 GMT+0200 (CEST)"));//form[6]));
            nameValuePairs.add(new BasicNameValuePair("trip[description]", URLEncoder.encode(form[8], "utf-8")));

            return nameValuePairs;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<NameValuePair> setSimpleGoAndBackTripForm(String[] form) {
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            String departureDatetime = parseDateToddMMyyyy(form[7]);
            String returnDatetime = parseDateToddMMyyyy(form[8]);

            Log.d("TRIP DATE B4 PARSE", form[7]);
            Log.d("TRIP DATE AFTER", departureDatetime);

            nameValuePairs.add(new BasicNameValuePair("trip[locations][0][name]", URLEncoder.encode(form[0], "utf-8")));
            nameValuePairs.add(new BasicNameValuePair("trip[locations][1][name]", URLEncoder.encode(form[1], "utf-8")));

            nameValuePairs.add(new BasicNameValuePair("trip[tripType]", form[2]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][0]", form[3]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][1]", form[4]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][2]", form[5]));
            nameValuePairs.add(new BasicNameValuePair("trip[price][3]", form[6]));

            nameValuePairs.add(new BasicNameValuePair("trip[departureDatetime]", departureDatetime));//"Wed Jun 17 2015 20:52:40 GMT+0200 (CEST)"));//form[6]));
            nameValuePairs.add(new BasicNameValuePair("trip[returnDatetime]", returnDatetime));//"Wed Jun 17 2015 20:52:40 GMT+0200 (CEST)"));//form[6]));

            nameValuePairs.add(new BasicNameValuePair("trip[description]", URLEncoder.encode(form[9], "utf-8")));

            return nameValuePairs;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String parseDateToddMMyyyy(String time) {
        Log.d("INPUT STRING", "DATE=" + time.replace("CEST", "GMT+2"));
        String inputPattern = "EEE MMM dd hh:mm:ss Z yyy";
        String outputPattern = "yyy-MM-dd'T'HH:mm:ssZ";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time.replace("CEST", "GMT+2"));
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

}
