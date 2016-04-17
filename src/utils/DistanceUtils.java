package utils;

import model.Particle;

import java.util.*;

public class DistanceUtils {

    public static double calculateDistance(Particle p1, Particle p2, boolean crossX, boolean crossY, float l) {
        double x2 = crossX ? (p2.getX() > p1.getX() ? p2.getX() - l : p2.getX() + l) : p2.getX();
        double y2 = crossY ? (p2.getY() > p1.getY() ? p2.getY() - l : p2.getY() + l) : p2.getY();

        return calculateDistance(p1.getX() ,p1.getY(), p1.getRadius(), x2, y2, p2.getRadius());
    }

    public static double calculateDistance(Particle p1, Particle p2) {
        return calculateDistance(p1.getX(), p1.getY(), p1.getRadius(), p2.getX(), p2.getY(), p2.getRadius());
    }

    private static double calculateDistance(double x1, double y1, double r1, double x2, double y2, double r2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) - r1 - r2;
    }

    public static Map<Particle, Set<Particle>> calculateBruteForceDistance(List<Particle> particleList, float rc) {
        Map<Particle, Set<Particle>> closeParticles = new LinkedHashMap<>();
        for (Particle p1 : particleList) {
            closeParticles.put(p1, new HashSet<>());
            for (Particle p2 : particleList) {
                if (p1 != p2) {
                    if (NeighborUtils.isNeighbor(p1, p2, closeParticles)
                            || DistanceUtils.calculateDistance(p1, p2) < rc)
                        closeParticles.get(p1).add(p2);
                }
            }
        }
        return closeParticles;
    }

}
