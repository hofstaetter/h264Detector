package main.java.org.frezy.h264;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by matthias on 09.05.17.
 */

public class Stream {
    private Thread thread;
    private String input;

    private FrameReader frameReader;
    private StreamBuffer streamBuffer;

    public Stream(String input) {
        this.input = input;

        this.frameReader = new FrameReader(this.input);
        this.thread = new Thread(this.frameReader);

        this.streamBuffer = new StreamBuffer(this);
    }

    public void open() {
        this.thread.start();
    }

    public void close() {
        this.thread.interrupt();
    }

    public void addObserver(Observer o) {
        this.frameReader.addObserver(o);
    }

    //region Getter and Setters

    public Thread.State getThreadState() {
        return this.thread.getState();
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public StreamBuffer getStreamBuffer() {
        return streamBuffer;
    }

    public void setStreamBuffer(StreamBuffer streamBuffer) {
        this.streamBuffer = streamBuffer;
    }

    //endregion

    class FrameReader extends Observable implements Runnable {
        private String input;

        public FrameReader(String input) {
            this.input = input;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    Process process = Runtime.getRuntime().exec("./ffprobe " + this.input + " -show_frames "); //+ " | grep 'media_type=/|pkt_pts_time=/|pkt_size=|pict_type=|coded_picture_number=|[/FRAME]'"); //TODO filter with ffmpeg (performance)

                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    //BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    System.out.println("Starting worker...");

                    String string;
                    String[] framestring = new String[25];
                    int count = 0;
                    while ((string = stdInput.readLine()) != null) {
                        framestring[count] = string;
                        count++;
                        if (string.equals("[/FRAME]")) { //frame end

                            //insert frame into buffer
                            Frame frame;
                            if (framestring[1].split("=")[1].equals("video")) {
                                frame = new VideoFrame(framestring);
                            } else {
                                frame = new AudioFrame(framestring);
                            }

                            this.setChanged();
                            this.notifyObservers(frame);

                            //reset variables
                            count = 0;
                        }
                    }
                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Stream ends...");
        }
    }
}
