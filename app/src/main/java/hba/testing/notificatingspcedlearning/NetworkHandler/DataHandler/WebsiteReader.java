package hba.testing.notificatingspcedlearning.NetworkHandler.DataHandler;

import android.content.Context;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import hba.testing.notificatingspcedlearning.NetworkHandler.NetworkPipe.Connection;

/**
 * Created by TranKim on 2/22/2017.
 */
//this is not used!!! just for debug
public class WebsiteReader {
    private Context ct;
    private Document website;
    private Connection con;
    public WebsiteReader(Context context,String websiteUrl){
        ct=context;
        try {
            Document doc= Jsoup.parse((new URL("https://google.com")),3000);
            Toast.makeText(ct,doc.getElementById("gstyle").data(),Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(ct,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
