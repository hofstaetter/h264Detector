package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Frame;
import main.java.org.frezy.h264.Stream;
import main.java.org.frezy.h264.VideoFrame;

import java.io.DataOutput;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import static main.java.org.frezy.h264.Stream.BUFFER_SIZE;
import static main.java.org.frezy.h264.VideoFrame.PictType.*;
import static main.java.org.frezy.h264Detector.Main.DEBUG;
import static main.java.org.frezy.h264Detector.Main.LOG;

/**
 * Created by matthias on 06.06.17.
 */
public class BitrateDetector extends Detector implements Observer {
    private long startTime = System.nanoTime();
    private MovingAverage movingAverage;
    private ExponentionalMovingAverage exponentionalMovingAverage;
    private Flatten flatten;
    private boolean movement = false;

    public BitrateDetector(Stream stream) {
        super(stream);

        movingAverage = new MovingAverage(this.stream);
        exponentionalMovingAverage = new ExponentionalMovingAverage(this.stream);
        flatten = new Flatten(this.stream);

        stream.addObserver(this);
    }

    public void detect() {
        if(exponentionalMovingAverage.buffer.size() < 50) return;
        double avg = exponentionalMovingAverage.buffer.stream().limit(50).mapToDouble(Double::doubleValue).average().getAsDouble();
        double diff = exponentionalMovingAverage.buffer.stream().limit(50).mapToDouble(Double::doubleValue).map(d -> Math.abs(d - avg)).sum() / 50;
        System.out.println("DIFF: " + diff);

        if(diff > (avg * 0.05)) {
            if (!movement) {
                movement = !movement;
                detected();
            }
        } else {
            if(movement) {
                movement = !movement;
                detected();
            }
        }

    }

    public void detected() {
        System.out.println("MOVEMENT CHANGED! " + movement);
        super.detected(movement);
    }

    @Override
    public void update(Observable o, Object arg) {
        Frame frame = (Frame) arg;

        if(frame instanceof VideoFrame) {
            VideoFrame videoFrame = (VideoFrame) frame;

            //refreshFlatten();
            //refreshStable();
            System.out.print("SIZE: " + videoFrame.getPktSize() + " ");
            detect();
            writeToCSV(videoFrame);
            /*if(DEBUG)
                writeToConsole(videoFrame);*/
        }
    }


    private void writeToConsole(VideoFrame videoFrame) {
        if(videoFrame.getPictType() == VideoFrame.PictType.P)
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + ": " + videoFrame.getPktPts() + " | " + videoFrame.getPktSize() + " | " + exponentionalMovingAverage.buffer.getFirst().toString().replace('.', ','));
    }

    /*private ArrayDeque<Integer> frameSizeBuffer = new ArrayDeque<Integer>(BUFFER_SIZE);

    private void refreshFrameBuffer(VideoFrame videoFrame) {
        if(videoFrame.getPictType() == I) return;

        if(frameSizeBuffer.size() == BUFFER_SIZE) frameSizeBuffer.removeLast();
        frameSizeBuffer.addFirst(videoFrame.getPktSize());
    }*/

    /*private final int FLATTEN_SIZE = 25;
    private ArrayDeque<Double> flattenBuffer = new ArrayDeque<Double>(BUFFER_SIZE);

    private void refreshFlatten() {
        if(FLATTEN_SIZE >= BUFFER_SIZE) {
            System.out.println("BUFFER_SIZE have to be larger than FLATTEN_SIZE");
            System.exit(0);
        }

        if(flattenBuffer.size() == BUFFER_SIZE) flattenBuffer.removeLast();

        if(this.stream.getBuffer().size() < FLATTEN_SIZE) {
            flattenBuffer.addFirst(-1.0);
            return;
        }

        flattenBuffer.addFirst(this.stream.getBuffer().stream().filter(f -> f instanceof VideoFrame).map(f -> (VideoFrame)f).filter(vf -> vf.getPictType() == VideoFrame.PictType.P).limit(FLATTEN_SIZE).mapToInt(Frame::getPktSize).average().getAsDouble());
    }

    private final int STABLEBUFFER_SIZE = 6;
    private final int STABLE_SIZE = 25;
    private ArrayDeque<Boolean> stableBuffer = new ArrayDeque<>(STABLEBUFFER_SIZE);

    private void refreshStable() {
        if(stableBuffer.size() == STABLEBUFFER_SIZE) stableBuffer.removeLast();

        if(flattenBuffer.size() < STABLE_SIZE + FLATTEN_SIZE) {
            stableBuffer.addFirst(true);
            return;
        }

        long stable = flattenBuffer.stream().limit(STABLE_SIZE).filter(i -> Math.abs(i - flattenBuffer.getFirst()) <= 500).count();
        stableBuffer.addFirst( stable >= STABLE_SIZE);

*/
    //Write to CSV
    FileWriter fileWriter;
    boolean first = true;

    private void writeToCSV(VideoFrame videoFrame) {
        if(videoFrame.getPictType() == I) return;
        if(this.stream.getBuffer().isEmpty()) return;

        if(exponentionalMovingAverage == null || movingAverage == null || flatten == null) return;
        if(exponentionalMovingAverage.buffer.isEmpty() || movingAverage.buffer.isEmpty() || flatten.buffer.isEmpty()) return;

        try {
            if (first) {
                fileWriter = new FileWriter("stats.csv");
                fileWriter.write("realtime;pktDtsTime;pktSize;codedPictureNumber;movingAverage\n");
                fileWriter.flush();
                first = false;
            }

            fileWriter.write((System.nanoTime() - startTime) + ";" + videoFrame.getPktPts() + ";" +  videoFrame.getPktSize() + ";" + videoFrame.getCodedPictureNumber() + ";" + exponentionalMovingAverage.buffer.getFirst().toString().replace(".", ",") + ";" + movingAverage.buffer.getFirst().toString().replace(".", ",") + ";" + flatten.buffer.getFirst().toString().replace(".", ",") + "\n");
            fileWriter.flush();

            //System.out.println(frameSizeBuffer.getFirst() + "|" + movingAverageBuffer.getFirst() + "|" + flattenBuffer.getFirst() + "|" + stableBuffer.getFirst());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
