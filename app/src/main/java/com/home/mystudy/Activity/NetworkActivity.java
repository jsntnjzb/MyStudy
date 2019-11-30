package com.home.mystudy.Activity;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.home.mystudy.BroadcastReceiver.NetworkReceiver;

public class NetworkActivity extends Activity {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";

    //判断是否是Wifi连接
    public static final boolean wifiConnected = false;
    //判断是否是手机网络连结
    public static final boolean mobileConnected = false;
    private NetworkReceiver receiver = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册广播
        if(receiver==null){
            receiver = new NetworkReceiver();
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            this.registerReceiver(receiver, filter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播
        if (receiver != null) {
            this.unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
