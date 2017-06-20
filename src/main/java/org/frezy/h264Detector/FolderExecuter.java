package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Stream;
import main.java.org.frezy.util.Pair;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by matthias on 06.06.17.
 */
public class FolderExecuter implements Observer {
    final private List<String> supportedExtentions = Arrays.asList("jar", "sh");

    private LinkedList<File> files;
    private Detector detector;

    public FolderExecuter(Detector detector, File folder) {
        this.detector = detector;
        this.detector.addObserver(this);

        files = new LinkedList<File>();
        readDirectory(folder);
    }

    public void readDirectory(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory())
                readDirectory(folder);

            if(supportedExtentions.stream().anyMatch(f -> f.equals(FilenameUtils.getExtension(file.getName()))))
                files.add(file);
        }
    }

    public void executeDirectory() {
        for(File file : files) {
            Executer executer = null;
            if(FilenameUtils.getExtension(file.getName()).equals("jar")) {
                executer = new JavaExecuter(file, this.detector.stream.getInput());
            }

            Thread thread = new Thread(executer);
            thread.start();
        }
    }

    @Override
    public void update(Observable o, Object arg) { //executed if movement detected

        if((boolean)arg) {
            executeDirectory();
        }
    }

    class Executer implements Runnable {
        private File file;
        private String parameter;

        public Executer(File file, String parameter) {
            this.file = file;
            this.parameter = parameter;
        }

        @Override
        public void run() {

        }
    }

    class JavaExecuter extends Executer {
        public JavaExecuter(File file, String parameter) {
            super(file, parameter);
        }

        @Override
        public void run() {
            try {
                Process process = Runtime.getRuntime().exec("java -jar " + super.file.getAbsolutePath() + " " + super.parameter);

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                String string;
                while ((string = stdInput.readLine()) != null) {
                    System.out.println(string);
                }

                while((string = stdError.readLine()) != null) {
                    System.out.println(string);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class BashExecuter extends Executer {
        public BashExecuter(File file, String parameter) { super(file, parameter); }

        @Override
        public void run() {
            try {
                Process process = Runtime.getRuntime().exec(super.file.getAbsolutePath() + " " + super.parameter);

                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                String string;
                while ((string = stdInput.readLine()) != null) {
                    System.out.println(string);
                }

                while((string = stdError.readLine()) != null) {
                    System.out.println(string);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
