package controller;

import java.util.ArrayList;
import java.util.List;

import model.Particle;
import utils.AngleUtils;
import utils.CollisionUtils;
import utils.NeighborUtils;

import java.util.*;

public class SimulationController {

    private float l;
    private List<Particle> particleList;

    public SimulationController(float l, List<Particle> particleList) {
        this.l = l;
        this.particleList = particleList;
    }

    public List<Particle> getParticleList() {
        return particleList;
    }

    public List<List<Particle>> simulateCollisions(int steps) {
        double dt = 0.1;
        List<List<Particle>> particleSteps = new ArrayList<>();
        List<Particle> lastStep = particleList;
        particleSteps.add(particleList);
        for (int i = 0; i < steps; i++) {
            CollisionUtils.CollidingParticleTime nextParticleCollision =
                    CollisionUtils.getNextParticleCollisionTime(lastStep);
            CollisionUtils.CollidingParticleTime nextWallCollision =
                    CollisionUtils.getNextWallCollisionTime(lastStep, l);

            CollisionUtils.CollidingParticleTime nextCollision =
                    (nextParticleCollision.getTime() != -1
                            && nextParticleCollision .getTime() < nextWallCollision.getTime()) ?
                            nextParticleCollision : nextWallCollision;

            if (nextCollision.getTime() > dt) {
                // Evolve particles
                List<Particle> newStep = new ArrayList<>();
                for (Particle p : lastStep) {
                    Particle newParticle = p.copy();
                    newParticle.setX(p.getX() + p.getVx() * dt);
                    newParticle.setY(p.getY() + p.getVy() * dt);
                    newStep.add(newParticle);
                }

                particleSteps.add(newStep);
                lastStep = newStep;
            } else {
                // Evolve particles
                List<Particle> newStep = new ArrayList<>();
                for (Particle p : lastStep) {
                    Particle newParticle = p.copy();
                    newParticle.setX(p.getX() + p.getVx() * nextCollision.getTime());
                    newParticle.setY(p.getY() + p.getVy() * nextCollision.getTime());

                    // Set the new particles to be updated
                    if (p.getId() == nextCollision.getP1().getId()) {
                        nextCollision.setP1(newParticle);
                    }
                    if (nextCollision.getP2() != null && p.getId() == nextCollision.getP2().getId()) {
                        nextCollision.setP2(newParticle);
                    }

                    newStep.add(newParticle);
                }

                // Collide!
                if (nextCollision.getP2() == null) {
                    // Wall collision
                    Particle p = nextCollision.getP1();
                    if (nextCollision.isxCollision()) {
                        p.setVx(-p.getVx());
                    } else {
                        p.setVy(-p.getVy());
                    }
                } else {
                    // Particle collision
                    CollisionUtils.collideParticles(nextParticleCollision);
                }

                particleSteps.add(newStep);
                lastStep = newStep;
            }
        }

        return particleSteps;
    }

    public List<List<Particle>> simulateSteps(int steps, double dt, double n) {
        double newX, newY, newAngle;

        List<List<Particle>> particleSteps = new ArrayList<>();
        particleSteps.add(particleList);

        List<Particle> lastStep = particleList;

        for (int i = 0; i < steps; i++) {
            List<Particle> newStep = new ArrayList<>();
            for (Particle p : lastStep) {
                Particle newParticle = p.copy();
                double angleAverage = AngleUtils.getAngleAverage(p, lastStep);
                newX = ((p.getX() + p.getVy() * dt) + l) % l;
                newY = ((p.getY() + p.getVx() * dt) + l) % l;
                newAngle = angleAverage + (Math.random() * n - n/2);

                newParticle.setX(newX);
                newParticle.setY(newY);
                newParticle.setAngle(newAngle);

                newStep.add(newParticle);
            }
            particleSteps.add(newStep);
            lastStep = newStep;
        }

        return particleSteps;
    }

    @SuppressWarnings("unchecked")
    public Map<Particle, Set<Particle>> calculateDistance(int m, float rc, boolean crossMap) {
        Map<Particle, Set<Particle>> closeParticles = new LinkedHashMap<>();
        List<Particle>[][] cells = new List[m][m];

        double cellSize = (double)l / m;

        for (Particle p : particleList) {
            int xCell = (int)Math.floor(p.getX()/cellSize);
            int yCell = (int)Math.floor(p.getY()/cellSize);

            if (xCell > 0 && xCell < cells.length && yCell > 0 && yCell < cells.length) {
                if (cells[xCell][yCell] == null) cells[xCell][yCell] = new ArrayList<>();
                cells[xCell][yCell].add(p);
            }
        }


        for (int i = 0; i < m; i++){
            for(int j = 0; j < m; j++) {
                List<Particle> cellParticles = cells[i][j];

                NeighborUtils.calculateNeighbors(cellParticles, closeParticles, cells, l, i, j, rc, crossMap);
                NeighborUtils.calculateNeighbors(cellParticles, closeParticles, cells, l, i + 1, j, rc, crossMap);
                NeighborUtils.calculateNeighbors(cellParticles, closeParticles, cells, l, i + 1, j + 1, rc, crossMap);
                NeighborUtils.calculateNeighbors(cellParticles, closeParticles, cells, l, i, j + 1, rc, crossMap);
                NeighborUtils.calculateNeighbors(cellParticles, closeParticles, cells, l, i + 1, j - 1, rc, crossMap);
            }
        }

        return closeParticles;
    }

}
