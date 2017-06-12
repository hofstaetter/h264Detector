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

        String key;
        String[] pair = new String[2];
        for(String string : strings) {
            if(string.equals("[FRAME]")) continue;
            if(string.equals("[/FRAME]")) return;

            //key = pair[0], value = pair[1]
            pair = string.split("=");

            if(pair[1].equals("N/A")) continue;

            if(pair[0].equals("sample_fmt"))
                this.sampleFmt = SampleFmt.valueOf(pair[1]);

            else if(pair[0].equals("nb_samples"))
                this.nbSamples = Integer.parseInt(pair[1]);

            else if(pair[0].equals("channels"))
                this.channels = Short.parseShort(pair[1]);

            else if(pair[0].equals("channel_layout"))
                this.channelLayout = ChannelLayout.valueOf(pair[1]);
        }
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
