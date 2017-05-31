package hba.testing.notificatingspcedlearning.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.Date;

import hba.testing.notificatingspcedlearning.Activity.MainSearch;
import hba.testing.notificatingspcedlearning.DataConverting.DataConvertingHelper;
import hba.testing.notificatingspcedlearning.DatabaseHelper.dtbConst;
import hba.testing.notificatingspcedlearning.LearningStrategy.Strategy;
import hba.testing.notificatingspcedlearning.R;

/**
 * DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED
 * Created by TranKim on 12/24/2016.
 * This service crate the reminding notification which display in appropriate time after the nearest remind
 * DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED DEPRECATED
 */
public class ReminderService extends JobService {
    public static final long JOB_EXTRA_TIME_DEADLINE = 10000;
    /*Do not set the overrideDeadlineto a specific number
    because it will override the delay of each item.
    If the overrideDeadline < minimunLatency, the OverrideDeadline will win so
    just setOverrideDeadline=minimumLatency + JOB_EXTRA_TIME_DEADLINE
    */
    public static final String REMINDER_NOTIFICATION_GROUP = "NOTIFICATION_SPACED_LEARNING_REMINDER";
    @Override
    public boolean onStartJob(JobParameters params) {
        PersistableBundle itemData = params.getExtras();
        NotificationManager notiManager = ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
        NotificationCompat.Builder notiBuilder;
        NotificationCompat.Action.Builder nextLevel, Back;
        Intent nxtLvlIntent, bckIntent;
        if (notiManager.getActiveNotifications().length == 0) {
            //If there has not been any notification from this app in the status bar
            //Create the group notification
            //Add action open the mainsearch when user click the main group notification
            Intent clickMain = new Intent(getApplicationContext(), MainSearch.class);
            PendingIntent openMainSearch = PendingIntent.getActivity(getApplicationContext(), 0, clickMain, PendingIntent.FLAG_CANCEL_CURRENT);
            notiBuilder = new NotificationCompat.Builder(getApplicationContext());
            notiBuilder
                    .setOngoing(true)
                    .setGroupSummary(true)
                    .setGroup(REMINDER_NOTIFICATION_GROUP)
                    .setContentTitle("Nothing for now, have fun!")
                    .setContentIntent(openMainSearch)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.ic_noti_icon);
            notiManager.notify(0, notiBuilder.build());
        }
        //Configure the buttons for the notification
        //Nextlevel button will increase the item level by 1
        nxtLvlIntent = new Intent(getApplicationContext(), ItemManagerService.class);
        nxtLvlIntent.putExtras(DataConvertingHelper.ToBundle.fromPersistableBundle(itemData));
        nxtLvlIntent.setAction(ItemManagerService.ITEM_MANAGER_SERVICE_NEXT_LEVEL);
        nextLevel = new NotificationCompat.Action.Builder(R.drawable.ic_done_24dp
                , "I got this!"
                , PendingIntent.getService(this.getApplicationContext()
                , itemData.getInt(dtbConst.ITEM_TABLE_COLS[0])
                , nxtLvlIntent
                , PendingIntent.FLAG_CANCEL_CURRENT));
        //Back button will decrease the item level by 1 unless the item's level is zero
        bckIntent = new Intent(getApplicationContext(), ItemManagerService.class);
        bckIntent.putExtras(DataConvertingHelper.ToBundle.fromPersistableBundle(itemData));
        bckIntent.setAction(ItemManagerService.ITEM_MANAGER_SERVICE_BACK_TO_BASE);
        Back = new NotificationCompat.Action.Builder(R.drawable.ic_reply_24dp
                , "Huh! What's this?"
                , PendingIntent.getService(getApplicationContext()
                , itemData.getInt(dtbConst.ITEM_TABLE_COLS[0])
                , bckIntent
                , PendingIntent.FLAG_CANCEL_CURRENT));
        //Build the notification
        notiBuilder = new NotificationCompat.Builder(getApplicationContext());
        notiBuilder
                .setGroupSummary(false)
                .setGroup(REMINDER_NOTIFICATION_GROUP)
                .setOngoing(true)
                .setContentTitle(itemData.getString(dtbConst.ITEM_TABLE_COLS[1]))
                .setSmallIcon(R.drawable.ic_noti_icon)
//                .setContentText(Long.toString(Strategy.doubleSpacedTimeLearning(Integer.parseInt(itemData.getString(dtbConst.ITEM_TABLE_COLS[3])))))
                .setContentText(itemData.getString(dtbConst.ITEM_TABLE_COLS[2]))
                .setSortKey(Long.toString((new Date()).getTime()))
                .addAction(Back.build())
                .addAction(nextLevel.build());
        notiManager.notify(itemData.getInt(dtbConst.ITEM_TABLE_COLS[0]), notiBuilder.build());
        jobFinished(params, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    /**
     * the method for set up the remind of an item
     *
     * @param ct       Application context
     * @param itemData Input the item data with the level and time columns updated
     */
    public static void scheduleReminder(Context ct, ContentValues itemData) {
        //Building the intent
        ComponentName cpnName = new ComponentName(ct, ReminderService.class);
        //Send the new item information to the service
        PersistableBundle persitBundle = DataConvertingHelper.ToPersitable.fromContentValue(itemData);
        //Create a Notification with id of the item
        JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(itemData.getAsInteger(dtbConst.ITEM_TABLE_COLS[0]), cpnName);
        Long delayTill=itemData.getAsLong(dtbConst.ITEM_TABLE_COLS[5])-itemData.getAsLong(dtbConst.ITEM_TABLE_COLS[4]);
        jobInfoBuilder
                .setMinimumLatency(delayTill)
                .setOverrideDeadline(delayTill+ReminderService.JOB_EXTRA_TIME_DEADLINE)
                .setPersisted(true)
                .setExtras(persitBundle);
        JobInfo jobinfo = jobInfoBuilder.build();
        JobScheduler scheduler = (JobScheduler) ct.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(jobinfo);
    }

    /**
     * the method for instantly displaying notification of an item
     * @param ct                Application context
     * @param itemData          Input the item data with the level and time columns updated
     *
     */
    public static void scheduleReminderNow(Context ct,ContentValues itemData){
        //Building the intent
        ComponentName cpnName=new ComponentName(ct, ReminderService.class);
        //Send the new item information to the service
        PersistableBundle persitBundle=DataConvertingHelper.ToPersitable.fromContentValue(itemData);
        //Create a Notification with id of the item
        JobInfo.Builder jobInfoBuilder=new JobInfo.Builder(itemData.getAsInteger(dtbConst.ITEM_TABLE_COLS[0]),cpnName);
        jobInfoBuilder
                .setMinimumLatency(0)
                .setOverrideDeadline(0)
                .setPersisted(true)
                .setExtras(persitBundle);
        JobInfo jobinfo=jobInfoBuilder.build();
        JobScheduler scheduler=(JobScheduler)ct.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(jobinfo);
    }

}
