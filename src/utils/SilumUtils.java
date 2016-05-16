package utils;

import controller.SimulationController;
import model.Particle;

import java.util.List;

public class SilumUtils {

    public static final int MAX_TRIES = 20;

    public static final float GRAVITY_CONSTANT = 9.8f;

    public static final int KN = 100000;
    public static final int KT = 200000;

    public static SimulationController generateSilumParticles(float l, float w, float D) {
        SimulationController simulationController = new SimulationController(l, w, D);

        int tries = 0;
        int i = 0;
        Particle particle;
        while(tries < 1) {
            particle = new Particle(i, D/10, 0, getRandPosition(w, D/10), getRandPosition(l, D/10));
            particle.setMass(0.01);
            particle.setVx((Math.random() * 20) - 10);
            tries++;
            if (!overlapsAny(particle, simulationController.getParticleList())) {
                simulationController.addPartcile(particle);
                i++;
                tries = 0;
            }
        }

        return simulationController;
    }

    private static double getRandPosition(float boxSize, float radius) {
        return (Math.random() * (boxSize - (radius * 2))) + radius;
    }

    private static boolean overlapsAny(Particle p, List<Particle> particleList) {
        for (Particle p2 : particleList) {
            if (DistanceUtils.calculateDistance(p, p2) < 0) {
                return true;
            }
        }
        return false;
    }

    public static void calculateWallCollision(Particle p, double l, double w, double d, double dt) {
        if ((p.getX() - p.getRadius()) < 0 && isWithinBounds(p.getY(), l)) {
            p.setVx(p.getVx() + (KN * (-(p.getX() - p.getRadius())) * dt));
        }
        if ((p.getX() + p.getRadius()) > w && isWithinBounds(p.getY(), l)) {
            p.setVx(p.getVx() + (KN * (w - (p.getX() + p.getRadius())) * dt));
        }
        if ((p.getY() - p.getRadius()) < 0 && !isInExit(p, w, d)) {
            p.setVy(p.getVy() + (((KN * (-(p.getY() - p.getRadius()))) / p.getMass()) * dt));
        }
    }

    public static double calculateWallXCollisionForce(Particle p, double l, double w) {
        if ((p.getX() - p.getRadius()) < 0 && isWithinBounds(p.getY(), l)) {
            return KN * (-(p.getX() - p.getRadius()));
        }
        if ((p.getX() + p.getRadius()) > w && isWithinBounds(p.getY(), l)) {
            return KN * (w - (p.getX() + p.getRadius()));
        }
        return 0;
    }

    public static double calculateWallYCollisionForce(Particle p, double w, double d) {
        if ((p.getY() - p.getRadius()) < 0 && !isInExit(p, w, d)) return KN * (-(p.getY() - p.getRadius()));
        return 0;
    }

    /*
    public static void calculateParticleCollision(Particle p1, Particle p2) {
        if (DistanceUtils.calculateDistance(p1, p2) > 0) return;

        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();

        double d = Math.sqrt()
    }*/

    private static boolean isWithinBounds(double position, double boxSize) {
        return position > 0 && position < boxSize;
    }

    private static boolean isInExit(Particle p, double w, double d) {
        return p.getX() > ((w/2) - (d/2)) && p.getX() < ((w/2) + (d/2));
    }

}
