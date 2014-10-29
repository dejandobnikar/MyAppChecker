package dd.appchecker.aws.service;

import android.app.Service;

import dagger.ObjectGraph;
import dd.appchecker.App;

/**
 * Created by Dejan on 22.10.2014.
 */
public abstract class BaseService extends Service {

    private ObjectGraph mObjectGraph;


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
