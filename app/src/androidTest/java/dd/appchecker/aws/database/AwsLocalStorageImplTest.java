package dd.appchecker.aws.database;

import android.test.AndroidTestCase;

import com.amazonaws.services.s3.model.ObjectListing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dd.appchecker.aws.data.ApkInfo;

/**
 * Created by Dejan on 24.10.2014.
 */
public class AwsLocalStorageImplTest extends AndroidTestCase {

    AwsLocalStorageImpl localStorage;
    StorageAdapter storageAdapter;

    List<ApkInfo> apkInfoList;


    public void setUp() {

        apkInfoList = new ArrayList<ApkInfo>();
        storageAdapter = new SharedPrefStorage(getContext());
        localStorage = new AwsLocalStorageImpl(getContext(), storageAdapter);


    }


    private ApkInfo getApkInfo1() {

        ApkInfo i1 = new ApkInfo();

        i1.setBucketName("bucket1");
        i1.setDate("2014-11-10");
        i1.setApkName("apk1.apk");
        i1.setBuildNumber(23);
        i1.setHash("jdfkabdojsfabofesjbcesabcjosea");
        i1.setBranchName("release");
        i1.setKey("asdfjkl");
        i1.setVersion("3.2.1");
        i1.setId(1);

        return i1;

    }

    private ApkInfo getApkInfo2() {

        ApkInfo i2 = new ApkInfo();

        i2.setBucketName("bucket2");

        i2.setDate("2014-11-10");

        i2.setApkName("apk2.apk");

        i2.setBuildNumber(31);

        i2.setHash("bcoeiabausbczasebcoaszbeozbcau");

        i2.setBranchName("features");

        i2.setKey("qerwerqewuiqo");

        i2.setVersion("4.1.2");

        i2.setId(2);

        return i2;

    }



    public void testStorage() {

        ApkInfo i1 = getApkInfo1();
        ApkInfo i2 = getApkInfo2();


        storageAdapter.clearData();

        assertTrue(localStorage.insertOrUpdate(i1));

        assertTrue(localStorage.insertOrUpdate(i2));

        localStorage = new AwsLocalStorageImpl(getContext(), storageAdapter);

        Map<String,ApkInfo> map = localStorage.getData();

        assertNotNull(map.get(i1.getBucketBranchName()));
        assertNotNull(map.get(i2.getBucketBranchName()));

        assertTrue(compare(i1, map.get(i1.getBucketBranchName())));
        assertTrue(compare(i2, map.get(i2.getBucketBranchName())));

        i1 = getApkInfo1();
        i1.setVersion("3.2.1");

        i1 = getApkInfo1();
        i1.setVersion("3.2.1.0.1");
        assertTrue(localStorage.insertOrUpdate(i1));

        i1 = getApkInfo1();
        i1.setVersion("3.2.1.1");
        assertTrue(localStorage.insertOrUpdate(i1));

        i1 = getApkInfo1();
        i1.setVersion("3.2.1.0.1");
        assertFalse(localStorage.insertOrUpdate(i1));

        i1 = getApkInfo1();
        i1.setVersion("3.2.1.0.1.0");
        assertFalse(localStorage.insertOrUpdate(i1));

        i1 = getApkInfo1();
        i1.setBuildNumber(44);
        assertTrue(localStorage.insertOrUpdate(i1));

        i1 = getApkInfo1();
        i1.setBuildNumber(45);
        assertTrue(localStorage.insertOrUpdate(i1));

        i1 = getApkInfo1();
        i1.setBuildNumber(44);
        assertFalse(localStorage.insertOrUpdate(i1));

        i2= getApkInfo2();
        i2.setDate("2014-11-11");
        assertTrue(localStorage.insertOrUpdate(i2));

        i2= getApkInfo2();
        i2.setDate("2014-12-11");
        assertTrue(localStorage.insertOrUpdate(i2));

        i2= getApkInfo2();
        i2.setDate("2015-01-11");
        assertTrue(localStorage.insertOrUpdate(i2));

        i2= getApkInfo2();
        i2.setDate("2016-01-11");
        assertTrue(localStorage.insertOrUpdate(i2));

        i2= getApkInfo2();
        i2.setDate("2016-02-11");
        assertTrue(localStorage.insertOrUpdate(i2));

        i2= getApkInfo2();
        i2.setDate("2016-02-12");
        assertTrue(localStorage.insertOrUpdate(i2));
        assertFalse(localStorage.insertOrUpdate(i2 ) );
    }




    private boolean compare(ApkInfo i1, ApkInfo i2) {

        return i1.getBucketBranchName().equals(i2.getBucketBranchName()) &&
                i1.getBucketName().equals(i2.getBucketName()) &&
                i1.getVersion().equals(i2.getVersion()) &&
                i1.getBuildNumber() == i2.getBuildNumber() &&
                i1.getApkName().equals(i2.getApkName()) &&
                i1.getBranchName().equals(i2.getBranchName()) &&
                i1.getCalendar().equals(i2.getCalendar()) &&
                i1.getDate().equals(i2.getDate()) &&
                i1.getHash().equals(i2.getHash()) &&
                i1.getId() == i2.getId() &&
                i1.getKey().equals(i2.getKey());

    }




}
