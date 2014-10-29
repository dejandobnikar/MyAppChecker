package dd.appchecker.aws.service;

import android.content.Context;
import android.content.Intent;

import dd.appchecker.aws.ExternalStorageManager;
import dd.appchecker.aws.data.ApkInfo;

/**
 * Created by Dejan on 23.10.2014.
 */
public class ServiceHelper {


    public static Intent getDownloadIntent(ApkInfo info, Context context) {

        Intent intent = new Intent(context, AWSDownloadService.class);
        intent.setAction(AWSDownloadService.ACTION_DOWNLOAD);
        intent.putExtra(AWSDownloadService.EXTRA_KEY, info.getKey());
        intent.putExtra(AWSDownloadService.EXTRA_BUCKET, info.getBucketName());
        intent.putExtra(AWSDownloadService.EXTRA_ID, info.getId());

        return intent;
    }


    public static Intent getInstallIntent(ApkInfo info, Context context) {

        String path = ExternalStorageManager.getFileUri(info.getBucketName(), info.getKey()).getPath();

        Intent intent = new Intent(context, AWSDownloadService.class);
        intent.setAction(AWSDownloadService.ACTION_INSTALL);
        intent.putExtra(AWSDownloadService.EXTRA_PATH, path);
        intent.putExtra(AWSDownloadService.EXTRA_ID, info.getId());

        return intent;
    }

}
