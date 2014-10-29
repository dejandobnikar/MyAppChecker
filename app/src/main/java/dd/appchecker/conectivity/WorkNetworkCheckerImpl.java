package dd.appchecker.conectivity;

import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Dejan on 20.10.2014.
 */
public class WorkNetworkCheckerImpl implements WorkNetworkChecker {
    private static final String TAG = "WorkNetworkCheckerImpl";

    private static final String[] workNetworks = new String[] {
            "DOFFICE",
            "DOFFICE5",
            "DOFFICE6",
            "DOFFICE7",
            "DGUEST"
    };


    @Override
    public boolean isWorkNetwork(String ssid) {

        // should always return ssid in quotation marks(documentation), bug (no "") in pre-4.2
        // http://stackoverflow.com/questions/13563032/jelly-bean-issue-wifimanager-getconnectioninfo-getssid-extra
        // pre 4.2 "" check only?
        if (ssid.startsWith("\"") && ssid.endsWith("\"")){
            ssid = ssid.substring(1, ssid.length()-1);
        }

        return Arrays.asList(workNetworks).contains(ssid.trim().toUpperCase(Locale.US));
    }
}
