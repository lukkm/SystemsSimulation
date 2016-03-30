package file;

import model.Particle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileWriter {

    public static void printToFile(String file, Map<Particle, Set<Particle>> particleSetMap) throws IOException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");

        StringBuilder builder;
        for (Particle p : particleSetMap.keySet()) {
            builder = new StringBuilder();
            builder.append(p.getId());
            builder.append(", ");
            for (Particle p2 : particleSetMap.get(p)) {
                builder.append(p2.getId());
                builder.append(", ");
            }
            builder.setLength(builder.length() - 2);
            writer.println(builder.toString());
        }

        writer.close();
    }

    public static void printToGraphicFile(String file, Map<Particle, Set<Particle>> particleSetMap, int particleId)
            throws IOException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");

        StringBuilder builder;
        Set<Particle> particles = particleSetMap.keySet();
        writer.println(particles.size());
        for (Particle p : particles) {
            builder = new StringBuilder();
            boolean isNeighbor = p.getId() == particleId || isNeighbor(particleId, p, particleSetMap);
            builder.append(isNeighbor ? "1.0 0.0" : "0.0 1.0");
            builder.append(" ");
            builder.append(p.getX());
            builder.append(" ");
            builder.append(p.getY());
            builder.append(" 0");
            writer.println(builder.toString());
        }

        writer.close();
    }

    public static void printStepsToGraphicFile(String file, List<List<Particle>> particleSteps) throws IOException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");

        StringBuilder builder;
        for (int i = 0; i < particleSteps.size(); i++) {
            writer.println(particleSteps.get(i).size());
            writer.println(i);
            for (Particle p : particleSteps.get(i)) {
                builder = new StringBuilder();
                builder.append("1.0 0.0 0.0");
                builder.append(" ");
                builder.append(p.getX());
                builder.append(" ");
                builder.append(p.getY());
                builder.append(" ");
                builder.append(p.getRadius());
                writer.println(builder.toString());
            }
        }

        writer.close();
    }

    private static boolean isNeighbor(int particleId, Particle p, Map<Particle, Set<Particle>> particleSetMap) {
        for (Particle p2 : particleSetMap.get(p)) {
            if (p2.getId() == particleId) return true;
        }
        return false;
    }

}
