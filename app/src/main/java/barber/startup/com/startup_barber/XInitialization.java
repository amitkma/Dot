package barber.startup.com.startup_barber;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Arish on 08-01-2016.
 */
public class XInitialization extends Application {
    public static final boolean APPDEBUG = true;
    static ParseUser parseUser = null;
    private final String TAG = "XInitialization";

    @Override
    public void onCreate() {
        super.onCreate();
        if (XInitialization.APPDEBUG)
            Log.d(TAG, "onCreate() called");
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, ParseKeys.PARSE_APPLICATION_ID, ParseKeys.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        if (parseUser == null)
            parseUser = ParseUser.getCurrentUser();

        if (parseUser != null)
            if (XInitialization.APPDEBUG)
                Log.d(TAG, parseUser.getUsername());

        FacebookSdk.sdkInitialize(getApplicationContext());

        ParseFacebookUtils.initialize(getApplicationContext());

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
