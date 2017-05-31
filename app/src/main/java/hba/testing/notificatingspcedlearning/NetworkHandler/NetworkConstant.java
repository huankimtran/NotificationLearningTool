package hba.testing.notificatingspcedlearning.NetworkHandler;

import android.widget.Toast;

import java.io.Console;
import java.net.URL;

/**
 * Created by TranKim on 2/23/2017.
 */
public class NetworkConstant {
    public static final int NETWORK_TIME_OUT=3000;
    public static String[] dictionaryWebsite = {"https://en.oxforddictionaries.com/definition/"};
    public static URL oxfordSearchURL(int urlIndex,String keyword){
        URL url;
        try{
            url=new URL(dictionaryWebsite[urlIndex]+keyword.replaceAll(" ","_"));
        }catch(Exception e){
            return null;
        }
        return url;
    }
}
