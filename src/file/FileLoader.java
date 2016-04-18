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
    private static final int MIN_STATIC_MASS_FORMAT = 3;
    private static final int MIN_DYNAMIC_FORMAT = 2;

    private static final float DEFAULT_VELOCITY = 0.03f;
    private static final int SMALL_MASS = 2;

    public static SimulationController loadFiles(
            String staticFile, String dynamicFile, boolean hasMass, float randomizeVelocity) throws IOException {
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
        double radius, color, x, y, mass;
        int id = 1;

        while ((staticLine = staticBr.readLine()) != null
                && (dynamicLine = dynamicBr.readLine()) != null
                && particleList.size() < n) {
            particleStaticInfo = staticLine.trim().split("\\s+");
            particleDynamicInfo = dynamicLine.trim().split("\\s+");

            if (particleStaticInfo.length < MIN_STATIC_FORMAT
                    || (hasMass && particleStaticInfo.length < MIN_STATIC_MASS_FORMAT)
                    || particleDynamicInfo.length < MIN_DYNAMIC_FORMAT) {
                return null;
            }

            radius = Float.valueOf(particleStaticInfo[0]);
            color = Float.valueOf(particleStaticInfo[1]);
            mass = hasMass ? Float.valueOf(particleStaticInfo[2]) : 0;
            x = Float.valueOf(particleDynamicInfo[0]);
            y = Float.valueOf(particleDynamicInfo[1]);

            Particle p = new Particle(id++, radius, color, x, y);
            if (hasMass) p.setMass(mass);

            // Set velocity
            if (randomizeVelocity != 0) {
                p.setVx(p.getMass() < SMALL_MASS ?
                        (Math.random() * randomizeVelocity * 2) - randomizeVelocity : 0);
                p.setVy(p.getMass() < SMALL_MASS ?
                        (Math.random() * randomizeVelocity * 2) - randomizeVelocity : 0);
            } else {
                p.setV(DEFAULT_VELOCITY);
                p.setAngle((float) (Math.random() * 2 * Math.PI));
            }

            particleList.add(p);
        }

        if (particleList.size() < n) return null;
        return new SimulationController(l, particleList);
    }

}
