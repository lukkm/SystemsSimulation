package model;

import java.util.List;

public class SimulationBoard {

    private Particle[][] board;
    private List<Particle> particleList;

    public SimulationBoard(int l, List<Particle> particleList) {
        board = new Particle[l][l];
        this.particleList = particleList;
    }

}
