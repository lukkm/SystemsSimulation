package utils;

import controller.SimulationController;
import model.Particle;

import java.util.List;

public class SilumUtils {

    public static final int MAX_TRIES = 20;

    public static SimulationController generateSilumParticles(float l, float w, float D) {
        SimulationController simulationController = new SimulationController(l, w, D);

        int tries = 0;
        int i = 0;
        Particle particle;
        while(tries < MAX_TRIES) {
            particle = new Particle(
                    i, 0.4, 0, DistanceUtils.getRandPosition(w, 0.4f), DistanceUtils.getRandPosition(l, 0.4f));
            particle.setMass(0.01);
            tries++;
            if (!DistanceUtils.overlapsAny(particle, simulationController.getParticleList())) {
                simulationController.addPartcile(particle);
                i++;
                tries = 0;
            }
        }

        return simulationController;
    }

}
