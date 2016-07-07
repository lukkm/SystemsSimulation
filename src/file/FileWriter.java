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
                builder.append(p.getVy());
                builder.append(" ");
                builder.append(p.getVx());
                builder.append(" ");
                builder.append(p.getRadius());
                writer.println(builder.toString());
            }
        }

        writer.close();
    }

    public static void printStepsToGraphicFileWithHuman(String file, List<List<Particle>> particleSteps, float l, float w) throws IOException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");

        StringBuilder builder;
        for (int i = 0; i < particleSteps.size(); i++) {
            writer.println(particleSteps.get(i).size() + 4);
            writer.println(i);
            writer.println("1.0 0.0 0.0 -0.5 -0.5 0 0 0");
            writer.println("1.0 0.0 0.0 -0.5 " + (l + 0.5) + " 0 0 0");
            writer.println("1.0 0.0 0.0 " + (w + 0.5) + " " + (l + 0.5) + " 0 0 0");
            writer.println("1.0 0.0 0.0 " + (w + 0.5) + " -0.5 0 0 0");
            Particle human = particleSteps.get(i).get(0);
            writer.println("0.0 1.0 0.0 " + human.getX() + " " + human.getY() + " " + human.getVy() + " " + human.getVx() + " " + human.getRadius());
            for (int j = 1; j < particleSteps.get(i).size(); j++) {
                Particle p = particleSteps.get(i).get(j);
                builder = new StringBuilder();
                builder.append("1.0 0.0 0.0");
                builder.append(" ");
                builder.append(p.getX());
                builder.append(" ");
                builder.append(p.getY());
                builder.append(" ");
                builder.append(p.getVy());
                builder.append(" ");
                builder.append(p.getVx());
                builder.append(" ");
                builder.append(p.getRadius());
                writer.println(builder.toString());
            }
        }

        writer.close();
    }

    public static void printStepsToGraphicFileWithMargins(
            String file, List<List<Particle>> particleSteps, float l, float w)
            throws IOException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");

        StringBuilder builder;
        for (int i = 0; i < particleSteps.size(); i++) {
            writer.println(particleSteps.get(i).size() + 4);
            writer.println(i);
            writer.println("1.0 0.0 0.0 0 0 0 0 0");
            writer.println("1.0 0.0 0.0 0 " + l + " 0 0 0");
            writer.println("1.0 0.0 0.0 " + w + " " + l + " 0 0 0");
            writer.println("1.0 0.0 0.0 " + w + " 0 0 0 0");
            for (Particle p : particleSteps.get(i)) {
                builder = new StringBuilder();
                builder.append("1.0 0.0 0.0");
                builder.append(" ");
                builder.append(p.getX());
                builder.append(" ");
                builder.append(p.getY());
                builder.append(" ");
                builder.append(p.getVy());
                builder.append(" ");
                builder.append(p.getVx());
                builder.append(" ");
                builder.append(p.getRadius());
                writer.println(builder.toString());
            }
        }

        writer.close();
    }

    public static void printStepsToGraphicFileWithDoor(
            String file, List<List<Particle>> particleSteps, float l, float w, double doorSize)
            throws IOException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");

        StringBuilder builder;
        for (int i = 0; i < particleSteps.size(); i++) {
            writer.println(particleSteps.get(i).size() + 600);
            writer.println(i);
            printBoardMarginsWithDoor(writer, l, w, doorSize);
            for (Particle p : particleSteps.get(i)) {
                builder = new StringBuilder();
                builder.append("1.0 0.0 0.0");
                builder.append(" ");
                builder.append(p.getX());
                builder.append(" ");
                builder.append(p.getY());
                builder.append(" ");
                builder.append(p.getVy());
                builder.append(" ");
                builder.append(p.getVx());
                builder.append(" ");
                builder.append(p.getRadius());
                writer.println(builder.toString());
            }
        }

        writer.close();
    }

    private static void printBoardMarginsWithDoor(PrintWriter writer, float l, float w, double doorSize) {
        // Top border
        for (float i = 0; i < w; i += (w / 100)) {
            writer.println("1.0 1.0 1.0 " + i + " 0 0 0 0.05");
        }

        // Left border
        for (float i = 0; i < l; i += (l / 100)) {
            writer.println("1.0 1.0 1.0 0 " + i + " 0 0 0.05");
        }

        // Bottom border
        for (float i = 0; i < w; i += (w / 100)) {
            writer.println("1.0 1.0 1.0 " + i + " " + l + " 0 0 0.05");
        }

        // Right border
        for (float i = 0; i < l; i += (l / 100)) {
            writer.println("1.0 1.0 1.0 " + w + " " + i + " 0 0 0.05");
        }

        double doorStart = (l/2) - (doorSize/2);
        double doorEnd = (l/2) + (doorSize/2);

        // Top wall
        for (double i = 0; i < doorStart; i += (doorStart / 100)) {
            writer.println("1.0 1.0 1.0 " + (w / 2) + " " + i + " 0 0 0.05");
        }

        // Bottom wall
        for (double i = doorEnd; i < l; i += (doorStart / 100)) {
            writer.println("1.0 1.0 1.0 " + (w / 2) + " " + i + " 0 0 0.05");
        }
    }

    private static boolean isNeighbor(int particleId, Particle p, Map<Particle, Set<Particle>> particleSetMap) {
        for (Particle p2 : particleSetMap.get(p)) {
            if (p2.getId() == particleId) return true;
        }
        return false;
    }

}
