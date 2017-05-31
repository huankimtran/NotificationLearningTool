package hba.testing.notificatingspcedlearning.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.Calendar;

import hba.testing.notificatingspcedlearning.DatabaseHelper.Query;
import hba.testing.notificatingspcedlearning.DatabaseHelper.dtbConst;
import hba.testing.notificatingspcedlearning.Debug.DatabaseBackup;
import hba.testing.notificatingspcedlearning.R;

import hba.testing.notificatingspcedlearning.Service.MaintainanceService;
import hba.testing.notificatingspcedlearning.UImanager.MainSearchExpandableListviewAdapter;
import hba.testing.notificatingspcedlearning.UImanager.MainSearchListViewAdapter;
import hba.testing.notificatingspcedlearning.UImanager.ViewClickListener;
import hba.testing.notificatingspcedlearning.UImanager.ViewKeyboardListener;

public class MainSearch extends AppCompatActivity {
    private EditText searchBox;
    private static ExpandableListView list;
    private static ListView searchList;
    private ImageButton addBt, listCatBt;
    private TextView totalItem,dailyItem;
    private Toolbar myToolbar;
    private static MainSearchExpandableListviewAdapter theListAdapter;
    private static MainSearchListViewAdapter lstvAdapter;
    private Query query;
    public  SharedPreferences itemCounter;
    public static final String[] dailyItemCounter={"TODAY","TODAY_WORD"};
    private final String[] RESTART={"QUERY","COUNTER"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Open database connection
        query=getQuery();
        //Loading things
        setContentView(R.layout.mainsearch);
        //Configure the Toolbar
        myToolbar=(Toolbar)findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        //Setting the buttons listener
        addBt=(ImageButton)findViewById(R.id.add_item);
        addBt.setTag(this);
        addBt.setOnClickListener(new ViewClickListener.MainSearchAddButton());
        listCatBt =(ImageButton)findViewById(R.id.category_list);
        listCatBt.setTag(this);
        listCatBt.setOnClickListener(new ViewClickListener.MainSearchCategoryListButton());
        //Search list
        searchList=(ListView)findViewById(R.id.main_search_search_list);
        lstvAdapter=new MainSearchListViewAdapter(this,query);
        searchList.setAdapter(lstvAdapter);
        //setting search box listener
        searchBox=(EditText)findViewById(R.id.search_box);
        searchBox.addTextChangedListener(new ViewKeyboardListener.MainSearchSearchBoxListener(lstvAdapter));
        //setting list view
        list=(ExpandableListView)findViewById(R.id.main_search_the_list);
        list.bringToFront();
        theListAdapter=new MainSearchExpandableListviewAdapter(query.getAllCategory(),this);
        list.setAdapter(theListAdapter);
        expandAllCategory(list);
        list.setOnChildClickListener(new ViewClickListener.ExpandableListViewItemListener(getApplicationContext()));
        //Setting up the summary footer
        dailyItem=(TextView)findViewById(R.id.today_item_added);
        totalItem=(TextView)findViewById(R.id.total_item_number);
        //Setting up item counter
        itemCounter=getSharedPreferences(getPackageName(), MODE_PRIVATE);
        //check if this is new date
        Calendar today=Calendar.getInstance();
//        itemCounter.edit().putInt(dailyItemCounter[0], today.get(Calendar.DAY_OF_MONTH)).putInt(dailyItemCounter[1], 0).commit();
//        Toast.makeText(this,Integer.toString(itemCounter.getInt(dailyItemCounter[1],0)),Toast.LENGTH_SHORT).show();
        //Checking routine maintainance service if it is running
        if(!MaintainanceService.isRunning(this)){
            //The first time run the app or has experienced a reboot
            DatabaseBackup.requestWritePermission(this);
            MaintainanceService.showNewDayNotification(this);
            MaintainanceService.SetupMaintainanceServiceNextRun(getApplicationContext());
        }else
            refreshAndupdateItemCounter(query);
        if  (itemCounter.getInt(dailyItemCounter[0],0)!=today.get(Calendar.DAY_OF_MONTH))
            itemCounter.edit().putInt(dailyItemCounter[0], today.get(Calendar.DAY_OF_MONTH)).putInt(dailyItemCounter[1], 0).apply();
//      debuging purpose
    }
    @Override
    protected void onStart() {
        super.onStart();
        query=getQuery();
        itemCounter=getSharedPreferences(getPackageName(), MODE_PRIVATE);
        //Update the Expandable list
        theListAdapter.refresh();
        lstvAdapter.refresh(searchBox.getText().toString());
        //Update counter display when there are changes
        dailyItem.setText(Integer.toString(itemCounter.getInt(dailyItemCounter[1], 0)));
        totalItem.setText(Integer.toString(query.getAllItem().getCount()));
    }
    public Query getQuery(){
        if(query==null)
            return new Query(this);
        return query;
    }
    public static void riseItemCounter(Context ct){
        SharedPreferences counter=ct.getSharedPreferences(ct.getPackageName(),MODE_PRIVATE);
        int thisMoment= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if (thisMoment!=counter.getInt(dailyItemCounter[0],0))
            counter.edit().putInt(dailyItemCounter[0],thisMoment).putInt(dailyItemCounter[1], 1).apply();
        else
            counter.edit().putInt(dailyItemCounter[1], counter.getInt(dailyItemCounter[1], 0) + 1).apply();
    }
    public static void downItemCounter(Query db,String id){
        Context ct=db.getContext();
        SharedPreferences counter=ct.getSharedPreferences(ct.getPackageName(),MODE_PRIVATE);
        int thisMoment= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        Cursor item=db.getItembyId(id);
        Calendar itemDate=Calendar.getInstance();
        itemDate.setTimeInMillis(item.getLong(7));
        if (thisMoment!=counter.getInt(dailyItemCounter[0],0))
            counter.edit().putInt(dailyItemCounter[0], thisMoment).putInt(dailyItemCounter[1],0).apply();
        else
            if(itemDate.get(Calendar.DAY_OF_MONTH)==counter.getInt(dailyItemCounter[0],0))
                counter.edit().putInt(dailyItemCounter[1],counter.getInt(dailyItemCounter[1],0)-1).apply();
    }
    public static void refreshAndupdateItemCounter(Query db){
        Cursor catList=db.getAllCategory();
        Context ct=db.getContext();
        SharedPreferences counter=ct.getSharedPreferences(ct.getPackageName(), MODE_PRIVATE);
        if(catList.getCount()==0)
            return;
        //refresh the category data
        do{
            String catName=catList.getString(1);
            Cursor catItem= db.getItemByCategory(catName);
            db.updateCategoryItemCounter(catName,-catList.getInt(2)+catItem.getCount());
        }while (catList.moveToNext());
        //refresh daily item counter
        int thisMoment= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if (thisMoment!=counter.getInt(dailyItemCounter[0],0))
            counter.edit().putInt(dailyItemCounter[0], thisMoment).putInt(dailyItemCounter[1],0).apply();
    }
    public void expandAllCategory(ExpandableListView lst){
        for(int i=0;i<lst.getExpandableListAdapter().getGroupCount();i++)
            lst.expandGroup(i);
    }
    public static void disableSearchListView(boolean disable){
        if(disable){
            searchList.setAlpha(0);
            searchList.setFocusable(false);
            searchList.setFocusableInTouchMode(false);
            list.setAlpha(1);
            list.setFocusable(true);
            list.setFocusableInTouchMode(true);
            list.bringToFront();
            theListAdapter.refresh();
        }else {
            searchList.setAlpha(1);
            searchList.setFocusable(true);
            searchList.setFocusableInTouchMode(true);
            list.setAlpha(0);
            list.setFocusable(false);
            list.setFocusableInTouchMode(false);
            searchList.bringToFront();
        }
    }
}
/*
Error log:
03/12/2017
Error: Lost context
the itemCounter was defined as static field and have value
itemCounter=getSharedPreferences(getPackageName(), MODE_PRIVATE) in onCreate
However, the riseItemCounter, downItemCounter can be called from addItem activity
When addItem activity is up and user change to Tflat dictionary, the MainSearch activity
potentially got destroyed and so does its context. Therefore, itemCounter value as that time is null
. This caused the NullObjectReference when the addItem call the method insertItem in of Query object,
which invoke riseItemCounter.
Solve:
Making itemCounter non static.
Adding parameter context to riseItemCounter and downItemCounter
call getSharedPreferences separately in those methods

 */