package dd.appchecker.aws.tasks;

import android.util.Log;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import dd.appchecker.aws.data.ApkInfo;
import dd.appchecker.aws.data.helper.AwsObjectParser;
import dd.appchecker.aws.database.AwsLocalStorage;
import dd.appchecker.aws.tasks.observers.EndObserver;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Dejan on 21.10.2014.
 */
public class AWSCheckTaskImpl implements AWSCheckTask {

    public static final String TAG = "AWSTaskImpl";

    private AmazonS3 mAmazonS3;
    private AwsObjectParser objectParser;
    private AwsLocalStorage database;
    private PublishSubject<List<ApkInfo>> mObjectListingRequest;
    private AWSBucketProvider mBucketProvider;

    @Inject
    public AWSCheckTaskImpl( AwsObjectParser objectParser, AwsLocalStorage database, AmazonS3 awsClient, AWSBucketProvider bucketProvider) {
        //this.context = context;
        this.objectParser = objectParser;
        this.database = database;
        this.mAmazonS3 = awsClient;
        this.mBucketProvider = bucketProvider;
    }


    @Override
    public Subscription loadObservableFiles(Observer<List<ApkInfo>> observer) {

        if (mObjectListingRequest != null) {
            Log.d(TAG, "Request already exists, subscribing to it");
            return mObjectListingRequest.subscribe(observer);
        }

        mObjectListingRequest = PublishSubject.create();

        mObjectListingRequest.subscribe(new EndObserver<List<ApkInfo>>() {
            @Override
            public void onEnd() {
                Log.d(TAG, "listing request ended");
                mObjectListingRequest = null;
            }

            @Override
            public void onNext(List<ApkInfo> apkInfos) {
                //nothing
            }
        });


        Subscription subscription = mObjectListingRequest.subscribe(observer);


        Observable<ObjectListing> observable = Observable.create( new Observable.OnSubscribe<ObjectListing>() {
            @Override
            public void call(Subscriber<? super ObjectListing> subscriber) {
                ObjectListing objectListing;

                for (String bucket : mBucketProvider.getBucketList() ) {

                    ListObjectsRequest objectRequest = new ListObjectsRequest().withBucketName(bucket);

                    Log.d(TAG, "connecting to AWS - bucket " + bucket);
                    do {
                        Log.d(TAG, "getting listings...");
                        objectListing = mAmazonS3.listObjects(objectRequest);

                        subscriber.onNext(objectListing);

                        objectRequest.setMarker(objectListing.getNextMarker());
                    } while (objectListing.isTruncated());

                    Log.d(TAG, "done, that's all from AWS - bucket " + bucket);

                }

                subscriber.onCompleted();
            }
        });

        observable
                .flatMap(new Func1<ObjectListing, Observable<List<ApkInfo>>>() {
                    @Override
                    public Observable<List<ApkInfo>> call(ObjectListing objectListing) {
                        // get only objectlistings which represend actual .apk
                        Log.d(TAG, "filtering out .apk listings (incomint listings: " + objectListing.getObjectSummaries().size() + ")");
                        return Observable.just(getApkInfoList(objectListing));
                    }
                })
                .flatMap(new Func1<List<ApkInfo>, Observable<List<ApkInfo>>>() {
                    @Override
                    public Observable<List<ApkInfo>> call(List<ApkInfo> apkInfos) {
                        // pass on only those which are new or update exists
                        Log.d(TAG, "filtering out new or updated apk listings (incoming apk infos: " + apkInfos.size() + ")");
                        return Observable.just(saveApkInfo(apkInfos));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObjectListingRequest);

        return subscription;
    }


    /**
     *
     * @return Method returns those which are new or updated
     */
    private List<ApkInfo> saveApkInfo(List<ApkInfo> incomingApkInfo) {

        List<ApkInfo> list = new LinkedList<ApkInfo>();

        for (ApkInfo apkInfo : incomingApkInfo) {
            if (database.insertOrUpdate(apkInfo) ){
                // this apkInfo is newer (by version or build or date) than previous with same branch name

                if (list.contains(apkInfo)) {
                    // if it contains previous info with same branch name, remove it
                    list.remove(apkInfo);
                }

                list.add(apkInfo);
            }
        }

        return list;
    }


    private List<ApkInfo> getApkInfoList(ObjectListing objectListing) {

        List<ApkInfo> list = new LinkedList<ApkInfo>();

        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            ApkInfo apkInfo = objectParser.parseObject(objectSummary);

            if (apkInfo != null) {
                Log.d(TAG, objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
                Log.d(TAG, "binary found: " + apkInfo.toString() );
                apkInfo.setBucketName(objectListing.getBucketName());
                list.add(apkInfo);
            }
        }

        return list;
    }


}
