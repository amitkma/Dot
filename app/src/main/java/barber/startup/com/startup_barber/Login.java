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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import barber.startup.com.startup_barber.Utility.CustomLinearLayout;
import barber.startup.com.startup_barber.Utility.NetworkCheck;
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

    //Textwatcher to monitor editTextField changes
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

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_choose_login);

        //Finding different elements of activity
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.loginCoordinatorLayout);
        linearLayout = (LinearLayout) findViewById(R.id.hideLinear);
        customRelativeLayout = (CustomLinearLayout) findViewById(R.id.customLayout);

        assert customRelativeLayout != null;
        customRelativeLayout.setOnSoftKeyboardListener(this);

        dummy = (ImageView) findViewById(R.id.imageview);

        rollno = (EditText) findViewById(R.id.rollno);
        name = (EditText) findViewById(R.id.name);

        button_fb_login = (Button) findViewById(R.id.button_fb_login);

        spinner = (MaterialSpinner) findViewById(R.id.spinner);

        //Placing logo in the imageView
        Glide.with(Login.this).load((R.drawable.logo)).centerCrop().into(dummy);

        //Add textwatcher to monitor changes in editTextFields
        rollno.addTextChangedListener(textWatcher);
        name.addTextChangedListener(textWatcher);

        //Retrieving keys for editTextFields
        rollno.setTag(rollno.getKeyListener());
        name.setTag(name.getKeyListener());

        // Set onClicklistener for button to login
        button_fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkCheck.checkConnection(Login.this)) {
                    updateUI(0);
                    link_with_parse();
                } else {
                    updateUI(1);
                    Snackbar.make(coordinatorLayout, "Error in connection", Snackbar.LENGTH_LONG).show();
                }

            }
        });

        //Populating spinner content
        setupSpinner();

        //Populating progress dialog
        pd = new ProgressDialog(Login.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Verifying");
        pd.setIndeterminate(true);
        callParseCloud();
    }

    private void callParseCloud() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", "amit");
        ParseCloud.callFunctionInBackground("hello", map, new FunctionCallback<String>() {
            @Override
            public void done(String object, ParseException e) {
                if (e == null)
                    Log.e("CLOUD", object);
                else
                    Log.e("CLOUD", e.getMessage() + " " + e.getCode());
            }
        });
    }

    private void updateUI(int i) {
        if (i == 0) {
            button_fb_login.setEnabled(false);
            rollno.setKeyListener(null);
            name.setKeyListener(null);
            spinner.setEnabled(false);
        }
        if (i == 1) {
            rollno.setKeyListener((KeyListener) rollno.getTag());
            name.setKeyListener((KeyListener) rollno.getTag());
            spinner.setEnabled(true);
            checkFieldsForEmptyValues();
        }
    }


    //Method for checking editTextFields state and updating UI accordingly
    private void checkFieldsForEmptyValues() {

        String userString = rollno.getText().toString();
        String passwordString = name.getText().toString();

        /**  if (userString.equals("") && passwordString.equals("")) {
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
         }**/
        if (userString.trim().length() == 8 && passwordString.length() > 0) {
            if (bhawanCode == -1) {
                button_fb_login.setEnabled(false);
                button_fb_login.setBackgroundResource(R.drawable.login_button_disable);
            } else {
                button_fb_login.setEnabled(true);
                button_fb_login.setBackgroundResource(R.drawable.login_button_ripple);
            }
        } else if (userString.trim().length() > 8) {
            button_fb_login.setEnabled(false);
            button_fb_login.setBackgroundResource(R.drawable.login_button_disable);
            Toast.makeText(Login.this, "Enrollment number is not valid", Toast.LENGTH_LONG).show();
        } else {
            button_fb_login.setEnabled(false);
            button_fb_login.setBackgroundResource(R.drawable.login_button_disable);
        }
    }


    //Method for populating spinner view
    private void setupSpinner() {

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.i("spinner", String.valueOf(position));
                bhawanCode = position;
                bhawanName = parent.getItemAtPosition(position).toString();
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(customRelativeLayout.getWindowToken(), 0);
                checkFieldsForEmptyValues();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("Azad Bhawan");
        categories.add("Cautley Bhawan");
        categories.add("Ganga Bhawan");
        categories.add("Govind Bhawan");
        categories.add("Jawahar Bhawan");
        categories.add("Kasturba Bhawan");
        categories.add("Rajendra Bhawan");
        categories.add("Ravindra Bhawan");
        categories.add("RKB Bhawan");
        categories.add("Sarojini Bhawan");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    //Create a parse user with facebook authentication
    private void link_with_parse() {

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, Arrays.asList("email", "user_friends", "public_profile"),
                new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null) {
                            if (user == null) {
                                Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                                updateUI(1);

                            } else if (user.isNew()) {
                                if (pd != null) {
                                    pd.show();
                                    pd.setMessage("Please Wait");
                                }

                                //Add parseACL and a role to the current user
                                ParseACL parseACL = new ParseACL();
                                parseACL.setPublicReadAccess(true);
                                parseACL.setPublicWriteAccess(true);
                                ParseQuery<ParseRole> role = ParseRole.getQuery();
                                role.whereEqualTo("name", "users");
                                role.getFirstInBackground(new GetCallback<ParseRole>() {
                                    @Override
                                    public void done(ParseRole object, ParseException e) {
                                        object.getUsers().add(ParseUser.getCurrentUser());
                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    getfbDetails(ParseUser.getCurrentUser());
                                                } else Log.e("Acl", e.getMessage());
                                            }
                                        });
                                    }
                                });

                                Log.d("MyApp", "User signed up and logged in through Facebook!");

                            } else {
                                updateInstallation(user); //User is already created. update installation id
                            }

                        } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                            Snackbar.make(coordinatorLayout, "Error in connection", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void getfbDetails(final ParseUser user) {

        Bundle parameters = new Bundle();
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
                            user.put("rewardWallet", Defaults.FIRST_TIME_REWARD);
                            user.put("bhawanCode", bhawanCode);
                            user.put("bhawanName", bhawanName);
                            user.put("verified", verifyDetails());

                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        updateInstallation(user);
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

    private void updateInstallation(ParseUser parseUser) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("userId", parseUser.getObjectId());
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (pd != null)
                        pd.dismiss();
                    startDataLoadActivity();
                } else Log.d("LoginInstallation", e.getMessage());
            }
        });
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

    private boolean verifyDetails() {

        if (pd != null)
            pd.show();

        else {
            pd = new ProgressDialog(Login.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("Verifying");
            pd.setIndeterminate(true);
        }

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
                        } else {
                            Snackbar.make(coordinatorLayout, "Wrong credentials", Snackbar.LENGTH_LONG).show();
                            if (pd != null) {
                                mVerified = false;
                                pd.dismiss();

                            }
                        }
                    }

                } else {
                    if (pd != null) {
                        pd.dismiss();
                    }
                    if (e.getCode() == ParseException.CONNECTION_FAILED) {
                        Snackbar.make(coordinatorLayout, "Error in connection.", Snackbar.LENGTH_LONG).show();
                    } else
                        Snackbar.make(coordinatorLayout, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
        return mVerified;
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
