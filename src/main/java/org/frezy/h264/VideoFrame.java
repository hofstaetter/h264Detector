package main.java.org.frezy.h264;

import java.time.Duration;

/**
 * Created by matthias on 08.05.17.
 */
public class VideoFrame extends Frame {
    public enum PictType {
        I, P, B
    }

    public enum PixFmt {
        yuv420p, yuyv422, rgb24, bgr24, yuv422p, yuv444p, rgb32, yuv410p, yuvj420p
    }

    private int width;
    private int height;
    private PixFmt pixFmt;
    private AspectRatio sampleAspectRatio;
    private PictType pictType;
    private long codedPictureNumber;
    private int displayPictureNumber;
    private int interlacedFrame;
    private int topFieldFirst;
    private int repeatPict;

    public VideoFrame(String[] strings) {
        super(strings);

        String key;
        String[] pair = new String[2];
        for(String string : strings) {
            if(string.equals("[FRAME]")) continue;
            if(string.equals("[/FRAME]")) return;

            //key = pair[0], value = pair[1]
            pair = string.split("=");

            if(pair[1].equals("N/A")) continue;

            if(pair[0].equals("width"))
                this.width = Integer.parseInt(pair[1]);

            else if(pair[0].equals("pix_fmt"))
                this.pixFmt = PixFmt.valueOf(pair[1]);

            else if(pair[0].equals("sample_aspect_ratio"))
                this.sampleAspectRatio = new AspectRatio(pair[1]);

            else if(pair[0].equals("pict_type"))
                this.pictType = PictType.valueOf(pair[1]);

            else if(pair[0].equals("coded_picture_number"))
                this.codedPictureNumber = Long.parseLong(pair[1]);

            else if(pair[0].equals("display_picture_number"))
                this.displayPictureNumber = Integer.parseInt(pair[1]);

            else if(pair[0].equals("interlaced_frame"))
                this.interlacedFrame = Integer.parseInt(pair[1]);

            else if(pair[0].equals("top_field_first"))
                this.topFieldFirst = Integer.parseInt(pair[1]);

            else if(pair[0].equals("repeat_pict"))
                this.repeatPict = Integer.parseInt(pair[1]);
        }
    }

    public String toString() {
        return super.toString() + "\n" +
                "   width: " + getWidth() + "\n" +
                "   height: " + getHeight() + "\n" +
                "   pix_fmt: " + getPixFmt() + "\n" +
                "   sample_aspect_ratio: " + getSampleAspectRatio() + "\n" +
                "   pict_type: " + getPictType() + "\n" +
                "   coded_picture_number: " + getCodedPictureNumber() + "\n" +
                "   display_picture_number: " + getDisplayPictureNumber() + "\n" +
                "   interlaced_frame: " + getInterlacedFrame() + "\n" +
                "   top_field_first: " + getTopFieldFirst() + "\n" +
                "   repeat_pict: " + getRepeatPict();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public PixFmt getPixFmt() {
        return pixFmt;
    }

    public void setPixFmt(PixFmt pixFmt) {
        this.pixFmt = pixFmt;
    }

    public AspectRatio getSampleAspectRatio() {
        return sampleAspectRatio;
    }

    public void setSampleAspectRatio(AspectRatio sampleAspectRatio) {
        this.sampleAspectRatio = sampleAspectRatio;
    }

    public PictType getPictType() {
        return pictType;
    }

    public void setPictType(PictType pictType) {
        this.pictType = pictType;
    }

    public long getCodedPictureNumber() {
        return codedPictureNumber;
    }

    public void setCodedPictureNumber(long codedPictureNumber) {
        this.codedPictureNumber = codedPictureNumber;
    }

    public int getDisplayPictureNumber() {
        return displayPictureNumber;
    }

    public void setDisplayPictureNumber(int displayPictureNumber) {
        this.displayPictureNumber = displayPictureNumber;
    }

    public int getInterlacedFrame() {
        return interlacedFrame;
    }

    public void setInterlacedFrame(int interlacedFrame) {
        this.interlacedFrame = interlacedFrame;
    }

    public int getTopFieldFirst() {
        return topFieldFirst;
    }

    public void setTopFieldFirst(int topFieldFirst) {
        this.topFieldFirst = topFieldFirst;
    }

    public int getRepeatPict() {
        return repeatPict;
    }

    public void setRepeatPict(int repeatPict) {
        this.repeatPict = repeatPict;
    }
}
