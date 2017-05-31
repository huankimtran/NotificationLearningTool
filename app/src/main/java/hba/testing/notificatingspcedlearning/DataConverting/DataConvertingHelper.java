package hba.testing.notificatingspcedlearning.DataConverting;

import android.app.job.JobInfo;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.service.notification.StatusBarNotification;
import android.widget.Switch;

import java.util.List;

import hba.testing.notificatingspcedlearning.DatabaseHelper.dtbConst;

/**
 * Created by TranKim on 12/24/2016.
 * All member will be chagned to String type
 */
public final class DataConvertingHelper {
    public static class ToBundle{
        /**
         *
         * @param itemData
         * @return
         * Numeric type will be cast to int if it is small enough
         */
        public static Bundle fromCursor (Cursor itemData) {
            Bundle bdl=new Bundle();
            String[] keySet=itemData.getColumnNames();
            ContentValues ct= new ContentValues();
            DatabaseUtils.cursorRowToContentValues(itemData, ct);
            for (int i=0;i<keySet.length;i++)
                switch(itemData.getType(itemData.getColumnIndex(keySet[i]))){
                    case SQLiteCursor.FIELD_TYPE_INTEGER:
                        long vl=itemData.getLong(itemData.getColumnIndex(keySet[i]));
                        if(vl<=Integer.MAX_VALUE)
                            bdl.putInt(keySet[i],(int)vl);
                        else
                            bdl.putLong(keySet[i], vl);
                        break;
                    case SQLiteCursor.FIELD_TYPE_STRING:
                        bdl.putString(keySet[i],itemData.getString(itemData.getColumnIndex(keySet[i])));
                        break;
                }
            return bdl;
        }
        public static Bundle fromPersistableBundle (PersistableBundle itemData){
            Bundle bdl=new Bundle();
            String[] keySet=new String[itemData.size()];
            itemData.keySet().toArray(keySet);
            for (int i=0;i<keySet.length;i++)
                bdl.putString(keySet[i],itemData.getString(keySet[i],Integer.toString(itemData.getInt(keySet[i]))));
            return bdl;
        }
        public static Bundle fromStatusBarNorificationArr(StatusBarNotification[] arg){
            Bundle dta=new Bundle();
            for (StatusBarNotification stt:arg){
                dta.putBoolean(Integer.toString(stt.getId()),true);
            }
            return dta;
        }
        public static Bundle fromJobInforArr(List<JobInfo> arg){
            Bundle dta=new Bundle();
            for (JobInfo job:arg) {
                dta.putBoolean(Integer.toString(job.getId()),true);
            }
            return dta;
        }
    }
    public static class ToContentValue{
        public static ContentValues fromBundle(Bundle itemData){
            ContentValues item=new ContentValues();
            String[] keySet=new String[itemData.size()];
            itemData.keySet().toArray(keySet);
            for (int i=0;i<keySet.length;i++)
                item.put(keySet[i], itemData.getString(keySet[i], Integer.toString(itemData.getInt(keySet[i]))));
            return item;
        }
    }
    public static class ToPersitable{
        public static PersistableBundle fromContentValue(ContentValues itemData){
            PersistableBundle data=new PersistableBundle();
            data.putInt(dtbConst.ITEM_TABLE_COLS[0], itemData.getAsInteger(dtbConst.ITEM_TABLE_COLS[0]));
            data.putString(dtbConst.ITEM_TABLE_COLS[1], itemData.getAsString(dtbConst.ITEM_TABLE_COLS[1]));
            data.putString(dtbConst.ITEM_TABLE_COLS[2], itemData.getAsString(dtbConst.ITEM_TABLE_COLS[2]));
            data.putString(dtbConst.ITEM_TABLE_COLS[3], itemData.getAsString(dtbConst.ITEM_TABLE_COLS[3]));
            data.putString(dtbConst.ITEM_TABLE_COLS[4], itemData.getAsString(dtbConst.ITEM_TABLE_COLS[4]));
            data.putString(dtbConst.ITEM_TABLE_COLS[5], itemData.getAsString(dtbConst.ITEM_TABLE_COLS[5]));
            return data;
        }
    }
}
