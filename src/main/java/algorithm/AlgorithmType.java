package algorithm;

public enum AlgorithmType {

    NEURAL_GAS("Neural gas"),
    KOHONEN("Kohonen");

    private String name;

    AlgorithmType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
