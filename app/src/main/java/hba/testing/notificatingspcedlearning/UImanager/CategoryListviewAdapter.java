package hba.testing.notificatingspcedlearning.UImanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import hba.testing.notificatingspcedlearning.Activity.CategoryDetail;
import hba.testing.notificatingspcedlearning.DatabaseHelper.Query;
import hba.testing.notificatingspcedlearning.DatabaseHelper.dtbConst;
import hba.testing.notificatingspcedlearning.R;

/**
 * Created by TranKim on 1/28/2017.
 */
public class CategoryListviewAdapter extends CursorAdapter {
    private final String NUMB_ITEM=" Item(s)";
    private Context ct;
    private Query dtb;
    public CategoryListviewAdapter(Context context){
        super(context,null,FLAG_REGISTER_CONTENT_OBSERVER);
        ct=context;
        dtb=new Query(context);
        this.changeCursor(dtb.getAllCategory());
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater infl=LayoutInflater.from(context);
        return infl.inflate(R.layout.itemlayout,null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView catName,catInfo;
        Intent intent;
        catName=(TextView)view.findViewById(R.id.list_view_item_name);
        catInfo=(TextView)view.findViewById(R.id.list_view_item_hint);
        catName.setText(cursor.getString(cursor.getColumnIndex(dtbConst.CATEGORY_TABLE_COLS[1])));
        catInfo.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(dtbConst.CATEGORY_TABLE_COLS[2]))) + NUMB_ITEM);
        view.setTag(ct);
        view.setOnClickListener(new ViewClickListener.CategoryListItemOnClick());
        intent=new Intent(ct,CategoryDetail.class);
        intent.setAction(CategoryDetail.INTENT_START_CATEGORY_DETAIL_KEY);
        intent.putExtra(CategoryDetail.INTENT_START_CATEGORY_DETAIL_KEY,Integer.toString(cursor.getInt(0)));
        catName.setTag(intent);
    }
    public void refresh(){
        this.changeCursor(dtb.getAllCategory());
        notifyDataSetChanged();
    }
}
