package barber.startup.com.startup_barber;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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

import barber.startup.com.startup_barber.Utility.NetworkCheck;


public class DataSaving extends AppCompatActivity {

    ProgressDialog progressDialog = null;
    private SharedPreferences prefs;
    private boolean dataSaved = false;

    private Context mContext;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_saving);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.data_saving_dialog);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        dataSaved = prefs.getBoolean("dataSaved", false);

        mContext = getApplicationContext();

    }

    @Override
    protected void onStart() {
        super.onStart();


        if (NetworkCheck.checkConnection(mContext)) {
            if (dataSaved == false) {

                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading styles and barbers");
                progressDialog.setCancelable(false);
                Fetch_data();

            } else if (dataSaved == true)
                startMainActivity();
        } else
            Snackbar.make(coordinatorLayout, "Error in connection", Snackbar.LENGTH_LONG).show();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    private void Fetch_data() {

        progressDialog.show();

        // Syncing all our data from server
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Defaults.INFO_CLASS);
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
                    if (e.getCode() == 100) {
                        Snackbar.make(coordinatorLayout, "Error in connection", Snackbar.LENGTH_LONG).show();
                    }
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }
            }
        });


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

}
