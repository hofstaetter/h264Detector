package main.java.org.frezy.h264Detector;

import java.io.File;
import java.util.*;

import static main.java.org.frezy.h264Detector.Main.INPUT;

/**
 * Created by matthias on 23.06.17.
 */
public class FileExecutor extends Executor implements Observer {
    private Detector detector;

    public FileExecutor(Detector detector, File file) {
        this.detector = detector;
        this.detector.addObserver(this);


        this.file = file;
    }

    @Override
    public void update(Observable o, Object arg) { //executed if movement detected
        if((boolean)arg) {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("input", INPUT);

            Executeable executeable = Executeable.getScriptClass(this.file, parameters);
            Thread thread = new Thread(executeable);
            thread.start();
        }
    }
}
