package anomaly_generation;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class Utils {

    public static int randomNumber(int min, int max) {
        if (max == 0 && min == 0) {
            return 0;
        }
        Random randomDouble = new Random();
        return randomDouble.nextInt(max) + min;
    }

    public static void fillRandomNumbers(int min, int max, int num, List<Integer> list) {
        int i = 0;
        while (i < num) {
            Random randomDouble = new Random();
            int random = randomDouble.nextInt(max) + min;
            if (list.contains(random)) {
                continue;
            } else {
                list.add(random);
                i++;
            }
        }
    }

    public static void writeHashMap(HashMap<Integer, List<Integer>> graph, String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            for (Entry<Integer, List<Integer>> entry : graph.entrySet()) {
                int fromVertex = entry.getKey();
                String output = fromVertex + "\t";
                if (entry.getValue() != null) {
                    for (Integer toVertex : entry.getValue()) {
                        writer.write(output + toVertex + "\n");
                    }
                }
            }
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
