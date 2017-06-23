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
import java.util.concurrent.CyclicBarrier;

/**
 * Created by matthias on 09.05.17.
 */

public class Stream {
    private final CyclicBarrier gate = new CyclicBarrier(2);
    private final File tempDirectory = new File("tmp");

    private Thread frameReaderThread;
    private Thread frameCaptureThread;
    private String input;

    private FrameReader frameReader;
    private FrameCapture frameCapture;
    private StreamBuffer streamBuffer;

    public Stream(String input) {
        this.input = input;

        this.frameReader = new FrameReader(this.input);
        this.frameCapture = new FrameCapture(this.input);
        this.frameReaderThread = new Thread(this.frameReader);
        this.frameCaptureThread = new Thread(this.frameCapture);

        this.streamBuffer = new StreamBuffer(this);

        try {
            if(tempDirectory.mkdir())
                System.out.println("created Directory");
            FileUtils.cleanDirectory(tempDirectory);
            System.out.println(tempDirectory.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void open() {
        this.frameReaderThread.start();
        this.frameCaptureThread.start();
    }

    public void close() {
        this.frameReaderThread.interrupt();
        this.frameCaptureThread.interrupt();
    }

    public void addObserver(Observer o) {
        this.frameReader.addObserver(o);
    }

    //region Getter and Setters

    public String getInput() {
        return input;
    }

    public StreamBuffer getStreamBuffer() {
        return streamBuffer;
    }

    //endregion

    class FrameCapture extends Observable implements Runnable {
        private String input;

        public FrameCapture(String input) {
            this.input = input;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    gate.await();
                    Process process = Runtime.getRuntime().exec("./ffmpeg -i " + this.input + " -vf [in]select=eq(pict_type\\,I),showinfo[out] -vsync vfr " + tempDirectory.getAbsolutePath() + "/iframe%03d.jpeg"); //+ " | grep 'media_type=/|pkt_pts_time=/|pkt_size=|pict_type=|coded_picture_number=|[/FRAME]'"); //TODO filter with ffmpeg (performance)

                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    System.out.println("Starting FrameCapture thraed...");

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
    }

    class FrameReader extends Observable implements Runnable {
        private String input;

        public FrameReader(String input) {
            this.input = input;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    gate.await();
                    Process process = Runtime.getRuntime().exec("./ffprobe " + this.input + " -show_frames "); //+ " | grep 'media_type=/|pkt_pts_time=/|pkt_size=|pict_type=|coded_picture_number=|[/FRAME]'"); //TODO filter with ffmpeg (performance)

                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    //BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    System.out.println("Starting FrameReader thread...");

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
                                /*if(((VideoFrame)frame).getPictType() == VideoFrame.PictType.I)
                                    System.out.println("pts: " + ((VideoFrame)frame).getPktPts() + " pts_time: " + ((VideoFrame)frame).getPktPtsTime());*/
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
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("Stream ends...");
        }
    }
}
