package main.java.org.frezy.h264;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.time.Duration;

/**
 * Created by matthias on 05.05.17.
 */
public class Frame {
    public enum MediaType {
        video, audio
    }

    protected MediaType mediaType;
    protected int streamIndex;
    protected boolean keyFrame;
    protected long pktPts;
    protected double pktPtsTime;
    protected long pktDts;
    protected double pktDtsTime;
    protected long bestEffortTimestamp;
    protected double bestEffortTimestampTime;
    protected long pktDuration;
    protected double pktDurationTime;
    protected long pktPos;
    protected int pktSize;

    public Frame(String[] strings) {
        String key;
        String[] pair = new String[2];
        for(String string : strings) {
            if(string.equals("[FRAME]")) continue;
            if(string.equals("[/FRAME]")) return;

            //key = pair[0], value = pair[1]
            pair = string.split("=");

            if(pair[1].equals("N/A")) continue;

            if(pair[0].equals("media_type"))
                this.mediaType = MediaType.valueOf(pair[1]);

            //else if(pair[0].equals("stream_index"))
                //this.streamIndex = Integer.parseInt(pair[1]);

            else if(pair[0].equals("key_frame"))
                this.keyFrame = Boolean.parseBoolean(pair[1]);

            else if(pair[0].equals("pkt_pts"))
                this.pktPts = Long.parseLong(pair[1]);

            else if(pair[0].equals("pkt_pts_time")) //TODO performance
                this.pktPtsTime = Double.parseDouble(pair[1]);

            //else if(pair[0].equals("pkt_dts"))
                //this.pktDts = Long.parseLong(pair[1]);

            //else if(pair[0].equals("pkt_dts_time")) //TODO performance
                //this.pktDtsTime = Double.parseDouble(pair[1]);

            //else if(pair[0].equals("best_effort_timestamp"))
                //this.bestEffortTimestamp = Long.parseLong(pair[1]);

            //else if(pair[0].equals("best_effort_timestamp_time")) //TODO performance
                //this.bestEffortTimestampTime = Double.parseDouble(pair[1]);

            //else if(pair[0].equals("pkt_duration"))
                //this.pktDuration = Long.parseLong(pair[1]);

            //else if(pair[0].equals("pkt_duration_time")) //TODO performance
                //this.pktDurationTime = Double.parseDouble(pair[1]);

            //else if(pair[0].equals("pkt_pos"))
                //this.pktPos = Long.parseLong(pair[1]);

            else if(pair[0].equals("pkt_size"))
                this.pktSize = Integer.parseInt(pair[1]);
        }
    }

    public String toString() {
        return "FRAME:\n" +
                "   media_type: " + getMediaType() + "\n" +
                "   stream_index: " + getStreamIndex() + "\n" +
                "   key_frame: " + isKeyFrame() + "\n" +
                "   pkt_pts: " + getPktPts() + "\n" +
                "   pkt_pts_time: " + getPktPtsTime() + "\n" +
                "   pkt_dts: " + getPktDts() + "\n" +
                "   best_effort_timestamp: " + getBestEffortTimestamp() + "\n" +
                "   pkt_duration: " + getPktDuration() + "\n" +
                "   pkt_pos: " + getPktPos() + "\n" +
                "   pkt_size: " + getPktSize();
    }

    public Frame(String string) {
        this.mediaType = null;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public int getStreamIndex() {
        return streamIndex;
    }

    public void setStreamIndex(int streamIndex) {
        this.streamIndex = streamIndex;
    }

    public boolean isKeyFrame() {
        return keyFrame;
    }

    public void setKeyFrame(boolean keyFrame) {
        this.keyFrame = keyFrame;
    }

    public long getPktPts() {
        return pktPts;
    }

    public void setPktPts(long pktPts) {
        this.pktPts = pktPts;
    }

    public double getPktPtsTime() {
        return pktPtsTime;
    }

    public void setPktPtsTime(double pktPtsTime) {
        this.pktPtsTime = pktPtsTime;
    }

    public long getPktDts() {
        return pktDts;
    }

    public void setPktDts(long pktDts) {
        this.pktDts = pktDts;
    }

    public double getPktDtsTime() {
        return pktDtsTime;
    }

    public void setPktDtsTime(double pktDtsTime) {
        this.pktDtsTime = pktDtsTime;
    }

    public long getBestEffortTimestamp() {
        return bestEffortTimestamp;
    }

    public void setBestEffortTimestamp(long bestEffortTimestamp) {
        this.bestEffortTimestamp = bestEffortTimestamp;
    }

    public double getBestEffortTimestampTime() {
        return bestEffortTimestampTime;
    }

    public void setBestEffortTimestampTime(double bestEffortTimestampTime) {
        this.bestEffortTimestampTime = bestEffortTimestampTime;
    }

    public long getPktDuration() {
        return pktDuration;
    }

    public void setPktDuration(long pktDuration) {
        this.pktDuration = pktDuration;
    }

    public double getPktDurationTime() {
        return pktDurationTime;
    }

    public void setPktDurationTime(double pktDurationTime) {
        this.pktDurationTime = pktDurationTime;
    }

    public long getPktPos() {
        return pktPos;
    }

    public void setPktPos(long pktPos) {
        this.pktPos = pktPos;
    }

    public int getPktSize() {
        return pktSize;
    }

    public void setPktSize(int pktSize) {
        this.pktSize = pktSize;
    }
}