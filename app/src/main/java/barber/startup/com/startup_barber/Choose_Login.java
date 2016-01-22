package barber.startup.com.startup_barber;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class Choose_Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    TextView login;
    TextView marquee_login;
    TextView trend;
    List<String> permissions;
    private LoginButton fb_loginButton;
    private CallbackManager callbackManager;

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "Tag";
    private GoogleApiClient mGoogleApiClient;
    private TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);

        signInButton.setScopes(gso.getScopeArray());

        signInButton.setOnClickListener(this);

     /*   if (ParseUser.getCurrentUser() != null) {
            //startActivity(new Intent(this, CurrentTrendsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY));
        } else if (Profile.getCurrentProfile() != null)
            //startActivity(new Intent(this, CurrentTrendsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY));
*/


        login = (TextView) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.register);
        marquee_login = (TextView) findViewById(R.id.MarqueeText);
        trend = (TextView) findViewById(R.id.textView2);
        fb_loginButton = (LoginButton) findViewById(R.id.login_button);
        marquee_login.setSelected(true);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startloginactivity();

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startsignupactivity();
            }
        });
        callbackManager = CallbackManager.Factory.create();
        permissions = Arrays.asList("email");

        // App code

        fb_loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                link_with_parse();

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e("Facebook", exception.toString());
            }
        });
    }

    private void startsignupactivity() {

        startActivity(new Intent(this, Signup_Activity.class));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }

    private void link_with_parse() {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    start_current_trends_activity();
                    Toast.makeText(getApplicationContext(), "Signed up successfully through Facebook !", Toast.LENGTH_SHORT);
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {
                    start_current_trends_activity();
                    Log.d("MyApp", "User logged in through Facebook!");

                }

            }
        });
    }

    private void start_current_trends_activity() {
      // startActivity(new Intent(this, CurrentTrendsActivity.class));
       finish();
    }

    private void startloginactivity() {
      //  Intent i = new Intent(this, Login_Activity.class);

           startActivity(new Intent(this, Login_Activity.class));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(getApplicationContext(), acct.getDisplayName(), Toast.LENGTH_SHORT).show();

            //startActivity(new Intent(this, Main2Activity.class));
            finsihing();

        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private void finsihing() {
        finish();
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onStart() {
        super.onStart();

       OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {

                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
