package hba.testing.notificatingspcedlearning.Service;

import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.database.DatabaseUtils;

import java.util.Calendar;
import java.util.Date;

import hba.testing.notificatingspcedlearning.DataConverting.DataConvertingHelper;
import hba.testing.notificatingspcedlearning.DatabaseHelper.Query;
import hba.testing.notificatingspcedlearning.DatabaseHelper.dtbConst;
import hba.testing.notificatingspcedlearning.Debug.DatabaseBackup;

/**
 * Created by TranKim on 1/9/2017.
 *
 */
public class MaintainanceService extends JobService {
    public static final int MAINTAINACE_SERVICE_ID=Integer.MAX_VALUE;
    public static final long MAINTAINACE_SERVICE_UNCERTAINTY=10*60*1000;                            //Job deadline, the job will be executed no later than 1:10AM
    private Query dtb;
    private NotificationManager notiMng;
    private JobScheduler jobSchdl;

    @Override
    public void onCreate() {
        super.onCreate();
        dtb=new Query(getApplicationContext());
        notiMng=(NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        jobSchdl=(JobScheduler)getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
    }
    @Override
    public boolean onStartJob(JobParameters params) {
        showNewDayNotification(getApplicationContext());
        SetupMaintainanceServiceNextRun(getApplicationContext());
        DatabaseBackup.backupDatabase(getApplicationContext());
        jobFinished(params, false);
        return false;
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
//------------------------------Used Methods-----------------------------------------------------
    public static void showNewDayNotification(Context context){
        String WHERE=dtbConst.ITEM_TABLE_COLS[5]+" <= ?";
        String[] arg={Long.toString(TodayTimeMark())};
        String orderBy=dtbConst.ITEM_TABLE_COLS[5]+" ASC";
        Query qr=new Query(context);
        //Get all item that will be notified today
        Cursor todayItem=qr.database.query(dtbConst.TABLE[0],null,WHERE,arg,null,null,orderBy);
        if(todayItem.getCount()>0){
            todayItem.moveToFirst();
            NotificationPublisher pls=new NotificationPublisher(context.getApplicationContext());
            do{
                pls.showNotiForItem(DataConvertingHelper.ToBundle.fromCursor(todayItem));
            }while (todayItem.moveToNext());
        }
    }
    public static long TodayTimeMark(){
        Calendar today=Calendar.getInstance();
        Long TodayTimeMark=today.getTimeInMillis()- (long)(today.get(Calendar.HOUR_OF_DAY) *3600*1000+today.get(Calendar.MINUTE)*60000+today.get(Calendar.SECOND)*1000);
        return TodayTimeMark;
    }

    /**
     * This method set up the Maintainance service so that it run at 1:00AM everyday
     * @param ct
     */
    public static void SetupMaintainanceServiceNextRun(Context ct){
        JobScheduler jobSetter;
        JobInfo.Builder job;
        ComponentName cpn;
        long upTime= TodayTimeMark()+3600*1000;                                                     //Set running time at 1 AM
        long thisNow=(new Date()).getTime();
        long nxtUp;
        if(thisNow>upTime)
            nxtUp=upTime+24*3600*1000-thisNow;                                                              //Setting next run to be on the next day
        else
            nxtUp=upTime-thisNow;
        jobSetter= (JobScheduler)ct.getSystemService(JOB_SCHEDULER_SERVICE);
        cpn=new ComponentName(ct,MaintainanceService.class);
        job=new JobInfo.Builder(MAINTAINACE_SERVICE_ID,cpn);
        job
                .setPersisted(true)
                .setMinimumLatency(nxtUp)
                .setOverrideDeadline(nxtUp+MAINTAINACE_SERVICE_UNCERTAINTY);
        jobSetter.schedule(job.build());
    }
    public static boolean isRunning(Context ct){
        JobScheduler jobSetter;
        JobInfo.Builder job;
        JobInfo[] jobList;
        jobSetter= (JobScheduler)ct.getSystemService(JOB_SCHEDULER_SERVICE);
        jobList=new JobInfo[jobSetter.getAllPendingJobs().size()];
        jobSetter.getAllPendingJobs().toArray(jobList);
        for (int i=0;i<jobList.length;i++)
            if(jobList[i].getId()==MAINTAINACE_SERVICE_ID)
                return true;
        return false;
    }
//-------------Deprecated method--------------------------------
    /**
     * Deprecated method
     * @param params
     * @return
     */
    public boolean OldonStartJob(JobParameters params) {
        Cursor item;
        Bundle jobs,notis;
        String id;
        Long now;
        ContentValues row;
        now = new Date().getTime();
        jobs= DataConvertingHelper.ToBundle.fromJobInforArr(jobSchdl.getAllPendingJobs());
        notis= DataConvertingHelper.ToBundle.fromStatusBarNorificationArr(notiMng.getActiveNotifications());
        item=dtb.getAllItem();
        for(int i=0;i<item.getCount();i++){
            id=Integer.toString(item.getInt(0));
            //if iteam is not either showed or scheduled a reminder then do it
            if(!(jobs.containsKey(id)||notis.containsKey(id))){
                row=new ContentValues();
                DatabaseUtils.cursorRowToContentValues(item,row);
                //if the next remind time has been passed then create a notification
                if(item.getLong(5)<=now)
                    ReminderService.scheduleReminderNow(getApplicationContext(),row);
                else
                    ReminderService.scheduleReminder(getApplicationContext(),row);
            }
            item.moveToNext();
            jobFinished(params,false);
        }
        return false;
    }
}
