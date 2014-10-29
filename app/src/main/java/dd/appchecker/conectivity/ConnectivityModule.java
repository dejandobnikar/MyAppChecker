package dd.appchecker.conectivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Dejan on 20.10.2014.
 */

@Module (
        library = true,
        injects = {
                ConnectivityReceiver.class
        }
)
public class ConnectivityModule {


    @Singleton
    @Provides
    public WorkNetworkChecker provideWorkNetworkChecher() {
        return new WorkNetworkCheckerImpl();
    }


}
