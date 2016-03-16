package model;

import utils.DistanceUtils;

import java.util.*;

public class SimulationBoard {

    private int l;
    private List<Particle> particleList;

    public SimulationBoard(int l, List<Particle> particleList) {
        this.l = l;
        this.particleList = particleList;
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
