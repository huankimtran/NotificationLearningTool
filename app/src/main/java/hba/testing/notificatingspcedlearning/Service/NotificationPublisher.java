package hba.testing.notificatingspcedlearning.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;

import java.util.Date;

import hba.testing.notificatingspcedlearning.Activity.ItemDetail;
import hba.testing.notificatingspcedlearning.Activity.MainSearch;
import hba.testing.notificatingspcedlearning.DataConverting.DataConvertingHelper;
import hba.testing.notificatingspcedlearning.DatabaseHelper.dtbConst;
import hba.testing.notificatingspcedlearning.R;

/**
 * Created by TranKim on 1/25/2017.
 */
public class NotificationPublisher {
    public static final String REMINDER_NOTIFICATION_GROUP = "NOTIFICATION_SPACED_LEARNING_REMINDER";
    private Context ct;
    private NotificationManager notiManager;
    public NotificationPublisher(Context context){
        notiManager = getNotificationService(context);
        ct=context;
    }
    public static NotificationManager getNotificationService(Context context){
        NotificationManager notiMng;
        notiMng = ((NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
        return notiMng;
    }
    public NotificationCompat.Builder buildNotiForItem(Bundle itemData){
        NotificationCompat.Builder notiBuilder;
        String notiContent;
        if (notiManager.getActiveNotifications().length == 0) {
            //If there has not been any notification from this app in the status bar
            //Create the group notification
            //Add action open the mainsearch when user click the main group notification
            Intent clickMain = new Intent(ct.getApplicationContext(), MainSearch.class);
            PendingIntent openMainSearch = PendingIntent.getActivity(ct.getApplicationContext(), 0, clickMain, PendingIntent.FLAG_CANCEL_CURRENT);
            notiBuilder = new NotificationCompat.Builder(ct.getApplicationContext());
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
        //Build the notification
        if(itemData.getString(dtbConst.ITEM_TABLE_COLS[2])!=null)
            if(!itemData.getString(dtbConst.ITEM_TABLE_COLS[2]).isEmpty())
                notiContent=itemData.getString(dtbConst.ITEM_TABLE_COLS[8]);
            else
                notiContent="Click to add information";
        else
            notiContent="Click to add information";
        Intent newInt=new Intent(ct.getApplicationContext(), ItemDetail.class);
        newInt.putExtra(ItemDetail.INTENT_START_ITEM_DETAIL_KEY,itemData.getInt(dtbConst.ITEM_TABLE_COLS[0]));
        PendingIntent openItem = PendingIntent.getActivity(ct.getApplicationContext(), (int)(new Date()).getTime() , newInt,0);
        notiBuilder = new NotificationCompat.Builder(ct.getApplicationContext());
        notiBuilder
                .setGroupSummary(false)
                .setGroup(REMINDER_NOTIFICATION_GROUP)
                .setOngoing(true)
                .setContentIntent(openItem)
                .setContentTitle(itemData.getString(dtbConst.ITEM_TABLE_COLS[1]))
                .setSmallIcon(R.drawable.ic_noti_icon)
//                .setContentText(Long.toString(Strategy.doubleSpacedTimeLearning(Integer.parseInt(itemData.getString(dtbConst.ITEM_TABLE_COLS[3])))))
                .setContentText(notiContent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notiContent))
                /*Make the notifications appear randomly to prevent user from learning the similarity in appearances of items
                (it means when user see items appearing next to each other too often,
                they automatically recall the items meaning when ever they see the sequences, and this is not good.
                 */
                .setSortKey(Double.toString(Math.random()))
                        //Configure the buttons for the notification
                .addAction(addShowAnsAction(itemData))
                .addAction(addTrueAnsAction(itemData))
                .addAction(addWrongAnsAction(itemData));
        return notiBuilder;
    }
    public void showNotiForItem(Bundle itemData){
        notiManager.notify(itemData.getInt(dtbConst.ITEM_TABLE_COLS[0]), buildNotiForItem(itemData).build());
    }
    NotificationCompat.Action addTrueAnsAction(Bundle itemData){
        NotificationCompat.Action.Builder nextLevel;
        Intent nxtLvlIntent;
        nxtLvlIntent = new Intent(ct.getApplicationContext(), ItemManagerService.class);
        nxtLvlIntent.putExtras(itemData);
        nxtLvlIntent.setAction(ItemManagerService.ITEM_MANAGER_SERVICE_NEXT_LEVEL);
        nextLevel = new NotificationCompat.Action.Builder(R.drawable.ic_done_24dp
                , "I got this!"
                , PendingIntent.getService(ct.getApplicationContext()
                , itemData.getInt(dtbConst.ITEM_TABLE_COLS[0])
                , nxtLvlIntent
                , PendingIntent.FLAG_CANCEL_CURRENT));
        return  nextLevel.build();
    }
    NotificationCompat.Action addShowAnsAction(Bundle itemData){
        //Nextlevel button will increase the item level by 1
        NotificationCompat.Action.Builder showAns;
        Intent showIntent;
        //ShowAns will show the content of the item in the notification show that user can check their answer
        showIntent= new Intent(ct.getApplicationContext(), ItemManagerService.class);
        showIntent.putExtras(itemData);
        showIntent.setAction(ItemManagerService.ITEM_MANAGER_SERVICE_SHOW_ANS);
        showAns = new NotificationCompat.Action.Builder(R.drawable.ic_done_24dp
                , "Show answer"
                , PendingIntent.getService(ct.getApplicationContext()
                , itemData.getInt(dtbConst.ITEM_TABLE_COLS[0])
                , showIntent
                , PendingIntent.FLAG_CANCEL_CURRENT));
        return showAns.build();
    }
    NotificationCompat.Action addWrongAnsAction(Bundle itemData){
        NotificationCompat.Action.Builder Back;
        Intent bckIntent;
        //Back button will decrease the item level by 1 unless the item's level is zero
        bckIntent = new Intent(ct.getApplicationContext(), ItemManagerService.class);
        bckIntent.putExtras(itemData);
        bckIntent.setAction(ItemManagerService.ITEM_MANAGER_SERVICE_BACK_TO_BASE);
        Back = new NotificationCompat.Action.Builder(R.drawable.ic_reply_24dp
                , "Wrong!"
                , PendingIntent.getService(ct.getApplicationContext()
                , itemData.getInt(dtbConst.ITEM_TABLE_COLS[0])
                , bckIntent
                , PendingIntent.FLAG_CANCEL_CURRENT));
        return Back.build();
    }
    Notification findNotiById(int Id){
        StatusBarNotification[] notiList= notiManager.getActiveNotifications();
        for(int i=0;i<notiList.length;i++)
            if(notiList[i].getId()==Id)
                return notiList[i].getNotification();
        return null;
    }
    void showAns(Bundle item){
        NotificationCompat.Builder noti = buildNotiForItem(item);
        String asw= item.getString(dtbConst.ITEM_TABLE_COLS[2]) + (item.getString(dtbConst.ITEM_TABLE_COLS[8])!=null?"\n" + item.getString(dtbConst.ITEM_TABLE_COLS[8]):"");
        //Modify noti's content to show answer
        noti.setStyle(new NotificationCompat.BigTextStyle().bigText(asw));
        noti.setSortKey(findNotiById(item.getInt(dtbConst.ITEM_TABLE_COLS[0])).getSortKey());
        notiManager.notify(item.getInt(dtbConst.ITEM_TABLE_COLS[0]), noti.build());
    }
}
