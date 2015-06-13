package async;

import android.os.AsyncTask;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.shipeer.app.GlobalState;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import async.interfaces.OnProfilePictureUploaderTaskCompleted;

/**
 * Created by mifercre on 07/04/15.
 */
public class ProfilePictureUploaderTask extends AsyncTask<String, Void, String[]>{

    private OnProfilePictureUploaderTaskCompleted listener;
    private Cloudinary mCloudinary;

    public ProfilePictureUploaderTask(OnProfilePictureUploaderTaskCompleted listener, Cloudinary mCloudinary) {
        this.listener = listener;
        this.mCloudinary = mCloudinary;
    }

    @Override
    protected String[] doInBackground(String... params) {
        String resId = params[1];
        String resVersion = null;

        final Map<String, String> options = new HashMap<>();
        options.put("public_id", resId);
        options.put("timestamp", params[2]);
        options.put("quality", "50");

        if(params[0] != null && !params[0].isEmpty()) {
            try {
                Map map = mCloudinary.uploader().upload(params[0], options);

                Set<Map.Entry> entries = map.entrySet();

                for (Map.Entry entry : entries) {
                    if(entry != null) {
                        Log.d("MAP ", "KEY=" + entry.getKey().toString() + ", VALUE=" + entry.getValue().toString());
                        if(entry.getKey().toString().equalsIgnoreCase("version")) {
                            resVersion = entry.getValue().toString();
                            GlobalState.saveProfilePicture(resId, resVersion);
                        }
                    }
                    else {
                        Log.d("ENTRY NULL", "ENTRY NULL");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        String[] res = {resId, resVersion};
        return res;
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(null);
        listener.onProfilePictureUploaderTaskCompleted(result);
    }
}
