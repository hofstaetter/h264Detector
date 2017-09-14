package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Frame;
import main.java.org.frezy.h264.Stream;
import main.java.org.frezy.h264.VideoFrame;

import java.util.ArrayDeque;
import java.util.Observable;
import java.util.Observer;

import static main.java.org.frezy.h264.VideoFrame.PictType.I;

public class Flatten implements Observer {
    Stream stream;

    public ArrayDeque<Double> buffer;

    public Flatten(Stream stream) {
        this.stream = stream;
        this.stream.addObserver(this);

        buffer = new ArrayDeque<>();
    }

    @Override
    public void update(Observable o, Object arg) {
        Frame frame = (Frame) arg;

        if(frame instanceof VideoFrame) {
            VideoFrame videoFrame = (VideoFrame) frame;
            if (videoFrame.getPictType() == I) return;

            if(this.stream.getBuffer().size() < 50)
                buffer.addFirst((double)videoFrame.getPktSize());
            else
                buffer.addFirst(this.stream.getBuffer().stream().filter(f -> f instanceof VideoFrame).map(VideoFrame.class::cast).filter(f -> f.getPictType() != VideoFrame.PictType.I).limit(50).mapToDouble(f -> f.getPktSize()).average().getAsDouble());
        }
    }
}
