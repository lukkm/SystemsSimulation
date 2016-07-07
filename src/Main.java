import file.FileLoader;
import model.Particle;
import controller.SimulationController;
import utils.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    private static final int CALCULATE_DISTANCE_MODE = 0;
    private static final int SIMULATE_MODE = 1;
    private static final int COLLISION_MODE = 2;
    private static final int ORBIT_MODE = 3;
    private static final int SILUM_MODE = 4;
    private static final int ESCAPE_MODE = 5;
    private static final int OBSTACLES_MODE = 6;

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
        float w = 1000f;
        float d = 100f;
        int N = 0;
        int moveableN = 0;

        switch (mode) {
            case ORBIT_MODE:
                l = Float.valueOf(args[1]);
                N = Integer.valueOf(args[2]);
                outputFile = args[3];
                break;
            case SILUM_MODE:
                l = Float.valueOf(args[1]);
                w = Float.valueOf(args[2]);
                d = Float.valueOf(args[3]);
                outputFile = args[4];
                break;
            case ESCAPE_MODE:
            case OBSTACLES_MODE:
                N = Integer.valueOf(args[1]);
                moveableN = Integer.valueOf(args[2]);
                outputFile = args[3];
                break;
            default:
                staticFile = args[1];
                dynamicFile = args[2];
                outputFile = args[3];
                M = Integer.valueOf(args[4]);
                rC = Float.valueOf(args[5]);
                crossMap = Integer.valueOf(args[6]) != 0;
                hasMass = Integer.valueOf(args[7]) != 0;
                velocityRange = Float.valueOf(args[8]);
        }

        try {
            SimulationController board;
            switch (mode) {
                case ORBIT_MODE:
                    board = OrbitUtils.generateOrbitBoard(l, N);
                    break;
                case SILUM_MODE:
                    board = SilumUtils.generateSilumParticles(l, w, d);
                    break;
                case ESCAPE_MODE:
                    board = EscapeUtils.generateEscapeParticles(N);
                    break;
                case OBSTACLES_MODE:
                    board = ObstacleUtils.generateObstacleParticles(N, moveableN);
                    break;
                default:
                    board = FileLoader.loadFiles(staticFile, dynamicFile, hasMass, velocityRange);
            }

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
                case SILUM_MODE:
                    List<List<Particle>> silumParticles = board.simulateSilum(500);
                    file.FileWriter.printStepsToGraphicFileWithMargins(outputFile, silumParticles, l, w);
                    break;
                case ESCAPE_MODE:
                    List<List<Particle>> escapeParticles = board.simulateEscape(500);
                    file.FileWriter.printStepsToGraphicFileWithDoor(
                            outputFile,
                            escapeParticles,
                            EscapeUtils.BOARD_SIZE,
                            EscapeUtils.BOARD_SIZE,
                            EscapeUtils.DOOR_SIZE);
                    break;
                case OBSTACLES_MODE:
                    List<List<Particle>> obstacleParticles = board.simulateObstacles(500);
                    file.FileWriter.printStepsToGraphicFileWithHuman(
                            outputFile,
                            obstacleParticles,
                            ObstacleUtils.BOARD_HEIGHT,
                            ObstacleUtils.BOARD_WIDTH);
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
