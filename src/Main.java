import file.FileLoader;
import model.SimulationBoard;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            SimulationBoard board = FileLoader.loadFiles(args[0], args[1]);
            int a = 0;
        } catch (IOException e) {
            // Do something
        }
    }

}
