package main.java.org.frezy.h264Detector;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.*;

/**
 * Created by matthias on 23.06.17.
 */
public class FileExecutor extends Executor implements Observer {
    private Detector detector;

    public FileExecutor(Detector detector, File folder) {
        this.detector = detector;
        this.detector.addObserver(this);

        this.file = folder;
    }

    @Override
    public void update(Observable o, Object arg) { //executed if movement detected
        if((boolean)arg) {
            if(Script.isFileSupported(this.file)) {
                Script executer = null;
                String extension = FilenameUtils.getExtension(file.getName());
                if (extension.equals("jar")) {
                    HashMap<String, String> parameters = new HashMap<String, String>();
                    parameters.put("input", this.detector.stream.getInput());

                    executer = new JavaScript(file, parameters);
                } else if(extension.equals("sh")) {
                    HashMap<String, String> parameters = new HashMap<String, String>();
                    parameters.put("input", this.detector.stream.getInput());

                    executer = new BashScript(file, parameters);
                }

                Thread thread = new Thread(executer);
                thread.start();
            }
        }
    }
}
