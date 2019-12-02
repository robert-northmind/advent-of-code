package Utility;

import java.io.*;
import java.util.*;

public class MyFileReader {
    private BufferedReader bufferedReader;

    public MyFileReader(String path) {
        try {
            FileInputStream fstream = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream);
            this.bufferedReader = new BufferedReader(new InputStreamReader(in));
        } catch (FileNotFoundException error) {
            System.err.println("Error: " + error.getMessage());
        }
    }

    public ArrayList<String> getInputLines() {
        ArrayList<String> lines = new ArrayList<String>();

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException error) {
            System.err.println("Error: " + error.getMessage());
        }
        return lines;
    }
}