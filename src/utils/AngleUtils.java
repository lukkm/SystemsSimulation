package utils;

import model.Particle;

import java.util.List;

public class AngleUtils {

    public static double getAngleAverage(Particle p, List<Particle> stepParticles) {
        List<Particle> particles = NeighborUtils.calculateBruteForceNeighbors(p, stepParticles);
        double sinSum = 0;
        double cosSum = 0;
        for (Particle p2 : particles) {
            sinSum += Math.sin(p2.getAngle());
            cosSum += Math.cos(p2.getAngle());
        }
        sinSum += Math.sin(p.getAngle());
        cosSum += Math.cos(p.getAngle());

        return Math.atan2((sinSum / (particles.size() + 1)), (cosSum / (particles.size() + 1)));
    }

}
