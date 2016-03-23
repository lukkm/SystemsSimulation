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
