package entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SRS implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<String> requirements;
	private TriState consistent;
	private boolean failed;

	public SRS() {
		this.requirements = new LinkedList<String>();
		this.consistent = TriState.UNINIT;
		this.failed = false;
	}
	public List<String> getRequirements() {
		return requirements;
	}
	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}
	public TriState getConsistent() {
		return consistent;
	}
	public void setConsistent(TriState consistent) {
		this.consistent = consistent;
	}
	public boolean isFailed() {
		return failed;
	}
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
}
