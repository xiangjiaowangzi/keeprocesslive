package com.example.keepprocesslive.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.keepprocesslive.IMyService;
import com.example.keepprocesslive.Utils;

/**
 * Created by LiuB on 2017/10/30.
 */

public class MyService1 extends Service {

    private MyBind bind;
    private MyConnection conn;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.log(" service is  onCreate");
        setForeground();
        bind = new MyBind();
        conn = new MyConnection();
        startService(new Intent(this , RemoteService.class));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.log(" service is  onStartCommand");
        this.bindService(new Intent(MyService1.this, RemoteService.class),
                conn, Context.BIND_IMPORTANT);
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Utils.log(" service is  onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Utils.log(" service is  onBind +++++++++ ");
        return bind;
    }

    class MyBind extends IMyService.Stub {

    }

    class MyConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Utils.log(" service is  onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Utils.log(" service is  onServiceDisconnected");
            //开启本地服务
            MyService1.this.startService(new Intent(MyService1.this, RemoteService.class));
            //绑定本地服务
            MyService1.this.bindService(new Intent(MyService1.this, RemoteService.class),
                    conn, Context.BIND_IMPORTANT);
        }
    }


    /**
     * 通过设置通知栏为前台服务
     * */
    void setForeground(){
        try {
            // 设置为前台服务避免kill，Android4.3及以上需要设置id为0时通知栏才不显示该通知；
            Notification notification = new Notification();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            startForeground(0, notification);
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

}
