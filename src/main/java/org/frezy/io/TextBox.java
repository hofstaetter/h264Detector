package main.java.org.frezy.io;

import javax.xml.soap.Text;

/**
 * Created by matthias on 15.06.17.
 */
public class TextBox extends ConsoleObject {
    private String content;

    public TextBox(String content) {
        this.height = 1;
        this.content = content;
    }

    public TextBox() {
        this.width = width;
        this.content = content;
    }

    public TextBox(String content, int width) {
        this.content = content;
    }

    @Override
    public String lineToString(int line) {
        return content;
    }
}
