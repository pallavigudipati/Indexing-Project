package anomaly_generation;
import java.util.List;
import java.util.Random;

public class Utils {

    public static int randomNumber(int min, int max) {
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
}
