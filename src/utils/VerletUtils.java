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
        double nextVelX = (nextPosX -  currentPosX) / dt;
        double nextVelY = (nextPosY - currentPosY) / dt;


        Particle retParticle = p.copy();
        retParticle.setX(nextPosX);
        retParticle.setY(nextPosY);
        retParticle.setVx(nextVelX);
        retParticle.setVy(nextVelY);
        return retParticle;
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

    public static Particle integrateWithParticleCollision(
            Particle p1, PreviousConditions piPrevious, Particle p2, double dt, double KN, double KT, double g){

        double d = Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
        double psi = p1.getRadius() + p2.getRadius() - d;

        double relVelX = p1.getVx() - p2.getVx();
        double relVelY = p1.getVy() - p2.getVy();

        double fn = -KN * psi; // Componente normal

        double cosTita = (p2.getX() - p1.getX()) / d;
        double senTita = (p2.getY() - p1.getY()) / d;
        double ft = -KT * psi * ((relVelX * -senTita) + (relVelY * cosTita));
        double fnx = fn * cosTita;
        double fny = fn * senTita;
        double ftx = ft * -senTita;
        double fty = ft * cosTita;

        double fx = fnx + ftx;
        double fy = fny + fty + (p1.getMass() * g);

        double currentPosX = p1.getX();
        double currentPosY = p1.getY();
        double nextPosX = 2 * currentPosX - piPrevious.x + (Math.pow(dt, 2) / p1.getMass()) * fx;
        double nextPosY = 2 * currentPosY - piPrevious.y + (Math.pow(dt, 2) / p1.getMass()) * fy;
        double nextVelX = (nextPosX - currentPosX) / dt;
        double nextVelY = (nextPosY - currentPosY) / dt;

        Particle retParticle = p1.copy();
        retParticle.setVx(nextVelX);
        retParticle.setVy(nextVelY);
        return retParticle;
    }

    private static boolean isWithinBounds(double position, double boxSize) {
        return position > 0 && position < boxSize;
    }

    private static boolean isInExit(Particle p, double w, double d) {
        return p.getX() > ((w/2) - (d/2)) && p.getX() < ((w/2) + (d/2));
    }

    public class PreviousConditions{
        public double x;
        public double y;

        public PreviousConditions(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

}