package utils;

import controller.SimulationController;
import model.Particle;
import org.omg.CORBA.ORB;

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
            p = new Particle(i, radius/(N/10), 0, 0, 0);

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

    private static void setRandomPosition(Particle p, int orbitSize,    double sunCenter) {
        double distance = (Math.random() * orbitSize + ORBIT_RANGE_START);
        double angle = Math.random() * 2 * Math.PI;

        if (distance < ORBIT_RANGE_START) {
            int a = 0;
        }

        p.setX(sunCenter + (distance * Math.cos(angle)));
        p.setY(sunCenter + (distance * Math.sin(angle)));
    }

}
