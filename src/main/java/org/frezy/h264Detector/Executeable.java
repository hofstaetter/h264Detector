package main.java.org.frezy.h264Detector;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matthias on 23.06.17.
 */
abstract class Executeable implements Runnable {
    static List<String> SUPPORTED_EXTENSIONS = Arrays.asList("jar", "sh");

    protected File file;
    protected String extension;
    protected HashMap<String, String> parameters;


    public Executeable(File file, HashMap<String, String> parameters) {
        this.file = file;
        this.extension = FilenameUtils.getExtension(file.getName());
        this.parameters = parameters;
    }

    public static Executeable getScriptClass(File file, HashMap<String, String> parameters) {
        String extension = FilenameUtils.getExtension(file.getName());

        if(!SUPPORTED_EXTENSIONS.stream().anyMatch(f -> f.equals(extension))) return null;

        if(extension.equals("jar")) {
            return new JavaExecuteable(file, parameters);
        } else if(extension.equals("sh")) {
            return new BashExecuteable(file, parameters);
        }
        return null;
    }

    @Override
    public void run() {
        System.out.println("DEFAULT APP");
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
