package codegen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import codegen.editor.CodeEditor;
import editor.GraphEditor;
import element.Goal;
import real.JSonLoaderExample;

public class MAPEGeneratorByBW {
	public static String jsonFile = "./20180222/test.json"; //Very Simple Example (task and goal)
	public static String path = "./20180222/test/";
	public static String packageName = "test";
    public static HashMap<String, Goal> goalList = new HashMap<String,Goal>();
    public static String reg = "(?<=[-+*/()])|(?=[-+*/()])";
	public static Pattern p;
    
	public static void main(String[] params) throws IOException {
		
		if(params.length > 0){		
			String path = params[0];
			jsonFile = path;
			packageName = params[1];
		}
		p = Pattern.compile(reg);
		loadJSon();
		writeKB();		
		writeMonitor();
		writeAnalyzer();
		writePlanner();
		writeExecutor();
	}
	
	public void transformJava(String filename, CodeEditor ce, GraphEditor ge) throws IOException, InterruptedException {
		this.jsonFile = filename.replace(".mxe", ".json");
		String fileName = jsonFile.split("\\\\")[jsonFile.split("\\\\").length-1];
		String outputPath = this.jsonFile.replace(fileName, "");		
		this.path = outputPath;
		
		p = Pattern.compile(reg);
		loadJSon();
		writeKB();		
		writeMonitor();
		writeAnalyzer();
		writePlanner();
		writeExecutor();
		
		String[] outputPaths = new String[6];
		outputPaths[0] = fileName;
		outputPaths[1] = outputPath+"Monitor.java";
		outputPaths[2] = outputPath+"Analyzer.java";
		outputPaths[3] = outputPath+"Planner.java";
		outputPaths[4] = outputPath+"Executor.java";
		outputPaths[5] = outputPath+"Knowledgebase.java";
		ce.openWindow(outputPaths,ge);

	}
	
	
	private static void writeExecutor() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(path+"Executor.java"));
		
		// 0. Basic Code
		bw.write("package "+packageName+";\n\n");
		
		bw.write("import java.util.ArrayList;\n");
		bw.write("import element.Goal;\n\n");
		
		bw.write("public class Executor{\n\n");
		
		bw.write("\tKnowledgebase kb; \n");
		bw.write("\tArrayList<Goal> taskList; \n\n");
		bw.write("\tArrayList<String> methodList;  \n\n");
				
		bw.write("\tpublic void Executor(){\n");
		bw.write("\t\tkb = new Knowledgebase();\n");
		bw.write("\t\ttaskList = new ArrayList<Goal>();\n");
		bw.write("\t}\n\n");
				
		bw.write("\tpublic void setTasks(ArrayList<String> taskList){\n");
		bw.write("\t\tfor(int i = 0 ; i>taskList.size(); i++){\n"
				+ "\t\t\tGoal task = kb.getGoal(taskList.get(i));\n"
				+ "\t\t\tthis.taskList.add(task);\n"
				+ "\t\t}\n"
				+ "\t\texecuting();\n");
		bw.write("\t}\n");
		
		bw.write("\tprivate void executing() {\n"
				+ "\t\tfor(int i = 0 ; i <taskList.size(); i++){\n"
				+ "\t\t\tGoal task = taskList.get(i);\n"
				+ "\t\t\tString taskName = task.getNodeName().split(\"_\")[1].split(\" \")[0];\n"
				+ "\t\t\tmethodList.add(taskName);\n"
				+ "\t\t}\n"
				+ "\t}\n");
		
		
		bw.write("\t\t // Auto generated code START \n\n");
		
		Iterator<String> goalIter = goalList.keySet().iterator();
		while(goalIter.hasNext()){
			String goalID = goalIter.next();
			Goal goal = goalList.get(goalID);
			String goalName = goal.getNodeName();
			String nodeType = goal.getNodeType();
			String detailInfo = goal.getDetailInfo();
			if(!(nodeType.equals("task") && detailInfo.toLowerCase().contains("tasktype=act"))) // Will be extended P,C,G
				continue;
			String input = detailInfo.split(";;")[1].toLowerCase().replace("inputinfo=", "");
			String output = detailInfo.split(";;")[2].toLowerCase().replace("outputinfo=", "");
			String taskName = setFirstLower(goalName.replace("E_",""));
//			String varName = setFirstLower(taskName.replace("get", ""));
			String inputType = input.split("/")[0];
			String inputName = input.split("/")[1];
			String outputType = output.split("/")[0];
			String outputName = output.split("/")[1];
			
			bw.write("\t /*"+goal.getNodeID()+" "+taskName+"\n");
			bw.write("\t /*type: "+goal.getNodeType()+"\n");
			if(outputType.equals("void")){
				bw.write("\t /*no return*/\n");
			}else{
				bw.write("\t /*return: "+outputType+" "+outputName+"*/\n");
			}			
			
			if(inputType.equals("void")){
				bw.write("\tprivate static "+outputType+" "+taskName+"(){\n");				
			}else{
				bw.write("\tprivate static "+outputType+" "+taskName+"("+inputType+" "+inputName+"){\n");				
			}
			
			if(!outputType.equals("void")){
				bw.write("\t\t"+outputType+" "+outputName+" = null;\n");
			}
			
			
			bw.write("\t\t/* Write Your Code here\n"
					+ "\t\t/* to "+goal.getNodeName() +"\n"
					+"\t\t/* parent goal: "+goalList.get(goal.getParentNode()).getNodeName()+"\n\n"
					+"\t\t\t/* your code start */\n\n\n\n");
			
			bw.write("\t\t\t/*your code end*/\n");
			if(!outputType.equals("void")){
				bw.write("\t\treturn result;\n");
			}			
			bw.write("\t}\n\n");
		}
		bw.write("\n\t\t // Auto generated code END \n\n");
			
		
		bw.write("}");
		
		bw.close();
	}

	private static void writePlanner() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(path+"Planner.java"));
		Goal goal = new Goal();
		
		
		// 0. Basic Code
		bw.write("package "+packageName+";\n\n");
		
		bw.write("import java.util.ArrayList;\n");
		bw.write("import java.util.HashMap;\n");
		bw.write("import java.util.Iterator;\n");
		bw.write("import element.Goal;\n");
		
		bw.write("public class Planner{\n\n");
		
		bw.write("\tKnowledgebase kb; \n");
		bw.write("\tExecutor executor; \n");
		bw.write("\tGoal violatedGoal; \n\n");
				
		bw.write("\tpublic void Planner(){\n");
		bw.write("\t\tkb = new Knowledgebase();\n");
		bw.write("\t\texecutor = new Executor();\n");
		bw.write("\t\tviolatedGoal = null;\n");
		bw.write("\t}\n\n");
		
		bw.write("\tpublic void alertGoalViolation(String violatedCondition){\n");
		bw.write("\t\tIterator<String> iter = kb.goalList.keySet().iterator(); \n");
		
		bw.write("\t\twhile(iter.hasNext()){\n"
				+ "\t\t\tGoal goal = kb.goalList.get(iter.next());\n"
				+ "\t\t\tif(goal.getNodeType().equals(\"condition\") || goal.getNodeType().equals(\"resource\")\n"
						+ "\t\t\t\t\t	&& goal.getNodeName().toLowerCase().replace(\" \",\"\").equals(violatedCondition)){\n"
						+ "\t\t\t\t	violatedGoal = goal;\n"
						+ "\t\t\t\tbreak;\n"
						+ "\t\t\t}else{\n"
						+ "\t\t\t\tcontinue;\n"
						+ "\t\t\t}\n");
		bw.write("\t\t}\n\n");
		bw.write("\t\tplanning(); \n\n");
		bw.write("\t}\n\n");
		
		bw.write("\tpublic void planning(){\n");
		bw.write("\t\tHashMap<String,Goal> strategyList = getStrategies(); \n");
		bw.write("\t\tArrayList<String> taskList = selectTasks(strategyList); \n");
		bw.write("\t\texecutor.setTasks(taskList); \n\n");
		bw.write("\t\tviolatedGoal = null; \n\n");
		bw.write("\t}\n\n");
		
		
		bw.write("\tpublic HashMap<String,Goal> getStrategies(){\n");
		bw.write("\t\tHashMap<String,Goal> strategyList = new HashMap<String,Goal>(); \n");
		bw.write("\t\tArrayList<String> subGoalList = violatedGoal.getRelation().getSubGoals(); \n");
		bw.write("\t\tfor(int i = 0 ; i<subGoalList.size(); i++){ \n");
		bw.write("\t\t\tGoal subGoal = kb.extractGoals().get(subGoalList.get(i)); \n");
		bw.write("\t\t\tif(!subGoal.getNodeType().equals(\"hardgoal\")) \n");
		bw.write("\t\t\t\tcontinue;\n");	
		bw.write("\t\t\tstrategyList.put(subGoal.getNodeID(), subGoal); \n\n");
		bw.write("\t\t}");
		bw.write("\treturn strategyList;\n\n");
		bw.write("\t}\n\n");
		
		bw.write("\tpublic ArrayList<String> selectTasks(HashMap<String,Goal> strategyList){\n");
		bw.write("\t\tArrayList<String> taskList = new ArrayList<String>(); \n\n");
		
		bw.write("\t\tfor(int i = 0 ; i<strategyList.size(); i++){ \n");
		bw.write("\t\t\tGoal strategy = kb.getGoal(strategyList.get(i).getNodeID()); \n");
		bw.write("\t\t\tArrayList<String> subGoalList = strategy.getRelation().getSubGoals(); \n\n");
		
		bw.write("\t\t\tif(strategy.getRelation().equals(\"AND\")){\n");
		bw.write("\t\t\t\ttaskList.addAll(subGoalList);\n");
		bw.write("\t\t\t}else{\n");
		bw.write("\t\t\t\tGoal firstTask = evaluateTasks(subGoalList);\n");
		bw.write("\t\t\t\ttaskList.add(firstTask.getNodeID());\n");
		bw.write("\t\t\t}\n\t\t}\n");
		
		bw.write("\t\treturn taskList;\n\t}\n\n");
		
		
		bw.write("\tpublic Goal evaluateTasks(ArrayList<String> tasksList){\n");
		bw.write("\t\tGoal firstTask = new Goal();\n");
		bw.write("\t\tdouble max = -1;\n\n");
		bw.write("\t\tfor(int i = 0 ; i<tasksList.size(); i++){ \n");
		bw.write("\t\t\tGoal task = kb.getGoal(tasksList.get(i));\n");
		bw.write("\t\t\tif(max < task.getEvaluation()){\n");
		bw.write("\t\t\t\tmax = task.getEvaluation();\n");
		bw.write("\t\t\t\tfirstTask = task;\n");
		bw.write("\t\t\t}\n\t\t}\n");
		
		bw.write("\t\treturn firstTask;\n\t}\n");
			
		
		bw.write("}");
		
		bw.close();
	}

	private static void writeAnalyzer() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(path+"Analyzer.java"));
		
		// 0. Basic Code
		bw.write("package "+packageName+";\n\n");
		bw.write("public class Analyzer{\n\n");
		
		bw.write("\tKnowledgebase kb; \n");
		bw.write("\tPlanner planner; \n\n");
				
		bw.write("\tpublic void Analyzer(){\n");
		bw.write("\t\tkb = new Knowledgebase();\n");
		bw.write("\t\tplanner = new Planner();\n");
		bw.write("\t}\n\n");
		

		bw.write("\tpublic void analylzing(){\n");
		bw.write("\t// Auto generated code START \n\n");
		bw.write("\t\twhile(true){\n");
		
		int condNum = 0;
		Iterator<String> goalIter = goalList.keySet().iterator();
		while(goalIter.hasNext()){
			String goalID = goalIter.next();
			Goal goal = goalList.get(goalID);
			String goalName = goal.getNodeName();
			String nodeType = goal.getNodeType();
			if(!nodeType.equals("resource") && !nodeType.equals("condition")) // Will be extended P,C,G
				continue;			
			
			
			String[] data = getCondition(goalName);
			String variable = setFirstLower(data[0]);			
			String req = data[1];
			String cond = data[2];
			
			bw.write("\t\t /* "+goal.getNodeID()+" "+goal.getNodeType() + " - "+goal.getParentNode()+" violation\n"
					+ "\t\t /* parent goal: "+goal.getParentNode()+" "+goalList.get(goal.getParentNode()).getNodeName()+"\n"
					+ "\t\t /* type: "+goal.getNodeType()+"\n"
					+ "\t\t /* input: "+data[0]+", req"+condNum+"("+data[1]+")*/\n\n");
			
			bw.write("\t\t\tif(kb."+setFirstLower(variable)+" " +cond + " "+req+")\n");
			bw.write("\t\t\t\tplanner.alertGoalViolation(\""+variable+""+cond+""+req+"\");\n");
			
			condNum++;
		}

		bw.write("\t\t}\n");
		
		bw.write("\n\t // Auto generated code END \n");
		bw.write("\t}\n");
		
		bw.write("}");
		
		bw.close();
	}

	private static void writeMonitor() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(path+"Monitor.java"));
		
		// 0. Basic Code
		bw.write("package "+packageName+";\n\n");
		bw.write("public class Monitor{\n\n");
		
		bw.write("\tKnowledgebase kb; \n\n");
				
		bw.write("\tpublic void Monitor(){\n");
		bw.write("\t\tkb = new Knowledgebase();\n");
		bw.write("\t}\n\n");
		

		bw.write("\tpublic void monitoring(){\n");
		bw.write("\t\twhile(true){\n");
		bw.write("\t\t // Auto generated code START \n\n");
		
		HashMap<String, Goal> monitorList = new HashMap<String, Goal>(); 
		Iterator<String> goalIter = goalList.keySet().iterator();
		while(goalIter.hasNext()){
			String goalID = goalIter.next();
			Goal goal = goalList.get(goalID);
			String goalName = goal.getNodeName();
			String nodeType = goal.getNodeType();
			String detailInfo = goal.getDetailInfo();
			if(!(nodeType.equals("task") && (detailInfo.toLowerCase().contains("tasktype=probe")
					||detailInfo.toLowerCase().contains("tasktype=gauge")))) // Will be extended P,C,G
				continue;
			String input = detailInfo.split(";;")[1].toLowerCase().replace("inputinfo=", "");
			String output = detailInfo.split(";;")[2].toLowerCase().replace("outputinfo=", "");
			String taskName = setFirstLower(goalName.replace("M_",""));
			String inputType = input.split("/")[0];
			String inputName = input.split("/")[1];
			String outputType = output.split("/")[0];
			String outputName = output.split("/")[1];						
//			
//			String varName = setFirstLower(taskName.replace("get", ""));
			if(inputType.equals("void")){
				bw.write("\t\t\tkb."+setFirstLower(outputName) + " = " +taskName+"();\n");
			}else{
				bw.write("\t\t\tkb."+setFirstLower(outputName) + " = " +taskName+"("+inputName+");\n");
			}
			monitorList.put(goalID, goal);
		}
		bw.write("\n\t\t // Auto generated code END \n\n");
		
		bw.write("\t\t}\n");
		bw.write("\t}\n\n");

		bw.write("\t // Auto generated code START \n\n");
		goalIter = monitorList.keySet().iterator();
		while(goalIter.hasNext()){
			Goal goal = monitorList.get(goalIter.next());
			String taskName = setFirstLower(goal.getNodeName().replace("M_",""));
//			String varName = setFirstLower(taskName.replace("get", ""));
			String detailInfo = goal.getDetailInfo();
			String input = detailInfo.split(";;")[1].toLowerCase().replace("inputinfo=", "");
			String output = detailInfo.split(";;")[2].toLowerCase().replace("outputinfo=", "");
			String inputType = input.split("/")[0];
			String inputName = input.split("/")[1];
			String outputType = output.split("/")[0];
			String outputName = output.split("/")[1];		
			
			bw.write("\t /*"+goal.getNodeID()+" "+taskName+"\n");
			bw.write("\t /*type: "+goal.getNodeType()+"\n");
			bw.write("\t /*return: "+outputType+" "+outputName+"*/\n");
			if(inputType.equals("void")){
				bw.write("\tprivate static "+outputType+" "+taskName+"(){\n");
			}else{
				bw.write("\tprivate static "+outputType+" "+taskName+"("+inputType+" "+inputName+"){\n");
			}
			if(outputType.equals("double") || outputType.equals("int"))
				bw.write("\t\t"+outputType+" "+outputName+" = 0;\n");
			else if(outputType.equals("String"))
				bw.write("\t\t"+outputType+" "+outputName+" = \"\";\n");
			else if(outputType.equals("bool"))
				bw.write("\t\t"+outputType+" "+outputName+" = null;\n");
			bw.write("\t\t/* Write Your Code here\n"
					+ "\t\t/* to "+goal.getNodeName() +"\n"
					+"\t\t/* parent goal: "+goalList.get(goal.getParentNode()).getNodeName()+"\n\n"
					+"\t\t\t/* your code start */\n\n\n\n");
			
			bw.write("\t\t\t/*your code end*/\n");
			bw.write("\t\treturn "+outputName+";\n");
			bw.write("\t}\n");
		}


		bw.write("\n\t // Auto generated code END \n\n");
		
		
		bw.write("}");
		
		bw.close();
	}
	private static void writeKB() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(path+"Knowledgebase.java"));
		
		// 0. Basic Code
		bw.write("package "+packageName+";\n\n");		
		
		bw.write("import java.io.IOException;\n");
		bw.write("import java.util.HashMap;\n");
		bw.write("import element.Goal;\n");
		bw.write("import element.util.JSonLoaderExample;\n\n");
		
		bw.write("public class Knowledgebase{\n\n");
		
		bw.write("\tpublic static HashMap<String, Goal> goalList = new HashMap<String,Goal>();\n\n");
		
		// 1. Declaration for Structure
		Set<String> inputVar = new HashSet<String>();
		HashMap<String, Goal> resourceList = new HashMap<String, Goal>(); 
		int condNum = 0;
		Iterator<String> goalIter = goalList.keySet().iterator();
		while(goalIter.hasNext()){
			String goalID = goalIter.next();
			Goal goal = goalList.get(goalID);
			String goalName = goal.getNodeName();
			String nodeType = goal.getNodeType();
			if(!nodeType.equals("resource") && !nodeType.equals("condition")) // Will be extended P,C,G
				continue;			
			
			String detailInfo = goal.getDetailInfo();
			
			String[] data = getCondition(goalName);
			String variable = setFirstLower(data[0]);			
			String req = data[1];
			String cond = data[2];
			String dataType = "";
			if(nodeType.equals("resource")){
				dataType = "boolean";
			}else{
				dataType = detailInfo.split(";;")[0].toLowerCase().replace(";condname=", "").split("/")[0];
			}
			
			if(!inputVar.contains(variable))			
				bw.write("\tstatic "+dataType+" "+variable+";\n");
			
			bw.write("\tstatic String cond"+condNum+" = \""+cond+"\";\n");
			bw.write("\tstatic "+dataType+" req"+condNum+" = "+req+";\n");
			
			inputVar.add(variable);
			resourceList.put(goalID, goal);
			condNum++;
		}
		
		bw.write("\n");
		
		// 2. Initialization
		bw.write("\tpublic void KnowledgeBase(){\n");

		bw.write("\t // Auto generated code START \n\n");
				
		Iterator<String> iter = resourceList.keySet().iterator();
		condNum = 0;
		while(iter.hasNext()){
			Goal goal = goalList.get(iter.next());
			String[] data = getCondition(goal.getNodeName());

			bw.write("\t\t /* "+goal.getNodeID()+" "+goal.getNodeType() + " - "+goal.getParentNode()+" violation\n"
					+ "\t\t /* parent goal: "+goal.getParentNode()+" "+goalList.get(goal.getParentNode()).getNodeName()+"\n"
					+ "\t\t /* type: "+goal.getNodeType()+"\n"
					+ "\t\t /* input: "+data[0]+", req"+condNum+"("+data[1]+")*/\n\n");
			
			bw.write("\t\t"+setFirstLower(data[0])+" = 0;\n");
			bw.write("\t\treq"+condNum+" = "+data[1]+";\n");
			
			condNum++;
		}
		bw.write("\n\t// Auto generated code END \n");
		bw.write("\t }\n\n\n");
		
		bw.write("\tprivate static void loadGoalModel(String fileName){\n");
		
		bw.write("\t\tJSonLoaderExample jsload = new JSonLoaderExample();\n"
				+ "\t\ttry {\n"
				+ "\t\t\tgoalList = jsload.readGoal(fileName);\n"
				+ "\t\t\tjsload.relValidateGoals(goalList);\n"
				+ "\t\t\tjsload.printGoals(goalList);\n"
				+ "\t\t} catch (IOException e) {\n"
				+ "\t\t\te.printStackTrace();\n"
				+ "\t\t}\n");		
		bw.write("\t}\n\n");
		
		bw.write("\tpublic HashMap<String,Goal> extractGoals(){\n");		
		bw.write("\t\treturn goalList;\n\n");		
		bw.write("\t}\n\n");
		
		bw.write("\tpublic Goal getGoal(String goalID){\n");
		bw.write("\t\tfor(int i = 0 ; i<goalList.size(); i++){;\n");
		bw.write("\t\t\tGoal goal = goalList.get(i);\n");
		bw.write("\t\t\tif(goal.getNodeID().toLowerCase().equals(goalID))\n");
		bw.write("\t\t\t\treturn goal;\n");
		bw.write("\t\t}\n");		
		bw.write("\t\treturn null;\n");
		bw.write("\t}\n\n");
		
		
		bw.write("}");
		
		bw.close();
		
	}
		
	
	private static String setFirstLower(String data) {
		if(data.contains(" ")){
			String[] dataArray = data.split(" ");
			data = dataArray[0];
			for(int i = 1 ; i<dataArray.length; i++){
				dataArray[i] = dataArray[i].replace(String.valueOf(dataArray[i] .charAt(0)), String.valueOf(dataArray[i] .charAt(0)).toUpperCase());
				
				data = data+dataArray[i];
			}			
		}
		data = data.replace(String.valueOf(data.charAt(0)), String.valueOf(data.charAt(0)).toLowerCase());
		return data;
		
	}
	private static String[] getCondition(String goalName) {
		String result = "";
		String[] data = goalName.split(" ");
		String variable = data[0];
		String cond = data[1].split("[a-zA-Z0-9]")[0];
		String req = data[1].replace(cond, "");
		Matcher m = p.matcher(goalName);
//		if(m.find()){
//			cond = m.group();
//		}else{
//			System.err.println("Wrong Condition");
//		}
		result = variable+"\t"+req+"\t"+cond;
		return result.split("\t");
	}
	private static void loadJSon() {
		JSonLoaderExample jsload = new JSonLoaderExample();        
        try {
        	goalList = jsload.readGoal(jsonFile);
        	jsload.relValidateGoals(goalList);
//        	jsload.printGoals(goalList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

