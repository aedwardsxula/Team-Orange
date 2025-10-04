import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BmiByRegion {
    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : "insurance.csv";

        List<Double> bmis = new ArrayList<>();
        List<String> regions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.err.println("File is empty: " + filename);
                return;
            }

            String[] headers = headerLine.split(",");
            int bmiIndex = -1, regionIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                String col = headers[i].trim().toLowerCase();
                if (col.equals("bmi")) bmiIndex = i;
                else if (col.equals("region")) regionIndex = i;
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                try {
                    double bmi = Double.parseDouble(parts[bmiIndex].trim());
                    String region = parts[regionIndex].trim().toLowerCase();
                    bmis.add(bmi);
                    regions.add(region);
                } catch (Exception e) {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.printf("Read %d rows of BMI/region data.%n", bmis.size());

        List<Double> southBMIs = new ArrayList<>();
        List<Double> otherBMIs = new ArrayList<>();

        for (int i = 0; i < bmis.size(); i++) {
            String region = regions.get(i).toLowerCase();
            if (region.contains("south")) {
                southBMIs.add(bmis.get(i));
            } else {
                otherBMIs.add(bmis.get(i));
            }
        }

        System.out.printf("South BMIs: %d%n", southBMIs.size());
        System.out.printf("Other BMIs: %d%n", otherBMIs.size());

        double avgSouth = southBMIs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgOther = otherBMIs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        System.out.printf("Average BMI (South): %.2f%n", avgSouth);
        System.out.printf("Average BMI (Other): %.2f%n", avgOther);

        boolean isHigher = avgSouth > avgOther;
        System.out.println("Do Southerners have a higher average BMI than others? " + isHigher);
    }
}
