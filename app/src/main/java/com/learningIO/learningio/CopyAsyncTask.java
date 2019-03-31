package com.learningIO.learningio;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;

public class CopyAsyncTask extends AsyncTask<File[], Void, Void> {
    // Setting up variables.
    private WeakReference<MainActivity> activityWeakReference;
    private File[] sourceFiles;
    private File path;

    /**
     *
     * @param activity The main activity.
     * @param path The path to write the files
     */
    public CopyAsyncTask(MainActivity activity, File path){
        this.activityWeakReference = new WeakReference<MainActivity>(activity);
        this.path = path;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     *
     * @param files The files that need to be transfered.
     * @return
     */
    @Override
    protected Void doInBackground(File[]... files) {
        long time = System.nanoTime();
        System.out.println("DEBUGING: " + time);
        // Getting the first value of files as this contains the files needed.
        this.sourceFiles = files[0];
        // Looping though each file
        for(int i = 0; i<this.sourceFiles.length;i++){
            try {
                // Copying each file to destination
                copyFilesOver(new FileInputStream(sourceFiles[i]), path.getPath() , sourceFiles[i].getName());
                publishProgress();
//                Message message = Message.obtain();
//                message.obj = "";
//                handler.sendMessage(message);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println("DEBUGING: " + (System.nanoTime() - time));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    /**
     *  Updating main UI thread
     * @param values
     */
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        // Getting main activity.
        MainActivity activity = activityWeakReference.get();
        // Calling updateThreadProgress.
        activity.updateThreadProgress();

    }

    /**
     *
     * @param intPath Source Path
     * @param outPath Destination Path.
     * @param nameOfFile The name of the file requested.
     */
    public synchronized void copyFilesOver(FileInputStream intPath, String outPath, String nameOfFile){
        try {
            // Setting the inital file channel path.
            FileChannel input = intPath.getChannel();
            // Creating the directory if it does not exist
            new File(outPath).mkdir();
            // Setting up the output file channel
            FileChannel output = new FileOutputStream(outPath + "/" + nameOfFile).getChannel();
            // Transfering the files to the new folder.
            input.transferTo(0,input.size(),output);
            input.close();
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
