package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Frame;
import main.java.org.frezy.h264.Stream;
import main.java.org.frezy.h264.VideoFrame;

import java.util.ArrayDeque;
import java.util.Observable;
import java.util.Observer;

import static main.java.org.frezy.h264.Stream.BUFFER_SIZE;
import static main.java.org.frezy.h264.VideoFrame.PictType.I;

public class MovingAverage implements Observer {
    private Stream stream;

    public ArrayDeque<Double> buffer;
    private long[] tempBuffer;

    private long[] binomialCoefficients;
    private long sumBinomialCoefficients;

    public MovingAverage(Stream stream) {
        this.stream = stream;
        this.tempBuffer = new long[16];
        this.buffer = new ArrayDeque<>(100);

        this.stream.addObserver(this);

        this.fillBuffer();
        this._count = this.tempBuffer.length - 1;

        this.calcBinomialCoefficents();
    }

    public MovingAverage(Stream stream, int size) {
        this.stream = stream;
        this.tempBuffer = new long[size];
        this.buffer = new ArrayDeque<>(100);

        this.stream.addObserver(this);

        this.fillBuffer();
        this._count = this.tempBuffer.length - 1;

        this.calcBinomialCoefficents();
    }

    public MovingAverage(Stream stream, int size, int bufferSize) {
        this.stream = stream;
        this.tempBuffer = new long[size];
        this.buffer = new ArrayDeque<>(bufferSize);

        this.stream.addObserver(this);

        this.fillBuffer();
        this._count = this.tempBuffer.length - 1;

        this.calcBinomialCoefficents();
    }

    private void fillBuffer() {
        for(int i = 0; i < this.tempBuffer.length / 2; i++)
            this.buffer.addFirst(-1d);
    }

    private void calcBinomialCoefficents() {
        this.binomialCoefficients = new long[this.tempBuffer.length];

        for(int i = 0; i < this.binomialCoefficients.length; i++) {
            this.binomialCoefficients[i] = binomialCoefficient(this.binomialCoefficients.length - 1, i);
            this.sumBinomialCoefficients += this.binomialCoefficients[i];
        }
    }

    public double getLastValue() {
        return this.buffer.getFirst();
    }

    private int _count;
    @Override
    public void update(Observable o, Object arg) {
        Frame frame = (Frame) arg;

        if(frame instanceof VideoFrame) {
            VideoFrame videoFrame = (VideoFrame) frame;
            if (videoFrame.getPictType() == I) return;

            //fill buffer
            if(this.stream.getBuffer().size() <= this.tempBuffer.length) {
                this.tempBuffer[_count--] = videoFrame.getPktSize();
                return;
            }

            //refresh temp buffer
            for(int i = this.tempBuffer.length - 2; i >= 0; i--) {
                this.tempBuffer[i + 1] = this.tempBuffer[i];
            }
            this.tempBuffer[0] = videoFrame.getPktSize();

            //calc new ma value
            double value = 0;
            int sub = 0;
            for(int i = 0; i < this.tempBuffer.length; i++) {
                value += binomialCoefficients[i] * this.tempBuffer[i];
            }
            this.buffer.addFirst(value / sumBinomialCoefficients);
        }
    }

    static long binomialCoefficient(int n, int k) {
        if ((n == k) || (k == 0))
            return 1;
        else
            return binomialCoefficient(n - 1, k) + binomialCoefficient(n - 1, k - 1);
    }
}
