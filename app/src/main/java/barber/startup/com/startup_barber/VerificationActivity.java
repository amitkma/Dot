package barber.startup.com.startup_barber;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sinch.verification.CodeInterceptionException;
import com.sinch.verification.Config;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

public class VerificationActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = Verification.class.getSimpleName();
    private final String APPLICATION_KEY = "2fa00d71-9c21-471a-ba3e-1400d0201aef";

    private Verification mVerification;
    private String phoneNumber;
    private ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ParseUser.getCurrentUser();
        setContentView(R.layout.activity_verification);
        showProgress();
        initiateVerification();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    void createVerification(String phoneNumber, String method, boolean skipPermissionCheck) {
        Config config = SinchVerification.config().applicationKey(APPLICATION_KEY).context(getApplicationContext())
                .build();
        VerificationListener listener = new MyVerificationListener();

        if (method.equalsIgnoreCase(SMS_Verification.SMS)) {

            if (!skipPermissionCheck && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) ==
                    PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 0);
                hideProgressBar();
            } else {
                mVerification = SinchVerification.createSmsVerification(config, phoneNumber, listener);
                mVerification.initiate();
            }

        } else {
            TextView messageText = (TextView) findViewById(R.id.textView);
            messageText.setText(R.string.flashcalling);
            mVerification = SinchVerification.createFlashCallVerification(config, phoneNumber, listener);
            mVerification.initiate();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "This application needs permission to read your SMS to automatically verify your "
                        + "phone, you may disable the permission once you have been verified.", Toast.LENGTH_LONG)
                        .show();
            }
            enableInputField(true);
        }
        initiateVerificationAndSuppressPermissionCheck();
    }

    void initiateVerification() {
        initiateVerification(false);
    }

    void initiateVerificationAndSuppressPermissionCheck() {
        initiateVerification(true);
    }

    void initiateVerification(boolean skipPermissionCheck) {
        Intent intent = getIntent();
        if (intent != null) {
            phoneNumber = intent.getStringExtra(SMS_Verification.INTENT_PHONENUMBER);
            String method = intent.getStringExtra(SMS_Verification.INTENT_METHOD);
            TextView phoneText = (TextView) findViewById(R.id.numberText);
            phoneText.setText(phoneNumber);
            createVerification(phoneNumber, method, skipPermissionCheck);
        }
    }

    public void onSubmitClicked(View view) {
        String code = ((EditText) findViewById(R.id.inputCode)).getText().toString();
        if (!code.isEmpty()) {
            if (mVerification != null) {
                mVerification.verify(code);
                showProgress();
                TextView messageText = (TextView) findViewById(R.id.textView);
                messageText.setText("Verification in progress");
                enableInputField(false);
            }
        }
    }

    void enableInputField(boolean enable) {
        View container = findViewById(R.id.inputContainer);
        if (enable) {
            container.setVisibility(View.VISIBLE);
            EditText input = (EditText) findViewById(R.id.inputCode);
            input.requestFocus();
        } else {
            container.setVisibility(View.GONE);
        }
    }

    void hideProgressBarAndShowMessage(int message) {
        hideProgressBar();
        TextView messageText = (TextView) findViewById(R.id.textView);
        messageText.setText(message);
    }

    void hideProgressBar() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.INVISIBLE);
        TextView progressText = (TextView) findViewById(R.id.progressText);
        progressText.setVisibility(View.INVISIBLE);
    }

    void showProgress() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.VISIBLE);
    }

    void showCompleted() {


        enterReveal();


        ParseACL acl = new ParseACL();
        acl.setWriteAccess(ParseUser.getCurrentUser(), true);
        ParseUser.getCurrentUser().setACL(acl);
        ParseUser user = ParseUser.getCurrentUser();
        Log.d("auth", String.valueOf(user.isAuthenticated()));
        user.put("verified", true);
        user.put("number", phoneNumber);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) e.printStackTrace();
                startmainactiviy();
            }
        });


    }

    void enterReveal() {
        ImageView checkMark = (ImageView) findViewById(R.id.checkmarkImage);

        int cx = checkMark.getMeasuredWidth() / 2;
        int cy = checkMark.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(checkMark.getWidth(), checkMark.getHeight()) / 2;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator anim =
                    null;

            anim = ViewAnimationUtils.createCircularReveal(checkMark, cx, cy, 0, finalRadius);

            checkMark.setVisibility(View.VISIBLE);
            anim.setDuration(500);
            anim.start();
        } else
            checkMark.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startmainactiviy();
            }
        }, 5000);

    }

    private void startmainactiviy() {
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        finish();

    }


    class MyVerificationListener implements VerificationListener {

        @Override
        public void onInitiated() {
            Log.d(TAG, "Initialized!");
            showProgress();
        }

        @Override
        public void onInitiationFailed(Exception exception) {
            Log.e(TAG, "Verification initialization failed: " + exception.getMessage());
            hideProgressBarAndShowMessage(R.string.failed);
        }

        @Override
        public void onVerified() {
            Log.d(TAG, "Verified!");
            hideProgressBarAndShowMessage(R.string.verified);
            showCompleted();
        }

        @Override
        public void onVerificationFailed(Exception exception) {
            Log.e(TAG, "Verification failed: " + exception.getMessage());
            if (exception instanceof CodeInterceptionException) {
                hideProgressBar();
            } else {
                Log.e("ERROR", exception.toString());
                hideProgressBarAndShowMessage(R.string.failed);
            }
            enableInputField(true);
        }
    }

}
