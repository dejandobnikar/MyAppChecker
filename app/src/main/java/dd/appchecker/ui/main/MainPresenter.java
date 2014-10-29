package dd.appchecker.ui.main;

import android.content.Context;

import dd.appchecker.aws.data.ApkInfo;

/**
 * Created by Dejan on 23.10.2014.
 */
public interface MainPresenter {


    void onResume();

    void onPause();

    void handleClick(ApkInfo info, Context context);

    void checkManually(Context context);
}
