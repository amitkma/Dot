package barber.startup.com.startup_barber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Login_Activity extends AppCompatActivity {
    private Button loginButton;
    private Button signupButton;
    private EditText usernameField;
    private EditText userPasswordField;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButtonId);
        signupButton = (Button) findViewById(R.id.signupButtonId);
        usernameField = (EditText) findViewById(R.id.username);
        userPasswordField = (EditText) findViewById(R.id.userPassword);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_connection() == false)
                    Snackbar.make(findViewById(R.id.coordinatorlayout_login_activity), R.string.network_state_false, Snackbar.LENGTH_SHORT).show();
                else if (usernameField.getText().toString().trim().length() != 0 || userPasswordField.getText().toString().trim().length() != 0)
                    loginParse();
                else {
                    dismissProgressBar();
                    Snackbar.make(findViewById(R.id.coordinatorlayout_login_activity), R.string.empty, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check_connection() == false)
                    Snackbar.make(findViewById(R.id.coordinatorlayout_login_activity), R.string.network_state_false, Snackbar.LENGTH_SHORT).show();
                else if (usernameField.getText().toString().trim().length() != 0 && userPasswordField.getText().toString().trim().length() != 0)
                    signupParse();
                else if (userPasswordField.getText().toString().trim().length() == 0)
                    Snackbar.make(findViewById(R.id.coordinatorlayout_login_activity), R.string.empty, Snackbar.LENGTH_SHORT).show();
            }
        });


    }


    private void loginParse() {
        showProgressBar();

        ParseUser.logInInBackground(usernameField.getText().toString().trim(), userPasswordField.getText().toString().trim(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "successfully logged in", Toast.LENGTH_SHORT).show();
                    dismissProgressBar();
                    //  Intent intent = new Intent(MainActivity.this, CurrentTrendsActivity.class);
                    finish();
                    //startActivity(intent);
                } else {
                    switch (e.getCode()) {
                        case ParseException.OBJECT_NOT_FOUND:
                            dismissProgressBar();
                            Snackbar.make(findViewById(R.id.coordinatorlayout_login_activity), e.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                        case ParseException.CONNECTION_FAILED:
                            dismissProgressBar();
                            Snackbar.make(findViewById(R.id.coordinatorlayout_login_activity), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            dismissProgressBar();
                            Snackbar.make(findViewById(R.id.coordinatorlayout_login_activity), R.string.error_unclassified, Snackbar.LENGTH_SHORT).show();
                            e.printStackTrace();
                            break;
                    }
                }
            }
        });
    }


    private void signupParse() {
        showProgressBar("Signing up! Please wait");

        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(usernameField.getText().toString().trim());
        parseUser.setPassword(userPasswordField.getText().toString().trim());

        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {


                if (e == null) {
                    dismissProgressBar();

                    Toast.makeText(getApplicationContext(), "successfully created Parse user", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Login_Activity.this, SMS_Verification.class);
                    startActivity(intent);
                    finish();
                } else {
                    switch (e.getCode()) {

                        case ParseException.USERNAME_TAKEN:
                            dismissProgressBar();
                            Snackbar.make(findViewById(R.id.coordinatorlayout_login_activity), e.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                        case ParseException.CONNECTION_FAILED:
                            dismissProgressBar();
                            Snackbar.make(findViewById(R.id.coordinatorlayout_login_activity), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            dismissProgressBar();
                            Snackbar.make(findViewById(R.id.coordinatorlayout_login_activity), R.string.error_unclassified, Snackbar.LENGTH_SHORT).show();
                            e.printStackTrace();
                            break;
                    }

                }


            }
        });
    }

    public boolean check_connection() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

//For 3G check
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
//For WiFi Check
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        System.out.println(is3g + " net " + isWifi);

        return !(!is3g && !isWifi);


    }


    public void showProgressBar() {
        progressDialog = ProgressDialog.show(this, "", "Loading! Please wait", true);
    }

    public void showProgressBar(String msg) {

        progressDialog = ProgressDialog.show(this, "", msg, true);

    }

    public void dismissProgressBar() {
        if (progressDialog !=null)
            progressDialog.dismiss();
    }
}
