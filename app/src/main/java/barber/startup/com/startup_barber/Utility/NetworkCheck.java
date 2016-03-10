package barber.startup.com.startup_barber.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import barber.startup.com.startup_barber.R;

/**
 * Created by Amit on 09-03-2016.
 */
public class NetworkCheck {
    private static boolean wifiConnected;
    private static boolean mobileConnected;

    public static boolean checkConnection(Context context) {

        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected) {
                Log.i("DATA SAVING", context.getString(R.string.wifi_connection));
                return true;
            } else if (mobileConnected) {
                Log.i("DATA SAVING", context.getString(R.string.mobile_connection));
                return true;
            }
        } else {
            Log.i("DATASAVING", context.getString(R.string.no_wifi_or_mobile));
            return false;
        }
        return false;
    }
}
