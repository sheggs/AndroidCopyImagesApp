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
    private Button copyFilesButton = null;
    private TextView permsRequiredTextView = null;
    private Button refreshButton = null;
    public String originFileName = null;
    public String outputFile = null;
    private int numberOfFilesCopied = 0;
    private int totalNumberOfFiles = 0;
    private MainActivity activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Total number of files
        totalNumberOfFiles = 2;
        // Source folder name inside the Downloads folder.
        originFileName = "sourceFolder";
        // Destination Folder inside the downloads folder.
        outputFile = "thiccbooty";
        // Storing the current activity.
        this.activity = this;
        // Storing the interactive buttons and text view
        permsRequiredTextView = (TextView) findViewById(R.id.tvMain);
        copyFilesButton = (Button) findViewById(R.id.buttonToCopy);
        refreshButton = (Button) findViewById(R.id.refreshListing);
        // Setting the textView.
        permsRequiredTextView.setText("No Permissions. Restart app.");
        // Checking if the build verion is greater than 23.
        if (Build.VERSION.SDK_INT >= 23) {
            // Requesting permissions
            requestPermissions(Permission_Requests, 138);
        }
        // Checking if the user has permissions
        if(canAccessPermissions()){
            // Updating the text view to show the user has permissions
            permsRequiredTextView.setText("Has Permissions");
            // Obtaining download folders details to display in TextViews.
            obtainingDownloadFolderDetails();
            // Refresh button to refresh folder contents.
            refreshButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    obtainingDownloadFolderDetails();
                }
            });
            // Function to be completed when the button is pressed.
            copyFilesButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // Setting the path to the downloads path.
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    // Setting the source path.
                    File pathOfFiles = new File(path.getPath() + "/" + originFileName);
                    // Getting the list of files in the source folder
                    File[] sourcesFiles = pathOfFiles.listFiles();
                    // Splitting the number of files into two and flooring the number.
                    int numberOfFilesForFirstThread = (int) Math.floor(sourcesFiles.length/2);
                    // Storing the files for transfer of the first thread
                    File[] filesForFirstThread = new File[numberOfFilesForFirstThread];
                    // Storing the files for transfer of the second thread.
                    File[] filesForSecondThread = new File[sourcesFiles.length - numberOfFilesForFirstThread];
                    // Looping through and adding the files for transfer.
                    for(int i = 0; i<numberOfFilesForFirstThread; i++){
                        filesForFirstThread[i] = sourcesFiles[i];
                    }
                    // Looping through and adding the files for transfer of the second thread.
                    int x = 0;
                    for(int i = (numberOfFilesForFirstThread ); i<(sourcesFiles.length);i++){

                        filesForSecondThread[x] = sourcesFiles[i];
                        x++;

                    }
                    // Creating two AsyncTask for the file transfer.
                    CopyAsyncTask A = new CopyAsyncTask(activity,filesForFirstThread, new File(path.getPath() + "/" + outputFile));
                    CopyAsyncTask B = new CopyAsyncTask(activity,filesForSecondThread, new File(path.getPath() + "/" + outputFile));

                    System.out.println("___------FIRSt------___");
                    System.out.println(filesForFirstThread[0].getName());
                    System.out.println(filesForSecondThread[0].getName());
                    System.out.println("_____LAST_____");
                    System.out.println(filesForFirstThread[filesForFirstThread.length - 1]);
                    System.out.println(filesForSecondThread[filesForSecondThread.length - 1]);
                    System.out.println("+++++STARTiNG_____");


                    // To run AsyncTasks concurrently.
                    A.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,filesForFirstThread);
                    B.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,filesForSecondThread);

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

    /**
     * This is to update the thread progress from AsyncTask.
     */
    public void updateThreadProgress(){
        // Increments the shared counter
        this.numberOfFilesCopied++;
        // Updates the text view.
        TextView ThreadUpdate = findViewById(R.id.transferRate);
        ThreadUpdate.setText("Currently " + this.numberOfFilesCopied + "/" + this.totalNumberOfFiles + " files have been copied");
        // Refreshes directory listing on app.
        obtainingDownloadFolderDetails();

    }

    /**
     * Checking if the user has permission
     * @return A boolean value whether the app has permission to read or write files.
     */
    public boolean canAccessPermissions(){
        // Default boolean value set to false.
        Boolean canAccess = false;
        // Loops through all permissions required
        for(int i = 0; i<this.Permission_Requests.length;i++) {
            // Checks if the activity has permission
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getApplicationContext(), this.Permission_Requests[i])) {
                canAccess = true;
            } else {
                canAccess = false;
                break;
            }
        }
        // Returns boolean value.
        return canAccess;
    }

    /**
     * Updates text views with directory information about the target and source folder.
     */
    public void obtainingDownloadFolderDetails(){
        // Setting up textview variables.
        TextView directoryTV = (TextView) findViewById(R.id.DirectoryInfo);
        TextView directoryTV_target = (TextView) findViewById(R.id.targetFolder);
        // Obtaining download path.
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // Obtaining target path.
        File targetPath = new File(path.getPath() + "/" + outputFile);
        // Obtaining source path.
        path = new File(path.getPath() + "/" + originFileName);
        // Getting list of source path files.
        File[] sourcesFiles = path.listFiles();
        // Getting list of files in the target path.
        File[] targetSourceFiles = targetPath.listFiles();
        // Updates textivews with information about the directory.
        directoryTV.setText("Directory Files: " + "\n" + path.getPath());
        directoryTV_target.setText("Target Folder: " + "\n" + path.getPath());
        // Checking if the path exists
        if(path.exists()){
            // Update the total number of files
            this.totalNumberOfFiles = sourcesFiles.length;
            // Looping through every file in the source folder
            for(int i = 0; i<sourcesFiles.length;i++){
                // Adding file name to the textview.
                directoryTV.setText(directoryTV.getText() + "\n" + sourcesFiles[i].getName());
            }
        }else{
            // If the source folder does not exist.
            directoryTV.setText( directoryTV.getText() + " Folder does not exist " );
        }
        // Checking if the target path exists.
        if(targetPath.exists()) {
            for (int i = 0; i < targetSourceFiles.length; i++) {
                // Adding path file names into text view.
                directoryTV_target.setText(directoryTV_target.getText() + "\n" + targetSourceFiles[i].getName());
            }
        }else{
            directoryTV_target.setText("Target Folder does not exist " + targetPath.getPath());
        }

    }


}
