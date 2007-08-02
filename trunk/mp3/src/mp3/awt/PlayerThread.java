package mp3.awt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import mp3.Decoder;

public class PlayerThread implements Runnable {

    private Decoder decoder = new Decoder();
    private File currentFile;
    private ArrayList fileList;
    private Thread thread;
    private boolean stop;

    public void stopPlaying() {
        stop = true;
        decoder.stop();
        try {
            thread.join();
        } catch (InterruptedException e) {
            // ignore
        }
    }
    
    public static PlayerThread startPlaying(File file, ArrayList list) {
        PlayerThread t = new PlayerThread();
        t.currentFile = file;
        t.fileList = list;
        Thread thread = new Thread(t);
        t.thread = thread;
        thread.start();
        return t;
    }
    
    public void run() {
        try {
            while(!stop) {
                if(currentFile == null) {
                    if(fileList != null && fileList.size() > 0) {
                        currentFile = (File) fileList.remove(0);
                    }
                }
                if(currentFile == null) {
                    break;
                } else {
                    play(currentFile);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private void play(File file) throws IOException {
        stop = false;
        if (!file.getName().endsWith(".mp3")) {
            return;
        }
        System.out.println("playing: " + file);
        FileInputStream in = new FileInputStream(file);
        BufferedInputStream bin = new BufferedInputStream(in, 128 * 1024);
        decoder.play(bin);
        currentFile = null;
    }

    public void playNext() {
        decoder.stop();
        currentFile = null;
    }
    
}
