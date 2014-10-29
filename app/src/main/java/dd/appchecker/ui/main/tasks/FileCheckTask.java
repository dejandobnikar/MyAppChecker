package dd.appchecker.ui.main.tasks;

import java.util.List;

import dd.appchecker.aws.data.ApkInfo;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Dejan on 23.10.2014.
 */
public interface FileCheckTask {

    Subscription checkFileSystem(Observer<List<ApkInfo>> observer);
}
