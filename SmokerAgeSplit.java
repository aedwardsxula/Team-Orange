import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SmokerAgeSplit {
    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : "insurance.csv";

        List<Integer> ages = new ArrayList<>();
        List<String> smokers = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.err.println("File is empty: " + filename);
                return;
            }

            String[] headers = headerLine.split(",");
            int ageIndex = -1, smokerIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                String col = headers[i].trim().toLowerCase();
                if (col.equals("age")) ageIndex = i;
                else if (col.equals("smoker")) smokerIndex = i;
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                try {
                    int age = Integer.parseInt(parts[ageIndex].trim());
                    String smoker = parts[smokerIndex].trim().toLowerCase();
                    ages.add(age);
                    smokers.add(smoker);
                } catch (Exception e) {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        Integer cutoffAnswer = null;

        for (int cutoff = 20; cutoff <= 60; cutoff++) {
            int youngCount = 0, youngSmokers = 0;
            int oldCount = 0, oldSmokers = 0;

            for (int i = 0; i < ages.size(); i++) {
                int age = ages.get(i);
                String smoker = smokers.get(i);
                if (age < cutoff) {
                    youngCount++;
                    if (smoker.equals("yes")) youngSmokers++;
                } else {
                    oldCount++;
                    if (smoker.equals("yes")) oldSmokers++;
                }
            }

            double youngRate = youngCount > 0 ? (100.0 * youngSmokers / youngCount) : 0.0;
            double oldRate = oldCount > 0 ? (100.0 * oldSmokers / oldCount) : 0.0;

            if (youngRate > oldRate) {
                cutoffAnswer = cutoff;
                break;
            }
        }

        if (cutoffAnswer != null) {
            System.out.printf("Yes. Young people smoke more than older people starting at age cutoff %d.%n", cutoffAnswer);
        } else {
            System.out.println("No. There is no age cutoff where young people smoke more than older people.");
        }
    }
}
