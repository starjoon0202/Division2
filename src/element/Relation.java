package element;

import java.util.ArrayList;

public class Relation {
	String relType;
	ArrayList<String> subGoals = new ArrayList<String>();
	ArrayList<Double> weights = new ArrayList<Double>();
	ArrayList<String> contGoals = new ArrayList<String>();
	ArrayList<Double> contributions = new ArrayList<Double>();
	
	
	
	public ArrayList<String> getContGoals() {
		return contGoals;
	}

	public void setContGoals(ArrayList<String> contGoals) {
		this.contGoals = contGoals;
	}

	public Relation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getRelType() {
		return relType;
	}
	public void setRelType(String relType) {
		RelTypeEnum[] types = RelTypeEnum.values();
		relType = relType.toUpperCase();
		boolean check = false;
		for(int i = 0 ; i<types.length; i++){
			if(relType.equals(types[i].name())){
				check = true;
				break;
			}
		}				
		if(check)
			this.relType = relType;
		else
			System.err.println("WRONG RELATION TYPE  " + relType);
		
		
	}
	public ArrayList<String> getSubGoals() {
		return subGoals;
	}
	public void setSubGoals(ArrayList<String> subGoals) {
		this.subGoals = subGoals;
	}
	public ArrayList<Double> getWeights() {
		return weights;
	}
	public void setWeights(ArrayList<Double> weights) {
		this.weights = weights;
	}

	public ArrayList<Double> getContributions() {
		return contributions;
	}

	public void setContributions(ArrayList<Double> contributions) {
		this.contributions = contributions;
	}
	
	
}
