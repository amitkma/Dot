package barber.startup.com.startup_barber;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import barber.startup.com.startup_barber.Utility.CustomLinearLayout;
import fr.ganfra.materialspinner.MaterialSpinner;

public class Login extends AppCompatActivity implements CustomLinearLayout.OnSoftKeyboardListener {


    private Button button_fb_login;
    private EditText rollno;
    private EditText name;
    private ImageView dummy;
    private int rollnumber;
    private String username;
    private String bhawanName;
    private int bhawanCode;
    private MaterialSpinner spinner;
    private CustomLinearLayout customRelativeLayout;
    private boolean mVerified = false;
    private ProgressDialog pd;

    private TextView textview;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_choose_login);

        customRelativeLayout = (CustomLinearLayout) findViewById(R.id.customLayout);
        customRelativeLayout.setOnSoftKeyboardListener(this);
        dummy = (ImageView) findViewById(R.id.imageView);

        textview = (TextView)findViewById(R.id.update_details_textview);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.loginCoordinatorLayout);
        rollno = (EditText) findViewById(R.id.rollno);
        name = (EditText) findViewById(R.id.name);
        button_fb_login = (Button) findViewById(R.id.button_fb_login);
        // Spinner element
        spinner = (MaterialSpinner) findViewById(R.id.spinner);

        rollno.addTextChangedListener(textWatcher);
        name.addTextChangedListener(textWatcher);

        rollno.setTag(rollno.getKeyListener());
        name.setTag(name.getKeyListener());

        setup_spinner();
        updateUI();

        pd = new ProgressDialog(Login.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Verifying");
        pd.setIndeterminate(true);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void checkFieldsForEmptyValues() {
        String userString = rollno.getText().toString();
        String passwordString = name.getText().toString();

        if (userString.equals("") && passwordString.equals("")) {
            button_fb_login.setEnabled(false);
            button_fb_login.setBackgroundResource(R.drawable.login_button_disable);
        } else if (!userString.equals("") && passwordString.equals("")) {
            button_fb_login.setEnabled(false);
            button_fb_login.setBackgroundResource(R.drawable.login_button_disable);
        } else if (userString.equals("") && !passwordString.equals("")) {
            button_fb_login.setEnabled(false);
            button_fb_login.setBackgroundResource(R.drawable.login_button_disable);
        } else {
            if (bhawanCode == -1) {
                button_fb_login.setEnabled(false);
                button_fb_login.setBackgroundResource(R.drawable.login_button_disable);
            } else {
                button_fb_login.setEnabled(true);
                button_fb_login.setBackgroundResource(R.drawable.login_button_ripple);
            }
        }
    }

    private void setup_spinner() {

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.i("spinner", String.valueOf(position));
                bhawanCode = position;
                bhawanName = parent.getItemAtPosition(position).toString();
                checkFieldsForEmptyValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Sarojini Bhawan");
        categories.add("Kasturba Bhawan");
        categories.add("Azad Bhawan");
        categories.add("Ravindra Bhawan");
        categories.add("RKB Bhawan");
        categories.add("Ganga Bhawan");
        categories.add("Cautley Bhawan");
        categories.add("Jawahar Bhawan");
        categories.add("Rajendra Bhawan");
        categories.add("Govind Bhawan");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }


    private void setup_login_button() {

        button_fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.show();
                rollnumber = Integer.parseInt(rollno.getText().toString().trim());
                username = name.getText().toString().trim();

                ParseQuery<ParseObject> parsequery = new ParseQuery<ParseObject>(Defaults.Enrollmentclass);
                parsequery.whereEqualTo("rollno", rollnumber);
                parsequery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            if (object != null) {
                                String nametemp = object.getString("name");

                                if (username.equalsIgnoreCase(nametemp)) {
                                    mVerified = true;
                                    if(pd != null){
                                        pd.dismiss();
                                    }
                                    updateUI();
                                } else {
                                    Snackbar.make(coordinatorLayout, "Wrong credentials", Snackbar.LENGTH_LONG).show();
                                    if (pd != null) {
                                        pd.dismiss();
                                    }
                                }
                            }

                        } else {
                            if(pd != null){
                                pd.dismiss();
                            }
                            if (e.getCode() == ParseException.CONNECTION_FAILED) {
                                Snackbar.make(coordinatorLayout, "Error in connection.", Snackbar.LENGTH_LONG).show();
                            } else
                                Log.e("Login", e.getMessage());

                        }
                    }
                });


            }
        });
    }

    private void updateUI() {

        if (mVerified) {

            spinner.setEnabled(false);
            textview.setVisibility(View.VISIBLE);
            textview.setClickable(true);
            textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVerified = false;
                    updateUI();
                }
            });
            rollno.setKeyListener(null);
            name.setKeyListener(null);
            button_fb_login.setText("Login with facebok");
            button_fb_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    link_with_parse();
                }
            });
        } else {
            spinner.setEnabled(true);
            textview.setVisibility(View.GONE);
            rollno.setKeyListener((KeyListener) rollno.getTag());
            name.setKeyListener((KeyListener) name.getTag());
            button_fb_login.setText("Verify");
            setup_login_button();
        }


    }

    private void link_with_parse() {

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, Arrays.asList("email", "user_friends", "public_profile"), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    getfb_details(user);

                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {

                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("userId", user.getObjectId());
                    installation.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                startDataLoadActivity();

                            } else Log.d("LoginInstallation", "useris not new" + e.getMessage());
                        }
                    });

                    Log.d("MyApp", "User logged in through Facebook!");

                }

            }
        });
    }

    private void getfb_details(final ParseUser user) {

        final Bundle parameters = new Bundle();
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
                            if (gender.equals("male")) {
                                gendercode = 1;
                            }
                            if (gender.equals("female")) {
                                gendercode = 0;
                            }
                            String pictureUrl = data.getString("url");
                            user.put("picUri", pictureUrl);
                            user.put("gender", gender);
                            user.put("genderCode", gendercode);

                            user.put("bhawanCode", bhawanCode);
                            user.put("bhawanName", bhawanName);
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
    public void onPause() {
        super.onPause();
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(customRelativeLayout.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void startDataLoadActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    public void onShown() {
        Log.e("CUSTOM", "onShow");
        dummy.setVisibility(View.GONE);
    }

    @Override
    public void onHidden() {
        Log.e("CUSTOM", "onShow");
        dummy.setVisibility(View.VISIBLE);
    }
}
