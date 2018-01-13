package kuldeep.com.wblogger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 *
 * Created by Kuldeep on 28/12/17.
 *
 **/

public class WBLoggerService extends Service {
    private  Server  server;
    public static final String TAG=WBLoggerService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null&&intent.hasExtra(Server.PORT))
        server=new Server(intent.getIntExtra(Server.PORT,8000));
        else server=new Server(8000);
        server.start();
        Log.i(TAG, "onStartCommand: Logger Server started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        server.start();
        Log.i(TAG, "onDestroy: Logger service stoped");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
