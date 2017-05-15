package main.java.org.frezy.h264;

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
    protected Duration pktPtsTime;
    protected long pktDts;
    protected Duration pktDtsTime;
    protected long bestEffortTimestamp;
    protected Duration bestEffortTimestampTime;
    protected long pktDuration;
    protected Duration pktDurationTime;
    protected long pktPos;
    protected int pktSize;

    public Frame(String[] strings) {
        this.mediaType = MediaType.valueOf(strings[1].split("=")[1]);
        this.streamIndex = Integer.parseInt(strings[2].split("=")[1]);
        this.keyFrame = Boolean.parseBoolean(strings[3].split("=")[1]);
        this.pktPts = Long.parseLong(strings[4].split("=")[1]);
        //
        if(!strings[6].split("=")[1].equals("N/A"))
            this.pktDts = Long.parseLong(strings[6].split("=")[1]);
        //
        this.bestEffortTimestamp = Long.parseLong(strings[8].split("=")[1]);
        //
        if(!strings[11].split("=")[1].equals("N/A"))
            this.pktDuration = Long.parseLong(strings[10].split("=")[1]);
        //
        if(!strings[12].split("=")[1].equals("N/A"))
            this.pktPos = Long.parseLong(strings[12].split("=")[1]);
        this.pktSize = Integer.parseInt(strings[13].split("=")[1]);
    }

    public String toString() {
        return "FRAME:\n" +
                "   media_type: " + getMediaType() + "\n" +
                "   stream_index: " + getStreamIndex() + "\n" +
                "   key_frame: " + isKeyFrame() + "\n" +
                "   pkt_pts: " + getPktPts() + "\n" +
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

    public Duration getPktPtsTime() {
        return pktPtsTime;
    }

    public void setPktPtsTime(Duration pktPtsTime) {
        this.pktPtsTime = pktPtsTime;
    }

    public long getPktDts() {
        return pktDts;
    }

    public void setPktDts(long pktDts) {
        this.pktDts = pktDts;
    }

    public Duration getPktDtsTime() {
        return pktDtsTime;
    }

    public void setPktDtsTime(Duration pktDtsTime) {
        this.pktDtsTime = pktDtsTime;
    }

    public long getBestEffortTimestamp() {
        return bestEffortTimestamp;
    }

    public void setBestEffortTimestamp(long bestEffortTimestamp) {
        this.bestEffortTimestamp = bestEffortTimestamp;
    }

    public Duration getBestEffortTimestampTime() {
        return bestEffortTimestampTime;
    }

    public void setBestEffortTimestampTime(Duration bestEffortTimestampTime) {
        this.bestEffortTimestampTime = bestEffortTimestampTime;
    }

    public long getPktDuration() {
        return pktDuration;
    }

    public void setPktDuration(long pktDuration) {
        this.pktDuration = pktDuration;
    }

    public Duration getPktDurationTime() {
        return pktDurationTime;
    }

    public void setPktDurationTime(Duration pktDurationTime) {
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