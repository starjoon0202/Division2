package editor.element;

public class MxeEdgeData {
	
	String edgeID;
	String edgeType;
	String value;	
	String source;
	String target;

	Geometry geo = new Geometry();
	
	
	
	public Geometry getGeo() {
		return geo;
	}
	public void setGeo(Geometry geo) {
		this.geo = geo;
	}
	
	
	public String getEdgeID() {
		return edgeID;
	}
	public void setEdgeID(String edgeID) {
		this.edgeID = edgeID;
	}
	public String getEdgeType() {
		return edgeType;
	}
	public void setEdgeType(String edgeType) {
		RelTypeEnum[] types = RelTypeEnum.values();
		edgeType = edgeType.toUpperCase();
		boolean check = false;
		for(int i = 0 ; i<types.length; i++){
			if(edgeType.equals(types[i].name())){
				check = true;
				break;
			}
		}				
		if(check)
			this.edgeType = edgeType;
		else
			System.err.println("WRONG RELATION TYPE  " + edgeType);
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	@Override
	public String toString() {
		return "MxeEdgeData [edgeID=" + edgeID + ", edgeType=" + edgeType + ", value=" + value + ", source=" + source
				+ ", target=" + target + ", geo = "+geo.toString()+"]";
	}
}
