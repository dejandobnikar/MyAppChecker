package dd.appchecker.aws;

import android.net.Uri;
import android.os.Environment;

import java.io.File;

import dd.appchecker.aws.data.ApkInfo;

/**
 * Created by Dejan on 22.10.2014.
 */
public class ExternalStorageManager {


    private static final String DAPPS = "DApps";


    public static boolean isExternalStorageAvailable() {
        return  isExternalStorageReadable() && isExternalStorageWritable();
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    public static Uri getDirectoryUri(String bucketName, String key) {
        return Uri.parse(Environment.getExternalStorageDirectory()
                + File.separator
                + DAPPS
                + File.separator
                + bucketName
                + File.separator
                + ApkInfo.getDirectory(key));
    }

    public static Uri getFileUri(String bucketName, String key) {

        return Uri.parse(Environment.getExternalStorageDirectory()
                + File.separator
                + DAPPS
                + File.separator
                + bucketName
                + File.separator
                + key);

    }

}
