package editor.element;

import java.util.ArrayList;

public class Condition {
	ArrayList<String> preCondition = new ArrayList<String>();
	ArrayList<String> postCondition = new ArrayList<String>();;
	public ArrayList<String> getPreCondition() {
		return preCondition;
	}
	public void setPreCondition(ArrayList<String> preCondition) {
		this.preCondition = preCondition;
	}
	public ArrayList<String> getPostCondition() {
		return postCondition;
	}
	public void setPostCondition(ArrayList<String> postCondition) {
		this.postCondition = postCondition;
	}
	
}
