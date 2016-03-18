package utils;

import model.Particle;

public class DistanceUtils {

    public static double calculateDistance(Particle p1, Particle p2) {
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2))
                - p1.getRadius() - p2.getRadius();
    }

    public static double calculateDistance(Particle p1, Particle p2, boolean cross, float l) {
        if (cross) {
            p1 = p1.copy();
            p2 = p2.copy();
            p1.setX(p1.getX() - l);
            p2.setX(p2.getX() - l);
        }
        return calculateDistance(p1 ,p2);
    }

}
