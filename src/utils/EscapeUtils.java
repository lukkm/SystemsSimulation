package utils;

import controller.SimulationController;
import model.Particle;

public class EscapeUtils {

    public static final int BOARD_SIZE = 20;
    public static final double DOOR_SIZE = 1.2;
    public static final int MAX_TRIES = 20;
    public static final int PERSON_MASS = 60;

    public static final int ESCAPE_X = 12;
    public static final int GOAL_2_X = 19;
    public static final int GOAL_1_X = 10;
    public static final int GOAL_Y = 10;

    public static final double TAU = 0.5;
    public static final double VD = 0.8;
    public static final double A = 2000;
    public static final double B = 0.08;

    public static SimulationController generateEscapeParticles(int N) {
        SimulationController simulationController = new SimulationController(BOARD_SIZE, BOARD_SIZE);

        int tries = 0;
        int i = 0;
        Particle particle;
        while(tries < MAX_TRIES && i < N) {
            particle = new Particle(
                    i,
                    0.25,
                    0,
                    DistanceUtils.getRandPosition(BOARD_SIZE/2, 0.25f),
                    DistanceUtils.getRandPosition(BOARD_SIZE, 0.25f));
            particle.setMass(PERSON_MASS);
            tries++;
            if (!DistanceUtils.overlapsAny(particle, simulationController.getParticleList())) {
                simulationController.addPartcile(particle);
                i++;
                tries = 0;
            }
        }

        return simulationController;
    }

    public static ParticleForce calculateGoalForces(Particle p) {
        double deltaX;
        if (p.getX() < GOAL_1_X && !isInExit(p, BOARD_SIZE, DOOR_SIZE)) {
            deltaX = GOAL_1_X - p.getX();
        } else {
            deltaX = GOAL_2_X - p.getX();
        }
        double deltaY = GOAL_Y - p.getY();
        double mod = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        double eX = deltaX / mod;
        double eY = deltaY / mod;
        return new ParticleForce(
                (p.getMass() * ((VD * eX) - p.getVx())) / TAU,
                (p.getMass() * ((VD * eY) - p.getVy())) / TAU);
    }

    public static ParticleForce calculateSocialForces(Particle p, Particle p2) {
        if (p.getX() > GOAL_1_X) return null;
        double deltaX = p.getX() - p2.getX();
        double deltaY = p.getY() - p2.getY();
        double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        double xi = p.getRadius() + p2.getRadius() - distance;
        double eX = deltaX / distance;
        double eY = deltaY / distance;
        return new ParticleForce(A * Math.exp(xi / B) * eX, A * Math.exp(xi / B) * eY);
    }

    public static boolean isXColliding(Particle p, double w) {
        return p.getX() - p.getRadius() < 0 || (p.getX() + p.getRadius()) > w;
    }

    public static boolean isYColliding(Particle p, double w) {
        return p.getY() - p.getRadius() < 0 || (p.getY() + p.getRadius()) > w;
    }

    public static boolean isDoorXColliding(Particle p, double w, double l) {
        return (p.getX() + p.getRadius()) > (w/2) && (p.getX() < (w/2)) && !isInExit(p, l, DOOR_SIZE);
    }

    private static boolean isInExit(Particle p, double w, double d) {
        return p.getY() > ((w/2) - (d/2)) && p.getY() < ((w/2) + (d/2));
    }



}
