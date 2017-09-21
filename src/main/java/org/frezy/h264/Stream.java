package main.java.org.frezy.h264;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedDeque;

import static main.java.org.frezy.h264Detector.Main.INPUT;
import static main.java.org.frezy.h264Detector.Main.VERBOSE;

/**
 * Created by matthias on 09.05.17.
 */

public class Stream implements Observer {
    public static short BUFFER_SIZE = 24 * 5;

    private String input;

    private Thread frameReaderThread;
    private FrameReader frameReader;

    private ConcurrentLinkedDeque<Frame> buffer;

    private long startPts = -1;

    public ConcurrentLinkedDeque<Frame> getBuffer() {
        return buffer;
    }

    public Stream(String input) {
        this.input = input;

        this.frameReader = new FrameReader(this.input);
        this.frameReaderThread = new Thread(this.frameReader);

        this.buffer = new ConcurrentLinkedDeque<>();

        this.frameReader.addObserver(this);
    }

    public Stream(String input, short bufferSize) {

        this.input = input;

        this.frameReader = new FrameReader(this.input);
        this.frameReaderThread = new Thread(this.frameReader);

        this.buffer = new ConcurrentLinkedDeque<>();

        this.frameReader.addObserver(this);

        this.BUFFER_SIZE = bufferSize;
    }

    public void open() {
        this.frameReaderThread.start();
    }

    public void close() {
        this.frameReaderThread.interrupt();
    }

    public void addObserver(Observer o) {
        this.frameReader.addObserver(o);
    }

    @Override
    public void update(Observable o, Object arg) {
        Frame frame = (Frame) arg;

        if(this.startPts == -1) {
            this.startPts = frame.getPktPts();
        }

        if(this.buffer.size() >= BUFFER_SIZE) {
            this.buffer.pollLast();
        }
        this.buffer.addFirst(frame);
    }

    class FrameReader extends Observable implements Runnable {
        private String input;
        private long lastResponse;

        public FrameReader(String input) {
            this.input = input;
        }

        @Override
        public void run() {
            try {
                if(VERBOSE) System.out.println("DEBUG: Trying to read from " + this.input + "...");

                lastResponse = System.currentTimeMillis();
                while (!Thread.interrupted()) {
                    if(System.currentTimeMillis() > lastResponse + 10000) {
                        System.out.println("No response from RTP stream!");
                        System.exit(-1);
                    }

                    Process process = Runtime.getRuntime().exec("./ffprobe " + this.input + " -show_frames "); //+ " | grep 'media_type=/|pkt_pts_time=/|pkt_size=|pict_type=|coded_picture_number=|[/FRAME]'"); //TODO filter with ffmpeg (performance)

                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    //BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

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

                            //notify observers
                            this.setChanged();
                            this.notifyObservers(frame);

                            //reset variables
                            count = 0;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Stream ends...");
        }
    }
}
