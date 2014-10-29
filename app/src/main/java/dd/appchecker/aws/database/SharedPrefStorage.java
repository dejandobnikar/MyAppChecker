package dd.appchecker.aws.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dd.appchecker.aws.data.ApkInfo;

/**
 * Created by Dejan on 21.10.2014.
 */
public class SharedPrefStorage implements StorageAdapter {

    private static final String PREF_NAME = "apk_prefs";
    private static final String KEY_COUNT = "count";

    private static final String KEY_BUILD_NUM = "build_num";
    private static final String KEY_BRANCH_NAME = "branch";
    private static final String KEY_VERSION = "version";
    private static final String KEY_HASH = "hash";
    private static final String KEY_APK_NAME = "apk_name";
    private static final String KEY_DATE = "date";
    private static final String KEY_KEY = "key";
    private static final String KEY_ID = "id";
    private static final String KEY_BUCKET = "bucket";

    private Context mContext;

    public SharedPrefStorage(Context context) {
        mContext = context;
    }


    private String getKey(int i, String key) {
        return i + "_" + key;
    }

    @Override
    public void saveApkData(Map<String, ApkInfo> data) {

        List<ApkInfo> list  = new ArrayList<ApkInfo>(data.values());

        int count = list.size();

        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(KEY_COUNT, count);

        for (int i = 0; i < count; i++) {
            ApkInfo info = list.get(i);

            editor.putString(getKey(i, KEY_APK_NAME), info.getApkName());
            editor.putString(getKey(i, KEY_BRANCH_NAME), info.getBranchName());
            editor.putInt(getKey(i, KEY_BUILD_NUM), info.getBuildNumber());
            editor.putString(getKey(i, KEY_DATE), info.getDate());
            editor.putString(getKey(i, KEY_VERSION), info.getVersion());
            editor.putString(getKey(i, KEY_HASH), info.getHash());
            editor.putString(getKey(i, KEY_KEY), info.getKey());
            editor.putInt(getKey(i, KEY_ID), info.getId());
            editor.putString(getKey(i, KEY_BUCKET), info.getBucketName());
        }

        //editor.commit();
        editor.apply();
    }

    @Override
    public List<ApkInfo> loadApkData() {

        List<ApkInfo> list = new ArrayList<ApkInfo>();

        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, 0);

        int count = prefs.getInt(KEY_COUNT, 0);

        for ( int i = 0; i < count; i++) {
            ApkInfo info = new ApkInfo();
            info.setBranchName(prefs.getString(getKey(i,KEY_BRANCH_NAME), ""));
            info.setVersion(prefs.getString(getKey(i,KEY_VERSION), ""));
            info.setHash(prefs.getString(getKey(i, KEY_HASH), ""));
            info.setBuildNumber(prefs.getInt(getKey(i, KEY_BUILD_NUM), 0));
            info.setApkName(prefs.getString(getKey(i, KEY_APK_NAME), ""));
            info.setDate(prefs.getString(getKey(i, KEY_DATE), ""));
            info.setKey(prefs.getString(getKey(i, KEY_KEY), ""));
            info.setId(prefs.getInt(getKey(i, KEY_ID), 0));
            info.setBucketName(prefs.getString(getKey(i, KEY_BUCKET), ""));

            list.add(info);
        }

        return list;
    }


    @Override
    public void clearData() {

    }
}
