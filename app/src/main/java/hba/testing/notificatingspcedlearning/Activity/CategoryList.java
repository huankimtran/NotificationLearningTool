package hba.testing.notificatingspcedlearning.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import hba.testing.notificatingspcedlearning.DatabaseHelper.Query;
import hba.testing.notificatingspcedlearning.R;
import hba.testing.notificatingspcedlearning.UImanager.CategoryListviewAdapter;
import hba.testing.notificatingspcedlearning.UImanager.MainSearchListViewAdapter;

/**
 * Created by TranKim on 1/28/2017.
 */
public class CategoryList extends AppCompatActivity {
    private ListView catList;
    private CategoryListviewAdapter listAdt;
    private Query dtb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorylist);
        catList=(ListView)findViewById(R.id.category_list);
        //Configuring view
        dtb=new Query(this);
        listAdt=new CategoryListviewAdapter(this);
        catList.setAdapter(listAdt);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listAdt.refresh();
    }
}
