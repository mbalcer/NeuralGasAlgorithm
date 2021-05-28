package algorithm;

import lombok.Getter;
import utils.FileHandler;
import utils.Metric;
import utils.MyLogger;
import utils.Utils;

import java.util.List;
import java.util.Random;

@Getter
public class Kohonen extends Neural {

	private double mapRadiusStart;
	private double learningRateStart;
	private double timeConst;

	private String destDir = "results_khn/";
	private String destImage = destDir + "out.png";
	private String destFile = destDir + "khn.data";
	private String imgcprFile = destDir + "imgcpr.data";

	private final double minRadius = 1.0E-10;
	private final int rerolls = 10;
	private MyLogger myLogger;

	public Kohonen(int neuronsNum, int iterations, String srcFilePath, String separator, boolean normalize,
			double mapRadiusStart, double learningRateStart, double timeConst) {
		super(neuronsNum, iterations, srcFilePath, separator, normalize);

		this.mapRadiusStart = mapRadiusStart;
		this.learningRateStart = learningRateStart;
		this.timeConst = (iterations) / (mapRadiusStart * timeConst);
		this.myLogger = MyLogger.getInstance();

		FileHandler.makeEmptyDir(destDir);
		FileHandler.copy(srcFilePath, destFile);
		
		rerollDead(rerolls);
	}

	public void learn(int epoch) {
		double distFromBMU, mapRadius, learningRate, influence, newWeight;
		int pointIndex;

		Random r = new Random();
		pointIndex = r.nextInt(data.size());

		List<Double> point = data.get(pointIndex);
		List<Double> nearestNeuron = neurons.get(winnerIds.get(pointIndex));

		myLogger.info("Neuron " + winnerIds.get(pointIndex) + " is winner." + "\n"
				+ "Weights of neuron winner: \n" + Utils.formatList(nearestNeuron));

		mapRadius = mapRadiusStart * Math.exp(-(double) epoch / timeConst);
		learningRate = learningRateStart * Math.exp(-(double) epoch / iterations);

		if (mapRadius < minRadius) {
			mapRadius = minRadius;
		}
		myLogger.info("Map radius: " + mapRadius + "\t" + "Learning rate: " + learningRate);

		for (int i = 0; i < neurons.size(); i++) {
			distFromBMU = Metric.euclidean(nearestNeuron, neurons.get(i));
			influence = Math.exp(-(distFromBMU * distFromBMU) / (2 * mapRadius * mapRadius));

			for (int j = 0; j < neurons.get(i).size(); j++) {
				newWeight = neurons.get(i).get(j) + learningRate * influence * (point.get(j) - neurons.get(i).get(j));
				neurons.get(i).set(j, newWeight);
			}
		}
	}

	public void calc() {
		for (int i = 0; i < iterations; i++) {
			myLogger.info("--------------------------------------\n" + "Iteration: " + (i+1));
			calcWinnersIds();
			learn(i);
		}

		FileHandler.writePointsAsClusters(winnerIds, neurons, imgcprFile, separator);
	}
}
