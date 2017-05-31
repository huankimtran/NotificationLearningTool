package hba.testing.notificatingspcedlearning.UImanager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import hba.testing.notificatingspcedlearning.DatabaseHelper.Query;
import hba.testing.notificatingspcedlearning.R;

/**
 * Created by TranKim on 12/16/2016.
 */
public class MainSearchListViewAdapter extends CursorAdapter{
    public Query query;

    /**
     *
     * @param ct
     *
     * Constructor initiate and open the app database
     * Then assign the its Curso to the allItem Curso in Query. This is to display all item when the user open the app
     */
    public MainSearchListViewAdapter(Context ct, Query qr){
        super(ct,null, FLAG_REGISTER_CONTENT_OBSERVER);
        this.query=qr;
        this.changeCursor(query.getAllItem());
    }
    public void RefreshAdapter(){
        this.changeCursor(query.getAllItem());
    }
    /**
     *
     * @param view
     * @param context
     * @param cursor
     * Called to bind the right information to the right place
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView itName=(TextView)view.findViewById(R.id.list_view_item_name);
        TextView itHint=(TextView)view.findViewById(R.id.list_view_item_hint);
        itName.setText(cursor.getString(1));
        itHint.setText(cursor.getString(8));
        view.setTag(cursor.getInt(0));
        view.setOnClickListener(new ViewClickListener.ListViewItemListener(context));
    }

    /**
     *
     * @param context
     * @param cursor
     * @param parent
     * @return
     * Called to create a new view when there is not enough view in the listview
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater infl=LayoutInflater.from(context);
        return infl.inflate(R.layout.itemlayout,null);
    }
    public void refresh(String searchText){
        changeCursor(query.likeQuery(searchText));
        notifyDataSetChanged();
    }
}
