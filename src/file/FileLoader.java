package file;

import model.Particle;
import controller.SimulationController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {

    private static final int MIN_STATIC_FORMAT = 2;
    private static final int MIN_DYNAMIC_FORMAT = 2;

    private static final float DEFAULT_VELOCITY = 0.03f;

    public static SimulationController loadFiles(String staticFile, String dynamicFile) throws IOException {
        BufferedReader staticBr = new BufferedReader(new FileReader(staticFile));
        BufferedReader dynamicBr = new BufferedReader(new FileReader(dynamicFile));

        String staticLine = staticBr.readLine();
        String dynamicLine = dynamicBr.readLine();
        if (staticLine == null || dynamicLine == null) return null;

        int n = Integer.valueOf(staticLine.trim());
        int t0 = Integer.valueOf(dynamicLine.trim());

        staticLine = staticBr.readLine();
        if (staticLine == null) return null;

        float l = Float.valueOf(staticLine.trim());

        List<Particle> particleList = new ArrayList<>();
        String[] particleStaticInfo;
        String[] particleDynamicInfo;
        double radius, color, x, y;
        int id = 1;

        while ((staticLine = staticBr.readLine()) != null
                && (dynamicLine = dynamicBr.readLine()) != null
                && particleList.size() < n) {
            particleStaticInfo = staticLine.trim().split("\\s+");
            particleDynamicInfo = dynamicLine.trim().split("\\s+");

            if (particleStaticInfo.length < MIN_STATIC_FORMAT || particleDynamicInfo.length < MIN_DYNAMIC_FORMAT) {
                return null;
            }

            radius = Float.valueOf(particleStaticInfo[0]);
            color = Float.valueOf(particleStaticInfo[1]);
            x = Float.valueOf(particleDynamicInfo[0]);
            y = Float.valueOf(particleDynamicInfo[1]);

            float v = DEFAULT_VELOCITY;
            float angle = (float) (Math.random() * 2 * Math.PI);

            Particle p = new Particle(id++, radius, color, x, y);
            p.setV(v);
            p.setAngle(angle);
            particleList.add(p);
        }

        if (particleList.size() < n) return null;
        return new SimulationController(l, particleList);
    }

}
