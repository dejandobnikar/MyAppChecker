package dd.appchecker.aws.data.helper;

import android.util.Log;

import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import dd.appchecker.aws.data.ApkInfo;

/**
 * Created by Dejan on 21.10.2014.
 */
public class AwsObjectParserImpl implements AwsObjectParser {

    public static final String TAG = "AwsObjectParserImpl";
    public static boolean D = false;

    private static final int MIN_KEY_LENGTH = 7;

    public static final String APK_EXT = ".apk";
    private static final String ROOT_FOLDER = "android";
    private static final String BINARY_FOLDER = "binary";
    private static final String BUILD_PREFIX = "Build";




    public AwsObjectParserImpl() {
    }


    @Override
    public ApkInfo parseObject(S3ObjectSummary objectSummary) {

        String key = objectSummary.getKey();

        List<String> parts =  new LinkedList<String>(Arrays.asList(key.split("/")));

        if (parts == null || parts.size() < MIN_KEY_LENGTH) return null;

        return parseKey(key, parts);
    }


    private ApkInfo parseKey(String key, List<String> parts) {

        //android/release/3.4.x/1.0.0.437/2014-10-07/Build_163_66d0cb577345722d62872a2a2cc15c79e4d42ce8/binary/a.apk

        String apkName = parts.get(parts.size()-1);
        parts.remove(parts.size()-1);

        if ( ! (apkName.endsWith(APK_EXT) || apkName.endsWith(APK_EXT.toUpperCase()) ) ) {
            if (D) Log.d(TAG, "does not contain .apk");
            return null;
        }

        String binary = parts.get(parts.size()-1);
        parts.remove(parts.size()-1);

        if (!binary.equals(BINARY_FOLDER)) {
            if (D) Log.d(TAG, ".apk not in binary folder");
            return null;
        }

        String build = parts.get(parts.size()-1);
        parts.remove(parts.size()-1);

        if (!build.startsWith(BUILD_PREFIX)) {
            if (D) Log.d(TAG, "build folder missing");
            return null;
        }

        String date = parts.get(parts.size()-1);
        parts.remove(parts.size()-1);

        String version = parts.get(parts.size()-1);
        parts.remove(parts.size()-1);

        String root = parts.get(0);
        parts.remove(0);

        if (!root.equals(ROOT_FOLDER)) {
            if (D) Log.d(TAG, "root folder: " + root + ", expected: " + ROOT_FOLDER);
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parts.size(); i++) {
            sb.append(parts.get(i));

            if (i < parts.size()-1) {
                sb.append("-");
            }
        }

        String branch = sb.toString();

        ApkInfo apkInfo = new ApkInfo();
        apkInfo.setApkName(apkName);
        apkInfo.setBuildNumber(parseBuildNumber(build));
        apkInfo.setHash(parseHash(build));
        apkInfo.setDate(date);
        apkInfo.setVersion(version);
        apkInfo.setBranchName(branch);
        apkInfo.setKey(key);

        return apkInfo;
    }


    private int parseBuildNumber(String build) {
        String[] buildParts = build.split("_");

        if (buildParts.length == 3) {
            try {
                return Integer.parseInt(buildParts[1]);
            }
            catch (NumberFormatException e) {
                Log.e(TAG, "cannot parse build number: " + build);
            }
        }

        return 0;
    }

    private String parseHash(String build) {
        String[] buildParts = build.split("_");

        if (buildParts.length == 3) return buildParts[2];

        return null;
    }

}
