package main.java.org.frezy.h264Detector;

import main.java.org.frezy.h264.Stream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.File;

/**
 * Created by matthias on 10.05.17.
 */
public class Main {
    public static String INPUT;
    public static Stream STREAM;
    private static Detector DETECTOR;
    public static double SENSITIVITY = 0.05;
    public static boolean VERBOSE = false;

    public static void main(String[] args) {
        System.out.println("streamdetector | developed by Matthias Hofstätter | Matthias@hofstätter.com (Matthias@xn--hofsttter-z2a.com) | Matthias.Hofstaetter@fau.de");

        if(args.length < 1) {
            System.out.println("USAGE: java -jar streamdetector.jar STREAM_SOURCE [-b] [-fx PATH] [-dbg] [-log]");
            return;
        }
        //read input source
        if(!args[0].contains("rtsp://")) {
            System.out.println("Please specify a rtsp path as first argument!");
            return;
        }
        INPUT = args[0];

        STREAM = new Stream(INPUT);

        try {
            Options options = new Options();
            options.addOption("f", true, "folder to execute");
            options.addOption("v", false, "enable debug mode");
            options.addOption("s", true, "sensitivity");

            CommandLineParser commandLineParser = new DefaultParser();
            CommandLine commandLine = commandLineParser.parse(options, args);

            DETECTOR = new BitrateDetector(STREAM);

            if (commandLine.hasOption("f")) {
                new FolderExecutor(DETECTOR, new File(commandLine.getOptionValue("f")));
                System.out.println(commandLine.getOptionValue("f") + " will be executed on detection.");
            }

            if (commandLine.hasOption("v")) {
                VERBOSE = true;
                System.out.println("VERBOSE activated!");
            }

            if(commandLine.hasOption("s")) {
                if(Double.parseDouble(commandLine.getOptionValue("s")) > 1 || Double.parseDouble(commandLine.getOptionValue("s")) < 0) {
                    System.out.println("Please set sensitivity between 0 and 1.");
                    return;
                }
                SENSITIVITY = Double.parseDouble(commandLine.getOptionValue("s"));
                System.out.println("Sensitivity set to " + commandLine.getOptionValue("s"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        STREAM.open();
    }
}
