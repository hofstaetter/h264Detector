package main.java.org.frezy.io;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.server.ExportException;
import java.util.LinkedList;

/**
 * Created by matthias on 15.06.17.
 */
public class Console {
    private int width, height;

    private LinkedList<ConsoleObject> consoleObjects;
    private OperationSystem operationSystem;

    public Console() {
        this.consoleObjects = new LinkedList<ConsoleObject>();

        this.operationSystem = findOperationSystem();

        //get width and height
        if (this.operationSystem == OperationSystem.macOS) {
            this.width = 150;
            this.height = 75;
        }
    }

    private OperationSystem findOperationSystem() {
        switch(System.getProperty("os.name")) {
            case "Windows":
                return OperationSystem.Windows;
            case "Mac OS X":
                return OperationSystem.macOS;
            case "Fedora":
                return OperationSystem.Fedora;
            default:
                throw new NotImplementedException();
        }
    }

    private String getLine(int lineNumber) {
        //consoleObjects.stream().filter(co -> co.consoleCoordinate.getLine() == lineNumber).

        return null;
    }

    private void refresh() {

    }

    private void clear() {

    }
}
