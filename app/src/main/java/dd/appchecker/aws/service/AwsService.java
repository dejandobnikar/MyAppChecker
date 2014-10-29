package dd.appchecker.aws.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import dd.appchecker.R;
import dd.appchecker.aws.data.ApkInfo;
import dd.appchecker.aws.tasks.AWSCheckTask;
import dd.appchecker.event.CheckCompletedEvent;
import de.greenrobot.event.EventBus;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Dejan on 22.10.2014.
 */
public class AwsService extends BaseService {

    private static final String TAG = "AwsService";

    @Inject
    AWSCheckTask task;

    private Subscription mRequest;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");

        if (mRequest == null) {

            showProgressNotification();

            mRequest = task.loadObservableFiles(new Observer<List<ApkInfo>>(){
                @Override
                public void onCompleted() {
                    Log.d(TAG, "AWS task successfully completed");
                    EventBus.getDefault().post(new CheckCompletedEvent());
                    stopAwsService();
                }

                @Override
                public void onNext(List<ApkInfo> list) {

                    setNotifications(list);
                }

                @Override
                public void onError(Throwable t) {
                    Log.d(TAG, "AWS task completed with error " + t.toString());
                    stopAwsService();
                }
            }) ;

        }

        return Service.START_STICKY;
    }

    private void stopAwsService() {
        mRequest = null;
        clearProgressNotification();
        stopSelf();
    }

    private int UNIQUE_VIEW_INT = 30000;
    public static final int NOTIFICATION_ID = 500;
    private void setNotifications(List<ApkInfo> list) {

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        for (ApkInfo info : list) {

            Log.d(TAG, "setting notification, id: " + info.getId() + " " + info.toString()) ;

            Intent intent = ServiceHelper.getDownloadIntent(info, this);

            PendingIntent pendingIntent = PendingIntent.getService(
                    this,
                    UNIQUE_VIEW_INT + info.getId(),
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            builder.setAutoCancel(true)
                    .setContentTitle(getString(R.string.awsservice_title) + info.getApkName())
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_more))
                    .setSmallIcon(android.R.drawable.ic_menu_directions)
                    .setContentText(info.getBranchName() + " | Ver: " + info.getVersion() + " | B: " + info.getBuildNumber() + " | " + info.getDate())
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent);

            manager.notify(NOTIFICATION_ID + info.getId(), builder.build());
        }

    }




    private void showProgressNotification() {

        Log.d(TAG, "setting up check notification");

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle(getString(R.string.checking_updates))
                .setContentText(getString(R.string.download_in_progress))
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setProgress(0, 0, true);

        manager.notify(NOTIFICATION_ID, builder.build());

    }


    private void clearProgressNotification() {

        Log.d(TAG, "clearing check notification");
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.cancel(NOTIFICATION_ID);
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
