package file;

import model.Particle;
import model.SimulationBoard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {

    public static SimulationBoard loadStaticFile(String file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line == null) return null;

            int n = Integer.valueOf(line);

            line = br.readLine();
            if (line == null) return null;

            int l = Integer.valueOf(line);

            List<Particle> particleList = new ArrayList<>();
            String[] particleInfo;
            float radius, color;

            while ((line = br.readLine()) != null && particleList.size() < n) {
                particleInfo = line.split(" ");
                if (particleInfo.length < 2) return null;

                radius = Float.valueOf(particleInfo[0]);
                color = Float.valueOf(particleInfo[1]);

                particleList.add(new Particle(radius, color));
            }

            if (particleList.size() < n) return null;
            return new SimulationBoard(l, particleList);
        }
    }

    public static SimulationBoard loadDynamicFile() {
        return null;
    }

}
