package entities;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;
import java.util.List;

public class SRS  implements Externalizable {
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
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		consistent = in.readBoolean();
		int size = in.readInt();
		requirements = new LinkedList<String>();
		for (int i = 0; i < size; i++) {
			requirements.add(in.readUTF());
		}
	}
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeBoolean(consistent);
		out.writeInt(requirements.size());
		for(String s : requirements) {
			out.writeUTF(s);
		}
	}
}
