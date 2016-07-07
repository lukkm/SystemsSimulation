package utils;

import model.Particle;


public class VerletUtils {

    public static final float GRAVITY_CONSTANT = -9.8f;

    public static final int KN = 100000;
    public static final int KT = 200000;

    public static Particle integrate(Particle p, double dt, double fx, double fy) {
        double currentPosX = p.getX();
        double currentPosY = p.getY();
        double nextPosX = ((2 * currentPosX) - p.getPrevX()) + ((Math.pow(dt, 2) / p.getMass()) * fx);
        double nextPosY = ((2 * currentPosY) - p.getPrevY()) + ((Math.pow(dt, 2) / p.getMass()) * fy);
        double nextVelX = (nextPosX - currentPosX) / dt;
        double nextVelY = (nextPosY - currentPosY) / dt;

        Particle retParticle = p.copy();
        retParticle.setX(nextPosX);
        retParticle.setY(nextPosY);
        retParticle.setVx(nextVelX);
        retParticle.setVy(nextVelY);
        return retParticle;
    }

    public static double calculateWallXCollisionForce(Particle p, double w) {
        if (p.getX() - p.getRadius() < 0 && isWithinBounds(p.getY())) return KN * (-(p.getX() - p.getRadius()));
        if ((p.getX() + p.getRadius()) > w && isWithinBounds(p.getY())) return KN * (w - (p.getX() + p.getRadius()));
        return 0;
    }

    public static double calculateWallYCollisionForce(Particle p, double w, double l, double d) {
        if ((p.getY() - p.getRadius()) < 0
                && Math.abs(p.getY() - p.getRadius()) < 0.01
                && !isInExit(p, w, d)) {
            return KN * (-(p.getY() - p.getRadius()));
        }
        if ((p.getY() + p.getRadius()) > l) {
            return KN * (l - (p.getY() + p.getRadius()));
        }
        return 0;
    }

    public static CollisionForces calculateParticleCollisionForce(Particle p1, Particle p2) {
        if (DistanceUtils.calculateDistance(p1, p2) > 0) return null;

        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();

        double d = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        double psi = (p1.getRadius() + p2.getRadius()) - d;

        double deltaVelX = p1.getVx() - p2.getVx();
        double deltaVelY = p1.getVy() - p2.getVy();

        double fn = -KN * psi;

        double cosTita = deltaX / d;
        double senTita = deltaY / d;
        double ft = -KT * psi * ((deltaVelX * -senTita) + (deltaVelY * cosTita));
        double fnx = fn * cosTita;
        double fny = fn * senTita;
        double ftx = ft * -senTita;
        double fty = ft * cosTita;

        return new CollisionForces(fnx + ftx, fny + fty);
    }

    private static boolean isWithinBounds(double position) {
        return position > 0;
    }

    private static boolean isInExit(Particle p, double w, double d) {
        return p.getX() > ((w/2) - (d/2)) && p.getX() < ((w/2) + (d/2));
    }

    public static class CollisionForces {
        public double fx;
        public double fy;

        public CollisionForces(double fx, double fy) {
            this.fx = fx;
            this.fy = fy;
        }

        public double getFx() {
            return fx;
        }

        public double getFy() {
            return fy;
        }
    }

}