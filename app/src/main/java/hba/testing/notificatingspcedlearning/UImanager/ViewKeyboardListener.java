package hba.testing.notificatingspcedlearning.UImanager;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import hba.testing.notificatingspcedlearning.Activity.MainSearch;
import hba.testing.notificatingspcedlearning.Thread.anOfflineSearch;
import hba.testing.notificatingspcedlearning.Thread.anOnlineSearch;

/**
 * Created by TranKim on 12/15/2016.
 */
public final class ViewKeyboardListener {
    public static class MainSearchSearchBoxListener implements TextWatcher{
        private MainSearchListViewAdapter adapter;
        public MainSearchSearchBoxListener(MainSearchListViewAdapter adt){
            adapter=adt;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()==0)
                MainSearch.disableSearchListView(true);
            else {
                MainSearch.disableSearchListView(false);
                Cursor likeCursor=adapter.query.likeQuery(s.toString());
                likeCursor.setExtras(adapter.getCursor().getExtras());
                adapter.changeCursor(likeCursor);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    public static class OnlineSearchSearchBoxListener implements TextWatcher{
        public final long WAIT_TIME=500;
        private static anOnlineSearch newOnlineSearch;
        private static anOfflineSearch newOfflineSearch;
        private OnlineSearchResultListAdapter onlineAdt,offlineAdt;
        private Handler handle;
        private TextView status;
        private Context ct;
        public OnlineSearchSearchBoxListener(Context ct,OnlineSearchResultListAdapter Onlineadapter,OnlineSearchResultListAdapter Offlineadapter,Handler handle,TextView status){
            onlineAdt =Onlineadapter;
            offlineAdt=Offlineadapter;
            this.handle=handle;
            this.status=status;
            this.ct=ct;
        }
        public void updateStatus(){
            status.setAlpha(1);
            status.setText("Typing");
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateStatus();
            if(newOnlineSearch != null) {
                newOnlineSearch.terminate();
                newOfflineSearch.terminate();
            }
            newOfflineSearch= new anOfflineSearch(ct, offlineAdt,handle,s.toString(),WAIT_TIME);
            newOfflineSearch.start();
            newOnlineSearch =new anOnlineSearch(s.toString(), onlineAdt,handle,status,WAIT_TIME);
            newOnlineSearch.start();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}

