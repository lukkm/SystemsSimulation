package model;

import java.util.ArrayList;
import java.util.List;
import utils.DistanceUtils;

import java.util.*;

public class SimulationBoard {

    private int l;
    private List<Particle> particleList;

    public SimulationBoard(int l, List<Particle> particleList) {
        this.l = l;
        this.particleList = particleList;
    }

    public Map<Particle, Set<Particle>> calculateDistance(int m, float rc) {
        Map<Particle, Set<Particle>> closeParticles = new LinkedHashMap<>();
        List<Particle>[][] cells = new List[m][m];

        double cellSize = (double)l / m;

        for (Particle p : particleList) {
            int xCell = (int)Math.floor(p.getX()/cellSize);
            int yCell = (int)Math.floor(p.getY()/cellSize);

            if (cells[xCell][yCell] == null) cells[xCell][yCell] = new ArrayList<>();
            cells[xCell][yCell].add(p);
        }


        for (int i = 0; i < m; i++){
            for(int j = 0; j < m; j++) {
                List<Particle> cellParticles = cells[i][j];

                calculateNeighbors(cellParticles, closeParticles, cells, i, j, rc);
                calculateNeighbors(cellParticles, closeParticles, cells, i + 1, j, rc);
                calculateNeighbors(cellParticles, closeParticles, cells, i + 1, j + 1, rc);
                calculateNeighbors(cellParticles, closeParticles, cells, i, j + 1, rc);
                calculateNeighbors(cellParticles, closeParticles, cells, i + 1, j - 1, rc);
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
            float rc) {
        if (particles == null) return;
        if (i < 0 || j < 0 || i >= cells.length || j >= cells.length) return;

        List<Particle> cellParticles = cells[i][j];
        if (cellParticles == null) return;

        for (Particle p1 : particles) {
            if (!particleSetMap.containsKey(p1)) particleSetMap.put(p1, new HashSet<>());
            for (Particle p2 : cellParticles) {
                if (!particleSetMap.containsKey(p2)) particleSetMap.put(p2, new HashSet<>());
                if (p1 != p2) {
                    if (isNeighbor(p1, p2, particleSetMap) || DistanceUtils.calculateDistance(p1, p2) < rc) {
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

}
