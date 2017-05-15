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
        this.width = Integer.parseInt(strings[14].split("=")[1]);
        this.height = Integer.parseInt(strings[15].split("=")[1]);
        this.pixFmt = PixFmt.valueOf(strings[16].split("=")[1]);
        this.sampleAspectRatio = new AspectRatio(strings[17].split("=")[1]);
        this.pictType = PictType.valueOf(strings[18].split("=")[1]);
        this.codedPictureNumber = Long.parseLong(strings[19].split("=")[1]);
        this.displayPictureNumber = Integer.parseInt(strings[20].split("=")[1]);
        this.interlacedFrame = Integer.parseInt(strings[21].split("=")[1]);
        this.topFieldFirst = Integer.parseInt(strings[22].split("=")[1]);
        this.repeatPict = Integer.parseInt(strings[23].split("=")[1]);
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
