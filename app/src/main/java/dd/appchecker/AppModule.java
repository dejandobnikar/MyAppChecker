package dd.appchecker;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dd.appchecker.aws.AWSModule;
import dd.appchecker.conectivity.ConnectivityModule;

/**
 * Created by Dejan on 20.10.2014.
 */

@Module(
        injects = App.class,
        library = true,
        includes = {
                ConnectivityModule.class,
                AWSModule.class
        }
)
public class AppModule {

    private App mApp;

    public AppModule(App app) {
        mApp = app;
    }

    @Provides
    public Context provideApplicationContext() {
        return mApp;
    }

}
