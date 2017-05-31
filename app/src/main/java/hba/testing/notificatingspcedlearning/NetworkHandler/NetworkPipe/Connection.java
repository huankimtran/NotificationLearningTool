package hba.testing.notificatingspcedlearning.NetworkHandler.NetworkPipe;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;

import java.net.MalformedURLException;
import java.net.URL;
/**
 * Created by TranKim on 2/20/2017.
 */
public class Connection {
    private HttpsURLConnection connection;
    private InputStream inp;
    private Context ct;
    public Connection(Context context,String url){
        ct=context;
        try {
            connection = (HttpsURLConnection) (new URL(url)).openConnection();
            inp= connection.getInputStream();
        }catch(MalformedURLException e){
            Toast.makeText(ct,e.getMessage(), Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            Toast.makeText(ct,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    public InputStream getInput(){
        return inp;
    }
}