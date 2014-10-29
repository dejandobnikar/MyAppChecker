package dd.appchecker;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by Dejan on 20.10.2014.
 */
public class App extends Application {


    /** Application object graph */
    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectGraph = ObjectGraph.create(getModules().toArray());
       // inject(this);

    }

    public void inject(Object object) {
        mObjectGraph.inject(object);
    }


    /**
     * Creates and return scoped object graph. It uses application object graph and
     * adds modules passed in from parameters.
     *
     * @param modules Modules to add to application object graph.
     * @return Merged object graph
     */
    public ObjectGraph createScopedGraph(Object... modules) {
        return mObjectGraph.plus(modules);
    }


    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }



    protected List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(this));
    }


}
