package dd.appchecker.aws.database;

import java.util.Map;

import dd.appchecker.aws.data.ApkInfo;

/**
 * Created by Dejan on 23.10.2014.
 */
public interface AwsLocalStorage {

    void loadData();

    void saveData();

    boolean insertOrUpdate(ApkInfo apkInfo);

    Map<String, ApkInfo> getData();

    boolean hasNewerVersionNumber(ApkInfo currentInfo, ApkInfo newInfo);

    boolean hasNewerDate(ApkInfo currentInfo, ApkInfo newInfo);

    boolean hasHigherBuildNumber(ApkInfo currentInfo, ApkInfo newInfo);

}
