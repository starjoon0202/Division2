package real;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import element.Condition;
import element.Goal;
import element.Relation;

public class JSonLoaderExample {
	
	public static void main(String[] args) throws IOException {
        
    	/* Modify Json Format (Misoo. 20171229)
    	 * Conditions: Value --> Array
    	 * 
    	*/
    	
    	
        String jsonStr = "";
        BufferedReader br = new BufferedReader(new FileReader("./data-misoo/rsu.json"));
        String str;
        while((str=br.readLine())!=null){
        	jsonStr = jsonStr+str;
        }        

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(jsonStr);
            JSONArray goalArray = (JSONArray) jsonObj.get("goals");

            System.out.println("=====Goals=====");
            for(int i=0 ; i<goalArray.size() ; i++){
                JSONObject tempObj = (JSONObject) goalArray.get(i);
                System.out.println(""+(i+1)+"번째 목표 아이디: "+tempObj.get("NodeID"));
                System.out.println(""+(i+1)+"번째 목표 이름: "+tempObj.get("NodeName"));
                System.out.println(""+(i+1)+"번째 목표의 부모 노드: "+tempObj.get("ParentNode"));
                System.out.println(""+(i+1)+"번째 목표의 도메인: "+tempObj.get("NodeDomain"));
                System.out.println(""+(i+1)+"번째 목표의 노드 종류: "+tempObj.get("NodeType"));
                
                Goal goal = new Goal();
            	goal.setNodeID((String) tempObj.get("NodeID"));
            	goal.setNodeName((String) tempObj.get("NodeID"));
            	goal.setNodeDomain((String) tempObj.get("NodeID"));
            	goal.setNodeType((String) tempObj.get("NodeType"));
            	                
                
                JSONObject relObj = (JSONObject)(tempObj.get("RELATION"));
                System.out.println(""+(i+1)+"번째 목표의 노드 관계 - 종류: "+relObj.get("RelType"));
                
                JSONArray subNodeArray = (JSONArray) relObj.get("SubNodes");
                JSONArray subNodeWeightArray = (JSONArray) relObj.get("Weight");
                if(subNodeArray.size() != subNodeWeightArray.size())
                	System.err.println("NO SAME NUMBER BETWEEN VALUES OF SUBNODES AND WEIGHT" + goal.getNodeID());
                for(int j = 0 ; j<subNodeArray.size(); j++){                    
                	System.out.println(""+(i+1)+"번째 목표의 노드 관계 - 하위노드 "+ j +"th (가중치): "+subNodeArray.get(j)+" ("+subNodeWeightArray.get(j)+")");                	
                }
                
                JSONArray contNodeArray = (JSONArray) relObj.get("Contribution");
                for(int j = 0 ; j<contNodeArray.size(); j++){                    
                	System.out.println(""+(i+1)+"번째 목표의 노드 관계 - "+ j +"th  기여목표 아이디 (기여도): "+((JSONArray)contNodeArray.get(j)).get(0)+" ("+((JSONArray)contNodeArray.get(j)).get(1)+")");                	
                }
                
                JSONObject condObj = ((JSONObject)(tempObj.get("Condition")));
                JSONArray condNodeArray = (JSONArray) condObj.get("PreCond");
                for(int j = 0 ; j<condNodeArray.size(); j++){                    
                	System.out.println(""+(i+1)+"번째 목표의 상태 - "+j+"th 사전조건 : "+condNodeArray.get(j));                	
                }
                
                condNodeArray = (JSONArray) condObj.get("PostCond");
                for(int j = 0 ; j<condNodeArray.size(); j++){                    
                	System.out.println(""+(i+1)+"번째 목표의 상태 - "+j+"th 사후조건 : "+condNodeArray.get(j));                	
                }

                System.out.println(""+(i+1)+"번째 멤버의 평가치 : "+tempObj.get("Evaluation"));
                System.out.println("----------------------------");
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
    public  HashMap<String,Goal> readGoal(String path) throws IOException {
        

    	/* Modify Json Format (Misoo. 20171229)
    	 * Conditions: Value --> Array
    	 * 
    	*/
    	
    	
        String jsonStr = "";
        BufferedReader br = new BufferedReader(new FileReader(path));
        String str;
        HashMap<String,Goal> goalList = new  HashMap<String,Goal>();
        
        while((str=br.readLine())!=null){
        	jsonStr = jsonStr+str;
        }        

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(jsonStr);
            JSONArray goalArray = (JSONArray) jsonObj.get("goals");

            for(int i=0 ; i<goalArray.size() ; i++){
                JSONObject tempObj = (JSONObject) goalArray.get(i);
                
                Goal goal = new Goal();
            	goal.setNodeID((String) tempObj.get("NodeID"));
            	goal.setNodeName((String) tempObj.get("NodeName"));
            	goal.setParentNode((String) tempObj.get("ParentNode"));
            	goal.setNodeDomain((String) tempObj.get("NodeDomain"));
            	goal.setNodeType((String) tempObj.get("NodeType"));
            	goal.setDetailInfo((String) tempObj.get("DetailedNodeInfo"));
            	
            	                
                
                JSONObject relObj = (JSONObject)(tempObj.get("RELATION"));
                
                Relation relation = new Relation();
                relation.setRelType((String) relObj.get("RelType"));
                
                JSONArray subNodeArray = (JSONArray) relObj.get("SubNodes");
                JSONArray subNodeWeightArray = (JSONArray) relObj.get("Weight");
                
                String[] subNodeList = new String[subNodeArray.size()];
                Double[] subNodeWeightList = new Double[subNodeWeightArray.size()];
                
                if(subNodeArray.size() != subNodeWeightArray.size()){
                	System.err.println("NO SAME NUMBER BETWEEN VALUES OF SUBNODES AND WEIGHT" + goal.getNodeID());
                	continue;
                }
                for(int j = 0 ; j<subNodeArray.size(); j++){
                	subNodeList[j] = (String) subNodeArray.get(j);
                	subNodeWeightList[j] =Double.parseDouble(subNodeWeightArray.get(j).toString());	
                }
                
                ArrayList<String> subNodes = new ArrayList<String>();
                ArrayList<Double> subNodeWeights = new ArrayList<Double>();
                
                Collections.addAll(subNodes, subNodeList);
                Collections.addAll(subNodeWeights, subNodeWeightList);
                relation.setSubGoals(subNodes);
                relation.setWeights(subNodeWeights);
                
                                
                JSONArray contNodeArray = (JSONArray) relObj.get("Contribution");
                String[] contNodeList = new String[contNodeArray.size()];
                Double[] contWeightList = new Double[contNodeArray.size()];
                
                for(int j = 0 ; j<contNodeArray.size(); j++){               
                	contNodeList[j] = (String) ((JSONArray)contNodeArray.get(j)).get(0);
                	contWeightList[j] = Double.parseDouble(((JSONArray)contNodeArray.get(j)).get(1).toString());             	
                }
                ArrayList<String> contNodes = new ArrayList<String>();
                ArrayList<Double> contNodeWeights = new ArrayList<Double>();                
                Collections.addAll(contNodes, contNodeList);
                Collections.addAll(contNodeWeights, contWeightList);
                relation.setContGoals(contNodes);
                relation.setContributions(contNodeWeights);
                
                goal.setRelation(relation);
                
                
                JSONObject condObj = ((JSONObject)(tempObj.get("Condition")));
                JSONArray condNodeArray = (JSONArray) condObj.get("PreCond");
                String[] preCondList = new String[condNodeArray.size()];
                for(int j = 0 ; j<condNodeArray.size(); j++){
                	preCondList[j] = (String) condNodeArray.get(j);
                }
               
                Condition condition = new Condition();
                ArrayList<String> preConds = new ArrayList<String>();         
                Collections.addAll(preConds, preCondList);
                condition.setPreCondition(preConds);
                
                condNodeArray = (JSONArray) condObj.get("PostCond");
                String[] postCondList = new String[condNodeArray.size()];
                for(int j = 0 ; j<condNodeArray.size(); j++){            
                	postCondList[j] = (String) condNodeArray.get(j);      	
                }
                ArrayList<String> postConds = new ArrayList<String>();         
                Collections.addAll(postConds, postCondList);
                condition.setPreCondition(postConds);
                
                goal.setCondition(condition);
                
                goalList.put(goal.getNodeID(), goal);
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return goalList;
    }

    public void printGoals( HashMap<String,Goal> goalList) throws IOException {
		Iterator<String> iter = goalList.keySet().iterator();
		int i = -1 ;
		while(iter.hasNext()){
			 String goalID = iter.next();
			 i++;
             Goal goal = goalList.get(goalID);
             System.out.println(""+(i+1)+"번째 목표 아이디: "+goal.getNodeID());
             System.out.println(""+(i+1)+"번째 목표 이름: "+goal.getNodeName());
             System.out.println(""+(i+1)+"번째 목표의 부모 노드: "+goal.getParentNode());
             System.out.println(""+(i+1)+"번째 목표의 도메인: "+goal.getNodeDomain());
             System.out.println(""+(i+1)+"번째 목표의 노드 종류: "+goal.getNodeType());
                                 	                
             
             Relation relation = goal.getRelation();
             System.out.println(""+(i+1)+"번째 목표의 노드 관계 - 종류: "+relation.getRelType());
             ArrayList<String> subNodeList = relation.getSubGoals();
             ArrayList<Double> subNodeWeightList = relation.getWeights();
             if(subNodeList.size() != subNodeWeightList.size()){
             	System.err.println("NO SAME NUMBER BETWEEN VALUES OF SUBNODES AND WEIGHT" + goal.getNodeID());
             	System.exit(1);;
             }
             for(int j = 0 ; j<subNodeList.size(); j++){                    
             	System.out.println(""+(i+1)+"번째 목표의 노드 관계 - 하위노드 "+ j +"th (가중치): "+subNodeList.get(j)+" ("+subNodeWeightList.get(j)+")");                	
             }
             
             ArrayList<String> contNodeList = relation.getContGoals();
             ArrayList<Double> contNodeWeightList = relation.getContributions();
             if(contNodeList.size() != contNodeWeightList.size()){
              	System.err.println("NO SAME NUMBER BETWEEN VALUES OF CONT GOALS AND CONTRIBUTION" + goal.getNodeID());
              	System.exit(1);;
              }
             
             for(int j = 0 ; j<contNodeList.size(); j++){                    
             	System.out.println(""+(i+1)+"번째 목표의 노드 관계 - "+ j +"th  기여목표 아이디 (기여도): "+contNodeList.get(j)+" ("+contNodeWeightList.get(j)+")");                	
             }
             
             Condition condition = goal.getCondition();
             ArrayList<String> preCondList = condition.getPreCondition();
             for(int j = 0 ; j<preCondList.size(); j++){                    
             	System.out.println(""+(i+1)+"번째 목표의 상태 - "+j+"th 사전조건 : "+preCondList.get(j));                	
             }
             
             ArrayList<String> postCondList = condition.getPostCondition();
             for(int j = 0 ; j<postCondList.size(); j++){                    
             	System.out.println(""+(i+1)+"번째 목표의 상태 - "+j+"th 사후조건 : "+postCondList.get(j));                	
             }

             System.out.println(""+(i+1)+"번째 멤버의 평가치 : "+goal.getEvaluation());
             System.out.println("----------------------------");
         }
    }
    
    
    public void relValidateGoals( HashMap<String,Goal> goalList) throws IOException {
		Iterator<String> iter = goalList.keySet().iterator();
		int i = -1 ;
		
		while(iter.hasNext()){
			 String goalID = iter.next();
			 i++;
             Goal goal = goalList.get(goalID);             
             
             String parentGoal = goal.getParentNode();
             if(!parentGoal.equals("")){
            	 ArrayList<String> subGoals = goalList.get(parentGoal).getRelation().getSubGoals();
            	 boolean check = false;
            	 for(int j = 0 ; j<subGoals.size(); j++){
            		 if(subGoals.get(j).equals(goalID)){
            			 check = true;
            			 break;
            		 }
            	 }
            	 
            	 if(!check){
            		 System.err.println(goalID+"'s parent goal is "+parentGoal
            				 +", but "+parentGoal+" doesn't have "+goalID+" as subgoals");
            		 System.exit(1);;
            	 }            		 
             }
             
             ArrayList<String> subGoals = goal.getRelation().getSubGoals();
        	 boolean check = false;
        	 for(int j = 0 ; j<subGoals.size(); j++){ 
        		 if(!goalList.containsKey(subGoals.get(j))){
        			 System.err.println(subGoals.get(j)+"  DOESN'T BE IN THE SUBGOAL LIST FOR "+goal.getNodeID());
        			 System.exit(1);;
        		 }
        			 
        		 String subGoalParentGoal = goalList.get(subGoals.get(j)).getParentNode();
            	 check = false;
        		 if(subGoalParentGoal.equals(goalID)){
        			 check = true;
        		 }
            	 
            	 if(!check){
            		 System.err.println(goalID+"'s sub goal is "+subGoals.get(j)
            				 +", but "+subGoals.get(j)+" doesn't have "+goalID+" as parent Goal");
            		 System.exit(1);
            	 }
        	 }
         }
    }
    
    
    @Test
    public void testPrintGoals() throws IOException {
    	
    	HashMap<String,Goal> goalList = readGoal("./data-misoo/rsu.json");
    	printGoals(goalList);
    	relValidateGoals(goalList);
    	
    }
}
