package kuldeep.com.loggerdemo;

import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String ip=getLocalIpAddress();
        if(ip!=null){
            TextView textView=findViewById(R.id.msg_text);
            textView.setText("Use http://"+ip+"/logs/ URL in browser to see the logs. \n Please make sure the devices are  connected in same network");
        }

    }
    public String getLocalIpAddress(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if(wifiManager!=null)
        return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        else return null;
    }

}
