package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Frame;
import main.java.org.frezy.h264.Stream;
import main.java.org.frezy.h264.VideoFrame;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.DoubleStream;

import static main.java.org.frezy.h264.VideoFrame.PictType.*;

/**
 * Created by matthias on 06.06.17.
 */
public class BitrateDetector extends Detector implements Observer {
    private long startTime = System.nanoTime();
    private MovingAverage movingAverage;
    private BinomialMovingAverage binomialMovingAverage;
    private ExponentionalMovingAverage exponentionalMovingAverage;
    private Flatten flatten;
    private boolean movement = false;

    //test
    private LinkedList<Double> differences;

    public BitrateDetector(Stream stream) {
        super(stream);

        movingAverage = new MovingAverage(this.stream);
        binomialMovingAverage = new BinomialMovingAverage(this.stream);
        exponentionalMovingAverage = new ExponentionalMovingAverage(this.stream);
        flatten = new Flatten(this.stream);

        stream.addObserver(this);

        //test
        differences = new LinkedList<Double>();
    }

    public void detect() {
        if(movingAverage.buffer.isEmpty()) return;

        //flat twice?
        double avg = movingAverage.buffer.stream().limit(49).mapToDouble(Double::doubleValue).sum() / 49;
        double diff = movingAverage.buffer.stream().limit(49).mapToDouble(Double::doubleValue).map(d -> Math.abs(d - avg)).sum() / 49;

        //test
        differences.add(diff);

        System.out.println(diff);

        /*if(flatten.buffer.isEmpty()) return;

        if(flatten.buffer.stream().limit(50).filter(f -> Math.abs(flatten.buffer.getFirst() - f) < 500).count() >= 50) {
            if(movement) {
                movement = !movement;
                System.out.println("FALSE");
            }
        } else {
            if(!movement) {
                movement = !movement;
                System.out.println("TRUE");
            }
        }*/

        /*if(exponentionalMovingAverage.buffer.size() < 50) return;
        double avg = exponentionalMovingAverage.buffer.stream().limit(50).mapToDouble(Double::doubleValue).average().getAsDouble();
        double diff = exponentionalMovingAverage.buffer.stream().limit(50).mapToDouble(Double::doubleValue).map(d -> Math.abs(d - avg)).sum() / 50;
        //System.out.println("DIFF: " + diff);

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
        }*/
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

            if(!flatten.buffer.isEmpty())
            //System.out.println("SIZE: " + videoFrame.getPktSize() + " FLATTEN: " + flatten.buffer.getFirst());
            detect();
            writeToCSV(videoFrame);
            /*if(VERBOSE)
                writeToConsole(videoFrame);*/
        }
    }


    private void writeToConsole(VideoFrame videoFrame) {
        if(videoFrame.getPictType() == VideoFrame.PictType.P)
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + ": " + videoFrame.getPktPts() + " | " + videoFrame.getPktSize());
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

    private void writeToCSV(VideoFrame videoFrame) {
        if(videoFrame.getPictType() == I) return;
        if(this.stream.getBuffer().isEmpty()) return;

        if(flatten == null) return;
        if(flatten.buffer.isEmpty()) return;

        try {
            if(fileWriter == null)
                fileWriter = new FileWriter("stats.csv");

            fileWriter.write((System.nanoTime() - startTime) + ";" + videoFrame.getPktPts() + ";" +  videoFrame.getPktSize() + ";" + movingAverage.buffer.getFirst().toString().replace(".", ",") + ";" + flatten.buffer.getFirst().toString().replace(".", ",") + ";" + binomialMovingAverage.buffer.getFirst().toString().replace(".",",") + ";"
                    + exponentionalMovingAverage.buffer.getFirst().toString().replace(".", ",") + ";" + ((movement) ? "10000" : "5000") + ";" + differences.get(differences.size()-1).toString().replace(".", ",") +  "\n");
            fileWriter.flush();

            //System.out.println(frameSizeBuffer.getFirst() + "|" + movingAverageBuffer.getFirst() + "|" + flattenBuffer.getFirst() + "|" + stableBuffer.getFirst());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
