package barber.startup.com.startup_barber;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Login extends AppCompatActivity {


    private Button button_fb_login;
    private EditText rollno;
    private EditText name;
    private ImageView dummy;
    private int rollnumber;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        setContentView(R.layout.activity_choose_login);


        dummy = (ImageView) findViewById(R.id.dummy);


        rollno = (EditText) findViewById(R.id.rollno);
        name = (EditText) findViewById(R.id.name);

        setup_fb_login_button();


    }


    private void setup_fb_login_button() {
        button_fb_login = (Button) findViewById(R.id.button_fb_login);
        button_fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!rollno.getText().toString().trim().isEmpty() && !name.getText().toString().trim().isEmpty()) {
                    rollnumber = Integer.parseInt(rollno.getText().toString());
                    username = name.getText().toString().toLowerCase();


                    ParseQuery<ParseObject> parsequery = new ParseQuery<ParseObject>(Defaults.Enrollmentclass);
                    parsequery.whereEqualTo("rollno", rollnumber);
                    parsequery.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                if (object != null) {
                                    String nametemp = object.getString("name");

                                    if (username.equals(nametemp.toLowerCase())) {
                                        Toast.makeText(getApplication(), "Hurrah! your details are verifed", Toast.LENGTH_SHORT).show();
                                        Log.i("Login", "Verified");
                                        link_with_parse();
                                    } else
                                        Toast.makeText(getApplication(), "Sorry! Try Again", Toast.LENGTH_SHORT).show();


                                }

                            } else Log.i("Login", e.getMessage());
                        }
                    });


                }


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
                            final String gender = response.getJSONObject().getString("gender");
                            int gendercode = -1;
                            if (gender.compareTo("male") == 0) {
                                gendercode = 1;
                            }
                            if (gender.compareTo("female") == 0) {
                                gendercode = 0;
                            }
                            String pictureUrl = data.getString("url");
                            user.put("picUri", pictureUrl);
                            user.put("gender", gender);
                            user.put("genderCode", gendercode);
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
