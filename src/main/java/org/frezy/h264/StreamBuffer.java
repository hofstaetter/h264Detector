package main.java.org.frezy.h264;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by matthias on 06.06.17.
 */
public class StreamBuffer implements Observer {
    private static final short BUFFER_SIZE = 24 * 15;

    private ArrayBlockingQueue<Frame> frames;

    public StreamBuffer(Stream stream) {
        stream.addObserver(this);

        frames = new ArrayBlockingQueue<Frame>(BUFFER_SIZE);
    }

    public ArrayBlockingQueue<Frame> getBuffer() {
        return frames;
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            Frame frame = (Frame) arg;

            //remove first element from buffer if overflow
            if(frames.size() >= BUFFER_SIZE) {
                frames.poll();
            }
            frames.put(frame);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
