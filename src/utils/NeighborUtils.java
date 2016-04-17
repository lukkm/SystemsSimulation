package utils;

import model.Particle;

import java.util.*;

public class NeighborUtils {

    public static List<Particle> calculateBruteForceNeighbors(Particle p, List<Particle> stepParticles) {
        List<Particle> neighbors = new ArrayList<>();
        for (Particle p2 : stepParticles) {
            if (p2 != p && DistanceUtils.calculateDistance(p, p2) < p.getRadius()) neighbors.add(p2);
        }
        return neighbors;
    }

    public static void calculateNeighbors(
            List<Particle> particles, Map<Particle,
            Set<Particle>> particleSetMap,
            List<Particle>[][] cells,
            float l,
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

    public static boolean isNeighbor(Particle p1, Particle p2, Map<Particle, Set<Particle>> particleSetMap) {
        return (particleSetMap.containsKey(p1) && particleSetMap.get(p1).contains(p2))
                || (particleSetMap.containsKey(p2) && particleSetMap.get(p2).contains(p1));
    }

    public static int normalize(int position, int size) {
        if (position < 0) return position + size;
        return position % size;
    }

}
