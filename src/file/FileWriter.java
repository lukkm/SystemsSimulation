package file;

import model.Particle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

public class FileWriter {

    public static void printToFile(String file, Map<Particle, Set<Particle>> particleSetMap) throws IOException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");

        StringBuilder builder;
        for (Particle p : particleSetMap.keySet()) {
            builder = new StringBuilder();
            builder.append(p.getId());
            builder.append(", ");
            for (Particle p2 : particleSetMap.get(p)) {
                builder.append(p2.getId());
                builder.append(", ");
            }
            builder.setLength(builder.length() - 2);
            writer.println(builder.toString());
        }

        writer.close();
    }

}
