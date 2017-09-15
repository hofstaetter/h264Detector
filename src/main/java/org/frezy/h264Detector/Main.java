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
    public static boolean VERBOSE = false;

    public static void main(String[] args) {
        System.out.println("streamdetector | developed by Matthias Hofstätter | Matthias@hofstätter.com (Matthias@xn--hofsttter-z2a.com) | Matthias.Hofstaetter@fau.de");

        if(args.length < 1) {
            System.out.println("USAGE: java -jar streamdetector.jar STREAM_SOURCE [-b] [-fx PATH] [-dbg] [-log]");
            return;
        }
        //read input source
        INPUT = args[0];

        STREAM = new Stream(INPUT);

        try {
            Options options = new Options();
            options.addOption("b", false, "activate Bitrate detector");
            options.addOption("f", true, "folder to execute");
            options.addOption("v", false, "enable debug mode");

            CommandLineParser commandLineParser = new DefaultParser();
            CommandLine commandLine = commandLineParser.parse(options, args);

            if (commandLine.hasOption("b")) {
                DETECTOR = new BitrateDetector(STREAM);
                System.out.println("Bitrate detector activated!");
            }

            if (commandLine.hasOption("f")) {
                new FolderExecutor(DETECTOR, new File(commandLine.getOptionValue("f")));
                System.out.println(commandLine.getOptionValue("f") + " will be executed on detection.");
            }

            if (commandLine.hasOption("v")) {
                VERBOSE = true;
                System.out.println("VERBOSE activated!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        STREAM.open();
    }
}
