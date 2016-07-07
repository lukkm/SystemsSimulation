package utils;

import controller.SimulationController;
import model.Particle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObstacleUtils {

    public static final int BOARD_HEIGHT = 10;
    public static final int BOARD_WIDTH = 20;
    public static final int PERSON_MASS = 60;
    public static final int OBSTACLE_MASS = 200;

    public static final double TAU = 0.5;
    public static final double VD = 2;

    public static final List<ObstacleGoal> GOALS_LIST = new ArrayList<>();
    public static final ObstacleGoal MAIN_GOAL = new ObstacleGoal(19, 5);

    static {
        // Populate goals list around the board.
        for (int i = 0; i < BOARD_WIDTH; i++) {
            GOALS_LIST.add(new ObstacleGoal(i, 0.5f));
            GOALS_LIST.add(new ObstacleGoal(i, BOARD_HEIGHT));
        }
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            GOALS_LIST.add(new ObstacleGoal(1, i));
            if (i != MAIN_GOAL.y) GOALS_LIST.add(new ObstacleGoal(MAIN_GOAL.x, i));
        }
        Collections.sort(GOALS_LIST);
    }

    public static SimulationController generateObstacleParticles(int N, int moveable) {
        SimulationController simulationController = new SimulationController(BOARD_HEIGHT, BOARD_WIDTH);

        // Add the first particle in the (1; 1) position
        Particle human = new Particle(0, 0.125, 0, 1, 5);
        human.setMass(PERSON_MASS);
        simulationController.addPartcile(human);

        Particle particle;
        for (int i = 1; i < N + 1; i++) {
            boolean isSet = false;
            while (!isSet) {
                particle = new Particle(
                        i,
                        0.25,
                        0,
                        DistanceUtils.getRandPositionBetween(2, BOARD_WIDTH - 2, 0.25f),
                        DistanceUtils.getRandPosition(BOARD_HEIGHT, 0.25f));
                particle.setMass(OBSTACLE_MASS);
                if (!DistanceUtils.overlapsAny(particle, simulationController.getParticleList())
                        && !DistanceUtils.overlapsAny(particle, simulationController.getObstacleList())
                        && !DistanceUtils.overlapsAny(particle, simulationController.getMoveableObstacleList())) {
                    if (moveable > 0) {
                        moveable--;
                        simulationController.addMoveableObstacle(particle);
                    } else {
                        simulationController.addObstacle(particle);
                    }
                    isSet = true;
                }
            }
        }

        return simulationController;
    }

    public static void setRandomGoal(Particle p) {
        int randomGoal = (int) Math.floor(Math.random() * GOALS_LIST.size());
        p.setGoal(GOALS_LIST.get(randomGoal));
    }

    public static ParticleForce calculateGoalForces(Particle p, ObstacleGoal goal) {
        double deltaX = goal.x - p.getX();
        double deltaY = goal.y - p.getY();
        double mod = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        double eX = deltaX / mod;
        double eY = deltaY / mod;
        return new ParticleForce(
                (p.getMass() * ((VD * eX) - p.getVx())) / TAU,
                (p.getMass() * ((VD * eY) - p.getVy())) / TAU);
    }

    public static class ObstacleGoal implements Comparable<ObstacleGoal> {

        private float x;
        private float y;
        private int valid = 0;

        public ObstacleGoal(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public boolean isValid() {
            return valid <= 0;
        }

        public void invalidate(int steps) {
            valid = steps;
        }

        public void revive() {
            if (valid > 0) valid--;
        }

        public double distanceTo(ObstacleGoal goal) {
            return Math.sqrt(Math.pow(goal.x - this.x, 2) + Math.pow(goal.y - this.y, 2));
        }

        @Override
        public int compareTo(ObstacleGoal o) {
            return (int) Math.signum(this.distanceTo(MAIN_GOAL) - o.distanceTo(MAIN_GOAL));
        }
    }

}
