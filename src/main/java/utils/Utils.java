package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Utils {

	private Utils() {

	}

	public static void normalize(List<List<Double>> data, List<Double> sds, boolean renormalize) {
		for (List<Double> row : data) {
			for (int i = 0; i < row.size(); i++) {
				if (renormalize) {
					row.set(i, row.get(i) * sds.get(i));
				} else {
					row.set(i, row.get(i) * sds.get(i));
				}
			}
		}
	}

	public static List<Double> calcSds(List<List<Double>> data) {
		List<Double> sds = new ArrayList<Double>();
		for (int i = 0; i < data.get(0).size(); i++) {
			sds.add(DataMath.standardDeviation(getColumn(data, i)));
		}
		return sds;
	}

	public static <T> List<T> getColumn(List<List<T>> matrix, int col) {
		List<T> ret = new ArrayList<T>();
		for (List<T> i : matrix) {
			ret.add(i.get(col));
		}

		return ret;
	}

	public static void rumCmd(String cmd) {
		final Runtime rt = Runtime.getRuntime();
		try {
			rt.exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String formatList(List<Double> list) {
		int pageSize = 8;
		return IntStream.range(0, (list.size() + pageSize - 1) / pageSize)
				.mapToObj(i -> list.subList(i * pageSize, (i + 1) * pageSize - 1))
				.map(l -> l.stream()
						.map(item -> DataMath.approximation(item, 2))
						.map(item -> item.toString())
						.collect(Collectors.joining(";")))
				.collect(Collectors.joining("\n"));
	}

}
