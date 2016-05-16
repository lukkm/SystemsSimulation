package utils;

import controller.SimulationController;
import model.Particle;
import org.omg.CORBA.ORB;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrbitUtils {

    public static final int ORBIT_RANGE_START = 100;
    public static final int ORBIT_RANGE_END = 1000;

    public static final double G = 0.13386;

    public static SimulationController generateOrbitBoard(float l, int N) {
        SimulationController simulationController = new SimulationController(l);

        double radius = l / 30;
        int orbitSize = ORBIT_RANGE_END - ORBIT_RANGE_START;

        Double orbitL = null;

        Particle sun = new Particle(N, radius, 0, (l / 2) - (radius / 2), (l / 2) - (radius / 2));
        sun.setMass(1);
        simulationController.addPartcile(sun);
        simulationController.setSun(sun);

        Particle p;
        for (int i = 0; i < N; i++) {
            p = new Particle(i, radius/N, 0, 0, 0);

            setRandomPosition(p, orbitSize, sun.getX());

            p.setMass(1.0/N);

            double deltaX = sun.getX() - p.getX();
            double deltaY = sun.getY() - p.getY();

            p.setAngle(Math.atan2(deltaY, deltaX) + (Math.PI / 2));

            double distanceToSun = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

            if (orbitL == null) {
                double velocity = Math.sqrt((G * sun.getMass()) / distanceToSun);

                p.setV(velocity);
                orbitL = velocity * distanceToSun;
            } else {
                p.setV(orbitL / distanceToSun);
                simulationController.setOrbitL(orbitL);
            }

            simulationController.addPartcile(p);
        }

        return simulationController;
    }

    public static void updateVelocity(Particle sun, Particle p, double orbitL) {
        double deltaX = sun.getX() - p.getX();
        double deltaY = sun.getY() - p.getY();

        double distanceToSun = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

        p.setV(orbitL / distanceToSun);
        p.setAngle(Math.atan2(deltaY, deltaX) + (Math.PI / 2));
    }

    public static List<Particle> calculateCollisions(Particle sun, double orbitL, List<Particle> particleList) {
        List<Particle> newList = new ArrayList<>();
        Set<Particle> collidedParticles = new HashSet<>();

        for (Particle p : particleList) {
            boolean collided = false;

            if (!collidedParticles.contains(p)) {
                for (Particle p2 : particleList) {
                    if (!collidedParticles.contains(p2)) {
                        if (p != p2) {
                            if (DistanceUtils.calculateDistance(p, p2) < 1) {
                                collided = true;
                                collidedParticles.add(p);
                                collidedParticles.add(p2);

                                // Generate new particle
                                Particle collisionParticle = new Particle(
                                        p.getId(),
                                        p.getRadius() + p2.getRadius(),
                                        0,
                                        ((p.getX() * p.getMass()) +
                                                (p2.getX() * p2.getMass())) /
                                                        (p.getMass() + p2.getMass()),
                                        ((p.getY() * p.getMass()) +
                                                (p2.getY() * p2.getMass())) /
                                                        (p.getMass() + p2.getMass()));

                                collisionParticle.setMass(p.getMass() + p2.getMass());
                                updateVelocity(sun, collisionParticle, orbitL);

                                newList.add(collisionParticle);
                            }
                        }
                    }
                }
                if (!collided) newList.add(p);
            }
        }

        return newList;
    }

    private static void setRandomPosition(Particle p, int orbitSize,    double sunCenter) {
        double distance = (Math.random() * orbitSize + ORBIT_RANGE_START);
        double angle = Math.random() * 2 * Math.PI;

        if (distance < ORBIT_RANGE_START) {
            int a = 0;
        }

        p.setX(sunCenter + (distance * Math.cos(angle)));
        p.setY(sunCenter + (distance * Math.sin(angle)));
    }

    private static double getDistance(Particle p1, Particle p2) {
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
    }

    public static double calculateTotalKineticEnergy(Particle sun, List<Particle> particleList) {
        double K = 0;
        for (Particle p : particleList) {
            if (p != sun) {
                K += (Math.pow(p.getV(), 2) * p.getMass()) / 2;
            }
        }
        return K;
    }

    public static double calculateTotalPotentialEnergy(Particle sun, List<Particle> particleList) {
        double U = 0;
        for (Particle p : particleList) {
            if (p != sun) {
                double sunDistance = Math.sqrt(Math.pow(p.getX() - sun.getX(), 2) + Math.pow(p.getY() - sun.getY(), 2));
                U += ((-G) * p.getMass() * sun.getMass()) / sunDistance;
            }
        }
        return U;
    }

}
