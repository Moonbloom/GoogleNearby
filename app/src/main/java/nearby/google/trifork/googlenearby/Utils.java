package nearby.google.trifork.googlenearby;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class Utils {

    private static final String TAG = "NERDBY - Utils";

    public static final String KHL = "KHL";
    public static final String ESO = "ESO";
    public static final String MOTO = "MOTO";

    @SuppressLint("HardwareIds")
    public static String getNameFromDeviceId(Context context) {
        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String publishName = "NULL";
        if (id != null) {
            switch (id) {
                case "23d182f3423de3cf":
                    publishName = KHL;
                    break;

                case "86adf4fd4948a0a3":
                    publishName = MOTO;
                    break;
                case "c65270bad031ed46":
                    publishName = ESO;
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