import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChargesVsRegion {
    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : "insurance.csv";

        List<Integer> regionCodes = new ArrayList<>();
        List<Double> charges = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.err.println("File is empty: " + filename);
                return;
            }

            String[] headers = headerLine.split(",");
            int regionIndex = -1, chargesIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                String col = headers[i].trim().toLowerCase();
                if (col.equals("region")) regionIndex = i;
                else if (col.equals("charges")) chargesIndex = i;
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                try {
                    String region = parts[regionIndex].trim().toLowerCase();
                    int code;
                    switch (region) {
                        case "southeast": code = 1; break;
                        case "southwest": code = 2; break;
                        case "northeast": code = 3; break;
                        case "northwest": code = 4; break;
                        default: code = 0; break; 
                    }
                    regionCodes.add(code);
                    charges.add(Double.parseDouble(parts[chargesIndex].trim()));
                } catch (Exception e) {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.printf("Read %d rows of region/charges data.%n", charges.size());

        double meanX = regionCodes.stream().mapToDouble(Double::valueOf).average().orElse(0.0);
        double meanY = charges.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        double numerator = 0.0;
        double denominator = 0.0;
        for (int i = 0; i < regionCodes.size(); i++) {
            numerator += (regionCodes.get(i) - meanX) * (charges.get(i) - meanY);
            denominator += Math.pow(regionCodes.get(i) - meanX, 2);
        }
        double slope = numerator / denominator;
        double intercept = meanY - slope * meanX;

        System.out.printf("Regression line: y = %.2f + %.2f*x%n", intercept, slope);

        double sumNumerator = 0.0;
        double sumXsq = 0.0;
        double sumYsq = 0.0;

        for (int i = 0; i < regionCodes.size(); i++) {
            double dx = regionCodes.get(i) - meanX;
            double dy = charges.get(i) - meanY;
            sumNumerator += dx * dy;
            sumXsq += dx * dx;
            sumYsq += dy * dy;
        }

        double r = sumNumerator / Math.sqrt(sumXsq * sumYsq);
        System.out.printf("Pearson correlation coefficient (r): %.4f%n", r);

        int[] newRegionCodes = {
            1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3,
            4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2,
            3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1
        };

        System.out.println("Predicted charges for new region codes:");
        System.out.printf("%-10s %-15s%n", "RegionCode", "PredictedCharge");
        for (int code : newRegionCodes) {
            double predictedCharge = intercept + slope * code;
            System.out.printf("%-10d %-15.2f%n", code, predictedCharge);
        }
    }
}
