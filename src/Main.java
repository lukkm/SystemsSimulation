import file.FileLoader;
import model.Particle;
import controller.SimulationController;
import utils.DistanceUtils;
import utils.OrbitUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    private static final int CALCULATE_DISTANCE_MODE = 0;
    private static final int SIMULATE_MODE = 1;
    private static final int COLLISION_MODE = 2;
    private static final int ORBIT_MODE = 3;

    public static void main(String[] args) {
        int mode = Integer.valueOf(args[0]);

        String staticFile = null;
        String dynamicFile = null;
        String outputFile = null;
        int M = 1;
        float rC = 1f;
        boolean crossMap = false;
        boolean hasMass = false;
        float velocityRange = 0.1f;

        float l = 1100f;
        int N = 0;

        if (mode != ORBIT_MODE) {
            staticFile = args[1];
            dynamicFile = args[2];
            outputFile = args[3];
            M = Integer.valueOf(args[4]);
            rC = Float.valueOf(args[5]);
            crossMap = Integer.valueOf(args[6]) != 0;
            hasMass = Integer.valueOf(args[7]) != 0;
            velocityRange = Float.valueOf(args[8]);
        } else {
            l = Float.valueOf(args[1]);
            N = Integer.valueOf(args[2]);
            outputFile = args[3];
        }

        try {
            SimulationController board = (mode != ORBIT_MODE) ?
                    FileLoader.loadFiles(staticFile, dynamicFile, hasMass, velocityRange) :
                    OrbitUtils.generateOrbitBoard(l, N);
            if (board == null) {
                System.out.println("Invalid file format");
                return;
            }

            switch (mode) {
                case CALCULATE_DISTANCE_MODE:
                    long bruteForceTime = System.currentTimeMillis();
                    DistanceUtils.calculateBruteForceDistance(board.getParticleList(), rC);
                    long bruteForceTime2 = System.currentTimeMillis();

                    long time = System.currentTimeMillis();
                    Map<Particle, Set<Particle>> closeParticles = board.calculateDistance(M, rC, crossMap);
                    long time2 = System.currentTimeMillis();

                    output(closeParticles);
                    System.out.println(String.format("Elapsed time for brute force: %d ms", bruteForceTime2 - bruteForceTime));
                    System.out.println(String.format("Elapsed time for Cell Index Method Algorithm: %d ms", time2 - time));

                    file.FileWriter.printToFile(outputFile, closeParticles);
                    file.FileWriter.printToGraphicFile("Graphic.txt", closeParticles, 0);
                    break;
                case SIMULATE_MODE:
                    List<List<Particle>> particleSteps = board.simulateSteps(500, 1, 2);
                    file.FileWriter.printStepsToGraphicFile(outputFile, particleSteps);
                    break;
                case COLLISION_MODE:
                    List<List<Particle>> particleCollisions = board.simulateCollisions(500);
                    file.FileWriter.printStepsToGraphicFile(outputFile, particleCollisions);
                    break;
                case ORBIT_MODE:
                    List<List<Particle>> orbitParticles = board.simulateOrbits(500);
                    file.FileWriter.printStepsToGraphicFile(outputFile, orbitParticles);
                    break;
            }

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
