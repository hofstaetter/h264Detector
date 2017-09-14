package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Frame;
import main.java.org.frezy.h264.Stream;
import main.java.org.frezy.h264.VideoFrame;

import java.util.ArrayDeque;
import java.util.Observable;
import java.util.Observer;

import static main.java.org.frezy.h264.VideoFrame.PictType.I;

public class ExponentionalMovingAverage implements Observer {
    private Stream stream;

    public ArrayDeque<Double> buffer;

    private double alpha;

    public ExponentionalMovingAverage(Stream stream) {
        this.stream = stream;
        this.alpha = 0.01;
        this.buffer = new ArrayDeque<>(100);

        this.stream.addObserver(this);
    }
    public ExponentionalMovingAverage(Stream stream, double alpha) {
        this.stream = stream;
        this.alpha = alpha;
        this.buffer = new ArrayDeque<>(100);

        this.stream.addObserver(this);
    }

    public ExponentionalMovingAverage(Stream stream, double alpha, int bufferSize) {
        this.stream = stream;
        this.alpha = alpha;
        this.buffer = new ArrayDeque<>(bufferSize);

        this.stream.addObserver(this);
    }

    private int _count;
    @Override
    public void update(Observable o, Object arg) {
        Frame frame = (Frame) arg;

        if(frame instanceof VideoFrame) {
            VideoFrame videoFrame = (VideoFrame) frame;
            if (videoFrame.getPictType() == I) return;

            if(this.buffer.isEmpty()) {
                this.buffer.addFirst((double)videoFrame.getPktSize());
                return;
            }

            //calc new ma value
            this.buffer.addFirst(videoFrame.getPktSize() * alpha + this.buffer.getFirst() * (1 - alpha));
        }
    }
}
