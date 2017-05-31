package hba.testing.notificatingspcedlearning.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import hba.testing.notificatingspcedlearning.DatabaseHelper.Query;
import hba.testing.notificatingspcedlearning.R;
import hba.testing.notificatingspcedlearning.UImanager.CategoryListviewAdapter;
import hba.testing.notificatingspcedlearning.UImanager.ViewClickListener;

/**
 * Created by TranKim on 1/28/2017.
 */
public class CategoryDetail extends AppCompatActivity{
    public final static String INTENT_START_CATEGORY_DETAIL_KEY="CATEGORY_ID";                                         //the key string of the intent that start this activity
    private TextView catName,catNumber,catId;
    private ImageButton saveBt,delBt;
    private Query dtb;
    private Cursor category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorydetail);
        catName=(TextView)findViewById(R.id.category_detail_name);
        catNumber=(TextView)findViewById(R.id.category_detail_number_item);
        catId=(TextView)findViewById(R.id.category_detail_id);
        saveBt=(ImageButton)findViewById(R.id.category_detail_save);
        delBt=(ImageButton)findViewById(R.id.category_detail_delete);
        //Configuring view
        dtb=new Query(this);
        category=dtb.getCategoryById(this.getIntent().getExtras().getString(INTENT_START_CATEGORY_DETAIL_KEY));
        catName.setText(category.getString(1));
        catNumber.setText(Integer.toString(category.getInt(2)));
        catId.setText(Integer.toString(category.getInt(0)));
        catName.setTag(this);
        saveBt.setOnClickListener(new ViewClickListener.CategoryDetailSaveButton());
        delBt.setOnClickListener(new ViewClickListener.CategoryDetailDeleteButton());
        saveBt.setTag(this);
        delBt.setTag(this);
    }

}
