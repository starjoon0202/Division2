package codegen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.speedment.common.codegen.controller.AutoJavadoc;
import com.speedment.common.codegen.internal.java.JavaGenerator;
import com.speedment.common.codegen.model.Class;
import com.speedment.common.codegen.model.Field;
import com.speedment.common.codegen.model.File;
import com.speedment.common.codegen.model.Method;

import element.Goal;
import real.JSonLoaderExample;

public class Generator {
	static String jsonFile = "./data-misoo/20180206/vehsp.json"; //Very Simple Example (task and goal)
    public static HashMap<String, Goal> goalList = new HashMap<String,Goal>();
    

	public static void extractJavaFile(String path) throws IOException {
		
		jsonFile = path;		
		jsonFile = jsonFile.replace(".mxe", ".json");
		loadJSon(); 
				
		String compName = jsonFile.split("/")[jsonFile.split("/").length-1].replace(".json", "");
		compName = compName.split("\\\\")[compName.split("\\\\").length-1].replace(".mxe", "");
				
		File file = File.of(compName+".java");	
		Class cls = Class.of(compName).public_();
		Iterator<String> iter = goalList.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();			
			Goal goal = goalList.get(key);
			if(goal.getNodeType().toLowerCase().equals("pseudo"))
				continue;
			
			String name = goal.getNodeName().replace(goal.getNodeName().charAt(0), goal.getNodeName().toLowerCase().charAt(0));
			String type = goal.getNodeType().toLowerCase();
						
			switch(type){
			case "task":
				if(goal.getCondition().getPostCondition().size() != 0){
					String methodCall = "";
					for(int i = 0 ; i< goal.getCondition().getPostCondition().size(); i++){
						String postCond = goal.getCondition().getPostCondition().get(i);
						Goal postGoal = goalList.get(postCond);
						if(!postGoal.getNodeType().equals("task"))
							continue;		
						
						String postName = postGoal.getNodeName().replace(postGoal.getNodeName().charAt(0), postGoal.getNodeName().toLowerCase().charAt(0));
						methodCall = methodCall + postName+"();";
						
					}
					cls.add(Method.of(name.replace(" ", "").replace(".", ""),void.class)
							.public_()
							.add(Field.of("params", Object.class))
							.add("// Written Your Code;")
							.add(methodCall)
						);					
					
				}else if(goal.getRelation().getSubGoals().size() == 0){
					cls.add(Method.of(name.replace(" ", "").replace(".", ""),void.class)
							.public_()
							.add(Field.of("params", Object.class))
						);						
				}else{
					String flagSet = "if(";
					for(int i = 0 ; i<goal.getRelation().getSubGoals().size()-1; i++){
						Goal subGoal = goalList.get(goal.getRelation().getSubGoals().get(i));
						if(subGoal.getNodeType().toLowerCase().equals("resource")){
							String condString = subGoal.getNodeName().replace(subGoal.getNodeName().charAt(0), subGoal.getNodeName().toLowerCase().charAt(0));
							flagSet = flagSet +subGoal.getNodeName()+" && ";
						}						
					}
					flagSet = flagSet.substring(0,flagSet.length()-4)+"){\n\n}";
					cls.add(Method.of(name.replace(" ", "").replace(".", ""),void.class)
							.public_()
							.add(Field.of("params", Object.class))
							.add(flagSet)
							.add("// you should not write the code under the IF case")
						);		
				}
				break;
			case "resource":
				if(name.contains("=")){
					name = name.split("=")[0];
				}
				if(cls.getFields().contains(Field.of(name.replace(" ", "").replace(".", ""),Object.class).public_())){
					System.err.println("same name field");
					break;
				}
				cls.add(Field.of(name.replace(" ", "").replace(".", ""),Object.class)
						.public_()
						);
				break;
			}
			
			
			
		}
		file.add(cls).call(new AutoJavadoc<>());
		String template = new JavaGenerator().on(file).get();
		System.out.println(template);
		
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile.replace(".json", ".java")));
		bw.write(template);
		bw.close();
		
	}
    
	public static void main(String[] params) throws IOException {
		loadJSon();

		String compName = jsonFile.split("/")[jsonFile.split("/").length-1].replace(".json", "");
		compName = compName.split("\\\\")[compName.split("\\\\").length-1].replace(".mxe", "");
				
		File file = File.of(compName+".java");	
		Class cls = Class.of(compName).public_();
		Iterator<String> iter = goalList.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();			
			Goal goal = goalList.get(key);
			if(goal.getNodeType().toLowerCase().equals("pseudo"))
				continue;
			
			String name = goal.getNodeName().replace(goal.getNodeName().charAt(0), goal.getNodeName().toLowerCase().charAt(0));
			String type = goal.getNodeType().toLowerCase();
						
			switch(type){
			case "task":
				if(goal.getCondition().getPostCondition().size() != 0){
					String methodCall = "";
					for(int i = 0 ; i< goal.getCondition().getPostCondition().size(); i++){
						String postCond = goal.getCondition().getPostCondition().get(i);
						Goal postGoal = goalList.get(postCond);
						if(!postGoal.getNodeType().equals("task"))
							continue;		
						
						String postName = postGoal.getNodeName().replace(postGoal.getNodeName().charAt(0), postGoal.getNodeName().toLowerCase().charAt(0));
						methodCall = methodCall + postName+"();";
						
					}
					cls.add(Method.of(name.replace(" ", "").replace(".", ""),void.class)
							.public_()
							.add(Field.of("params", Object.class))
							.add("// Written Your Code;")
							.add(methodCall)
						);					
					
				}else if(goal.getRelation().getSubGoals().size() == 0){
					cls.add(Method.of(name.replace(" ", "").replace(".", ""),void.class)
							.public_()
							.add(Field.of("params", Object.class))
						);						
				}else{
					String flagSet = "if(";
					for(int i = 0 ; i<goal.getRelation().getSubGoals().size()-1; i++){
						Goal subGoal = goalList.get(goal.getRelation().getSubGoals().get(i));
						if(subGoal.getNodeType().toLowerCase().equals("resource")){
							String condString = subGoal.getNodeName().replace(subGoal.getNodeName().charAt(0), subGoal.getNodeName().toLowerCase().charAt(0));
							flagSet = flagSet +subGoal.getNodeName()+" && ";
						}						
					}
					flagSet = flagSet.substring(0,flagSet.length()-4)+"){\n\n}";
					cls.add(Method.of(name.replace(" ", "").replace(".", ""),void.class)
							.public_()
							.add(Field.of("params", Object.class))
							.add(flagSet)
							.add("// you should not write the code under the IF case")
						);		
				}
				break;
			case "resource":
				if(name.contains("=")){
					name = name.split("=")[0];
				}
				if(cls.getFields().contains(Field.of(name.replace(" ", "").replace(".", ""),Object.class).public_())){
					System.err.println("same name field");
					break;
				}
				cls.add(Field.of(name.replace(" ", "").replace(".", ""),Object.class)
						.public_()
						);
				break;
			}
			
			
			
		}
		file.add(cls).call(new AutoJavadoc<>());
		String template = new JavaGenerator().on(file).get();
		System.out.println(template);
		
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile.replace(".json", ".java")));
		bw.write(template);
		bw.close();
		
	}
	private static void loadJSon() {
		JSonLoaderExample jsload = new JSonLoaderExample();        
        try {
        	goalList = jsload.readGoal(jsonFile);
        	jsload.relValidateGoals(goalList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
