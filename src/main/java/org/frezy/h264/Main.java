package main.java.org.frezy.h264;

import main.java.org.frezy.h264Detector.BitrateDetector;
import main.java.org.frezy.h264Detector.Detector;
import main.java.org.frezy.h264Detector.FolderExecuter;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by matthias on 10.05.17.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("h.264 Inspector | developed by Matthias Hofstätter | Matthias@hofstätter.com (Matthias@xn--hofsttter-z2a.com)");

        System.out.print("Please give me some input: ");

        //read input source
        String input = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            input = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Media media = new Media(input);
        if(input.isEmpty()) { //REMOVE THIS
            input = "rtsp://admin:uni2017$@172.16.173.210:554/rtsp_live0";
        }

        Stream stream = new Stream(input);
        stream.open();

        BitrateDetector bitrateDetector = new BitrateDetector(stream);
        StreamStatistics streamStatistics = new StreamStatistics(stream);
        FolderExecuter folderExecuter = new FolderExecuter(bitrateDetector, new File("./execute"));

        Long lastFramesCount = 0L;
        while(true) {
            //System.out.println("State: " + stream.getThreadState());
            //System.out.println("Readed Frames: " + stream.getReadedFramesCount());
            //System.out.println("Average Package Size: " + stream.getAveragePackageSize());
            //System.out.println("FPS: " + streamStatistics.getFramesPerSecond());
            //lastFramesCount = stream.getReadedFramesCount();
            //System.out.println("AvgBitrate: " + streamStatistics.getAverageBitrate());

            //System.out.print(String.format("\033[%dA",3));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
