package com.thalesians.linear;

import com.google.common.base.Preconditions;

public class SimpleRectangle implements Rectangle {
	private int toprow;
	private int leftcolumn;
	private int rowcount;
	private int columncount;
	
	private SimpleRectangle(int toprow, int leftcolumn, int rowcount, int columncount) {
		Preconditions.checkArgument(toprow >= 0 && leftcolumn >= 0 && rowcount >= 1 && columncount >= 1);
		this.toprow = toprow;
		this.leftcolumn = leftcolumn;
		this.rowcount = rowcount;
		this.columncount = columncount;
	}
	
	public static SimpleRectangle create(int toprow, int leftcolumn, int rowcount, int columncount) {
		return new SimpleRectangle(toprow, leftcolumn, rowcount, columncount);
	}
	
	@Override public int getTopRow() {
		return toprow;
	}
	
	@Override public int getLeftColumn() {
		return leftcolumn;
	}
	
	@Override public int getRowCount() {
		return rowcount;
	}
	
	@Override public int getColumnCount() {
		return columncount;
	}
	
	@Override public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		SimpleRectangle rhs = (SimpleRectangle) obj;
		return toprow == rhs.toprow && leftcolumn == rhs.leftcolumn && rowcount == rhs.rowcount && columncount == rhs.columncount;
	}
	
	@Override public String toString() {
		return new StringBuilder().append("(").append(toprow).append(", ").append(leftcolumn).append(", ").append(rowcount).append(", ").append(columncount).append(")").toString();
	}
}
