package entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SRS implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<String> requirements;
	private boolean consistent;

	public SRS() {
		requirements = new LinkedList<String>();
	}
	public List<String> getRequirements() {
		return requirements;
	}
	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}
	public boolean isConsistent() {
		return consistent;
	}
	public void setConsistent(boolean consistent) {
		this.consistent = consistent;
	}
}
