package algorithm;

import utils.FileHandler;
import utils.Metric;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class NeuralGas extends Neural {

	private double mapRadiusStart;
	private double learningRateStart;
	private double timeConst;

	public final String destDir = "results_ng/";
	public final String destFile = "ng.data";
	public final String neuronsFile = "neurons.data";
	public final String imgcprFile = "imgcpr.data";
	public final int rerolls = 10;

	public NeuralGas(int neuronsNum, int iterations, String srcFilePath, String separator, boolean normalize,
			double mapRadiusStart, double learningRateStart, double timeConst) {
		super(neuronsNum, iterations, srcFilePath, separator, normalize);

		this.mapRadiusStart = mapRadiusStart;
		this.learningRateStart = learningRateStart;
		this.timeConst = (iterations) / (mapRadiusStart * timeConst);

		FileHandler.makeEmptyDir(destDir);
		FileHandler.copy(srcFilePath, destDir + destFile);
		
		rerollDead(rerolls);
	}

	public void learn(int epoch) {
		double learningRate, influence, newWeight, mapRadius;
		int pointIndex;

		Random r = new Random();
		pointIndex = r.nextInt(data.size());

		List<Double> point = data.get(pointIndex);
		List<Double> nearestNeuron = neurons.get(winnerIds.get(pointIndex));

		mapRadius = mapRadiusStart * Math.exp(-(double) epoch / timeConst);
		learningRate = learningRateStart * Math.exp(-(double) epoch / iterations);
		System.out.println("Map radius: " + mapRadius + "\t" + "Learning rate: " + learningRate);

		Map<Double, List<Double>> map = new TreeMap<Double, List<Double>>();
		for (int i = 0; i < neurons.size(); i++) {
			map.put(Metric.euclidean(neurons.get(i), nearestNeuron), neurons.get(i));
		}

		int i = 0;
		for (Map.Entry<Double, List<Double>> e : map.entrySet()) {
			List<Double> neuron = e.getValue();
			for (int j = 0; j < neuron.size(); j++) {
				influence = Math.exp(-(i) / (mapRadius));
				newWeight = neuron.get(j) + learningRate * influence * (point.get(j) - neuron.get(j));
				neuron.set(j, newWeight);
			}
			i++;
		}
	}

	public void calc() {
		for (int i = 0; i < iterations; i++) {
			calcWinnersIds();
			learn(i);

			System.out.println("Iteration: " + i);
		}

		FileHandler.writePointsAsClusters(winnerIds, neurons, destDir + imgcprFile, separator);
	}
}
