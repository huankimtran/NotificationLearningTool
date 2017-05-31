package hba.testing.notificatingspcedlearning.Debug;

import android.content.Context;
import android.os.Looper;
import android.widget.TextView;

import org.w3c.dom.Text;

import hba.testing.notificatingspcedlearning.NetworkHandler.DataHandler.WebsiteReader;

/**
 * Created by TranKim on 2/22/2017.
 */
public class DebugThread extends Thread{
    Context ct;
    TextView tv;
    DebugThread(Context context, TextView tv){
        ct=context;
        tv=this.tv;
    }
    @Override
    public void run() {
        super.run();
        Looper.prepare();
        WebsiteReader web=new WebsiteReader(ct,"https://google.com");
        Looper.loop();
    }
}
