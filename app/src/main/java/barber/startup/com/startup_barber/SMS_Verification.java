package barber.startup.com.startup_barber;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sinch.verification.PhoneNumberFormattingTextWatcher;
import com.sinch.verification.PhoneNumberUtils;

public class SMS_Verification extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    public static final String SMS = "sms";
    public static final String FLASHCALL = "flashcall";
    public static final String INTENT_PHONENUMBER = "phonenumber";
    public static final String INTENT_METHOD = "method";

    private EditText mPhoneNumberField;
    private Button mVerifySMSButton;
    private TextWatcher mNumberTextWatcher;
    private String mCountryIso;
    private Button mVerifyCallButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_sms__verification);

        mPhoneNumberField = (EditText)findViewById(R.id.PhoneNumber);
        mVerifySMSButton = (Button) findViewById(R.id.verify_sms_button_id);
        mVerifyCallButton = (Button)findViewById(R.id.verify_call_button_id);
        mCountryIso = PhoneNumberUtils.getDefaultCountryIso(this);

        resetNumberTextWatcher(mCountryIso);
        tryAndPrefillPhoneNumber();
    }
    private void tryAndPrefillPhoneNumber() {
        if (checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mPhoneNumberField.setText(manager.getLine1Number());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            tryAndPrefillPhoneNumber();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "This application needs permission to read your phone number to automatically "
                        + "pre-fill it", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openActivity(String phoneNumber, String method) {
        Intent verification = new Intent(this, VerificationActivity.class);
        verification.putExtra(INTENT_PHONENUMBER, phoneNumber);
        verification.putExtra(INTENT_METHOD, method);
        startActivity(verification);
    }


    private void setButtonsEnabled(boolean enabled) {
        mVerifySMSButton.setEnabled(enabled);
        mVerifyCallButton.setEnabled(enabled);
    }
    public void onButtonClicked(View view) {
        if (view == mVerifySMSButton) {
            openActivity(getE164Number(), SMS);
        }

       else if(view == mVerifyCallButton){
            openActivity(getE164Number(), FLASHCALL);
        }


    }

    private void resetNumberTextWatcher(String countryIso) {

        if (mNumberTextWatcher != null) {
            mPhoneNumberField.removeTextChangedListener(mNumberTextWatcher);
        }

        mNumberTextWatcher = new PhoneNumberFormattingTextWatcher(countryIso) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                super.beforeTextChanged(s, start, count, after);
            }

            @Override
            public synchronized void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (isPossiblePhoneNumber()) {
                    setButtonsEnabled(true);
                    mPhoneNumberField.setTextColor(Color.BLACK);
                } else {
                    setButtonsEnabled(false);
                    mPhoneNumberField.setTextColor(getResources().getColor(R.color.accent));
                }
            }
        };

        mPhoneNumberField.addTextChangedListener(mNumberTextWatcher);
    }

    private boolean isPossiblePhoneNumber() {
        return PhoneNumberUtils.isPossibleNumber(mPhoneNumberField.getText().toString(), mCountryIso);
    }

    private String getE164Number() {
        return PhoneNumberUtils.formatNumberToE164(mPhoneNumberField.getText().toString(), mCountryIso);
    }

}
