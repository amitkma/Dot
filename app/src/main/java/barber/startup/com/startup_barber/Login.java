package barber.startup.com.startup_barber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity {


    private Button button_fb_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        setContentView(R.layout.activity_choose_login);

        setup_fb_login_button();

    }

    private void setup_fb_login_button() {
        button_fb_login = (Button) findViewById(R.id.button_fb_login);
        button_fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link_with_parse();
            }
        });
    }

    private void link_with_parse() {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, Arrays.asList("email", "user_photos", "public_profile"), new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    getfb_details(user);
                    startDataLoadActivity();
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {
                    startDataLoadActivity();
                    Log.d("MyApp", "User logged in through Facebook!");

                }

            }
        });
    }

    private void getfb_details(final ParseUser user) {

        Bundle parameters = new Bundle();
        parameters.putString("fields", "picture.type(small).width(100).height(100),name");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {

                            String name = response.getJSONObject().getString("name");
                            user.setUsername(name);


                            JSONObject picture = response.getJSONObject().getJSONObject("picture");

                            JSONObject data = picture.getJSONObject("data");

                            String pictureUrl = data.getString("url");

                            user.put("picUri", pictureUrl);
                            Log.d("uri", pictureUrl);

                            user.saveInBackground();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);


    }


    @Override
    public void onStart() {
        super.onStart();


        if (ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(this, DataSaving.class));
            finish();
        }

    }


    private void startDataLoadActivity() {

        startActivity(new Intent(getApplicationContext(), DataSaving.class));
        overridePendingTransition(0, 0);
        finish();

    }


}
