package dd.appchecker.ui.main;

import java.util.List;

import dd.appchecker.aws.data.ApkInfo;

/**
 * Created by Dejan on 23.10.2014.
 */
public interface MainView {

    void setData(List<ApkInfo> list);

}
