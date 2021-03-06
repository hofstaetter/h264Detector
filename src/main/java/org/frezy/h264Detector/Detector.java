package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Stream;
import main.java.org.frezy.util.Pair;

import java.util.Observable;

/**
 * Created by matthias on 15.05.17.
 */
public class Detector extends Observable {
    protected Stream stream;

    protected boolean state = false;

    public Detector(Stream stream) {
        this.stream = stream;
    }

    protected void switchState() {
        System.out.println("State changed: " + state);
        this.setChanged();
        this.notifyObservers(state);
        this.state = !state;
    }
}