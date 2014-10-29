package dd.appchecker.aws.tasks;

import java.util.List;

import dd.appchecker.aws.data.ApkInfo;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Dejan on 21.10.2014.
 */
public interface AWSCheckTask {

    Subscription loadObservableFiles(Observer<List<ApkInfo>> observer);

}
