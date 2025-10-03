import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ChargesPerChild {
    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : "insurance.csv";

        List<Integer> childrenList = new ArrayList<>();
        List<Double> chargesList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.err.println("File is empty: " + filename);
                return;
            }

            String[] headers = headerLine.split(",");
            int childrenIndex = -1, chargesIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                String col = headers[i].trim().toLowerCase();
                if (col.equals("children")) childrenIndex = i;
                else if (col.equals("charges")) chargesIndex = i;
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                try {
                    int children = Integer.parseInt(parts[childrenIndex].trim());
                    double charge = Double.parseDouble(parts[chargesIndex].trim());
                    childrenList.add(children);
                    chargesList.add(charge);
                } catch (Exception e) {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.printf("Read %d rows of children/charges data.%n", childrenList.size());

        Map<Integer, List<Double>> chargesByChildren = new TreeMap<>();
        for (int i = 0; i < childrenList.size(); i++) {
            int numChildren = childrenList.get(i);
            double charge = chargesList.get(i);
            chargesByChildren.putIfAbsent(numChildren, new ArrayList<>());
            chargesByChildren.get(numChildren).add(charge);
        }

        System.out.println("Children  AvgCharge  AvgChargePerChild");

        List<Double> avgPerChildList = new ArrayList<>();
        List<Integer> childCounts = new ArrayList<>();
        for (Map.Entry<Integer, List<Double>> entry : chargesByChildren.entrySet()) {
            int numChildren = entry.getKey();
            List<Double> charges = entry.getValue();
            double totalCharge = charges.stream().mapToDouble(Double::doubleValue).sum();
            double avgCharge = totalCharge / charges.size();
            double avgChargePerChild = numChildren > 0 ? totalCharge / (numChildren * charges.size()) : 0.0;
            System.out.printf("%8d  %9.2f  %15.2f%n", numChildren, avgCharge, avgChargePerChild);

            if (numChildren > 0) {
                avgPerChildList.add(avgChargePerChild);
                childCounts.add(numChildren);
            }
        }

        boolean strictlyDecreasing = true;
        for (int i = 1; i < avgPerChildList.size(); i++) {
            if (avgPerChildList.get(i) >= avgPerChildList.get(i - 1)) {
                strictlyDecreasing = false;
                break;
            }
        }

        System.out.println("Do people with more children have lower charges per child? " + strictlyDecreasing);
    }
}
