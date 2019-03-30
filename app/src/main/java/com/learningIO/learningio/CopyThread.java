package com.learningIO.learningio;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CopyThread extends Thread{
    // Creating variables.
    File[] sourceFiles;
    File path;

    // Parametrised Constructor that takes in the list of files to be copied.
    public CopyThread(File[] sourceFiles, File path){
        // Initialises the fields.
        this.path = path;
        this.sourceFiles = sourceFiles;
      //  this.handler = handler;

    }

    @Override
    public void run() {
        for(int i = 0; i<this.sourceFiles.length;i++){
            try {
                copyFilesOver(new FileInputStream(sourceFiles[i]), path.getPath() , sourceFiles[i].getName());
//                Message message = Message.obtain();
//                message.obj = "";
//                handler.sendMessage(message);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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
