import file.FileLoader;
import model.SimulationBoard;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Input: <static file> <dynamic file> M");
            return;
        }

        String staticFile = args[0];
        String dynamicFile = args[1];
        int M = Integer.valueOf(args[2]);

        try {
            SimulationBoard board = FileLoader.loadFiles(staticFile, dynamicFile);
        } catch (IOException e) {
            // Do something
        }
    }

}
