package dd.appchecker.ui.main.tasks;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dd.appchecker.aws.ExternalStorageManager;
import dd.appchecker.aws.data.ApkInfo;
import dd.appchecker.aws.database.AwsLocalStorage;
import dd.appchecker.aws.tasks.observers.EndObserver;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Dejan on 23.10.2014.
 */
public class FileCheckTaskImpl implements FileCheckTask {

    private static final String TAG = "FileCheckTask";


    private PublishSubject<List<ApkInfo>> mFileCheckRequest;
    private AwsLocalStorage mStorage;

    public FileCheckTaskImpl(AwsLocalStorage storage) {
        this.mStorage = storage;
    }


    public Subscription checkFileSystem(Observer<List<ApkInfo>> observer) {


        if (mFileCheckRequest != null) {
            Log.d(TAG, "File request already exists, subscribing to it");
            return mFileCheckRequest.subscribe(observer);
        }

        mFileCheckRequest = PublishSubject.create();

        mFileCheckRequest.subscribe(new EndObserver<List<ApkInfo>>() {
            @Override
            public void onEnd() {
                Log.d(TAG, "file request ended");
                mFileCheckRequest = null;
            }

            @Override
            public void onNext(List<ApkInfo> apkInfos) {
                //nothing
            }
        });


        Subscription subscription = mFileCheckRequest.subscribe(observer);

        Observable<List<ApkInfo>> observable = Observable.create(new Observable.OnSubscribe<List<ApkInfo>>() {

            @Override
            public void call(Subscriber<? super List<ApkInfo>> subscriber) {

                List<ApkInfo> list = new ArrayList<ApkInfo>(mStorage.getData().values());

                for (ApkInfo info : list) {
                    info.setExsistsOnFileSystem(checkFile(info));
                }

                subscriber.onNext(list);

                subscriber.onCompleted();
            }
        });

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mFileCheckRequest);


        return subscription;
    }


    private boolean checkFile(ApkInfo info) {

        Uri uri = ExternalStorageManager.getFileUri(info.getBucketName(), info.getKey());
        File file = new File(uri.getPath());

        return file.exists();
    }

}
