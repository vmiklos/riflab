package entities;

import java.util.LinkedList;
import java.util.List;

public class Product {
	private boolean verified;
	private boolean valid;
	private List<File> sourceFiles;
	private List<File> testSuite;
	private List<File> documentation;
	private List<String> architecture;
	private SRS srs;
	
	public Product() {
		sourceFiles = new LinkedList<File>();
		testSuite = new LinkedList<File>();
		documentation = new LinkedList<File>();
		architecture = new LinkedList<String>();
		srs = new SRS();
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
}
