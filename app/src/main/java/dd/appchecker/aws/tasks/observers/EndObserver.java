package dd.appchecker.aws.tasks.observers;

import rx.Observer;

/**
 * Created by Dejan on 22.10.2014.
 */
public abstract class EndObserver<T> implements Observer<T> {

    @Override
    public void onCompleted() {
        onEnd();
    }

    @Override
    public void onError(Throwable e) {
        onEnd();
    }

    public abstract void onEnd();
}
