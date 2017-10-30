package com.example.keepprocesslive;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.keepprocesslive.account.SyncAccountUtils;
import com.example.keepprocesslive.service.MyService1;

import java.io.PushbackInputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    void startService() {
        Intent intent = new Intent(this, MyService1.class);
        startService(intent);
    }

    void stopService() {
        Intent intent = new Intent(this, MyService1.class);
        stopService(intent);
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                startService();
                break;
            case R.id.btn2:
                stopService();
                break;
            case R.id.btn3:
                startJobScheduler();
                break;
            case R.id.btn4:
                addAccount();
                break;
        }
    }

    private static final long PERIODIC_TIME = 60 * 1000; // 时间60s

    @TargetApi((Build.VERSION_CODES.LOLLIPOP))
    void startJobScheduler() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int job = 1;
                JobInfo.Builder builder = new JobInfo.Builder(job,
                        new ComponentName(this, JobSchedulerService.class));
                builder.setPeriodic(PERIODIC_TIME);
                builder.setPersisted(true);

                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.schedule(builder.build());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 添加同步账号
     * 该方案适用于所有的 Android 版本，包括被 forestop 掉的进程也可以进行拉活。
     * 最新 Android 版本（Android N）中系统好像对账户同步这里做了变动，该方法不再有效。
     */
    public void addAccount() {
        try {
            SyncAccountUtils.createSyncAccount(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
