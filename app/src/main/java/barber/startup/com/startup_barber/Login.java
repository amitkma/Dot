package barber.startup.com.startup_barber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
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
    private ImageView dummy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        setContentView(R.layout.activity_choose_login);


        dummy = (ImageView) findViewById(R.id.dummy);
        setup_fb_login_button();

        downloadData();
    }

    private void downloadData() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Defaults.DataClass);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {

                        if (Application.DEBUG)
                            Log.d("Login", String.valueOf(objects.size()));
                        for (int i = 0; i < objects.size(); i++) {

                            ParseObject parseObject = objects.get(i);
                            ParseFile parseFile = parseObject.getParseFile("image");
                            Glide.with(getApplicationContext()).load(parseFile.getUrl()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(dummy);
                        }

                    }
                } else if (Application.DEBUG) Log.d("Login", e.getMessage());
            }
        });
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
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, Arrays.asList("email", "user_friends", "public_profile"), new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    getfb_details(user);

                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {

                    startDataLoadActivity();
                    Log.d("MyApp", "User logged in through Facebook!");

                }

            }
        });
    }

    private void getfb_details(final ParseUser user) {
/*GraphRequest.
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code

                        Log.d("JSONobject", String.valueOf(object));


                    }
                });*/
        Bundle parameters = new Bundle();
        //  parameters.putString("fields", "id,name,first_name,last_name,age_range,link,gender,locale,picture,user_friends");
        // request.setParameters(parameters);
        // request.executeAsync();


        parameters.putString("fields", "picture.type(small).width(100).height(100),name,gender,birthday");


        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {

                            Log.d("response", String.valueOf(response));
                            String name = response.getJSONObject().getString("name");
                            user.setUsername(name);


                            JSONObject picture = response.getJSONObject().getJSONObject("picture");

                            JSONObject data = picture.getJSONObject("data");

                            String gender = response.getJSONObject().getString("gender");
                            String pictureUrl = data.getString("url");

                            user.put("picUri", pictureUrl);
                            user.put("gender", gender);


                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                        installation.put("userId", user.getObjectId());
                                        installation.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    startDataLoadActivity();
                                                } else Log.d("LoginInstallation", e.getMessage());
                                            }
                                        });
                                    } else Log.d("LoginUser", e.getMessage());

                                }
                            });


                        } catch (JSONException e) {
                            e.getMessage();
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
