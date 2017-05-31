package hba.testing.notificatingspcedlearning.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import hba.testing.notificatingspcedlearning.R;
import hba.testing.notificatingspcedlearning.UImanager.OnlineSearchResultListAdapter;
import hba.testing.notificatingspcedlearning.UImanager.ViewClickListener;
import hba.testing.notificatingspcedlearning.UImanager.ViewKeyboardListener;

/**
 * Created by TranKim on 2/20/2017.
 */
public class OnlineSearch extends AppCompatActivity {
    private EditText search;
    private ListView resultList,dtbresult;
    private ImageButton addManually;
    private TextView searchState;
    private OnlineSearchResultListAdapter adt,adt2;
    private Handler handle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onlinesearch);
        handle=new Handler(getMainLooper());
        search=(EditText)findViewById(R.id.online_search_search_box);
        searchState=(TextView)findViewById(R.id.online_search_state);
        addManually=(ImageButton)findViewById(R.id.online_search_add_item_manually);
        resultList=(ListView)findViewById(R.id.online_search_result_list);
        dtbresult=(ListView)findViewById(R.id.online_search_database_result_list);
        adt=new OnlineSearchResultListAdapter(this,handle);
        adt2=new OnlineSearchResultListAdapter(this,handle);
        resultList.setAdapter(adt);
        dtbresult.setAdapter(adt2);
        search.addTextChangedListener(new ViewKeyboardListener.OnlineSearchSearchBoxListener(this,adt,adt2, handle, searchState));
        addManually.setOnClickListener(new ViewClickListener.OnlineAddManually());
    }
}
