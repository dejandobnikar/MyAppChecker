package dd.appchecker.aws.service;

import android.app.IntentService;

import dagger.ObjectGraph;
import dd.appchecker.App;

/**
 * Created by Dejan on 21.10.2014.
 */
public abstract class BaseIntentService extends IntentService {

    private ObjectGraph mObjectGraph;

    public BaseIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectGraph = ((App) getApplicationContext()).getObjectGraph();
        mObjectGraph.inject(this);

    }

    @Override
    public void onDestroy() {
        mObjectGraph = null;

        super.onDestroy();
    }
}
