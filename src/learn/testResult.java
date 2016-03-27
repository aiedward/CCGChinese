package learn;

public class testResult {

	public testResult() {
	}
	
	public testResult(double p, double r, double f) {
		precision = p;
		recall = r;
		F1 = f;
	}
	
	private double precision;
	private double recall;
	private double F1;
	
	public double getPrecision() {
		return precision;
	}
	public double getRecall() {
		return recall;
	}
	public double getF1() {
		return F1;
	}
	
	public void setPrecision(double p) {
		precision = p;
		return;
	}
	public void setRecall(double r) {
		recall = r;
		return;
	}
	public void setF1(double f) {
		F1 = f;
		return;
	}

}
