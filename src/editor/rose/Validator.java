package editor.rose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class Validator {
	static Object[] vertices;

	public HashMap<String, Object> isValidForSave(mxGraphComponent graphComponent, mxGraph graph) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("flag", true);		
		result.put("message", "");
		mxAnalysisGraph aGraph = new mxAnalysisGraph();		
		aGraph.setGraph(graph);
		vertices = aGraph.getChildVertices(aGraph.getGraph().getDefaultParent());

		// 1. Check Null Names
		HashMap<String, Object> nullCheck = isNullValue(graphComponent, graph, aGraph);
		if(!(boolean)(nullCheck.get("flag")) && result.containsKey("flag")){
			result.replace("flag", false);
			result.replace("message", result.get("message")+"\n\tEXIST NULL NAME NODE: "+nullCheck.get("message"));
		}
		
		// 2. Check Duplicated Nodes except Resource and Condition
		HashMap<String, Object> dupCheck = isDupNode(graphComponent, graph, aGraph);
		if(!(boolean)(dupCheck.get("flag")) && result.containsKey("flag")){
			result.replace("flag", false);
			result.replace("message", result.get("message")+"\n\tDUPLICATE NAME: "+dupCheck.get("message"));
		}

		// 3. Is Connected
		HashMap<String, Object> connected = isConnected(graphComponent, graph, aGraph);
		if(!(boolean)(connected.get("flag")) && result.containsKey("flag")){
			result.replace("flag", false);
			result.replace("message", result.get("message")+"\n\tNOT CONNECTED MODEL: "+connected.get("message"));
		}		
		
		// 4. Compare Names of Task and Resource/Condition		
		HashMap<String, Object> taskVar = isSameTaskVariable(graphComponent, graph, aGraph);
		if(!(boolean)(taskVar.get("flag")) && result.containsKey("flag")){
			result.replace("flag", false);
			result.replace("message", result.get("message")+"\n\tUNSUITABLE TASKS OR VARS: "+taskVar.get("message"));
		}
		
//		
		return result;
	}

	private HashMap<String, Object> isDupNode(mxGraphComponent graphComponent, mxGraph graph, mxAnalysisGraph aGraph) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("flag", true);
		result.put("message", "");
		HashSet<String> dupNameSet = new HashSet<String>(); 
		for (int i = 0; i < vertices.length-1; i++){
			mxCell cell = (mxCell) vertices[i];						
			String name = ((String) cell.getValue()).toLowerCase().replace(" ", "");
			String style = getStyle(cell.getStyle());
			if(style.equals("resource") || style.equals("condition"))
				continue;
			for (int j = i+1; j < vertices.length; j++){
				mxCell cell2 = (mxCell) vertices[j];
				String name2 = ((String) cell2.getValue()).toLowerCase().replace(" ", "");
				System.out.println(name+" "+name2);
				String style2 = getStyle(cell2.getStyle());
				if(style2.equals("resource") || style2.equals("condition"))
					continue;
				if(name2.equals(name)){
					dupNameSet.add(name);
				}
			}
		}
		
		if(dupNameSet.size() > 0){
			result.replace("flag", false);
			String data = "";
			Iterator iter = dupNameSet.iterator();
			while(iter.hasNext()){
				data = data + iter.next()+" ";
			}
			result.replace("message", "DupNames = "+data);
		}
		return result;
	}

	private HashMap<String, Object> isNullValue(mxGraphComponent graphComponent, mxGraph graph, mxAnalysisGraph aGraph) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("flag", true);
		result.put("message", "");
		int nullNum = 0;
		for (int i = 0; i < vertices.length; i++){
			mxCell cell = (mxCell) vertices[i];
			String name = (String) cell.getValue();
			if(name.equals("")){
				nullNum++;
			}
		}
		
		if(nullNum > 0){
			result.replace("flag", false);
			result.replace("message", "Null Name Number = "+nullNum);
		}
		return result;
	}

	private HashMap<String, Object> isSameTaskVariable(mxGraphComponent graphComponent, mxGraph graph,
			mxAnalysisGraph aGraph) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("flag", true);
		result.put("message", "");
		ArrayList<String> taskNameList = new ArrayList<String>();
		ArrayList<String> varNameList = new ArrayList<String>();
				
		for (int i = 0; i < vertices.length; i++){
			mxCell cell = (mxCell) vertices[i];
			String style = getStyle(cell.getStyle());
			if(style.equals("task")){
				if(cell.getValue().toString().contains("get"))
					taskNameList.add(cell.getValue().toString());
			}else if(style.equals("resource") || style.equals("condition")){
				varNameList.add(cell.getValue().toString().toLowerCase().split(" ")[0]);
			}
		}
		
		String message = "";
		for(int i = 0 ; i<taskNameList.size(); i++){
			boolean flag = false;
			for(int j = 0 ; j<varNameList.size(); j++){
				if(taskNameList.get(i).toLowerCase().replaceFirst("get", "").replaceFirst("act", "").equals(varNameList.get(j))){
					flag = true;
					break;
				}
			}
			if(!flag)
				message = message + "no variable for "+taskNameList.get(i)+ " (task)\n";
		}
		
		for(int i = 0 ; i<varNameList.size(); i++){
			boolean flag = false;
			for(int j = 0 ; j<taskNameList.size(); j++){
				if(taskNameList.get(i).toLowerCase().replaceFirst("get", "").replaceFirst("act", "").equals(varNameList.get(j))){
					flag = true;
					break;
				}
			}
			if(!flag)
				message = message + "no used vars =  "+varNameList.get(i)+ "\n";
		}
		
		if(!message.equals("")){
			result.replace("flag", false);
			result.replace("message", message);
		}
		
		return result;
	}

	private String getStyle(String styleType) {
		String style = "";
		if(styleType.contains("ellipse;shape=cloud;fillColor=skyblue")){
			style = "softgoal";
		}else if(styleType.contains("shape=hexagon;fillColor=yellow")){
			style = "task";
		}else if(styleType.contains("rectangle;shape=doubleRectangle")){
			style = "condition";
		}else if(styleType.contains("swimlane")){
			style = "domain";
		}else if(styleType.contains("rectangle")){
			style = "resource";	
		}else if(styleType.contains("ellipse")){
			style = "hardgoal";
		}
		return style;
	}

	private HashMap<String, Object> isConnected(mxGraphComponent graphComponent, mxGraph graph, mxAnalysisGraph aGraph) {
		HashMap<String, Object> result = new HashMap<String, Object>();		
		String unConnectedVertice = "";
		int vertexNum = vertices.length;

		if (vertexNum == 0)
		{
			throw new IllegalArgumentException();
		}

		//data preparation
		int connectedVertices = 1;
		int[] visited = new int[vertexNum];
		visited[0] = 1;

		for (int i = 1; i < vertexNum; i++)
		{
			visited[i] = 0;
		}

		ArrayList<Object> queue = new ArrayList<Object>();
		queue.add(vertices[0]);

		//repeat the algorithm until the queue is empty
		while (queue.size() > 0)
		{
			//cut out the first vertex
			Object currVertex = queue.get(0);
			queue.remove(0);

			//fill the queue with neighboring but unvisited vertexes
			Object[] neighborVertices = aGraph.getOpposites(aGraph.getEdges(currVertex, null, true, true, false, true), currVertex, true,
					true);

			for (int j = 0; j < neighborVertices.length; j++)
			{
				//get the index of the neighbor vertex
				int index = 0;

				for (int k = 0; k < vertexNum; k++)
				{
					if (vertices[k].equals(neighborVertices[j]))
					{
						index = k;
					}
				}

				if (visited[index] == 0)
				{
					queue.add(vertices[index]);
					visited[index] = 1;
					connectedVertices++;
				}
			}
		}
		for (int i = 1; i < vertexNum; i++)
		{
			if(visited[i] == 0){
				mxCell cell = (mxCell) vertices[i];
				unConnectedVertice = unConnectedVertice + cell.getValue()+";";
			}
		}
		
		// if we visited every vertex, the graph is connected
		if (connectedVertices == vertexNum)
		{
			result.put("flag", true);
		}
		else
		{
			result.put("flag", false);
		}
		result.put("message", unConnectedVertice);
		return result;
	}

}
