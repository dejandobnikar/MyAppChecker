package dd.appchecker.ui.main;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dd.appchecker.aws.database.AwsLocalStorage;
import dd.appchecker.ui.main.tasks.FileCheckTask;
import dd.appchecker.ui.main.tasks.FileCheckTaskImpl;

/**
 * Created by Dejan on 23.10.2014.
 */


@Module(
        injects = MainActivity.class,
        complete = false
)
public class MainModule {

    private MainView mView;

    public MainModule(MainView view) {
        this.mView = view;
    }


    @Provides @Singleton
    MainPresenter provideMainPresenter(AwsLocalStorage storage, FileCheckTask fileCheckTask) {
        return new MainPresenterImpl(mView, storage, fileCheckTask);
    }



    @Provides @Singleton
    FileCheckTask provideFileCheckTask(AwsLocalStorage storage) {
        return new FileCheckTaskImpl(storage);
    }
}
