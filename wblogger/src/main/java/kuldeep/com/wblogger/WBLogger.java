package kuldeep.com.wblogger;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Intent;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Kuldeep on 28/12/17.
 */

public class WBLogger {
    private  static WBLogger _instance;
    private static   int port;
    private Application application;
    public  static WBLogger init(Application application,int port){
        if(_instance==null)
        _instance=new WBLogger(port,application);
        return _instance;
    }

    private  WBLogger(int mPort, Application application){
        port=mPort;
        this.application=application;
        startLogServer();
    }

    public static int getRunningPort(){
        return port;
    }


    private void startLogServer(){
        if(isServiceRunning(WBLoggerService.class))stopService();
        Intent intent=new Intent(application,WBLoggerService.class);
        intent.putExtra(Server.PORT,port);
        application.startService(intent);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) application.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void stopService(){
        Intent intent=new Intent(application,WBLoggerService.class);
        application.stopService(intent);

    }
}
