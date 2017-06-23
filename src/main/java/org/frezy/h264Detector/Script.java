package main.java.org.frezy.h264Detector;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matthias on 23.06.17.
 */
abstract class Script implements Runnable {
    static List<String> SUPPORTED_EXTENSIONS = Arrays.asList("jar", "sh");

    protected File file;
    protected HashMap<String, String> parameters;

    public Script(File file, HashMap<String, String> parameters) {
        this.file = file;
        this.parameters = parameters;
    }

    public static boolean isFileSupported(File file) {
        if(SUPPORTED_EXTENSIONS.stream().anyMatch(f -> f.equals(FilenameUtils.getExtension(file.getName())))) return true;
        else return false;
    }

    //assist
    protected String concatParameters(String string, HashMap<String, String> parameters) {
        StringBuilder stringBuilder = new StringBuilder(string);
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            stringBuilder.append(" " + entry.getKey() + "=" + entry.getValue());
        }
        return stringBuilder.toString();
    }

    protected void readOutput(Process process) throws IOException {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String string;
        while ((string = stdInput.readLine()) != null) {
            System.out.println(string);
        }

        while((string = stdError.readLine()) != null) {
            System.out.println(string);
        }
    }
}
