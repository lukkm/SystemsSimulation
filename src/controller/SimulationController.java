package controller;

import java.util.ArrayList;
import java.util.List;

import model.Particle;
import utils.DistanceUtils;

import java.util.*;

public class SimulationController {

    private float l;
    private List<Particle> particleList;

    public SimulationController(float l, List<Particle> particleList) {
        this.l = l;
        this.particleList = particleList;
    }

    public List<List<Particle>> simulateSteps(int steps, double dt, double n) {
        double newX, newY, newAngle;

        List<List<Particle>> particleSteps = new ArrayList<>();
        particleSteps.add(particleList);

        for (int i = 0; i < steps; i++) {
            List<Particle> newStep = new ArrayList<>();
            for (Particle p : particleList) {
                Particle newParticle = p.copy();
                double angleAverage = getAngleAverage(p);
                newX = (p.getX() + p.getVx() * dt) % l;
                newY = (p.getY() + p.getVy() * dt) % l;
                newAngle = angleAverage + (Math.random() * n - n/2);

                newParticle.setX(newX);
                newParticle.setY(newY);
                newParticle.setAngle(newAngle);

                newStep.add(newParticle);
            }
            particleSteps.add(newStep);
        }

        return particleSteps;
    }

    private double getAngleAverage(Particle p) {
        List<Particle> particles = calculateBruteForceNeighbors(p);
        double sinSum = 0;
        double cosSum = 0;
        for (Particle p2 : particles) {
            sinSum += Math.sin(p2.getAngle());
            cosSum += Math.cos(p2.getAngle());
        }
        sinSum += Math.sin(p.getAngle());
        cosSum += Math.cos(p.getAngle());

        return Math.atan((sinSum / (particles.size() + 1)) / (cosSum / (particles.size() + 1)));
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

                calculateNeighbors(cellParticles, closeParticles, cells, i, j, rc, crossMap);
                calculateNeighbors(cellParticles, closeParticles, cells, i + 1, j, rc, crossMap);
                calculateNeighbors(cellParticles, closeParticles, cells, i + 1, j + 1, rc, crossMap);
                calculateNeighbors(cellParticles, closeParticles, cells, i, j + 1, rc, crossMap);
                calculateNeighbors(cellParticles, closeParticles, cells, i + 1, j - 1, rc, crossMap);
            }
        }

        return closeParticles;
    }

    private void calculateNeighbors(
            List<Particle> particles, Map<Particle,
            Set<Particle>> particleSetMap,
            List<Particle>[][] cells,
            int i,
            int j,
            float rc,
            boolean crossMap) {
        if (particles == null) return;

        List<Particle> cellParticles = cells[normalize(i, cells.length)][normalize(j, cells.length)];
        boolean crossX = false;
        boolean crossY = false;
        if (i < 0 || i >= cells.length) {
            if (!crossMap) return;
            crossX = true;
        }

        if (j < 0 || j >= cells.length) {
            if (!crossMap) return;
            crossY = true;
        }

        if (cellParticles == null) return;

        for (Particle p1 : particles) {
            if (!particleSetMap.containsKey(p1)) particleSetMap.put(p1, new HashSet<>());
            for (Particle p2 : cellParticles) {
                if (!particleSetMap.containsKey(p2)) particleSetMap.put(p2, new HashSet<>());
                if (p1 != p2) {
                    if (isNeighbor(p1, p2, particleSetMap)
                            || DistanceUtils.calculateDistance(p1, p2, crossX, crossY, l) < rc) {
                        particleSetMap.get(p1).add(p2);
                        particleSetMap.get(p2).add(p1);
                    }
                }
            }
        }
    }

    private List<Particle> calculateBruteForceNeighbors(Particle p) {
        List<Particle> neighbors = new ArrayList<>();
        for (Particle p2 : particleList) {
            if (p2 != p && DistanceUtils.calculateDistance(p, p2) < p.getRadius()) neighbors.add(p2);
        }
        return neighbors;
    }

    public Map<Particle, Set<Particle>> calculateBruteForceDistance(float rc) {
        Map<Particle, Set<Particle>> closeParticles = new LinkedHashMap<>();
        for (Particle p1 : particleList) {
            closeParticles.put(p1, new HashSet<>());
            for (Particle p2 : particleList) {
                if (p1 != p2) {
                    if (isNeighbor(p1, p2, closeParticles) || DistanceUtils.calculateDistance(p1, p2) < rc)
                        closeParticles.get(p1).add(p2);
                }
            }
        }
        return closeParticles;
    }

    private boolean isNeighbor(Particle p1, Particle p2, Map<Particle, Set<Particle>> particleSetMap) {
        return (particleSetMap.containsKey(p1) && particleSetMap.get(p1).contains(p2))
                || (particleSetMap.containsKey(p2) && particleSetMap.get(p2).contains(p1));
    }

    private int normalize(int position, int size) {
        if (position < 0) return position + size;
        return position % size;
    }

}
