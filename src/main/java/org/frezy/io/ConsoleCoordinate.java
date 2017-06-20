package main.java.org.frezy.io;

/**
 * Created by matthias on 15.06.17.
 */
public class ConsoleCoordinate {
    private int line;
    private int position;

    public ConsoleCoordinate(int line, int position) {
        this.line = line;
        this.position = position;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
