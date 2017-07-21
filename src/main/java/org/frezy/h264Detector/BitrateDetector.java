package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Frame;
import main.java.org.frezy.h264.Stream;
import main.java.org.frezy.h264.VideoFrame;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Observable;
import java.util.Observer;

import static main.java.org.frezy.h264.VideoFrame.PictType.*;
import static main.java.org.frezy.h264Detector.Main.DEBUG;

/**
 * Created by matthias on 06.06.17.
 */
public class BitrateDetector extends Detector implements Observer {
    private long startTime = System.nanoTime();

    public BitrateDetector(Stream stream) {
        super(stream);

        stream.addObserver(this);
    }

    //private short detectCounter = 0;
    private boolean movement = false;

    public void detect() {
        if(stableBuffer.size() != STABLEBUFFER_SIZE) return;
        Boolean[] booleans = new Boolean[STABLEBUFFER_SIZE];
        booleans = stableBuffer.toArray(booleans);

        //if booleans < 2

        if(isMovementStart(booleans) && !movement) {//CASE DETECT MOVEMENT
            movement = !movement;
            detected();
        }
        else if(isMovmentEnd(booleans) && movement) {
            movement = !movement;
            detected();
        }
    }

    private boolean isMovementStart(Boolean[] booleans) {
        if(!booleans[booleans.length - 1]) return false;

        for(int i = 0; i <= booleans.length - 2; i++) {
            if(booleans[i]) return false;
        }
        return true;
    }

    private boolean isMovmentEnd(Boolean[] booleans) {
        if(booleans[booleans.length - 1]) return false;

        for(int i = 0; i <= booleans.length - 2; i++) {
            if(!booleans[i]) return false;
        }
        return true;
    }

    public void detected() {
        if(DEBUG)
            System.out.println("MOVEMENT CHANGED! " + movement);
        super.detected(movement);
    }

    @Override
    public void update(Observable o, Object arg) {
        Frame frame = (Frame) arg;

        if(frame instanceof VideoFrame) {
            VideoFrame videoFrame = (VideoFrame) frame;

            refreshFrameBuffer(videoFrame);
            refreshMovingAverage(videoFrame);
            refreshFlatten();
            refreshStable();
            detect();
            if(DEBUG)
                writeToCSV(videoFrame);
        }
    }

    private final int FRAMESIZEBUFFER_SIZE = 240;
    private ArrayDeque<Integer> frameSizeBuffer = new ArrayDeque<Integer>(FRAMESIZEBUFFER_SIZE);

    private void refreshFrameBuffer(VideoFrame videoFrame) {
        if(videoFrame.getPictType() == I) return;

        if(frameSizeBuffer.size() == FRAMESIZEBUFFER_SIZE) frameSizeBuffer.removeLast();
        frameSizeBuffer.addFirst(videoFrame.getPktSize());
    }

    private final int MOVINGAVERAGEBUFFER_SIZE = 240;
    private ArrayDeque<Double> movingAverageBuffer = new ArrayDeque<Double>(MOVINGAVERAGEBUFFER_SIZE);

    private void refreshMovingAverage(VideoFrame videoFrame) {
        if(videoFrame.getPictType() == I) return;

        //refresh actual moving average value
        if(movingAverageBuffer.isEmpty()) movingAverageBuffer.addFirst((double)videoFrame.getPktSize());
        if(movingAverageBuffer.size() == MOVINGAVERAGEBUFFER_SIZE) movingAverageBuffer.removeLast();
        movingAverageBuffer.addFirst(0.001 * videoFrame.getPktSize() + 0.999 * movingAverageBuffer.getFirst());
    }

    private final int FLATTEN_BUFFER_SIZE = 240;
    private final int FLATTEN_SIZE = 50;
    private ArrayDeque<Double> flattenBuffer = new ArrayDeque<Double>(FLATTEN_BUFFER_SIZE);

    private void refreshFlatten() {
        if(flattenBuffer.size() == FLATTEN_BUFFER_SIZE) flattenBuffer.removeLast();

        if(frameSizeBuffer.size() < FLATTEN_SIZE) {
            flattenBuffer.addFirst(-1.0);
            return;
        }

        flattenBuffer.addFirst(frameSizeBuffer.stream().limit(FLATTEN_SIZE).mapToInt(Integer::intValue).average().getAsDouble());
    }

    private final int STABLEBUFFER_SIZE = 8;
    private final int STABLE_SIZE = 60;
    private ArrayDeque<Boolean> stableBuffer = new ArrayDeque<>(STABLEBUFFER_SIZE);

    private void refreshStable() {
        if(stableBuffer.size() == STABLEBUFFER_SIZE) stableBuffer.removeLast();

        if(flattenBuffer.size() < STABLE_SIZE + FLATTEN_SIZE) {
            stableBuffer.addFirst(true);
            return;
        }

        long stable = flattenBuffer.stream().limit(STABLE_SIZE).filter(i -> Math.abs(i - flattenBuffer.getFirst()) <= 500).count();
        stableBuffer.addFirst( stable >= STABLE_SIZE);
        System.out.println(stable);
    }


    //Write to CSV
    FileWriter fileWriter;
    boolean first = true;

    private void writeToCSV(VideoFrame videoFrame) {
        if(frameSizeBuffer.isEmpty() || movingAverageBuffer.isEmpty() || flattenBuffer.isEmpty() || stableBuffer.isEmpty()) return;
        try {
            if (first) {
                fileWriter = new FileWriter("stats.csv");
                fileWriter.write("realtime;pktDtsTime;pktPos;pktSize;codedPictureNumber;movingAverage;flatten;stable\n");
                fileWriter.flush();
                first = false;
            }

            fileWriter.write((System.nanoTime() - startTime) + ";" + videoFrame.getPktDts() + ";" + videoFrame.getPktPos() + ";" +  videoFrame.getPktSize() + ";" + videoFrame.getCodedPictureNumber() + ";" + movingAverageBuffer.getFirst().toString().replace('.', ',') + ";" + flattenBuffer.getFirst().toString().replace(".", ",") + ";" + (stableBuffer.getFirst() ? "5000" : "10000") + "\n");
            fileWriter.flush();

            //System.out.println(frameSizeBuffer.getFirst() + "|" + movingAverageBuffer.getFirst() + "|" + flattenBuffer.getFirst() + "|" + stableBuffer.getFirst());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
