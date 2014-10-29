package dd.appchecker.aws.tasks;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dd.appchecker.aws.ExternalStorageManager;
import dd.appchecker.aws.data.DownloadInfo;
import dd.appchecker.aws.tasks.observers.KeyEndObserver;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Dejan on 22.10.2014.
 */
public class AWSDownloadTaskImpl implements AWSDownloadTask {

    public static final String TAG = "AWSDownloadTaskImpl";


    private Context mContext;
    private AmazonS3 mAmazonS3;

    private Map<String, PublishSubject<String>> mRequestMap;


    @Inject
    public AWSDownloadTaskImpl(Context context, AmazonS3 awsClient) {
        this.mContext = context;
        this.mAmazonS3 = awsClient;
        mRequestMap = new HashMap<String, PublishSubject<String>>();
    }


    @Override
    public Subscription downloadObservableFile(final DownloadInfo downloadInfo, Observer<String> observer) {


        // check if request for key already exists
        PublishSubject<String> request = mRequestMap.get(downloadInfo.getBucketKeyName());

        if (request != null) {
            Log.d(TAG, "Request already exists, subscribing to it");
            return request.subscribe(observer);
        }


        request = PublishSubject.create();

        mRequestMap.put(downloadInfo.getBucketKeyName(), request);

        request.subscribe(new KeyEndObserver<String>(downloadInfo.getBucketKeyName()) {
            @Override
            public void onEnd() {
                Log.d(TAG, "listing request ended");
                mRequestMap.remove(getKey());
            }

            @Override
            public void onNext(String di) {
                //nothing
            }
        });


        Subscription subscription = request.subscribe(observer);

        Observable<String> observable = Observable.create( new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                String savedPath = downloadFile(downloadInfo);

                subscriber.onNext(savedPath);

                subscriber.onCompleted();
            }
        });

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(request);


        return subscription;
    }



    @Override
    public String downloadFile(DownloadInfo downloadInfo) {

        InputStream reader = null;
        OutputStream writer = null;

        try {
            S3Object object = mAmazonS3.getObject(downloadInfo.getBucketName(), downloadInfo.getKey());

            reader = new BufferedInputStream(object.getObjectContent());

            Uri uriDirectory = ExternalStorageManager.getDirectoryUri(downloadInfo.getBucketName(), downloadInfo.getKey());

            Uri uriFile = ExternalStorageManager.getFileUri(downloadInfo.getBucketName(), downloadInfo.getKey());

            Log.d(TAG, "PATH: " + uriDirectory.getPath());
            File fDirectory = new File(uriDirectory.getPath());
            fDirectory.mkdirs();

            File fFile = new File(uriFile.getPath());

            writer = new BufferedOutputStream(new FileOutputStream(fFile));

            int read = -1;

            while ( ( read = reader.read() ) != -1 ) {
                writer.write(read);
            }

            return uriFile.getPath();
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        finally {
            try {
                writer.flush();
                writer.close();
                reader.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }


}
