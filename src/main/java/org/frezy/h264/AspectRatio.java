package main.java.org.frezy.h264;

/**
 * Created by matthias on 08.05.17.
 */
public class AspectRatio {
    private int x;
    private int y;

    public AspectRatio(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public AspectRatio(String aspect) {
        if(aspect.equals("N/A")) return;
        this.x = Integer.parseInt(aspect.split(":")[0]);
        this.y = Integer.parseInt(aspect.split(":")[1]);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
