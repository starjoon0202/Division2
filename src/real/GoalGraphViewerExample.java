package real;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;

import element.Goal;
import element.Relation;

public class GoalGraphViewerExample {
	
	// jSon file path
    public static String path = "./data-misoo/20180220/car-wheel.json";
    public static String viewType = "NAME"; // ID, NAME
    public static String edgeType = "WEIGHT"; // WEIGHT, CONTRIBUTION
    public static HashMap<String, Goal> goalList = new HashMap<String,Goal>();
    //hardgoal, softgoal, task, resource
    public static String[] styles = {"shape=ellipse;strokeColor=black;fillColor=lightgray;fontColor=black", 
    		"shape=cloud;strokeColor=black;fontColor=black",
    		"shape=hexagon;strokeColor=black;fillColor=yellow;fontColor=black",
    		"shape=rectangle;strokeColor=black;fillColor=white;fontColor=black"};
    //AND,OR,CHOICE
    public static String[] edgeStyles = {"","dashed=true","strockColor=gray;dashed=true"};

    private static void createAndShowGui() {
        JFrame frame = new JFrame("Goal Viewer : "+path);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

        ListenableGraph<String, MyEdge> g = buildGraph();
        JGraphXAdapter<String, MyEdge> graphAdapter = 
                new JGraphXAdapter<String, MyEdge>(g);
        
        graphAdapter.getStylesheet().getDefaultEdgeStyle().replace("strokeColor", "black");
        graphAdapter.getStylesheet().getDefaultEdgeStyle().replace("fontColor", "black");
        
        Iterator<String> iter = goalList.keySet().iterator();
        while(iter.hasNext()){
        	String goalID = iter.next();
        	Goal goal = goalList.get(goalID);
        	String nodeID = goal.getNodeID();
        	if(viewType.equals("NAME")){
        		nodeID = nodeID+"\n"+goal.getNodeName();
        	}
        	String nodeType = goal.getNodeType();
        	mxICell currentCell = graphAdapter.getVertexToCellMap().get(nodeID);
        	setVertexStyle(graphAdapter,nodeType,currentCell);

        	String relType = goal.getRelation().getRelType();
        	setEdgeStyle(graphAdapter,relType,goal);
        }              
        
        graphAdapter.setCellsLocked(true);
        graphAdapter.setCellsEditable(false);
        graphAdapter.setCellsDeletable(false); 
        graphAdapter.refresh();
        
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        frame.add(new mxGraphComponent(graphAdapter));

        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private static void setEdgeStyle(JGraphXAdapter<String, MyEdge> graphAdapter, String relType, Goal goal) {
    	String nodeID = goal.getNodeID();
    	if(viewType.equals("NAME")){
    		nodeID = nodeID+"\n"+goal.getNodeName();
    	}
    	ArrayList<String> subGoals = goal.getRelation().getSubGoals();
    	    	    	
        Iterator<MyEdge> iter = graphAdapter.getEdgeToCellMap().keySet().iterator();
        while(iter.hasNext()){
        	MyEdge cell = iter.next();
        	mxICell value = graphAdapter.getEdgeToCellMap().get(cell);
        	if(cell.getSource().equals(nodeID)){
        		for(int i = 0 ; i<subGoals.size(); i++){
        			String subGoalID = subGoals.get(i);
        	    	if(viewType.equals("NAME")){
        	    		subGoalID = subGoalID+"\n"+goalList.get(subGoals.get(i)).getNodeName();
        	    	}
        			if(cell.getTarget().equals(subGoalID)){
        				switch(relType){
        		    	case "AND":
        		    		value.setStyle(edgeStyles[0]);
        		    		break;
        		    	case "OR":
        		    		value.setStyle(edgeStyles[1]);
        		    		break;
        		    	case "CHOICE":
        		    		value.setStyle(edgeStyles[2]);
        		    		break;
        		    	}
        		    	
        			}
        		}
        	}
        		
        }
        
        
	}

	private static void setVertexStyle(JGraphXAdapter<String, MyEdge> graphAdapter, String nodeType, mxICell currentCell) {    	
    	currentCell.setGeometry(new mxGeometry(currentCell.getGeometry().getX(), currentCell.getGeometry().getY(), 
    			currentCell.getGeometry().getWidth()+50, currentCell.getGeometry().getHeight()+10));
    	switch(nodeType){
    	case "hardgoal":
    		currentCell.setStyle(styles[0]);
    		break;
    	case "softgoal":
    		currentCell.setStyle(styles[1]);
    		break;
    	case "task":
    		currentCell.setStyle(styles[2]);
    		break;
    	case "resource":
    		currentCell.setStyle(styles[3]);
    		break;
    	}
    	
	}

	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
            }
        });
    }

    @SuppressWarnings("serial")
	public static class MyEdge extends DefaultWeightedEdge {
    	
        @Override
		protected Object getSource() {
			// TODO Auto-generated method stub
			return super.getSource();
		}

		@Override
		protected Object getTarget() {
			// TODO Auto-generated method stub
			return super.getTarget();
		}

		@Override
		protected double getWeight() {
			// TODO Auto-generated method stub
			return super.getWeight();
		}

		@Override
        public String toString() {
            return String.valueOf(getWeight());
        }
    }

    public static ListenableGraph<String, MyEdge> buildGraph() {
        @SuppressWarnings("deprecation")
		ListenableDirectedWeightedGraph<String, MyEdge> g = 
            new ListenableDirectedWeightedGraph<String, MyEdge>(MyEdge.class);
        
        JSonLoaderExample jsload = new JSonLoaderExample();        
        try {
        	goalList = jsload.readGoal(path);
        	jsload.relValidateGoals(goalList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Iterator<String> iter = goalList.keySet().iterator();
        while(iter.hasNext()){
        	String goalID = iter.next();
        	Goal goal = goalList.get(goalID);
        	System.out.println(goal);
        	switch(viewType){
    		case "ID":
    			g.addVertex(goal.getNodeID());    			
    			break;
    		case "NAME":
    			g.addVertex(goal.getNodeID()+"\n"+goal.getNodeName()); break;
        	}
        	
        }        
        iter = goalList.keySet().iterator();
        while(iter.hasNext()){
        	String goalID = iter.next();
        	Goal goal = goalList.get(goalID);
//        	String parentGoal = goal.getParentNode();
        	Relation rel = goal.getRelation();
        	ArrayList<String> subGoals = rel.getSubGoals();
        	ArrayList<String> contGoals = rel.getContGoals();
        	       
        	switch(edgeType){
    		case "WEIGHT":
	        	for(int i = 0 ; i <subGoals.size(); i++){
	        		MyEdge edge = new MyEdge();
	        		switch(viewType){
	        		case "ID":        			
	        			edge = g.addEdge(goalID,subGoals.get(i)); break;
	        		case "NAME": 
	        			edge = g.addEdge(goal.getNodeID()+"\n"+goal.getNodeName(),subGoals.get(i)+"\n"+goalList.get(subGoals.get(i)).getNodeName());
	        			break;
	        		}        		
	        		g.setEdgeWeight(edge, goal.getRelation().getWeights().get(i));
	        	}
	        	break;
    		case "CONTRIBUTION":
    			for(int i = 0 ; i <contGoals.size(); i++){
	        		MyEdge edge = new MyEdge();
	        		switch(viewType){
	        		case "ID":        			
	        			edge = g.addEdge(goalID,contGoals.get(i)); break;
	        		case "NAME": 
	        			edge = g.addEdge(goal.getNodeID()+"\n"+goal.getNodeName(),contGoals.get(i)+"\n"+goalList.get(contGoals.get(i)).getNodeName());
	        			break;
	        		}        		
	        		g.setEdgeWeight(edge, goal.getRelation().getContributions().get(i));
	        	}
	        	break;
        	}        	
        }
                
        return g;
    }
}