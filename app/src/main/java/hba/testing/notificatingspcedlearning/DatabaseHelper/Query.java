package hba.testing.notificatingspcedlearning.DatabaseHelper;

import android.app.NotificationManager;
import android.app.job.JobScheduler;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.webkit.WebHistoryItem;

import hba.testing.notificatingspcedlearning.Activity.MainSearch;

/**
 * Created by TranKim on 12/16/2016.'
 *
 */
public class Query extends SQLiteOpenHelper{
    public static int DATABASE_VERSION=9;
    public SQLiteDatabase database;
    private Cursor allItem;
    private Context ct;
    public Query(Context ct){
        super(ct,dtbConst.DTBNAME[0],null,DATABASE_VERSION);
        this.ct=ct;
        database=this.getWritableDatabase();
            //get all the item of the table
        String[] tmp={dtbConst.ITEM_TABLE_COLS[0],dtbConst.ITEM_TABLE_COLS[1],dtbConst.ITEM_TABLE_COLS[2]};
        allItem=database.query(dtbConst.TABLE[0], tmp, null, null, null, null, null, null);
    }
    public Query(Context ct,String dtbName){
        super(ct, dtbName, null,DATABASE_VERSION);
        this.ct=ct;
        database=this.getWritableDatabase();
        //get all the item of the table
        String[] tmp={dtbConst.ITEM_TABLE_COLS[0],dtbConst.ITEM_TABLE_COLS[1],dtbConst.ITEM_TABLE_COLS[2]};
        allItem=database.query(dtbConst.TABLE[0], tmp, null, null, null, null, null, null);
//        database.execSQL(dtbConst.CATEGORYTABLEINITIATING);
//        database.execSQL("DROP TABLE TABLE1");
//        database.execSQL("INSERT INTO TABLE1 (NAME,CONTENT,LEVEL,TIME) VALUES ('huan','kim',2,2);");
    }
    public Cursor retriveAllTable(String tblName){
        return database.query(tblName,null,null,null,null,null,null,null);
    }
    public Cursor likeQuery(String clause){
        String[] tmp1={"%"+clause+"%","%"+clause+"%"};
        String WHERE_CLAUSE="NAME LIKE ? OR CONTENT LIKE ?";
        return database.query(dtbConst.TABLE[0],null,WHERE_CLAUSE,tmp1,null,null,null,null);
    }
    public Cursor getAllItem(){
        Bundle newCr=allItem.getExtras();
        allItem=database.query(dtbConst.TABLE[0],null,null,null,null,null,null,null);
        allItem.setExtras(newCr);
        if(allItem.getCount()>0)
            allItem.moveToFirst();
        return allItem;
    }
    public Cursor getItembyId(String id){
        String WHERE_CLAUSE="_id = ?";
        String[] arg={id};
        Cursor tmp=database.query(dtbConst.TABLE[0], null, WHERE_CLAUSE, arg, null, null, null, null);
        tmp.moveToFirst();
        return tmp;
    }
    public int updateItemTableRow(ContentValues newRow, String itemId){
        String WHERE_CLAUSE="_id=?";
        String[] arg={itemId};
        return database.update(dtbConst.TABLE[0], newRow, WHERE_CLAUSE, arg);
    }
    public int updateCategoryTableRow(ContentValues newRow, String cattId){
        String WHERE_CLAUSE="_id=?";
        String[] arg={cattId};
        return database.update(dtbConst.TABLE[1], newRow, WHERE_CLAUSE, arg);
    }

    public int deleteRow(String itemId){
        String WHERE_CLAUSE="_id=?";
        String[] col={itemId};
        return database.delete(dtbConst.TABLE[0], WHERE_CLAUSE, col);
    }

    /**
     *
     * @param ct        Application context
     * @param itemId    Id of the item need to be removed
     *                  Remove an item of the database and cancel its remind
     *                  And update the item counter
     */
    public void deleteItem(Context ct,String itemId){
        JobScheduler scheduler=(JobScheduler)ct.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        NotificationManager notiMng=(NotificationManager)ct.getSystemService(Context.NOTIFICATION_SERVICE);
        //Cancel all notifications and reminds of this item
        scheduler.cancel(Integer.parseInt(itemId));
        notiMng.cancel(Integer.parseInt(itemId));
        //Decrease daily new item counter and total item counter
        MainSearch.downItemCounter(this, itemId);
        //Remove item data
        deleteRow(itemId);
    }
    public int deleteRows(String[] itemId){
        String WHERE_CLAUSE="_id = ?";
        final String OR_CLAUSE="OR";
        String FINAL_WHERE_CLAUSE="";
        for(int i=0;i<itemId.length;i++){
            FINAL_WHERE_CLAUSE+=WHERE_CLAUSE;
            if(i!=itemId.length-1)
                FINAL_WHERE_CLAUSE+=OR_CLAUSE;
        }
        return database.delete(dtbConst.TABLE[0], FINAL_WHERE_CLAUSE, itemId);
    }
    public long insertRow(ContentValues newRow){
        return database.insert(dtbConst.TABLE[0], null, newRow);
    }

