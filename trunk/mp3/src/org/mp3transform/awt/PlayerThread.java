package org.mp3transform.awt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.mp3transform.Decoder;

public class PlayerThread implements Runnable {

    private Decoder decoder = new Decoder();
    private File currentFile;
    private ArrayList fileList;
    private Thread thread;
    private boolean stop;
    private Player player;

    public void stopPlaying() {
        stop = true;
        decoder.stop();
        try {
            thread.join();
        } catch (InterruptedException e) {
            // ignore
        }
    }
    
    public static PlayerThread startPlaying(Player player, File file, ArrayList list) {
        PlayerThread t = new PlayerThread();
        t.player = player;
        t.currentFile = file;
        t.fileList = list;
        Thread thread = new Thread(t);
        t.thread = thread;
        thread.start();
        return t;
    }
    
    public void run() {
        try {
            while (!stop) {
                if (currentFile == null) {
                    if (fileList != null && fileList.size() > 0) {
                        currentFile = (File) fileList.remove(0);
                    }
                }
                if (currentFile == null) {
                    break;
                } else {
                    play(currentFile);
                }
            }
            player.setCurrentFile(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void play(File file) throws IOException {
        player.setCurrentFile(file);
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
