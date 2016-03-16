package utils;

import model.Particle;

public class DistanceUtils {

    public static double calculateDistance(Particle p1, Particle p2) {
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2))
                - p1.getRadius() - p2.getRadius();
    }

}
