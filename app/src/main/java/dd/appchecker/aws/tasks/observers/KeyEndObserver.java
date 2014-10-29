package dd.appchecker.aws.tasks.observers;

import dd.appchecker.aws.tasks.observers.EndObserver;

/**
 * Created by Dejan on 22.10.2014.
 */
public abstract class KeyEndObserver<T> extends EndObserver<T> {

    private String mKey;

    public KeyEndObserver(String key) {
        mKey = key;
    }

    public String getKey() {
        return mKey;
    }


}