    /**
     *
     *
     * @return the item id
     * This add an item into the database and increase the item counter
     */
    public boolean isNewCategory(String category){
        String[] cols={dtbConst.CATEGORY_TABLE_COLS[1]};
        String WHERE_CLAUSE="NAME=?";
        String[] arg={category};
        Cursor rs=database.query(dtbConst.TABLE[1],cols,WHERE_CLAUSE,arg,null,null,null);
        if(rs.getCount()!=0)
            return false;
        else
            return true;
    }
    public long addCategory(String category){
        ContentValues newCatt=new ContentValues();
        newCatt.put(dtbConst.CATEGORY_TABLE_COLS[1],category);
        newCatt.put(dtbConst.CATEGORY_TABLE_COLS[2],0);
        return database.insert(dtbConst.TABLE[1],null,newCatt);
    }
    public void updateCategoryItemCounter(String category, int hmany){
        String WHERE_CLAUSE="NAME=?";
        String[] arg={category};
        Cursor rs=database.query(dtbConst.TABLE[1],null,WHERE_CLAUSE,arg,null,null,null);
        rs.moveToFirst();
        ContentValues catt=new ContentValues();
        DatabaseUtils.cursorRowToContentValues(rs,catt);
        catt.put(dtbConst.CATEGORY_TABLE_COLS[2],catt.getAsInteger(dtbConst.CATEGORY_TABLE_COLS[2])+hmany);
        database.update(dtbConst.TABLE[1], catt, WHERE_CLAUSE, arg);
    }
    public long insertItem(ContentValues newRow){
        MainSearch.riseItemCounter(ct);
        if(isNewCategory(newRow.getAsString(dtbConst.ITEM_TABLE_COLS[6])))
            addCategory(newRow.getAsString(dtbConst.ITEM_TABLE_COLS[6]));
        else
            updateCategoryItemCounter(newRow.getAsString(dtbConst.ITEM_TABLE_COLS[6]), 1);
        return insertRow(newRow);
    }
    public Cursor getAllCategory(){
        Cursor tmp=database.query(dtbConst.TABLE[1], null, null, null, null, null, null, null);
        tmp.moveToFirst();
        return tmp;
    }
    public Cursor getItemByCategory(String catt){
        String WHERE_CLAUSE="CATEGORY=?";
        String[] arg={catt};
        Cursor tmp=database.query(dtbConst.TABLE[0], null, WHERE_CLAUSE, arg, null, null, null, null);
        tmp.moveToFirst();
        return tmp;
    }
    public Cursor getCategoryById(String Id){
        String WHERE_CLAUSE="_id = ?";
        String[] arg={Id};
        Cursor category=database.query(dtbConst.TABLE[1], null, WHERE_CLAUSE, arg, null, null, null);
        category.moveToFirst();
        return category;
    }
    public void deleteCategory(String Id){
        String WHERE_CLAUSE="_id = ?";
        String WHERE_CLAUSE_ITEM="CATEGORY = ?";
        String[] arg1={Id};
        Cursor category=getCategoryById(Id);
        String[] arg2={category.getString(1)};
        database.delete(dtbConst.TABLE[0],WHERE_CLAUSE_ITEM,arg2);
        database.delete(dtbConst.TABLE[1], WHERE_CLAUSE, arg1);
    }
    public void updateCategoryItemCategoryName(String oldName, String newName){
        Cursor itemList=getItemByCategory(oldName);
        do{
            ContentValues tmp=new ContentValues();
            DatabaseUtils.cursorRowToContentValues(itemList,tmp);
            tmp.put(dtbConst.ITEM_TABLE_COLS[6],newName);
            updateItemTableRow(tmp,tmp.getAsString(dtbConst.ITEM_TABLE_COLS[0]));
        }while (itemList.moveToNext());
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(dtbConst.ITEMTABLEINITIATING);
        db.execSQL(dtbConst.CATEGORYTABLEINITIATING);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public Context getContext(){
        return this.ct;
    }
}
