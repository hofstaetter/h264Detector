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
    private double averageDefaultBitrate;

    private int syncCount = 0;
    private boolean sync = false;

    public BitrateDetector(Stream stream) {
        super(stream);

        stream.addObserver(this);
        this.averageDefaultBitrate = -1.0;
    }

    private int highFrameCounter = 0;

    public void detectMovement(VideoFrame videoFrame) {
        if(sync) {
            if((averageDefaultBitrate < videoFrame.getPktSize() && (averageDefaultBitrate + (averageDefaultBitrate * 0.3)) < videoFrame.getPktSize()) ||
                    (averageDefaultBitrate > videoFrame.getPktSize() && (averageDefaultBitrate - (averageDefaultBitrate * 0.3)) > videoFrame.getPktSize())) {
                highFrameCounter++;
            } else {
                highFrameCounter = 0;
            }

            if(highFrameCounter >= 3) {
                System.out.println("MOVEMENT");
            }
        }
    }

    private long framesCount = 0;

    public void getDefaultBitrate(VideoFrame videoFrame) {
        if(!sync)
            if(videoFrame.getPictType() == P) { //IMPROVE THIS, movement from start
                averageDefaultBitrate = (averageDefaultBitrate * framesCount + videoFrame.getPktSize()) / (framesCount + 1);
                System.out.println(averageDefaultBitrate);

                if((averageDefaultBitrate < videoFrame.getPktSize() && (averageDefaultBitrate + (averageDefaultBitrate * 0.3)) > videoFrame.getPktSize()) ||
                        (averageDefaultBitrate > videoFrame.getPktSize() && (averageDefaultBitrate - (averageDefaultBitrate * 0.3)) < videoFrame.getPktSize()))
                    syncCount++;
                else
                    syncCount = 0;

                if(syncCount >= 40) sync = true;
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

            detectMovement(videoFrame);
            getDefaultBitrate(videoFrame);

            //System.out.println(videoFrame.getPktSize() + " " + videoFrame.getPictType());

            /*if (averageDefaultBitrate < frame.getPktSize()) { //average bitrate < than actual frame
                if ((averageDefaultBitrate + (averageDefaultBitrate * 0.3)) < frame.getPktSize()) { //frame out of range
                    if (secondChances > 0) { //has average another chance?
                        secondChances--;
                        return;
                    }
                    averageDefaultBitrate = frame.getPktSize();
                    secondChances = 1;
                    this.secondChanceCounter = 0;
                } else { //frame in range
                    //small average bitrate correction
                    averageDefaultBitrate = (averageDefaultBitrate + frame.getPktSize()) / 2;

                    //second chance add
                    this.secondChanceCounter++;
                    if (this.secondChanceCounter == 3) {
                        secondChances++;
                        this.secondChanceCounter = 0;
                    }
                }
            } else {
                if ((averageDefaultBitrate - (averageDefaultBitrate * 0.3)) > frame.getPktSize()) {
                    if (secondChances > 0) { //has average another chance?
                        secondChances--;
                        return;
                    }
                    averageDefaultBitrate = frame.getPktSize();
                    secondChances = 1;
                    this.secondChanceCounter = 0;
                } else { //frame in range
                    //small average bitrate correction
                    averageDefaultBitrate = (averageDefaultBitrate + frame.getPktSize()) / 2;

                    //second chance add
                    this.secondChanceCounter++;
                    if (this.secondChanceCounter == 3) {
                        secondChances++;
                        this.secondChanceCounter = 0;
                    }
                }
            }*/
        }
    }
}
