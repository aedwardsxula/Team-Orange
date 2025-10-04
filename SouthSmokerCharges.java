import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SouthSmokerCharges {
    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : "insurance.csv";

        List<String> smokers = new ArrayList<>();
        List<String> regions = new ArrayList<>();
        List<Double> charges = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.err.println("File is empty: " + filename);
                return;
            }

            String[] headers = headerLine.split(",");
            int smokerIndex = -1, regionIndex = -1, chargesIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                String col = headers[i].trim().toLowerCase();
                if (col.equals("smoker")) smokerIndex = i;
                else if (col.equals("region")) regionIndex = i;
                else if (col.equals("charges")) chargesIndex = i;
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                try {
                    smokers.add(parts[smokerIndex].trim().toLowerCase());
                    regions.add(parts[regionIndex].trim().toLowerCase());
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

        List<Double> southSmokerCharges = new ArrayList<>();
        List<Double> otherSmokerCharges = new ArrayList<>();

        for (int i = 0; i < smokers.size(); i++) {
            if (!smokers.get(i).equals("yes")) continue; 
            if (regions.get(i).contains("south")) southSmokerCharges.add(charges.get(i));
            else otherSmokerCharges.add(charges.get(i));
        }

        double avgSouthSmokers = southSmokerCharges.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgOtherSmokers = otherSmokerCharges.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        System.out.printf("Average charges (South smokers): %.2f%n", avgSouthSmokers);
        System.out.printf("Average charges (Other smokers): %.2f%n", avgOtherSmokers);

        boolean isAtLeast25PercentHigher = avgSouthSmokers >= 1.25 * avgOtherSmokers;
        System.out.println("Are South smokers charged at least 25% more? " + isAtLeast25PercentHigher);
    }
}
