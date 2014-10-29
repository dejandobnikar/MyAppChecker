package dd.appchecker.aws.tasks;

import dd.appchecker.aws.data.DownloadInfo;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Dejan on 22.10.2014.
 */
public interface AWSDownloadTask {
    Subscription downloadObservableFile(DownloadInfo downloadInfo, Observer<String> observer);

    String downloadFile(DownloadInfo di);
}
