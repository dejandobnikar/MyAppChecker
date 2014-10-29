package dd.appchecker.conectivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import javax.inject.Inject;

import dd.appchecker.aws.service.AwsService;

/**
 * Created by Dejan on 20.10.2014.
 */
public class ConnectivityReceiver  extends BaseReceiver {

    private static final String TAG = "ConnectivityReceiver";


    @Inject
    WorkNetworkChecker networkChecker;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context,intent);

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiManager == null) return;

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();

        if (networkChecker.isWorkNetwork(ssid)) {
            Toast.makeText(context, "Work network: " + ssid, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "work network detected: " + ssid);
            Intent i  = new Intent(context, AwsService.class);
            context.startService(i);
        }
        else {
            Log.d(TAG, "Unknown network (" + ssid + ")");
            Toast.makeText(context, "Unknown network (" + ssid + ")", Toast.LENGTH_SHORT).show();
        }




        mObjectGraph = null;
    }
}
