package model;

import java.util.List;

public class SimulationBoard {

    private int l;
    private List<Particle> particleList;

    public SimulationBoard(int l, List<Particle> particleList) {
        this.l = l;
        this.particleList = particleList;
    }

}
