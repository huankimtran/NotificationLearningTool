package hba.testing.notificatingspcedlearning.Debug;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import hba.testing.notificatingspcedlearning.DatabaseHelper.dtbConst;

/**
 * Created by TranKim on 1/18/2017.
 */
public class DatabaseRestore {
    private String file;
    private FileInputStream inp;
    private FileOutputStream out;
    private Context ct;
    private Boolean isOk;
    public DatabaseRestore(Context context){
        ct=context;
        isOk=true;
        file=Environment.getExternalStorageDirectory().getPath()+"/data/"+dtbConst.DTBNAME[0];
        Toast.makeText(context,file, Toast.LENGTH_SHORT).show();
        try{
            inp=new FileInputStream(file);
        }catch (FileNotFoundException e){
            Toast.makeText(context,context.getFilesDir().getPath(),Toast.LENGTH_LONG).show();
            isOk=false;
        }
        try{
            out=new FileOutputStream(context.getDatabasePath(dtbConst.DTBNAME[0]).getPath());
        }catch (FileNotFoundException e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            isOk=false;
        }
    }
    public void copy(){
        int b;
        if(!isOk)
            return;
        else
            try{
                while((b=inp.read())!=-1)
                    out.write(b);
                inp.close();
                out.close();
            }catch (IOException e){

            }

    }
}
