package main.java.org.frezy.h264;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by matthias on 05.05.17.
 */

//DO NOT USE

public class Media {
    public LinkedList<Frame> frames;
    private String input;

    public Media(String input) {
        this.frames = new <Frame>LinkedList();
        this.input = input;
        readFrames();
    }

    public void readFramesAsync(String input) {

    }



    public void readFrames() {
        try {
            Process process = Runtime.getRuntime().exec("./ffprobe " + this.input + " -show_frames");

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            //BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            System.out.println("Starting worker...");

            String string;
            String[] framestring = new String[25];
            int count = 0;
            Frame.MediaType mediaType;
            while ((string = stdInput.readLine()) != null) {
                //read full frame string
                System.out.println("READ: " + string);
                framestring[count] = string;
                count++;
                if (string.equals("[/FRAME]")) { //frame end
                    //insert frame into buffer
                    Frame frame;
                    if (framestring[1].split("=")[1].equals("video")) {
                        frame = new VideoFrame(framestring);
                    } else {
                        frame = new AudioFrame(framestring);
                    }
                    //frames.put(frame);

                    //remove first element from buffer if overflow
                    /*if(frames.size() >= BUFFER_SIZE) {
                        frames.poll();
                    }*/

                    //update statistics
                    //updateStatistics(frame);

                    //reset variables
                    count = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Pair<key,value> {

        private final key left;
        private final value right;

        public Pair(key left, value right) {
            this.left = left;
            this.right = right;
        }

        public key getLeft() { return left; }
        public value getRight() { return right; }

        @Override
        public int hashCode() { return left.hashCode() ^ right.hashCode(); }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair)) return false;
            Pair pair = (Pair) o;
            return this.left.equals(pair.getLeft()) &&
                    this.right.equals(pair.getRight());
        }

    }
}
