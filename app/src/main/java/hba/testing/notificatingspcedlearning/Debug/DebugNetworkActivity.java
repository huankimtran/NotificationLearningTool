package hba.testing.notificatingspcedlearning.Debug;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import hba.testing.notificatingspcedlearning.NetworkHandler.DataHandler.WebsiteReader;
import hba.testing.notificatingspcedlearning.R;

/**
 * Created by TranKim on 2/22/2017.
 */
public class DebugNetworkActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemdetail);
        TextView v= (TextView)findViewById(R.id.item_detail_content);
        (new DebugThread(this,v)).start();
    }
}
