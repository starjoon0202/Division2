package editor.rose;

import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

public class TypeWriter {

	public void inputTypeNewWindow(mxGraphComponent graphComponent, mxGraph graph, mxCell cell)	
	{
			
		String styleType = graph.getModel().getStyle(cell);
		String style = "";
		switch(styleType){							
		case "ellipse":	// HardGoal
			style = "HardGoal";
			break;
		case "ellipse;shape=cloud;fillColor=skyblue":	// SoftGoal
			style = "SoftGoal";
			break;
		case "shape=hexagon;fillColor=yellow": //Task
			style = "Task";
			break;
		case "rectangle":	// Resource
			style = "Resource";		
			break;
		case "rectangle;shape=doubleRectangle":	// Condition
			style = "Condition";
			break;
		case "swimlane":	// Condition
			style = "Domain";
			break;				
		}
		JTextField styleField = new JTextField(style);
		if(style.equals("HardGoal") || style.equals("SoftGoal") || style.equals("Domain")){
//			JOptionPane.showMessageDialog(graphComponent, style+"\n"+styleType, mxResources.get("style"),
//					JOptionPane.PLAIN_MESSAGE, null);									
		}else if(style.equals("Task")){
			JTextField field0 = new JTextField(style);field0.setEditable(false);
			JTextField field1 = new JTextField(""); 
			JTextField field2 = new JTextField("");
			JTextField field3 = new JTextField("");
			Object[] message = {mxResources.get("style"), field0, "TYPE (Probe,Gauge,Act)", field1, "INPUT (Type/Name)", field2, "OUTPUT (Type/Name)",field3};
			String value = "";
			String taskName = "";
			while(true){
				int option = JOptionPane.showConfirmDialog(graphComponent, message, style+" EDITOR", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION)
				{
				    String value1 = field1.getText().toLowerCase().replace(" ", "");
				    if(!(value1.equals("probe") || value1.equals("gauge") || value1.equals("act"))){
				    	JOptionPane.showMessageDialog(graphComponent, "Select Probe, Gauge or Act");
				    	continue;
				    }
				    String value2 = field2.getText().toLowerCase().replace(" ", "");
				    String[] inputs = value2.split("/");
				    if(inputs.length <= 1){
				    	JOptionPane.showMessageDialog(graphComponent, "Please split INPUT between Type and Name by \"/\"\n(if void, input \"void\\void\")");
				    	continue;
				    }
				    String value3 = field3.getText().toLowerCase().replace(" ", "");;
				    inputs = value3.split("/");
				    if(inputs.length <= 1){
				    	JOptionPane.showMessageDialog(graphComponent, "Please split OUTPUT between Type and Name by \"/\"\n(if void, input \"void\\void\")");
				    	continue;
				    }
				    String outputName = value3.split("/")[1];
				    if(value1.equals("probe")){										    	
				    	taskName = "get"+ outputName.replace(String.valueOf(outputName.charAt(0)), String.valueOf(outputName.charAt(0)).toUpperCase());
				    }else if(value1.equals("act")){										    	
				    	taskName = "execute"+ outputName.replace(String.valueOf(outputName.charAt(0)), String.valueOf(outputName.charAt(0)).toUpperCase());
				    }else if(value1.equals("gauge")){										    	
				    	taskName = "gauge"+ outputName.replace(String.valueOf(outputName.charAt(0)), String.valueOf(outputName.charAt(0)).toUpperCase());
				    }
				    value = styleType+";;"+"tasktype="+value1+";;inputinfo="+value2+";;outputinfo="+value3;
					break;
				}else if(option == JOptionPane.CANCEL_OPTION){
					cell.removeFromParent();
					break;	
				}		
			}
			cell.setValue(taskName);
			cell.setStyle(value);
		}else if(style.equals("Resource")){
			JTextField field0 = new JTextField(style);field0.setEditable(false);
			JTextField field1 = new JTextField(""); 
			JTextField field2 = new JTextField("");
			Object[] message = {mxResources.get("style"), field0, "NAME", field1, "COND", field2};
			
			String value = "";
			while(true){
				int option = JOptionPane.showConfirmDialog(graphComponent, message, style+" EDITOR", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION)
				{
				    String value1 = field1.getText().toLowerCase().replace(" ", "");
				    if(value1.length() <= 2){
				    	JOptionPane.showMessageDialog(graphComponent, "Length of name is larger than 3 characters");
				    	continue;
				    }
				    String value2 = field2.getText().toLowerCase().replace(" ", "");						    
				    if(!(value2.equals("true")||value2.equals("false"))){
				    	JOptionPane.showMessageDialog(graphComponent, "Condition of resource is should be true or false");
				    	continue;
				    }
				    
				    value = styleType+";;"+"resourcename="+value1+";;condition="+value2;
				    cell.setValue(value1+" == "+value2);
					break;
				}else if(option == JOptionPane.CANCEL_OPTION){
					cell.removeFromParent();
					break;	
				}			
			}
			cell.setStyle(value);
			
			
		}else if(style.equals("Condition")){
			JTextField field0 = new JTextField(style);field0.setEditable(false);
			JTextField field1 = new JTextField(""); 
			JTextField field2 = new JTextField("");
			Object[] message = {mxResources.get("style"), field0, "TYPE/Name", field1, "COND", field2};
			String value = "";
			while(true){
				int option = JOptionPane.showConfirmDialog(graphComponent, message, style+" EDITOR", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION)
				{
				    String value1 = field1.getText().toLowerCase().replace(" ", "");
				    String[] inputs = value1.split("/");
				    if(inputs.length <= 1){
				    	JOptionPane.showMessageDialog(graphComponent, "Please split CONDITION DATA between Type and Name by \"/\"\n(if void, input \"void\\void\")");
				    	continue;
				    }
				    String value2 = field2.getText().toLowerCase().replace(" ", "");						    
				    if(!(value2.contains(">")||value2.contains("<")||value2.contains("==")||value2.contains(">=")||value2.contains("<="))){
				    	JOptionPane.showMessageDialog(graphComponent, "Condition should be written by inequality or equality sign");
				    	continue;
				    }
				    
				    value = styleType+";;"+"condname="+value1+";;condcondition="+value2;
				    cell.setValue(value1.split("/")[1]+" "+value2);
					break;
				}else if(option == JOptionPane.CANCEL_OPTION){
					cell.removeFromParent();
					break;	
				}
			}
			cell.setStyle(value);
		}
	}
	
