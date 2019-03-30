package com.learningIO.learningio;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Environment;

import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Build;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    public final String[] Permission_Requests = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Button copyFilesButton;
    private TextView permsRequiredTextView;
    public final String originFileName = "sourceFolder";
    public final String outputFile = "thiccbooty";
    private int numberOfFilesCopied = 0;
    private int totalNumberOfFiles = 2;
    private MainActivity activity;
 //   private CopyAsyncTask B;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.activity = this;
        permsRequiredTextView = (TextView) findViewById(R.id.tvMain);
        copyFilesButton = (Button) findViewById(R.id.buttonToCopy);
        permsRequiredTextView.setText("No Permissions. Restart app.");
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(Permission_Requests, 138);
            //System.out.println("REQUESTING");
        }
        if(canAccessPermissions()){
            permsRequiredTextView.setText("Has Permissions");
            obtainingDownloadFolderDetails();
            copyFilesButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File pathOfFiles = new File(path.getPath() + "/" + originFileName);
                    File[] sourcesFiles = pathOfFiles.listFiles();
                    int numberOfFilesForFirstThread = (int) Math.floor(sourcesFiles.length/2);
                    File[] filesForFirstThread = new File[numberOfFilesForFirstThread];
                    File[] filesForSecondThread = new File[sourcesFiles.length - numberOfFilesForFirstThread];
                    for(int i = 0; i<numberOfFilesForFirstThread; i++){
                        filesForFirstThread[i] = sourcesFiles[i];
                    }
                    int x = 0;
                    for(int i = (numberOfFilesForFirstThread ); i<(sourcesFiles.length);i++){

                        filesForSecondThread[x] = sourcesFiles[i];
                        x++;

                    }
                    CopyAsyncTask A = new CopyAsyncTask(activity,filesForFirstThread, new File(path.getPath() + "/" + outputFile));
                    CopyAsyncTask B = new CopyAsyncTask(activity,filesForSecondThread, new File(path.getPath() + "/" + outputFile));

                    System.out.println("___------FIRSt------___");
                    System.out.println(filesForFirstThread[0].getName());
                    System.out.println(filesForSecondThread[0].getName());
                    System.out.println("_____LAST_____");
                    System.out.println(filesForFirstThread[filesForFirstThread.length - 1]);
                    System.out.println(filesForSecondThread[filesForSecondThread.length - 1]);
                    System.out.println("+++++STARTiNG_____");

                    A.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    B.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                }
            });
        }

    }

//    public void createSourceFiles(){
//        boolean doesSourceExists = false;
//        String[] images = {"sample1.JPG","sample2.jpeg"};
//        Bitmap[] bitmapImages = {BitmapFactory.decodeResource( getResources(), R.drawable.sample1),BitmapFactory.decodeResource( getResources(), R.drawable.sample2)};
//        for(int i = 0; i<bitmapImages.length;i++){
//             try {
//                String DownloadsFolder = Environment.getExternalStorageDirectory().toString();
//                 new File(DownloadsFolder + "/" + originFileName).mkdir();
//                File file = new File(DownloadsFolder + "/" + originFileName, images[i]);
//                FileOutputStream savingFile = new FileOutputStream(file);
//                bitmapImages[i].compress(Bitmap.CompressFormat.PNG, 100, savingFile);
//                 savingFile.flush();
//                 savingFile.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                 e.printStackTrace();
//             }
//
//        }
//        obtainingDownloadFolderDetails();
//    }

    public void updateThreadProgress(){
        this.numberOfFilesCopied++;
        TextView ThreadUpdate = findViewById(R.id.transferRate);
        ThreadUpdate.setText("Currently " + this.numberOfFilesCopied + "/" + this.totalNumberOfFiles + " files have been copied");
        obtainingDownloadFolderDetails();

    }

    public boolean canAccessPermissions(){
        Boolean canAccess = true;
        for(int i = 0; i<this.Permission_Requests.length;i++) {
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getApplicationContext(), this.Permission_Requests[i])) {
                canAccess = true;
            } else {
                canAccess = false;
                break;
            }
        }
        return canAccess;
    }
    public void obtainingDownloadFolderDetails(){
        TextView directoryTV = (TextView) findViewById(R.id.DirectoryInfo);
        TextView directoryTV_target = (TextView) findViewById(R.id.targetFolder);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File targetPath = new File(path.getPath() + "/" + outputFile);
        path = new File(path.getPath() + "/" + originFileName);
        File[] sourcesFiles = path.listFiles();
        File[] targetSourceFiles = targetPath.listFiles();
        directoryTV.setText("Directory Files: " + "\n" + path.getPath());
        directoryTV_target.setText("Target Folder: " + "\n" + path.getPath());
        if(path.exists()){
            this.totalNumberOfFiles = sourcesFiles.length;
            for(int i = 0; i<sourcesFiles.length;i++){
                directoryTV.setText(directoryTV.getText() + "\n" + sourcesFiles[i].getName());
            }
        }else{
            directoryTV.setText( directoryTV.getText() + " Folder does not exist " );
        }
        if(targetPath.exists()) {
            for (int i = 0; i < targetSourceFiles.length; i++) {
                directoryTV_target.setText(directoryTV_target.getText() + "\n" + targetSourceFiles[i].getName());
            }
        }else{
            directoryTV_target.setText("Target Folder does not exist " + targetPath.getPath());
        }

    }


}
