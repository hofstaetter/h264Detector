package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Frame;
import main.java.org.frezy.h264.Stream;
import main.java.org.frezy.h264.VideoFrame;

import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static main.java.org.frezy.h264.VideoFrame.PictType.I;
import static main.java.org.frezy.h264.VideoFrame.PictType.P;

/**
 * Created by matthias on 06.06.17.
 */
public class BitrateDetector extends Detector implements Observer {
    private double averageDefaultBitrate = 0.0;

    private boolean sync = false;

    public BitrateDetector(Stream stream) {
        super(stream);

        stream.addObserver(this);
        this.averageDefaultBitrate = -1.0;
    }

    private short detectCounter = 0;

    public void detect(VideoFrame videoFrame) {
        if((averageDefaultBitrate + (averageDefaultBitrate * 0.10)) < videoFrame.getPktSize()) {
            if(this.detectCounter == 0)
                if(!super.state)
                    detected(true);
            if(this.detectCounter < 5)
                this.detectCounter++;
        } else {
            if(this.detectCounter == 0)
                if(super.state)
                    detected(false);
            if(this.detectCounter > -5)
                this.detectCounter--;
        }
    }

    public void detected(boolean state) {
        System.out.println("MOVEMENT CHANGED! " + state);
        super.detected(state);
    }

    private int resyncCounter = 0;

    public void resync(VideoFrame videoFrame) {
        //case bitrate rise
        //TODO

        //case bitrate drop
        if((averageDefaultBitrate - (averageDefaultBitrate * 0.3)) > videoFrame.getPktSize()) {
            resyncCounter++;

            if(resyncCounter >= 24) {
                sync = false;
                averageDefaultBitrate = 0;
                framesCount = 0;
                resyncCounter = 0;
            }
        } else {
            resyncCounter = 0;
        }
    }

    private int syncCount = 0;
    private long framesCount = 0;

    public void sync(VideoFrame videoFrame) {
        if(videoFrame.getPictType() == I) return;

        if(averageDefaultBitrate == -1) {
            averageDefaultBitrate = videoFrame.getPktSize();
            return;
        }
        double newAverageDefaultBitrate = 0.1 * videoFrame.getPktSize() + 0.9 * averageDefaultBitrate;

        double differenceBetweenAverages = (newAverageDefaultBitrate > averageDefaultBitrate) ? newAverageDefaultBitrate - averageDefaultBitrate : averageDefaultBitrate - newAverageDefaultBitrate; //diffence between old and new average

        //if
        if(averageDefaultBitrate * 0.05 < differenceBetweenAverages) {
            syncCount = 0;
            averageDefaultBitrate = newAverageDefaultBitrate;
            //System.out.println("SYNC RESET");
            return;
        }

        averageDefaultBitrate = newAverageDefaultBitrate;

        syncCount++;

        if(syncCount >= 96) sync = true;

        //System.out.println(averageDefaultBitrate);
    }

    @Override
    public void update(Observable o, Object arg) {
        Frame frame = (Frame) arg;

        if(frame instanceof VideoFrame) {
            VideoFrame videoFrame = (VideoFrame) frame;

            if(sync) {
                detect(videoFrame);
                resync(videoFrame);
                stable(videoFrame);
            }
            else sync(videoFrame);
        }
    }

    //test
    private final int STABLE_BUFFER_SIZE = 480;
    private Queue<Boolean> stableList = new ArrayBlockingQueue<Boolean>(STABLE_BUFFER_SIZE);

    public void stable(VideoFrame videoFrame) {
        if(videoFrame.getPictType() == I) return;

        if(stableList.size() == STABLE_BUFFER_SIZE) stableList.remove();

        double newAverageDefaultBitrate = 0.01 * videoFrame.getPktSize() + 0.99 * averageDefaultBitrate;
        if(newAverageDefaultBitrate > averageDefaultBitrate) { //rise
            stableList.add(true);
        } else if (newAverageDefaultBitrate < averageDefaultBitrate) { //fall
            stableList.add(false);
        } else return;

        int count = 0;
        for(Boolean b : stableList) {
            if(b) count++;
            else count--;
        }

        averageDefaultBitrate = newAverageDefaultBitrate;

        System.out.println("STABLE: " + count);
    }
}
