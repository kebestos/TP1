package com.jordan.tp1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static int PReqCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Verify the storage permission
        permissionReadStorage();

        //Open the View
        PictureView v = new PictureView(this);
        setContentView(v);

    }

    //Permission to access of the storage
    private void permissionReadStorage() {
        if(ContextCompat.checkSelfPermission( this,Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }
    }
}
