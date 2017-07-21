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
public class FolderExecutor extends Executor {
    private ArrayList<Executor> executors;

    public FolderExecutor(Detector detector, File folder) {
        this.file = folder;

        this.executors = new ArrayList<Executor>();

        for(File file : folder.listFiles()) {
            if(file.isDirectory()) executors.add(new FolderExecutor(detector, folder));

            executors.add(new FileExecutor(detector, file));
        }
    }
}
