package main.java.org.frezy.h264;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by matthias on 09.05.17.
 */
public class Stream {
    private static final short BUFFER_SIZE = 24 * 5;

    private ArrayBlockingQueue<Frame> frames;
    private ArrayBlockingQueue<Double> averagePackageSizes;
    private Thread thread;
    private String input;

    private volatile long readedFramesCount;
    private volatile long iframes;
    private volatile long pframes;
    private volatile long bframes;
    private volatile double averagePackageSize;

    public Stream(String input) {
        this.input = input;
        this.frames = new ArrayBlockingQueue<Frame>(BUFFER_SIZE);
        this.averagePackageSizes = new ArrayBlockingQueue<Double>(2);

        this.thread = new Thread(new FrameReader(this.input));
    }

    public void open() {
        this.thread.start();
    }

    public void close() {
        this.thread.interrupt();
    }

    private void calculateAveragePackageSize() {
        /*averagePackageSize = 0;
        for(Frame frame : this.frames) {
            averagePackageSize += frame.getPktSize();
        }
        averagePackageSize /= BUFFER_SIZE;*/
        //Java 8
        this.setAveragePackageSize(frames.stream().mapToInt(x -> x.getPktSize()).average().orElseThrow(IllegalStateException::new));
    }

    public long getReadedFramesCount() {
        return readedFramesCount;
    }

    public void setReadedFramesCount(long readedFramesCount) {
        this.readedFramesCount = readedFramesCount;
    }

    public ArrayBlockingQueue<Frame> getFrames() {
        return frames;
    }

    public void setFrames(ArrayBlockingQueue<Frame> frames) {
        this.frames = frames;
    }

    public Thread.State getThreadState() {
        return this.thread.getState();
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public long getIframes() {
        return iframes;
    }

    public void setIframes(long iframes) {
        this.iframes = iframes;
    }

    public long getPframes() {
        return pframes;
    }

    public void setPframes(long pframes) {
        this.pframes = pframes;
    }

    public long getBframes() {
        return bframes;
    }

    public void setBframes(long bframes) {
        this.bframes = bframes;
    }

    public double getAveragePackageSize() {
        return averagePackageSize;
    }

    public void setAveragePackageSize(double averagePackageSize) {
        this.averagePackageSize = averagePackageSize;
    }

    public ArrayBlockingQueue<Double> getAveragePackageSizes() {
        return averagePackageSizes;
    }

    class FrameReader implements Runnable {
        private String input;

        public FrameReader(String input) {
            this.input = input;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    Process process = Runtime.getRuntime().exec("./ffprobe " + this.input + " -show_frames");

                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    //BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    System.out.println("Starting worker...");

                    String string;
                    String[] framestring = new String[25];
                    int count = 0;
                    Frame.MediaType mediaType;
                    while ((string = stdInput.readLine()) != null) {
                        //read full frame string
                        //System.out.println("READ: " + string);
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

                            //remove first element from buffer if overflow
                            if(frames.size() >= BUFFER_SIZE) {
                                frames.poll();
                            }
                            frames.put(frame);

                            //update statistics
                            updateStatistics(frame);

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

        private void insertFrameIntoBuffer(Frame frame) {

        }

        private void updateStatistics(Frame frame) {
            if(frame instanceof VideoFrame) { //videoframe
                updateAveragePktSize((VideoFrame)frame);
                updateFrameTypes((VideoFrame)frame);
            } else { //audioframe

            }
        }

        private void updateAveragePktSize(VideoFrame videoFrame) {
            averagePackageSize = (averagePackageSize + videoFrame.getPktSize()) / 2;

            if(averagePackageSizes.size() >= 2) {
                averagePackageSizes.poll();
            }
            try {
                averagePackageSizes.put(averagePackageSize);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void updateFrameTypes(VideoFrame videoFrame) {
            if(videoFrame.getPictType() == VideoFrame.PictType.I)
                iframes++;
            else if(videoFrame.getPictType() == VideoFrame.PictType.P)
                pframes++;
            else bframes++;
        }
    }
}
