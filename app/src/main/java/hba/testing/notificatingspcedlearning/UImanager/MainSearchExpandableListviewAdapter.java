package hba.testing.notificatingspcedlearning.UImanager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import hba.testing.notificatingspcedlearning.DatabaseHelper.Query;
import hba.testing.notificatingspcedlearning.DatabaseHelper.dtbConst;
import hba.testing.notificatingspcedlearning.R;

/**
 * Created by TranKim on 1/17/2017.
 */
public class MainSearchExpandableListviewAdapter extends CursorTreeAdapter{
    private Query db;
    private Context ct;
    public MainSearchExpandableListviewAdapter(Cursor cr,Context context){
        super(cr,context,false);
        ct=context;
        db=new Query(context);
    }
    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        return db.getItemByCategory(groupCursor.getString(groupCursor.getColumnIndex(dtbConst.CATEGORY_TABLE_COLS[1])));
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.mainsearchgrouplayout,null);
    }
    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        LinearLayout parent=(LinearLayout)view;
        TextView groupName=(TextView)parent.findViewById(R.id.main_search_group_layout_group_name);
        groupName.setText(cursor.getString(cursor.getColumnIndex(dtbConst.CATEGORY_TABLE_COLS[1])));
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.itemlayout,null);
    }
    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        LinearLayout parent=(LinearLayout)view;
        TextView itemName=(TextView)parent.findViewById(R.id.list_view_item_name);
        TextView itemHint=(TextView)parent.findViewById(R.id.list_view_item_hint);
        itemName.setText(cursor.getString(cursor.getColumnIndex(dtbConst.ITEM_TABLE_COLS[1])));
        itemHint.setText(cursor.getString(cursor.getColumnIndex(dtbConst.ITEM_TABLE_COLS[8])));
        //Setting the tag of this view for the use in ItemManager
        parent.setTag(cursor.getInt(cursor.getColumnIndex(dtbConst.ITEM_TABLE_COLS[0])));
    }
    public void refresh(){
        this.changeCursor(db.getAllCategory());
        notifyDataSetChanged();
    }
}
