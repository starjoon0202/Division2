package editor.element;

public class Goal {

	String nodeID;
	String nodeName;
	String parentNode;
	String nodeDomain;
	String nodeType;
	Relation relation;
	Condition condition;
	
	double evaluation;

	public String getNodeID() {
		return nodeID;
	}

	public Condition getCondition() {
		return condition;
	}

	@Override
	public String toString() {
		return "Goal [nodeID=" + nodeID + ", nodeName=" + nodeName + ", parentNode=" + parentNode + ", nodeDomain="
				+ nodeDomain + ", nodeType=" + nodeType + ", relation=" + relation + ", condition=" + condition
				+ ", evaluation=" + evaluation + "]";
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getParentNode() {
		return parentNode;
	}

	public void setParentNode(String parentNode) {
		this.parentNode = parentNode;
	}

	public String getNodeDomain() {
		return nodeDomain;
	}

	public void setNodeDomain(String nodeDomain) {
		this.nodeDomain = nodeDomain;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		nodeType = nodeType.toLowerCase();
		NodeTypeEnum[] types = NodeTypeEnum.values();
		boolean check = false;
		for(int i = 0 ; i<types.length; i++){
			if(nodeType.equals(types[i].name())){
				check = true;
				break;
			}
		}				
		if(check)
			this.nodeType = nodeType;
		else
			System.err.println("WRONG NODE TYPE " + nodeType);
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public double getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(double evaluation) {
		this.evaluation = evaluation;
	}	
	
	
	
}
