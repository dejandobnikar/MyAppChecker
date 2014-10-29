package dd.appchecker.aws.database;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dd.appchecker.aws.data.ApkInfo;

/**
 * Created by Dejan on 21.10.2014.
 */
public class AwsLocalStorageImpl implements AwsLocalStorage {

    private static final String TAG = "AwsLocalStorage";

    private Map<String, ApkInfo> mApkMap;
    private Context mContext;
    private StorageAdapter mStorageAdapter;
    private int id;


    public AwsLocalStorageImpl(Context context, StorageAdapter storageAdapter) {
        mApkMap = new HashMap<String, ApkInfo>();
        mContext = context;
        mStorageAdapter = storageAdapter;
        id=1;
        loadData();

    }


    @Override
    public Map<String, ApkInfo> getData() {
        return mApkMap;
    }


    @Override
    public void loadData() {
        List<ApkInfo> list = mStorageAdapter.loadApkData();

        mApkMap.clear();

        for (ApkInfo info : list) {
            id = Math.max(id, info.getId());

            mApkMap.put(info.getBucketBranchName(), info);
        }

        print();
    }


    @Override
    public void saveData() {
        mStorageAdapter.saveApkData(mApkMap);
    }


    private void insert(ApkInfo info) {
        info.setId(++id);
        mApkMap.put(info.getBucketBranchName(), info);
        saveData(); //async save with editor.apply() instead of editor.commit
    }


    @Override
    public boolean insertOrUpdate(ApkInfo apkInfo) {

        ApkInfo currentInfo = mApkMap.get(apkInfo.getBucketBranchName());

        if (currentInfo == null) {
            // no info in map
            Log.d(TAG, "no entry for " + apkInfo.getBucketBranchName() + ", inserting");
            insert(apkInfo);
            return true;
        }
        else {
            return compareAndInsert(currentInfo, apkInfo);
        }
    }


    private boolean compareAndInsert(ApkInfo currentInfo, ApkInfo newInfo) {

        if (hasNewerVersionNumber(currentInfo, newInfo)) {
            Log.d(TAG,  newInfo.getBucketBranchName() + " has newer version number (" + newInfo.getVersion() + " > " + currentInfo.getVersion());
            //mApkMap.put(newInfo.getBranchName(), newInfo);
            insert(newInfo);
            return true;
        }
        else if (hasNewerDate(currentInfo, newInfo)) {
            Log.d(TAG,  newInfo.getBucketBranchName() + " has newer date (" + newInfo.getDate() + " > " + currentInfo.getDate());
            //mApkMap.put(newInfo.getBranchName(), newInfo);
            insert(newInfo);
            return true;
        }
        else if (hasHigherBuildNumber(currentInfo, newInfo)) {
            Log.d(TAG,  newInfo.getBucketBranchName() + " has higher build number (" + newInfo.getBuildNumber() + " > " + currentInfo.getBuildNumber());
           // mApkMap.put(newInfo.getBranchName(), newInfo);
            insert(newInfo);
            return true;
        }

        Log.d(TAG,  "this is old version: " + newInfo.toString());
        return false;
    }


    @Override
    public boolean hasHigherBuildNumber(ApkInfo currentInfo, ApkInfo newInfo) {
        return newInfo.getBuildNumber() > currentInfo.getBuildNumber();
    }


    @Override
    public boolean hasNewerDate(ApkInfo currentInfo, ApkInfo newInfo) {

        Calendar currentCal = currentInfo.getCalendar();
        Calendar newCal = newInfo.getCalendar();

        if (currentCal != null && newCal != null) {
            return newCal.after(currentCal);
        }

        return false;
    }


    @Override
    public boolean hasNewerVersionNumber(ApkInfo currentInfo, ApkInfo newInfo) {

       // return versionCompare(currentInfo.getVersion(), newInfo.getVersion()) < 0;

        int i = versionCompareV2(currentInfo.getVersion(), newInfo.getVersion());

        System.out.println(currentInfo.getVersion() + " vs " + newInfo.getVersion() + " : " + i);

        return i > 0;
    }



    public static int versionCompareV2(String str1, String str2) {

        String[] str1Array = str1.split("\\.");
        String[] str2Array = str2.split("\\.");

        int maxLength = Math.max(str1Array.length, str2Array.length);

        int[] int1Array = new int[maxLength];
        int[] int2Array = new int[maxLength];


        for (int i = 0; i < str1Array.length; i++) {

            try {
                int1Array[i] = Integer.parseInt(str1Array[i]);
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < str2Array.length; i++) {

            try {
                int2Array[i] = Integer.parseInt(str2Array[i]);
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }


        for (int i = 0; i < int1Array.length; i++) {
            int i1 = int1Array[i];
            int i2 = int2Array[i];

            if (i2 > i1) return 1;
            if (i2 < i1) return -1;
        }

        return 0;


    }


    public void print() {

        Log.d(TAG, "Map with newest entries only:");

        for (Map.Entry<String, ApkInfo> entry : mApkMap.entrySet()) {
            String key = entry.getKey();
            ApkInfo value = entry.getValue();

            Log.d(TAG, "  " + value.toString());
        }
    }

}
