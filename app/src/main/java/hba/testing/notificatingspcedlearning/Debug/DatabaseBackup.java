package hba.testing.notificatingspcedlearning.Debug;

import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import hba.testing.notificatingspcedlearning.DatabaseHelper.dtbConst;

/**
 * Created by TranKim on 1/18/2017.
 */
public class DatabaseBackup {
    private String file;
    private FileInputStream inp;
    private FileOutputStream out;
    private Context ct;
    private Boolean isOk;
    public DatabaseBackup(Context context){
        ct=context;
        isOk=true;
        file=context.getDatabasePath(dtbConst.DTBNAME[0]).getPath();
        Toast.makeText(context,"Backing process is running",Toast.LENGTH_SHORT).show();
        try{
            inp=new FileInputStream(file);
        }catch (FileNotFoundException e){
            Toast.makeText(context,context.getFilesDir().getPath(),Toast.LENGTH_LONG).show();
            isOk=false;
        }
        try{
            out=new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/"+dtbConst.DTBNAME[0]);
        }catch (FileNotFoundException e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            isOk=false;
        }
    }
    public void backup(){
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
    public static void backupDatabase(Context ct){
        new DatabaseBackup(ct).backup();
    }
    public static void requestWritePermission(Activity avt){
        String[] ask={"android.permission.WRITE_EXTERNAL_STORAGE"};
        avt.requestPermissions(ask, 200);
    }
}
