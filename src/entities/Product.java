package entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Product implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int id;
	private boolean verified;
	private boolean valid;
	private List<File> sourceFiles;
	private List<File> testSuite;
	private List<File> documentation;
	private List<String> architecture;
	private SRS srs;
	
	public Product(int id) {
		sourceFiles = new LinkedList<File>();
		testSuite = new LinkedList<File>();
		documentation = new LinkedList<File>();
		architecture = new LinkedList<String>();
		srs = new SRS();
		this.id = id;
	}
	
	public String toString() {
		return "Product #" + id + " (r"+srs.getRequirements().size()+
			", cons"+(srs.isConsistent() ? 1 : 0) +
			", a"+getArchitecture().size()+
			", code" + sourceFiles.size() +
			", t" + testSuite.size() +
			", d" + documentation.size() +
			", ve" + (isVerified() ? 1: 0) +
			", va" + (isValid() ? 1:0) + ")";
	}
	
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public List<File> getSourceFiles() {
		return sourceFiles;
	}
	public void setSourceFiles(List<File> sourceFiles) {
		this.sourceFiles = sourceFiles;
	}
	public List<File> getTestSuite() {
		return testSuite;
	}
	public void setTestSuite(List<File> testSuite) {
		this.testSuite = testSuite;
	}
	public List<File> getDocumentation() {
		return documentation;
	}
	public void setDocumentation(List<File> documentation) {
		this.documentation = documentation;
	}
	public List<String> getArchitecture() {
		return architecture;
	}
	public void setArchitecture(List<String> architecture) {
		this.architecture = architecture;
	}
	public SRS getSrs() {
		return srs;
	}
	public void setSrs(SRS srs) {
		this.srs = srs;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
