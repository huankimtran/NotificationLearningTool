package hba.testing.notificatingspcedlearning.Thread;

import android.os.Handler;
import android.widget.TextView;

import java.util.ArrayList;

import hba.testing.notificatingspcedlearning.NetworkHandler.DataHandler.DictionaryDataPuller;
import hba.testing.notificatingspcedlearning.NetworkHandler.DataHandler.Entry;
import hba.testing.notificatingspcedlearning.UImanager.OnlineSearchResultListAdapter;

/**
 * Created by TranKim on 2/23/2017.
 */
public class anOnlineSearch extends Thread{
    private final String STATUS_UPDATE="Running the search!";
    private boolean running=true;
    private String keyword;
    private OnlineSearchResultListAdapter adapter;
    private ArrayList<Entry> result;
    private Handler handle;
    private TextView status;
    private long checkTime;
    public anOnlineSearch(String keyword, OnlineSearchResultListAdapter adapter, Handler handle, TextView status, long checkTime){
        this.keyword=keyword;
        this.adapter=adapter;
        this.handle=handle;
        this.status=status;
        this.checkTime=checkTime;
    }
    public void terminate(){
        running =false;
    }
    @Override
    public void run() {
        super.run();
        //waiting to check if user really want to do the search
        try{
            sleep(checkTime);
        }catch(Exception e){

        }
        for (int i=0; running &&i<3;++i)
            switch (i){
                case 0:
                    handle.post(new updateProgress(STATUS_UPDATE,status));
                    break;
                case 1:
                    result=DictionaryDataPuller.OALDUnitility.doASearch(keyword);
                    break;
                case 2:
                    if(result.size()==0)
                        handle.post(new updateProgress("Not found",status));
                    handle.post(new postingResult(adapter,result,status));
                    break;
            }
    }
    public class updateProgress implements Runnable{
        private String message;
        private TextView status;
        public updateProgress(String message,TextView status){
            this.message=message;
            this.status=status;
        }
        @Override
        public void run() {
            status.setAlpha(1);
            status.setText(message);
        }
    }
    public class postingResult implements Runnable{
        private String keyword;
        private OnlineSearchResultListAdapter adapter;
        private ArrayList<Entry> result;
        private TextView status;
        public postingResult(OnlineSearchResultListAdapter adapter,ArrayList<Entry> result,TextView status){
            this.adapter=adapter;
            this.result=result;
            this.status=status;
        }
        @Override
        public void run() {
            adapter.clear();
            if(result.size()!=0) {
                adapter.addAll(result);
                status.setAlpha(0);
            }
                adapter.notifyDataSetChanged();
        }
    }
}
