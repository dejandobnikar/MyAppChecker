package dd.appchecker.aws.data;

import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

import dd.appchecker.aws.data.helper.AwsObjectParserImpl;

/**
 * Created by Dejan on 21.10.2014.
 */
public class ApkInfo {

    private int id;
    private int mBuildNumber;
    private String mBranchName;
    private String mDate;
    private String mVersion;
    private String mApkName;
    private String mHash;
    private String mKey;
    private String mBucketName;

    private boolean exsistsOnFileSystem;

    public int getBuildNumber() {
        return mBuildNumber;
    }


    public String getBranchName() {
        return mBranchName;
    }


    public String getVersion() {
        return mVersion;
    }


    public String getDate() {
        return mDate;
    }


    public String getHash() {
        return mHash;
    }

    public String getBucketName() {
        return mBucketName;
    }

    public void setBucketName(String mBucketName) {
        this.mBucketName = mBucketName;
    }

    public String getBucketBranchName() {
        return mBucketName + "_" + mBranchName;
    }

    public boolean hasBinary() {
        return mApkName != null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApkName() {
        return mApkName;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void setBuildNumber(int mBuildNumber) {
        this.mBuildNumber = mBuildNumber;
    }

    public void setBranchName(String mBranchName) {
        this.mBranchName = mBranchName;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public void setVersion(String mVersion) {
        this.mVersion = mVersion;
    }

    public void setApkName(String mApkName) {
        this.mApkName = mApkName;
    }

    public void setHash(String mHash) {
        this.mHash = mHash;
    }

    public boolean exsistsOnFileSystem() {
        return exsistsOnFileSystem;
    }

    public void setExsistsOnFileSystem(boolean exsistsOnFileSystem) {
        this.exsistsOnFileSystem = exsistsOnFileSystem;
    }

    public Calendar getCalendar() {

        if (mDate == null) return null;

        String[] date = mDate.split("-");

        if (date.length != 3) return null;

        int year;
        int month;
        int day;

        try {
            year = Integer.parseInt(date[0]);
            month = Integer.parseInt(date[1]);
            day = Integer.parseInt(date[2]);
        }
        catch (NumberFormatException e) {
            Log.e("ApkInfo", "cannot create calendar for " + mDate);
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }


    public static String getDirectory(String key) {
        if (key == null) return null;

        Uri uri = Uri.parse(key);

        String apkName = uri.getLastPathSegment();

        if (apkName.endsWith(AwsObjectParserImpl.APK_EXT) || apkName.endsWith(AwsObjectParserImpl.APK_EXT.toUpperCase())) {

            return key.substring(0, key.length() - apkName.length());
        }

        return null;
    }


    @Override
    public boolean equals(Object o) {

        if (o == null) return false;
        if (! (o instanceof ApkInfo ) ) return false;

        ApkInfo obj = (ApkInfo) o;

        if (obj.getBranchName() == null || mBranchName == null ) return false;

        if (mBranchName.equals(obj.getBranchName()) ) return true;

        return false;
    }

    @Override
    public String toString() {
        return "apk: " + mApkName + " branch: " + mBranchName + " date: " + mDate + " buildNum: " + mBuildNumber + " version: " +mVersion;
    }
    


}