	public void inputTypeModifyWindow(mxGraphComponent graphComponent, mxGraph graph, mxCell cell)	
	{
		String initial = graph.getModel().getStyle(graph.getSelectionCell());				
		String[] types =  ((String) cell.getStyle()).split(";;");
		String styleType = types[0];				
		String style =" ";
		String taskType = " ";					
		String inType = " ";
		String outType = " ";
		String parType = " ";
		String parCond = " ";	
		String condType = " ";
		String cond = "";
		System.out.println(styleType);
		switch(styleType){							
		case "ellipse":	// HardGoal
			style = "HardGoal";
			break;
		case "ellipse;shape=cloud;fillColor=skyblue":	// SoftGoal
			style = "SoftGoal";
			break;
		case "shape=hexagon;fillColor=yellow": //Task
			style = "Task";
			if(types.length > 1){
				taskType = types[1].replace(" ", "").replace("tasktype=", "");
				inType = types[2].replace(" ", "").replace("inputinfo=", "");
				outType = types[3].replace(" ", "").replace("outputinfo=", "");;
			}
			break;
		case "rectangle":	// Resource
			style = "Resource";				
			if(types.length > 1){
				parType = types[1].replace(" ", "").replace("resourcename=", "");
				parCond = types[2].replace(" ", "").replace("condition=", "");
			}
			break;
		case "rectangle;shape=doubleRectangle":	// Condition
			style = "Condition";
			if(types.length > 1){
				condType = types[1].replace(" ", "").replace("condname=", "");
				cond = types[2].replace(" ", "").replace("condcondition=", "");
			}
			break;
		case "swimlane":	// Condition
			style = "Domain";
			break;				
		}
		JTextField styleField = new JTextField(style);
		if(style.equals("HardGoal") || style.equals("SoftGoal") || style.equals("Domain")){
			JOptionPane.showMessageDialog(graphComponent, style+"\n"+styleType, mxResources.get("style"),
					JOptionPane.PLAIN_MESSAGE, null);
			
		}else if(style.equals("Task")){
			JTextField field0 = new JTextField(style);field0.setEditable(false);
			JTextField field1 = new JTextField(taskType); 
			JTextField field2 = new JTextField(inType);
			JTextField field3 = new JTextField(outType);
			Object[] message = {mxResources.get("style"), field0, "TYPE (Probe,Gauge,Act)", field1, "INPUT (Type/Name)", field2, "OUTPUT (Type/Name)",field3};
			String value = "";
			String taskName = "";
			while(true){
				int option = JOptionPane.showConfirmDialog(graphComponent, message, style+" EDITOR", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION)
				{
				    String value1 = field1.getText().toLowerCase().replace(" ", "");
				    if(!(value1.equals("probe") || value1.equals("gauge") || value1.equals("act"))){
				    	JOptionPane.showMessageDialog(graphComponent, "Select Probe, Gauge or Act");
				    	continue;
				    }
				    String value2 = field2.getText().toLowerCase().replace(" ", "");
				    String[] inputs = value2.split("/");
				    if(inputs.length <= 1){
				    	JOptionPane.showMessageDialog(graphComponent, "Please split INPUT between Type and Name by \"/\"\n(if void, input \"void\\void\")");
				    	continue;
				    }
				    String value3 = field3.getText().toLowerCase().replace(" ", "");;
				    inputs = value3.split("/");
				    if(inputs.length <= 1){
				    	JOptionPane.showMessageDialog(graphComponent, "Please split OUTPUT between Type and Name by \"/\"\n(if void, input \"void\\void\")");
				    	continue;
				    }
				    String outputName = value3.split("/")[1];
				    if(!value1.equals("act")){										    	
				    	taskName = "get"+ outputName.replace(String.valueOf(outputName.charAt(0)), String.valueOf(outputName.charAt(0)).toUpperCase());
				    }
				    value = styleType+";;"+"tasktype="+value1+";;inputinfo="+value2+";;outputinfo="+value3;
					break;
				}else if(option == JOptionPane.CANCEL_OPTION){
					value = initial;
					break;					
				}
			}
			cell.setValue(taskName);
			graph.setCellStyle(value);
		}else if(style.equals("Resource")){
			JTextField field0 = new JTextField(style);field0.setEditable(false);
			JTextField field1 = new JTextField(parType); 
			JTextField field2 = new JTextField(parCond);
			Object[] message = {mxResources.get("style"), field0, "NAME", field1, "COND", field2};
			
			String value = "";
			while(true){
				int option = JOptionPane.showConfirmDialog(graphComponent, message, style+" EDITOR", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION)
				{
				    String value1 = field1.getText().toLowerCase().replace(" ", "");
				    if(value1.length() <= 2){
				    	JOptionPane.showMessageDialog(graphComponent, "Length of name is larger than 3 characters");
				    	continue;
				    }
				    String value2 = field2.getText().toLowerCase().replace(" ", "");						    
				    if(!(value2.equals("true")||value2.equals("false"))){
				    	JOptionPane.showMessageDialog(graphComponent, "Condition of resource is should be true or false");
				    	continue;
				    }
				    cell.setValue(value1+" == "+value2);
				    value = styleType+";;"+"resourcename="+value1+";;condition="+value2;
					break;
				}else if(option == JOptionPane.CANCEL_OPTION){
					value = initial;
					break;								
				}
			}
			
			graph.setCellStyle(value);
			
			
		}else if(style.equals("Condition")){
			JTextField field0 = new JTextField(style);field0.setEditable(false);
			JTextField field1 = new JTextField(condType); 
			JTextField field2 = new JTextField(cond);
			Object[] message = {mxResources.get("style"), field0, "TYPE/Name", field1, "COND", field2};
			String value = "";
			while(true){
				int option = JOptionPane.showConfirmDialog(graphComponent, message, style+" EDITOR", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION)
				{
				    String value1 = field1.getText().toLowerCase().replace(" ", "");
				    String[] inputs = value1.split("/");
				    if(inputs.length <= 1){
				    	JOptionPane.showMessageDialog(graphComponent, "Please split CONDITION DATA between Type and Name by \"/\"\n(if void, input \"void\\void\")");
				    	continue;
				    }
				    String value2 = field2.getText().toLowerCase().replace(" ", "");						    
				    if(!(value2.contains(">")||value2.contains("<")||value2.contains("==")||value2.contains(">=")||value2.contains("<="))){
				    	JOptionPane.showMessageDialog(graphComponent, "Condition should be written by inequality or equality sign");
				    	continue;
				    }
				    
				    value = styleType+";;"+"condname="+value1+";;condcondition="+value2;
				    cell.setValue(value1.split("/")[1]+" "+value2);
					break;
				}else if(option == JOptionPane.CANCEL_OPTION){
					value = initial;
					break;								
				}
			}
			
			graph.setCellStyle(value);
		}
	}
	
}
