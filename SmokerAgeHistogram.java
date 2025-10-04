import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SmokerAgeHistogram {
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

        List<Integer> smokerAges = new ArrayList<>();
        for (int i = 0; i < ages.size(); i++) {
            if (smokers.get(i).equals("yes")) {
                smokerAges.add(ages.get(i));
            }
        }

        System.out.printf("Found %d smokers with recorded ages.%n", smokerAges.size());

        int minAge = smokerAges.stream().min(Integer::compare).orElse(0);
        int maxAge = smokerAges.stream().max(Integer::compare).orElse(0);
        int numBins = 10;
        int binWidth = (int) Math.ceil((double)(maxAge - minAge) / numBins);

        int[] counts = new int[numBins];
        for (int age : smokerAges) {
            int bin = (age - minAge) / binWidth;
            if (bin == numBins) bin--;
            counts[bin]++;
        }

        int maxCount = 0;
        for (int c : counts) if (c > maxCount) maxCount = c;

        for (int level = maxCount; level > 0; level--) {
            for (int c : counts) {
                if (c >= level) System.out.printf("%-5s", "*");
                else System.out.printf("%-5s", " ");
            }
            System.out.println();
        }

        for (int i = 0; i < numBins; i++) {
            int lower = minAge + i * binWidth;
            System.out.printf("%-5d", lower);
        }
        System.out.println();
    }
}
