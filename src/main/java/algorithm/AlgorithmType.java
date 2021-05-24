package algorithm;

public enum AlgorithmType {

    NEURAL_GAS("Neural gas");

    private String name;

    AlgorithmType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
