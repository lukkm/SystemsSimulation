package model;

import java.util.ArrayList;
import java.util.List;

public class SimulationBoard {

    private int l;
    private List<Particle> particleList;

    public SimulationBoard(int l, List<Particle> particleList) {
        this.l = l;
        this.particleList = particleList;
    }

    public void AddParticles(int m) {
        List<Particle>[][] cells = new List[m][m];

        double cellSize = l / m;

        for (Particle p : particleList) {
            int xCell = (int)Math.floor(p.getX()/cellSize);
            int yCell = (int)Math.floor(p.getY()/cellSize);

            if (cells[xCell][yCell] == null) cells[xCell][yCell] = new ArrayList<>();
            cells[xCell][yCell].add(p);
        }


        for (int i = 0; i < m; i++){
            for(int j = 0; j < m; j++) {
                List<Particle> cellParticles = cells[i][j];
                calculateDistance(cellParticles, cells, i+1, j);
                calculateDistance(cellParticles, cells, i+1, j+1);
                calculateDistance(cellParticles, cells, i, j+1);
                calculateDistance(cellParticles, cells, i+1, j-1);
            }
        }
    }

    private void calculateDistance(List<Particle> particles, List<Particle>[][] cells, int i, int j) {
        if (i < 0 || j < 0 || i >= cells.length || j >= cells.length) return;

        List<Particle> cellParticles = cells[i][j];
        if (cellParticles == null) return;

        for (Particle p : cellParticles) {

        }
    }

}
