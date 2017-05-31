package hba.testing.notificatingspcedlearning.Activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import hba.testing.notificatingspcedlearning.DatabaseHelper.Query;
import hba.testing.notificatingspcedlearning.DatabaseHelper.dtbConst;
import hba.testing.notificatingspcedlearning.R;
import hba.testing.notificatingspcedlearning.UImanager.CategorySpinnerAdapter;
import hba.testing.notificatingspcedlearning.UImanager.OnlineSearchResultListAdapter;
import hba.testing.notificatingspcedlearning.UImanager.ViewClickListener;

/**
 * Created by TranKim on 12/16/2016.
 */
public class AddItem extends AppCompatActivity{
    public final static String INTENT_START_ADD_ITEM_KEY="NEW_ITEM";                                         //the key string of the intent that start this activity
    public final static String INTENT_START_ADD_ONLINE_ITEM_KEY="NEW_ONLINE_ITEM";                                         //the key string of the intent that start this activity
    private LinearLayout root;
    private EditText itemName,itemContent,itemNewCategory,itemHint;
    private Toolbar toolbar;
    private ImageButton saveBt,saveMoreBt,cancelBt;
    private Spinner itemCategory;
    private CheckBox chkbNewCat;
    private Query qr;
    private String itemID;
    private ContentValues itemData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additem);
        //Retrieving UI item
        saveBt=(ImageButton)findViewById(R.id.add_item_save);
        saveMoreBt=(ImageButton)findViewById(R.id.add_item_save_more);
        cancelBt=(ImageButton)findViewById(R.id.add_item_cancel);
        itemName=(EditText)findViewById(R.id.add_item_name);
        itemContent=(EditText)findViewById(R.id.add_item_content);
        itemHint=(EditText)findViewById(R.id.add_item_hint);
        itemNewCategory=(EditText)findViewById(R.id.add_item_new_category);
        toolbar=(Toolbar)findViewById(R.id.add_item_toolbar);
        itemCategory=(Spinner)findViewById(R.id.add_item_category);
        chkbNewCat =(CheckBox)findViewById(R.id.add_item_new_category_check_box);
        qr=new Query(this);
        //Configuring toolbar
        setSupportActionBar(toolbar);
        //Configuring the buttons
        saveBt.setTag(this);
        saveMoreBt.setTag(this);
        cancelBt.setTag(this);
        saveBt.setOnClickListener(new ViewClickListener.AddItemSaveButton());
        cancelBt.setOnClickListener(new ViewClickListener.AddItemCancelButton());
        saveMoreBt.setOnClickListener(new ViewClickListener.AddItemSaveButton());
        //Set up Item category picker and new item
        itemCategory.setAdapter(new CategorySpinnerAdapter(this));
        //set up checkbox
        chkbNewCat.setOnClickListener(new ViewClickListener.AddItemCheckBoxNewCategory());
        //Check if there is any category having been created yet
        if(qr.getAllCategory().getCount()==0) {
            chkbNewCat.setChecked(true);
            chkbNewCat.callOnClick();
        }
        if(getIntent().getAction().compareTo(INTENT_START_ADD_ONLINE_ITEM_KEY)==0){
            Bundle entry=getIntent().getExtras();
            itemName.setText(entry.getString(dtbConst.ITEM_TABLE_COLS[1]));
            itemContent.setText(entry.getString(dtbConst.ITEM_TABLE_COLS[2]));
            itemHint.setText(entry.getString(dtbConst.ITEM_TABLE_COLS[8]));
        }
    }
}
