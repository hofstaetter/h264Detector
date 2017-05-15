package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Stream;

/**
 * Created by matthias on 15.05.17.
 */
public class Detector {
    private Stream stream;
    private boolean ready;

    public Detector(Stream stream) {
        this.stream = stream;
    }

    public boolean isMovmentDetected() {
        if(!ready) return false;
        //TODO dynamic
        for(double averageBitrate : stream.getAveragePackageSizes()) {
            if(averageBitrate < 10000) return false;
        }
        return true;
    }
}
