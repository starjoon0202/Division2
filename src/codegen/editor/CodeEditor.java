package codegen.editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import editor.GraphEditor;

/**
 * Standalone version of the demo.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CodeEditor extends JDialog {
	
	
	public CodeEditor() {
	}
	
	public CodeEditor(String[] filePaths2, String string, GraphEditor ge) {
		setDefaultCloseOperation(CodeEditor.DISPOSE_ON_CLOSE);
		createTabbedPane(ge);
			
		setTitle("Code Editor: "+string);
		setBounds(770,0,600, 770);
		setResizable(false);
		
		setVisible(true);
	}

	static String[] fileNames;
	static String[] filePaths;
	
	static CodeEditor codeEditor = null;
	
	static GraphEditor ged = null;
	public static void openWindow(String[] filePath, GraphEditor ge) {
		ged = ge;
		filePaths = new String[filePath.length-1];
		fileNames = new String[filePath.length-1];
		for(int i = 1 ; i < filePath.length; i++){
			String path = filePath[i];
			String fileName = path.split("\\\\")[path.split("\\\\").length - 1];
			fileNames[i-1] = fileName;
			filePaths[i-1] = path;
		}
		if(codeEditor == null)		
			codeEditor = new CodeEditor(filePaths, filePath[0], ged);
		else
			refreshWindow(filePaths, filePath[0], ged);
	} 
	
	private static void refreshWindow(String[] filePaths2, String string, GraphEditor ge) {
		CodeEditor ce = codeEditor;
		ce.setVisible(false);
		codeEditor = new CodeEditor(filePaths, string, ge);
		
		
	}

	public static void closingWindow(){		
		CodeEditor ce = codeEditor;
		ce.setVisible(false);
	}
	
	public static void main(String[] args) {
		String[] filePath = {
				"C:\\Users\\rose\\git\\iCPS\\test0307\\Monitor.java",
				"C:\\Users\\rose\\git\\iCPS\\test0307\\Analyzer.java",
				"C:\\Users\\rose\\git\\iCPS\\test0307\\Planner.java",
				"C:\\Users\\rose\\git\\iCPS\\test0307\\Executor.java",
				"C:\\Users\\rose\\git\\iCPS\\test0307\\Knowledgebase.java"};

		filePaths = new String[filePath.length];
		fileNames = new String[filePath.length];
		for(int i = 0 ; i < filePath.length; i++){
			String path = filePath[i];
			String fileName = path.split("\\\\")[path.split("\\\\").length - 1];
			fileNames[i] = fileName;
			filePaths[i] = path;
		}
		
//		new CodeEditor(filePaths);

	} 
	static int count = 0;
	public static DemoRootPane[] drps;
	
	void createTabbedPane(GraphEditor ge) {
		
		JTabbedPane tPane = new JTabbedPane();
		add(tPane);
		textArea = new RSyntaxTextArea[fileNames.length];;
		drps = new DemoRootPane[fileNames.length];
		for(int i = 0 ; i<fileNames.length; i++){
			String fileName = fileNames[i];
			JPanel mainPanel = new JPanel();
			DemoRootPane drp = new DemoRootPane(filePaths[i],ge);
			drp.setPreferredSize(new Dimension(600,675));
			mainPanel.add(drp);
			drps[i]=drp;
			JButton jb = new JButton("SAVE");
			jb.setName(String.valueOf(i));
			jb.addActionListener(new SaveAction(filePaths[i],fileNames[i],drp.textArea.getText()));
			textArea[i] = drp.textArea;
			mainPanel.add(jb);
			tPane.addTab(fileName, mainPanel);
		}
	}
	public String getValues(){
		if(drps == null)
			return "";
		return drps[0].getFuncName();
	}
	
	
	static RSyntaxTextArea[] textArea;	
	
	public static class SaveAction extends AbstractAction {
		
		String path;
		String name;
		String content;
		
		public SaveAction(String path, String name, String content) {
			this.path = path;
			this.name = name;
			this.content = content;
		}

		public void actionPerformed(ActionEvent e) {
			
			int fileNum = Integer.parseInt(((JButton)e.getSource()).getName());
			try {
				String content = textArea[fileNum].getText();
				BufferedWriter bw = new BufferedWriter(new FileWriter(path));				
				bw.write(content);
				bw.close();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			
		}
	}


}