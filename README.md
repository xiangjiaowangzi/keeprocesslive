# keeprocesslive

本项目主要用于目前应用保活的demo

除此之外，端外推送也是一种十分可靠的解决方案；


## 保活手段 ##

**黑色保活：**

不同的app进程，用广播相互唤醒（包括利用系统提供的广播进行唤醒，其实这也是一种拉活的方式）。

**白色保活：**

启动前台Service。

**灰色保活：**

利用系统的漏洞启动前台Service。

## 保活方案 ##

**1.利用Activity提升进程等级**

**2 监听广播，如解锁，屏亮，开机**

	public class WatchmenReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			context.startService(new Intent(context, MyService1.class));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}

**3.onStartCommand方法中使用START_STICKY**
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.log(" service is  onStartCommand");
        this.bindService(new Intent(MyService1.this, RemoteService.class),
                conn, Context.BIND_IMPORTANT);
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

**4.作为前台服务**

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

**5.闹铃守护，双进程守护**

	 @Override
        public void onServiceDisconnected(ComponentName name) {
            Utils.log(" remote service is  onServiceDisconnected");
            //开启本地服务
            RemoteService.this.startService(new Intent(RemoteService.this, MyService1.class));
            //绑定本地服务
            RemoteService.this.bindService(new Intent(RemoteService.this, MyService1.class),
                    conn, Context.BIND_IMPORTANT);
        }


**6.后使用JobScheduler**

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

**7.设置同步账号**
	
	public static void createSyncAccount(Context context) {
        boolean newAccount = false;

        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = GenericAccountService.GetAccount(context, ACCOUNT_TYPE);
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(
                    account, CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY);
            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount) {
            triggerRefresh(context);
        }
    }

