package com.example.shrutijagwani.browser;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ReaderActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private static final int REQUEST_CAMERA=1;
    private ZXingScannerView scannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checPermission()){
                Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show();
            }
            else
                requestPermission();
        }


    }
    private boolean checPermission(){
        return (ContextCompat.checkSelfPermission(this, CAMERA)== PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode,String permissions[],int grantResults[]){
        switch (requestCode){
            case REQUEST_CAMERA:if(grantResults.length>0){
                boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(cameraAccepted){
                    Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        if(shouldShowRequestPermissionRationale(CAMERA)){
                            displayAlertMessage("You need to allow access for both permissions", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{CAMERA},REQUEST_CAMERA);
                                }
                            });
                            return;
                        }
                    }


                }
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
            if(checPermission()){
            if (scannerView == null) {
                new ZXingScannerView(this);
                setContentView(scannerView);
            }
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
            else
                requestPermission();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton("OK",listener).setNegativeButton("CANCEL",null).create().show();
    }

    @Override
    public void handleResult(final Result result) {
        final String scanResult=result.getText();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(ReaderActivity.this);
            }


        });

        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Intent x=new Intent(Intent.ACTION_VIEW,Uri.parse(scanResult));
                //startActivity(x);

                String url=scanResult;
                Intent intent = new Intent(ReaderActivity.this,MainActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);
                finish();

            }
        });
        builder.setMessage(scanResult);
        AlertDialog alert=builder.create();
        alert.show();
    }
}
