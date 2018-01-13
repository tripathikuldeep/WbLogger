package kuldeep.com.loggerdemo;

import android.app.Application;

import kuldeep.com.wblogger.WBLogger;

/**
 * Created by Kuldeep on 28/12/17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        WBLogger.init(this,9000);
    }
}
