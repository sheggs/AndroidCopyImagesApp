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

    private WeakReference<MainActivity> activityWeakReference;
    private File[] sourceFiles;
    private File path;
    public CopyAsyncTask(MainActivity activity,File[] sourceFiles, File path){
        this.activityWeakReference = new WeakReference<MainActivity>(activity);
        this.path = path;
        this.sourceFiles = null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(File[]... files) {
        long time = System.nanoTime();
        System.out.println("DEBUGING: " + time);
        this.sourceFiles = files[0];
        for(int i = 0; i<this.sourceFiles.length;i++){
            try {
                copyFilesOver(new FileInputStream(sourceFiles[i]), path.getPath() , sourceFiles[i].getName());
                System.out.println(sourceFiles[i].getName());
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

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        MainActivity activity = activityWeakReference.get();
        activity.updateThreadProgress();

    }

    public void copyFilesOver(FileInputStream intPath, String outPath, String nameOfFile){
        try {
            FileChannel input = intPath.getChannel();
            new File(outPath).mkdir();
            FileChannel output = new FileOutputStream(outPath + "/" + nameOfFile).getChannel();
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
