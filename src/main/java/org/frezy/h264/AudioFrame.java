package main.java.org.frezy.h264;

/**
 * Created by matthias on 08.05.17.
 */
public class AudioFrame extends Frame {

    public enum SampleFmt {
        fltp
    }

    public enum ChannelLayout {
        stereo
    }

    private SampleFmt sampleFmt;
    private int nbSamples;
    private short channels;
    private ChannelLayout channelLayout;

    public AudioFrame(String[] strings) {
        super(strings);
        this.sampleFmt = SampleFmt.valueOf(strings[14].split("=")[1]);
        this.nbSamples = Integer.parseInt(strings[15].split("=")[1]);
        this.channels = Short.parseShort(strings[16].split("=")[1]);
        this.channelLayout = ChannelLayout.valueOf(strings[17].split("=")[1]);
    }

    public String toString() {
        return super.toString() + "\n" +
                "   sample_fmt: " + getSampleFmt() + "\n" +
                "   nb_samples: " + getNbSamples() + "\n" +
                "   channels: " + getChannels() + "\n" +
                "   channelLayout: " + getChannelLayout() + "\n";
    }

    public SampleFmt getSampleFmt() {
        return sampleFmt;
    }

    public void setSampleFmt(SampleFmt sampleFmt) {
        this.sampleFmt = sampleFmt;
    }

    public int getNbSamples() {
        return nbSamples;
    }

    public void setNbSamples(int nbSamples) {
        this.nbSamples = nbSamples;
    }

    public short getChannels() {
        return channels;
    }

    public void setChannels(short channels) {
        this.channels = channels;
    }

    public ChannelLayout getChannelLayout() {
        return channelLayout;
    }

    public void setChannelLayout(ChannelLayout channelLayout) {
        this.channelLayout = channelLayout;
    }
}
