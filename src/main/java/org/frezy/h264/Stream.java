package main.java.org.frezy.h264;

import org.apache.commons.io.FileUtils;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CyclicBarrier;

import static main.java.org.frezy.h264Detector.Main.DEBUG;
import static main.java.org.frezy.h264Detector.Main.INPUT;
import static main.java.org.frezy.h264Detector.Main.STREAM;

/**
 * Created by matthias on 09.05.17.
 */

public class Stream implements Observer {
    public static final short BUFFER_SIZE = 24 * 5;
    //private final CyclicBarrier gate = new CyclicBarrier(2);
    //private final File tempDirectory = new File("tmp");

    private String input;

    private Thread frameReaderThread;
    private FrameReader frameReader;

    private ConcurrentLinkedDeque<Frame> buffer;

    private long startPts = -1;

    //private Thread frameCaptureThread;
    //private FrameCapture frameCapture;

    public ConcurrentLinkedDeque<Frame> getBuffer() {
        return buffer;
    }

    public void setBuffer(ConcurrentLinkedDeque<Frame> buffer) {
        this.buffer = buffer;
    }

    public Stream(String input) {

        this.input = input;

        this.frameReader = new FrameReader(this.input);
        this.frameReaderThread = new Thread(this.frameReader);

        this.buffer = new ConcurrentLinkedDeque<>();

        //this.frameCapture = new FrameCapture(this.input);
        //this.frameCaptureThread = new Thread(this.frameCapture);

        /*try {
            if(tempDirectory.mkdir())
                System.out.println("created Directory");
            FileUtils.cleanDirectory(tempDirectory);
            System.out.println(tempDirectory.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void open() {
        this.frameReaderThread.start();
        //this.frameCaptureThread.start();
    }

    public void close() {
        this.frameReaderThread.interrupt();
        //this.frameCaptureThread.interrupt();
    }

    public void addObserver(Observer o) {
        this.frameReader.addObserver(this);
        this.frameReader.addObserver(o);
    }

    @Override
    public void update(Observable o, Object arg) {
        Frame frame = (Frame) arg;

        if(this.startPts == -1) {
            this.startPts = frame.getPktPts();
        }

        if(this.buffer.size() >= BUFFER_SIZE) {
            this.buffer.poll();
        }
        this.buffer.addFirst(frame);
    }

    //endregion

    /*class FrameCapture extends Observable implements Runnable {
        private String input;

        public FrameCapture(String input) {
            this.input = input;
        }

        @Override
        public void run() {
            boolean first = true;
            try {
                while (!Thread.interrupted()) {
                    gate.await();
                    Process process = Runtime.getRuntime().exec("./ffmpeg -i " + this.input + " -vf [in]select=eq(pict_type\\,I),showinfo[out] -vsync vfr " + tempDirectory.getAbsolutePath() + "/iframe%03d.jpeg"); //+ " | grep 'media_type=/|pkt_pts_time=/|pkt_size=|pict_type=|coded_picture_number=|[/FRAME]'"); //TODO filter with ffmpeg (performance)

                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));


                    long count = 0;
                    File[] files;
                    String string;
                    while ((string = stdError.readLine()) != null) {
                        if(string.contains("plane_checksum")) {
                            //System.out.println(string);
                            count++;
                            if(count >= 24) {
                                files = tempDirectory.listFiles();
                                for(File file : files) {
                                    file.delete();
                                    break;
                                }
                            }
                        }
                    }
                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("Stream ends...");
        }
    }*/

    class FrameReader extends Observable implements Runnable {
        private String input;

        public FrameReader(String input) {
            this.input = input;
        }

        @Override
        public void run() {
            try {
                int retry = -1;
                boolean first = true;
                while (!Thread.interrupted()) {
                    retry++;
                    if(retry >= 5) {
                        System.out.println("No response from stream! Please check your input.");
                        System.exit(0);
                    }

                    //gate.await();
                    Process process = Runtime.getRuntime().exec("./ffprobe " + this.input + " -show_frames "); //+ " | grep 'media_type=/|pkt_pts_time=/|pkt_size=|pict_type=|coded_picture_number=|[/FRAME]'"); //TODO filter with ffmpeg (performance)

                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    if(retry == 0)
                        System.out.print("Listening to " + INPUT + "...");

                    String string;

                    String[] framestring = new String[25];
                    int count = 0;
                    while ((string = stdInput.readLine()) != null) {
                        if(first) {
                            System.out.println("done");
                            first = false;
                        }
                        /*if(DEBUG)
                            System.out.println(string);*/

                        framestring[count] = string;
                        count++;
                        if (string.equals("[/FRAME]")) { //frame end

                            //insert frame into buffer
                            Frame frame;
                            if (framestring[1].split("=")[1].equals("video")) {
                                frame = new VideoFrame(framestring);
                                /*if(((VideoFrame)frame).getPictType() == VideoFrame.PictType.I)
                                    System.out.println("pts: " + ((VideoFrame)frame).getPktPts() + " pts_time: " + ((VideoFrame)frame).getPktPtsTime());*/
                            } else {
                                frame = new AudioFrame(framestring);
                            }

                            //System.out.println(frame.toString());

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
