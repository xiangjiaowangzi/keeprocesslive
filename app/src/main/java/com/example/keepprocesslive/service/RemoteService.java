package com.example.keepprocesslive.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.keepprocesslive.IMyService;
import com.example.keepprocesslive.Utils;

/**
 * Created by LiuB on 2017/10/31.
 */

public class RemoteService extends Service {

    private MyBind bind;
    private MyConnection conn;

    @Override
    public void onCreate() {
        Utils.log(" remote service is  onCreate");
        super.onCreate();
        bind = new MyBind();
        conn = new MyConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.bindService(new Intent(this, MyService1.class), conn, Context.BIND_IMPORTANT);
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Utils.log(" remote service is  onBind +++++++++ ");
        return bind;
    }

    class MyBind extends IMyService.Stub {

    }

    class MyConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Utils.log(" remote service is  onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Utils.log(" remote service is  onServiceDisconnected");
            //开启本地服务
            RemoteService.this.startService(new Intent(RemoteService.this, MyService1.class));
            //绑定本地服务
            RemoteService.this.bindService(new Intent(RemoteService.this, MyService1.class),
                    conn, Context.BIND_IMPORTANT);
        }
    }

}
