package riflab3;

import riflab3.entities.Product;

public class Workflow {

	Product p1, p2, p3, p4;
	int step;
	boolean isConsistent;
	
	public Product getP1() {
		return p1;
	}

	public void setP1(Product p1) {
		this.p1 = p1;
	}

	public Product getP2() {
		return p2;
	}

	public void setP2(Product p2) {
		this.p2 = p2;
	}

	public Product getP3() {
		return p3;
	}

	public void setP3(Product p3) {
		this.p3 = p3;
	}

	public Product getP4() {
		return p4;
	}

	public void setP4(Product p4) {
		this.p4 = p4;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public boolean isConsistent() {
		return isConsistent;
	}

	public void setConsistent(boolean isConsistent) {
		this.isConsistent = isConsistent;
	}
	
	public Workflow() {
		this.isConsistent = false;
	}

	public Workflow(boolean isConsistent) {
		this.isConsistent = isConsistent;
	}
}
