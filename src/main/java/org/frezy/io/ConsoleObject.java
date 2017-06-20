package main.java.org.frezy.io;

/**
 * Created by matthias on 15.06.17.
 */
public abstract class ConsoleObject implements ConsoleObjectInterface {
    protected int width;
    protected int height;
    protected ConsoleCoordinate consoleCoordinate;

    public ConsoleObject() {
        this.height = 1;
    }

    @Override
    public String lineToString(int line) {
        return null;
    }
}
