public class InsuranceRecord {
    int age;
    String sex;
    double bmi;
    int children;
    boolean smoker;
    String region;
    double charges;

    public InsuranceRecord(String[] data) {
        this.age = Integer.parseInt(data[0].trim());
        this.sex = data[1].trim();
        this.bmi = Double.parseDouble(data[2].trim());
        this.children = Integer.parseInt(data[3].trim());
        this.smoker = data[4].trim().equalsIgnoreCase("yes");
        this.region = data[5].trim();
        this.charges = Double.parseDouble(data[6].trim());
    }

    @Override
    public String toString() {
        return age + ", " + sex + ", " + bmi + ", " + children + ", " + smoker + ", " + region + ", " + charges;
    }
}
