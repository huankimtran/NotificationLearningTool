package hba.testing.notificatingspcedlearning.UImanager;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import hba.testing.notificatingspcedlearning.NetworkHandler.DataHandler.Entry;
import hba.testing.notificatingspcedlearning.R;

/**
 * Created by TranKim on 2/20/2017.
 */
public class OnlineSearchResultListAdapter extends ArrayAdapter<Entry>{
    private Handler handle;
    private Context ct;
    public OnlineSearchResultListAdapter(Context context, Handler handle) {
        super(context, R.layout.onlinesearchlistitemlayout);
        ct=context;
        this.handle = handle;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout v=(LinearLayout)convertView;
        if(v==null)
            v=(LinearLayout)((LayoutInflater)ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.onlinesearchlistitemlayout,null);
        ((TextView)v.findViewById(R.id.online_search_item_name)).setText(this.getItem(position).getName());
        ((TextView)v.findViewById(R.id.online_search_item_content)).setText(this.getItem(position).getContent());
        v.setTag(getItem(position));
        switch (parent.getId()){
            case R.id.online_search_database_result_list:
                v.setOnClickListener(new ViewClickListener.OnlineSearchOfflineItemOnClick());
                break;
            case R.id.online_search_result_list:
                v.setOnClickListener(new ViewClickListener.OnlineSearchOnlineItemOnClick());
                break;
        }
        return v;
    }
}
