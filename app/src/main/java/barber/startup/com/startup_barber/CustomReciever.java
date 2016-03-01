package barber.startup.com.startup_barber;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by ayush on 28/2/16.
 */
public class CustomReciever extends ParsePushBroadcastReceiver {

    private Context context;

    @Override
    protected void onPushReceive(Context context, Intent intent) {

        if (intent == null)
            return;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.e("Push", String.valueOf(json));

            Intent parseIntent = intent;

            parsePushJson(context, json);

        } catch (JSONException e) {
            Log.e("push", e.getMessage());
        }
    }

    private void parsePushJson(Context context, JSONObject json) {

        this.context = context;
        try {
            boolean isBackground = json.getBoolean("is_background");
            JSONObject data = json.getJSONObject("data");
            String message = data.getString("message");

            // Syncing all our data from server
            if (check_connection()) {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Data");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null)
                            if (objects.size() > 0) {
                                if (Application.APPDEBUG)
                                    Log.d("Objectsize", String.valueOf(objects.size()));
                                unpinAndRepinData(objects);
                            }
                    }
                });
            }
        } catch (JSONException e) {
            Log.e("CustomReciever", "Pushexception: " + e.getMessage());
        }
    }

    public boolean check_connection() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

//For 3G check
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
//For WiFi Check
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        System.out.println(is3g + " net " + isWifi);

        if (!is3g && !isWifi) {

            return false;
        } else {

            return true;
        }


    }

    private void unpinAndRepinData(final List<ParseObject> objects) {


        ParseObject.unpinAllInBackground("data", new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (Application.APPDEBUG)
                        Log.d("MainActivityPin", "unPinnedAll");

                    ParseObject.pinAllInBackground("data", objects, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if (Application.APPDEBUG)
                                    Log.d("MainActivityPin", "PinnedAll");
                            } else e.printStackTrace();
                        }
                    });

                } else e.printStackTrace();

            }
        });


    }
}
