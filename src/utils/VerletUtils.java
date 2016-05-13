package utils;

import model.Particle;


public class VerletUtils {

    public static Particle integrateWithoutCollision(Particle pi, PreviousConditions piPrevious, double dt, double g){
        double fy =pi.getMass()*g;
        double currentPosX = pi.getX();
        double currentPosY = pi.getY();
        double nextPosX = 2* currentPosX - piPrevious.x;
        double nextPosY = 2* currentPosY - piPrevious.y + (Math.pow(dt,2)/pi.getMass())* fy;
        double nextVelX = (nextPosX - currentPosX)/dt;
        double nextVelY = (nextPosY -currentPosY)/dt;


        Particle retParticle = new Particle(pi.getId(),pi.getRadius(),pi.getColor(), nextPosX, nextPosY);
        retParticle.setVx(nextVelX);
        retParticle.setVy(nextVelY);
        return  retParticle;

    }

    public static Particle integrateWithParticleCollision(Particle pi, PreviousConditions piPrevious, Particle pj, double dt, double KN, double KT, double g){

        double d = Math.sqrt(Math.pow(pj.getX() - pi.getX(),2) + Math.pow(pj.getY() - pi.getY(),2);;//hipotenusa, modulo vectorial de la posicion
        double psi = pi.getRadius() + pj.getRadius() - d;

        double relVelX = pi.getVx() - pj.getVx();
        double relVelY = pi.getVy() -pj.getVy();

        double fn = -KN * psi;//componente normal

        double cosTita = ( pj.getX()  -pi.getX())/ d;
        double senTita = (pj.getY()-pi.getY())/ d;
        double ft = -KT * psi * ((relVelX * -senTita)+ (relVelY * cosTita)));
        double fnx = fn* cosTita ;
        double fny = fn * senTita ;
        double ftx = ft * (- senTita);
        double fty = ft * cosTita ;

        double fx = fnx + ftx;
        double fy = fny + fty + pi.getMass() * g;

        double currentPosX = pi.getX();
        double currentPosY = pi.getY();
        double nextPosX = 2* currentPosX - piPrevious.x + (Math.pow(dt,2)/pi.getMass())* fx;
        double nextPosY = 2* currentPosY - piPrevious.y + (Math.pow(dt,2)/pi.getMass())* fy;
        double nextVelX = (nextPosX - currentPosX)/dt;
        double nextVelY = (nextPosY -currentPosY)/dt;


        Particle retParticle = new Particle(pi.getId(),pi.getRadius(),pi.getColor(), nextPosX, nextPosY);
        retParticle.setVx(nextVelX);
        retParticle.setVy(nextVelY);
        return  retParticle;

    }

    public class PreviousConditions{
        public float x;
        public float y;

    }

}