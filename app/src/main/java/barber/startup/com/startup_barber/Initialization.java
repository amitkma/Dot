package barber.startup.com.startup_barber;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Arish on 08-01-2016.
 */
public class Initialization extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, ParseKeys.PARSE_APPLICATION_ID, ParseKeys.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        FacebookSdk.sdkInitialize(getApplicationContext());

        ParseFacebookUtils.initialize(getApplicationContext());

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
