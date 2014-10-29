package dd.appchecker.conectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dagger.ObjectGraph;
import dd.appchecker.App;

/**
 * Created by Dejan on 20.10.2014.
 */
public abstract class BaseReceiver extends BroadcastReceiver {

    protected ObjectGraph mObjectGraph;

    @Override
    public void onReceive(Context context, Intent intent) {
        mObjectGraph = ((App) context.getApplicationContext()).getObjectGraph();
        mObjectGraph.inject(this);
    }
}
