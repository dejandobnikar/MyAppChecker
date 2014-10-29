package dd.appchecker.ui.main;

import android.content.Context;
import android.content.ContextWrapper;
import android.test.ActivityUnitTestCase;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dd.appchecker.aws.AWSModule;
import dd.appchecker.aws.data.ApkInfo;
import dd.appchecker.aws.database.AwsLocalStorage;
import dd.appchecker.mock.MockApplicationCompact;

import static org.mockito.Mockito.mock;

/**
 * Created by Dejan on 23.10.2014.
 */
public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {

    @Inject
    MainPresenter mPresenter;

    @Inject
    AwsLocalStorage mStorage;

    Context mContext;
    MockApplicationCompact mApp;

    public MainActivityTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

        mContext = new ContextWrapper(getInstrumentation().getTargetContext()) {
            @Override
            public Context getApplicationContext() {
                return mApp;
            }
        };

        final List<Object> modules = Arrays.<Object>asList(new TestModule(mContext));

        mApp = new MockApplicationCompact(mContext) {
            @Override
            protected List<Object> getModules() {
                return modules;
            }
        };
        mApp.onCreate();

        setApplication(mApp);

        mApp.inject(this);
    }


    public void testFields() {
        assertTrue(mPresenter != null);
        assertTrue(mStorage != null);
    }


    public void testCompareFunctions() {

        ApkInfo i1 = new ApkInfo();
        i1.setVersion("1");

        ApkInfo i2 = new ApkInfo();
        i2.setVersion("1.0");

        assertFalse(mStorage.hasNewerVersionNumber(i1,i2));
        assertFalse(mStorage.hasNewerVersionNumber(i2,i1));

        i1.setVersion("1.1");
        i2.setVersion("1.10");

        assertTrue(mStorage.hasNewerVersionNumber(i1, i2));
        assertFalse(mStorage.hasNewerVersionNumber(i2,i1));

        i1.setVersion("1.1");
        i2.setVersion("1.11");

        assertTrue(mStorage.hasNewerVersionNumber(i1, i2));
        //assertTrue(mStorage.hasNewerVersionNumber(i2,i1));

        i1.setVersion("1.1");
        i2.setVersion("1.1.0");

        assertFalse(mStorage.hasNewerVersionNumber(i1, i2));

        i1.setVersion("1.1");
        i2.setVersion("1.1.0.0.1");

        assertTrue(mStorage.hasNewerVersionNumber(i1, i2));

        i1.setDate("2014-10-3");
        i2.setDate("2014-10-3");

        assertFalse(mStorage.hasNewerDate(i1, i2));

        i1.setDate("2014-10-4");
        i2.setDate("2014-10-3");

        assertFalse(mStorage.hasNewerDate(i1, i2));

        i1.setDate("2014-10-4");
        i2.setDate("2014-10-5");

        assertTrue(mStorage.hasNewerDate(i1, i2));


    }





    @Module(
            injects = {
                    MainActivity.class,
                    MainActivityTest.class
            },
            library = true,
            overrides = true,

            includes = {
                AWSModule.class
            }

    )
    public class TestModule {

        private Context mContext;

        public TestModule(Context context) {
            mContext = context;
        }

        @Provides
        @Singleton
        public MainPresenter providePresenter() {
            return mock(MainPresenter.class);
        }

        @Provides
        @Singleton
        public Context provideContext() {
            return mContext;
        }

    }
}
