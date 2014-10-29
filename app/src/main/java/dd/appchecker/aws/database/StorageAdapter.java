package dd.appchecker.aws.database;

import java.util.List;
import java.util.Map;

import dd.appchecker.aws.data.ApkInfo;

/**
 * Created by Dejan on 21.10.2014.
 */
public interface StorageAdapter {

    void saveApkData(Map<String, ApkInfo> data);

    List<ApkInfo> loadApkData();
    //Map<String, ApkInfo> loadApkData();

    void clearData();

}
