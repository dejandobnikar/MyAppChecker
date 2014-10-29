package dd.appchecker.aws;

import android.content.Context;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dd.appchecker.aws.data.helper.AwsObjectParser;
import dd.appchecker.aws.data.helper.AwsObjectParserImpl;
import dd.appchecker.aws.database.AwsLocalStorage;
import dd.appchecker.aws.database.AwsLocalStorageImpl;
import dd.appchecker.aws.database.SharedPrefStorage;
import dd.appchecker.aws.database.StorageAdapter;
import dd.appchecker.aws.service.AWSDownloadService;
import dd.appchecker.aws.service.AwsService;
import dd.appchecker.aws.tasks.AWSBucketProvider;
import dd.appchecker.aws.tasks.AWSBucketProviderImpl;
import dd.appchecker.aws.tasks.AWSCheckTask;
import dd.appchecker.aws.tasks.AWSCheckTaskImpl;
import dd.appchecker.aws.tasks.AWSDownloadTask;
import dd.appchecker.aws.tasks.AWSDownloadTaskImpl;

/**
 * Created by Dejan on 21.10.2014.
 */

@Module(
        injects = {
                AWSDownloadService.class,
                AwsService.class
        },
        complete = false,
        library = true
)
public class AWSModule {


    private static final String ACCESS_KEY = "my key";
    private static final String SECRET_KEY = "my secret key";

    @Provides
    AwsObjectParser provideAwsObjectParser() {
        return new AwsObjectParserImpl();
    }

    @Provides @Singleton
    AWSCheckTask provideAwsCheckTask(AwsObjectParser parser, AwsLocalStorage database, AmazonS3 awsClient, AWSBucketProvider bucketProvider) {
        return new AWSCheckTaskImpl(parser, database, awsClient, bucketProvider);
    }

    @Provides
    AWSDownloadTask prowideDownloadTask(Context context, AmazonS3 client) {
        return new AWSDownloadTaskImpl(context, client);
    }


    @Provides @Singleton
    AwsLocalStorage provideAwsDatabase(Context context, StorageAdapter storageAdapter) {
        return new AwsLocalStorageImpl(context, storageAdapter);
    }

    @Provides @Singleton
    StorageAdapter provideStorageAdapter(Context context) {
        return new SharedPrefStorage(context);
    }


    @Provides @Singleton
    AWSCredentials provideAwsCredentials() {
        return new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
    }

    @Provides
    AmazonS3 provideAmazonS3Client(AWSCredentials credentials) {
        return new AmazonS3Client(credentials);
    }


    @Provides @Singleton
    AWSBucketProvider provideBucketList() {
        return new AWSBucketProviderImpl();
    }

}
