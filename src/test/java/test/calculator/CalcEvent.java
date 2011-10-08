package test.calculator;

import java.io.Serializable;

public class CalcEvent implements Serializable {
	
	public CalcOperation op;
	public Number value;
	
	public CalcOperation getOp() {
		return op;
	}
	
	public Number getValue() {
		return value;
	}

}
