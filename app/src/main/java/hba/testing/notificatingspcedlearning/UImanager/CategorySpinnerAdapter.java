package hba.testing.notificatingspcedlearning.UImanager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


import hba.testing.notificatingspcedlearning.Activity.AddItem;
import hba.testing.notificatingspcedlearning.DatabaseHelper.Query;
import hba.testing.notificatingspcedlearning.DatabaseHelper.dtbConst;
import hba.testing.notificatingspcedlearning.R;


/**
 * Created by TranKim on 1/17/2017.
 */
public class CategorySpinnerAdapter extends CursorAdapter {
    private Query db;
    private Context ct;
    public CategorySpinnerAdapter(Context context){
        super(context,null,FLAG_REGISTER_CONTENT_OBSERVER);
        ct=context;
        db=new Query(context);
        changeCursor(db.getAllCategory());
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.additemcategoryspinner,null);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView itCatt=(TextView)view;
        ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(dtbConst.CATEGORY_TABLE_COLS[1])));
    }
    public int findItemId(String item){
        Cursor dta=getCursor();
        dta.moveToFirst();
        do{
            if(dta.getString(dta.getColumnIndex(dtbConst.CATEGORY_TABLE_COLS[1])).compareTo(item)==0)
                return dta.getPosition();
        }while (dta.moveToNext());
        return 0;
    }
}
