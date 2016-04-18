package utils;

import model.Particle;

import java.util.List;

public class CollisionUtils {

    public static CollidingParticleTime getNextParticleCollisionTime(List<Particle> particleList) {
        double nextCollisionTime = -1;
        CollidingParticleTime particleTime = new CollidingParticleTime(-1);

        double[] deltaR, deltaV;
        double sigma, deltaVR, deltaVV, deltaRR, d, collisionTime;
        Particle p, p2;
        for (int i = 0; i < particleList.size(); i++) {
            for (int j = i + 1; j < particleList.size(); j++) {
                p = particleList.get(i);
                p2 = particleList.get(j);
                deltaR = new double[] {p.getX() - p2.getX(), p.getY() - p2.getY()};
                deltaV = new double[] {p.getVx() - p2.getVx(), p.getVy() - p2.getVy()};
                sigma = p.getRadius() + p2.getRadius();

                deltaVR = deltaR[0] * deltaV[0] + deltaR[1] * deltaV[1];

                // Condition 1
                if (deltaVR < 0) {
                    deltaVV = deltaV[0] * deltaV[0] + deltaV[1] * deltaV[1];
                    deltaRR = deltaR[0] * deltaR[0] + deltaR[1] * deltaR[1];

                    d = Math.pow(deltaVR, 2) - deltaVV * (deltaRR - Math.pow(sigma, 2));

                    // Condition 2
                    if (d >= 0) {
                        // Update next collision time
                        collisionTime = - ((deltaVR + Math.sqrt(d))/deltaVV);
                        if (nextCollisionTime < 0 || collisionTime < nextCollisionTime) {
                            nextCollisionTime = collisionTime;
                            particleTime.time = nextCollisionTime;
                            particleTime.p1 = p;
                            particleTime.p2 = p2;
                        }
                    }
                }
            }
        }

        return particleTime;
    }

    public static CollidingParticleTime getNextWallCollisionTime(List<Particle> particleList, float l) {
        double nextCollisionTime = -1;
        CollidingParticleTime particleTime = new CollidingParticleTime(-1);

        double xCollisionTime, yCollisionTime, collisionTime;
        for (Particle p : particleList) {
            if (p.getVx() == 0 && p.getVy() == 0) continue;
            xCollisionTime = p.getVx() > 0 ?
                    (l - p.getRadius() - p.getX()) / p.getVx() : (p.getRadius() - p.getX()) / p.getVx();
            yCollisionTime = p.getVy() > 0 ?
                    (l - p.getRadius() - p.getY()) / p.getVy() : (p.getRadius() - p.getY()) / p.getVy();
            collisionTime = Math.min(xCollisionTime, yCollisionTime);
            if (nextCollisionTime < 0 || collisionTime < nextCollisionTime) {
                nextCollisionTime = collisionTime;
                particleTime.time = nextCollisionTime;
                particleTime.p1 = p;
                particleTime.xCollision = xCollisionTime < yCollisionTime;
            }
        }

        return particleTime;
    }

    public static void collideParticles(CollidingParticleTime collidingParticleTime) {
        if (collidingParticleTime.p1 == null || collidingParticleTime.p2 == null) return;

        double deltaX, deltaY, jX, jY, sigma, deltaVR, J;
        double[] deltaR, deltaV;

        Particle p1 = collidingParticleTime.p1;
        Particle p2 = collidingParticleTime.p2;

        deltaX = p1.getX() - p2.getX();
        deltaY = p1.getY() - p2.getY();

        deltaR = new double[] {deltaX, deltaY};
        deltaV = new double[] {p1.getVx() - p2.getVx(), p1.getVy() - p2.getVy()};

        deltaVR = (deltaR[0] * deltaV[0]) + (deltaR[1] * deltaV[1]);

        sigma = p1.getRadius() + p2.getRadius();

        J = (2 * p1.getMass() * p2.getMass() * deltaVR) / (sigma * (p1.getMass() + p2.getMass()));
        jX = (J * deltaX) / sigma;
        jY = (J * deltaY) / sigma;

        p1.setVx(p1.getVx() - (jX / p1.getMass()));
        p1.setVy(p1.getVy() - (jY / p1.getMass()));
        p2.setVx(p2.getVx() + (jX / p2.getMass()));
        p2.setVy(p2.getVy() + (jY / p2.getMass()));
    }

    public static class CollidingParticleTime {

        double time;
        Particle p1;
        Particle p2;
        boolean xCollision;

        public CollidingParticleTime(double time) {
            this.time = time;
        }

        public double getTime() {
            return time;
        }

        public void setP1(Particle p1) {
            this.p1 = p1;
        }

        public Particle getP1() {
            return p1;
        }

        public void setP2(Particle p2) {
            this.p2 = p2;
        }

        public Particle getP2() {
            return p2;
        }

        public boolean isxCollision() {
            return xCollision;
        }
    }

    public static CollidingParticleTime getNextCollision(List<Particle> particleList, float l) {
        CollisionUtils.CollidingParticleTime nextParticleCollision =
                CollisionUtils.getNextParticleCollisionTime(particleList);
        CollisionUtils.CollidingParticleTime nextWallCollision =
                CollisionUtils.getNextWallCollisionTime(particleList, l);

        return (nextParticleCollision.getTime() != -1
                && nextParticleCollision.getTime() < nextWallCollision.getTime()) ?
                        nextParticleCollision : nextWallCollision;
    }

}
