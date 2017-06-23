package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Stream;
import main.java.org.frezy.util.Pair;

import java.util.Observable;

/**
 * Created by matthias on 15.05.17.
 */
public class Detector extends Observable {
    protected Stream stream;

    protected boolean state;

    public Detector(Stream stream) {
        this.stream = stream;
    }

    protected void detected(boolean state) {
        this.setChanged();
        this.notifyObservers(state);
        this.state = state;
    }
}