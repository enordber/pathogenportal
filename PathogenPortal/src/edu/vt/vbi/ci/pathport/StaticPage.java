package edu.vt.vbi.ci.pathport;

public class StaticPage extends PPDataSet{

	String content;
	
	public void setContent(String c) {
		content = c;
	}
	
	public String getHTML() {
		return content;
	}
}
