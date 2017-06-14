package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Frame;
import main.java.org.frezy.h264.Stream;
import main.java.org.frezy.h264.VideoFrame;

import java.util.Observable;
import java.util.Observer;

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
        if((averageDefaultBitrate + (averageDefaultBitrate * 0.3)) < videoFrame.getPktSize()) {
            if(this.detectCounter == 0)
                if(!super.state)
                    detected(true);
            if(this.detectCounter < 3)
                this.detectCounter++;
        } else {
            if(this.detectCounter == 0)
                if(super.state)
                    detected(false);
            if(this.detectCounter > -3)
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

            if(resyncCounter >= 6) {
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
        if(videoFrame.getPictType() == P) {
            averageDefaultBitrate = (averageDefaultBitrate * framesCount + videoFrame.getPktSize()) / (framesCount + 1);
            System.out.println(averageDefaultBitrate);

            if((averageDefaultBitrate < videoFrame.getPktSize() && (averageDefaultBitrate + (averageDefaultBitrate * 0.3)) > videoFrame.getPktSize()) ||
                    (averageDefaultBitrate > videoFrame.getPktSize() && (averageDefaultBitrate - (averageDefaultBitrate * 0.3)) < videoFrame.getPktSize()))
                syncCount++;
            else
                syncCount = 0;

            if(syncCount >= 24) sync = true;
            System.out.println(sync);
            framesCount++;
        }

        System.out.println(averageDefaultBitrate);
    }

    @Override
    public void update(Observable o, Object arg) {
        Frame frame = (Frame) arg;

        if(frame instanceof VideoFrame) {
            VideoFrame videoFrame = (VideoFrame) frame;

            if(sync) {
                detect(videoFrame);
                resync(videoFrame);
            }
            else sync(videoFrame);
        }
    }
}
