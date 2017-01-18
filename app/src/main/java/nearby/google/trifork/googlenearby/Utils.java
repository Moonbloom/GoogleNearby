package nearby.google.trifork.googlenearby;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class Utils {

    private static final String TAG = "NERDBY - Utils";

    public static String getNameFromDeviceId(Context context) {
        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String publishName = "NULL";
        if (id != null) {
            switch (id) {
                case "23d182f3423de3cf":
                    publishName = "KHL";
                    break;
                case "c65270bad031ed46":
                    publishName = "ESO";
                    break;
                default:
                    Log.d(TAG, "UNKNOWN ID: " + id);
                    publishName = "UNKNOWN";
                    break;
            }
        }

        return publishName;
    }
}