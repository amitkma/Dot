package barber.startup.com.startup_barber;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Arish on 08-01-2016.
 */
public class Application extends android.app.Application {
    public static final boolean DEBUG = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());



        Parse.enableLocalDatastore(this);
        Parse.initialize(this, ParseKeys.PARSE_APPLICATION_ID, ParseKeys.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());

    }
}
