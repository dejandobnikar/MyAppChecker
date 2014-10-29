package dd.appchecker.aws.service;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dd.appchecker.aws.ExternalStorageManager;
import dd.appchecker.aws.data.DownloadInfo;
import dd.appchecker.aws.tasks.observers.KeyObserver;
import dd.appchecker.aws.tasks.AWSDownloadTask;
import dd.appchecker.event.DownloadCompletedEvent;
import de.greenrobot.event.EventBus;
import rx.Subscription;

/**
 * Created by Dejan on 21.10.2014.
 */
public class AWSDownloadService extends BaseService {

    private static final String TAG = "AWSDownloadService";

    public static final String ACTION_DOWNLOAD = "action_download";
    public static final String ACTION_INSTALL = "action_install";

    public static final String EXTRA_KEY = "extra_key";
    public static final String EXTRA_BUCKET = "extra_bucket";

    public static final String EXTRA_PATH = "extra_path";
    public static final String EXTRA_ID = "extra_id";

    private static final int PROGRESS_NOTIFICATION_ID = 777;

    @Inject
    AWSDownloadTask task;

    private Map<String, Subscription> mSubsMap;


    @Override
    public void onCreate() {
        super.onCreate();
        mSubsMap = new HashMap<String, Subscription>();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            String action = intent.getAction();

            if (ACTION_DOWNLOAD.equals(action)) {
                String bucketName = intent.getStringExtra(EXTRA_BUCKET);
                String key = intent.getStringExtra(EXTRA_KEY);
                int id = intent.getIntExtra(EXTRA_ID, -1);

                if (id >= 0) {
                    clearNotification(id);
                }

                requestDownload(new DownloadInfo(bucketName, key));
            }
            else if (ACTION_INSTALL.equals(action)) {
                String path = intent.getStringExtra(EXTRA_PATH);
                int id = intent.getIntExtra(EXTRA_ID, -1);
                clearNotification(id);
                // clear notification if exists
                installApk(path);
            }


        }

        return Service.START_STICKY;
    }


    private void requestDownload(DownloadInfo downloadInfo) {

        if (!ExternalStorageManager.isExternalStorageAvailable()) {
            Log.e(TAG, "external storage not available");
            return;
        }

        if (downloadInfo == null || downloadInfo.getKey() == null) return;

        Subscription sub = mSubsMap.get(downloadInfo.getBucketKeyName());

        if (sub != null) {
            Log.d(TAG, "already in progress: " + downloadInfo.getBucketKeyName());
            return;
        }

        Log.d(TAG, "downloading: " + downloadInfo.getBucketKeyName());
        sub = task.downloadObservableFile(downloadInfo, new KeyObserver<String>(downloadInfo.getBucketKeyName()) {

            @Override
            public void onNext(String s) {
                installApk(s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "error: " + e.toString());
                clearRequest(getKey());
            }

            @Override
            public void onCompleted() {
                clearRequest(getKey());
            }
        });

        mSubsMap.put(downloadInfo.getBucketKeyName(), sub);

        showNotification();
    }


    private void clearRequest(String key) {

        mSubsMap.remove(key);

        EventBus.getDefault().post(new DownloadCompletedEvent());

        if (mSubsMap.isEmpty()) {
            Log.d(TAG, "stopping service.");
            clearNotification();
            stopSelf();
        }
        else {
            showNotification();
            Log.d(TAG, "request for " + key + " finished, but other requests still running");
        }

    }


    private void showNotification() {

        Log.d(TAG, "setting up download notification");

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle("APK Download (" + mSubsMap.size() + ")")
                .setContentText("Download in progress")
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setProgress(0, 0, true);

        manager.notify(PROGRESS_NOTIFICATION_ID, builder.build());

    }


    private void clearNotification() {

        Log.d(TAG, "clearing download notification");
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.cancel(PROGRESS_NOTIFICATION_ID);
    }



    private void clearNotification(int id) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.cancel(AwsService.NOTIFICATION_ID + id);
    }


    public void installApk(String data) {

        Log.d(TAG, "installing: " + data);

        if (data == null) return;

        try {

            String path = "file:///" + data;

            Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                    .setDataAndType(Uri.parse(path), "application/vnd.android.package-archive");
            promptInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(promptInstall);

        }
        catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
