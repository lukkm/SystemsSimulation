package file;

import model.Particle;

import java.io.IOException;
import java.io.PrintWriter;
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
        for (Particle p : particleSetMap.keySet()) {
            builder = new StringBuilder();
            builder.append(p.getId() == particleId || isNeighbor(particleId, p, particleSetMap) ? "100" : "200");
            builder.append(" ");
            builder.append(p.getX());
            builder.append(" ");
            builder.append(p.getY());
            builder.append(" 0");
            writer.println(builder.toString());
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
