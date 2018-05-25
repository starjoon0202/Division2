package element;

public class MxeNodeData {
	String nodeID;
	String nodeName;
	String nodeType;
	String nodeInfo;
	
	Geometry geo = new Geometry();	
		
	public String getNodeInfo() {
		return nodeInfo;
	}
	public void setNodeInfo(String nodeInfo) {
		this.nodeInfo = nodeInfo;
	}
	public Geometry getGeo() {
		return geo;
	}
	public void setGeo(Geometry geo) {
		this.geo = geo;
	}
		
	public String getNodeID() {
		return nodeID;
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
	@Override
	public String toString() {
		return "MxeNodeData [nodeID=" + nodeID + ", nodeName=" + nodeName + ", nodeType=" + nodeType + 
				", Geo: "+geo.toString()+"], nodeInfo = "+nodeInfo;
	}
	
	
}
