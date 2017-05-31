package hba.testing.notificatingspcedlearning.Thread;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;

import java.util.ArrayList;

import hba.testing.notificatingspcedlearning.Activity.OnlineSearch;
import hba.testing.notificatingspcedlearning.DatabaseHelper.Query;
import hba.testing.notificatingspcedlearning.NetworkHandler.DataHandler.Entry;
import hba.testing.notificatingspcedlearning.UImanager.OnlineSearchResultListAdapter;

/**
 * Created by TranKim on 3/13/2017.
 */
public class anOfflineSearch extends Thread {
    private Context ct;
    private OnlineSearchResultListAdapter adt;
    private Handler handler;
    private Query query;
    private String search;
    private long waitTime;
    private boolean running=true;
    public anOfflineSearch(Context ct,OnlineSearchResultListAdapter adt, Handler handler,String search,long wait_time){
        this.ct=ct;
        this.adt=adt;
        this.handler=handler;
        this.search=search;
        query=new Query(ct);
        waitTime=wait_time;
    }
    public class postingData implements Runnable{
        OnlineSearchResultListAdapter adt;
        ArrayList<Entry> newDta;
        public postingData(OnlineSearchResultListAdapter adt,ArrayList<Entry> newDta){
            this.adt=adt;
            this.newDta=newDta;
        }
        @Override
        public void run() {
            adt.clear();
            adt.addAll(newDta);
            adt.notifyDataSetChanged();
        }
    }
    public void terminate(){
        running =false;
    }
    private ArrayList<Entry> Curso2Entry(Cursor dta){
        ArrayList<Entry> convert=new ArrayList<>();
        if(dta.getCount()==0)
            return convert;
        dta.moveToFirst();
        do{
            //the _id of the item is embeded into the field source of the new Entry instance
            convert.add(new Entry(Integer.toString(dta.getInt(0)),dta.getString(1),dta.getString(2),dta.getString(8),""));
        }while(dta.moveToNext());
        return convert;
    }
    @Override
    public void run() {
        super.run();
        ArrayList<Entry> newDta=new ArrayList<>();
        try{
            sleep(waitTime);
            if(!running)
                return;
        }catch (Exception e){

        }
        if(search.isEmpty()) {
            handler.post(new postingData(adt, newDta));
            return;
        }
        Cursor newCursor=query.likeQuery(search);
        for (int i=0;running&&i<2;i++)
            switch (i){
                case 0:
                    newDta=Curso2Entry(newCursor);
                    break;
                case 1:
                    handler.post(new postingData(adt,newDta));
                    break;
            }
    }
}
