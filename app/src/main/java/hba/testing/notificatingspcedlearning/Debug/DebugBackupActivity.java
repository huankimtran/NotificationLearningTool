package hba.testing.notificatingspcedlearning.Debug;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by TranKim on 1/18/2017.
 */
public class DebugBackupActivity extends Activity {
    DatabaseBackup dtb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        dtb=new DatabaseBackup(this);
        String[] ask={"android.permission.WRITE_EXTERNAL_STORAGE"};
        requestPermissions(ask, 200);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==200){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Granted",Toast.LENGTH_SHORT).show();
                dtb.backup();
            }else {
                Toast.makeText(this,"not Granted",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

