package hba.testing.notificatingspcedlearning.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import hba.testing.notificatingspcedlearning.R;
import hba.testing.notificatingspcedlearning.DatabaseHelper.*;
import hba.testing.notificatingspcedlearning.Service.NotificationPublisher;
import hba.testing.notificatingspcedlearning.UImanager.CategorySpinnerAdapter;
import hba.testing.notificatingspcedlearning.UImanager.ViewClickListener;

/**
 * Created by TranKim on 12/18/2016.
 */
public class ItemDetail extends AppCompatActivity{
    public final static String INTENT_START_ITEM_DETAIL_KEY="ITEM_ID";                                         //the key string of the intent that start this activity
    private EditText itemName,itemContent,itemHint,itemLevel;
    private CalendarView itemNextDate;
    private Toolbar toolbar;
    private TextView itemId;
    private ImageButton saveBt,deleteBt,backBt,upLevel,downLevel;
    private Spinner itemCatt;
    private int itemID;
    private Cursor itemData;
    private Query query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemdetail);
        //Retrieving UI item
        saveBt=(ImageButton)findViewById(R.id.item_detail_save);
        deleteBt=(ImageButton)findViewById(R.id.item_detail_delete);
        backBt=(ImageButton)findViewById(R.id.item_detail_back);
        upLevel=(ImageButton)findViewById(R.id.item_detail_next_level);
        downLevel=(ImageButton)findViewById(R.id.item_detail_back_level);
        itemName=(EditText)findViewById(R.id.item_detail_name);
        itemContent=(EditText)findViewById(R.id.item_detail_content);
        itemHint=(EditText)findViewById(R.id.item_detail_hint);
        itemCatt=(Spinner)findViewById(R.id.item_detail_category);
        itemLevel=(EditText)findViewById(R.id.item_detail_level);
        itemNextDate=(CalendarView)findViewById(R.id.item_detail_next_remind);
        itemId=(TextView)findViewById(R.id.item_detail_id);                                         //this view is invisible, just for holder the item id which will be used in the listener to identify the selected item
        toolbar=(Toolbar)findViewById(R.id.item_detail_toolbar);
        //Configuring toolbar
        setSupportActionBar(toolbar);
        //Retrieving data of selected item from database
        query=new Query(this);
        itemID=getIntent().getIntExtra(INTENT_START_ITEM_DETAIL_KEY, 0);
//        itemData=query.getAllItem();
        itemData=query.getItembyId(Integer.toString(itemID));
        //feed the UI item with information from database
        itemName.setText(itemData.getString(1));
        itemContent.setText(itemData.getString(2));
        itemHint.setText(itemData.getString(8));
        itemCatt.setAdapter(new CategorySpinnerAdapter(this));
        itemCatt.setSelection(((CategorySpinnerAdapter) itemCatt.getAdapter()).findItemId(itemData.getString(6)));
        itemLevel.setText(Integer.toString(itemData.getInt(3)));
        itemNextDate.setDate(itemData.getLong(5));
        itemId.setText(itemData.getString(0));
        //Configure the buttons
        saveBt.setTag(this);                                                                        //This is a trick to get the activity in the dialog button
        saveBt.setOnClickListener(new ViewClickListener.ItemDetailSaveButtonListener());
        deleteBt.setTag(this);
        deleteBt.setOnClickListener(new ViewClickListener.ItemDetailDeleteButtonListener());
        upLevel.setOnClickListener(new ViewClickListener.ItemDetailLevelButtonsListener());
        downLevel.setOnClickListener(new ViewClickListener.ItemDetailLevelButtonsListener());
        backBt.setTag(this);
        backBt.setOnClickListener(new ViewClickListener.ItemDetailBackButtonListener());
        if(itemData.getString(2).isEmpty())
            NotificationPublisher.getNotificationService(this).cancel(itemID);
    }
}
