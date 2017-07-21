package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Stream;
import main.java.org.frezy.h264.StreamStatistics;
import main.java.org.frezy.h264Detector.BitrateDetector;
import main.java.org.frezy.h264Detector.Detector;
import main.java.org.frezy.h264Detector.FolderExecutor;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by matthias on 10.05.17.
 */
public class Main {
    public static String INPUT;
    public static Stream STREAM;
    public static Detector DETECTOR;
    public static boolean DEBUG = false;

    public static void main(String[] args) {
        //main.java.org.frezy.h264Inspector.Graph.main(args);

        System.out.println("streamwatch | developed by Matthias Hofstätter | Matthias@hofstätter.com (Matthias@xn--hofsttter-z2a.com) | Matthias.Hofstaetter@fau.de");

        if(args.length < 1) {
            System.out.println("USAGE: java -jar streamwatch.jar STREAM_SOURCE [-b] [-fx PATH] [-dbg]");
            return;
        }
        //read input source
        INPUT = args[0];

        STREAM = new Stream(INPUT);
        STREAM.open();

        for(int i = 1; i < args.length; i++) {
            if(args[i].equals("-b")) {
                DETECTOR = new BitrateDetector(STREAM);
                System.out.println("Bitrate detector activated!");
            } else if(args[i].equals("-fx")) {
                i++;
                new FolderExecutor(DETECTOR, new File(args[i]));
                System.out.println(args[i] + " will be executed on detection.");
            } else if(args[i].equals("-ss")) {
                new StreamStatistics(STREAM);
            } else if(args[i].equals("-dbg")) {
                DEBUG = true;
                System.out.println("DEBUG activated!");
            }
        }
    }
}
