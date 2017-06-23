package main.java.org.frezy.h264Detector;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by matthias on 23.06.17.
 */
class BashScript extends Script {
    public BashScript(File file, HashMap<String, String> parameters) {
        super(file, parameters);
    }

    @Override
    public void run() {
        try {
            Process process = Runtime.getRuntime().exec(concatParameters(super.file.getAbsolutePath(), super.parameters));

            readOutput(process);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
