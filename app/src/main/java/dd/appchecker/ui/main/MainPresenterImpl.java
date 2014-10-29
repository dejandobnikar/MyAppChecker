package dd.appchecker.ui.main;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import dd.appchecker.aws.data.ApkInfo;
import dd.appchecker.aws.database.AwsLocalStorage;
import dd.appchecker.aws.service.AwsService;
import dd.appchecker.aws.service.ServiceHelper;
import dd.appchecker.event.CheckCompletedEvent;
import dd.appchecker.event.DownloadCompletedEvent;
import dd.appchecker.ui.main.tasks.FileCheckTask;
import de.greenrobot.event.EventBus;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Dejan on 23.10.2014.
 */
public class MainPresenterImpl implements MainPresenter {

    private MainView mMainView;
    private AwsLocalStorage mStorage;
    private Subscription mRequest;
    private FileCheckTask mFileCheckTask;

    public MainPresenterImpl(MainView mainView, AwsLocalStorage storage, FileCheckTask fileCheckTask) {
        this.mMainView = mainView;
        this.mStorage = storage;
        this.mFileCheckTask = fileCheckTask;
    }


    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        loadCurrentData();
    }


    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);

        if (mRequest != null) {
            mRequest.unsubscribe();
            mRequest = null;
        }
    }


    @Override
    public void handleClick(ApkInfo info, Context context) {
        if (info.exsistsOnFileSystem()) {
            // launch install prompt
            Intent intent = ServiceHelper.getInstallIntent(info, context);
            context.startService(intent);
        }
        else {
            // launch download task
            Intent intent = ServiceHelper.getDownloadIntent(info, context);
            context.startService(intent);
        }
    }


    @Override
    public void checkManually(Context context) {
        Intent i  = new Intent(context, AwsService.class);
        context.startService(i);
    }

    private void loadCurrentData() {

        if (mRequest == null) {
            mRequest = mFileCheckTask.checkFileSystem(new Observer<List<ApkInfo>>(){

                @Override
                public void onCompleted() {
                    mRequest = null;
                }

                @Override
                public void onError(Throwable e) {
                    mRequest = null;
                }

                @Override
                public void onNext(List<ApkInfo> list) {
                    displayData(list);
                }
            });
        }
    }


    private void displayData(List<ApkInfo> info) {
        mMainView.setData(info);
    }


    public void onEventMainThread(CheckCompletedEvent event) {
        loadCurrentData();
    }


    public void onEventMainThread(DownloadCompletedEvent event) {
        loadCurrentData();
    }

}
