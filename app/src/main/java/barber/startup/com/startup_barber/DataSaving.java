package barber.startup.com.startup_barber;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class DataSaving extends AppCompatActivity {

    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    ProgressDialog progressDialog = null;
    private SharedPreferences prefs;
    private boolean dataSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_saving);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        dataSaved = prefs.getBoolean("dataSaved", false);


    }

    @Override
    protected void onStart() {
        super.onStart();


        if (dataSaved == false && check_connection()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("First Run Setup");
            progressDialog.setCancelable(false);
            Fetch_data();
        } else if (dataSaved == true)
            startMainActivity();

    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    private void Fetch_data() {

        progressDialog.show();
        // Syncing all our data from server
        if (check_connection()) {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Defaults.DataClass);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            if (Application.DEBUG)
                                Log.d("Objectsize", String.valueOf(objects.size()));
                            for (int i = 0; i < objects.size(); i++) {
                                ParseObject parseObject = objects.get(i);
                                ParseFile parseFile = parseObject.getParseFile("image");
                                Glide.with(getApplicationContext()).load(parseFile.getUrl());
                            }
                            unpinAndRepinData(objects);
                        }
                    } else {
                        e.printStackTrace();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                    }
                }
            });
        }


    }

    private void unpinAndRepinData(final List<ParseObject> objects) {


        ParseObject.unpinAllInBackground("data", new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (Application.DEBUG)
                        Log.d("MainActivityPin", "unPinnedAll");

                    ParseObject.pinAllInBackground("data", objects, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if (Application.DEBUG)
                                    Log.d("Login", "PinnedAll");

                                prefs.edit().putBoolean("dataSaved", true).commit();
                                progressDialog.dismiss();
                                progressDialog = null;
                                startMainActivity();

                            } else {
                                e.printStackTrace();
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                }
                            }
                        }


                    });

                } else e.printStackTrace();

            }
        });


    }

    public boolean check_connection() {

        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected) {
                Log.i("DATA SAVING", getString(R.string.wifi_connection));
                return true;
            } else if (mobileConnected) {
                Log.i("DATA SAVING", getString(R.string.mobile_connection));
                return true;
            }
        } else {
            Log.i("DATASAVING", getString(R.string.no_wifi_or_mobile));
            return false;
        }
        return false;
    }
}
