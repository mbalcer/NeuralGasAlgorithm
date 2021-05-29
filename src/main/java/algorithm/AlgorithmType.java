package algorithm;

public enum AlgorithmType {

    NEURAL_GAS("Neural gas", "ng"),
    KOHONEN("Kohonen", "khn");

    private String name;
    private String abbreviation;

    AlgorithmType(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
