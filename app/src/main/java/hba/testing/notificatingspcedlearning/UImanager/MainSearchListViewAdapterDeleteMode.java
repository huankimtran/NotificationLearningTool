package hba.testing.notificatingspcedlearning.UImanager;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import hba.testing.notificatingspcedlearning.DatabaseHelper.Query;
import hba.testing.notificatingspcedlearning.R;

/**
 * Created by TranKim on 12/16/2016.
 */
public class MainSearchListViewAdapterDeleteMode extends CursorAdapter{
    public Query query;

    /**
     *
     * @param ct
     *
     * Constructor initiates and opens the app database
     * Then assign its Curso to the allItem Curso in Query. This is to display all item when the user open the app
     */
    public MainSearchListViewAdapterDeleteMode(Context ct, Query qr){
        super(ct,null, FLAG_REGISTER_CONTENT_OBSERVER);
        this.query=qr;
        this.changeCursor(query.getAllItem());
        //Put an array of boolean that represent the state of each item checkbox. THis will help in determining which item is chose to be deleted
        this.getCursor().setExtras(getItemBundleState(this.getCursor()));
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
        TextView itName=(TextView)view.findViewById(R.id.main_search_delete_item_name);
        TextView itCt=(TextView)view.findViewById(R.id.main_search_delete_item_content);
        CheckBox chkb=(CheckBox)view.findViewById(R.id.main_search_delete_item_status);
        //Set item name
        itName.setText(cursor.getString(1));
        //Set item content
        itCt.setText(cursor.getString(2));
        //set item checkbox
        chkb.setChecked(cursor.getExtras().getBoolean(Integer.toString(cursor.getInt(0))));
        //put extra information about the item state into the checkbox
        chkb.setTag(cursor.getExtras());
        //Checkbox listener
        chkb.setOnClickListener(new ViewClickListener.ListViewCheckboxItemListenerDeleteMode());
        //This view has the item id as the extra information
        view.setTag(Integer.toString(cursor.getInt(0)));
        //Set onclick for this item so that a click on the item itself will toggle the checkbox's state
        view.setOnClickListener(new ViewClickListener.ListViewItemListenerDeleteMode());
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
        return infl.inflate(R.layout.mainsearchideletetemlayout,null);
    }
    public Bundle getItemBundleState(Cursor cs){
        Bundle bd=new Bundle();
        for (int i=0;i<cs.getCount();i++){
            cs.moveToPosition(i);
            bd.putBoolean(Integer.toString(cs.getInt(0)),false);
        }
        return bd;
    }
}
