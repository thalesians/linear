package com.thalesians.linear;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

public final class StringUtils {
	private StringUtils() {
		throw new AssertionError("This class cannot be instantiated");
	}
	
	public static <T> String matrixToString(Matrix<T> matrix) {
		StringBuilder sb = new StringBuilder();
		sb.append(matrix.getRowCount()).append('x').append(matrix.getColumnCount()).append(" [");
		for (int i = 0; i < matrix.getRowCount(); ++i) {
			if (i > 0) sb.append(", ");
			sb.append("[");
			for (int j = 0; j < matrix.getColumnCount(); ++j) {
				if (j > 0) { sb.append(", "); }
				sb.append(matrix.get(i, j));
			}
			sb.append("]");
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static Matrix<Double> fromString(MatrixBuilder<Double> builder, String str) throws LinearException {
		if (str.indexOf("x") != -1) {
			return fromSparseFormatString(builder, str);
		} else {
			return fromDenseFormatString(builder, str);
		}
	}
	
	public static Matrix<Double> fromDenseFormatString(MatrixBuilder<Double> builder, String str) throws LinearException {                                      
		str = str.replace("[", "").replace("]", "");
		ImmutableList<String> rowStrings = ImmutableList.copyOf(Splitter.on(";").trimResults().omitEmptyStrings().split(str));
		int rc = rowStrings.size();
		int cc = -1;
		for (int i = 0; i < rc; ++i) {
			String rowString = rowStrings.get(i);
			ImmutableList<String> valueStrings = ImmutableList.copyOf(Splitter.on(",").trimResults().omitEmptyStrings().split(rowString));
			if (i == 0) {
				cc = valueStrings.size();
				builder.setShape(rc, cc);
			} else {
				if (valueStrings.size() != cc) {
					throw new LinearException("Unexpected number of values in row at index " + i + ": expected: " + cc + ", got " + valueStrings.size());
				}
			}
			for (int j = 0; j < valueStrings.size(); ++j) {
				builder.set(i, j, Double.valueOf(valueStrings.get(j)));
			}
		}
		return builder.build();
	}
	
	private static final Pattern sparseFormatSizePattern = Pattern.compile("\\s*(\\d+)\\s*x\\s*(\\d+)\\s*");
	private static final Pattern sparseFormatElementValuePattern = Pattern.compile("\\s*(\\d+)\\s*\\,\\s*(\\d+)\\s*=(.+)\\s*");
	
	public static Matrix<Double> fromSparseFormatString(MatrixBuilder<Double> builder, String str) throws LinearException {
		ImmutableList<String> strs = ImmutableList.copyOf(Splitter.on(":").trimResults().split(str));
		if (strs.size() > 2) { throw new LinearException("Invalid matrix string format"); }
		
		String size = strs.get(0);
		Matcher sizeMatcher = sparseFormatSizePattern.matcher(size);
		if (!sizeMatcher.matches()) { throw new LinearException("Invalid matrix string format"); }
		
		int rowCount = Integer.valueOf(sizeMatcher.group(1));
		int columnCount = Integer.valueOf(sizeMatcher.group(2));
		
		builder.setShape(rowCount, columnCount);
		
		if (strs.size() > 1) {
			ImmutableList<String> elementValues = ImmutableList.copyOf(Splitter.on(";").trimResults().omitEmptyStrings().split(strs.get(1)));
			for (String ev : elementValues) {
				Matcher elementValueMatcher = sparseFormatElementValuePattern.matcher(ev);
				if (!elementValueMatcher.matches()) { throw new LinearException("Invalid matrix string format"); }
				int row = Integer.parseInt(elementValueMatcher.group(1));
				int column = Integer.parseInt(elementValueMatcher.group(2));
				double value = Double.parseDouble(elementValueMatcher.group(3));
				builder.set(row, column, value);
			}
		}
		
		return builder.build();
	}
}
