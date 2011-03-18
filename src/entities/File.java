package entities;

import java.io.Serializable;

public class File implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String content;
	private Integer quality;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getQuality() {
		return quality;
	}
	public void setQuality(Integer quality) {
		this.quality = quality;
	}
}
