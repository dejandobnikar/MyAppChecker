package dd.appchecker.aws.tasks.observers;

import rx.Observer;

/**
 * Created by Dejan on 22.10.2014.
 */
public abstract class KeyObserver<T> implements Observer<T> {

    private String mKey;

    public KeyObserver(String key) {
        mKey = key;
    }

    public String getKey() {
        return mKey;
    }

}
