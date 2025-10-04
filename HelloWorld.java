import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HelloWorld {
    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : "insurance.csv";

        List<Double> bmis = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.err.println("File is empty: " + filename);
                return;
            }

            String[] headers = headerLine.split(",");
            int bmiIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase("bmi")) {
                    bmiIndex = i;
                    break;
                }
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                try {
                    bmis.add(Double.parseDouble(parts[bmiIndex].trim()));
                } catch (Exception e) {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        double min = bmis.stream().min(Double::compare).orElse(0.0);
        double max = bmis.stream().max(Double::compare).orElse(0.0);
        int numBins = 10;
        double binWidth = (max - min) / numBins;

        int[] counts = new int[numBins];
        for (double bmi : bmis) {
            int bin = (int) ((bmi - min) / binWidth);
            if (bin == numBins) bin--; 
            counts[bin]++;
        }

        int maxCount = 0;
        for (int count : counts) {
            if (count > maxCount) maxCount = count;
        }

        for (int level = maxCount; level > 0; level--) {
            for (int count : counts) {
                if (count >= level) {
                    System.out.printf("%-10s", "*"); // wider column for full bin ranges
                } else {
                    System.out.printf("%-10s", " ");
                }
            }
            System.out.println();
        }

        for (int i = 0; i < numBins; i++) {
            double lower = min + i * binWidth;
            double upper = lower + binWidth;
            System.out.printf("%-10s", String.format("[%.1f-%.1f]", lower, upper));
        }
        System.out.println();

         // === Problem 3: Horizontal Histogram of Age ===
        System.out.println("=== Horizontal Histogram of Age ===");
        
        List<InsuranceRecord> allRecords = getFirstNRecords(filename, Integer.MAX_VALUE);

        List<Integer> ages = new ArrayList<>();
        for (InsuranceRecord r : allRecords) {
            ages.add(r.age);
        }

        int minAge = Collections.min(ages);
        int maxAge = Collections.max(ages);
        int ageBins = 8;
        int ageBinWidth = (int) Math.ceil((maxAge - minAge + 1) / (double) ageBins);

        int[] ageCounts = new int[ageBins];
        for (int age : ages) {
            int binIndex = (age - minAge) / ageBinWidth;
            if (binIndex >= ageBins) binIndex = ageBins - 1;
            ageCounts[binIndex]++;
        }

        for (int i = 0; i < ageBins; i++) {
            int binStart = minAge + i * ageBinWidth;
            int binEnd = binStart + ageBinWidth - 1;
            System.out.printf("%2d–%2d | ", binStart, binEnd);
            for (int j = 0; j < ageCounts[i]; j++) {
                System.out.print("*");
            }
            System.out.println(" (" + ageCounts[i] + ")");
        }
        // === Problem 5: Count Records by Number of Children ===
        countChildrenRecords(allRecords);
        // Problem 7: Check Region Fairness
        checkRegionFairness(allRecords);
        // Problem 9 : Compare Charge Ranges by BMI Groups
        compareChargeRangesByBMI(allRecords);
        // === Problem 11: Smokers vs Non-Smokers Charges ===
        analyzeSmokerCharges(allRecords);
        // Problem 13: Do smokers average lower BMI than non-smokers?
        compareSmokerBMI(allRecords);
        // === Problem 15: Sort Regions by Average Charges Descending ===
        sortRegionsByAverageCharges(allRecords);
        // === Problem 17: Southerners vs Northerners Smoking Rate and Average Age ===
        compareSmokingByRegion(allRecords);
        // === Problem 19: Do southerners average more children than northerners? At what average age? ===
        compareChildrenByRegion(allRecords);
        // === Problem 21: Linear Regression of Charges vs Children ===
        regressionChargesVsChildren(allRecords);









    }    
    // === Problem 1: Get First N Records ===
    public static List<InsuranceRecord> getFirstNRecords(String filename, int N) {
        List<InsuranceRecord> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String header = br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null && records.size() < N) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length != 7) continue;
                try {
                    InsuranceRecord record = new InsuranceRecord(parts);
                    records.add(record);
                } catch (Exception e) {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return records;
    };
    
    // === Problem 5: Count Records by Number of Children ===
    public static void countChildrenRecords(List<InsuranceRecord> records) {
        Map<Integer, Integer> childrenCount = new TreeMap<>(); // TreeMap keeps keys sorted

        for (InsuranceRecord record : records) {
            int numChildren = record.children;
            childrenCount.put(numChildren, childrenCount.getOrDefault(numChildren, 0) + 1);
        }

        System.out.println("=== Number of Records per Number of Children ===");
        for (Map.Entry<Integer, Integer> entry : childrenCount.entrySet()) {
            System.out.printf("Children: %d => %d record%s\n",
                    entry.getKey(),
                    entry.getValue(),
                    entry.getValue() == 1 ? "" : "s");
        }
    } 
    // Problem 7: Check Region Fairness
    public static void checkRegionFairness(List<InsuranceRecord> records) {
        Map<String, Integer> regionCounts = new TreeMap<>();
    
        for (InsuranceRecord record : records) {
            String region = record.region.toLowerCase().trim();
            regionCounts.put(region, regionCounts.getOrDefault(region, 0) + 1);
        }
    
        int totalRecords = records.size();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
    
        for (int count : regionCounts.values()) {
            if (count < min) min = count;
            if (count > max) max = count;
        }
    
        double threshold = totalRecords * 0.05;
        boolean isFair = (max - min) <= threshold;
    
        System.out.println("=== Region Fairness Check ===");
        for (Map.Entry<String, Integer> entry : regionCounts.entrySet()) {
            System.out.printf("Region: %-12s => %d records\n", entry.getKey(), entry.getValue());
        }
        System.out.printf("Max-Min Difference: %d\n", (max - min));
        System.out.printf("Allowed Threshold (5%% of %d): %.2f\n", totalRecords, threshold);
        System.out.println("Is data fair? " + (isFair ? "✅ YES" : "❌ NO"));
    }
    // Problem 9 : Compare Charge Ranges by BMI Groups
    public static void compareChargeRangesByBMI(List<InsuranceRecord> records) {
        List<Double> chargesUnder30 = new ArrayList<>();
        List<Double> charges30to45 = new ArrayList<>();
        List<Double> chargesOver45 = new ArrayList<>();
    
        for (InsuranceRecord record : records) {
            double bmi = record.bmi;
            double charge = record.charges;
    
            if (bmi < 30) {
                chargesUnder30.add(charge);
            } else if (bmi <= 45) {
                charges30to45.add(charge);
            } else {
                chargesOver45.add(charge);
            }
        }
    
        double rangeUnder30 = calculateRange(chargesUnder30);
        double range30to45 = calculateRange(charges30to45);
        double rangeOver45 = calculateRange(chargesOver45);
    
        System.out.println("=== Problem 9: Charges Range by BMI Groups ===");
        System.out.printf("BMI < 30      -> Range: %.2f\n", rangeUnder30);
        System.out.printf("BMI 30–45     -> Range: %.2f\n", range30to45);
        System.out.printf("BMI > 45      -> Range: %.2f\n", rangeOver45);
    
        boolean isGroupBGreatest = range30to45 > rangeUnder30 && range30to45 > rangeOver45;
        System.out.println("Is BMI 30–45 range the greatest? " + (isGroupBGreatest ? "✅ YES" : "❌ NO"));
    }
    
    private static double calculateRange(List<Double> charges) {
        if (charges.isEmpty()) return 0.0;
    
        double min = Collections.min(charges);
        double max = Collections.max(charges);
        return max - min;
    }
    // === Problem 11: Analyze Charges for Smokers vs Non-Smokers ===
    public static void analyzeSmokerCharges(List<InsuranceRecord> records) {
        List<Double> smokerCharges = new ArrayList<>();
        List<Double> nonSmokerCharges = new ArrayList<>();

        for (InsuranceRecord record : records) {
            if (record.smoker) {
                smokerCharges.add(record.charges);
            } else {
                nonSmokerCharges.add(record.charges);
            }
            
        }

        double avgSmoker = average(smokerCharges);
        double avgNonSmoker = average(nonSmokerCharges);
        double rangeSmoker = range(smokerCharges);
        double rangeNonSmoker = range(nonSmokerCharges);

        System.out.println("=== Problem 11: Smokers vs Non-Smokers Charges ===");
        System.out.printf("Smokers:     Count = %d, Avg = %.2f, Range = %.2f\n", smokerCharges.size(), avgSmoker, rangeSmoker);
        System.out.printf("Non-Smokers: Count = %d, Avg = %.2f, Range = %.2f\n", nonSmokerCharges.size(), avgNonSmoker, rangeNonSmoker);

        boolean higherAvg = avgSmoker > avgNonSmoker;
        boolean widerRange = rangeSmoker > rangeNonSmoker;

        System.out.println("Do smokers have higher average charges? " + (higherAvg ? "✅ Yes" : "❌ No"));
        System.out.println("Do smokers have a wider range of charges? " + (widerRange ? "✅ Yes" : "❌ No"));
    }

    // Helper: Calculate average
    public static double average(List<Double> values) {
        if (values.isEmpty()) return 0.0;
        double sum = 0.0;
        for (double v : values) sum += v;
        return sum / values.size();
    }

    // Helper: Calculate range (max - min)
    public static double range(List<Double> values) {
        if (values.isEmpty()) return 0.0;
        double min = Collections.min(values);
        double max = Collections.max(values);
        return max - min;
    }
    // Problem 13: Do smokers average lower BMI than non-smokers?
    public static void compareSmokerBMI(List<InsuranceRecord> records) {
        double smokerBMISum = 0;
        int smokerCount = 0;
        double nonSmokerBMISum = 0;
        int nonSmokerCount = 0;

        for (InsuranceRecord record : records) {
            if (record.smoker) {  // smoker is boolean
                smokerBMISum += record.bmi;
                smokerCount++;
            } else {
                nonSmokerBMISum += record.bmi;
                nonSmokerCount++;
            }
        }

        double avgSmokerBMI = smokerCount > 0 ? smokerBMISum / smokerCount : 0;
        double avgNonSmokerBMI = nonSmokerCount > 0 ? nonSmokerBMISum / nonSmokerCount : 0;

        System.out.printf("Average BMI for smokers: %.2f\n", avgSmokerBMI);
        System.out.printf("Average BMI for non-smokers: %.2f\n", avgNonSmokerBMI);
        System.out.println("Do smokers average lower BMI than non-smokers? " + (avgSmokerBMI < avgNonSmokerBMI ? "✅ YES" : "❌ NO"));
    }
    // === Problem 15: Sort Regions by Average Charges Descending ===
    public static void sortRegionsByAverageCharges(List<InsuranceRecord> records) {
        Map<String, List<Double>> regionChargesMap = new TreeMap<>();

        // Group charges by region
        for (InsuranceRecord record : records) {
            String region = record.region.toLowerCase().trim();
            regionChargesMap.putIfAbsent(region, new ArrayList<>());
            regionChargesMap.get(region).add(record.charges);
        }

        // Compute average charge for each region
        List<Map.Entry<String, Double>> regionAvgCharges = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : regionChargesMap.entrySet()) {
            List<Double> charges = entry.getValue();
            double sum = 0;
            for (double c : charges) sum += c;
            double avg = charges.size() > 0 ? sum / charges.size() : 0;
            regionAvgCharges.add(Map.entry(entry.getKey(), avg));
        }

        // Sort by average charges descending
        regionAvgCharges.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        System.out.println("=== Regions Sorted by Average Charges (Descending) ===");
        for (Map.Entry<String, Double> entry : regionAvgCharges) {
            System.out.printf("Region: %-10s | Avg Charges: $%.2f\n", entry.getKey(), entry.getValue());
        }
    }
    // === Problem 17: Southerners vs Northerners Smoking Rate and Average Age ===
    public static void compareSmokingByRegion(List<InsuranceRecord> records) {
        int southernSmokers = 0;
        int southernTotal = 0;
        int southernAgeSum = 0;

        int northernSmokers = 0;
        int northernTotal = 0;
        int northernAgeSum = 0;

        for (InsuranceRecord record : records) {
            String region = record.region.toLowerCase().trim();
            int age = record.age;

            if (region.equals("southeast") || region.equals("southwest")) {
                southernTotal++;
                southernAgeSum += age;
                if (record.smoker) southernSmokers++;  // no need for equalsIgnoreCase
            } else if (region.equals("northeast") || region.equals("northwest")) {
                northernTotal++;
                northernAgeSum += age;
                if (record.smoker) northernSmokers++;
            }
        }

        double southernSmokingRate = southernTotal > 0 ? (double) southernSmokers / southernTotal : 0;
        double northernSmokingRate = northernTotal > 0 ? (double) northernSmokers / northernTotal : 0;

        double avgSouthernAge = southernTotal > 0 ? (double) southernAgeSum / southernTotal : 0;
        double avgNorthernAge = northernTotal > 0 ? (double) northernAgeSum / northernTotal : 0;

        System.out.println("=== Smoking Comparison: Southerners vs Northerners ===");
        System.out.printf("Southern Smoking Rate: %.2f%% (Avg Age: %.1f)\n", southernSmokingRate * 100, avgSouthernAge);
        System.out.printf("Northern Smoking Rate: %.2f%% (Avg Age: %.1f)\n", northernSmokingRate * 100, avgNorthernAge);

        System.out.println("Do southerners smoke more than northerners? " + 
            (southernSmokingRate > northernSmokingRate ? "✅ YES" : "❌ NO"));
    }
    // === Problem 19: Do southerners average more children than northerners? At what average age? ===
    public static void compareChildrenByRegion(List<InsuranceRecord> records) {
        int southernChildren = 0;
        int southernTotal = 0;
        int southernAgeSum = 0;

        int northernChildren = 0;
        int northernTotal = 0;
        int northernAgeSum = 0;

        for (InsuranceRecord record : records) {
            String region = record.region.toLowerCase().trim();
            if (region.equals("southeast") || region.equals("southwest")) {
                southernChildren += record.children;
                southernAgeSum += record.age;
                southernTotal++;
            } else if (region.equals("northeast") || region.equals("northwest")) {
                northernChildren += record.children;
                northernAgeSum += record.age;
                northernTotal++;
            }
        }

        double avgSouthernChildren = southernTotal > 0 ? (double) southernChildren / southernTotal : 0;
        double avgSouthernAge = southernTotal > 0 ? (double) southernAgeSum / southernTotal : 0;

        double avgNorthernChildren = northernTotal > 0 ? (double) northernChildren / northernTotal : 0;
        double avgNorthernAge = northernTotal > 0 ? (double) northernAgeSum / northernTotal : 0;

        System.out.println("=== Children Comparison: Southerners vs Northerners ===");
        System.out.printf("Southern: Avg Children = %.2f, Avg Age = %.1f\n", avgSouthernChildren, avgSouthernAge);
        System.out.printf("Northern: Avg Children = %.2f, Avg Age = %.1f\n", avgNorthernChildren, avgNorthernAge);

        System.out.println("Do southerners average more children? " +
            (avgSouthernChildren > avgNorthernChildren ? "✅ YES" : "❌ NO"));
    }
    // === Problem 21: Linear Regression of Charges vs Children ===
    public static void regressionChargesVsChildren(List<InsuranceRecord> records) {
        List<Integer> xChildren = new ArrayList<>();
        List<Double> yCharges = new ArrayList<>();

        for (InsuranceRecord r : records) {
            xChildren.add(r.children);
            yCharges.add(r.charges);
        }

        int n = xChildren.size();
        double sumX = 0, sumY = 0;
        for (int i = 0; i < n; i++) {
            sumX += xChildren.get(i);
            sumY += yCharges.get(i);
        }

        double meanX = sumX / n;
        double meanY = sumY / n;

        double numerator = 0, denominatorX = 0, denominatorY = 0;
        for (int i = 0; i < n; i++) {
            double dx = xChildren.get(i) - meanX;
            double dy = yCharges.get(i) - meanY;
            numerator += dx * dy;
            denominatorX += dx * dx;
            denominatorY += dy * dy;
        }

        double r = numerator / Math.sqrt(denominatorX * denominatorY);
        double slope = numerator / denominatorX;  // a
        double intercept = meanY - slope * meanX; // b

        System.out.println("=== Linear Regression: Charges vs. Children ===");
        System.out.printf("r (Pearson Correlation Coefficient): %.4f\n", r);
        System.out.printf("Regression Equation: y = %.2f * x + %.2f\n", slope, intercept);

        // Apply regression to 22 new x values (e.g., children counts from 6 to 27)
        System.out.println("=== Regression Predictions for New Children Counts ===");
        for (int x = 6; x < 28; x++) {  // 22 values
            double predictedY = slope * x + intercept;
            System.out.printf("Children: %2d => Predicted Charges: $%.2f\n", x, predictedY);
        }
    }


        



    
}

    
    

