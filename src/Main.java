import file.FileLoader;
import model.Particle;
import model.SimulationBoard;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("Input: <static file> <dynamic file> <output file> M rC");
            return;
        }

        String staticFile = args[0];
        String dynamicFile = args[1];
        String outputFile = args[2];
        int M = Integer.valueOf(args[3]);
        float rC = Float.valueOf(args[4]);

        try {
            SimulationBoard board = FileLoader.loadFiles(staticFile, dynamicFile);
            if (board == null) {
                System.out.println("Invalid file format");
                return;
            }

            long bruteForceTime = System.currentTimeMillis();
            board.calculateBruteForceDistance(rC);
            long bruteForceTime2 = System.currentTimeMillis();

            long time = System.currentTimeMillis();
            Map<Particle, Set<Particle>> closeParticles = board.calculateDistance(15, rC);
            long time2 = System.currentTimeMillis();

            output(closeParticles);
            System.out.println(String.format("Elapsed time for brute force: %d ms", bruteForceTime2 - bruteForceTime));
            System.out.println(String.format("Elapsed time for Cell Index Method Algorithm: %d ms", time2 - time));

            file.FileWriter.printToFile(outputFile, closeParticles);
            file.FileWriter.printToGraphicFile("Graphic.txt", closeParticles, 68);
        } catch (IOException e) {
            // Do something
        }
    }

    private static void output(Map<Particle, Set<Particle>> particleSetMap) {
        for (Particle p : particleSetMap.keySet()) {
            System.out.print(p.getId());
            System.out.print(": ");
            for (Particle p2 : particleSetMap.get(p)) {
                System.out.print(p2.getId() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}
