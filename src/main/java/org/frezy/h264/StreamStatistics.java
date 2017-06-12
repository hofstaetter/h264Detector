package main.java.org.frezy.h264;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by matthias on 31.05.17.
 */
public class StreamStatistics {
    private Stream stream;

    public StreamStatistics(Stream stream) {
        this.stream = stream;
    }

    public double getAverageBitrate() { //OPTIMIZE THIS
        ArrayBlockingQueue<Frame> arrayBlockingQueue = this.stream.getStreamBuffer().getBuffer();
        if(arrayBlockingQueue.isEmpty()) return -1.0;
        Frame last = arrayBlockingQueue.stream().reduce((a,b) -> b).orElse(null);

        return arrayBlockingQueue.stream()
                .filter(frame -> frame.getPktPtsTime() > last.getPktPtsTime() - 1)
                .filter(frame -> frame.getPktPtsTime() <= last.getPktPtsTime())
                .filter(frame -> frame.getMediaType() == Frame.MediaType.video)
                .mapToDouble(Frame::getPktSize)
                .average().getAsDouble();
    }

    public long getFramesPerSecond() {
        ArrayBlockingQueue<Frame> arrayBlockingQueue = this.stream.getStreamBuffer().getBuffer();
        if(arrayBlockingQueue.isEmpty()) return -1;
        Frame last = arrayBlockingQueue.stream().reduce((a,b) -> b).orElse(null);

        return arrayBlockingQueue.stream()
                .filter(frame -> frame.getPktPtsTime() > last.getPktPtsTime() - 1)
                .filter(frame -> frame.getPktPtsTime() <= last.getPktPtsTime())
                .filter(frame -> frame.getMediaType() == Frame.MediaType.video)
                .count();
    }
}
