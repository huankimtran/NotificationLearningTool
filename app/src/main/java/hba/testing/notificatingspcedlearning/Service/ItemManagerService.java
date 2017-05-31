package hba.testing.notificatingspcedlearning.Service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import java.util.Date;

import hba.testing.notificatingspcedlearning.Activity.ItemDetail;
import hba.testing.notificatingspcedlearning.DataConverting.DataConvertingHelper;
import hba.testing.notificatingspcedlearning.DatabaseHelper.Query;
import hba.testing.notificatingspcedlearning.DatabaseHelper.dtbConst;
import hba.testing.notificatingspcedlearning.LearningStrategy.Strategy;

/**
 * Created by TranKim on 12/24/2016.
 * This is a service running in the background waiting for the signal sent from the notification
 * Basing on the the answer of the user through the notification, this service will update item level
 * and set up the time for the next remind
 */
public class ItemManagerService extends Service{
    public static final String ITEM_MANAGER_SERVICE_NEXT_LEVEL="FORWARD";                           //Intent action yes from notification
    public static final String ITEM_MANAGER_SERVICE_BACK_TO_BASE="BACKWARD";                        //Intent action no from notification
    public static final String ITEM_MANAGER_SERVICE_SHOW_ANS="SHOWANS";                        //Intent action no from notification
    private Query query;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        int nxtLevel;
        Intent newInt=null;
        if(intent==null)
            return START_NOT_STICKY;
        if(intent.getExtras()==null)
            return START_NOT_STICKY;
        if (intent.getAction().compareTo(ITEM_MANAGER_SERVICE_NEXT_LEVEL)==0)
            nxtLevel=1;
        else
            if (intent.getAction().compareTo(ITEM_MANAGER_SERVICE_BACK_TO_BASE)==0)
                // When user cannot remember item, bring it back to the first level
                nxtLevel=0;
            else
                if (intent.getAction().compareTo(ITEM_MANAGER_SERVICE_SHOW_ANS)==0){
                    new NotificationPublisher(this).showAns(intent.getExtras());
                    return START_STICKY;
                }
                else
                    return START_STICKY;
        NotificationManager notimanager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        ContentValues itemData= DataConvertingHelper.ToContentValue.fromBundle(intent.getExtras());
        //clear the notification
        notimanager.cancel(itemData.getAsInteger(dtbConst.ITEM_TABLE_COLS[0]));
        //Determine the next level of the item
        if(nxtLevel!=0)
            nxtLevel+=itemData.getAsInteger(dtbConst.ITEM_TABLE_COLS[3]);
        long currentTime=(new Date()).getTime();
        long nxtNoti=currentTime+Strategy.doubleSpacedTimeLearning(nxtLevel);
        //Collect new item's remind data
        itemData.put(dtbConst.ITEM_TABLE_COLS[3], nxtLevel);
        itemData.put(dtbConst.ITEM_TABLE_COLS[4], currentTime);
        itemData.put(dtbConst.ITEM_TABLE_COLS[5], nxtNoti);
        //update item data
        query.updateItemTableRow(itemData, itemData.getAsString(dtbConst.ITEM_TABLE_COLS[0]));
        if(notimanager.getActiveNotifications().length<=1)
            MaintainanceService.showNewDayNotification(getApplicationContext());
//        //Create an intent in order to open the item detail
//        newInt=new Intent(getApplicationContext(), ItemDetail.class);
//        newInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        newInt.putExtra(ItemDetail.INTENT_START_ITEM_DETAIL_KEY, intent.getExtras().getInt(dtbConst.ITEM_TABLE_COLS[0]));
//        startActivity(newInt);
//        getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        return START_STICKY;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        query=new Query(getApplicationContext());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
