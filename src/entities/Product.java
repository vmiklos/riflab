package entities;

import java.io.Externalizable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

public class Product implements Externalizable {
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
			", a"+architecture.size()+
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

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		id = in.readInt();
		verified = in.readBoolean();
		valid = in.readBoolean();
		int size = in.readInt();
		sourceFiles = new LinkedList<File>();
		for (int i = 0; i < size; i++) {
			sourceFiles.add((File)in.readObject());
		}
		size = in.readInt();
		testSuite = new LinkedList<File>();
		for (int i = 0; i < size; i++) {
			testSuite.add((File)in.readObject());
		}
		size = in.readInt();
		documentation = new LinkedList<File>();
		for (int i = 0; i < size; i++) {
			documentation.add((File)in.readObject());
		}
		size = in.readInt();
		architecture = new LinkedList<String>();
		for (int i = 0; i < size; i++) {
			architecture.add(in.readUTF());
		}
		srs = (SRS)in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(id);
		out.writeBoolean(verified);
		out.writeBoolean(valid);
		out.writeInt(sourceFiles.size());
		for(File f : sourceFiles) {
			out.writeObject(f);
		}
		out.writeInt(testSuite.size());
		for(File f : testSuite) {
			out.writeObject(f);
		}
		out.writeInt(documentation.size());
		for(File f : documentation) {
			out.writeObject(f);
		}
		out.writeInt(architecture.size());
		for(String s : architecture) {
			out.writeUTF(s);
		}
		out.writeObject(srs);
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<String> requirements = new LinkedList<String>();
        requirements.add("elso kovetelmeny");
        requirements.add("masodik kovetelmeny");
        Product product = new Product(1);
        product.getSrs().setRequirements(requirements);
        product.getSrs().setConsistent(true);
        List<String> architecture = new LinkedList<String>();
        architecture.add("model");
        architecture.add("view");
        architecture.add("controller");
        product.setArchitecture(architecture);
        List<File> sourceFiles = new LinkedList<File>();
        File file = new File();
        file.setContent("public static void main()");
        file.setName("main.java");
        file.setQuality(100);
        sourceFiles.add(file);
        product.setSourceFiles(sourceFiles);
        List<File> documentation = new LinkedList<File>();
        File file2 = new File();
        file2.setContent("doc content");
        file2.setName("doc.txt");
        file2.setQuality(100);
        documentation.add(file);
        product.setDocumentation(documentation);
        List<File> testSuite = new LinkedList<File>();
        File file3 = new File();
        file3.setContent("public static void test()");
        file3.setName("test.java");
        file3.setQuality(100);
        testSuite.add(file);
        product.setTestSuite(testSuite);
        product.setValid(true);
        product.setVerified(true);
        
		FileOutputStream fos = new FileOutputStream("/tmp/product");
		ObjectOutputStream outStream = new ObjectOutputStream(fos);
		product.writeExternal(outStream);
		outStream.close();
		
		FileInputStream fis = new FileInputStream("/tmp/product");
		ObjectInputStream inStream = new ObjectInputStream(fis);
		Product p2 = new Product(0);
		p2.readExternal(inStream);
		System.out.println(p2);
	}
}
