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
        while(tries < MAX_TRIES) {
            particle = new Particle(i, 0.4, 0, getRandPosition(w, 0.4f), getRandPosition(l, 0.4f));
            particle.setMass(0.01);
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

}
