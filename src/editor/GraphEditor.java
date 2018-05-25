/**
 * Copyright (c) 2006-2012, JGraph Ltd */
package editor;

import java.awt.Color;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.w3c.dom.Document;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

import codegen.editor.CodeEditor;
import editor.common.BasicGraphEditor;
import editor.common.EditorMenuBar;
import editor.common.EditorPalette;

public class GraphEditor extends BasicGraphEditor
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4601740824088314699L;

	/**
	 * Holds the shared number formatter.
	 * 
	 * @see NumberFormat#getInstance()
	 */
	public static final NumberFormat numberFormat = NumberFormat.getInstance();

	/**
	 * Holds the URL for the icon to be used as a handle for creating new
	 * connections. This is currently unused.
	 */
	public static URL url = null;


	public GraphEditor( CodeEditor ce)
	{
		this("Goal Model Editor", new CustomGraphComponent(new CustomGraph()),ce);
	}

	/**
	 * 
	 */
	public GraphEditor(String appTitle, mxGraphComponent component,  CodeEditor ce)
	{
		super(appTitle, component, ce);
		final mxGraph graph = graphComponent.getGraph();		

		// Creates the shapes palette
		EditorPalette shapesPalette = insertPalette(mxResources.get("shapes"));
		// Sets the edge template to be used for creating new edges if an edge
		// is clicked in the shape palette
		shapesPalette.addListener(mxEvent.SELECT, new mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				Object tmp = evt.getProperty("transferable");

				if (tmp instanceof mxGraphTransferable)
				{
					mxGraphTransferable t = (mxGraphTransferable) tmp;
					Object cell = t.getCells()[0];

					if (graph.getModel().isEdge(cell))
					{
						((CustomGraph) graph).setEdgeTemplate(cell);
					}
				}
			}		
		});
		

		// Adds some template cells for dropping into the graph
		shapesPalette
				.addTemplate(
						"Domain",
						new ImageIcon("./images/swimlane.png"),
						"swimlane", 500, 400, "Domain");
		shapesPalette
		.addTemplate(
				"Goal",
				new ImageIcon(
						
								"./images/ellipse.png"),
				"ellipse", 100, 50, "");
		
		shapesPalette
		.addTemplate(
				"Soft goal",
				new ImageIcon(
						
								"./images/cloud.png"),
				"ellipse;shape=cloud;fillColor=skyblue", 100, 50, "");
		shapesPalette
				.addTemplate(
						"Resource",
						new ImageIcon(
								
										"./images/rectangle.png"),
						"rectangle", 80, 30, "");
		shapesPalette
			.addTemplate(
				"Condition",
				new ImageIcon(
						
								"./images/doublerectangle.png"),
				"rectangle;shape=doubleRectangle", 80, 30, "");
		
		
		
		shapesPalette
			.addTemplate(
				"Task",
				new ImageIcon(
						
								"./images/hexagon.png"),
				"shape=hexagon;fillColor=yellow", 100, 50, "");
		

	}

	/**
	 * 
	 */
	public static class CustomGraphComponent extends mxGraphComponent
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -6833603133512882012L;

		/**
		 * 
		 * @param graph
		 */
		public CustomGraphComponent(mxGraph graph)
		{
			super(graph);
			// Sets switches typically used in an editor
			
			setPageVisible(true);			
			setPreferPageSize(true);
			getConnectionHandler().setCreateTarget(true);
			

			// Loads the defalt stylesheet from an external file
			mxCodec codec = new mxCodec();
			Document doc = mxUtils.loadDocument("./resources/basic-style.xml");
			codec.decode(doc.getDocumentElement(), graph.getStylesheet());

			// Sets the background to white
			getViewport().setOpaque(true);
			getViewport().setBackground(Color.WHITE);
			
			mxConnectionHandler handler = getConnectionHandler();
			handler.setCreateTarget(false);
			
			
		}

		
	}

	/**
	 * A graph that creates new edges from a given template edge.
	 */
	public static class CustomGraph extends mxGraph
	{
		/**
		 * Holds the edge to be used as a template for inserting new edges.
		 */
		protected Object edgeTemplate;

		/**
		 * Custom graph that defines the alternate edge style to be used when
		 * the middle control point of edges is double clicked (flipped).
		 */
		public CustomGraph()
		{
			setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
//			setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.Orthogonal");

		}

		/**
		 * Sets the edge template to be used to inserting edges.
		 */
		public void setEdgeTemplate(Object template)
		{
			edgeTemplate = template;
		}	

		/**
		 * Overrides the method to use the currently selected edge template for
		 * new edges.
		 * 
		 * @param graph
		 * @param parent
		 * @param id
		 * @param value
		 * @param source
		 * @param target
		 * @param style
		 * @return
		 */
		public Object createEdge(Object parent, String id, Object value,
				Object source, Object target, String style)
		{
			if (edgeTemplate != null)
			{
				mxCell edge = (mxCell) cloneCells(new Object[] { edgeTemplate })[0];
				edge.setId(id);

				return edge;
			}
			
			return super.createEdge(parent, id, value, source, target, style);
		}

	}

	/**
	 * 
	 * @param args
	 */
	static CodeEditor ce;
	static GraphEditor editor;
	static JFrame frm;
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
		mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";
		
		ce = new CodeEditor();
		editor = new GraphEditor(ce);
		frm = editor.createFrame(new EditorMenuBar(editor, ce, editor));
		frm.setVisible(true);
	}
	
	
	public static void alarmText(String data){
		if (editor != null) {
			System.out.println(data);
			mxGraphComponent graphComponent = editor.getGraphComponent();
			mxGraph graph = graphComponent.getGraph();
			Object[] vertices = graph.getChildVertices(graph.getDefaultParent());

			for(int i = 0 ; i<vertices.length; i++){
				mxCell cell = (mxCell)vertices[i];
				String cellName = cell.getValue().toString().toLowerCase();
				if(cellName.contains("\n"))
					continue;
				if(cellName.contains("double") || cellName.contains("bool") || cellName.contains("int"))
					cellName = cellName.split(" ")[0];		
				if(cellName.equals(data)){
					 graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#FF0000", new Object[]{cell});
				}else if(cell.getStyle().contains("hexagon")){
					graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#ffff00", new Object[]{cell});
				}else if(cell.getStyle().contains("ellipse")){
					graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#c0c0c0", new Object[]{cell});
				}else{
					graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#ffffff", new Object[]{cell});
				}
			}	
			graphComponent.repaint();
		}

	}
}
