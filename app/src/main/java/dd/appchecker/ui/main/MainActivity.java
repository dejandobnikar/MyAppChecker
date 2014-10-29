package dd.appchecker.ui.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import javax.inject.Inject;

import dd.appchecker.R;
import dd.appchecker.aws.data.ApkInfo;
import dd.appchecker.ui.BaseActivity;


public class MainActivity extends BaseActivity implements MainView, ListView.OnItemClickListener {

    @Inject
    MainPresenter mPresenter;



    private ListView mListView;
    private MainAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setEmptyView(findViewById(R.id.empty));
        mAdapter = new MainAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_check) {
            mPresenter.checkManually(this);
            return true;
        }

        return false;
    }

    @Override
    public Object[] getModules() {
        return new Object[] {new MainModule(this)};
    }


    @Override
    public void setData(List<ApkInfo> list) {
        mAdapter.setData(list);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ApkInfo info = mAdapter.getItem(i);
        mPresenter.handleClick(info, this);
    }
}
